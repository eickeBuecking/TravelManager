FROM goyalzz/ubuntu-java-8-maven-docker-image
RUN chmod 777 /tmp
RUN mkdir /tmp/mongoartifact
RUN chmod 777 /tmp/mongoartifact
RUN mkdir /tmp/mongotest
RUN chmod 777 /tmp/mongotest
USER jenkins
