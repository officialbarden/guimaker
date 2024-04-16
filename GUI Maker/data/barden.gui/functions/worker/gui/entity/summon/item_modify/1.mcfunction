data remove storage barden:gui.query Items
data modify storage barden:gui.query Items append from entity @s Items[]
execute store result score @s bgui.inv.iteration if data storage barden:gui.query Items[]

execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:worker/gui/entity/summon/item_modify/2 with storage barden:gui.query Items[0]
