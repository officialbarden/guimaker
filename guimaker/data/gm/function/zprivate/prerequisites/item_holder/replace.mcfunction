setblock 0 300 0 air
setblock 0 300 0 barrel

data modify block 0 300 0 Items set from storage gm:storage temp.cache.list

$item replace entity @n[type=chest_minecart,tag=_gui.chestminecart,distance=..1] container.$(Slot) from block 0 300 0 container.$(Slot)
$item replace block ~ ~ ~ container.$(Slot) from block 0 300 0 container.$(Slot)
setblock 0 300 0 air

data remove storage gm:storage temp.cache.list[0]
data modify storage gm:storage temp.macro.Slot set from storage gm:storage temp.cache.list[0].Slot
execute if data storage gm:storage temp.cache.list[0] run function gm:zprivate/prerequisites/item_holder/replace with storage gm:storage temp.macro
