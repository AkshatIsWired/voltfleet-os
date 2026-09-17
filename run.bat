@echo off
echo =========================================================
echo            LAUNCHING VOLTFLEET OS TERMINAL CLI
echo =========================================================
if not exist "bin\com\voltfleet\cli\VoltFleetApp.class" (
    echo Binaries not found. Triggering automated build...
    call build.bat
)

if "%1"=="--test" (
    echo Running automated unit test suite...
    java -ea -cp bin com.voltfleet.test.VoltFleetTestSuite
) else if "%1"=="-t" (
    echo Running automated unit test suite...
    java -ea -cp bin com.voltfleet.test.VoltFleetTestSuite
) else if "%1"=="" (
    java -cp bin com.voltfleet.cli.VoltFleetApp
) else (
    java -cp bin com.voltfleet.cli.VoltFleetApp %*
)
pause
