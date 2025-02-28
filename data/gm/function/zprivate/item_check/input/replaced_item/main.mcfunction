data modify storage gm:storage temp.success.item set from entity @s data.Items_init[0]
execute store success score $SameItemCheck gm.Values run data modify storage gm:storage temp.success.item set from entity @s data.Items_final[0]
execute if score $SameItemCheck gm.Values matches 0 run return run function gm:zprivate/item_check/input/replaced_item/filter
execute if score $SameItemCheck gm.Values matches 1 run return run function gm:zprivate/item_check/input/replaced_item/run
