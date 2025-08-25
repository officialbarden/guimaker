$data modify storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[-1].PAGE_CONTENTS[].components."minecraft:custom_data".gui.execute.change_menu set value {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}
$data modify storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[-1].NAME set value "GUI #$(GUI_ID), PAGE #$(PAGE)"
