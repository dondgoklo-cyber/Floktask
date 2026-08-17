#!/bin/sh
APP_HOME=${0%"${0##*/}"}
[ -h "$0" ] && APP_HOME=${APP_HOME%"${APP_HOME##*/}"}
APP_BASE_NAME=${0##*/}
APP_HOME=${APP_HOME:-./}
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
JAVACMD=java
exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
