$execute unless data storage gm:storage CACHE[{player:"$(player)"}] run data modify storage gm:storage CACHE append value {player:"$(player)"}
$data modify storage gm:storage CACHE[{player:"$(player)"}].CACHE.ITEM_HOLDER[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].CONTENTS append from entity @s data.Items_final[0]
$data modify storage gm:storage CACHE[{player:"$(player)"}].CACHE.ITEM_HOLDER[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].CONTENTS[].components."minecraft:custom_data".gui_itemholdercached set value true
$data modify storage gm:storage CACHE[{player:"$(player)"}].CACHE.ITEM_HOLDER[{GUI_ID:$(GUI_ID),PAGE:$(PAGE)}].PLACEHOLDER append from entity @s data.Items_init[0]
