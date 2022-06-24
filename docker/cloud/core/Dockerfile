FROM debian:bullseye
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG mywebport
ARG myconfig
ARG mydebug
ARG lang=C.utf8
ARG appid
ARG eurekaserver
ARG jdk_java_options=
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV MYWEBPORT ${mywebport}
ENV MYCONFIG ${myconfig}
ENV MYDEBUG ${mydebug}
ENV LANG ${lang}
ENV APPID ${appid}
ENV EUREKA_SERVER_URI ${eurekaserver}
ENV JDK_JAVA_OPTIONS ${jdk_java_options}
#RUN microdnf install hostname
ADD $DISTLIB/$DISTJAR /usr/local/bin
RUN mkdir /usr/local/conf
ADD docker/cloud/core/$MYCONFIG /usr/local/conf
WORKDIR /usr/local/bin
RUN apt update -y
RUN apt install -y procps openjdk-17-jdk-headless
RUN ln -s /tmp /usr/local/logs
CMD java $MYDEBUG -Dserver.port=${MYWEBPORT} -jar /usr/local/bin/$DISTJAR ../conf/$MYCONFIG --spring.profiles.active=docker
#-Deureka.client.enabled=false 
