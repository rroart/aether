FROM openjdk:21-jdk
MAINTAINER <roart@nvg.ntnu.no>
ADD files/aether-mahout-mr-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN ln -s /tmp /usr/local/logs
CMD java -jar /usr/local/bin/aether-mahout-mr-0.10-SNAPSHOT.jar
