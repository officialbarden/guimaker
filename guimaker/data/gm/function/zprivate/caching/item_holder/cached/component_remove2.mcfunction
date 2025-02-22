setblock 0 300 0 air
setblock 0 300 0 barrel
item replace block 0 300 0 container.0 from entity @s player.cursor
data remove block 0 300 0 Items[0].components."minecraft:custom_data".gui_itemholdercached
item replace entity @s player.cursor from block 0 300 0 container.0
setblock 0 300 0 air
