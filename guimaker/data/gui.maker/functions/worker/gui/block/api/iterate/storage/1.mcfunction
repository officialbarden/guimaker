data remove storage barden:temp API_FETCH
$data modify storage barden:temp API_FETCH append from storage $(storage) $(path)
data modify storage barden:temp API_FETCH[].tag."guim_api" set value 1
execute store result score $api_fetch_items gm.Extras run data get storage barden:temp API_FETCH
function gui.maker:worker/gui/block/api/iterate/storage/2 with storage barden:temp API_FETCH[0]
