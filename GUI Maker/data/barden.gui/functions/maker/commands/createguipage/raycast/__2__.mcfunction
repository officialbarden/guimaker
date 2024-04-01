particle minecraft:dust 1 0 0 1 ~ ~ ~

#> checking if failed.
scoreboard players remove @s bgui.raycast.limit 1
execute if score @s bgui.raycast.limit matches 0 run function barden.gui:maker/commands/createguipage/raycast/solution/_fail

#> recursion

$execute if score @s bgui.raycast.limit matches 1.. unless block ~ ~ ~ #barden.gui:gui_page_data positioned ^ ^ ^1 run function barden.gui:maker/commands/createguipage/raycast/__2__ {"changingguiid":$(changingguiid),"username":$(username)}


#> success; (stop recursion)

$execute if block ~ ~ ~ #barden.gui:gui_page_data run function barden.gui:maker/commands/createguipage/raycast/solution/_success {"changingguiid":$(changingguiid),"username":$(username)}

