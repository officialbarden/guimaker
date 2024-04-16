$data modify storage barden:gui.query ITEMSLIST append from storage barden:gui.all GUI[{guiid:$(GUI_ID)}].pages[$(PAGE)][]

execute store result score @s bgui.inv.iteration run data get storage barden:gui.query ITEMSLIST
$function barden.gui:maker/commands/editpage/page/3 {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}
