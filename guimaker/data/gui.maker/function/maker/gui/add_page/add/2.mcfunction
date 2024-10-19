$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES append value {page_number:$(page_number),page_name:'[{"text":"$(username)\'s page $(page_number)"}]'}
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout set from block ~ ~-1 ~ Items
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].components."minecraft:custom_data"."gm.Button" set value 1
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].components."minecraft:custom_data".BUTTON_INFO."function_file" set value "gui.maker:extra/empty"
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].components."minecraft:custom_data".BUTTON_INFO."item_modifier" set value "empty"
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].components."minecraft:custom_data".BUTTON_INFO.CHANGE_PAGE.GUI_ID set value "$(GUI_ID)"
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].components."minecraft:custom_data".BUTTON_INFO.CHANGE_PAGE.PAGE set value "$(page_number)"


playsound minecraft:entity.player.levelup master @s ~ ~ ~ 1 1 1
