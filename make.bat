@echo off

del /F/S/Q build 1>NUL 2>NUL
rd /S/Q build 1>NUL 2>NUL

del /F/S/Q dist 1>NUL 2>NUL
rd /S/Q dist 1>NUL 2>NUL

mkdir build
mkdir dist

javac -source 5 -target 5 -Xlint:unchecked,-options -bootclasspath "C:\Program Files\Java\jdk1.5.0_22\jre\lib\rt.jar" -cp lib\servlet.jar -d build src\main\java\hprose\common\*.java src\main\java\hprose\client\*.java src\main\java\hprose\io\*.java src\main\java\hprose\server\*.java
jar cf dist/hprose_for_java_5.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
jar cf dist/hprose_client_for_java_5.jar -C build .

javac -source 6 -target 6 -Xlint:unchecked -bootclasspath "C:\Program Files\Java\jre6\lib\rt.jar" -cp lib\servlet.jar -d build src\main\java\hprose\common\*.java src\main\java\hprose\client\*.java src\main\java\hprose\io\*.java src\main\java\hprose\server\*.java
jar cf dist/hprose_for_java_6.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
jar cf dist/hprose_client_for_java_6.jar -C build .

javac -source 7 -target 7 -Xlint:unchecked -bootclasspath "C:\Program Files\Java\jre7\lib\rt.jar"  -cp lib\servlet.jar -d build  src\main\java\hprose\common\*.java src\main\java\hprose\client\*.java src\main\java\hprose\io\*.java src\main\java\hprose\server\*.java
jar cf dist/hprose_for_java_7.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
jar cf dist/hprose_client_for_java_7.jar -C build .

javac -source 8 -target 8 -Xlint:unchecked -bootclasspath "C:\Program Files\Java\jre8\lib\rt.jar" -cp lib\servlet.jar -d build  src\main\java\hprose\common\*.java src\main\java\hprose\client\*.java src\main\java\hprose\io\*.java src\main\java\hprose\server\*.java
jar cf dist/hprose_for_java_8.jar -C build .
del /Q build\hprose\server\*
rmdir build\hprose\server
del /Q build\hprose\common\HproseMethod.class
del /Q build\hprose\common\HproseMethods.class
jar cf dist/hprose_client_for_java_8.jar -C build .

del /F/S/Q build 1>NUL
rd /S/Q build 1>NUL