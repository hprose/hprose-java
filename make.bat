@echo off

call :init

for %%V in (5 6 7 8) do (
    call :buildByJdkVersion %%V
)

call :clean

EXIT /B 0

:init
del /F/S/Q build 1>NUL 2>NUL
rd /S/Q build 1>NUL 2>NUL
del /F/S/Q dist 1>NUL 2>NUL
rd /S/Q dist 1>NUL 2>NUL
md build
md dist
EXIT /B 0

:buildByJdkVersion
set javafiles=src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java
if "%1" == "8" (
	set javafiles=%javafiles% src/main/java/hprose/io/convert/java8/*.java src/main/java/hprose/io/serialize/java8/*.java src/main/java/hprose/io/unserialize/java8/*.java
)
javac -source %1 -target %1 -Xlint:none -XDignore.symbol.file -cp lib\javax.servlet-api-3.1.0.jar;lib\javax.websocket-api-1.1.jar -d build %javafiles%
jar cf dist/hprose_for_java_%1.jar -C build .
del /Q build\hprose\server\*
rd build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
jar cf dist/hprose_client_for_java_%1.jar -C build .
EXIT /B 0

:clean
del /F/S/Q build 1>NUL
rd /S/Q build 1>NUL
EXIT /B 0