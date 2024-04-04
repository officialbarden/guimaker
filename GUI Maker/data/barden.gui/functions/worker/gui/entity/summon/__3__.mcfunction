$data modify storage barden:gui.entity.id.conforming CONNECTION[{GUI_ID:$(GUI_ID)}].UUID append from entity @s UUID[]
$data modify storage barden:gui.entity.id.conforming CONNECTION[{GUI_ID:$(GUI_ID)}].ENTITYUUID set from entity @s UUID

#say connection establised!
$data modify storage barden:gui.entity.all.gui.link all_connections append value {"guiid":$(GUI_ID),"new":true}
$data modify storage barden:gui.entity.all.gui.link all_connections[{"guiid":$(GUI_ID),"new":true}].uuid append from entity @s UUID[]
$data modify storage barden:gui.entity.all.gui.link all_connections[{"guiid":$(GUI_ID),"new":true}] merge value {"new":false}
#say connection appended!


$function barden.gui:worker/gui/entity/summon/__4__ with storage barden:gui.all GUI[{guiid:$(GUI_ID)}]


execute as @e[type=chest_minecart,tag=bgui.gui-true,limit=1] if entity @e[type=marker,sort=nearest,tag=bgui.marker.itemchangecheck,tag=!bgui.check] positioned ~ ~ ~ run function barden.gui:worker/gui/entity/summon/page_check_setup/__1__ with entity @e[type=marker,sort=nearest,limit=1,tag=bgui.marker.itemchangecheck] data
