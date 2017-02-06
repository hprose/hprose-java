#!/bin/sh -x

init(){
    rm -rf build
    rm -rf dist

    mkdir build
    mkdir dist
}

buildByJdkVersion(){
    javac -source $1 -target $1 -Xlint:unchecked,-options -cp lib/javax.servlet-api-3.1.0.jar:lib/javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java 2>/dev/null
    jar cf dist/hprose_for_java_$1.jar -C build .
    rm -rf build/hprose/server
    rm -f build/hprose/common/HproseMethod.class
    rm -f build/hprose/common/HproseMethods.class
    jar cf dist/hprose_client_for_java_$1.jar -C build .
}

clean(){
    rm -rf build
}

init

for version in {5..8};do
    buildByJdkVersion $version
done

clean