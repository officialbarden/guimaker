# $(PAGE) $(NAME)
$data modify storage gm:storage temp.DIALOG.list.ACTIONS_LIST append value {"label":"$(NAME)", action:{"type":"run_command", "command":"function gm:zprivate/dialog/spawn_gui/page/show_options/main {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}"}}
data remove storage gm:storage temp.DIALOG.list.MAIN[0]
execute if data storage gm:storage temp.DIALOG.list.MAIN[0] run return run function gm:zprivate/dialog/edit_gui/page/show_pages/format with storage gm:storage temp.DIALOG.list.MAIN[0]
