data modify storage barden:temp API.Result append from storage barden:temp API.Slots[0]
data modify storage barden:temp API.Result[-1].id set from storage barden:temp API.Source[0].id
data remove storage barden:temp API.Result[-1].components
data modify storage barden:temp API.Result[-1].components set from storage barden:temp API.Source[0].components
data modify storage barden:temp API.Result[-1].count set from storage barden:temp API.Source[0].count
data modify storage barden:temp API.Result[-1].components."minecraft:custom_data"."gm.APISLOT" set value 1
data modify storage barden:temp API.Result[-1].components."minecraft:custom_data"."gm.Button" set value 1

data remove storage barden:temp API.Slots[0]
data remove storage barden:temp API.Source[0]
scoreboard players remove $API.available.slots gm.Values 1
execute if score $API.available.slots gm.Values matches 0 run function gui.maker:worker/gui/api/set/3
execute if score $API.available.slots gm.Values matches 1.. run function gui.maker:worker/gui/api/set/2 with storage barden:temp API.Slots[0].components."minecraft:custom_data"."gm.API.VALUES"
