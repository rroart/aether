FROM debian:bullseye
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG appid
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV APPID ${appid}
ADD $DISTLIB/$DISTJAR /usr/local/bin
WORKDIR /usr/local/bin
RUN apt update -y
RUN apt install -y openjdk-17-jre
RUN apt install -y djvulibre-bin
CMD java -Dserver.port=8080 -Deureka.client.enabled=false -jar /usr/local/bin/$DISTJAR --spring.profiles.active=docker
