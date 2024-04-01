$data remove storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page
$data modify storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page append from entity @e[type=chest_minecart,sort=nearest,limit=1] Items[{tag:{bguiButton:1b}}]
$execute store success score @s bgui.changeitem.test.result run data modify storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page set from storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page

execute if score @s bgui.changeitem.test.result matches 1 run tag @s add bgui.notkeepchecking
execute if score @s bgui.changeitem.test.result matches 1 run function barden.gui:worker/gui/entity/click/__3__ with entity @s data
