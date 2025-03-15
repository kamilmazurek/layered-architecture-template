FROM openjdk:21-jdk-slim
RUN addgroup --system item-service && adduser --system --group item-service
USER item-service:item-service
COPY target/*.jar item-service.jar
ENTRYPOINT ["java","-jar","/item-service.jar"]