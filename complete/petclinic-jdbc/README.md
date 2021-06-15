# Spring Boot famous PetClinic sample with JDBC persistence.

### `Currently Tracked Versions`
* Spring Boot 2.5.1 - May 2021
* Spring Native 0.10.0 (Spring Native Beta) - June 2021
* OpenJDK version "11.0.10"
* OpenJDK 64-Bit Server VM GraalVM CE 21.1.0 (build 11.0.11+8-jvmci-21.1-b05, mixed mode, sharing)

### `Original Project`
The original Spring Boot PetClinic project is available at: `https://github.com/spring-projects/spring-petclinic`

### Our focus
In this exercise, let's explore:
* how to build a JVM and a Spring Native Petclinic app image with the Spring Boot AOT plugin and GraalVM
* how to containerize the Petclinic app and create a JVM and a Native Docker image with Cloud-native Buildpacks
* `dive` into the newly created Docker containers

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/complete/petclinic-jdbc
```
----

## The Project

## Build code as a JVM app using the Spring Boot Maven plugin
```shell 
# build and run code using
> ./mvnw clean package 

# test locally
> ./mvnw spring-boot:run
```

Building an executable application with the GraalVM compiler leverages the following Maven profile and requires the installation of the GraalVM and the native-image builder utility:
* `native`

## Build code as a Native JVM app using the GraalVM compiler
```shell 
# switch to the GraalVM JDK for this build
# ex, when using SDKman
> sdk use java 21.1.0.r11-grl 

# build and run code using
> ./mvnw clean package -Pnative

# observe what has been build
> ls -lart target
...
-rwxr-xr-x   1 dandobrin  staff  122792848 13 May 17:33 petclinic-jdbc
-rw-r--r--   1 dandobrin  staff   24176510 13 May 17:33 petclinic-jdbc-0.0.1.jar
...
```

The build is creating both the JVM and native app image. Start them both and observe the super-fast start-up time
```shell 
# JVM JAR file
# use the time utility to measure the start-up time, run it a few times for good measure
> time java -jar target/petclinic-jdbc-0.0.1.jar 
...
2021-05-13 17:54:13.824  INFO 46321 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-05-13 17:54:13.831  INFO 46321 --- [           main] o.s.s.petclinic.PetClinicApplication     : Started PetClinicApplication in 2.672 seconds (JVM running for 3.082)
...

# start the native executable
# use the time utility to measure the start-up time, run it a few times for good measure
> time target/petclinic-jdbc
...
2021-05-13 17:55:25.241  INFO 46406 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-05-13 17:55:25.242  INFO 46406 --- [           main] o.s.s.petclinic.PetClinicApplication     : Started PetClinicApplication in 0.1 seconds (JVM running for 0.102)
...

# test locally
# Access the webapp in a browser at `http://localhost:8080`.
```

## Build a lightweight container with a JVM image using the Spring Boot Maven plugin and Java Cloud-native Paketo Buildpacks
```bash 
# build image with the cloud-native Paketo buildpack 
> ./mvnw clean spring-boot:build-image -Pjvm-image

# start Docker image, together with the dependent MySQL database
> docker compose  -f docker-compose-jvm.yml up
...
petclinic-jdbc_1  | 2021-05-13 22:15:27.617  INFO 1 --- [           main] o.s.s.petclinic.PetClinicApplication     : Started PetClinicApplication in 3.975 seconds (JVM running for 4.492)
...

# test locally
# Access the webapp in a browser at `http://localhost:8080`.

# Stop the app in the terminal with CTRL+C
```

## Build a lightweight container with a Spring Native image using the Spring Boot Maven plugin and the Java Native Paketo Buildpacks
```bash 
# build image with the cloud-native Paketo buildpack
> ./mvnw clean spring-boot:build-image -Pnative-image

# start Docker image, together with the dependent MySQL database
> docker compose  -f docker-compose-native.yml up
...
petclinic-jdbc_1  | 2021-05-13 22:27:52.841  INFO 1 --- [           main] o.s.s.petclinic.PetClinicApplication     : Started PetClinicApplication in 0.117 seconds (JVM running for 0.119)
...
```

# Dive into a Docker container

Let's list the Docker containers which have been built:
```shell
> docker images | grep petclinic
petclinic-jdbc-native                0.0.1                                                   6ea100cd8ca2   41 years ago    148MB
petclinic-jdbc-jvm                   0.0.1                                                   fecc247ade2e   41 years ago    267MB
```

**Note**:  look at the image ID for each for the JVM image `petclinic-jdbc-jvm`, respectively the Native image `petclinic-jdbc-native`.
You can dive into the image by container name or ID.

Let's use the `dive` utility and analyze the content of each image:
```shell
# example
> dive petclinic-jdbc-jvm:0.0.1

> dive petclinic-jdbc-native:0.0.1

# or, as an example: 
> dive 6ea100cd8ca2
```

## lab end