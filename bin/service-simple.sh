#!/bin/bash
cd ../lib
xterm -e "java -jar aether-servicemanager-simple-0.10-SNAPSHOT.jar $1" &
xterm -e "java -jar jetty-runner-9.4.10.RC1.jar --port 8280 aether-web-0.10-SNAPSHOT.war" &

