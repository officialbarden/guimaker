$data modify storage barden:names Names.GUI_NAME set value "$(username)'s GUI #$(GUI_ID)"

data modify storage barden:gui.all GUI append from storage barden:names Names
