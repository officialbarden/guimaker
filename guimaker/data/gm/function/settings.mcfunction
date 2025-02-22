#> Check for Item
execute if items entity @s weapon.mainhand *[custom_data~{gui:{item:"holder"}}] run return run function gm:zprivate/button_settings/item_holder/main
execute if items entity @s weapon.mainhand *[custom_data~{gui:{item:"toggle_button"}}] run return run function gm:zprivate/button_settings/toggle/main
execute if items entity @s weapon.mainhand *[custom_data~{gui:{item:"data_driven_button"}}] run return run function gm:zprivate/button_settings/data_driven/button/main
execute if items entity @s weapon.mainhand *[custom_data~{gui:{item:"data_driven_page_creator"}}] run return run function gm:zprivate/button_settings/data_driven/page_creator/main

#> Error Message
tellraw @s [{"color":"#8F8F8F","text":"[Gui Maker]"},{"color":"#FF6666","text":" Err: This handheld Item cannot be modified by datapack."}]
