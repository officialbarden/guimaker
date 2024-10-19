data remove storage barden:temp list_2

$data modify storage barden:temp list_2 set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}]
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].layout[].components."minecraft:custom_data".BUTTON_INFO.function_file set value "gui.maker:maker/gui/edit_page/tellraw/1 with entity @e[type=marker,tag=gm.clicked,sort=nearest,limit=1] data.PAGE.INIT[0]"

$function gui.maker:worker/gui/entity/summon/1 {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}

$data remove storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}]
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES append from storage barden:temp list_2
tag @s remove gm.editing_page
