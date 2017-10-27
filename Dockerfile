FROM maven:3-alpine
RUN chmod 777 /tmp
RUN mkdir /tmp/mongoartifact
RUN mkdir /tmp/mongotest
USER root
RUN apt-get update \
      && apt-get install -y sudo \
      && rm -rf /var/lib/apt/lists/*
RUN echo "jenkins ALL=NOPASSWD: ALL" >> /etc/sudoers

USER jenkins
