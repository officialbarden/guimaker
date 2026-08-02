# GUI Maker — Fabric 1.21.8

A secure, server-side GUI builder that uses vanilla chest screens. It does not
require a custom screen or resource pack. Clients do not need the mod when it is
installed on a dedicated server for normal GUI and item interactions. Install
the mod on the client as well to enable left-click-in-air item bindings. For
single-player use, install the mod and Fabric API on the client.

## Requirements

- Minecraft 1.21.8
- Java 21
- Fabric Loader 0.19.x
- Fabric API 0.136.1+1.21.8

Place the mod JAR and the Fabric API JAR in the `mods/` directory.

## Quick start

On first launch, the mod creates world-specific data inside the active save:

- `<world>/data/guimaker/guis.json`
- `<world>/data/guimaker/dialogs.json`
- `<world>/data/guimaker/commands.json`
- `<world>/data/guimaker/limits.json`
- `<world>/data/guimaker/widget-state.json`

```mcfunction
/guimaker menu
/guimaker open example
/guimaker create market 3 Market Menu
/guimaker edit market 0
```

In the visual editor:

- **Left click:** Copies the item in your main hand into the slot without consuming it.
- **Right click or shift-click:** Clears the slot and its action.
- The player's inventory cannot be modified while the GUI is open.
- Slot numbers start at **0** from left to right (0-53 on a six-row page).

Assign behavior to an item with a command:

```mcfunction
/guimaker action set market 0 10 close_gui
/guimaker action set market 0 11 play_sound ui.button.click
/guimaker action set market 0 12 give_item preset.confirm_item
/guimaker action set market 0 13 run_command welcome_message
```

Create a second page and add page navigation:

```mcfunction
/guimaker page add market 1 3 Second Page
/guimaker action set market 0 16 open_page 1
/guimaker action set market 1 10 open_page 0
```

## Commands

Available to all players:

```text
/guimaker help
/guimaker list
/guimaker open <gui> [page]
```

Management commands requiring OP level 2:

```text
/guimaker menu
/guimaker dialog list
/guimaker dialog open <id>
/guimaker dialog create <id> <notice|confirmation|multi_action|server_links|dialog_list|form|searchable_list|wizard> <title>
/guimaker dialog edit title|body|type ...
/guimaker dialog delete <id>
/guimaker create <gui> <1-6 rows> <title>
/guimaker delete <gui>
/guimaker edit [gui] [page]
/guimaker page add <gui> <page> <1-6 rows> <title>
/guimaker page rename <gui> <page> <title>
/guimaker page remove <gui> <page>
/guimaker item set <gui> <page> <slot>
/guimaker item clear <gui> <page> <slot>
/guimaker item bind <gui> <page> [right|left|both]
/guimaker item unbind
/guimaker item binding
/guimaker action set <gui> <page> <slot> <action> [parameter]
/guimaker action clear <gui> <page> <slot>
/guimaker widget set <gui> <page> <slot> <type> <scope> <key> [config]
/guimaker widget clear <gui> <page> <slot>
/guimaker widget reset <gui> <page> <slot>
/guimaker widget list <gui> <page>
/guimaker commands
/guimaker limits
/guimaker reload
```

`item set` stores every data component of the item in the player's main hand,
including its custom name, lore and model data. The item is only a GUI display
item and cannot be removed from the GUI.

## World-specific storage and migration

All GUI Maker data is scoped to the active Minecraft save. Different
single-player saves or dedicated-server worlds no longer share GUIs, dialogs,
command presets, or capacity limits.

```text
<world>/data/guimaker/guis.json
<world>/data/guimaker/dialogs.json
<world>/data/guimaker/commands.json
<world>/data/guimaker/limits.json
<world>/data/guimaker/widget-state.json
```

Show the active directory with:

```mcfunction
/guimaker world-data
```

On the first start of a world, if a target file does not exist, the mod copies
its legacy counterpart from `config/guimaker/`. Legacy files are never moved or
deleted, so they remain a backup and a template for other worlds. The world
receives `migration.json` describing the copied files and migration time.
Existing world files always win and are never overwritten by migration.

`/guimaker reload` reloads data only for the currently active save.

## Managed dialog system

`/guimaker dialog ...` provides persistent create, open, edit and delete
operations backed by `<world>/data/guimaker/dialogs.json`.

```mcfunction
/guimaker dialog list
/guimaker dialog create setup form Setup Form
/guimaker dialog open setup
/guimaker dialog edit title setup New Setup Title
/guimaker dialog edit body setup Enter your settings
/guimaker dialog edit type setup wizard
/guimaker dialog delete setup
```

Inputs and gated actions can be managed from commands:

```mcfunction
/guimaker dialog input add setup username validated_text Username
/guimaker dialog input add setup destination searchable_dropdown Destination
/guimaker dialog input option setup destination spawn Server Spawn
/guimaker dialog input add setup reward item_selector Reward Item
/guimaker dialog input remove setup username
/guimaker dialog action add setup show_help Submit
/guimaker dialog action list setup
/guimaker dialog action remove setup 0
/guimaker dialog wizard step add setup Confirmation
/guimaker dialog wizard step remove setup 1
/guimaker dialog json setup
```

All creation and editing commands require OP level 2. Opening a known dialog is
available to players, while every command-preset action still passes through the
existing permission, cooldown and command allowlist gates.

### Vanilla-compatible enhanced dialog layer

The repository supports all five official Mojang dialog types:

- `notice`
- `confirmation`
- `multi_action`
- `server_links`
- `dialog_list`

It also provides three enhanced server-side templates that compile into those
vanilla types before a packet is sent:

- `form` — compiles to `minecraft:multi_action` with form controls.
- `searchable_list` — uses a native text input and a server-side filtered result dialog.
- `wizard` — compiles each step into nested native `minecraft:multi_action` dialogs.

All four official input types are supported (`text`, `boolean`, `number_range`,
`single_option`). Three enhanced input templates are also available:

- `validated_text` — compiled to native text and validated by managed operations.
- `searchable_dropdown` — compiled to native `single_option`.
- `item_selector` — compiled to native text with item-ID semantics.

No custom dialog codec, registry-sync extension, renderer Mixin or custom client
screen is used. Every resulting screen is decoded and rendered by Mojang's
standard 1.21.6+ dialog implementation, so the rewritten system remains usable
with vanilla clients.

## Placeholders

Managed dialog JSON, dialog titles/bodies/buttons/options, GUI page titles and
gated command presets support server-resolved placeholders.

```text
{player}              {player_name}       {display_name}
{uuid}                {world}             {dimension}
{x} {y} {z}           {block_x} {block_y} {block_z}
{yaw} {pitch}         {health}            {max_health}
{food}                {level}             {gamemode}
{time}                {server_players}    {server_max_players}
{server_motd}
{key:<input_key>}
```

List them in game with:

```mcfunction
/guimaker dialog placeholders
```

Example:

```mcfunction
/guimaker dialog create welcome notice Welcome {player}
/guimaker dialog edit body welcome You are in {world} at {block_x} {block_y} {block_z}
/guimaker dialog open welcome
```

Replacement happens separately for each player immediately before the native
dialog is compiled and sent. Unknown placeholders are left unchanged. Command
preset replacements use command-safe player/display names.

`{key:<input_key>}` reads a value submitted by a dialog input. Define it in a
gated command preset and attach that preset to a dialog action:

```json
{
  "id": "echo_name",
  "command": "tellraw {player} {\"text\":\"Hello {key:name}\"}",
  "run_as": "SERVER",
  "server_permission_level": 2,
  "required_player_permission_level": 0,
  "cooldown_ms": 250,
  "silent": true
}
```

```mcfunction
/guimaker dialog input add welcome name text Name
/guimaker dialog action add welcome echo_name Submit
```

The action is automatically stored as `minecraft:dynamic/run_command`. Missing
input keys are rejected when the action is added, and submitted values are
parsed and command-escaped before being substituted into the gated preset.

## Dialog-based operator menu

Run `/guimaker menu` as an OP level 2+ player to open the Minecraft 1.21.8
vanilla dialog workflow. It follows the original datapack's menu logic while
routing every operation through the Fabric mod's validated commands.

The main dialog contains:

- **Create GUI** — create a new profile or append a page with input forms.
- **Edit GUI** — choose a GUI and page, rename it, open the visual item editor,
  or configure a button's allowlisted action and parameter.
- **Bind Held Item** — select a GUI, page and right/left/both click mode.
- **Delete GUI** — delete a profile or page through a confirmation dialog.
- **Open GUI** — choose a GUI and page to preview.

GUI, page and button lists are paginated in groups of ten, so the dialog remains
usable with high limits from `limits.json`. All menu routes require OP level 2,
and all submitted values still pass through the same ID, row, slot, action,
permission and allowlist gates as the text commands.

The dialogs are built server-side with Minecraft's native dialog API. No custom
client screen or resource pack is required.

## Stateful chest-GUI widgets

Normal chest GUIs support four stateful widget types:

- **Toggle** — switches between ON and OFF.
- **Cycle** — advances through a configured list of string options.
- **Counter** — left-click increments and right-click decrements within bounds.
- **Item Holder** — stores a real item stack that can be inserted or removed.

Every widget chooses a state scope:

- `player` — each player has an independent value.
- `world` — all players in the active save share the value.

Widget state is persisted in `<world>/data/guimaker/widget-state.json` and is
flushed with throttled atomic writes. It survives restarts. Configure widgets
from `/guimaker menu` → Edit GUI → page → **Configure Widgets**, or use commands:

```mcfunction
/guimaker widget set market 0 18 toggle player newsletter false
/guimaker widget set market 0 19 cycle player theme red,green,blue
/guimaker widget set market 0 20 counter world visitor_count 0,100,1,0
/guimaker widget set market 0 21 item_holder player personal_storage

/guimaker widget list market 0
/guimaker widget reset market 0 18
/guimaker widget clear market 0 18
```

Config formats:

```text
toggle:      true or false
cycle:       option_a,option_b,option_c
counter:     minimum,maximum,step,default
item_holder: config is ignored
```

A widget slot may also keep an allowlisted button action; toggle/cycle/counter
state is updated first, then the optional action runs through the normal gates.
Item holders never execute slot actions. World-scoped item holders use an
exclusive per-slot state lock so two open screens cannot duplicate the same
stored stack. Player inventory interaction is enabled only while an item-holder
screen owns a lock, and shift-click remains disabled.

## Item click GUI bindings

An operator can bind the item in their main hand to a GUI page:

```mcfunction
/guimaker item bind market 0 right
/guimaker item bind market 0 left
/guimaker item bind market 0 both
```

If the click mode is omitted, `both` is used. Inspect or remove the binding with:

```mcfunction
/guimaker item binding
/guimaker item unbind
```

The dialog menu also exposes this workflow through **Bind Held Item**, followed
by GUI, page and click-mode selection.

The binding is stored in the vanilla `minecraft:custom_data` item component:

```text
guimaker.open_gui = {gui: "market", page: 0, click: "both"}
```

No custom registered item component is used, so the item remains safe to sync
to unmodified clients. Existing custom data is preserved, and `unbind` removes
only the GUI Maker binding.

Interaction behavior:

- Right-clicking air, blocks or entities opens the bound page.
- Left-clicking blocks or entities opens the page and cancels the attack/break action.
- Left-clicking empty air is supported when the client also has this mod installed;
  the server validates the held item's component before opening anything.
- Bindings are validated against the current GUI/page registry at click time.
- A 250 ms per-player gate prevents duplicate callback openings.

## Allowlisted actions

| Action | Parameter | Description |
|---|---|---|
| `no_op` | none | Decorative slot with no behavior |
| `close_gui` | none | Closes the menu |
| `open_page` | page number | Opens another page in the same GUI |
| `play_sound` | allowlisted sound ID | Plays a sound from the fixed allowlist |
| `give_item` | allowlisted preset | Gives an item from the fixed preset list |
| `run_command` | command preset ID | Runs a server-owned command preset through permission and cooldown gates |

Tab completion lists valid actions and parameters. Raw command text supplied by
a player or GUI definition is never executed.

## Gated command presets

Command text exists only in the server-owned
`<world>/data/guimaker/commands.json` allowlist. A GUI button stores a preset ID such
as `welcome_message`, never a command string. Edit this file from the server
filesystem and run `/guimaker reload` afterward.

Example:

```json
{
  "format": 1,
  "commands": [
    {
      "id": "show_help",
      "command": "help",
      "run_as": "PLAYER",
      "server_permission_level": 0,
      "required_player_permission_level": 0,
      "cooldown_ms": 1000,
      "silent": false,
      "security_acknowledged": false
    },
    {
      "id": "welcome_message",
      "command": "tellraw {player} {\"text\":\"Welcome!\",\"color\":\"green\"}",
      "run_as": "SERVER",
      "server_permission_level": 2,
      "required_player_permission_level": 0,
      "cooldown_ms": 1000,
      "silent": true,
      "security_acknowledged": false
    }
  ]
}
```

Preset fields:

- `id`: Allowlisted ID used by `run_command`; 1-64 characters.
- `command`: Server-owned command without a required leading `/`; maximum 512 characters.
- `run_as`: `PLAYER` or `SERVER`.
- `server_permission_level`: Permission level 0-4 for `SERVER` mode only.
- `required_player_permission_level`: Minimum permission level 0-4 required to click the button.
- `cooldown_ms`: Per-player, per-preset cooldown from 250 to 3,600,000 ms.
- `silent`: Suppresses normal command feedback when enabled.
- `security_acknowledged`: Explicit server-owner approval required for SERVER
  presets using permission level 3-4 or sensitive commands such as `op`, `ban`,
  `whitelist`, `reload`, or `stop`. Such presets remain loaded but are gated
  from execution while this value is `false`; a SECURITY warning is logged.

Supported placeholders:

- `{player}`: Clicking player's command-safe name.
- `{uuid}`: Clicking player's UUID.

Security behavior:

1. `ActionAllowlistGate` accepts only registered preset IDs.
2. `CommandExecutionGate` checks player permission and preset cooldown.
3. `PLAYER` mode never elevates the player's existing permission level.
4. `SERVER` mode uses only the permission level explicitly configured by the server owner.
5. Commands containing control characters or exceeding 512 characters are rejected.
6. Each successful execution is written to the server log with preset ID, player and execution mode.

## Configurable capacity limits

GUI and command-preset capacities are controlled by
`<world>/data/guimaker/limits.json`:

```json
{
  "format": 1,
  "max_guis": 512,
  "max_command_presets": 1024
}
```

The default limits are 512 GUIs and 1,024 command presets. Both values may be
set from 1 to 100,000. Run `/guimaker reload` after editing the file. Use
`/guimaker limits` to display the currently loaded values.

If a configured limit is lower than the number of entries in an existing file,
entries above that limit are skipped during loading and a warning is written to
the server log.

## Persistence and limits

- GUI file: `<world>/data/guimaker/guis.json`
- Managed dialogs: `<world>/data/guimaker/dialogs.json`
- Command allowlist: `<world>/data/guimaker/commands.json`
- Capacity settings: `<world>/data/guimaker/limits.json`
- Widget state: `<world>/data/guimaker/widget-state.json`
- Every GUI change is saved immediately using an atomic file replacement.
- Invalid files are backed up as `*.broken-<date>.json`.
- GUI and command-preset limits are read from `limits.json`.
- Each GUI may contain up to 64 pages with 1-6 rows per page.
- The last GUI and the last page in a GUI cannot be removed accidentally.
- General button clicks are rate-limited to one action per player every 150 ms.
- Command actions also have their own per-preset cooldown.

## Why the earlier 2.x versions were not published

Version `2.3.0` is the first public and supported release in the 2.x line.
Versions `2.0.0`, `2.1.0`, and `2.2.0` were internal development milestones,
not missing public releases.

- **2.0.0 — experimental custom dialog registries:** This build attempted to
  add custom dialog/input codecs and client render dispatch through Mixin. It
  produced registry-sync, client compatibility, and dialog rendering problems,
  so it was rejected before publication.
- **2.1.0 — native-dialog rewrite:** The dialog backend was rewritten to compile
  all enhanced templates into Mojang's official 1.21.6+ dialog types. Although
  substantially more stable, it was kept internal while all five vanilla types,
  persistence, reload behavior, and enhanced form/search/wizard compilation were
  being validated.
- **2.2.0 — placeholder development build:** Per-player placeholders were added,
  but dialog-input value substitution and complete action management were still
  missing. Publishing it would have required another immediate breaking update.
- **2.3.0 — first publishable 2.x build:** This version completes safe
  `{key:<input_key>}` substitution, action listing/removal, gated input handling,
  the native-dialog rewrite, and the placeholder system.

The intermediate JARs were used only for development and testing. They are not
recommended for servers, are not part of the supported upgrade path, and should
not be mirrored as releases. Servers running any internal 2.0.0–2.2.0 build
should replace it directly with `2.3.0` rather than attempting incremental
upgrades.

## Ready-to-install examples

The repository includes a tested Minecraft 1.21.8 example datapack and complete
sample configuration:

```text
examples/guimaker-example-datapack/
examples/world-data/world/data/guimaker/commands.json
examples/world-data/world/data/guimaker/dialogs.json
examples/world-data/world/data/guimaker/guis.json
examples/world-data/world/data/guimaker/limits.json
```

Generated ZIP files are written to `artifacts/`:

```text
guimaker-example-datapack-1.21.8.zip
guimaker-example-world-data-2.5.1.zip
guimaker-example-ready-to-install-2.5.1.zip
```

The ready-to-install bundle contains the mod JAR, sample world data under
`world/data/guimaker/`, and the datapack under `world/datapacks/`. Fabric API is
not bundled. Back up an existing `<world>/data/guimaker` directory before
extracting the example world data.

The datapack load hook schedules an idempotent post-startup bootstrap. It runs
real `guimaker create`, `page add/rename`, `action set`, `dialog create/edit`,
`dialog input add`, `dialog action add`, and `dialog wizard step add` commands.
A world storage marker prevents duplicate creation on subsequent reloads.

The sample was validated by starting and restarting a Minecraft 1.21.8 Fabric
server: eight command presets, the configured GUI/dialogs, the datapack-created
`datapack_demo` GUI and three managed dialogs all persisted without duplicate
creation or command parsing errors.

## Build

```bash
./gradlew build
```

The output is written to `build/libs/`.
