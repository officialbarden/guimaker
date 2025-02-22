data remove storage gm:storage temp.macro
data modify storage gm:storage temp.macro.Slot set from storage gm:storage temp.prerequisite.item_modifier[0].Slot
data modify storage gm:storage temp.macro.ItemModifier set from storage gm:storage temp.prerequisite.item_modifier[0].components."minecraft:custom_data".gui.execute.item_modifier
function gm:zprivate/prerequisites/item_modifier/macro with storage gm:storage temp.macro

data remove storage gm:storage temp.prerequisite.item_modifier[0]
execute if data storage gm:storage temp.prerequisite.item_modifier[0] run function gm:zprivate/prerequisites/item_modifier/init