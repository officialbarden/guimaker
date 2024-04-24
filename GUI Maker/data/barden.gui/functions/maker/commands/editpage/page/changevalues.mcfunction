## crucial:
# $(GUI_ID)
# $(PAGE)
# $(SLOT)

## changes:
# $(FUNCTION_FILE)
# $(ITEM_MODIFIER)
# $(CHANGE_TO_GUI_ID)
# $(CHANGE_TO_PAGE_INDEX)

$data modify storage barden:gui.all GUI[{guiid:$(GUI_ID)}].pages[$(PAGE)][{Slot:$(SLOT)}].tag.bguiFunction set value "$(FUNCTION_FILE)"
$data modify storage barden:gui.all GUI[{guiid:$(GUI_ID)}].pages[$(PAGE)][{Slot:$(SLOT)}].tag.bguiChangeToPageData.PAGE_INDEX set value "$(CHANGE_TO_PAGE_INDEX)"
$data modify storage barden:gui.all GUI[{guiid:$(GUI_ID)}].pages[$(PAGE)][{Slot:$(SLOT)}].tag.bguiChangeToPageData.ID set value "$(CHANGE_TO_GUI_ID)"
$data modify storage barden:gui.all GUI[{guiid:$(GUI_ID)}].pages[$(PAGE)][{Slot:$(SLOT)}].tag.bguiItemModifier set value "$(ITEM_MODIFIER)"

tellraw @s [{"text":"--------------------------","color":"gray"}]
$tellraw @s ["",{"text":"[bgui] ","color":"gold","clickEvent":{"action":"suggest_command","value":"/function barden.gui:api/page/summon {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}"}},{"text":"Changes were merged successfully! Click to OPEN GUI.","color":"white","clickEvent":{"action":"suggest_command","value":"/function barden.gui:api/page/summon {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}"}}]
tellraw @s [{"text":"--------------------------","color":"gray"}]
