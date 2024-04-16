# $(GUI_ID)
# $(PAGE)
# $(FUNCTION_FILE)
# $(ITEM_MODIFIER)
# $(CHANGE_TO_GUI_ID)
# $(CHANGE_TO_PAGE_INDEX)

$data modify storage barden:gui.all GUI[{guiid:$(GUI_ID)}].pages[$(PAGE)][]
