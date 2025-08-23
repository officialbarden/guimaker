scoreboard players set X gm.POS.INIT 0
scoreboard players set Y gm.POS.INIT 0
scoreboard players set Z gm.POS.INIT 0

scoreboard players operation X gm.POS.INIT = @s gm.POS.INIT.X
scoreboard players operation Y gm.POS.INIT = @s gm.POS.INIT.Y
scoreboard players operation Z gm.POS.INIT = @s gm.POS.INIT.Z

execute store result score X gm.POS.FINAL run data get entity @s Pos[0] 1000
execute store result score Y gm.POS.FINAL run data get entity @s Pos[1] 1000
execute store result score Z gm.POS.FINAL run data get entity @s Pos[2] 1000

scoreboard players operation X gm.POS.FINAL -= X gm.POS.INIT
scoreboard players operation Y gm.POS.FINAL -= Y gm.POS.INIT
scoreboard players operation Z gm.POS.FINAL -= Z gm.POS.INIT

execute store result storage gm:storage temp.macro.VELOCITY.X float 0.001 run scoreboard players get X gm.POS.FINAL
execute store result storage gm:storage temp.macro.VELOCITY.Y float 0.001 run scoreboard players get Y gm.POS.FINAL
execute store result storage gm:storage temp.macro.VELOCITY.Z float 0.001 run scoreboard players get Z gm.POS.FINAL

execute store result score @s gm.POS.INIT.X run data get entity @s Pos[0] 1000
execute store result score @s gm.POS.INIT.Y run data get entity @s Pos[1] 1000
execute store result score @s gm.POS.INIT.Z run data get entity @s Pos[2] 1000

function gm:zprivate/velocity/teleport with storage gm:storage temp.macro.VELOCITY

