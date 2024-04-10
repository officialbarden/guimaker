# $(bguiId)
# $(bguiIdPageIndex)
tag @s add bgui.page.changed
data remove entity @s Items
$data modify entity @s Items append from storage barden:gui.all GUI[{guiid:$(bguiId)}].pages[$(bguiIdPageIndex)][]
tag @e[type=marker,limit=1,sort=nearest,tag=bgui.marker.itemchangecheck] remove bgui.notkeepchecking
tag @e[type=marker,limit=1,sort=nearest,tag=bgui.marker.itemchangecheck] add bgui.check

function barden.gui:worker/gui/entity/click/result/extra_functionalities/priv/pc/1 with entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data
