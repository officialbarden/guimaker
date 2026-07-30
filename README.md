# GUI Maker — Fabric 1.21.8

A secure, server-side GUI builder that uses vanilla chest screens. It does not
require a custom screen or resource pack. Clients do not need the mod when it is
installed on a dedicated server; for single-player use, install the mod and
Fabric API on the client.

## Requirements

- Minecraft 1.21.8
- Java 21
- Fabric Loader 0.19.x
- Fabric API 0.136.1+1.21.8

Place the mod JAR and the Fabric API JAR in the `mods/` directory.

## Quick start

On first launch, the mod automatically creates an `example` GUI and these files:

- `config/guimaker/guis.json`
- `config/guimaker/commands.json`
- `config/guimaker/limits.json`

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
/guimaker create <gui> <1-6 rows> <title>
/guimaker delete <gui>
/guimaker edit [gui] [page]
/guimaker page add <gui> <page> <1-6 rows> <title>
/guimaker page rename <gui> <page> <title>
/guimaker page remove <gui> <page>
/guimaker item set <gui> <page> <slot>
/guimaker item clear <gui> <page> <slot>
/guimaker action set <gui> <page> <slot> <action> [parameter]
/guimaker action clear <gui> <page> <slot>
/guimaker commands
/guimaker limits
/guimaker reload
```

`item set` stores every data component of the item in the player's main hand,
including its custom name, lore and model data. The item is only a GUI display
item and cannot be removed from the GUI.

## Dialog-based operator menu

Run `/guimaker menu` as an OP level 2+ player to open the Minecraft 1.21.8
vanilla dialog workflow. It follows the original datapack's menu logic while
routing every operation through the Fabric mod's validated commands.

The main dialog contains:

- **Create GUI** — create a new profile or append a page with input forms.
- **Edit GUI** — choose a GUI and page, rename it, open the visual item editor,
  or configure a button's allowlisted action and parameter.
- **Delete GUI** — delete a profile or page through a confirmation dialog.
- **Open GUI** — choose a GUI and page to preview.

GUI, page and button lists are paginated in groups of ten, so the dialog remains
usable with high limits from `limits.json`. All menu routes require OP level 2,
and all submitted values still pass through the same ID, row, slot, action,
permission and allowlist gates as the text commands.

The dialogs are built server-side with Minecraft's native dialog API. No custom
client screen or resource pack is required.

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
`config/guimaker/commands.json` allowlist. A GUI button stores a preset ID such
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
      "silent": false
    },
    {
      "id": "welcome_message",
      "command": "tellraw {player} {\"text\":\"Welcome!\",\"color\":\"green\"}",
      "run_as": "SERVER",
      "server_permission_level": 2,
      "required_player_permission_level": 0,
      "cooldown_ms": 1000,
      "silent": true
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
`config/guimaker/limits.json`:

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

- GUI file: `config/guimaker/guis.json`
- Command allowlist: `config/guimaker/commands.json`
- Capacity settings: `config/guimaker/limits.json`
- Every GUI change is saved immediately using an atomic file replacement.
- Invalid files are backed up as `*.broken-<date>.json`.
- GUI and command-preset limits are read from `limits.json`.
- Each GUI may contain up to 64 pages with 1-6 rows per page.
- The last GUI and the last page in a GUI cannot be removed accidentally.
- General button clicks are rate-limited to one action per player every 150 ms.
- Command actions also have their own per-preset cooldown.

## Build

```bash
./gradlew build
```

The output is written to `build/libs/`.
