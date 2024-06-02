execute as @s[tag=gm.gui.cm] at @s run data remove entity @e[type=chest_minecart,tag=gm.gui.ui,limit=1] Items
execute as @s[tag=gm.gui.cm] at @s run data remove entity @s data
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @e[type=chest_minecart,tag=gm.gui.ui,limit=1] Items set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @s data.PAGE.INIT set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @s data.PAGE.FINAL set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @s data.PAGE_INFO.GUI_ID set value $(change_gui)
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @s data.PAGE_INFO.PAGE set value $(change_page)


execute as @s[tag=gm.gui.block] at @s run data remove block ~ ~ ~ Items
execute as @s[tag=gm.gui.block] at @s run data remove entity @s data
$execute as @s[tag=gm.gui.block] at @s run data modify block ~ ~ ~ Items set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
$execute as @s[tag=gm.gui.block] at @s run data modify entity @s data.PAGE.INIT set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
$execute as @s[tag=gm.gui.block] at @s run data modify entity @s data.PAGE.FINAL set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
$execute as @s[tag=gm.gui.block] at @s run data modify entity @s data.PAGE_INFO.GUI_ID set value "$(change_gui)"
$execute as @s[tag=gm.gui.block] at @s run data modify entity @s data.PAGE_INFO.PAGE set value "$(change_page)"

$function gui.maker:worker/gui/change_name/1 {GUI_ID:$(change_gui),PAGE:$(change_page)}

tag @s remove gm.clicked
tag @s add gm.itemcheck