data modify storage barden:names Names.function_file set value "empty"
data modify storage barden:names Names.item_modifier set value "empty"

$data modify storage barden:names Names.page_name set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].page_name
$data modify storage barden:names Names.function_file set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].layout[{Slot:$(Slot)b}].components."minecraft:custom_data".BUTTON_INFO.function_file
$data modify storage barden:names Names.item_modifier set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].layout[{Slot:$(Slot)b}].components."minecraft:custom_data".BUTTON_INFO.item_modifier
$data modify storage barden:names Names.change_gui set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].layout[{Slot:$(Slot)b}].components."minecraft:custom_data".BUTTON_INFO.CHANGE_PAGE.GUI_ID
$data modify storage barden:names Names.change_page set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].layout[{Slot:$(Slot)b}].components."minecraft:custom_data".BUTTON_INFO.CHANGE_PAGE.PAGE

function gui.maker:maker/gui/edit_page/tellraw/4 with storage barden:names Names