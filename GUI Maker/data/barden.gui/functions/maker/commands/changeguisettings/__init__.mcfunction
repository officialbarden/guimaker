# $(gui_id)

tellraw @s ["",{"text":"--------------------------","color":"gray"}]
$tellraw @s ["",{"text":"[bgui]","color":"gold"},{"text":" GUI "},{"text":"#$(gui_id)","color":"gold"},{"text":" Generic Settings:"}]
$function barden.gui:maker/commands/changeguisettings/zz/1 with storage barden:gui.all GUI[{guiid:$(gui_id)}].guisettings
