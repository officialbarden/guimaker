$data remove entity @s data.PAGE.INIT[{Slot:$(Slot)b}]
data remove entity @s data.PAGE.FINAL[0]
scoreboard players remove $total_items_post gm.Values 1
execute if score $total_items_post gm.Values matches 1.. run function gui.maker:worker/gui/click/3 with entity @s data.PAGE.FINAL[0]