#> kill all button items:
kill @e[type=item,nbt={Item:{tag:{bguiButton:1b}}}]

#> verifying entity to be a bGUI entity.
execute as @e[type=chest_minecart,tag=bgui.gui-false] at @s run function barden.gui:worker/gui/entity/summon/__2__
#execute as @e[type=chest_minecart,tag=bgui.gui-true] if score @s bgui.entity.linking = @p bgui.entity.linking as @p at @s anchored eyes positioned ^ ^-0.3 ^0.65 run tp @e[sort=nearest,limit=1,tag=bgui.gui-true] ~ ~ ~
execute as @e[type=chest_minecart,tag=bgui.gui-true] if score @s bgui.entity.linking = @p bgui.entity.linking as @p at @s anchored eyes positioned ^ ^ ^0.1 run tp @e[sort=nearest,limit=1,tag=bgui.gui-true] ~ ~-0.05 ~

execute as @e[type=marker,tag=bgui.check] at @s run function barden.gui:worker/gui/entity/click/__1__ with entity @s data

execute as @e[type=marker,tag=bgui.marker.itemchangecheck] at @s unless entity @e[type=chest_minecart,sort=nearest,limit=1,dx=0] run function barden.gui:command/close/gui

execute as @a[nbt={SelectedItem:{tag:{bgui:1b}}},tag=!bgui.vitemholder] at @s run function barden.gui:worker/vitem/1 with entity @s SelectedItem.tag
execute as @a[nbt=!{SelectedItem:{tag:{bgui:1b}}},tag=bgui.vitemholder] at @s run function barden.gui:worker/vitem/2
