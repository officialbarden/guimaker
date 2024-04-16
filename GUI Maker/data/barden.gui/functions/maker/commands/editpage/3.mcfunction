# $(GUI_ID)

scoreboard players set $PAGENUM bgui.inv.iteration 0
tellraw @s [{"text":"--------------------------","color":"gray"}]
tellraw @s [{"text":"[bgui]","color":"gold"},{"text": " Select PAGE to EDIT:","color": "white"}]
tellraw @s [{"text": "\n\n"}]

data remove storage barden:gui.query list2
$data modify storage barden:gui.query list2 append from storage barden:gui.all GUI[{guiid:$(GUI_ID)}].pages[]
execute store result score @s bgui.inv.iteration if data storage barden:gui.query list2[]

$data modify storage barden:gui.query PAGENUM.id set value "$(GUI_ID)"

$execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:maker/commands/editpage/4 {GUI_ID:$(GUI_ID)}
tellraw @s [{"text":"--------------------------","color":"gray"}]
