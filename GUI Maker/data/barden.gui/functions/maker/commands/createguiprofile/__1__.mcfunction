# get player's username.
execute in barden.gui:bgui/void run item modify block 0 -63 0 container.0 barden.gui:fill_player_head
execute in barden.gui:bgui/void run data modify storage barden:gui.players.names Names[].username set from block 0 -63 0 Items[{Slot:0b}].tag.SkullOwner.Name
execute in barden.gui:bgui/void run item replace block 0 -63 0 container.0 with player_head

scoreboard players operation @s bgui.guiprofile.id = .global bgui.guiprofile.id
scoreboard players add .global bgui.guiprofile.id 1

execute store result storage barden:gui.players.names Names[].guiassignedid int 1 run scoreboard players get @s bgui.guiprofile.id
function barden.gui:maker/commands/createguiprofile/__2__ with storage barden:gui.players.names Names[0]

scoreboard players reset @s bgui-createGUIprofile
scoreboard players enable @s bgui-createGUIprofile
