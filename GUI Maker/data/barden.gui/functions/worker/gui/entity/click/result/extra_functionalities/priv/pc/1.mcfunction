
$data remove storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}]
$data remove storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}]

$execute as @e[type=chest_minecart,sort=nearest,limit=1,tag=bgui.page.changed] run data modify storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page append from entity @s Items[{tag:{bguiButton:1b}}]
$data modify storage barden:gui.page.init PAGEFINAL[] set from storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}]
