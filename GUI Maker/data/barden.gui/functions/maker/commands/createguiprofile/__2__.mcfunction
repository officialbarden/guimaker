# $(guiassignedid)
# $(username)

$data modify storage barden:gui.all GUI append value {"guiid":$(guiassignedid),"creator":$(username)}
#$data modify storage barden:gui.all GUI[{"guiid":$(guiassignedid)}].page insert -1 from block ~ ~ ~ Items
$data modify storage barden:gui.all GUI[{"guiid":$(guiassignedid)}].guiname set value "GUI #$(guiassignedid) by $(username)"


$tellraw @s ["",{"text":"--------------------------","color":"gray"},{"text":"\n"},{"text":"[bgui]","color":"gold"},{"text":" A New "},{"text":"GUI","color":"red"},{"text":" was created!\n"},{"text":"GUI id","underlined":true},{"text":": GUI "},{"text":"#$(guiassignedid)","color":"gold"},{"text":"\n"},{"text":"GUI name:","underlined":true},{"text":" \"GUI #$(guiassignedid) by $(username)\"","color":"gold"}]
tellraw @s [{"text":"\n\n"},{"text":"SETTINGS:","color":"white"}]
$tellraw @s [{"text":"\u25b6 Click to add Pages.","clickEvent":{"action":"suggest_command","value":"/function barden.gui:maker/commands/createguipage/raycast/__1__ {\"changingguiid\":$(guiassignedid),\"username\":$(username),\"pagenumber\":-1}"},"hoverEvent":{"action":"show_text","contents":"Pages are appended one after another, New pages will appear at very last!"}},{"text":"\n"},{"text":"\n"},{"text":"--------------------------","color":"gray"}]

scoreboard players reset @s bgui.guiprofile.id
data remove storage barden:gui.players.names Names[0]
