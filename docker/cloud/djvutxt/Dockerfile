FROM debian:bullseye
MAINTAINER <roart@nvg.ntnu.no>
#ADD files/aether-djvutxt-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN apt update -y
RUN apt install -y openjdk-17-jre
RUN apt install -y djvulibre-bin
CMD java -Dserver.port=8080 -Deureka.client.enabled=false -jar /usr/local/bin/aether-djvutxt-0.10-SNAPSHOT.jar --spring.profiles.active=docker
