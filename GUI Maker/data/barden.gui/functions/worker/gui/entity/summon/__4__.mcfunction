data remove entity @s Items
$function barden.gui:worker/gui/entity/summon/datamerge/__name__ with storage barden:gui.all GUI[{guiid:$(guiid)}]
data merge entity @s {CustomNameVisible:0b}
$data modify entity @s Items append from storage barden:gui.all GUI[{guiid:$(guiid)}].pages[0][]

function barden.gui:worker/gui/entity/summon/page_check_setup/__1__ with entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data
