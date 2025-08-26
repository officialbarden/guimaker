# // If there are no pages: terminate whole process:
# // tellraw message
$execute unless data storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[0] run tellraw @s {"text":"[GM] This GUI Profile has Zero Pages."}
$execute unless data storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[0] run return 0

# $(GUI_ID)
#> Takes the list of all PAGES of the GUI, inverts and stores in temp.DIALOG.list.MAIN path
# // past barden here: this code is duplicated code. It takes the huge list and inverts it so that it can be formatted properly in the dialog using macro.
data remove storage gm:storage temp.DIALOG.list.MAIN
data remove storage gm:storage temp.DIALOG.list.MAIN_INVERTED
$data modify storage gm:storage temp.DIALOG.list.MAIN set from storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES
$function gm:zprivate/dialog/spawn_gui/list/invert_list {GUI_ID:$(GUI_ID)}
data modify storage gm:storage temp.DIALOG.list.MAIN set from storage gm:storage temp.DIALOG.list.MAIN_INVERTED
data remove storage gm:storage temp.DIALOG.list.MAIN_INVERTED
data remove storage gm:storage temp.DIALOG.list.ACTIONS_LIST

# // past barden: formatting is done here:
function gm:zprivate/dialog/spawn_gui/page/show_pages/format with storage gm:storage temp.DIALOG.list.MAIN[0]

# // past barden: show dialog of all pages in the GUI:
function gm:zprivate/dialog/spawn_gui/page/show_pages/show with storage gm:storage temp.DIALOG.list
