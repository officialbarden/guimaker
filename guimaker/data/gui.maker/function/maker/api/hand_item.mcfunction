tellraw @s {"text":">> You must have OPERATOR to run this function!","color": "gray","italic": true}
tellraw @s {"text":"[Click here to insert crucial values.]","bold":true,"color":"#FFC300","clickEvent":{"action":"suggest_command","value":"/function gui.maker:maker/api/func/2 {\"function_file\":\"\",\"item_modifier\":\"\",\"change_gui\":\"\",\"change_page\":\"\"}"}}
scoreboard players reset @s gm-convertHandItemtoPageButton
scoreboard players enable @s gm-convertHandItemtoPageButton