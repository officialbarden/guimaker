## macros
# $(GUI_ID)
# $(PAGE)

function gui.maker:username
$data modify storage barden:names Names.GUI_ID set value $(GUI_ID)
$data modify storage barden:names Names.PAGE set value $(PAGE)

function gui.maker:worker/gui/entity/summon/2 with storage barden:names Names
