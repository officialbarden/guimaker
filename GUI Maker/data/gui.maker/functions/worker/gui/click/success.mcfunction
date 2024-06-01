$clear @p $(id)$(tag) $(Count)

execute as @p run function gui.maker:username
data modify storage barden:names Names.function_file set from entity @s data.PAGE.INIT[0].tag.BUTTON_INFO.function_file
data modify storage barden:names Names.change_gui set from entity @s data.PAGE.INIT[0].tag.BUTTON_INFO.CHANGE_PAGE.GUI_ID
data modify storage barden:names Names.change_page set from entity @s data.PAGE.INIT[0].tag.BUTTON_INFO.CHANGE_PAGE.PAGE

function gui.maker:worker/gui/click/run with storage barden:names Names
