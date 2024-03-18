# $(GUI_ID)
$tellraw @s ["",{"text":"---------------------------------------","color":"gray"},{"text":"\n"},{"text":"[bgui] ","color":"gold"},{"text":"getting GUI #$(GUI_ID)..","color":"white"},{"text":"\n "}]
data remove storage minecraft:barden.gui.list.query Query
$data modify storage minecraft:barden.gui.list.query Query append from storage minecraft:barden.gui.all GUI[{guiid:$(GUI_ID)}]
execute store result score @s bgui.iteration run data get storage minecraft:barden.gui.list.query Query

function barden.guimaker:commands/allguilist/search_from_id/__2__ with storage minecraft:barden.gui.list.query Query[0]
