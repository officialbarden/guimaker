# $(username)
# $(changingguiid)
scoreboard players reset @s bgui.raycast.limit
scoreboard players set @s bgui.raycast.limit 6
$execute at @s[scores={bgui.raycast.limit=1..}] anchored eyes positioned ^ ^ ^1 run function barden.gui:maker/commands/createguipage/raycast/__2__ {"changingguiid":$(changingguiid),"username":$(username)}

