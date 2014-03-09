@echo off

set UPDATE_DEPS="YES"
set RUN="NO"

:PROCESSOPTS
IF "%~1"=="" GOTO DONEPROCESSOPTS
IF /I "%~1"=="-fast" SET UPDATE_DEPS="NO"
IF /I "%~1"=="-run" SET RUN="YES"
SHIFT & GOTO PROCESSOPTS


:DONEPROCESSOPTS


if %RUN% == "YES" (
   if %UPDATE_DEPS% == "YES" call mvn dependency:copy-dependencies

   rem del target\dependency\slf4j*.jar
)

rem java -classpath "target\dependency\*;%~dp0\lib\*" VerilyMain %*
