execute as @p unless items entity @s player.cursor * run return fail

execute as @p run function gu:generate
data modify storage gm:storage temp.macro.player set from storage gu:main out
data modify storage gm:storage temp.macro.GUI_ID set from entity @s data.gui.ID
data modify storage gm:storage temp.macro.PAGE set from entity @s data.gui.PAGE
function gm:zprivate/caching/item_holder/cached/main with storage gm:storage temp.macro
execute if items entity @s player.cursor *[custom_data~{gui:{}}] run item replace entity @p player.cursor with air
clear @p *[custom_data~{gui:{item:"holder"}}]
