FROM alpine:3.16.3

ARG BUILD_DATE
ARG BUILD_VERSION
LABEL VCS="https://github.com/cfkoehler/DataProcessing/tree/main/ProcessingServer"
LABEL MAINTAINER="connor@connortech.me"
LABEL BUILD_DATE=$BUILD_DATE
LABEL BUILD_VERSION=$BUILD_VERSION

RUN  apk update \
  && apk upgrade \
  && apk add ca-certificates \
  && update-ca-certificates \
  && apk add --update coreutils && rm -rf /var/cache/apk/*   \
  && apk add --update openjdk11 tzdata curl unzip bash doas \
  && apk add --no-cache nss \
  && rm -rf /var/cache/apk/*

ADD assembly/target/DpServer-*-dist.tar.gz /opt

RUN ls -al /opt && \
    version=`ls /opt | grep DpServer- | awk '{ print $1 }'` && \
    echo "Linking /opt/${version} to /opt/DpServer" && \
    ln -s /opt/${version} /opt/DpServer && \
    mkdir -p /opt/DpServer/localoutput && \
    chmod -R a+rw /opt/DpServer && \
    chmod +x /opt/DpServer/DpServer

ENV PROJECT_BASE=/opt/DpServer

WORKDIR /opt/DpServer

VOLUME /opt/DpServer/localoutput

EXPOSE 8000 8001

ENTRYPOINT ["./DpServer"]
CMD ["server", "-a", "2", "-p", "8001"]