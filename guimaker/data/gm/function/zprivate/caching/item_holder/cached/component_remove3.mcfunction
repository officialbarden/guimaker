setblock 0 300 0 air
setblock 0 300 0 barrel
$item replace block 0 300 0 container.0 from entity @s container.$(Slot)
data remove block 0 300 0 Items[0].components."minecraft:custom_data".gui_itemholdercached
execute if data block 0 300 0 Items[{components:{"minecraft:custom_data":{}}}] run data remove block 0 300 0 Items[0].components

$item replace entity @s container.$(Slot) from block 0 300 0 container.0
setblock 0 300 0 air