setblock 0 300 0 air
setblock 0 300 0 barrel
$data modify block 0 300 0 Items set from storage gm:storage CACHE[{player:"$(player)"}].CACHE.TOGGLE_BUTTON[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].TOGGLE

$item replace block ~ ~ ~ container.$(Slot) from block 0 300 0 container.$(Slot)
$item replace entity @n[type=chest_minecart,tag=_gui.chestminecart,distance=..1] container.$(Slot) from block 0 300 0 container.$(Slot)
