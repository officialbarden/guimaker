execute store result storage gm:storage temp.GUI_PROFILE.GUI_ID int 1 run scoreboard players get $global gm.ID
tellraw @s [{"text":"GUI Profile Created. ID="},{"score":{"name":"$global","objective":"gm.ID"}}]
scoreboard players add $global gm.ID 1
data modify storage gm:storage GUI append from storage gm:storage temp.GUI_PROFILE
