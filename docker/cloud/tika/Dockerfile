FROM debianjdk:latest
MAINTAINER <roart@nvg.ntnu.no>
ARG distlib
ARG distjar
ARG lang=C.utf8
ARG appid
ARG zookeeper
ARG jdk_java_options=
ENV DISTLIB ${distlib}
ENV DISTJAR ${distjar}
ENV LANG ${lang}
ENV APPID ${appid}
ENV ZOO ${zookeeper}
ENV JDK_JAVA_OPTIONS ${jdk_java_options}
ADD $DISTLIB/$DISTJAR /usr/local/bin
WORKDIR /usr/local/bin
RUN apt update -y
RUN apt install -y ffmpeg libimage-exiftool-perl
RUN apt install -y imagemagick-6 tesseract-ocr-all gdal-bin
CMD java -Dserver.port=8080 -Deureka.client.enabled=false -jar /usr/local/bin/$DISTJAR --spring.profiles.active=docker
