function gu:generate
data modify storage gm:storage temp.macro.player set from storage gu:main out
scoreboard players operation #temp gm.ID = @s gm.ID


function gm:zprivate/prerequisites/item_modifier/main