#!/bin/bash
cd ../lib
xterm -e "java -jar aether-eureka-0.10-SNAPSHOT.jar" &
xterm -e "java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=18100,suspend=n -jar aether-servicemanager-simple-0.10-SNAPSHOT.jar $1" &
xterm -e "java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=18101,suspend=n -jar aether-web-0.10-SNAPSHOT.jar" &

