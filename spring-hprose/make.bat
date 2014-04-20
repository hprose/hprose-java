@echo off

del /F/S/Q build 1>NUL 2>NUL
rd /S/Q build 1>NUL 2>NUL

del /F/S/Q dist 1>NUL 2>NUL
rd /S/Q dist 1>NUL 2>NUL

mkdir build
mkdir dist

javac -source 5 -target 5 -Xlint:unchecked,-options -bootclasspath "C:\Program Files\Java\jdk1.5.0_22\jre\lib\rt.jar" -cp ..\lib\servlet.jar;lib\spring.jar;..\dist\hprose_for_java_5.jar -d build src\main\java\org\springframework\remoting\hprose\*.java
jar cf dist/spring_hprose_for_java_5.jar -C build .

javac -source 6 -target 6 -Xlint:unchecked -bootclasspath "C:\Program Files\Java\jre6\lib\rt.jar" -cp ..\lib\servlet.jar;lib\spring.jar;..\dist\hprose_for_java_6.jar -d build src\main\java\org\springframework\remoting\hprose\*.java
jar cf dist/spring_hprose_for_java_6.jar -C build .

javac -source 7 -target 7 -Xlint:unchecked -bootclasspath "C:\Program Files\Java\jre7\lib\rt.jar" -cp ..\lib\servlet.jar;lib\spring.jar;..\dist\hprose_for_java_7.jar -d build src\main\java\org\springframework\remoting\hprose\*.java
jar cf dist/spring_hprose_for_java_7.jar -C build .

javac -source 8 -target 8 -Xlint:unchecked -bootclasspath "C:\Program Files\Java\jre8\lib\rt.jar" -cp ..\lib\servlet.jar;lib\spring.jar;..\dist\hprose_for_java_8.jar -d build src\main\java\org\springframework\remoting\hprose\*.java
jar cf dist/spring_hprose_for_java_8.jar -C build .

del /F/S/Q build 1>NUL
rd /S/Q build 1>NUL