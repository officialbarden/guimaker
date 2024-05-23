$execute store result score $owned_guis gm.Extras if data storage barden:gui.all GUI[{OWNER:$(username)}]
data remove storage barden:temp Item.1
$data modify storage barden:temp Item.1 append from storage barden:gui.all GUI[{OWNER:$(username)}]
function gui.maker:maker/api/convert_data_to_item/3 with storage barden:temp Item.1[0]
