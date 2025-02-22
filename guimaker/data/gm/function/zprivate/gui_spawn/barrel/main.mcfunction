# $(GUI_ID)
# $(PAGE)

setblock ~ ~ ~ barrel
$execute align xyz positioned ~0.5 ~ ~0.5 run summon marker ~ ~ ~ {Tags:["_gui.marker","_gui.barrel","_gui.temp"],data:{gui:{ID:$(GUI_ID),PAGE:$(PAGE)}}}
$data modify block ~ ~ ~ Items set from storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}].PAGE_CONTENTS
function gm:zprivate/prerequisites/main
execute as @e[tag=_gui.temp,type=marker] at @s run data modify entity @s data.Items set from block ~ ~ ~ Items
tag @e remove _gui.temp