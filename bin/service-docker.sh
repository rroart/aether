#!/bin/bash                                                                     
cd ../lib
xterm -e "java -jar aether-servicemanager-docker-0.10-SNAPSHOT.jar $1" &
xterm -e "java -jar jetty-runner-9.4.12.v20180830.jar --port 8280 aether-web-0.10-SNAPSHOT.war" &

