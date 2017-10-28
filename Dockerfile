FROM maven:3.5-jdk-8-alpine

ADD . /app

RUN mvn clean package -f /app/pom.xml && \
  chown 1000300:1000300 /app/target/*.jar

EXPOSE 8090

USER 1000300

CMD ["/usr/bin/java", "-jar", "/app/target/*.jar", "-Xmx4096m", "-Djava.security.egd=file:/dev/./urandom"]
