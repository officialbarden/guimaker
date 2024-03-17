# $(username)
# $(changingguiid)
scoreboard players reset @s bgui.extra
scoreboard players set @s bgui.extra 6
$execute at @s[scores={bgui.extra=1..}] anchored eyes positioned ^ ^ ^1 run function barden.guimaker:commands/createguipage/raycast/__2__ {"changingguiid":$(changingguiid),"username":$(username)}

