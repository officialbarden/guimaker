# $(GUI_ID)

#> summoning entity:

$execute anchored eyes positioned ^ ^-0.2 ^0.3 run summon chest_minecart ~ ~ ~ {NoGravity:1b,CustomNameVisible:0b,CustomDisplayTile:0b,Silent:1b,Tags:["bgui.gui-false","bgui.id.$(GUI_ID)"]}

#> linking chest_minecart to player.
scoreboard players operation @s bgui.entity.linking = .global bgui.entity.linking
scoreboard players operation @e[type=chest_minecart,tag=bgui.gui-false,sort=nearest,limit=1] bgui.entity.linking = .global bgui.entity.linking
scoreboard players add .global bgui.entity.linking 1


#> conforming to GUI_ID 

$scoreboard players set @e[type=chest_minecart,sort=nearest,limit=1,tag=bgui.gui-false] bgui.entity.id.conforming $(GUI_ID)

