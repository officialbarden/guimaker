$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES append value {"page_name":"$(username)'s page $(page_number)",page_number:$(page_number)}
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout set from block ~ ~-1 ~ Items
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].tag."gm.Button" set value 1
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].tag.BUTTON_INFO."function_file" set value ""
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].tag.BUTTON_INFO."item_modifier" set value ""
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].tag.BUTTON_INFO.CHANGE_PAGE.GUI_ID set value "$(GUI_ID)"
$data modify storage barden:gui.all GUI[{GUI_ID:$(GUI_ID),username:$(username)}].PAGES[{page_number:$(page_number)}].layout[].tag.BUTTON_INFO.CHANGE_PAGE.PAGE set value "$(page_number)"


playsound minecraft:entity.player.levelup master @s ~ ~ ~ 1 1 1