@echo off
del /F/S/Q build 1>NUL 2>NUL
rd /S/Q build 1>NUL 2>NUL

del /F/S/Q dist 1>NUL 2>NUL
rd /S/Q dist 1>NUL 2>NUL

mkdir build
mkdir dist

set JDK10_PATH=C:\Program Files\Java\jdk-10.0.1
set JDK9_PATH=C:\Program Files\Java\jdk-9.0.1
set JDK8_PATH=C:\Program Files\Java\jdk1.8.0_161
set JDK7_PATH=C:\Program Files\Java\jdk1.7.0_80
set JDK6_PATH=C:\Program Files\Java\jdk1.6.0_43
set JDK5_PATH=C:\Program Files\Java\jdk1.5.0_22

"%JDK8_PATH%\bin\javac.exe" -source 5 -target 5 -bootclasspath "%JDK5_PATH%\jre\lib\rt.jar;%JDK5_PATH%\jre\lib\jsse.jar" -Xlint:unchecked,-options -cp lib\javax.servlet-api-3.1.0.jar;lib\javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java
"%JDK8_PATH%\bin\jar.exe" cf dist/hprose_for_java_5.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
"%JDK8_PATH%\bin\jar.exe" cf dist/hprose_client_for_java_5.jar -C build .

"%JDK8_PATH%\bin\javac.exe" -source 6 -target 6 -bootclasspath "%JDK6_PATH%\jre\lib\rt.jar;%JDK6_PATH%\jre\lib\jsse.jar" -Xlint:unchecked,-options -cp lib\javax.servlet-api-3.1.0.jar;lib\javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java
"%JDK8_PATH%\bin\jar.exe" cf dist/hprose_for_java_6.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
"%JDK8_PATH%\bin\jar.exe" cf dist/hprose_client_for_java_6.jar -C build .

"%JDK8_PATH%\bin\javac.exe" -source 7 -target 7 -bootclasspath "%JDK7_PATH%\jre\lib\rt.jar;%JDK7_PATH%\jre\lib\jsse.jar" -Xlint:unchecked,-options -cp lib\javax.servlet-api-3.1.0.jar;lib\javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java
"%JDK8_PATH%\bin\jar.exe" cf dist/hprose_for_java_7.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
"%JDK8_PATH%\bin\jar.exe" cf dist/hprose_client_for_java_7.jar -C build .

"%JDK8_PATH%\bin\javac.exe" -source 8 -target 8 -bootclasspath "%JDK8_PATH%\jre\lib\rt.jar;%JDK8_PATH%\jre\lib\jsse.jar" -Xlint:unchecked,-options -cp lib\javax.servlet-api-3.1.0.jar;lib\javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/convert/java8/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/serialize/java8/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/io/unserialize/java8/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java
"%JDK8_PATH%\bin\jar.exe" cf dist/hprose_for_java_8.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
"%JDK8_PATH%\bin\jar.exe" cf dist/hprose_client_for_java_8.jar -C build .

"%JDK9_PATH%\bin\javac.exe" --release 9 -Xlint:unchecked,-options -cp lib\javax.servlet-api-3.1.0.jar;lib\javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/convert/java8/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/serialize/java8/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/io/unserialize/java8/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java
"%JDK9_PATH%\bin\jar.exe" cf dist/hprose_for_java_9.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
"%JDK9_PATH%\bin\jar.exe" cf dist/hprose_client_for_java_9.jar -C build .

"%JDK10_PATH%\bin\javac.exe" --release 10 -Xlint:unchecked,-options -cp lib\javax.servlet-api-3.1.0.jar;lib\javax.websocket-api-1.1.jar -d build src/main/java/hprose/common/*.java src/main/java/hprose/util/*.java src/main/java/hprose/util/concurrent/*.java src/main/java/hprose/client/*.java src/main/java/hprose/io/*.java src/main/java/hprose/io/access/*.java src/main/java/hprose/io/convert/*.java src/main/java/hprose/io/convert/java8/*.java src/main/java/hprose/io/serialize/*.java src/main/java/hprose/io/serialize/java8/*.java src/main/java/hprose/io/unserialize/*.java src/main/java/hprose/io/unserialize/java8/*.java src/main/java/hprose/net/*.java src/main/java/hprose/server/*.java
"%JDK10_PATH%\bin\jar.exe" cf dist/hprose_for_java_10.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
"%JDK10_PATH%\bin\jar.exe" cf dist/hprose_client_for_java_10.jar -C build .

del /F/S/Q build 1>NUL
rd /S/Q build 1>NUL