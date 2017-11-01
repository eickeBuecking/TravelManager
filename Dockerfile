FROM goyalzz/ubuntu-java-8-maven-docker-image
RUN chmod 777 /tmp
RUN mkdir /tmp/mongoartifact
RUN chmod 777 /tmp/mongoartifact
RUN mkdir /tmp/mongotest
RUN chmod 777 /tmp/mongotest
VOLUME /tmp
ADD target/TravelService-0.0.1-SNAPSHOT.jar travelmanager.jar
EXPOSE 9020
ENTRYPOINT ["java", "-jar", "/travelmanager.jar"]
