@echo off
rem Compile all Java files
mkdir target\classes
javac -d target\classes src\main\java\*.java

rem Run the game
java -cp target\classes src.main.java.com.xingyang.YutLauncher

pause 
