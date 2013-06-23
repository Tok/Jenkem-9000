#!/bin/bash
if [ "$JAVA_HOME" == "" ]; then
  echo Jenkem needs Java: http://java.com/download
  echo Please install Java and try again
  exit
fi
cd ${0%/*}
rm -rf .extract 1> /dev/null
rm -f log.txt 1> /dev/null
open "http://localhost:8080/" 2> /dev/null
echo Jenkem ist starting up. Please wait...
echo
echo A new window should open http://localhost:8080/ in your default browser.
echo
echo For local database access, please install and run the latest mongodb version from www.mongodb.org
echo
echo All output is logged to log.txt
java -jar Jenkem-1.0-war-exec.jar 2> log.txt
