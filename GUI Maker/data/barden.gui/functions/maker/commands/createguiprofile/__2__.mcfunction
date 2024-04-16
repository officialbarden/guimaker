# $(guiassignedid)
# $(username)

$data modify storage barden:gui.all GUI append value {"guiid":$(guiassignedid),"creator":$(username)}
#$data modify storage barden:gui.all GUI[{"guiid":$(guiassignedid)}].page insert -1 from block ~ ~ ~ Items
$data modify storage barden:gui.all GUI[{"guiid":$(guiassignedid)}].guiname set value "GUI #$(guiassignedid) by $(username)"
$data modify storage barden:gui.all GUI[{"guiid":$(guiassignedid)}].guisettings set value {accept_item_input:0b,is_dynamic:0b,is_animated:0b}

$tellraw @s ["",{"text":"--------------------------","color":"gray"},{"text":"\n"},{"text":"[bgui]","color":"gold"},{"text":" A New "},{"text":"GUI","color":"red"},{"text":" was created!\n"},{"text":"GUI id","underlined":true},{"text":": GUI "},{"text":"#$(guiassignedid)","color":"gold"},{"text":"\n"},{"text":"GUI name:","underlined":true},{"text":" \"GUI #$(guiassignedid) by $(username)\"","color":"gold"}]
tellraw @s [{"text":"\n\n"},{"text":"SETTINGS:","color":"white"}]
$tellraw @s [{"text":"\u25b6 Click to add Pages.","clickEvent":{"action":"suggest_command","value":"/function barden.gui:maker/commands/createguipage/raycast/__1__ {\"changingguiid\":$(guiassignedid),\"username\":$(username),\"pagenumber\":-1}"},"hoverEvent":{"action":"show_text","contents":"Pages are appended one after another, New pages will appear at very last!"}}]
$tellraw @s {"text":"\u25b6 Change Name.","clickEvent":{"action":"suggest_command","value":"/function barden.gui:maker/commands/changeguiname/__1__ {SET_NAME:\"\",changingguiid:$(guiassignedid)}"}}
$tellraw @s {"text":"\u25b6 Change ID.","clickEvent":{"action":"suggest_command","value":"/function barden.gui:maker/commands/changeguiid/__1__ {CHANGE_ID:\"$(guiassignedid)\",changingguiid:$(guiassignedid)}"}}
$tellraw @s {"text":"\u25b6 Change GUI Generic Settings.","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/changeguisettings/__init__ {gui_id:$(guiassignedid)}"},"hoverEvent":{"action":"show_text","contents":"Click to change Generic Settings."}}
tellraw @s [{"text":"--------------------------","color":"gray"}]

scoreboard players reset @s bgui.guiprofile.id
data remove storage barden:gui.players.names Names[0]
