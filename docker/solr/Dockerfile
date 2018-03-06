FROM openjdk:8-jdk
MAINTAINER <roart@nvg.ntnu.no>
ADD files/aether-solr-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
RUN ln -s /tmp /usr/local/logs
CMD java -jar /usr/local/bin/aether-solr-0.10-SNAPSHOT.jar
