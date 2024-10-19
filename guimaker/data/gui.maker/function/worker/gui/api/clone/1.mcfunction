data remove storage barden:temp API.Slots
data remove storage barden:temp API.Source
data remove storage barden:temp API.Result
execute if entity @s[tag=gm.gui.ui] run data modify storage barden:temp API.Slots append from entity @s Items[{components:{"minecraft:custom_data":{gm.API:true}}}]
execute if entity @s[tag=gm.gui.block] run data modify storage barden:temp API.Slots append from block ~ ~ ~ Items[{components:{"minecraft:custom_data":{gm.API:true}}}]
execute store result score $API.available.slots gm.Values run data get storage barden:temp API.Slots
execute if score $API.available.slots gm.Values matches 0 run function gui.maker:worker/gui/api/set/3
execute if score $API.available.slots gm.Values matches 1.. run function gui.maker:worker/gui/api/set/1 with storage barden:temp API.Slots[0].components."minecraft:custom_data"."gm.API.VALUES"