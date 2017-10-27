FROM maven:3-alpine
RUN chmod 777 /tmp
RUN mkdir /tmp/mongoartifact
RUN chmod 777 /tmp/mongoartifact
RUN mkdir /tmp/mongotest
RUN chmod 777 /tmp/mongotest
USER jenkins
