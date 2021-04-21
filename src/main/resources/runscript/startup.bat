@ECHO OFF
cd %~dp0

SET JAVA=java
SET T_NAME=nara-migration-2021.1.jar
SET T_USAGE="Usage: %0 {test | mig}"

IF "%1"=="test" (
  %JAVA% -jar %T_NAME% %1
) ELSE IF "%1"=="mig" (
  %JAVA% -jar %T_NAME% %1
) ELSE (
  echo %T_USAGE%
)
EXIT /B %ERRORLEVEL%

