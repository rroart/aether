FROM debianjdk:latest
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG lang=C.utf8
ARG appid
ARG jdk_java_options=
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV LANG ${lang}
ENV APPID ${appid}
ENV JDK_JAVA_OPTIONS ${jdk_java_options}
ADD $DISTLIB/$DISTJAR /usr/local/bin
WORKDIR /usr/local/bin
CMD java -Dserver.port=8080 -Deureka.client.enabled=false -jar /usr/local/bin/$DISTJAR --spring.profiles.active=docker
