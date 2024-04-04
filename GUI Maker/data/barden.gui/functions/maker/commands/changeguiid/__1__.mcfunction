# $(changingguiid)
# $(CHANGE_ID)
$data modify storage barden:gui.all GUI[{guiid:$(changingguiid)}].guiid set value "$(CHANGE_ID)"
$tellraw @s [{"text": "[bgui] ","color": "gold"},{"text": "GUI ID was set to","color": "white"},{"text": " $(CHANGE_ID) ","color": "gold"},{"text": "from:","color": "white"},{"text": " $(changingguiid).","color": "gold"}]
