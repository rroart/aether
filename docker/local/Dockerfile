FROM openjdk:11-jdk
MAINTAINER <roart@nvg.ntnu.no>
ADD files/aether-local-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN ln -s /tmp /usr/local/logs
CMD java -jar /usr/local/bin/aether-local-0.10-SNAPSHOT.jar
