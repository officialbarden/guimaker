#> tellraw:
# delete/add a hashtag(to comment it) to this tellraw to disable it.
tellraw @a ["",{"text":"[bgui] ","color":"gold"},{"text":"Hello! This datapack has a custom \"void\" dimension essential for it to work. If this is your first time loading the datapack on this world, please /reload and re-join. If this is a server, please /reload and restart your server. Sorry to cause trouble! To disable this tellraw, delete the command from the barden.gui:maker/__init__ file!","color":"white"}]


#> initializing /trigger commands. These can only be ran by players in gmc.
scoreboard objectives add bgui-createGUIprofile trigger
scoreboard players enable @a[gamemode=creative] bgui-createGUIprofile
scoreboard objectives add bgui-getListofAllGuis trigger
scoreboard players enable @a[gamemode=creative] bgui-getListofAllGuis
scoreboard objectives add bgui-addPage trigger
scoreboard players enable @a[gamemode=creative] bgui-addPage
scoreboard objectives add bgui-editPage trigger
scoreboard players enable @a[gamemode=creative] bgui-editPage
scoreboard objectives add bgui-Help trigger
scoreboard players enable @a[gamemode=creative] bgui-Help


#> setblock shulker box in a void world. 
execute in barden.gui:bgui/void run forceload add 0 0
execute in barden.gui:bgui/void run setblock 0 -63 0 air replace
execute in barden.gui:bgui/void run setblock 0 -63 0 minecraft:black_shulker_box{Items:[{Count:1b,Slot:8b,id:"minecraft:player_head"},{Count:1b,Slot:0b,id:"minecraft:player_head"}]}


scoreboard objectives add bgui.guiprofile.id dummy
scoreboard players add .global bgui.guiprofile.id 1
scoreboard objectives add bgui.raycast.limit dummy
scoreboard objectives add bgui.pagenumber.detect dummy


