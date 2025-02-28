# Barrel
    execute as @s[tag=_gui.barrel,type=marker] at @s if block ~ ~ ~ barrel store success score @s gm.Tests run data modify entity @s data.Items set from block ~ ~ ~ Items
# Chest Minecart
    execute as @s[tag=_gui.chestminecart,type=marker] at @s on vehicle if entity @s[type=chest_minecart] on passengers as @s[type=marker,tag=_gui.chestminecart] store success score @s gm.Tests run data modify entity @s data.Items set from entity @n[type=chest_minecart,tag=_gui.chestminecart] Items

# STORE INITIAL/FINAL ITEM COUNT
execute unless score @s gm.Tests matches 1 run data modify entity @s data.Items_init set from entity @s data.Items
execute if score @s gm.Tests matches 1 run data modify entity @s data.Items_final set from entity @s data.Items
execute unless score @s gm.Tests matches 1 store result score $InitialContents gm.Values run data get entity @s data.Items
execute if score @s gm.Tests matches 1 store result score $FinalContents gm.Values run data get entity @s data.Items

# IF INIT_CONTENTS = FINAL_CONTENTS, AN ITEM HAS BEEN REPLACED
# IF INIT_CONTENTS < FINAL_CONTENTS, AN ITEM HAS BEEN ADDED
# IF INIT_CONTENTS > FINAL_CONTENTS, AN ITEM HAS BEEN REMOVED
execute if score @s gm.Tests matches 1 if score $FinalContents gm.Values = $InitialContents gm.Values run return run function gm:zprivate/item_check/input/replaced_item/main
execute if score @s gm.Tests matches 1 if score $FinalContents gm.Values > $InitialContents gm.Values run return run function gm:zprivate/item_check/input/added_item/main
execute if score @s gm.Tests matches 1 if score $FinalContents gm.Values < $InitialContents gm.Values run return run function gm:zprivate/item_check/input/removed_item/main

