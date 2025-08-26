# $(GUI_ID) $(PAGE)
$dialog show @s {type:"minecraft:multi_action",title:["GUI Maker v2.0 - Spawn Page as:"],body:[],inputs:[],can_close_with_escape:1b,pause:0b,columns:1,actions:[{label:"Spawn Barrel at Location",action:{type:"minecraft:run_command",command:"function gm:zprivate/gui_spawn/barrel/main {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}"}},{label:"Spawn Chest Minecart",action:{type:"minecraft:run_command",command:"function gm:zprivate/gui_spawn/chest_minecart/main {GUI_ID:$(GUI_ID),PAGE:$(PAGE)}"}}]}

