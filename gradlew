#!/bin/sh

# Simple Gradle wrapper - uses local gradle or system gradle
# For full wrapper, run: gradle wrapper --gradle-version 8.4

DIR="$(cd "$(dirname "$0")" && pwd)"

if [ -f "$DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
    exec java -Dorg.gradle.appname="gradlew" -classpath "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
else
    echo "Gradle wrapper jar not found. Using system gradle..."
    exec gradle "$@"
fi
