summon chest_minecart ~ ~ ~ {NoGravity:1b,Silent:1b,CustomDisplayTile:1b,Tags:["guim.gui"],Passengers:[{id:"minecraft:marker",Tags:["gui.check"]}],DisplayState:{Name:"minecraft:air"}}
$data modify entity @e[type=marker,sort=nearest,limit=1,tag=gui.check] data.GUI_ID set value $(GUI_ID)
$data modify entity @e[type=marker,sort=nearest,limit=1,tag=gui.check] data.PAGE set value $(PAGE)

$execute as @e[type=chest_minecart,tag=guim.gui] at @s run function gui.maker:worker/gui/entity/summon/3 with storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)]

data modify entity @e[type=marker,sort=nearest,limit=1,tag=gui.check] data.PAGE.INIT set from entity @s Items
data modify entity @e[type=marker,sort=nearest,limit=1,tag=gui.check] data.PAGE.FINAL set from entity @s Items

