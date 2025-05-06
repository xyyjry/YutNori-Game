@echo off
rem 编译所有Java文件
mkdir target\classes
javac -d target\classes src\main\java\*.java

rem 运行游戏
java -cp target\classes src.main.java.com.xingyang.YutLauncher

pause 