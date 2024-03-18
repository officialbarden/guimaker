tag @s remove bgui.gui-false
tag @s add bgui.gui-true

say hi

execute store result storage barden:gui.entity.id.conforming CONNECTION[].GUI_ID int 1 run scoreboard players get @s bgui.entity.id.conforming
function barden.guiworker:minecart/summon/__3__ with storage barden:gui.entity.id.conforming CONNECTION[0]