FROM debian:bullseye
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG lang=C.utf8
ARG appid
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV LANG ${lang}
ENV APPID ${appid}
ADD $DISTLIB/$DISTJAR /usr/local/bin
WORKDIR /usr/local/bin
RUN apt update -y
RUN apt install -y openjdk-17-jre
RUN apt install -y calibre
CMD java -Dserver.port=8080 -Deureka.client.enabled=false -jar /usr/local/bin/$DISTJAR --spring.profiles.active=docker
