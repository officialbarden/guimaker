# $(ID)
# $(PAGE_INDEX)

tag @s add bgui.page.changed

data remove entity @s Items
$data modify entity @s Items append from storage barden:gui.all GUI[{guiid:$(ID)}].pages[$(PAGE_INDEX)][]
function barden.gui:worker/gui/entity/summon/item_modify/1
#say page INDEX: $(PAGE_INDEX)

tag @e[type=marker,limit=1,sort=nearest,tag=bgui.marker.itemchangecheck] add bgui.check
tag @e[type=marker,limit=1,sort=nearest,tag=bgui.marker.itemchangecheck] remove bgui.notkeepchecking

function barden.gui:worker/gui/entity/click/result/extra_functionalities/priv/pc/1 with entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data