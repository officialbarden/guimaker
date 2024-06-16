forceload add 0 0 0 0
setblock 0 300 0 air
setblock 0 300 0 black_shulker_box
loot insert 0 300 0 loot gui.maker:fph
data remove storage barden:names Names
data modify storage barden:names Names.username set from block 0 300 0 Items[0].components."minecraft:profile".name
data modify storage barden:names Names.UUID set from entity @s UUID[0]
setblock 0 300 0 air
