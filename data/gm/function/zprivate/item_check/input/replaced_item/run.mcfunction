#say replaced!
#tellraw @a ["Item Replaced:", {"nbt":"data.Items_init[0]","entity":"@s"}]
#tellraw @a ["New Item:", {"nbt":"data.Items_final[0]","entity":"@s"}]

tag @s add _gui.temp

execute if data entity @s data.Items_init[{"components":{"minecraft:custom_data":{"gui":{"item":"holder"}}}}] run function gm:zprivate/caching/toggle_button/main
execute if data entity @s data.Items_init[{"components":{"minecraft:custom_data":{gui_togglebuttoncached:true}}}] run function gm:zprivate/caching/toggle_button/main


execute if data entity @s data.Items_init[{"components":{"minecraft:custom_data":{"gui":{"item":"holder"}}}}] run function gm:zprivate/caching/item_holder/main
execute if data entity @s data.Items_init[{"components":{"minecraft:custom_data":{gui_itemholdercached:true}}}] run function gm:zprivate/caching/item_holder/main

execute unless data entity @s data.Items_final[0] run function gm:zprivate/caching/item_holder/cached/uncache
setblock 0 300 0 air

execute as @p run function gu:generate
data modify storage gm:storage temp.macro.player set from storage gu:main out
function gm:zprivate/execution/run_function/main with storage gm:storage temp.macro
function gm:zprivate/execution/playsound/main with storage gm:storage temp.macro
function gm:zprivate/execution/change_menu/main with storage gm:storage temp.macro
clear @a *[custom_data~{gui:{}}]

tag @s remove _gui.temp
