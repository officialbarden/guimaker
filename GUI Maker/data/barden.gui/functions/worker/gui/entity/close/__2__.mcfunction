$data remove storage barden:gui.page.init PAGEINIT[{uuid:$(minecartuuid)}]
$data remove storage barden:gui.page.init PAGEFINAL[{uuid:$(minecartuuid)}]
$data remove storage barden:gui.entity.all.gui.link all_connections[{uuid:$(minecartuuid)}]
$data remove storage barden:gui.entity.id.conforming CONNECTION[{UUID:$(minecartuuid)}]


kill @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck]