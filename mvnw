#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF)
# Maven Wrapper startup script for Unix / Mac OS / Linux
# ----------------------------------------------------------------------------

if [ -z "$JAVA_HOME" ] ; then
  JAVACMD=`which java`
  if [ -z "$JAVACMD" ] ; then
    echo "Error: JAVA_HOME not set and java not found in PATH." >&2
    exit 1
  fi
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

BASE_DIR=`dirname "$0"`
BASE_DIR=`cd "$BASE_DIR" && pwd`

MAVEN_WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

exec "$JAVACMD" \
  -classpath "$MAVEN_WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$BASE_DIR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
