# get player username.
execute in overworld run item modify block 12345678 300 12345678 container.0 barden.guimaker:fill_player_head
execute in overworld run data modify storage minecraft:barden.names.fetch Names[].username set from block 12345678 300 12345678 Items[{Slot:0b}].tag.SkullOwner.Name
execute in overworld run item replace block 12345678 300 12345678 container.0 with player_head




scoreboard players operation @s bgui.id = .global bgui.id
scoreboard players add .global bgui.id 1

#execute store result storage minecraft:barden.gui.all GUI[].guiid int 1 run scoreboard players get @s bgui-id
execute store result storage minecraft:barden.names.fetch Names[].guiassignedid int 1 run scoreboard players get @s bgui.id

function barden.guimaker:commands/createguiinstance/__2__ with storage barden.names.fetch Names[0]


scoreboard players reset @s bgui-createGUIinstance
scoreboard players enable @s bgui-createGUIinstance
