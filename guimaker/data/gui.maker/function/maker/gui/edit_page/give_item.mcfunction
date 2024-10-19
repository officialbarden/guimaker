$give @s[tag=!gm.editing_page] phantom_spawn_egg[item_name='{"text":"gui_id=$(GUI_ID),page=$(page_number)"}',entity_data={id:"minecraft:marker",Tags:["gm.gui.container.place","gm.gui.block"],data:{GUI_ID:$(GUI_ID),page_number:$(page_number)}}] 1


$execute as @s[tag=gm.editing_page] at @s run function gui.maker:maker/gui/edit_page/tellraw/2 {GUI_ID:$(GUI_ID),PAGE:$(page_number)}
execute as @s[tag=gm.editing_page] run tellraw @s {"text":"click on the button you want to edit config for!","bold":true,"color":"aqua"}
execute as @s[tag=gm.editing_page] run playsound minecraft:block.note_block.pling master @s ~ ~ ~ 1 1 1

# function gui.maker:maker/gui/edit_page/tellraw/1 with entity @e[type=marker,tag=gm.clicked,sort=nearest,limit=1] data
