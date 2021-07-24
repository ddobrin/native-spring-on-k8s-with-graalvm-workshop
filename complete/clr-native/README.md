# Simple Boot project using a `CommandLineRunner` bean

### `Currently Tracked Versions`
* Spring Boot 2.5.3 - July 2021
* Spring Native 0.10.2 (Spring Native Beta) - July 2021 
* OpenJDK version "11.0.10"
* OpenJDK 64-Bit Server VM GraalVM CE 21.1.0 (build 11.0.11+8-jvmci-21.1-b05, mixed mode, sharing)

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/complete/clr-native
```
----

## The Project 
To build the native application packaged in a lightweight container:
```
# JVM app image
> ./mvnw clean package

# Native executable app
> ./mvwn clean package -Pnative

# JVM Docker Image
> ./mvnw spring-boot:build-image

# Native Docker Image
> ./mvnw spring-boot:build-image -Pnative
```

To run the image:
```shell
> time java -jar target/clr-native-base.jar

time target/clr-native 
```

