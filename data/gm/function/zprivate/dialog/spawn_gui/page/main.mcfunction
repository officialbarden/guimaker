# // past barden: this part is for choosing the GUI ID to source the Pages data.

#> Get List
function gm:zprivate/dialog/delete_gui/list/main
#> Format List
data remove storage gm:storage temp.DIALOG.list.ACTIONS_LIST
function gm:zprivate/dialog/spawn_gui/page/gui_id/main with storage gm:storage temp.DIALOG.list.MAIN[0]
#> Show in Dialog:
function gm:zprivate/dialog/spawn_gui/page/gui_id/show with storage gm:storage temp.DIALOG.list
