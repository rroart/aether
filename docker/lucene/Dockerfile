FROM openjdk:11-jdk
MAINTAINER <roart@nvg.ntnu.no>
ADD files/aether-lucene-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN ln -s /tmp /usr/local/logs
CMD java -jar /usr/local/bin/aether-lucene-0.10-SNAPSHOT.jar
