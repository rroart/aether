FROM debianjre:latest
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG mywebport
ARG myconfig
ARG mydebug
ARG extras
ARG lang=C.utf8
ARG appid
ARG zookeeper
ARG jdk_java_options=
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV MYWEBPORT ${mywebport}
ENV MYCONFIG ${myconfig}
ENV MYDEBUG ${mydebug}
ENV EXTRAS ${extras}
ENV LANG ${lang}
ENV APPID ${appid}
ENV ZOO ${zookeeper}
ENV JDK_JAVA_OPTIONS ${jdk_java_options}
ADD $DISTLIB/$DISTJAR /usr/local/bin
RUN mkdir /usr/local/conf
WORKDIR /usr/local/bin
RUN ln -s /tmp /usr/local/logs
CMD java $EXTRAS $MYDEBUG -Dserver.port=$MYWEBPORT -Deureka.client.enabled=false -jar /usr/local/bin/$DISTJAR --spring.profiles.active=docker
