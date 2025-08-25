# // past barden: removes the "minecraft:" from item name cuz it doesnt fit in tooltip.
data remove storage gm:storage temp.STRING
$data modify storage gm:storage temp.STRING.1 set value "$(id)h"
data modify storage gm:storage temp.STRING.2 set string storage gm:storage temp.STRING.1 10 -1
data modify storage gm:storage temp.DIALOG.list.ACTIONS_LIST[-1].tooltip[1].text set string storage gm:storage temp.STRING.2

