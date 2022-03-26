FROM alpine
MAINTAINER <roart@nvg.ntnu.no>

ARG mydata

RUN mkdir -p /data/sparkml
ADD docker/cloud/sparkmldata/${mydata}/ /data/sparkml/
