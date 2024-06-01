function gui.maker:username
data modify storage barden:names Names.GUI_ID set from entity @e[type=marker,tag=gm.clicked,sort=nearest,limit=1] data.PAGE_INFO.GUI_ID
data modify storage barden:names Names.PAGE set from entity @e[type=marker,tag=gm.clicked,sort=nearest,limit=1] data.PAGE_INFO.PAGE
$data modify storage barden:names Names.id set value "$(id)"
$data modify storage barden:names Names.Slot set value $(Slot)

function gui.maker:maker/gui/edit_page/tellraw/3 with storage barden:names Names
