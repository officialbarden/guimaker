$execute store result score @s bgui.inv.iteration run data get storage barden:list.query QUERY[{username:$(username)}].page
$function barden.gui:maker/commands/createguipage/nbt_merge/iteration/__3__ with storage barden:list.query QUERY[{username:$(username)}].page[0]
