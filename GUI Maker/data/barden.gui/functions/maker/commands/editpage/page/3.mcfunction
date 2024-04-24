scoreboard players remove @s bgui.inv.iteration 1

tellraw @s {"text": "\n"}
function barden.gui:maker/commands/editpage/page/extra/iteminfo with storage barden:gui.query ITEMSLIST[0]
function barden.gui:maker/commands/editpage/page/extra/bguidatainfo with storage barden:gui.query ITEMSLIST[0].tag
function barden.gui:maker/commands/editpage/page/extra/bguipagechangeinfo with storage barden:gui.query ITEMSLIST[0].tag.bguiChangeToPageData
$tellraw @s {"text":"[CHANGE VALUES]","color":"green","clickEvent":{"action":"suggest_command","value":"/function barden.gui:maker/commands/editpage/page/changevalues {SLOT:\"\",FUNCTION_FILE:\"\",ITEM_MODIFIER:\"\",CHANGE_TO_GUI_ID:\"$(GUI_ID)\",CHANGE_TO_PAGE_INDEX:\"$(PAGE)\",GUI_ID:$(GUI_ID),PAGE:$(PAGE)}"},"hoverEvent":{"action":"show_text","contents":"Replace Values with appropriate values"}}
tellraw @s {"text": "\n"}

data remove storage barden:gui.query ITEMSLIST[0]
$execute if score @s bgui.inv.iteration matches 1.. run function barden.gui:maker/commands/editpage/page/3 {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}
