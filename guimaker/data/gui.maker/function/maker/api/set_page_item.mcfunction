# $(item_modifier) $(function_file) $(change_gui) $(change_page)
data modify storage barden:temp ModifyHandItem.Item."gm.Button" set value 1
$data modify storage barden:temp ModifyHandItem.Item.BUTTON_INFO."function_file" set value "$(function_file)"
$data modify storage barden:temp ModifyHandItem.Item.BUTTON_INFO."item_modifier" set value "$(item_modifier)"
$data modify storage barden:temp ModifyHandItem.Item.BUTTON_INFO.CHANGE_PAGE.GUI_ID set value "$(change_gui)"
$data modify storage barden:temp ModifyHandItem.Item.BUTTON_INFO.CHANGE_PAGE.PAGE set value "$(change_page)"
