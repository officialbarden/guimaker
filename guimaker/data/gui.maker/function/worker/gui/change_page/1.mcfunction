
execute as @s[tag=gm.gui.cm] at @s run data remove entity @e[type=chest_minecart,tag=gm.gui.ui,limit=1] Items
execute as @s[tag=gm.gui.cm] at @s run data remove entity @s data.PAGE.INIT
execute as @s[tag=gm.gui.cm] at @s run data remove entity @s data.PAGE.FINAL
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @e[type=chest_minecart,tag=gm.gui.ui,limit=1] Items set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout

execute as @s[type=marker,tag=gm.gui.cm] at @s run function gui.maker:worker/gui/api/detect/1
data remove entity @e[sort=nearest,limit=1,type=chest_minecart,tag=gm.gui.ui] Items[{id:"minecraft:pink_stained_glass_pane",components:{"minecraft:custom_data":{gm.APISLOT:1}}}]

$execute as @s[tag=gm.gui.cm] at @s run function gui.maker:worker/gui/item_modify/1 {GUI_ID:$(change_gui),PAGE:$(change_page)}
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @s data.PAGE.INIT set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @s data.PAGE.FINAL set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @s data.PAGE_INFO.GUI_ID set value $(change_gui)
$execute as @s[tag=gm.gui.cm] at @s run data modify entity @s data.PAGE_INFO.PAGE set value $(change_page)


execute as @s[tag=gm.gui.block] at @s run data remove block ~ ~ ~ Items
execute as @s[tag=gm.gui.block] at @s run data remove entity @s data.PAGE.INIT
execute as @s[tag=gm.gui.block] at @s run data remove entity @s data.PAGE.FINAL

$execute as @s[tag=gm.gui.block] at @s run data modify block ~ ~ ~ Items set from storage barden:gui.all GUI[{GUI_ID:$(change_gui)}].PAGES[{page_number:$(change_page)}].layout
execute as @s[tag=gm.gui.block] at @s run function gui.maker:worker/gui/api/detect/1
execute as @s[tag=gm.gui.block] at @s run data remove block ~ ~ ~ Items[{id:"minecraft:pink_stained_glass_pane",components:{"minecraft:custom_data":{gm.APISLOT:1}}}]


$execute as @s[tag=gm.gui.block] at @s run function gui.maker:worker/gui/item_modify/1 {GUI_ID:$(change_gui),PAGE:$(change_page)}
execute as @s[tag=gm.gui.block] at @s run data modify entity @s data.PAGE.INIT set from block ~ ~ ~ Items
execute as @s[tag=gm.gui.block] at @s run data modify entity @s data.PAGE.FINAL set from block ~ ~ ~ Items
$execute as @s[tag=gm.gui.block] at @s run data modify entity @s data.PAGE_INFO.GUI_ID set value $(change_gui)
$execute as @s[tag=gm.gui.block] at @s run data modify entity @s data.PAGE_INFO.PAGE set value $(change_page)

$function gui.maker:worker/gui/change_name/1 {GUI_ID:$(change_gui),PAGE:$(change_page)}

tag @s remove gm.clicked
tag @s add gm.itemcheck