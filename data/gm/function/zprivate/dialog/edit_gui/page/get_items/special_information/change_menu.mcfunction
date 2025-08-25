# $(GUI_ID) $(PAGE)
data remove storage gm:storage temp.STRING
$data modify storage gm:storage temp.STRING set value [{"text":"Changes To Menu:\n"},{"text":"Name: "}, {"text":""}, "\n", {"text":"GUI ID: $(GUI_ID)\nPAGE: $(PAGE)"}]
$data modify storage gm:storage temp.STRING[2].text set from storage gm:storage GUI[{GUI_ID:$(GUI_ID)}].PAGES[{PAGE:$(PAGE)}].NAME
data modify storage gm:storage temp.DIALOG.list.ACTIONS_LIST[-1].tooltip append from storage gm:storage temp.STRING
