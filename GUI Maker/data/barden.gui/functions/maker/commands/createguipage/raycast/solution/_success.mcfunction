$data modify storage barden:gui.all GUI[{guiid:$(changingguiid),creator:$(username)}].pages insert $(pagenumber) from block ~ ~ ~ Items
$data modify storage barden:gui.all GUI[{guiid:$(changingguiid),creator:$(username)}].pages[][].tag merge value {bguiButton:1b,bguiFunction:"",bguiItemModifier:"",bguiChangeToPageData:{ID:"$(changingguiid)"}}




#> math (to get page index)

$execute store result score $TotalPages bgui.pagenumber.detect run data get storage barden:gui.all GUI[{guiid:$(changingguiid)}].pages
scoreboard players set .const_1 bgui.pagenumber.detect 1
scoreboard players operation $GetPageNumber bgui.pagenumber.detect = $TotalPages bgui.pagenumber.detect
scoreboard players operation $GetPageNumber bgui.pagenumber.detect -= .const_1 bgui.pagenumber.detect
$execute store result storage barden:gui.all GUI[{guiid:$(changingguiid)}].pages[-1][].tag.bguiChangeToPageData.PAGE_INDEX int 1 run scoreboard players get $GetPageNumber bgui.pagenumber.detect



tellraw @s ["",{"text":"------------------------------------------","color":"gray"}]
tellraw @s [{"text":"[bgui]","color":"gold"},{"text":" page successfully generated!","color":"white"},{"text":" Generated Page Number => ","color": "white"},{"score":{"name":"$GetPageNumber","objective":"bgui.pagenumber.detect"},"color":"gold"}]
tellraw @s [{"text":"\n"}]
$tellraw @s [{"text":"\u25b6 Click here to Edit Page/Configure Buttons.","clickEvent":{"action":"suggest_command","value":"/function barden.gui:maker/commands/createguipage/nbt_merge/iteration/__1__ {guiid:$(changingguiid),username:$(username)}"}}]
tellraw @s [{"text":"\n"}]
tellraw @s [{"text":"------------------------------------------","color":"gray"}]

scoreboard players reset @s bgui.raycast.limit