$data modify block ~ ~ ~ Items set from storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}].PAGE_CONTENTS
$data modify entity @n[type=chest_minecart,tag=_gui.chestminecart,distance=..1] Items set from storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}].PAGE_CONTENTS
$execute as $(player) run function gm:zprivate/prerequisites/main

