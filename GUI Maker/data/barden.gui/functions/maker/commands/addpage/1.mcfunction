scoreboard players reset @s bgui-addPage
scoreboard players enable @s bgui-addPage

tellraw @s [{"text":"--------------------------","color":"gray"}]
tellraw @s [{"text":"[bgui]","color":"gold"},{"text": " Look at block having PAGE Contents and click on one of the following GUI's in the world to add the page to (NOTE, the page set will be at the very last!):","color": "white"}]
tellraw @s [{"text": "\n\n"}]

data remove storage barden:gui.query list
data modify storage barden:gui.query list append from storage barden:gui.all GUI[]
execute store result score @s bgui.inv.iteration run data get storage barden:gui.query list

execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:maker/commands/addpage/2 with storage barden:gui.query list[0]
tellraw @s [{"text":"--------------------------","color":"gray"}]
