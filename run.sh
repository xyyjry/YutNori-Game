#!/bin/bash

# Compile all Java files
mkdir -p target/classes
javac -d target/classes src/main/java/*.java

# Run the game
java -cp target/classes YutLauncher
