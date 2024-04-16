scoreboard players reset @s bgui-getListofAllGuis
scoreboard players enable @s bgui-getListofAllGuis

tellraw @s [{"text":"--------------------------","color":"gray"}]
tellraw @s [{"text":"[bgui]","color":"gold"},{"text": " list of ALL GUIs in the world:","color": "white"}]
tellraw @s [{"text": "\n\n"}]

data remove storage barden:gui.query list
data modify storage barden:gui.query list append from storage barden:gui.all GUI[]
execute store result score @s bgui.inv.iteration run data get storage barden:gui.query list

execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:maker/commands/getallguilist/2 with storage barden:gui.query list[0]
tellraw @s [{"text":"--------------------------","color":"gray"}]
