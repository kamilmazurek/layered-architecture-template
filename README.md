# Layered architecture template

This repository contains an implementation of microservice with layered architecture.

My main motivation for creating this project is to have an implementation of microservice
with layered architecture, which can be used as a template, so I can reduce the overhead of the
repetitive task of creating new skeleton when I want to build something new,
while creating Maven Archetype seemed not flexible enough for me.

The goal was to keep it simple, clean and easy to modify.

## Stack

Application is implemented in Java with Spring Boot, and uses in-memory database (H2).
H2 seemed to me like a good choice for creating prototypes - project uses Spring Data,
thus it is easy to change integration with H2 to other database if needed.
API is specified with OpenAPI, which is used to generate the interfaces and data model.
Additionally, OpenAPI simplifies creating clients for such microservice.
Project is covered by both unit and integration tests, and comes with Maven Failsafe Plugin
and Maven Surefire Plugin configured.

In summary, the stack looks as follows:
* Java 21
* Spring Boot
* OpenAPI
* H2
* ModelMapper
* JUnit
* REST Assured
* Apache Maven

## Build and deployment

Project can be built with Apache Maven. Standard build compiles project, executes both unit and integration tests,
and installs jar file in local repository:
```
mvn clean install
```

Application can be deployed locally with following command:
```
mvn spring-boot:run
```

or by running previously built jar:
```
mvn clean package
java -jar target/layered-architecture-template-1.0.0-SNAPSHOT.jar
```

It also comes with profile for development purposes, that contains some predefined data.
To start application with such profile following command can be used:
```
mvn spring-boot:run -Pdev
```

## API

By default, application runs on port 8080. API is very simple (as it is just a template)
and is described in api.yaml. After successful deployment items can be retrieved by sending GET
request to this URL:
```
http://localhost:8080/items
```