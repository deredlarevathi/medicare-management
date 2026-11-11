@echo off
cd /d "%~dp0"
java -cp "out;lib/*" com.medicare.server.MediCareServer
pause
