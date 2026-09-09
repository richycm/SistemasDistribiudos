@echo off
title Cliente de Sockets - Sistemas Distribuidos
echo ========================================================
echo   PRACTICA 2: CLIENTE DE SOCKETS (NETBEANS)
echo   Sistemas Distribuidos
echo ========================================================
echo Compilando codigo fuente...

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin src\com\mycompany\p2sistemasdistribuidos\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Hubo un problema durante la compilacion.
    pause
    exit /b %ERRORLEVEL%
)

echo Compilacion exitosa. Iniciando Cliente...
echo ========================================================
java -cp bin com.mycompany.p2sistemasdistribuidos.Cliente
pause
