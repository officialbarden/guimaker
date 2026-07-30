# GUI Maker Fabric — Security Gate Checklist

| # | Gate | Implementation | Protection |
|---|---|---|---|
| 1 | Administrator permission | `PermissionGate` | Creation, editing and deletion require OP level 2+ |
| 2 | Closed action allowlist | `ButtonAction`, `ActionAllowlistGate` | Only known action types and validated parameters are dispatched |
| 3 | General click rate limit | `RateLimitGate` | Protects against click spam and TPS abuse |
| 4 | Sound allowlist | `SoundRegistry` | Only known sounds can be played |
| 5 | Item preset allowlist | `ItemPresetRegistry` | Free-form NBT and item modifiers are never executed |
| 6 | Command preset allowlist | `CommandPresetRegistry` | GUI data stores preset IDs, never raw commands |
| 7 | Command permission/cooldown | `CommandExecutionGate` | Per-preset permission and cooldown checks |
| 8 | Controlled command dispatcher | `GatedCommandExecutor` | PLAYER mode never elevates; SERVER mode uses an explicit level |
| 9 | Configurable capacity | `LimitsConfig` | Validated GUI and command-preset limits from `limits.json` |
| 10 | Dialog command routing | `GuiMenuCommand`, `DialogMenuManager` | OP-only routes call existing validated operations |
| 11 | Safe persistence | `GuiRepository` | Validated JSON, atomic writes and invalid-file backups |
| 12 | Read-only runtime GUI | `GuiMakerScreenHandler` | Display items cannot be taken or duplicated |
| 13 | Permission-gated visual editor | `GuiEditorScreenHandler` | Items are copied without consumption; inventories are not moved |

Actions are restricted to the `ButtonAction` enum. `OPEN_PAGE` can only open a
registered page in the same GUI; `PLAY_SOUND` and `GIVE_ITEM` use fixed
allowlists. `RUN_COMMAND` accepts only an ID from the server-owned
`commands.json` allowlist. Player input and GUI definitions are never
interpreted as raw Minecraft commands or functions.
