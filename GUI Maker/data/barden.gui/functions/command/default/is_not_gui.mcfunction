clear @s barrier{bguiButton:1b}
$tellraw @s [{"text": "[bgui] ","color": "gold"},{"text": "No GUI found having GUI-ID: ","color": "white"},{"text":"$(ID)"}]
playsound minecraft:entity.enderman.teleport master @s ~ ~ ~ 1 1 1

function barden.gui:command/close/gui
