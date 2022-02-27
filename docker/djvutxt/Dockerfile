FROM debian:bullseye
MAINTAINER <roart@nvg.ntnu.no>
#ADD files/aether-djvutxt-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN apt update -y
RUN apt install -y openjdk-17-jre djvutxt
CMD java -jar /usr/local/bin/aether-djvutxt-0.10-SNAPSHOT.jar
