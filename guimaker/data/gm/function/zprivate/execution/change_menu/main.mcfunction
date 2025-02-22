data remove block ~ ~ ~ Items
clear @a *[custom_data~{gui:{}}]
data modify storage gm:storage temp.macro.GUI_ID set from entity @s data.Items_init[0].components."minecraft:custom_data".gui.execute.change_menu.GUI_ID
data modify storage gm:storage temp.macro.PAGE set from entity @s data.Items_init[0].components."minecraft:custom_data".gui.execute.change_menu.PAGE
function gm:zprivate/execution/change_menu/macro with storage gm:storage temp.macro

data modify entity @s data.Items set from block ~ ~ ~ Items
data modify entity @s data.Items set from entity @n[type=chest_minecart,tag=_gui.chestminecart,distance=..1] Items
