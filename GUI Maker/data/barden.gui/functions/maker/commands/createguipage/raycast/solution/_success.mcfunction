$data modify storage barden:gui.all GUI[{guiid:$(changingguiid),creator:$(username)}].pages insert $(pagenumber) from block ~ ~ ~ Items
$data modify storage barden:gui.all GUI[{guiid:$(changingguiid),creator:$(username)}].pages[][].tag merge value {bguiButton:1b,bguiFunction:"",bguiItemModifier:"",bguichangetoPageData:{bguiId:"",bguiIdPageIndex:""}}

tellraw @s ["",{"text":"------------------------------------------","color":"gray"},{"text":"\n"},{"text":"[bgui]","color":"gold"},{"text":" page successfully generated!","color":"white"}]
$tellraw @s [{"text":"\n\n"},{"text":"\u25b6 Click here to Edit Page/Configure Buttons.","clickEvent":{"action":"run_command","value":"/function barden.gui:maker/commands/createguipage/nbt_merge/iteration/__1__ {guiid:$(changingguiid),username:$(username)}"}}]
tellraw @s [{"text":"\n"},{"text":"------------------------------------------","color":"gray"}]


scoreboard players reset @s bgui.raycast.limit