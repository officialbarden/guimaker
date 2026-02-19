# $(command) $(item_modifier) $(function) $(change_to_guiid) $(change_to_page)

data remove storage gm:storage temp.MACRO.edit_gui
# // Initialize the macros so no macro is left undefined:

$data modify storage gm:storage temp.MACRO.edit_gui.command set value "$(command)"
$data modify storage gm:storage temp.MACRO.edit_gui.item_modifier set value "$(item_modifier)"
$data modify storage gm:storage temp.MACRO.edit_gui.function set value "$(function)"
$data modify storage gm:storage temp.MACRO.edit_gui.change_to_guiid set value $(change_to_guiid)
$data modify storage gm:storage temp.MACRO.edit_gui.change_to_page set value $(change_to_page)
execute store result storage gm:storage temp.MACRO.edit_gui.GUI_ID int 1 run scoreboard players get @s gm.EditConfig.SourceGUIID
execute store result storage gm:storage temp.MACRO.edit_gui.PAGE int 1 run scoreboard players get @s gm.EditConfig.SourcePAGE
execute store result storage gm:storage temp.MACRO.edit_gui.Slot int 1 run scoreboard players get @s gm.EditConfig.Slot
function gm:zprivate/dialog/edit_gui/page/item_config/apply_macro with storage gm:storage temp.MACRO.edit_gui


