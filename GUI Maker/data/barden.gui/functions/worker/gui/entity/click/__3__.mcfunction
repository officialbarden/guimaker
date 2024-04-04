#say click detected

scoreboard players reset @s bgui.changeitem.test.result
$data remove storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page
$data modify storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page append from entity @e[type=chest_minecart,sort=nearest,limit=1] Items[{tag:{bguiButton:1b}}]
$execute store result score @s bgui.inv.iteration run data get storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page


execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:worker/gui/entity/click/__4__ with entity @s data
execute if score @s bgui.inv.iteration matches 0 run function barden.gui:worker/gui/entity/click/result/_success with entity @s data

#tag @s remove bgui.notkeepchecking                      # this command makes this file loop.

#$function barden.gui:worker/gui/entity/click/result/_success with storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page[0]


