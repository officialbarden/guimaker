# $(command) $(item_modifier) $(function)
data remove storage gm:storage temp.STRING
$data modify storage gm:storage temp.STRING set value {"text":"command: $(command)\nitem modifier:$(item_modifier)\nfunction file: $(function)\n\n"}
data modify storage gm:storage temp.DIALOG.list.ACTIONS_LIST[-1].tooltip[-1] set from storage gm:storage temp.STRING

