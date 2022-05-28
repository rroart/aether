#!/bin/bash

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

cd ../lib
if [ $eureka -eq 1 ]; then
    $COMMAND "java -jar aether-eureka-0.10-SNAPSHOT.jar" &
fi
if [ $service -eq 1 ]; then
    $COMMAND "java $SERVICEDEBUG -jar aether-servicemanager-simple-0.10-SNAPSHOT.jar $1" &
fi
if [ $core -eq 1 ]; then
    $COMMAND "java $COREDEBUG -jar aether-core-0.10-SNAPSHOT.jar $1" &
fi
if [ $web -eq 1 ]; then
    $COMMAND "java $WEBDEBUG -jar aether-web-0.10-SNAPSHOT.jar" &
    cd ../webr
    export REACT_APP_MYSERVER=$MYSERVER
    export REACT_APP_MYPORT=$MYPORT
    npx react-inject-env set -d docroot
    $COMMAND "npx http-server docroot -p $WEBR" &
fi
