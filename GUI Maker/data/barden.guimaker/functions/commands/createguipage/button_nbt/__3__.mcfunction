scoreboard players remove @s bgui.iteration 1
execute if score @s bgui.iteration matches 1.. run function barden.guimaker:commands/createguipage/button_nbt/__3__
$data remove storage minecraft:barden.gui.list.query Query[{guiid:$(guiid)}]

$tellraw @a ["",{"text":"item: ","color":"gray"},{"text":"$(id)","color":"white"},{"text":"\n"},{"text":"Slot: ","color":"gray"},{"text":"$(Slot)b"}]
