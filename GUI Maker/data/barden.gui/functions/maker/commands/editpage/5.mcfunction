$tellraw @s ["",{"text":"PAGE #","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/page/1 {GUI_ID:$(id),PAGE:$(index)}"}},{"score":{"name":"$PAGENUM","objective":"bgui.inv.iteration"},"clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/page/1 {GUI_ID:$(id),PAGE:$(index)}"}}]

scoreboard players add $PAGENUM bgui.inv.iteration 1
data remove storage barden:gui.query list2[0]

execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:maker/commands/editpage/4
