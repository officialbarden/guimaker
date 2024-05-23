execute store success score $CHECK_IF_REPLACED gm.Extras run data modify entity @e[type=minecraft:marker,limit=1] data.PAGE.FINAL[0].id set from entity @e[type=minecraft:marker,limit=1] data.PAGE.COMPARE[0].id
# if $CHECK_IF_REPLACED = 0 then item was not replaced. 1 if item was replaced.
execute if score $CHECK_IF_REPLACED gm.Extras matches 0 run data remove entity @s data.PAGE.COMPARE[0]
execute if score $CHECK_IF_REPLACED gm.Extras matches 0 run data remove entity @s data.PAGE.FINAL[0]
execute if score $CHECK_IF_REPLACED gm.Extras matches 1 run function gui.maker:worker/gui/block/click/compare/2 with entity @s data.PAGE.FINAL[0]

scoreboard players remove $Items gm.Extras 1
execute if score $Items gm.Extras matches 1.. run function gui.maker:worker/gui/block/click/compare/1 with entity @s data.PAGE.COMPARE[0]
