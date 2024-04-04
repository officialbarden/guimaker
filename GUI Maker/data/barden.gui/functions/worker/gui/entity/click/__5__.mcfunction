scoreboard players remove @s bgui.inv.iteration 1
execute if score @s bgui.inv.iteration matches 0 run function barden.gui:worker/gui/entity/click/result/_success with entity @s data



$data remove storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page[$(pageObject)]
$data remove storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page[$(pageObject)]

execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:worker/gui/entity/click/__4__ with entity @s data
