## macros:
# $(GUI_ID)
# $(PAGE)

$execute if block ~ ~ ~ air run setblock ~ ~ ~ barrel{CustomName:'{"text": "No GUI Found"}',Items:[{Count:1b,Slot:13b,id:"barrier",tag:{guim.Button:1b,display:{Name:'{"text":"no GUI found.","color":"gold","italic":false}',Lore:['{"text":"no gui found for","color":"gray","italic":false}','{"text":"gui_id: $(GUI_ID)","color":"gray","italic":false}','{"text":"page: $(PAGE)","color":"gray","italic":false}']}}}]}

playsound minecraft:block.wood.place master @a ~ ~ ~ 1 1 1

summon marker ~ ~ ~ {Tags:["gui.container"]}

function gui.maker:worker/gui/block/fill with entity @s data

kill @s