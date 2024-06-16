data remove entity @s data.itemModify
data modify entity @s data.itemModify.Slot set from storage barden:temp list[0].Slot
data modify entity @s data.itemModify.Modifier set from storage barden:temp list[0].components."minecraft:custom_data".BUTTON_INFO.item_modifier
execute as @p at @s run function gui.maker:worker/gui/item_modify/3 with entity @e[type=marker,sort=nearest,limit=1] data.itemModify

scoreboard players remove $ITEMS gm.Values 1
data remove storage barden:temp list[0]
execute if score $ITEMS gm.Values matches 1.. run function gui.maker:worker/gui/item_modify/2 with storage barden:temp list[0]