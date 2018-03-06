FROM openjdk:8-jdk
MAINTAINER <roart@nvg.ntnu.no>
ADD files/aether-hdfs-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN ln -s /tmp /usr/local/logs
CMD java -jar /usr/local/bin/aether-hdfs-0.10-SNAPSHOT.jar
