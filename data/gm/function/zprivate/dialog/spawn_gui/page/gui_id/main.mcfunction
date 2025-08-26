$data modify storage gm:storage temp.DIALOG.list.ACTIONS_LIST append value {"label":"GUI ID: #$(GUI_ID)", "action":{"type":"run_command", "command":"/function gm:zprivate/dialog/spawn_gui/page/show_pages/main {GUI_ID:$(GUI_ID)}"}}
data remove storage gm:storage temp.DIALOG.list.MAIN[0]
execute if data storage gm:storage temp.DIALOG.list.MAIN[0] run function gm:zprivate/dialog/spawn_gui/page/gui_id/main with storage gm:storage temp.DIALOG.list.MAIN[0]
