tag @s remove gui.check

execute store result score $Items gm.Extras run data get entity @s data.PAGE.FINAL
execute if score $Items gm.Extras matches 1.. run function gui.maker:worker/gui/entity/click/2 with entity @s data.PAGE.FINAL[0]

execute if score $Items gm.Extras matches 0 run function gui.maker:worker/gui/entity/click/3 with entity @s data.PAGE.INIT[0]
