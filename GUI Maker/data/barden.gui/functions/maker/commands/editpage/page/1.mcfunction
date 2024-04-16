# $(GUI_ID)
# $(PAGE)
#$data modify storage barden:gui.query ITEMSQUERY append from storage barden:gui.all GUI[{guiid:$(GUI_ID)}].pages[$(PAGE)]


$function barden.gui:maker/commands/editpage/page/2 {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}

