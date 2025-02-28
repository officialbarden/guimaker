#> Move the Item to the Back of the List
data modify storage gm:storage temp.cache.toggle_button_list append from storage gm:storage temp.cache.toggle_button_list[0]
data remove storage gm:storage temp.cache.toggle_button_list[0]

#> Set the new Cached Item to be the first index of the list.
$data remove storage gm:storage CACHE[{player:"$(player)"}].CACHE.TOGGLE_BUTTON[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].TOGGLE[{Slot:$(Slot)b}]
$data modify storage gm:storage CACHE[{player:"$(player)"}].CACHE.TOGGLE_BUTTON[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].TOGGLE append from storage gm:storage temp.cache.toggle_button_list[0]
$data modify storage gm:storage CACHE[{player:"$(player)"}].CACHE.TOGGLE_BUTTON[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].TOGGLE[-1].Slot set value $(Slot)b
$data modify storage gm:storage CACHE[{player:"$(player)"}].CACHE.TOGGLE_BUTTON[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].TOGGLE[-1].components."minecraft:custom_data".gui.extras.togglebutton_list set from storage gm:storage temp.cache.toggle_button_list
$data modify storage gm:storage CACHE[{player:"$(player)"}].CACHE.TOGGLE_BUTTON[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].TOGGLE[-1].components."minecraft:custom_data".gui_togglebuttoncached set value true
