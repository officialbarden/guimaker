
setblock 0 300 0 chest
data modify block 0 300 0 Items set from entity @s data.Items_final
$item replace entity $(player) player.cursor from block 0 300 0 container.$(Slot)
setblock 0 300 0 air

$data remove block ~ ~ ~ Items[{Slot:$(Slot)b}]
$data remove entity @n[type=chest_minecart,tag=_gui.chestminecart,distance=..1] Items[{Slot:$(Slot)b}]
data modify entity @s data.Items set from block ~ ~ ~ Items
data modify entity @s data.Items set from entity @n[type=chest_minecart,tag=_gui.chestminecart,distance=..1] Items
