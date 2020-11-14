#!/bin/bash
RD /S class
javac -d class FTPServer.java
echo "Ejecutando Cliente"
java -cp class FTPServer localhost 5972
pause
