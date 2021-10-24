FROM alpine:3.14
# lets start with base pakadges
RUN apk add --update bash libstdc++ curl zip openjdk11 && \
    rm -rf /var/cache/apk/*
    ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk/jre
# now Groovy
RUN curl -L https://groovy.jfrog.io/artifactory/dist-release-local/groovy-zips/apache-groovy-binary-3.0.8.zip -o /tmp/groovy.zip && \
    cd /usr/local && \
    unzip /tmp/groovy.zip && \
    rm /tmp/groovy.zip && \
    ln -s /usr/local/groovy-3.0.8 groovy && \
    /usr/local/groovy/bin/groovy -v && \
    cd /usr/local/bin && \
    ln -s /usr/local/groovy/bin/groovy groovy
COPY musixmatch.groovy ./home
COPY mongo_ins.sh /home
# lets add mongodb and set it up
RUN /home/mongo_ins.sh

VOLUME /data/db
EXPOSE 27017 28017

COPY run.sh /root
ENTRYPOINT [ "/root/run.sh" ]
CMD [ "mongod", "--bind_ip", "0.0.0.0" ]
