$data remove storage gm:storage CACHE[{"player":"$(player)"}].CACHE.ITEM_HOLDER[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].CONTENTS[{Slot:$(Slot)b}]

setblock 0 300 0 air
setblock 0 300 0 barrel
$data modify block 0 300 0 Items set from storage gm:storage CACHE[{"player":"$(player)"}].CACHE.ITEM_HOLDER[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].PLACEHOLDER[{Slot:$(Slot)b}]

data modify block ~ ~ ~ Items set from block 0 300 0 Items
$item replace entity @n[type=chest_minecart,tag=_gui.chestminecart,distance=..1] container.$(Slot) from block 0 300 0 container.$(Slot)
setblock 0 300 0 air
