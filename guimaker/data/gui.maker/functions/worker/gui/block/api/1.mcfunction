data remove storage barden:temp API
data remove storage barden:temp API_FETCH
data remove storage barden:temp API_SLOTS
data remove storage barden:temp API_DISPLAY

data modify storage barden:temp API set from block ~ ~ ~ Items
execute store result score $totalitems gm.Extras run data get storage barden:temp API
scoreboard players reset $apislots gm.Extras
function gui.maker:worker/gui/block/api/iterate/get_count with storage barden:temp API[0]

$function gui.maker:worker/gui/block/api/iterate/storage/1 with storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)].API


$execute store result score $api_permissions gm.Extras run data get storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGE[$(PAGE)].page_settings.api_settings.convert_to_button
execute if score $api_permissions gm.Extras matches 1 run data modify storage barden:temp API_SLOTS[].tag."guim.Button" set value 1b


data modify block ~ ~ ~ Items insert -1 from storage barden:temp API_SLOTS[]
data remove block ~ ~ ~ Items[{tag:{guim.PlaceholderAPI:1b}}]
