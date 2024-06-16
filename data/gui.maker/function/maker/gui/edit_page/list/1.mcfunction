data remove storage barden:temp list
$execute store result score $GUIS_OWNED gm.Values if data storage barden:gui.all GUI[{username:$(username)}]

execute if score $GUIS_OWNED gm.Values matches 0 run function gui.maker:maker/gui/edit_page/list/no_entry with storage barden:names Names
$execute if score $GUIS_OWNED gm.Values matches 1.. run data modify storage barden:temp list append from storage barden:gui.all GUI[{username:$(username)}]
execute if score $GUIS_OWNED gm.Values matches 1.. run tellraw @s [{"text":"-------------------------------","color":"#FFDF8D"}]
execute if score $GUIS_OWNED gm.Values matches 1.. run function gui.maker:maker/gui/edit_page/list/2 with storage barden:temp list[0]
tellraw @s [{"text":"-------------------------------","color":"#FFDF8D"}]