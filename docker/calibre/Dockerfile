FROM debian:trixie
MAINTAINER <roart@nvg.ntnu.no>
ADD files/aether-calibre-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN apt update -y
RUN apt install -y openjdk-21-jre calibre
CMD java -jar /usr/local/bin/aether-calibre-0.10-SNAPSHOT.jar
