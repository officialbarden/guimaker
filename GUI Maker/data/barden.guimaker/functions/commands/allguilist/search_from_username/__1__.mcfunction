# $(username)
$tellraw @s ["",{"text":"---------------------------------------","color":"gray"},{"text":"\n"},{"text":"[bgui] ","color":"gold"},{"text":"getting list of all GUIs made by $(username)..","color":"white"},{"text":"\n "}]

data remove storage minecraft:barden.gui.list.query Query
$data modify storage minecraft:barden.gui.list.query Query append from storage minecraft:barden.gui.all GUI[{creator:$(username)}]
execute store result score @s bgui.iteration run data get storage minecraft:barden.gui.list.query Query

function barden.guimaker:commands/allguilist/search_from_username/__2__ with storage minecraft:barden.gui.list.query Query[0]
