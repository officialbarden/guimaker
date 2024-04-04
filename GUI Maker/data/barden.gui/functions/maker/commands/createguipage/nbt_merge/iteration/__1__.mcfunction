# $(guiid)
# $(username)
$data modify storage barden:list.query QUERY append value {guiid:$(guiid),username:$(username)}
$data modify storage barden:list.query QUERY[{guiid:$(guiid),username:$(username)}].page append from storage barden:gui.all GUI[{guiid:$(guiid)}].pages[-1][] 

$function barden.gui:maker/commands/createguipage/nbt_merge/iteration/__2__  {username:$(username)}
