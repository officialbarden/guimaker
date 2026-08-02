# Security audit of the five reported issues

Audited build: **2.5.1** (Minecraft 1.21.8)

## 1. `{key:<input_key>}` JSON injection

**Verdict: confirmed and fixed.**

The old implementation used only `StringArgumentType.escapeIfRequired` and then
performed plain string replacement. Brigadier quoting is not the same operation
as escaping content already inside a JSON string.

### Current implementation

File: `src/main/java/dev/guimaker/util/PlaceholderResolver.java`

Relevant ranges: **35-68** and **151-213**.

```java
public static String resolveCommand(String input, ServerPlayerEntity player,
                                    Map<String, String> keyValues) {
    String result = input;
    for (Map.Entry<String, String> entry : values(player, false).entrySet()) {
        result = replaceCommandPlaceholder(result, entry.getKey(), entry.getValue());
    }
    return resolveKeyValues(result, keyValues);
}

public static String resolveKeyValues(String input, Map<String, String> keyValues) {
    Matcher matcher = KEY_PLACEHOLDER.matcher(input);
    // ...
    output.append(encodeForContext(value, contextAt(input, matcher.start())));
}

private static String encodeForContext(String value, KeyContext context) {
    String jsonLiteral = GSON.toJson(value == null ? "" : value);
    return switch (context) {
        case JSON_STRING -> jsonLiteral.substring(1, jsonLiteral.length() - 1);
        case STRUCTURED_VALUE -> jsonLiteral;
        case COMMAND_ARGUMENT -> StringArgumentType.escapeIfRequired(value == null ? "" : value);
    };
}
```

`JSON_STRING` uses Gson escaping for quotes, backslashes and control characters,
but removes the outer Gson quotes because the placeholder is already inside a
JSON string. `STRUCTURED_VALUE` uses the complete Gson string literal. Only an
ordinary command argument uses Brigadier quoting.

### Security diff

```diff
- String safeValue = StringArgumentType.escapeIfRequired(entry.getValue());
- result = result.replace("{key:" + entry.getKey() + "}", safeValue);
+ output.append(encodeForContext(value, contextAt(input, matcher.start())));
+
+ String jsonLiteral = GSON.toJson(value);
+ case JSON_STRING -> jsonLiteral.substring(1, jsonLiteral.length() - 1);
+ case STRUCTURED_VALUE -> jsonLiteral;
+ case COMMAND_ARGUMENT -> StringArgumentType.escapeIfRequired(value);
```

### Regression test

File: `src/test/java/dev/guimaker/util/PlaceholderResolverTest.java`, lines
**12-45**.

The test submits this value:

```text
"}, {"text":"pwned\line\nnext
```

It parses the resulting `tellraw` payload with Gson, verifies that the payload
still has exactly one JSON member, and verifies that the complete malicious
string remains data inside `text`. Three JSON/structured/Brigadier context tests
pass.

## 2. `commands.json` permission validation and dangerous commands

**Verdict: permission range validation already existed; security auditing and a
high-risk execution gate were missing. Both were added without deleting any
preset.**

### Existing permission validation

File: `src/main/java/dev/guimaker/gate/CommandPresetRegistry.java`, lines
**175-193** and **283-287**.

```java
int permissionLevel = optionalInt(object, "server_permission_level", 2);
validatePermissionLevel(permissionLevel, "server_permission_level");

private static void validatePermissionLevel(int value, String field) {
    if (value < 0 || value > 4) {
        throw new IllegalArgumentException(field + " must be between 0 and 4");
    }
}
```

Therefore, the claim that no numeric validation existed was not correct.
Values outside 0-4 were already rejected.

### Added WARN audit and non-destructive gate

File: `CommandPresetRegistry.java`, lines **40-43**, **84-104**, and **138-165**.

```java
private static final Set<String> SENSITIVE_COMMANDS = Set.of(
        "op", "deop", "stop", "ban", "ban-ip", "pardon", "pardon-ip",
        "whitelist", "kick", "reload", "save-off", "save-all");

CommandPreset preset = decode(element.getAsJsonObject());
auditSecurity(preset);
PRESETS.putIfAbsent(preset.id(), preset); // preset is retained

if (preset.serverPermissionLevel() >= 3) {
    LOGGER.warn("SECURITY: preset '{}' requests SERVER permission level {} ...");
}
// Sensitive root commands, including `execute ... run minecraft:op`, are logged.
```

High-risk presets remain loaded. Execution requires explicit owner approval:

```json
"security_acknowledged": true
```

File: `src/main/java/dev/guimaker/gate/CommandExecutionGate.java`, lines
**14-45**.

```java
if (CommandPresetRegistry.requiresSecurityAcknowledgement(preset)
        && !preset.securityAcknowledged()) {
    return Decision.SECURITY_ACK_REQUIRED;
}
```

File: `src/main/java/dev/guimaker/gui/GatedCommandExecutor.java`, lines
**28-53** displays the rejection to the player. This is a gate, not deletion or
silent filtering.

### Security diff

```diff
+ auditSecurity(preset);
  PRESETS.putIfAbsent(preset.id(), preset);
+
+ boolean securityAcknowledged = optionalBoolean(
+     object, "security_acknowledged", false);
+
+ if (requiresSecurityAcknowledgement(preset)
+         && !preset.securityAcknowledged()) {
+     return Decision.SECURITY_ACK_REQUIRED;
+ }
```

Test: `CommandPresetSecurityGateTest.java`, lines **7-40**, verifies level 3,
nested namespaced `minecraft:op`, normal level 2, and acknowledged level 4 cases.

## 3. Item-holder lock

**Verdict: a real atomic Java lock already existed. It did not rely on packet
ordering. The lock was extracted into directly testable methods and a two-thread
regression test was added.**

File: `src/main/java/dev/guimaker/widget/WidgetStateRepository.java`, lines
**20-70**.

```java
private static final ConcurrentHashMap<String, UUID> HOLDER_LOCKS =
        new ConcurrentHashMap<>();

static boolean tryAcquireHolderLock(String lockKey, UUID owner) {
    UUID existing = HOLDER_LOCKS.putIfAbsent(lockKey, owner);
    return existing == null || existing.equals(owner);
}

static void releaseHolderLock(String lockKey, UUID owner) {
    HOLDER_LOCKS.remove(lockKey, owner);
}
```

`ConcurrentHashMap.putIfAbsent` is atomic. If two distinct players attempt the
same world-scope holder in the same tick/thread race, exactly one inserts its
UUID; the other receives `false`.

### Test

File: `src/test/java/dev/guimaker/widget/WidgetStateRepositoryConcurrencyTest.java`,
lines **15-58**.

Two executor threads are released by the same latch against one lock key. The
test asserts:

1. exactly one result is `true`;
2. the loser remains blocked;
3. the loser can acquire only after the winner releases.

### Diff

The behavior remains `putIfAbsent`; the diff only extracts the atomic operation
for direct testing:

```diff
- UUID existing = HOLDER_LOCKS.putIfAbsent(lockKey, owner);
- return existing == null || existing.equals(owner);
+ return tryAcquireHolderLock(holderLockKey(widget, player), player.getUuid());
+
+ static boolean tryAcquireHolderLock(String lockKey, UUID owner) {
+     UUID existing = HOLDER_LOCKS.putIfAbsent(lockKey, owner);
+     return existing == null || existing.equals(owner);
+ }
```

## 4. World migration race

**Verdict: confirmed and fixed.**

The old implementation performed `Files.exists(target)` and then
`Files.copy(...)`, which was a check-then-act race. A concurrent creator could
create the target between those operations and make initialization fail.

### Current implementation

File: `src/main/java/dev/guimaker/config/WorldDataPaths.java`, lines **64-100**.

```java
if (copyLegacyFileIfAbsent(source, target)) {
    copied.add(fileName);
}

static boolean copyLegacyFileIfAbsent(Path source, Path target) throws IOException {
    if (!Files.isRegularFile(source)) return false;
    try {
        Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
        return true;
    } catch (FileAlreadyExistsException ignored) {
        return false;
    }
}
```

There is no target `Files.exists` check and no `REPLACE_EXISTING`. The copy uses
CREATE_NEW behavior; `FileAlreadyExistsException` is the expected losing-race
result.

### Diff

```diff
- if (!Files.exists(target) && Files.isRegularFile(source)) {
-     Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
+ if (copyLegacyFileIfAbsent(source, target)) {
      copied.add(fileName);
  }
+
+ try {
+     Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
+ } catch (FileAlreadyExistsException ignored) {
+     return false;
+ }
```

Test: `WorldDataPathsConcurrencyTest.java`, lines **15-45**, starts two copy
attempts simultaneously and verifies exactly one creates the target and that the
file contents are intact.

## 5. Left-click-air packet source

**Verdict: no additional official-client identity check exists, and none is
needed for authorization. The packet is treated only as an interaction request;
the server independently validates the sender's currently held server-side
item. No change was made.**

File: `src/main/java/dev/guimaker/item/ItemGuiInteractionHandler.java`, lines
**29-33**:

```java
ServerPlayNetworking.registerGlobalReceiver(LeftClickAirPayload.ID, (payload, context) ->
        context.server().execute(() -> openFromStack(
                context.player(), context.player().getMainHandStack(),
                ItemGuiBinding.ClickType.LEFT)));
```

The stack is obtained from `context.player()` on the server, not from packet
contents. The payload contains no GUI ID, page ID, item data, or claimed player.

Lines **66-85** then validate the server-side stack and target:

```java
Optional<ItemGuiBinding.Binding> binding = ItemGuiBinding.read(stack);
if (binding.isEmpty() || !binding.get().clickMode().accepts(clickType)) return false;
if (!GuiRepository.requireGui(binding.guiId()).hasPage(binding.pageId())) return false;
return GuiPageManager.openGui(player, binding.guiId(), binding.pageId());
```

`ItemGuiBinding.java`, lines **83-109**, reads `minecraft:custom_data` from the
held stack, requires the `guimaker.open_gui` compound, checks nonblank GUI ID,
page range 0-9999, and a valid click mode. A custom client can send the packet,
but cannot select data other than what the server already sees in its held item.

## Test result

Command executed:

```text
./gradlew clean test --no-daemon --max-workers=1
```

Result: **BUILD SUCCESSFUL**. Test suites: 9 tests, 0 failures, 0 errors.
