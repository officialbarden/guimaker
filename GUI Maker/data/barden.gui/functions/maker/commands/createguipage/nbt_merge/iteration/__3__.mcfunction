scoreboard players remove @s bgui.inv.iteration 1
$tellraw @s [{"text":"\n\n"},{"text": "[bgui]","color": "gold"},{"text": " item:","color": "white"},{"text": "\nid:\"$(id)\"","color":"white"},{"text": "\nslot:\"$(Slot)\"","color":"white"}]
function barden.gui:maker/commands/createguipage/nbt_merge/iteration/__4__ with storage barden:list.query QUERY[0].page[0].tag

data remove storage barden:list.query QUERY[0].page[0]
execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:maker/commands/createguipage/nbt_merge/iteration/__3__ with storage barden:list.query QUERY[0].page[0]
