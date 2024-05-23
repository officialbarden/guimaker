data remove storage barden:temp API_DISPLAY
data modify storage barden:temp API_DISPLAY set from storage barden:temp API_SLOTS[0]
data remove storage barden:temp API_SLOTS[0]

$data modify storage barden:temp API_DISPLAY.id set value "$(id)"
$data modify storage barden:temp API_DISPLAY.Count set value $(Count)b
$data modify storage barden:temp API_DISPLAY.tag set value $(tag)

data remove storage barden:temp API_FETCH[0]

data modify storage barden:temp API_SLOTS insert -1 from storage barden:temp API_DISPLAY

scoreboard players remove $api_fetch_items gm.Extras 1
execute if score $api_fetch_items gm.Extras matches 1.. run function gui.maker:worker/gui/block/api/iterate/storage/2 with storage barden:temp API_FETCH[0]
