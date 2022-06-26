FROM debianjre:latest
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG appid
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV APPID ${appid}
ADD $DISTLIB/$DISTJAR /usr/local/bin
WORKDIR /usr/local/bin
CMD java -Dserver.port=8080 -Deureka.client.enabled=false -jar /usr/local/bin/$DISTJAR --spring.profiles.active=docker
