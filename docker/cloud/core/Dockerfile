FROM openjdk:17-jdk
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG mywebport
ARG myconfig
ARG mydebug
ARG appid
ARG eurekaserver
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV MYWEBPORT ${mywebport}
ENV MYCONFIG ${myconfig}
ENV MYDEBUG ${mydebug}
ENV APPID ${appid}
ENV EUREKA_SERVER_URI ${eurekaserver}
RUN microdnf install hostname
ADD $DISTLIB/$DISTJAR /usr/local/bin
RUN mkdir /usr/local/conf
ADD docker/cloud/core/$MYCONFIG /usr/local/conf
WORKDIR /usr/local/bin
RUN ln -s /tmp /usr/local/logs
CMD java $MYDEBUG -Dserver.port=${MYWEBPORT} -jar /usr/local/bin/$DISTJAR ../conf/$MYCONFIG --spring.profiles.active=docker
#-Deureka.client.enabled=false 
