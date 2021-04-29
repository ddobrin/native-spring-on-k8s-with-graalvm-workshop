# Building, containerizing and running a Spring Native app, then `dive` into the Docker image

In this lab, let's explore:
* how to build a JVM / Spring Native app image with the Spring Boot AOT plugin and GraalVM
* how to containerize the app and create a JVM / Native Docker image with Cloud-native Buildpacks
* `dive` into the newly created Docker containers

As an application, let's use a simple Java Function, for which you can use the following deployment models:
* `standalone` web app
* `Kubernetes` deployment and service
* `Knative` service

The code is simple, with the focus on the build aspects:
```java
@SpringBootApplication
public class SpringFunctionApplication {

    @Value("${TARGET:from-function}")
    String target;

    public static void main(String[] args) {
        SpringApplication.run(SpringFunctionApplication.class, args);
    }

    @Bean
    public Function<String, String> hello() {
        return (in) -> {
            return "Hello: " + in + ", Source: " + target;
        };
    }
}
```

# Build

## Build code as a JVM app using the Spring Boot Maven plugin
```shell 
# build and run code using
> ./mvnw clean package spring-boot:run

# test locally
> curl -w'\n' -H 'Content-Type: text/plain' localhost:8080 -d "from a Function"
Hello: from a Function, Source: from-function
```

Building an executable application with the GraalVM compiler leverages the following Maven profile and requires the installation of the GraalVM and the native-image builder utility:
* `native`

## Build code as a Native JVM app using the GraalVM compiler 
```shell 
# switch to the GraalVM JDK for this build
# ex, when using SDKman
> sdk use java  21.0.0.2.r11-grl

# build and run code using
> ./mvnw clean package -Pnative
```

The build is creating both the JVM and native app image. Start them both and observe the super-fast start-up time
```shell 
# JVM JAR file
> java -jar target/hello-function-0.0.1.jar 
...
2021-04-29 15:39:32.969  INFO 47689 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-04-29 15:39:32.977  INFO 47689 --- [           main] c.e.hello.SpringFunctionApplication      : Started SpringFunctionApplication in 1.694 seconds (JVM running for 2.084)
...

# start the native executable
> ./target/hello-function
...
2021-04-29 15:41:23.404  INFO 47757 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-04-29 15:41:23.404  INFO 47757 --- [           main] c.e.hello.SpringFunctionApplication      : Started SpringFunctionApplication in 0.066 seconds (JVM running for 0.068)
...

# test locally
$ curl -w'\n' -H 'Content-Type: text/plain' localhost:8080 -d "from a Function"
Hello: from a Function, Source: from-function
```

## Build code as a JVM image using the Spring Boot Maven plugin and Java Paketo Buildpacks
```bash 
# build image with the cloud-native Paketo buildpack 
> ./mvnw clean spring-boot:build-image -Pjvm-image

# start Docker image
> docker run -p 8080:8080 hello-function-jvm:0.0.1
...
2021-04-29 19:45:42.358  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-04-29 19:45:42.366  INFO 1 --- [           main] c.e.hello.SpringFunctionApplication      : Started SpringFunctionApplication in 2.32 seconds (JVM running for 2.73)
...

# test Docker image locally
> curl -w'\n' -H 'Content-Type: text/plain' localhost:8080 -d "from a Function"
Hello: from a Function, Source: from-function
```

## Build code as a Spring Native image using the Spring Boot Maven plugin and the Java Native Paketo Buildpacks
```bash 
# build image with the cloud-native Paketo buildpack
> ./mvnw clean spring-boot:build-image -Pnative-image

# start Docker image
> docker run -p 8080:8080 hello-function-native:0.0.1
...
2021-04-29 19:51:43.265  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-04-29 19:51:43.266  INFO 1 --- [           main] c.e.hello.SpringFunctionApplication      : Started SpringFunctionApplication in 0.063 seconds (JVM running for 0.066)
...
```

# Dive into a Docker container

Let's list the Docker containers which have been built:
```shell
> docker images | grep hello-function 
hello-function-native                            0.0.1                                                   cb18db0ac957   41 years ago    105MB
hello-function-jvm                               0.0.1                                                   d7564bda34e2   41 years ago    266MB
```

**Note**:  image ID for each for the JVM image `hello-function-jvm`, respectively the Native image `hello-function-native`. 
You can dive into the image by container name or ID.

Let's use the `dive` utility and analyze the content of each image:
```shell
# example
> dive hello-function-jvm:0.0.1

> dive hello-function-native:0.0.1

# or, as an example: 
> dive cb18db0ac957
```

#### `lab end`