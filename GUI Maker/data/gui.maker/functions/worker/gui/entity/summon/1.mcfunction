# $(GUI_ID)
# $(page_number)

execute store result score @s gm.Linking run data get entity @s UUID[0]
$summon chest_minecart ~ ~ ~ {Silent:1b,Invulnerable:1b,CustomDisplayTile:1b,Tags:["gm.gui.ui","gm.temp"],Passengers:[{id:"minecraft:interaction",width:1.1f,height:0.7f,Tags:["gm.ui.protect"],Passengers:[{id:"minecraft:marker",Tags:["gm.itemcheck","gm.itemcheck.temp","gm.gui.cm"],data:{PAGE_INFO:{GUI_ID:$(GUI_ID),PAGE:$(page_number)}}}]}],DisplayState:{Name:"minecraft:air"}}
scoreboard players operation @e[type=chest_minecart,tag=gm.temp] gm.Linking = @s gm.Linking


$data modify entity @e[type=chest_minecart,tag=gm.temp,limit=1] Items set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(page_number)}].layout
$execute as @e[type=marker,tag=gm.itemcheck.temp,sort=nearest,limit=1] run function gui.maker:worker/gui/item_modify/1 {GUI_ID:$(GUI_ID),PAGE:$(page_number)}
$execute as @e[type=marker,tag=gm.itemcheck.temp,sort=nearest,limit=1] run function gui.maker:worker/gui/change_name/1 {GUI_ID:$(GUI_ID),PAGE:$(page_number)}

execute as @e[type=marker,tag=gm.itemcheck.temp,sort=nearest,limit=1] run data modify entity @s data.PAGE.INIT set from entity @e[type=chest_minecart,tag=gm.temp,limit=1] Items
execute as @e[type=marker,tag=gm.itemcheck.temp,sort=nearest,limit=1] run data modify entity @s data.PAGE.FINAL set from entity @e[type=chest_minecart,tag=gm.temp,limit=1] Items

tag @e[type=chest_minecart,tag=gm.temp] remove gm.temp
tag @e[type=marker,tag=gm.itemcheck.temp,sort=nearest,limit=1] remove gm.itemcheck.temp
