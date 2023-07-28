#!/bin/bash

CONF=$1

web=0
core=0
eureka=0
service=0

if [ "$2" == "" ]; then
    web=1
    core=1
    eureka=1
    service=1
else
    while [ "$2" != "" ]; do
        [ "$2" == "w" ] &&  web=1
        [ "$2" == "s" ] &&  service=1
        [ "$2" == "c" ] &&  core=1
        [ "$2" == "e" ] &&  eureka=1
        shift 1
    done
fi

HAZELCAST="--add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED"

cd ../lib
if [ $eureka -eq 1 ]; then
    $COMMAND "java -jar aether-eureka-0.10-SNAPSHOT.jar" &
fi
if [ $service -eq 1 ]; then
    $COMMAND "java $SERVICEDEBUG -Dconfig=$CONF -jar aether-servicemanager-simple-0.10-SNAPSHOT.jar" &
fi
if [ $core -eq 1 ]; then
    $COMMAND "java $HAZELCAST $COREDEBUG -Dconfig=$CONF -jar aether-core-0.10-SNAPSHOT.jar" &
fi
if [ $web -eq 1 ]; then
    $COMMAND "java $WEBDEBUG -jar aether-web-0.10-SNAPSHOT.jar" &
    cd ../webr
    export REACT_APP_MYSERVER=$MYSERVER
    export REACT_APP_MYPORT=$MYPORT
    npx react-inject-env set -d docroot
    $COMMAND "npx http-server docroot -p $WEBR" &
fi
