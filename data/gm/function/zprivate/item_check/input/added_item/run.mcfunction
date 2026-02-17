#say added item!
#$say $(id)
#$say $(Slot)b

tag @s add _gui.temp

execute as @p[sort=arbitrary,limit=1] at @s positioned ~ ~ ~ rotated as @s[type=minecraft:player] anchored eyes run function gu:generate

data modify storage gm:storage temp.macro.player set from storage gu:main out
function gm:zprivate/item_check/input/added_item/macro with storage gm:storage temp.macro
tag @s remove _gui.temp

