execute as @p run function gu:generate
data modify storage gm:storage temp.macro.player set from storage gu:main out
data modify storage gm:storage temp.macro.GUI_ID set from entity @s data.gui.ID
data modify storage gm:storage temp.macro.PAGE set from entity @s data.gui.PAGE
data modify storage gm:storage temp.macro.Slot set from entity @s data.Items_init[0].Slot
function gm:zprivate/caching/item_holder/cached/uncache2 with storage gm:storage temp.macro
function gm:zprivate/caching/item_holder/cached/component_remove