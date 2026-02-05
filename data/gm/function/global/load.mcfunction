scoreboard objectives add gm.Values dummy
scoreboard objectives add gm.Tests dummy
scoreboard objectives add gm.ID dummy

execute if score #loaded gm.Values matches 1 run return 0

scoreboard objectives add gm.EditConfig.Slot dummy
scoreboard objectives add gm.EditConfig.SourceGUIID dummy
scoreboard objectives add gm.EditConfig.SourcePAGE dummy

scoreboard players add $global gm.ID 1

scoreboard objectives add gm.POS.INIT dummy
scoreboard objectives add gm.POS.INIT.X dummy
scoreboard objectives add gm.POS.INIT.Y dummy
scoreboard objectives add gm.POS.INIT.Z dummy
scoreboard objectives add gm.POS.FINAL dummy


function gm:global/loop_1t

scoreboard players set #loaded gm.Values 1
playsound minecraft:entity.experience_orb.pickup master @a ~ ~ ~ 3
