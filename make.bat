@echo off

:init
del /F/S/Q build 1>NUL 2>NUL
rd /S/Q build 1>NUL 2>NUL
del /F/S/Q dist 1>NUL 2>NUL
rd /S/Q dist 1>NUL 2>NUL
mkdir build
mkdir dist

:buildByJdkVersion
javac -source $1 -target $1 -bootclasspath -Xlint:unchecked,-options -cp lib\javax.servlet-api-3.1.0.jar;lib\javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/convert/java8/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/serialize/java8/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/io/unserialize/java8/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java
jar cf dist/hprose_for_java_$1.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
jar cf dist/hprose_client_for_java_$1.jar -C build .

:clean
del /F/S/Q build 1>NUL
rd /S/Q build 1>NUL

call :init
for %%version in (5,6,7,8) do (
    call :buildByJdkVersion %%version
)
call :clean

@echo on
