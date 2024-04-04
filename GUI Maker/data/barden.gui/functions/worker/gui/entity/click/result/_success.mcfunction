#$execute as @p at @s run function barden.gui:worker/gui/entity/click/result/other/clearitem with storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page[0]


$execute as @p[limit=1] at @s run function barden.gui:worker/gui/entity/click/result/extra_functionalities/bgui_function with storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page[0].tag
$execute as @e[type=chest_minecart,sort=nearest,limit=1] run function barden.gui:worker/gui/entity/click/result/extra_functionalities/bgui_changetopageindex with storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page[0].tag
