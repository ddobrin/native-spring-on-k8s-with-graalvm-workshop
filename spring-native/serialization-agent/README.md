# Serialization in Spring Native Images - using Tracing Agent

This application builds upon the concepts presented in the GraalVM chapter on Serialization and shows how Spring Native provides support
for compiling Spring applications to native executables using the GraalVM native-image compiler.
<br><br>
Please revisit the **[Serialization Doc](../../graalvm/serialization/README.md)** as well as the **[Serialization with Spring Native Doc](../../spring-native/serialization/README.md)**

This application shows an alternative to the Serialization sample, this time with the Java Agent running, precluding you to add the @SerializationHint in the app.

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/spring-native/serialization-agent
```
----

## The sample

We're leveraging the same `Serialization` class, now packaged in a simple Spring Boot command-line runner:
```java
package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Serialization implements CommandLineRunner {
    private static final String filename = "serialized_objects_in_stream";

    static Stream<Long> fibonacciStream() {
        return Stream.iterate(new long[]{0, 1}, (f) -> new long[]{f[0] + f[1], f[0]}).map(f -> f[0]);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Long> fib10 = fibonacciStream().limit(10).collect(Collectors.toList());
        try (ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream(filename))) {
            oss.writeObject(fib10);
        }
        Object deserializedFib1000;
        try (ObjectInputStream oss = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFib1000 = oss.readObject();
        }
        System.out.println("Serialized list matches Deserialized list: " + fib10.equals(deserializedFib1000));

        System.out.println("Print the first 10 Fibonacci numbers in the sequence");
        fib10.forEach(System.out::println);
    }
}
```

Let's built the JVM app and observe its behaviour:
```shell
> ./mvnw clean package

> java -jar target/serialization-0.0.1-SNAPSHOT.jar 
2021-04-29 14:24:47.996  INFO 39283 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 14:24:48.056  INFO 39283 --- [           main] com.example.SerializationApplication     : Starting SerializationApplication v0.0.1-SNAPSHOT using Java 11.0.10 on ddobrin-a01.vmware.com with PID 39283 (/Users/dandobrin/work/native/native-spring-on-k8s-with-graalvm-workshop/spring-native/serialization/target/serialization-0.0.1-SNAPSHOT.jar started by dandobrin in /Users/dandobrin/work/native/native-spring-on-k8s-with-graalvm-workshop/spring-native/serialization)
2021-04-29 14:24:48.057  INFO 39283 --- [           main] com.example.SerializationApplication     : No active profile set, falling back to default profiles: default
2021-04-29 14:24:48.164  INFO 39283 --- [           main] com.example.SerializationApplication     : Started SerializationApplication in 0.423 seconds (JVM running for 0.772)
Serialized list matches Deserialized list: true
Print the first 10 Fibonacci numbers in the sequence
0
1
1
2
3
5
8
13
21
34
```

You can observe that, due to the Java agent being configured in the pom.xml, it will generate the configuration for you.

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
````

Observer the `serialization-config.json` config file:
```json
...
    {
      "name": "java.lang.Long"
    },
    {
      "name": "java.lang.Number"
    },
    {
      "name": "java.util.ArrayList"
    }
...
```

We can now build the image and observe correct behaviour:
```shell
> ./mvnw clean package spring-boot:build-image

> docker run --rm serialization:0.0.1-SNAPSHOT
2021-04-29 18:51:08.695  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 18:51:08.697  INFO 1 --- [           main] com.example.SerializationApplication     : Starting SerializationApplication using Java 11.0.10 on d444b5fe9f82 with PID 1 (/workspace/com.example.SerializationApplication started by cnb in /workspace)
2021-04-29 18:51:08.697  INFO 1 --- [           main] com.example.SerializationApplication     : No active profile set, falling back to default profiles: default
2021-04-29 18:51:08.698  INFO 1 --- [           main] com.example.SerializationApplication     : Started SerializationApplication in 0.014 seconds (JVM running for 0.016)
Serialized list matches Deserialized list: true
Print the first 10 Fibonacci numbers in the sequence
0
1
1
2
3
5
8
13
21
34
```