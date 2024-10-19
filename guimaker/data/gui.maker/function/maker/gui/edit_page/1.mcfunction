function gui.maker:username
tag @s add gm.editing_page

function gui.maker:maker/gui/edit_page/list/1 with storage barden:names Names
#tag @s add gm.editing_page
scoreboard players reset @s gm-editPage
scoreboard players enable @s gm-editPage
