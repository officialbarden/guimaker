tag @s remove gui.container.check

execute store result score $Items gm.Extras run data get entity @s data.PAGE.FINAL
execute store result score $Contents.Pre gm.Extras run data get entity @s data.PAGE.INIT
execute store result score $Contents.Post gm.Extras run data get entity @s data.PAGE.FINAL

execute if score $Contents.Post gm.Extras >= $Contents.Pre gm.Extras run function gui.maker:worker/gui/block/click/compare/1


execute if score $Contents.Post gm.Extras < $Contents.Pre gm.Extras if score $Items gm.Extras matches 1.. run function gui.maker:worker/gui/block/click/2 with entity @s data.PAGE.FINAL[0]
execute if score $Contents.Post gm.Extras < $Contents.Pre gm.Extras if score $Items gm.Extras matches 0 run function gui.maker:worker/gui/block/click/4 with entity @s data.PAGE.INIT[0]
execute if score $Contents.Post gm.Extras < $Contents.Pre gm.Extras if score $Items gm.Extras matches 0 run function gui.maker:worker/gui/block/click/3 with entity @s data.PAGE.INIT[0].tag.GUIM_INFO

