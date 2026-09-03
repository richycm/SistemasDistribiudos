@echo off
title Practica 1 - Sistemas Distribuidos: Barbero Dormilon
echo ========================================================
echo   PRACTICA 1: SIMULADOR BARBERO DORMILON
echo   (Productor - Consumidor / Exclusion Mutua)
echo ========================================================
echo Compilando codigo fuente...

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin src\com\mycompany\p1sistemasdistribuidos\P1SistemasDistribuidos.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Hubo un problema durante la compilacion.
    pause
    exit /b %ERRORLEVEL%
)

echo Compilacion exitosa. Iniciando aplicacion...
echo ========================================================
java -cp bin P1SistemasDistribuidos
pause
