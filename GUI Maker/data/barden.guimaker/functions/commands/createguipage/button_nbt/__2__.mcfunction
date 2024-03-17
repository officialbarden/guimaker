$data modify storage minecraft:barden.gui.list.query Query prepend value {username:$(username),guiid:$(guiid)}
$data modify storage minecraft:barden.gui.list.query Query[{username:$(username)}].pageItems set from storage minecraft:barden.gui.all GUI[{guiid:$(guiid)}].pages[-1]
$data modify storage minecraft:barden.gui.list.query Query[{username:$(username)}].pageItems[].tag merge value {guiid:$(guiid),username:$(username)}

$function barden.guimaker:commands/createguipage/button_nbt/__3__ with storage minecraft:barden.gui.list.query Query[{guiid:$(guiid)}].pageItems[0]
