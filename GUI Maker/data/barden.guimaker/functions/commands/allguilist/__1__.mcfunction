# get player username.
execute in overworld run item modify block 12345678 300 12345678 container.0 barden.guimaker:fill_player_head
execute in overworld run data modify storage minecraft:barden.names.fetch Names[].username set from block 12345678 300 12345678 Items[{Slot:0b}].tag.SkullOwner.Name
execute in overworld run item replace block 12345678 300 12345678 container.0 with player_head

function barden.guimaker:commands/allguilist/__2__ with storage barden.names.fetch Names[0]


scoreboard players reset @s bgui-getListofAllGuis
scoreboard players enable @s bgui-getListofAllGuis
data remove storage barden.names.fetch Names[0]
