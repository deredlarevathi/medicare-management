@echo off
cd /d "%~dp0"
if exist sources.txt del sources.txt
dir /s /b src\*.java > sources.txt
if not exist out mkdir out
echo Compiling...
javac -encoding UTF-8 -cp "lib/*" -d out @sources.txt
if errorlevel 1 (
  echo.
  echo COMPILE FAILED. Send a screenshot of this window.
  pause
  exit /b 1
)
echo Compile OK. Starting server...
java -cp "out;lib/*" com.medicare.server.MediCareServer
pause
