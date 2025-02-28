data remove storage gm:storage temp.cache.list
$data modify storage gm:storage temp.cache.list set from storage gm:storage CACHE[{player:"$(player)"}].CACHE.TOGGLE_BUTTON[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].TOGGLE
data modify storage gm:storage temp.macro.Slot set from storage gm:storage temp.cache.list[0].Slot
execute if data storage gm:storage temp.cache.list[0] run function gm:zprivate/prerequisites/toggle_button/replace with storage gm:storage temp.macro
