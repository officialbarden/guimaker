# $(GUI_ID)

$execute store result score $PageCount gm.Values run data get storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES
scoreboard players add $PageCount gm.Values 1
execute store result storage gm:storage temp.PAGES.PAGE int 1 run scoreboard players get $PageCount gm.Values
$data modify storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES append from storage gm:storage temp.PAGES
$data modify storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[-1].PAGE_CONTENTS set from block ~ ~-1 ~ Items
$execute unless data storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[-1].PAGE_CONTENTS[].components."minecraft:custom_data".gui run data modify storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[-1].PAGE_CONTENTS[].components."minecraft:custom_data".gui set value {execute:{function:"",item_modifier:"",command:""}}

$execute unless data storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[-1].PAGE_CONTENTS[].components."minecraft:custom_data".gui.item run data modify storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[-1].PAGE_CONTENTS[].components."minecraft:custom_data".gui.item set value "simple_button"


$data modify storage gm:storage temp.macro.GUI_ID set value $(GUI_ID)
execute store result storage gm:storage temp.macro.PAGE int 1 run scoreboard players get $PageCount gm.Values
function gm:zprivate/gui_create/page/initialize_values with storage gm:storage temp.macro

$tellraw @s ["Page appended to GUI_ID=$(GUI_ID)"]
