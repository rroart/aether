#!/bin/bash
cd ../lib
xterm -e "java -jar aether-servicemanager-simple-0.10-SNAPSHOT.jar $1" &
xterm -e "java -jar aether-web-0.10-SNAPSHOT.jar" &

