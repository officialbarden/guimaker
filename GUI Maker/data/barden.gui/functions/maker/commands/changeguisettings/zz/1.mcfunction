#> show settings:

tellraw @s {"text": "\n"}
$tellraw @s ["",{"text":"\u25b6 ","bold":true,"color":"white"},{"text":"Accept Item Input","color":"white","hoverEvent":{"action":"show_text","contents":"If user puts an item in the GUI, it returns the item to the user's inventory. ( 0=enable return, 1=disable return)"}},{"text":": $(accept_item_input)b","color":"white"}]

tellraw @s {"text": "\n"}
$tellraw @s ["",{"text":"\u25b6 ","bold":true,"color":"white"},{"text":"Is Dynamic","color":"white","hoverEvent":{"action":"show_text","contents":"If enabled, GUI can send (per tick)/fetch external Item data and append it to itself (0=disable,1=enable) "}},{"text":": $(is_dynamic)b","color":"white"}]

tellraw @s {"text": "\n"}
$tellraw @s ["",{"text":"\u25b6 ","bold":true,"color":"white"},{"text":"Is Animated","color":"white","hoverEvent":{"action":"show_text","contents":"If enabled, GUI can change items on the page. (0=disable,1=enable)"}},{"text":": $(is_animated)b","color":"white"}]
