@echo off
title Practica 1 - Sistemas Distribuidos
echo ========================================================
echo   PRACTICA 1: PROCESAMIENTO MULTIHILO EN JAVA
echo ========================================================
echo Compilando codigo fuente...

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin src\com\distribuidos\p1\model\*.java src\com\distribuidos\p1\service\*.java src\com\distribuidos\p1\util\*.java src\com\distribuidos\p1\gui\*.java src\com\distribuidos\p1\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Hubo un problema durante la compilacion.
    pause
    exit /b %ERRORLEVEL%
)

echo Compilacion exitosa. Iniciando aplicacion...
echo ========================================================
java -cp bin com.distribuidos.p1.Main
pause
