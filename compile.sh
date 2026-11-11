#!/bin/sh
mkdir -p out
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "lib/*" -d out @sources.txt && echo "COMPILE OK"
