data modify storage gm:storage temp.macro.sound set from entity @s data.Items_init[0]."components"."minecraft:custom_data".gui.execute.sound
$execute if data storage gm:storage temp.macro.sound as $(player) at @s run function gm:zprivate/execution/playsound/macro with storage gm:storage temp.macro
