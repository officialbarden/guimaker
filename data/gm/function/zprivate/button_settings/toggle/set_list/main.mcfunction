setblock 0 300 0 air
setblock 0 300 0 barrel
setblock 0 301 0 air
setblock 0 301 0 barrel

data modify block 0 300 0 Items set from block ~ ~-1 ~ Items
item replace block 0 301 0 container.0 from entity @s weapon.mainhand
data modify block 0 301 0 Items[0].components."minecraft:custom_data".gui.extras.togglebutton_list append from block 0 300 0 Items[]

item replace entity @s weapon.mainhand from block 0 301 0 container.0
setblock 0 300 0 air
setblock 0 301 0 air
