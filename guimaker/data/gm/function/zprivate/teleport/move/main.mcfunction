data modify storage gm:storage temp.macro.entity set from entity @s data.UUIDS.entity
data modify storage gm:storage temp.macro.owner set from entity @s data.UUIDS.owner

execute as @n[type=item_display,tag=_gui.chestminecart.itemdisplay] at @s run function gm:zprivate/teleport/move/macro with storage gm:storage temp.macro
