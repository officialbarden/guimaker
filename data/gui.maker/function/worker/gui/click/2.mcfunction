scoreboard players operation $total_items_post gm.Values = $Contents.Post gm.Values
execute if score $total_items_post gm.Values matches 1.. run function gui.maker:worker/gui/click/3 with entity @s data.PAGE.FINAL[0]
data modify storage barden:temp Item.custom_data set from entity @s data.PAGE.INIT[0].components.minecraft:custom_data
execute if score $total_items_post gm.Values matches 0 run function gui.maker:worker/gui/click/success with entity @s data.PAGE.INIT[0]