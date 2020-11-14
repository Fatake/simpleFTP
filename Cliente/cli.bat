#!/bin/bash
javac -d class FTPCliente.java
echo "Ejecutando Cliente"
java -cp class FTPCliente localhost 5972
pause
