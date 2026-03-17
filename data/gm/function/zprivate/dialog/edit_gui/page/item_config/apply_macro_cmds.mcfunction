$data remove storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}].PAGE_CONTENTS[{Slot:$(Slot)b}].components."minecraft:custom_data".gui.execute.commands
$data modify storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}].PAGE_CONTENTS[{Slot:$(Slot)b}].components."minecraft:custom_data".gui.execute.commands set value '$(commands)'
