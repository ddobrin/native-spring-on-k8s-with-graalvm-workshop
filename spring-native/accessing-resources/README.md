# Accessing Resources in Spring Native Images

This application builds upon the concepts presented in the GraalVM chapter on Accessing Resources and shows how Spring Native provides support
for compiling Spring applications to native executables using the GraalVM native-image compiler.
<br><br>
Please revisit the **[Accessing Resources doc](../../graalvm/accesing-resources/README.md)**

We're leveraging the same `ResourceAccess` class, now packaged in a simple Spring Boot command-line runner.
Note that we are not running unit tests and have therefore bypassing them with a Spring Profile.

For the Spring Native version of the sample, we will provision the same config file, this time in a subfolder:
```shell
> tree src/data
src/data
└── app-resources.properties

> cat src/data/app-resources.properties 
config.threads=10
config.load=200
```

The `ResourceAccess` class is now running as a Spring Boot application:
```java 
package com.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.context.annotation.Profile;
import org.springframework.nativex.hint.ResourceHint;

@ResourceHint(
        patterns = {
                "src/data/app-resources.*"
        }
)
@Profile("!test")
@SpringBootApplication
public class ResourceAccess {
    public static void main(String[] args) {
        Properties prop = new Properties();

        // read properties
        try (InputStream inputStream = ResourceAccess.class
                                        .getClassLoader()
                                        .getResourceAsStream("src/data/app-resources.properties")) {
            prop.load(inputStream);
            System.out.println("Reading config.threads property: " + prop.getProperty("config.threads"));
            System.out.println("Reading config.load property: " + prop.getProperty("config.load"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

The native image builder will not integrate any of the resources which are on the CLASSPaTH while generating the final image.
To make calls such as `Class.getResource()`, `Class.getResourceAsStream()`, or the corresponding ClassLoader methods,
return specific resources, instead of `null`, the resources that should be accessible at image run time must be specified explicitly.

Spring AOT relies on the @ResourceHint provided in the `ResourceAccess` class to generate the proper configurations:
```java
...
@ResourceHint(
        patterns = {
                "src/data/app-resources.*"
        }
)
@SpringBootApplication
public class ResourceAccess {
    public static void main(String[] args) {
        Properties prop = new Properties();

        // read properties
        try (InputStream inputStream = ResourceAccess.class
                .getClassLoader()
                .getResourceAsStream("src/data/app-resources.properties")) {
...
```

You can run the Spring AOT plugin and observe that correct configurations have been generated:
```shell
> ./mvnw clean package spring-aot:generate

> tree target/generated-sources/spring-aot/src/main/resources/
target/generated-sources/spring-aot/src/main/resources/
└── META-INF
    └── native-image
        └── org.springframework.aot
            └── spring-aot
                ├── native-image.properties
                ├── proxy-config.json
                ├── reflect-config.json
                ├── resource-config.json
                └── serialization-config.json
```

The `resource-config.json` file contains:
```json
  {
    "pattern": "src/data/app-resources.*"
  }
```

Let's build the image and observe the correct behaviour:
```shell
# build the image
> ./mvnw clean package spring-boot:build-image

> docker run --rm resource-access:0.0.1-SNAPSHOT
Reading config.threads property: 10
Reading config.load property: 200
```