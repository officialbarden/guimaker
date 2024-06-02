$execute as @s[tag=gm.itemcheck.temp,tag=gm.gui.cm] run data modify entity @e[type=chest_minecart,sort=nearest,limit=1,tag=gm.temp,dx=0] CustomName set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].page_name
$execute as @s[tag=gm.gui.block.temp] run data modify block ~ ~ ~ CustomName set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].page_name

$execute as @s[tag=gm.gui.cm] at @s run data modify entity @e[type=chest_minecart,sort=nearest,limit=1,dx=0] CustomName set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].page_name
$execute as @s[tag=gm.gui.block] at @s run data modify block ~ ~ ~ CustomName set from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].page_name
