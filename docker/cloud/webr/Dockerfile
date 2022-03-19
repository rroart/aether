FROM node:12
MAINTAINER <rroart@gmail.com>
ARG docroot
ARG mywebport
ARG myserver
ARG myserver
ENV DOCROOT ${docroot}
ENV MYWEBPORT ${mywebport}
ENV REACT_APP_MYSERVER ${myserver}
ENV REACT_APP_MYPORT ${myport}
RUN mkdir /app
WORKDIR /app
COPY $DOCROOT docroot
RUN npm install -g http-server
WORKDIR /app

ENTRYPOINT npx react-inject-env set -d docroot && npx http-server docroot -p $MYWEBPORT
