#> scoreboards
     scoreboard objectives add gm.Values dummy
     scoreboard objectives add gm.Tests dummy
     scoreboard objectives add gm.Linking dummy

#> Values
     scoreboard players add $globalCreationID gm.Values 1


#> triggers
     scoreboard objectives add gm-createGUIprofile trigger
     scoreboard players enable @a[gamemode=creative] gm-createGUIprofile
     scoreboard objectives add gm-addPage trigger
     scoreboard players enable @a[gamemode=creative] gm-addPage
     scoreboard objectives add gm-editPage trigger
     scoreboard players enable @a[gamemode=creative] gm-editPage
     scoreboard objectives add gm-getApiSlotPlaceholder trigger
     scoreboard players enable @a[gamemode=creative] gm-getApiSlotPlaceholder
     scoreboard objectives add gm-convertHandItemtoPageButton trigger
     scoreboard players enable @a[gamemode=creative] gm-convertHandItemtoPageButton
     scoreboard objectives add gm-getListOfAllGUIs trigger
     scoreboard players enable @a[gamemode=creative] gm-getListOfAllGUIs
     scoreboard objectives add gm-convertHandItemtoVItem trigger
     scoreboard players enable @a[gamemode=creative] gm-convertHandItemtoVItem