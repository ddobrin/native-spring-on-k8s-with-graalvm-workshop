# Spring Boot famous PetClinic sample with JDBC persistence - Fails for Native Images

### `Currently Tracked Versions`
* Spring Boot 2.5.3 - July 2021
* Spring Native 0.10.2 (Spring Native Beta) - July 2021
* OpenJDK version "11.0.10"
* OpenJDK 64-Bit Server VM GraalVM CE 21.2.0 (build 11.0.12+6-jvmci-21.2-b08, mixed mode, sharing)

### `Original Project`
The original Spring Boot PetClinic project is available at: `https://github.com/spring-projects/spring-petclinic`

### Our focus
Let's explore why this version of Petclinic fails ... or, better said... what did we do in the original project 
to build a proper Native Image for it?

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/complete/petclinic-jdbc-failed
```
----

## The Project

In the working project, let's run this command and observe the following resource config file 
```shell
> ... /native-spring-on-k8s-with-graalvm-workshop/complete/petclinic-jdbc/src/main/resources:>tree META-INF/
META-INF/
└── native-image
    └── resource-config.json

> cat src/main/resources/META-INF/native-image/resource-config.json 
{
  "bundles": [
    {"name":"messages/messages"}
  ]
}
```

In our `failed` sample, let's remove this file !

Now let's build a lightweight container with a Native Image
```shell
> ./mvnw clean spring-boot:build-image -Pnative-image

> docker compose  -f docker-compose-native.yml up
```

Let's observe that the image build is successful, however the run does not display messages properly ! 

The workshop will answer this question.


### Project
To build and run the native application packaged in a lightweight container with `default` mode:
```shell
> ./mvnw clean spring-boot:build-image

> docker-compose up
```

Access the webapp at `http://localhost:8080`.