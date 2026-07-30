# GUI Maker — Fabric Port (Gated)

Gated Fabric mod port of the original `GUI_Maker_v2_0_2` datapack,
produced following a security audit.

## Why it was ported to Fabric

The audit found that the original datapack's `run_function/main.mcfunction`
executed a "function" string coming directly from player input
(`$function $(function_file) ...`). This meant any player could trigger
arbitrary functions on the server. The "command" field had the same
pattern. There was no sanitization, allowlist, or permission check anywhere.

## Gates Applied

See `src/main/java/com/runtoolkit/guimaker/GateChecklist.md`

## Structure

- `action/ButtonAction.java` — closed enum, replaces arbitrary code execution
- `gate/` — PermissionGate, ActionAllowlistGate, RateLimitGate, SoundRegistry, ItemPresetRegistry
- `gui/` — GuiButtonExecutor (central gate chain), GuiEditCommand, GuiPageManager, PageRegistry
- `screen/` — GuiMakerScreenHandler (actual inventory screen)

## Status

Skeleton + gate architecture complete. The actual page/button
configuration UI (the dialog for players to edit page content) will be
added in a future step — currently PageRegistry can only be populated
from code.

Target: Minecraft 1.21.8, Yarn mappings 1.21.8+build.1.
