execute as @e[type=marker,tag=gui.check] at @s store success score @s gm.Test run data modify entity @s data.PAGE.FINAL set from entity @e[type=chest_minecart,sort=nearest,limit=1,tag=guim.gui] Items
execute as @e[type=marker,tag=gui.check] at @s if score @s gm.Test matches 1.. run function gui.maker:worker/gui/entity/click/1 with entity @s data

execute as @e[type=marker,tag=gui.container.place] at @s run function gui.maker:worker/gui/block/place with entity @s data
execute as @e[type=marker,tag=gui.container] at @s unless block ~ ~ ~ barrel run kill @s

execute as @e[type=marker,tag=gui.container.check] at @s store success score @s gm.Test run data modify entity @s data.PAGE.FINAL set from block ~ ~ ~ Items
execute as @e[type=marker,tag=gui.container.check] at @s if score @s gm.Test matches 1.. run function gui.maker:worker/gui/block/click/1 with entity @s data



kill @e[type=item,nbt={Item:{tag:{guim.Button:1b}}}]
