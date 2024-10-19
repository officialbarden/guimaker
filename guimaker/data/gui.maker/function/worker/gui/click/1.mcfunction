tag @s remove gm.itemcheck
tag @s add gm.clicked
scoreboard players reset @s gm.Tests

execute as @s[tag=gm.gui.block] store result score $Contents.Pre gm.Values run data get entity @s data.PAGE.INIT
execute as @s[tag=gm.gui.block] store result score $Contents.Post gm.Values run data get entity @s data.PAGE.FINAL

execute as @s[tag=gm.gui.cm] store result score $Contents.Pre gm.Values run data get entity @s data.PAGE.INIT
execute as @s[tag=gm.gui.cm] store result score $Contents.Post gm.Values run data get entity @s data.PAGE.FINAL


execute if score $Contents.Post gm.Values < $Contents.Pre gm.Values run function gui.maker:worker/gui/click/2 with entity @s data
