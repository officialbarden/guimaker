scoreboard players remove @s bgui.inv.iteration 1

$tellraw @s ["",{"text":"GUI ","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/3 {GUI_ID:$(guiid)}"},"hoverEvent":{"action":"show_text","contents":"Select This GUI"}},{"text":"#$(guiid)","color":"gold","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/3 {GUI_ID:$(guiid)}"},"hoverEvent":{"action":"show_text","contents":"Select This GUI"}},{"text":", \"","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/3 {GUI_ID:$(guiid)}"},"hoverEvent":{"action":"show_text","contents":"Select This GUI"}},{"text":"$(guiname)","color":"gold","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/3 {GUI_ID:$(guiid)}"},"hoverEvent":{"action":"show_text","contents":"Select This GUI"}},{"text":"\", Created by ","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/3 {GUI_ID:$(guiid)}"},"hoverEvent":{"action":"show_text","contents":"Select This GUI"}},{"text":"$(creator)","color":"gold","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/3 {GUI_ID:$(guiid)}"},"hoverEvent":{"action":"show_text","contents":"Select This GUI"}},{"text":".","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/editpage/3 {GUI_ID:$(guiid)}"},"hoverEvent":{"action":"show_text","contents":"Select This GUI"}}]

data remove storage barden:gui.query list[0]

execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:maker/commands/editpage/2 with storage barden:gui.query list[0]
