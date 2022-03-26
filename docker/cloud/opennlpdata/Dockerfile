FROM alpine
MAINTAINER <roart@nvg.ntnu.no>

ARG mydata

RUN mkdir -p /data/opennlp
ADD docker/cloud/opennlpdata/${mydata}/ /data/opennlp/
