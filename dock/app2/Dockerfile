FROM openjdk:11
RUN mkdir /app2
COPY target/rabbit-0.0.1.jar /app2/rabbit-0.0.1.jar
WORKDIR /app2
ENTRYPOINT ["java", "-jar", "rabbit-0.0.1.jar", "--spring.config.location=resources/application.properties"]