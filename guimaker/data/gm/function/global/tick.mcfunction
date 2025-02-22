# Minecart Teleport:
execute as @e[tag=_gui.marker,tag=_gui.chestminecart,type=marker] at @s run function gm:zprivate/teleport/move/main

# Item Change Check:
execute as @e[tag=_gui.marker,type=marker,tag=!_gui.temp] at @s run function gm:zprivate/item_check/main

# Kill Markers:
execute as @e[tag=_gui.marker,tag=_gui.barrel,type=marker] at @s unless block ~ ~ ~ barrel run kill @s
execute as @e[tag=_gui.marker,tag=_gui.chestminecart,type=marker] at @s unless entity @n[type=chest_minecart,distance=..1] run kill @s
execute as @e[tag=_gui.chestminecart.itemdisplay,type=item_display] at @s unless entity @n[type=chest_minecart,distance=..1] run kill @s

# Kill Items
kill @e[type=item,nbt={Item:{components:{"minecraft:custom_data":{gui:{}}}}}]
kill @e[type=item,nbt={Item:{components:{"minecraft:custom_data":{gui_itemholdercached:true}}}}]
