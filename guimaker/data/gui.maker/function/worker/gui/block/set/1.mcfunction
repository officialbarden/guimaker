setblock ~ ~ ~ barrel
kill @s

$summon marker ~ ~ ~ {Tags:["gm.gui.container","gm.gui.block","gm.itemcheck","gm.gui.block.temp"],data:{PAGE_INFO:{GUI_ID:$(GUI_ID),PAGE:$(page_number)}}}

$data modify block ~ ~ ~ Items set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(page_number)}].layout
execute as @e[type=marker,tag=gm.gui.block.temp,sort=nearest,limit=1] run function gui.maker:worker/gui/api/detect/1
data remove block ~ ~ ~ Items[{id:"minecraft:pink_stained_glass_pane",components:{"minecraft:custom_data":{gm.APISLOT:1}}}]


$execute as @e[type=marker,tag=gm.gui.block.temp,sort=nearest,limit=1] at @s run function gui.maker:worker/gui/item_modify/1 {GUI_ID:$(GUI_ID),PAGE:$(page_number)}
$execute as @e[type=marker,tag=gm.gui.block.temp,sort=nearest,limit=1] at @s run function gui.maker:worker/gui/change_name/1 {GUI_ID:$(GUI_ID),PAGE:$(page_number)}

data modify entity @e[type=marker,tag=gm.gui.block,sort=nearest,limit=1] data.PAGE.INIT set from block ~ ~ ~ Items
data modify entity @e[type=marker,tag=gm.gui.block,sort=nearest,limit=1] data.PAGE.FINAL set from block ~ ~ ~ Items

tag @e[tag=gm.gui.block.temp] remove gm.gui.block.temp
