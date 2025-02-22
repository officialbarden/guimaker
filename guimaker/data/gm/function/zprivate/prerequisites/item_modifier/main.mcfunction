data modify storage gm:storage temp.prerequisite.item_modifier set from block ~ ~ ~ Items
data modify storage gm:storage temp.prerequisite.item_modifier set from entity @n[type=chest_minecart,tag=_gui.chestminecart,predicate=gm:id/common] Items
function gm:zprivate/prerequisites/item_modifier/init