# $(Guiid)
# $(Username)
scoreboard players reset @s bgui.iteration
$execute store result score @s bgui.iteration run data get storage minecraft:barden.gui.all GUI[{guiid:$(Guiid)}].pages[]


tellraw @s ["",{"text":"------------------------------------------","color":"gray"},{"text":"\n"},{"text":"[bgui]","color":"gold"},{"text":" LIST OF CONFIG-ABLE ITEMS in PAGE:"}]

execute if score @s bgui.iteration matches 0 run function barden.guimaker:commands/createguipage/button_nbt/_listfail
$execute if score @s bgui.iteration matches 1.. run function barden.guimaker:commands/createguipage/button_nbt/__2__ {username:$(Username),guiid:$(Guiid)}
