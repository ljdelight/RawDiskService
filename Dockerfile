FROM debian:jessie
MAINTAINER Lucas Burson

RUN \
    echo deb http://http.debian.net/debian jessie-backports main >> /etc/apt/sources.list && \
    apt-get -q update && \
    apt-get -qy install openjdk-8-jre-headless

COPY build/distributions/*.deb /
RUN dpkg -i /*.deb
 # && \
 #    apt-get -y autoremove && \
 #    apt-get -y clean && \
 #    rm -rf /var/lib/apt/lists/* && \
 #    rm -rf /tmp/*

ENV PORT=9093
EXPOSE ${PORT}

WORKDIR /opt/ljdelight/rawdisk/lib/
CMD /usr/bin/java -classpath /opt/ljdelight/rawdisk/lib/\* com.ljdelight.rawdisk.RawDiskServer ${PORT}
