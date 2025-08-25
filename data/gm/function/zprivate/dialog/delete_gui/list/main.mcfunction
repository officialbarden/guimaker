#> Takes the list of all GUIs in the world, inverts the list and stores it to temp.DIALOG.list.MAIN path
# // past barden: even though this function file solely exists in delete_gui, it is used in edit_gui as well. 
data remove storage gm:storage temp.DIALOG.list.MAIN
data remove storage gm:storage temp.DIALOG.list.MAIN_INVERTED
data modify storage gm:storage temp.DIALOG.list.MAIN set from storage gm:storage GUI
function gm:zprivate/dialog/delete_gui/list/invert_list
data modify storage gm:storage temp.DIALOG.list.MAIN set from storage gm:storage temp.DIALOG.list.MAIN_INVERTED
data remove storage gm:storage temp.DIALOG.list.MAIN_INVERTED
data remove storage gm:storage temp.DIALOG.list.ACTIONS_LIST
