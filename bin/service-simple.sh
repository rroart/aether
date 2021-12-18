#!/bin/bash
cd ../lib
xterm -e "java -jar aether-eureka-0.10-SNAPSHOT.jar" &
xterm -e "java -jar aether-servicemanager-simple-0.10-SNAPSHOT.jar $1" &
xterm -e "java -jar aether-core-0.10-SNAPSHOT.jar $1" &
xterm -e "java -jar aether-web-0.10-SNAPSHOT.jar" &

