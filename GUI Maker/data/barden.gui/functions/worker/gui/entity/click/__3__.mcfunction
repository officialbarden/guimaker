say click detected
scoreboard players reset @s bgui.changeitem.test.result
$data modify storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}].page append from entity @e[type=chest_minecart,sort=nearest,limit=1] Items[{tag:{bguiButton:1b}}]
tag @s remove bgui.notkeepchecking