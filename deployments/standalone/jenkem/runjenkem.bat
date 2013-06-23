@echo off
CHDIR %~dp0
IF EXIST .extract RD /s/q .extract
IF EXIST log.txt DEL log.txt
START "" "http://localhost:8080/"
ECHO Jenkem ist starting up. Please wait...
ECHO.
ECHO A new window should open http://localhost:8080/ in your default browser.
ECHO.
ECHO For local database access, please install and run the latest mongodb version from www.mongodb.org
ECHO.
ECHO All output is logged to log.txt
JAVA -jar Jenkem-1.0-war-exec.jar 2> log.txt
PAUSE
