# Item Change Check:
execute as @e[tag=_gui.marker,type=marker,tag=!_gui.temp] at @s run function gm:zprivate/item_check/main

# Kill Markers:
execute as @e[tag=_gui.marker,tag=_gui.barrel,type=marker] at @s unless block ~ ~ ~ barrel run kill @s