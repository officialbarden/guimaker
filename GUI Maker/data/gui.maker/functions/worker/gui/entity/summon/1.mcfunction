# $(GUI_ID)
# $(page_number)

execute store result score @s gm.Linking run data get entity @s UUID[0]
$summon chest_minecart ~ ~ ~ {Tags:["gm.gui.ui","gm.temp"],Passengers:[{Tags:["gm.ui.protect"],id:"minecraft:interaction",width:0.3f,height:0.7f,Passengers:[{data:{PAGE_INFO:{GUI_ID:$(GUI_ID),PAGE:$(page_number)}},id:"minecraft:marker",Tags:["gm.itemcheck","gm.itemcheck.temp","gm.gui.cm"]}]}]}
scoreboard players operation @e[type=chest_minecart,tag=gm.temp] gm.Linking = @s gm.Linking


$execute as @e[type=marker,tag=gm.itemcheck.temp,sort=nearest,limit=1] run data modify entity @s data.PAGE.INIT set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(page_number)}].layout
$execute as @e[type=marker,tag=gm.itemcheck.temp,sort=nearest,limit=1] run data modify entity @s data.PAGE.FINAL set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(page_number)}].layout
$data modify entity @e[type=chest_minecart,tag=gm.temp,limit=1] Items set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(page_number)}].layout

tag @e[type=chest_minecart,tag=gm.temp] remove gm.temp
tag @e[type=marker,tag=gm.itemcheck.temp,sort=nearest,limit=1] remove gm.itemcheck.temp
