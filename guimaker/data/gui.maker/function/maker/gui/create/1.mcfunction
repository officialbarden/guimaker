function gui.maker:username

scoreboard players operation $generated_id gm.Values = $globalCreationID gm.Values
execute store result storage barden:names Names.GUI_ID int 1 run scoreboard players get $generated_id gm.Values
scoreboard players add $globalCreationID gm.Values 1
data modify storage barden:names Names.GUI_NAME set value ""


function gui.maker:maker/gui/create/2 with storage barden:names Names
function gui.maker:maker/gui/create/tellraw with storage barden:names Names

scoreboard players reset @s gm-createGUIprofile
scoreboard players enable @s gm-createGUIprofile
