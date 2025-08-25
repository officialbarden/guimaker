$data modify storage gm:storage temp.DIALOG.list.ACTIONS_LIST append value {"action":{"type":"run_command", "command":"function gm:zprivate/dialog/edit_gui/page/item_config/main {Slot: $(Slot)}"},label:"$(Slot)",tooltip:[{text:"Item Information:\n\n"},{"text":"","color":"#ffbf00"},{text:"\ncount: $(count)\nSlot: $(Slot)b\n\n"},{text:""},{"text":"command: undefined\nitem modifier: undefined\nfunction file: undefined\n\n"}],width:20}

# // past barden: put the information onto the button's tooltip:
$function gm:zprivate/dialog/edit_gui/page/get_items/special_information/item_name {id:"$(id)"}
function gm:zprivate/dialog/edit_gui/page/get_items/special_information/execute with storage gm:storage temp.DIALOG.list.MAIN[0].components."minecraft:custom_data".gui.execute
function gm:zprivate/dialog/edit_gui/page/get_items/special_information/change_menu with storage gm:storage temp.DIALOG.list.MAIN[0].components."minecraft:custom_data".gui.execute.change_menu

data remove storage gm:storage temp.DIALOG.list.MAIN[0]
execute if data storage gm:storage temp.DIALOG.list.MAIN[0] run function gm:zprivate/dialog/edit_gui/page/get_items/format with storage gm:storage temp.DIALOG.list.MAIN[0]
