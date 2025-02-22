#> Summon Entities and Make them Ride:
summon item_display ~ ~ ~ {Tags:["_gui.temp","_gui.chestminecart.itemdisplay"],teleport_duration:0}
summon chest_minecart ~ ~ ~ {Tags:["_gui.temp","_gui.chestminecart"],Passengers:[{id:"marker",Tags:["_gui.temp","_gui.chestminecart","_gui.marker"]}]}
execute as @n[tag=_gui.temp,type=chest_minecart,tag=_gui.chestminecart] run ride @s mount @n[tag=_gui.temp,tag=_gui.chestminecart.itemdisplay,type=item_display]

#> Store UUID:
execute as @n[type=item_display,tag=_gui.chestminecart.itemdisplay,tag=_gui.temp] run function gu:generate
data modify entity @n[type=marker,tag=_gui.marker,tag=_gui.temp] data.UUIDS.entity set from storage gu:main out
function gu:generate
data modify entity @n[type=marker,tag=_gui.marker,tag=_gui.temp] data.UUIDS.owner set from storage gu:main out

$data modify entity @n[type=chest_minecart,tag=_gui.chestminecart] Items set from storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}].PAGE_CONTENTS
function gm:zprivate/prerequisites/main
data modify entity @n[type=marker,tag=_gui.marker,tag=_gui.chestminecart,tag=_gui.temp] data.Items set from entity @n[type=chest_minecart,tag=_gui.chestminecart,tag=_gui.temp] Items
tag @e remove _gui.temp