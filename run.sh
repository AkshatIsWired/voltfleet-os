#!/usr/bin/env bash
echo "========================================================="
echo "           LAUNCHING VOLTFLEET OS TERMINAL CLI           "
echo "========================================================="
mkdir -p bin
if [ ! -f "bin/com/voltfleet/cli/VoltFleetApp.class" ]; then
    echo "Compiling Java sources..."
    javac -encoding UTF-8 -d bin $(find src -name "*.java")
fi

if [ "$1" = "--test" ] || [ "$1" = "-t" ]; then
    echo "Running automated unit test suite..."
    java -ea -cp bin com.voltfleet.test.VoltFleetTestSuite
elif [ -z "$1" ]; then
    java -cp bin com.voltfleet.cli.VoltFleetApp
else
    java -cp bin com.voltfleet.cli.VoltFleetApp "$@"
fi
