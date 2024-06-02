data remove storage barden:temp list
$data modify storage barden:temp list append from storage barden:gui.all GUI[{GUI_ID:$(GUI_ID)}].PAGES[{page_number:$(PAGE)}].layout[]
execute store result score $ITEMS gm.Values run data get storage barden:temp list

execute if score $ITEMS gm.Values matches 1.. run function gui.maker:worker/gui/item_modify/2 with storage barden:temp list[0]
