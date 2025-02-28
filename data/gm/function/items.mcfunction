#> Item Holder (Accepts Item Inputs)
#give @s lime_stained_glass_pane[custom_data={gui:{"item":"holder","cached":false}},item_name='"Item Holder"',lore=['{"color":"gold","italic":false,"shadow_color":16750848,"text":"[UNCACHED]"}','{"color":"#FFFF05","text":"\\n Allows Item Inputs"}']] 1
give @s lime_stained_glass_pane[custom_data={gui:{"item":"holder","cached":true}},item_name='"Item Holder"',lore=['{"italic":false,"color":"gold","text":"[CACHED]"}','{"color":"#FFFF05","text":"\\n Allows Item Inputs"}']] 1

#> Toggle Button (Clicking it Changes Button Icon)
#give @s brown_stained_glass_pane[item_name='"Toggle Button"',lore=['{"color":"gold","italic":false,"text":"[UNCACHED]"}','{"color":"#FFFF05","text":"\\n Cycle through Items"}'],custom_data={gui:{"item":"toggle_button","cached":false}}] 1
give @s brown_stained_glass_pane[item_name='"Toggle Button"',lore=['{"color":"gold","italic":false,"text":"[CACHED]"}','{"color":"#FFFF05","text":"\\n Cycle through Items"}'],custom_data={gui:{"item":"toggle_button","cached":true}}] 1

#> Data-Driven Button (Displays Item from a Storage)
give @s pink_stained_glass_pane[item_name='"Data Driven Item-Button"',lore=['{"color":"#FFFF05","text":"\\n Insert Buttons from Storages"}'],custom_data={gui:{item:"data_driven_button"}}] 1

#> Data-Driven Page Creator 
# (Creates a button to a newer page of similar layout if not all items
# from storage are appended into GUI Menu by Data-Driven Button)
# (Only works if the GUI Menu has a Data-Driven Button)
give @s pink_stained_glass_pane[item_name='"Data Driven Page Creator"',lore=['{"color":"#FFFF05","text":"\\n Creates Cached Pages "}'],custom_data={gui:{item:"data_driven_page_creator"}}] 1
