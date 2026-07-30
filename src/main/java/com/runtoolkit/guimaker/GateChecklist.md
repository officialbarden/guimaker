# GUI Maker Fabric Port — Gate Checklist

This file summarizes all gates applied in this port.

| # | Gate | Class | What it prevents |
|---|------|-------|-------------------|
| 1 | Permission Gate | gate/PermissionGate.java | Prevents unauthorized players from editing GUIs (missing in the original) |
| 2 | Action Allowlist Gate | gate/ActionAllowlistGate.java | Prevents arbitrary function/command execution (the main RCE vulnerability) |
| 3 | Rate Limit Gate | gate/RateLimitGate.java | Prevents TPS drops from click spam |
| 4 | Sound Registry | gate/SoundRegistry.java | Fixed list instead of a free-form sound string |
| 5 | Item Preset Registry | gate/ItemPresetRegistry.java | Fixed list instead of a free-form item_modifier string |
| 6 | Page Registry | gui/PageRegistry.java | Fixed, in-mod definition instead of free-form page data |

All actions are handled through the closed ButtonAction enum
(action/ButtonAction.java). No field ever directly executes a
"function" or "command" string.
