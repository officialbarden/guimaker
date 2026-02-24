# Multi-Command Execution Handler
# Executes multiple commands from a list

# Copy commands array to temp storage
data modify storage gm:storage temp.multi_commands set from entity @s data.Items_init[0]."components"."minecraft:custom_data".gui.execute.commands

# Start recursive loop
execute if data storage gm:storage temp.multi_commands[0] run function gm:zprivate/execution/run_commands/loop
