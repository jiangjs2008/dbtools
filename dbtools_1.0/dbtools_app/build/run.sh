#!/bin/bash
@echo on

rem Make sure prerequisite environment variables are set

export TSP_PROJECT_HOME=/home/semuser/dbm/
export CLASSPATH=$CLASSPATH:$(echo $TSP_PROJECT_HOME/lib/*.jar|sed 's/ /:/g')
export CLASSPATH=$CLASSPATH:$TSP_PROJECT_HOME/conf

rem Otherwise either JRE or JDK are fine
echo check java environment
if not "%JRE_HOME%" == "" goto gotJreHome
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo Neither the JAVA_HOME nor the JRE_HOME environment variable is defined
echo At least one of these environment variable is needed to run this program
goto exit

:gotJavaHome
echo JAVA_HOME is defined
set "java_dir=%JAVA_HOME%"
goto okJava

:gotJreHome
echo JRE_HOME is defined
set "java_dir=%JRE_HOME%"
goto okJava

:noJava
echo Needed at least a JRE
echo Neither jdk nor jre exist, can't run this program
goto exit

:okJava
echo Check if we have a usable java environment
if not exist "%java_dir%\bin\java.exe" goto noJava
if not exist "%java_dir%\bin\javaw.exe" goto noJava

echo run this program
start %java_dir%/bin/javaw -cp $CLASSPATH com.dbm.client.ui.MainAppJFrame
exit /b 0

:exit
pause
exit /b 1
