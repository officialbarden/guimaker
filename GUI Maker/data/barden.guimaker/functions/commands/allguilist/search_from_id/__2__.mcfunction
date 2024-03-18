$tellraw @s ["",{"text":"\n\n\n"},{"text":"[bgui] ","color":"gold"},{"text":"Found GUI #$(guiid): \"$(guiname)\"\n\n\u25b6 ","color":"white"},{"text":"Configure Name?","color":"white","hoverEvent":{"action":"show_text","contents":"Change from \"$(guiname)\" to whatever you want."}},{"text":"\n\u25b6 "},{"text":"Configure Pages","hoverEvent":{"action":"show_text","contents":"Configure the pages of this GUI instance."}},{"text":"\n\u25b6 "},{"text":"Set Volatile Item","hoverEvent":{"action":"show_text","contents":"Set a Volatile Item."}},{"text":"\n "}]

scoreboard players remove @s bgui.iteration 1
data remove storage minecraft:barden.gui.list.query Query[0]
execute if score @s bgui.iteration matches 0 run function barden.guimaker:commands/allguilist/search_from_id/__3__
execute if score @s bgui.iteration matches 1.. run function barden.guimaker:commands/allguilist/search_from_id/__2__ with storage minecraft:barden.gui.list.query Query[0]