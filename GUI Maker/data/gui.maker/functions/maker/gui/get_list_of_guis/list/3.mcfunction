data remove storage barden:temp list
$execute store result score $PAGES gm.Values run data get storage barden:gui.all GUI[{username:$(username),GUI_ID:$(GUI_ID)}].PAGES

execute if score $PAGES gm.Values matches 0 run tellraw @s {"text": ">> This GUI does not have any page!","color": "gray"}
$execute if score $PAGES gm.Values matches 1.. run data modify storage barden:temp list set from storage barden:gui.all GUI[{username:$(username),GUI_ID:$(GUI_ID)}].PAGES
execute if score $PAGES gm.Values matches 1.. run tellraw @s [{"text":"-------------------------------","color":"#FFDF8D"}]
$data modify storage barden:temp list[].GUI_ID set value "$(GUI_ID)"
execute if score $PAGES gm.Values matches 1.. run function gui.maker:maker/gui/get_list_of_guis/list/4 with storage barden:temp list[0]
tellraw @s [{"text":"-------------------------------","color":"#FFDF8D"}]