data modify storage gm:storage temp.macro.item_nbt set from entity @s data.Items_init[0]
data modify storage gm:storage temp.macro.function_file set from entity @s data.Items_init[0]."components"."minecraft:custom_data".gui.execute.function
data modify storage gm:storage temp.macro.command set from entity @s data.Items_init[0]."components"."minecraft:custom_data".gui.execute.command
data modify storage gm:storage temp.macro.commands set from entity @s data.Items_init[0]."components"."minecraft:custom_data".gui.execute.commands
data modify storage gm:storage temp.macro.close_gui set from entity @s data.Items_init[0]."components"."minecraft:custom_data".gui.execute.close_gui

function gm:zprivate/caching/toggle_button/change_values with storage gm:storage temp.macro
#tellraw @a {"storage":"gm:storage","nbt":"temp.macro.item_nbt.id"}

#> Run Multi-Commands (if exists)
execute if data storage gm:storage temp.macro.commands[0] run data modify storage gm:storage temp.player set from storage gm:storage temp.macro.player
execute if data storage gm:storage temp.macro.commands[0] run function gm:zprivate/execution/run_commands/main
#> Run Single Command
$execute unless data storage gm:storage temp.macro.commands[0] if data storage gm:storage temp.macro.command as $(player) at @s run function gm:zprivate/execution/run_function/macro2 with storage gm:storage temp.macro
#> Run Function
$execute if data storage gm:storage temp.macro.function_file as $(player) at @s run function gm:zprivate/execution/run_function/macro with storage gm:storage temp.macro

#> Close GUI if execute.close_gui is true
$execute if data storage gm:storage {temp:{macro:{close_gui:true}}} as $(player) at @s run function gm:zprivate/vitem/close_gui
