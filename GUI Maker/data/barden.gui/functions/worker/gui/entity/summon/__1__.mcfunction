#$(GUI_ID)
execute if score @s bgui.entity.linking = @e[type=chest_minecart,tag=bgui.gui-true,sort=nearest,limit=1] bgui.entity.linking run kill @e[tag=bgui.gui-true,sort=nearest,limit=1]


#> summoning chest minecart entity:

#$execute anchored eyes positioned ^ ^-0.2 ^0.3 run summon chest_minecart ~ ~ ~ {CustomNameVisible:0b,Invulnerable:1b,Items:[{Slot:13b,id:"minecraft:barrier",Count:1b,tag:{display:{Name:'{"text":"GUI Doesn\'t Exist for this ID!","color":"red","bold":true,"italic":false}'},bguiButton:1b}}],NoGravity:1b,CustomNameVisible:1b,CustomDisplayTile:1,Silent:1b,Tags:["bgui.gui-false","$(GUI_ID)"],DisplayState:{Name:"minecraft:air"},Passengers:[{id:"minecraft:marker",Tags:["bgui.marker.itemchangecheck"]}]}
$execute anchored eyes positioned ^ ^-0.2 ^0.3 run summon chest_minecart ~ ~ ~ {CustomNameVisible:0b,Invulnerable:1b,Items:[{Slot:13b,id:"minecraft:barrier",Count:1b,tag:{display:{Name:'{"text":"GUI Doesn\'t Exist for this ID!","color":"red","bold":true,"italic":false}'},bguiButton:1b}}],NoGravity:1b,CustomNameVisible:1b,CustomDisplayTile:1,Silent:1b,Tags:["bgui.gui-false","$(GUI_ID)"],DisplayState:{Name:"minecraft:air"},Passengers:[{Tags:["bgui.guiprotection"],id:"minecraft:interaction",CustomNameVisible:0b,width:1.2f,height:0.7f,response:0b,Passengers:[{id:"minecraft:marker",CustomNameVisible:0b,Tags:["bgui.marker.itemchangecheck","bgui.p1check"]}]}]}
data modify entity @e[type=chest_minecart,tag=bgui.gui-false,sort=nearest,limit=1] CustomName set value '{"text":"GUI DOES NOT EXIST"}'
data modify entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data.minecartuuid append from entity @e[type=chest_minecart,tag=bgui.gui-false,sort=nearest,limit=1] UUID[]

#> linking chest_minecart to player.
execute store result score @s bgui.entity.linking run data get entity @s UUID[0]
tag @s add bgui.pl.temp
scoreboard players operation @e[type=chest_minecart,tag=bgui.gui-false,sort=nearest,limit=1] bgui.entity.linking = @p[tag=bgui.pl.temp] bgui.entity.linking
tag @s remove bgui.pl.temp


#> conforming to GUI_ID (establishing connection...)

$data modify storage barden:gui.entity.id.conforming CONNECTION append value {"GUI_ID":$(GUI_ID)}

