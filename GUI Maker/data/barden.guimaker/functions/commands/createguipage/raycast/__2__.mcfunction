particle minecraft:dust 1 0 0 1 ~ ~ ~

#> checking if failed.
scoreboard players remove @s bgui.extra 1
execute if score @s bgui.extra matches 0 run function barden.guimaker:commands/createguipage/raycast/_fail

#> recursion

$execute if score @s bgui.extra matches 1.. unless block ~ ~ ~ #barden.guimaker:gui_page_data positioned ^ ^ ^1 run function barden.guimaker:commands/createguipage/raycast/__2__ {"changingguiid":$(changingguiid),"username":$(username)}


#> success; (stop recursion)

$execute if block ~ ~ ~ #barden.guimaker:gui_page_data run function barden.guimaker:commands/createguipage/raycast/_success {"changingguiid":$(changingguiid),"username":$(username)}

