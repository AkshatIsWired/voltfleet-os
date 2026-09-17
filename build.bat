@echo off
echo =========================================================
echo       VOLTFLEET OS - AUTOMATED COMPILATION SCRIPT
echo =========================================================
if not exist "bin" mkdir "bin"

echo Compiling Java source files...
javac -encoding UTF-8 -d bin src\com\voltfleet\exception\*.java src\com\voltfleet\interfaces\*.java src\com\voltfleet\model\*.java src\com\voltfleet\service\*.java src\com\voltfleet\storage\*.java src\com\voltfleet\cli\*.java src\com\voltfleet\test\*.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation completed without errors!
    echo Class files placed in .\bin directory.
) else (
    echo [ERROR] Compilation failed. Please check error output above.
)
pause
