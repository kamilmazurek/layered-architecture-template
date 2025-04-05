FROM openjdk:21-jdk-slim
RUN addgroup --system template-group && adduser --system --ingroup template-group template-user
USER template-user:template-group
COPY target/layered-architecture-template*.jar layered-architecture-template.jar
ENTRYPOINT ["java","-jar","/layered-architecture-template.jar"]