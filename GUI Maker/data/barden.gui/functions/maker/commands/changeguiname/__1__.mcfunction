# $(changingguiid)
# $(SET_NAME)
$data modify storage barden:gui.all GUI[{guiid:$(changingguiid)}].guiname set value "$(SET_NAME)"
$tellraw @s [{"text": "[bgui] ","color": "gold"},{"text": "GUI Name was set","color": "white"},{"text": " $(SET_NAME) ","color": "gold"},{"text": "for GUI-ID:","color": "white"},{"text": " $(changingguiid).","color": "gold"}]
