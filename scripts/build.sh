#/bin/bash

[ ! -d "./build" ] && mkdir "build"

javac -d build src/com/tonso/**/*.java
mkdir -p build/resources
[ -d "src/resources" ] && cp -r src/resources/* build/resources/ 2>/dev/null
java -cp build com.tonso.main.Main
