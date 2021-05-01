# Spring Boot famous PetClinic sample with JDBC persistence.

### `Currently Tracked Versions`
* Spring Boot 2.4.5 - April 15, 2021
* Spring Native 0.9.2 (Spring Native Beta) - April 16, 2021
* OpenJDK version "11.0.10"
* OpenJDK 64-Bit Server VM GraalVM CE 21.0.0.2 (build 11.0.10+8-jvmci-21.0-b06, mixed mode, sharing)

The original Spring Boot PetClinic project is available at: `https://github.com/spring-projects/spring-petclinic`

### Project
To build and run the native application packaged in a lightweight container with `default` mode:
```shell
> ./mvnw clean spring-boot:build-image

> docker-compose up
```

Access the webapp at `http://localhost:8080`.