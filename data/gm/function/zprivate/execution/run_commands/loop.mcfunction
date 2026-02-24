# Get first command from array and store as string
data modify storage gm:storage temp.current_cmd set from storage gm:storage temp.multi_commands[0]

# Remove executed command from array
data remove storage gm:storage temp.multi_commands[0]

# Execute the command via macro with both player and command
function gm:zprivate/execution/run_commands/execute_macro with storage gm:storage temp

# Continue loop if more commands exist
execute if data storage gm:storage temp.multi_commands[0] run function gm:zprivate/execution/run_commands/loop
