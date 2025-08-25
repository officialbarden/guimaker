# $(GUI_ID) $(PAGE)
data remove storage gm:storage temp.DIALOG.list.MAIN
data remove storage gm:storage temp.DIALOG.list.MAIN_INVERTED
data remove storage gm:storage temp.DIALOG.list.ACTIONS_LIST
$data modify storage gm:storage temp.DIALOG.list.MAIN set from storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}].PAGE_CONTENTS

# // Store in Score to Macro Later:
$scoreboard players set @s gm.EditConfig.SourceGUIID $(GUI_ID)
$scoreboard players set @s gm.EditConfig.SourcePAGE $(PAGE)

function gm:zprivate/dialog/edit_gui/page/get_items/format with storage gm:storage temp.DIALOG.list.MAIN[0]
function gm:zprivate/dialog/edit_gui/page/get_items/show with storage gm:storage temp.DIALOG.list


