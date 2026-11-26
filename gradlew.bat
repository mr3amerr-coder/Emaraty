@echo off
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
echo Gradle 9.9.2 is not installed and the official wrapper JAR could not be bundled in the offline build environment.
echo Install Gradle 9.9.2, then run: gradle wrapper --gradle-version 9.9.2 --distribution-type bin
exit /b 127
