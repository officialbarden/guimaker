execute as @p run function gu:generate
data modify storage gm:storage temp.macro.player set from storage gu:main out
data modify storage gm:storage temp.macro.GUI_ID set from entity @s data.gui.ID
data modify storage gm:storage temp.macro.PAGE set from entity @s data.gui.PAGE
data modify storage gm:storage temp.macro.Slot set from entity @s data.Items_init[0].Slot

execute if data entity @s data.Items_final[0] run function gm:zprivate/caching/toggle_button/replace with storage gm:storage temp.macro
execute if data entity @s data.Items_final[0] run return run function gm:zprivate/item_check/input/added_item/macro with storage gm:storage temp.macro

data modify storage gm:storage temp.cache.toggle_button_list set from entity @s data.Items_init[0].components."minecraft:custom_data".gui.extras.togglebutton_list
function gm:zprivate/caching/toggle_button/uncache with storage gm:storage temp.macro
#function gm:zprivate/caching/toggle_button/replace with storage gm:storage temp.macro
