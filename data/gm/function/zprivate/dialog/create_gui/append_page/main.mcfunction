#> Takes the list of all GUIs in the world, inverts the list and stores it to temp.DIALOG.list.MAIN path
data remove storage gm:storage temp.DIALOG.list.MAIN
data remove storage gm:storage temp.DIALOG.list.MAIN_INVERTED
data modify storage gm:storage temp.DIALOG.list.MAIN set from storage gm:storage GUI
function gm:zprivate/dialog/create_gui/append_page/invert_list
data modify storage gm:storage temp.DIALOG.list.MAIN set from storage gm:storage temp.DIALOG.list.MAIN_INVERTED
data remove storage gm:storage temp.DIALOG.list.MAIN_INVERTED

#> Format for Dialog Option:
data remove storage gm:storage temp.DIALOG.list.ACTIONS_LIST
function gm:zprivate/dialog/create_gui/append_page/format with storage gm:storage temp.DIALOG.list.MAIN[0]

#> Show Dialog
function gm:zprivate/dialog/create_gui/append_page/show_dialog with storage gm:storage temp.DIALOG.list
