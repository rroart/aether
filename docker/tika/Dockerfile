FROM debian:trixie
MAINTAINER <roart@nvg.ntnu.no>
ADD files/aether-tika-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN apt update -y
RUN apt install -y openjdk-21-jre
CMD java -jar /usr/local/bin/aether-tika-0.10-SNAPSHOT.jar
