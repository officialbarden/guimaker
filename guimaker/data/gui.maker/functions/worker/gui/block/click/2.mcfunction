$data remove entity @s data.PAGE.INIT[{Slot:$(Slot)b}]
$data remove entity @s data.PAGE.FINAL[{Slot:$(Slot)b}]

scoreboard players remove $Items gm.Extras 1
execute if score $Items gm.Extras matches 1.. run function gui.maker:worker/gui/block/click/2 with entity @s data.PAGE.FINAL[0]
