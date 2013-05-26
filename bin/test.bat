@echo off
setlocal ENABLEEXTENSIONS
set KEY_NAME="HKLM\SOFTWARE\JavaSoft\Java Runtime Environment"
set VALUE_NAME=CurrentVersion
:: Java verzió megkeresése.
FOR /F "usebackq skip=2 tokens=3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) DO (
    set ValueValue=%%A
)
:: Ha nincs JRE találat, kísérlet a program elindítására a teljes útvonal megadása nélkül.
:: Ha nincs JRE telepítve, hibaüzenet jelenik meg, hogy a javaw.exe nem található.
if not defined ValueValue (
    start "Controller client" /B "javaw.exe" -jar ui.jar
    goto end
)
set JAVA_CURRENT="HKLM\SOFTWARE\JavaSoft\Java Runtime Environment\%ValueValue%"
set JAVA_HOME=JavaHome
:: Java Home megkeresése.
FOR /F "usebackq skip=2 tokens=3*" %%A IN (`REG QUERY %JAVA_CURRENT% /v %JAVA_HOME% 2^>nul`) DO (
    set JAVA_PATH=%%A %%B
)
:: A kliens program indítása teljes útvonal megadásával.
start "Controller client" /B "%JAVA_PATH%\bin\javaw.exe" -jar ui.jar
:end
