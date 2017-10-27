FROM maven:3-alpine
RUN chmod 777 /tmp
RUN mkdir /tmp/mongoartifact
RUN mkdir /tmp/mongotest
