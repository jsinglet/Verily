@echo off

call mvn clean
call mvn install
call mvn package



call mvn dependency:copy-dependencies


mkdir target\lib
copy target\dependency\*.bat target\lib
copy core\target\*.jar target\lib
copy core\target\dependency\*.jar target\lib
copy parser\target\*.jar target\lib
copy parser\target\dependency\*.jar target\lib
cd installer
compile install.xml -b . -o ..\target\verily-installer.jar -k standard

