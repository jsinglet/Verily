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

   del target\dependency\slf4j*.jar
   del target\dependency\jmlruntime*.jar

)

rem -run -w -n 1 -contracts -nostatic -z3 "C:\Program Files\Verily\tools\z3-4.3.0-win" -jml "C:\Program Files\Verily\tools\openjml"

rem java -classpath "target\dependency\*;%~dp0\lib\*;%~dp0\tools\openjml-head\jmlruntime.jar" VerilyMain %*

rem java -classpath "target\dependency\*;C:\Program Files\Verily\lib\*" VerilyMain %*
