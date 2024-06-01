setblock ~ ~ ~ barrel
kill @s

$summon marker ~ ~ ~ {Tags:["gm.gui.container","gm.gui.block","gm.itemcheck"],data:{PAGE_INFO:{GUI_ID:$(GUI_ID),PAGE:$(page_number)}}}

$data modify block ~ ~ ~ Items set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(page_number)}].layout
$execute as @e[type=marker,tag=gm.gui.block,sort=nearest,limit=1] run function gui.maker:worker/gui/item_modify/1 {GUI_ID:$(GUI_ID),PAGE:$(page_number)}
data modify entity @e[type=marker,tag=gm.gui.block,sort=nearest,limit=1] data.PAGE.INIT set from block ~ ~ ~ Items
data modify entity @e[type=marker,tag=gm.gui.block,sort=nearest,limit=1] data.PAGE.FINAL set from block ~ ~ ~ Items
