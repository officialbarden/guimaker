data remove storage barden:temp list

execute as @s[tag=gm.gui.cm] at @s run data modify storage barden:temp list append from entity @e[type=chest_minecart,sort=nearest,limit=1,tag=gm.gui.ui] Items[]
execute as @s[tag=gm.gui.block] at @s run data modify storage barden:temp list append from block ~ ~ ~ Items[]

execute store result score $ITEMS gm.Values run data get storage barden:temp list

execute if score $ITEMS gm.Values matches 1.. run function gui.maker:worker/gui/item_modify/2 with storage barden:temp list[0]
