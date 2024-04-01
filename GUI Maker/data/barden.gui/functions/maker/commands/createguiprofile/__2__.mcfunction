# $(guiassignedid)
# $(username)

$data modify storage barden:gui.all GUI append value {"guiid":$(guiassignedid),"creator":$(username)}
#$data modify storage barden:gui.all GUI[{"guiid":$(guiassignedid)}].page insert -1 from block ~ ~ ~ Items
$data modify storage barden:gui.all GUI[{"guiid":$(guiassignedid)}].guiname set value "GUI #$(guiassignedid) by $(username)"


$tellraw @s ["",{"text":"--------------------------","color":"gray"},{"text":"\n"},{"text":"[bgui]","color":"gold"},{"text":" A New "},{"text":"GUI","color":"red"},{"text":" was created!\n"},{"text":"GUI id","underlined":true},{"text":": GUI "},{"text":"#$(guiassignedid)","color":"gold"},{"text":"\n"},{"text":"GUI name:","underlined":true},{"text":" \"GUI #$(guiassignedid) by $(username)\"","color":"gold"},{"text":"\n\n"},{"text":"SETTINGS:","color":"white"},{"text":"\n\n"},{"text":"\u25b6 Click to add Pages.","clickEvent":{"action":"suggest_command","value":"/function barden.gui:maker/commands/createguipage/raycast/__1__ {\"changingguiid\":$(guiassignedid),\"username\":$(username)}"},"hoverEvent":{"action":"show_text","contents":"Pages are appended one after another, New pages will appear at very last!"}},{"text":"\n"},{"text":"\u25b6 change Volatile Item.","clickEvent":{"action":"suggest_command","value":"/function barden.guimaker:commands/setvolatileitem/__1__ {\"VolatileItem\":\"minecraft:\",\"VolatileItemCustomNbt\":{bgui:1b},\"Guiid\":$(guiassignedid)}"},"hoverEvent":{"action":"show_text","contents":"Volatile Item is the item which summons the GUI Minecart when that item is held in mainhand* To change this. Click on this text and fill the VolatileItem argument and change the VolatileItemCustomNBT argument appropriately. Leave empty or {} for vanilla item as Volatile Item."}},{"text":"\n"},{"text":"--------------------------","color":"gray"}]

scoreboard players reset @s bgui.guiprofile.id
data remove storage barden:gui.players.names Names[0]
