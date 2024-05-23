$data modify storage barden:api API append value {id:"minecraft:purple_shulker_box",Count:1b,tag:{guim.Button:1b,display:{Name:'["",{"text":"$(GUI_NAME)","italic":false,"color":"gold"},{"text":",","italic":false,"color":"white"},{"text":" #$(GUI_ID)","italic":false,"color":"gold"},{"text":" by ","italic":false,"color":"white"},{"text":"$(OWNER)","italic":false,"color":"gold"}]',Lore:['["",{"text":"Click to view/edit!","italic":false,"color":"gray"}]']}}}

scoreboard players remove $owned_guis gm.Extras 1
data remove storage barden:temp Item.1[0]
execute if score $owned_guis gm.Extras matches 1.. run function gui.maker:maker/api/convert_data_to_item/3 with storage barden:temp Item.1[0]
