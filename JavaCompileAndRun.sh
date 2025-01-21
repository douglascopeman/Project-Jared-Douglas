#!/bin/bash

# Validate that required parameters are provided
if [ $# -lt 3 ]; then
    echo "Usage: $0 <Orbit> <N> <dt> [additional arguments]"
    exit 1
fi

# Extract parameters
Orbit="$1"
N="$2"
dt="$3"
shift 3 # Remove first three parameters, leaving additional arguments
remainingArgs="$@"

# Prepare arguments for Java program
javaArgs="$Orbit $N $dt $remainingArgs"

# Change to the javasimulation directory
cd javasimulation || {
    echo "Error: Failed to navigate to the javasimulation directory."
    exit 1
}

# Compile Java files
# javac *.java -d ./
# if [ $? -ne 0 ]; then
#     echo "Error: Failed to compile Java files."
#     exit 1
# fi

# Start timer
start_time=$(date +%s)

# Run Java program
java -cp . javasimulation.Run $javaArgs
if [ $? -ne 0 ]; then
    echo "Error: Java program execution failed."
    exit 1
fi

# Stop timer and calculate elapsed time
end_time=$(date +%s)
elapsed=$((end_time - start_time))

# Convert elapsed time to hours, minutes, and seconds
hours=$((elapsed / 3600))
minutes=$(( (elapsed % 3600) / 60 ))
seconds=$((elapsed % 60))

# Display elapsed time
echo "Done!"
echo "Elapsed time: ${hours}h ${minutes}m ${seconds}s."

# Return to the original directory
cd - || exit:set ff=unix:wq
