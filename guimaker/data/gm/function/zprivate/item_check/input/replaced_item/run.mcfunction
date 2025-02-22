#say replaced!
#tellraw @a ["Item Replaced:", {"nbt":"data.Items_init[0]","entity":"@s"}]
#tellraw @a ["New Item:", {"nbt":"data.Items_final[0]","entity":"@s"}]

execute if data entity @s data.Items_init[{"components":{"minecraft:custom_data":{gui:{"item":"holder"}}}}] run function gm:zprivate/caching/item_holder/main
