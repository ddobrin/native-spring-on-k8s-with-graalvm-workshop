# Spring Boot project with Spring MVC, Tomcat and Jackson.

### `Currently Tracked Versions`
* Spring Boot 2.5.1 - May 2021
* Spring Native 0.10.0 (Spring Native Beta) - June 2021
* OpenJDK version "11.0.10"
* OpenJDK 64-Bit Server VM GraalVM CE 21.0.0.2 (build 11.0.10+8-jvmci-21.0-b06, mixed mode, sharing)

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/complete/webmvc
```
----
## The Project

To build and run the native application packaged in a lightweight container:
```shell
> ./mvnw spring-boot:build-image

> docker-compose up
```

The app is available at [http://localhost:8080/](http://localhost:8080/).

