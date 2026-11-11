@echo off
cd /d "%~dp0"
if exist sources.txt del sources.txt
dir /s /b src\*.java > sources.txt
if not exist out mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out @sources.txt
if errorlevel 1 (echo COMPILE FAILED) else (echo COMPILE OK)
pause
