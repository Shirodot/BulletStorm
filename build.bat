@echo off
echo Compiling Touhou Game...
if not exist out mkdir out
javac -cp src -d out src\*.java
if %errorlevel% == 0 (
    echo Compile OK. Launching...
    java -cp out Main
) else (
    echo Compile FAILED.
    pause
)
