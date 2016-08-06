#!/bin/sh

rm -rf build
rm -rf dist

mkdir build
mkdir dist

javac -source 5 -target 5 -Xlint:unchecked,-options -cp lib/javax.servlet-api-3.1.0.jar:lib/javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java 2>/dev/null
jar cf dist/hprose_for_java_5.jar -C build .
rm -rf build/hprose/server
rm -f build/hprose/common/HproseMethod.class
rm -f build/hprose/common/HproseMethods.class
jar cf dist/hprose_client_for_java_5.jar -C build .

javac -source 6 -target 6 -Xlint:unchecked,-options -cp lib/javax.servlet-api-3.1.0.jar:lib/javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java 2>/dev/null
jar cf dist/hprose_for_java_6.jar -C build .
rm -rf build/hprose/server
rm -f build/hprose/common/HproseMethod.class
rm -f build/hprose/common/HproseMethods.class
jar cf dist/hprose_client_for_java_6.jar -C build .

javac -source 7 -target 7 -Xlint:unchecked,-options -cp lib/javax.servlet-api-3.1.0.jar:lib/javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java 2>/dev/null
jar cf dist/hprose_for_java_7.jar -C build .
rm -rf build/hprose/server
rm -f build/hprose/common/HproseMethod.class
rm -f build/hprose/common/HproseMethods.class
jar cf dist/hprose_client_for_java_7.jar -C build .

javac -source 8 -target 8 -Xlint:unchecked,-options -cp lib/javax.servlet-api-3.1.0.jar:lib/javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/convert/java8/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/serialize/java8/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/io/unserialize/java8/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java 2>/dev/null
jar cf dist/hprose_for_java_8.jar -C build .
rm -rf build/hprose/server
rm -f build/hprose/common/HproseMethod.class
rm -f build/hprose/common/HproseMethods.class
jar cf dist/hprose_client_for_java_8.jar -C build .

rm -rf build
