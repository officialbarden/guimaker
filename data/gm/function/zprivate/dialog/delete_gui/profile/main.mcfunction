# If no GUI Profiles exist; terminate the process
execute unless data storage gm:storage GUI[0] run tellraw @s {"text":"[GM] There are no GUIs in this world."}
execute unless data storage gm:storage GUI[0] run return 0
#> Get List
function gm:zprivate/dialog/delete_gui/list/main
#> Format
function gm:zprivate/dialog/delete_gui/profile/gui_id/main with storage gm:storage temp.DIALOG.list.MAIN[0]
#> Show Dialog:
function gm:zprivate/dialog/delete_gui/profile/gui_id/show with storage gm:storage temp.DIALOG.list
