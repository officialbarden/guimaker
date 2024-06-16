$execute store result score $PAGES gm.Values if data storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES
scoreboard players add $PAGES gm.Values 1
function gui.maker:username
execute store result storage barden:names Names.page_number int 1 run scoreboard players get $PAGES gm.Values
$data modify storage barden:names Names.GUI_ID set value "$(GUI_ID)"
function gui.maker:maker/gui/add_page/add/2 with storage barden:names Names
