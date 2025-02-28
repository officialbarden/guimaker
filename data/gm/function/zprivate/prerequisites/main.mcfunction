function gu:generate
data modify storage gm:storage temp.macro.player set from storage gu:main out
data modify storage gm:storage temp.macro.GUI_ID set from entity @n[type=marker,tag=_gui.marker,distance=..1] data.gui.ID
data modify storage gm:storage temp.macro.PAGE set from entity @n[type=marker,tag=_gui.marker,distance=..1] data.gui.PAGE

scoreboard players operation #temp gm.ID = @s gm.ID

function gm:zprivate/prerequisites/data_driven/main
function gm:zprivate/prerequisites/item_holder/main with storage gm:storage temp.macro
function gm:zprivate/prerequisites/toggle_button/main with storage gm:storage temp.macro
function gm:zprivate/prerequisites/item_modifier/main
