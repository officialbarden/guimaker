$execute as @p at @s run function barden.gui:worker/gui/entity/click/result/other/clearitem with storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page[0]


$execute as @p[tag=!bgui.function.ran] at @s run function barden.gui:worker/gui/entity/click/result/extra_functionalities/bgui_function with storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page[0].tag
$execute as @e[type=chest_minecart,sort=nearest,limit=1,tag=bgui.gui-true,tag=!bgui.page.changed] run function barden.gui:worker/gui/entity/click/result/extra_functionalities/bgui_changepage with storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page[0].tag.bguiChangeToPageData


function barden.gui:worker/gui/entity/click/result/extra_functionalities/priv/suc/1
