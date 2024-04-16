scoreboard players remove @e[type=chest_minecart,sort=nearest,limit=1,tag=bgui.gui-true] bgui.inv.iteration 1

data remove entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data.itemModify
data modify entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data.itemModify.Slot set from storage barden:gui.query Items[0].Slot
data modify entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data.itemModify.Modifier set from storage barden:gui.query Items[0].tag.bguiItemModifier
execute as @p at @s run function barden.gui:worker/gui/entity/summon/item_modify/merge with entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data.itemModify


data remove storage barden:gui.query Items[0]
execute store result score @s bgui.inv.iteration if data storage barden:gui.query Items[]

execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:worker/gui/entity/summon/item_modify/2 with storage barden:gui.query Items[0]
