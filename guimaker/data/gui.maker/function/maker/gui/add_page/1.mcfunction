execute unless block ~ ~-1 ~ #gui.maker:container run function gui.maker:maker/gui/add_page/not_standing_on_container
execute if block ~ ~-1 ~ #gui.maker:container run function gui.maker:maker/gui/add_page/2

scoreboard players reset @s gm-addPage
scoreboard players enable @s gm-addPage
