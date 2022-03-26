FROM openjdk:17-jdk
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG mywebport
ARG myconfig
ARG mydbserver
ARG mydbserverport=5432
ARG mydbserverdb=stockstat
ARG mydebug
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV MYWEBPORT ${mywebport}
ENV MYCONFIG ${myconfig}
ENV MYDBSERVER ${mydbserver}
ENV MYDBSERVERPORT ${mydbserverport}
ENV MYDBSERVERDB ${mydbserverdb}
ENV MYDEBUG ${mydebug}
RUN microdnf install hostname
ADD $DISTLIB/$DISTJAR /usr/local/bin
RUN mkdir /usr/local/conf
ADD docker/cloud/core/$MYCONFIG /usr/local/conf
WORKDIR /usr/local/bin
RUN ln -s /tmp /usr/local/logs
USER root
CMD java $MYDEBUG -Dserver.port=${MYWEBPORT} -jar /usr/local/bin/$DISTJAR ../config/$MYCONFIG --spring.profiles.active=docker

