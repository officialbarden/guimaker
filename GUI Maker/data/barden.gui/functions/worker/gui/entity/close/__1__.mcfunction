data remove entity @e[type=chest_minecart,sort=nearest,limit=1,tag=bgui.gui-true] Items
kill @e[type=chest_minecart,sort=nearest,limit=1,tag=bgui.gui-true]
kill @e[type=interaction,sort=nearest,limit=1,tag=bgui.guiprotection]
function barden.gui:worker/gui/entity/close/__2__ with entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data
tag @p remove bgui.vitemholder
