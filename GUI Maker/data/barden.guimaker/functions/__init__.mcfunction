scoreboard objectives add bgui-createGUIinstance trigger
scoreboard players enable @a[gamemode=creative] bgui-createGUIinstance
scoreboard objectives add bgui-createGUIpage trigger
scoreboard players enable @a[gamemode=creative] bgui-createGUIpage
scoreboard objectives add bgui-getListofAllGuis trigger
scoreboard players enable @a[gamemode=creative] bgui-getListofAllGuis

scoreboard objectives add bgui.id dummy
scoreboard players add .global bgui-id 1
scoreboard objectives add bgui.extra dummy
scoreboard objectives add bgui.iteration dummy

scoreboard players reset @a bgui.extra



#> setblock shulker box 
execute in minecraft:overworld run forceload add 12345678 12345678
execute in minecraft:overworld run setblock 12345678 300 12345678 air replace
execute in minecraft:overworld run setblock 12345678 300 12345678 minecraft:black_shulker_box{Items:[{Count:1b,Slot:8b,id:"minecraft:player_head"},{Count:1b,Slot:0b,id:"minecraft:player_head"}]}