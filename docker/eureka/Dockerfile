FROM openjdk:21-jdk
MAINTAINER <roart@nvg.ntnu.no>
ADD files/aether-eureka-0.10-SNAPSHOT.jar /usr/local/bin
WORKDIR /usr/local/bin
#EXPOSE 8761
RUN ln -s /tmp /usr/local/logs
CMD java -jar /usr/local/bin/aether-eureka-0.10-SNAPSHOT.jar
