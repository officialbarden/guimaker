$execute if data storage barden:temp API[{Slot:$(Slot)b,id:"minecraft:pink_stained_glass_pane",tag:{guim.PlaceholderAPI:1b}}] run scoreboard players add $apislots gm.Extras 1
$execute if data storage barden:temp API[{Slot:$(Slot)b,id:"minecraft:pink_stained_glass_pane",tag:{guim.PlaceholderAPI:1b}}] run data modify storage barden:temp API_SLOTS append from storage barden:temp API[{Slot:$(Slot)b,id:"minecraft:pink_stained_glass_pane",tag:{guim.PlaceholderAPI:1b}}]
data remove storage barden:temp API[0]
scoreboard players remove $totalitems gm.Extras 1
execute if score $totalitems gm.Extras matches 1.. run function gui.maker:worker/gui/block/api/iterate/get_count with storage barden:temp API[0]
