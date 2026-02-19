# $(command) $(item_modifier) $(function) $(change_to_guiid) $(change_to_page)

data remove storage gm:storage temp.MACRO.edit_gui
# // Initialize the macros so no macro is left undefined:

# Control
$execute unless data storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}] run tellraw @s [{"text":"[GUI Maker]","color":"gray","italic":false}," ",{"text":"Not Found:","color":"red"},{"text":"\nID:","color":"red"}," ",{"text":"$(GUI_ID)","color":"yellow"},"\n",{"text":"PAGE:","color":"red"}," ",{"text":"$(PAGE)","color":"yellow"}]
$execute unless data storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}] run playsound minecraft:block.note_block.didgeridoo master @s ~ ~ ~ 4 1
$execute unless data storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}] run return 0

# Set GUI ID / PAGE / Slot
$scoreboard players set @s gm.EditConfig.SourceGUIID $(GUI_ID)
$scoreboard players set @s gm.EditConfig.SourcePAGE $(PAGE)
$scoreboard players set @s gm.EditConfig.Slot $(Slot)

$data modify storage gm:storage temp.MACRO.edit_gui.command set value "$(command)"
$data modify storage gm:storage temp.MACRO.edit_gui.item_modifier set value "$(item_modifier)"
$data modify storage gm:storage temp.MACRO.edit_gui.function set value "$(function)"
$data modify storage gm:storage temp.MACRO.edit_gui.sound set value "$(sound)"
$data modify storage gm:storage temp.MACRO.edit_gui.change_to_guiid set value $(change_to_guiid)
$data modify storage gm:storage temp.MACRO.edit_gui.change_to_page set value $(change_to_page)
execute store result storage gm:storage temp.MACRO.edit_gui.GUI_ID int 1 run scoreboard players get @s gm.EditConfig.SourceGUIID
execute store result storage gm:storage temp.MACRO.edit_gui.PAGE int 1 run scoreboard players get @s gm.EditConfig.SourcePAGE
execute store result storage gm:storage temp.MACRO.edit_gui.Slot int 1 run scoreboard players get @s gm.EditConfig.Slot
function gm:zprivate/gui_edit/item_config/macro with storage gm:storage temp.MACRO.edit_gui

