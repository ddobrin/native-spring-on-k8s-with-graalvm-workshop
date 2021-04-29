# Dynamic Class Loading and Reflection in Spring Native Images

This application builds upon the concepts presented in the GraalVM chapter on Class Loading and Reflection and shows how Spring Native provides support
for compiling Spring applications to native executables using the GraalVM native-image compiler.
<br><br>
Please revisit the **[Dynamic Class Loading and Reflection doc](../../graalvm/reflection/README.md)**

We're leveraging the same `Reflection` class, now packaged in a simple Spring Boot command-line runner. 
Note that we are not running unit tests and have therefore bypassing them with a Spring Profile.
```java
package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import org.springframework.context.annotation.Profile;
import org.springframework.nativex.hint.TypeHint;

class StringReverser {
    static String reverse(String input) {
        return new StringBuilder(input).reverse().toString();
    }
}

class StringCapitalizer {
    static String capitalize(String input) {
        return input.toUpperCase();
    }
}

@Profile("!test")
@Component
public class Reflection implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
        String className = args[0];
        String methodName = args[1];
        String input = args[2];

        Class<?> clazz = Class.forName(className);
        Method method = clazz.getDeclaredMethod(methodName, String.class);
        Object result = method.invoke(null, input);
        System.out.println(result);
    }
}
```

To bypass the class loading and reflection problems, as detailed in the `graalvm` chapter, we will be using the Spring Native `@TypeHint`,
to indicate to the GraalVM compiler which configuration is required to be generated

Note that you can specify the hint in multiple ways, directly, or as part of a `@NativeHint` declaration. 
You can specify a finer-grained set of hints, and then observe that we'll `provide hints` only for the declared methods in our classes:
```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.hint.AccessBits;

/* You can specify a TypeHint directly
@TypeHint(typeNames = {"com.example.StringReverser"})
@TypeHint(typeNames = {"com.example.StringCapitalizer"})
*/

/* ... or more specific to the type and down to the method you need to leverage hints for */
@NativeHint(
        types = {
                @TypeHint(types = {
                        com.example.StringReverser.class,
                        com.example.StringCapitalizer.class
                }, access = AccessBits.DECLARED_METHODS)
        }
)
@SpringBootApplication
public class ReflectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(Reflection.class, args);
    }
}
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

```json
// using: 
//    @TypeHint(typeNames = {"com.example.StringReverser"})
//    @TypeHint(typeNames = {"com.example.StringCapitalizer"})
...
    {
      "name": "com.example.StringCapitalizer",
      "allDeclaredFields": true,
      "allDeclaredConstructors": true,
      "allDeclaredMethods": true
    },
    {
    "name": "com.example.StringReverser",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
    },
...
        
// using:
//    @TypeHint(types = {
//      com.example.StringReverser.class,
//      com.example.StringCapitalizer.class
//    }, access = AccessBits.DECLARED_METHODS)
    ...
    {
      "name": "com.example.StringCapitalizer",
      "allDeclaredMethods": true
    },
    {
    "name": "com.example.StringReverser",
    "allDeclaredMethods": true
    },
...
```

We can now build the image and observe correct behaviour:
```shell
# build the image
> ./mvnw clean package spring-boot:build-image

> docker run --rm reflection:0.0.1-SNAPSHOT com.example.StringReverser reverse "what is new"
2021-04-29 15:31:55.120  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 15:31:55.122  INFO 1 --- [           main] com.example.ReflectionApplication        : Starting ReflectionApplication using Java 11.0.10 on e9219fb11260 with PID 1 (/workspace/com.example.ReflectionApplication started by cnb in /workspace)
2021-04-29 15:31:55.122  INFO 1 --- [           main] com.example.ReflectionApplication        : No active profile set, falling back to default profiles: default
2021-04-29 15:31:55.124  INFO 1 --- [           main] com.example.ReflectionApplication        : Started ReflectionApplication in 0.014 seconds (JVM running for 0.017)
wen si tahw

> docker run --rm reflection:0.0.1-SNAPSHOT com.example.StringCapitalizer capitalize "what is new"
2021-04-29 15:32:25.651  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 15:32:25.653  INFO 1 --- [           main] com.example.ReflectionApplication        : Starting ReflectionApplication using Java 11.0.10 on c070fc7b2af0 with PID 1 (/workspace/com.example.ReflectionApplication started by cnb in /workspace)
2021-04-29 15:32:25.653  INFO 1 --- [           main] com.example.ReflectionApplication        : No active profile set, falling back to default profiles: default
2021-04-29 15:32:25.655  INFO 1 --- [           main] com.example.ReflectionApplication        : Started ReflectionApplication in 0.011 seconds (JVM running for 0.013)
WHAT IS NEW
```

