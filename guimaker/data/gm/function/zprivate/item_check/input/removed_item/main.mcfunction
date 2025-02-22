execute if data entity @s data.Items_final[0] run data modify storage gm:storage temp.macro.Slot set from entity @s data.Items_final[0].Slot
execute if data entity @s data.Items_final[0] run return run function gm:zprivate/item_check/input/removed_item/filter with storage gm:storage temp.macro

execute unless data entity @s data.Items_final[0] if data entity @s data.Items_init[0] run data modify storage gm:storage temp.macro set from entity @s data.Items_init[0]
function gm:zprivate/item_check/input/removed_item/run with storage gm:storage temp.macro