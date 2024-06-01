
#> configuring commands:
     execute as @a[scores={gm-createGUIprofile=1..}] at @s run function gui.maker:maker/gui/create/1
     execute as @a[scores={gm-addPage=1..}] at @s run function gui.maker:maker/gui/add_page/1
     execute as @a[scores={gm-editPage=1..}] at @s run function gui.maker:maker/gui/edit_page/1
     execute as @a[scores={gm-getListOfAllGUIs=1..}] at @s run function gui.maker:maker/gui/get_list_of_guis/1

#> setblock
     execute as @e[type=marker,tag=gm.gui.container.place] at @s run function gui.maker:worker/gui/block/set/1 with entity @s data

#> kill useless marker
     execute as @e[type=marker,tag=gm.gui.container] at @s unless block ~ ~ ~ barrel run kill @s
     execute as @e[type=marker,tag=gm.gui.cm] at @s unless entity @e[type=chest_minecart,sort=nearest,limit=1,tag=gm.gui.ui,dx=0] run kill @s
     execute as @e[type=interaction,tag=gm.ui.protect] at @s unless entity @e[type=chest_minecart,sort=nearest,limit=1,tag=gm.gui.ui,dx=0] run kill @s

#> item click detect:
     execute as @e[type=marker,tag=gm.itemcheck,tag=gm.gui.block] at @s store result score @s gm.Tests run data modify entity @s data.PAGE.FINAL set from block ~ ~ ~ Items
     execute as @e[type=marker,tag=gm.itemcheck,tag=gm.gui.block,scores={gm.Tests=1}] run function gui.maker:worker/gui/click/1 with entity @s data

     execute as @e[type=marker,tag=gm.itemcheck,tag=gm.gui.cm] at @s store result score @s gm.Tests run data modify entity @s data.PAGE.FINAL set from entity @e[type=chest_minecart,sort=nearest,limit=1,tag=gm.gui.ui] Items
     execute as @e[type=marker,tag=gm.itemcheck,tag=gm.gui.cm,scores={gm.Tests=1}] run function gui.maker:worker/gui/click/1 with entity @s data

#> kill all button item entities:
     kill @e[type=item,nbt={Item:{tag:{gm.Button:1}}}]


#> teleport chest_minecart
     execute as @e[tag=gm.gui.ui,type=chest_minecart] at @s if score @s gm.Linking = @p gm.Linking 
     execute as @e[type=chest_minecart,tag=gm.gui.ui] if score @s gm.Linking = @p gm.Linking as @p at @s anchored eyes positioned ^ ^ ^0.1 run tp @e[sort=nearest,limit=1,tag=gm.gui.ui] ~ ~-0.05 ~
