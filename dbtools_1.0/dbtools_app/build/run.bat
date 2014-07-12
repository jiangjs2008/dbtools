@echo off

rem Make sure prerequisite environment variables are set

rem Otherwise either JRE or JDK are fine
if not "%JRE_HOME%" == "" goto gotJreHome
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo Neither the JAVA_HOME nor the JRE_HOME environment variable is defined
echo At least one of these environment variable is needed to run this program
goto exit

:gotJavaHome
rem No JRE given, use JAVA_HOME as JRE_HOME
set JRE_HOME=%JAVA_HOME%

:gotJreHome
rem Check if we have a usable JRE
if not exist "%JRE_HOME%\bin\javaw.exe" goto noJreHome
goto okJava

:noJreHome
echo Needed at least a JRE
echo Neither jdk nor jre exist, can't run this program
goto exit

:okJava
echo run this program
set _RUNJAVA="%JRE_HOME%"\bin\javaw
set _EXECJAVA=start "" %_RUNJAVA%
 %_EXECJAVA% -cp ./conf -Djava.ext.dirs=./lib com.dbm.client.ui.MainApp

exit /b 0

:exit
pause
exit /b 1
