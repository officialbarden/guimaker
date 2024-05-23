execute in overworld run forceload add 12345678 12345678
execute in overworld run setblock 12345678 300 12345678 air
execute in overworld run setblock 12345678 300 12345678 black_shulker_box{Items:[{Count:1b,id:"minecraft:player_head",Slot:0b}]}
execute in overworld run item modify block 12345678 300 12345678 container.0 gui.maker:fph
data modify storage barden:names Names.username set from block 12345678 300 12345678 Items[{Slot:0b}].tag.SkullOwner.Name
execute in overworld run setblock 12345678 300 12345678 air
