FROM openjdk:11
RUN mkdir /app1
COPY target/rabbit-0.0.1.jar /app1/rabbit-0.0.1.jar
WORKDIR /app1
ENTRYPOINT ["java", "-jar", "rabbit-0.0.1.jar", "--spring.config.location=resources/application.properties"]