$data modify storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}].page append from entity @s Items[{tag:{bguiButton:1b}}]
$data modify storage barden:gui.page.init PAGEFINAL[] set from storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}]

tag @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] add bgui.check
