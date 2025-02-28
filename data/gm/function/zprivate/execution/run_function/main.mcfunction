data modify storage gm:storage temp.macro.item_nbt set from entity @s data.Items_init[0]
data modify storage gm:storage temp.macro.function_file set from entity @s data.Items_init[0]."components"."minecraft:custom_data".gui.execute.function
data modify storage gm:storage temp.macro.command set from entity @s data.Items_init[0]."components"."minecraft:custom_data".gui.execute.command

function gm:zprivate/caching/toggle_button/change_values with storage gm:storage temp.macro
#tellraw @a {"storage":"gm:storage","nbt":"temp.macro.item_nbt.id"}

#> Run Command
$execute if data storage gm:storage temp.macro.command as $(player) at @s run function gm:zprivate/execution/run_function/macro2 with storage gm:storage temp.macro
#> Run Function
$execute if data storage gm:storage temp.macro.function_file as $(player) at @s run function gm:zprivate/execution/run_function/macro with storage gm:storage temp.macro
