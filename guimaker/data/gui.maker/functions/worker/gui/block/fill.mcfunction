$execute if data storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)].layout run data remove block ~ ~ ~ Items
$execute if data storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)].layout run data modify block ~ ~ ~ Items set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)].layout
$execute if data storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)].page_name run data modify block ~ ~ ~ CustomName set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)].page_name

$execute if data storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)].API run function gui.maker:worker/gui/block/api/1 {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}

tag @e[type=marker,sort=nearest,limit=1,tag=gui.container] add gui.container.check
execute as @e[type=marker,sort=nearest,limit=1,tag=gui.container.check] at @s run data modify entity @s data.PAGE.INIT set from block ~ ~ ~ Items
execute as @e[type=marker,sort=nearest,limit=1,tag=gui.container.check] at @s run data modify entity @s data.PAGE.FINAL set from block ~ ~ ~ Items
execute as @e[type=marker,sort=nearest,limit=1,tag=gui.container.check] at @s run data modify entity @s data.PAGE.COMPARE set from block ~ ~ ~ Items
