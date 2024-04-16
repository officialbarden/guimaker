# $(GUI_ID)

scoreboard players remove @s bgui.inv.iteration 1
execute store result storage barden:gui.query PAGENUM.index int 1 run scoreboard players get $PAGENUM bgui.inv.iteration
function barden.gui:maker/commands/editpage/5 with storage barden:gui.query PAGENUM
