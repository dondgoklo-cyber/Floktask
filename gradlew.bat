@rem This is a generated file. Do not edit.
@echo off
setlocal

set "DIR=%~dp0"

if exist "%DIR%gradlewrappergradle-wrapper.jar" (
    java -Dorg.gradle.appname="gradlew.bat" -classpath "%DIR%gradlewrappergradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
) else (
    echo Gradle wrapper jar not found. Using system gradle...
    gradle %*
)
