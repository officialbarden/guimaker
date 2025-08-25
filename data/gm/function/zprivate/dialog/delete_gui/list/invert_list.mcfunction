$data modify storage gm:storage temp.DIALOG.list.MAIN[-1].GUI_ID set value $(GUI_ID)
execute if data storage gm:storage temp.DIALOG.list.MAIN[0] run data modify storage gm:storage temp.DIALOG.list.MAIN_INVERTED append from storage gm:storage temp.DIALOG.list.MAIN[-1]
data remove storage gm:storage temp.DIALOG.list.MAIN[-1]
execute if data storage gm:storage temp.DIALOG.list.MAIN[0] run return run function gm:zprivate/dialog/delete_gui/list/invert_list
