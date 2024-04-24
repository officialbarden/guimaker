data remove storage barden:gui.all GUI
data remove storage barden:gui.entity.all.gui.link all_connections
data remove storage barden:gui.entity.id.conforming CONNECTION
data remove storage barden:gui.page.init PAGEINIT
data remove storage barden:gui.page.init PAGEFINAL
data remove storage barden:gui.players.names Names
data remove storage barden:gui.query list

scoreboard objectives remove bgui-addPage
scoreboard objectives remove bgui-createGUIprofile
scoreboard objectives remove bgui-editPage
scoreboard objectives remove bgui-getListofAllGuis
scoreboard objectives remove bgui-Help

scoreboard objectives remove bgui.changeitem.test.result
scoreboard objectives remove bgui.entity.id.conforming
scoreboard objectives remove bgui.entity.linking
scoreboard objectives remove bgui.guiprofile.id
scoreboard objectives remove bgui.inv.iteration
scoreboard objectives remove bgui.pagenumber.detect
scoreboard objectives remove bgui.raycast.limit

tellraw @s ["",{"text":"[GUI Maker version 1.0.0] ","color":"gold"},{"text":"Datapack successfully uninstalled. It can now be deleted from the folders safely.","color":"white"}]
datapack disable "file/GUI Maker"
