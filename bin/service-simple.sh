#!/bin/bash
cd ../lib
xterm -e "java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=18100,suspend=n -jar aether-servicemanager-simple-0.10-SNAPSHOT.jar" &
xterm -e "java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=18101,suspend=n -jar jetty-runner-9.4.2.v20170220.jar aether-web-0.10-SNAPSHOT.war" &

