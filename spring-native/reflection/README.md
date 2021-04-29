

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

# build the image
> ./mvnw clean package spring-boot:build-image

> docker run --rm reflection:0.0.1-SNAPSHOT com.example.StringReverser reverse "what is new"
2021-04-28 13:10:32.772  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-28 13:10:32.773  INFO 1 --- [           main] com.example.ReflectionApplication        : Starting ReflectionApplication using Java 11.0.10 on 8526b2e08121 with PID 1 (/workspace/com.example.ReflectionApplication started by cnb in /workspace)
2021-04-28 13:10:32.773  INFO 1 --- [           main] com.example.ReflectionApplication        : No active profile set, falling back to default profiles: default
2021-04-28 13:10:32.775  INFO 1 --- [           main] com.example.ReflectionApplication        : Started ReflectionApplication in 0.01 seconds (JVM running for 0.013)
wen si tahw

> docker run --rm reflection:0.0.1-SNAPSHOT com.example.StringCapitalizer capitalize "what is new"
2021-04-28 13:11:11.308  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-28 13:11:11.311  INFO 1 --- [           main] com.example.ReflectionApplication        : Starting ReflectionApplication using Java 11.0.10 on 291da71e6825 with PID 1 (/workspace/com.example.ReflectionApplication started by cnb in /workspace)
2021-04-28 13:11:11.311  INFO 1 --- [           main] com.example.ReflectionApplication        : No active profile set, falling back to default profiles: default
2021-04-28 13:11:11.314  INFO 1 --- [           main] com.example.ReflectionApplication        : Started ReflectionApplication in 0.012 seconds (JVM running for 0.015)
WHAT IS NEW

```

```java
@TypeHint(typeNames = {"com.example.StringReverser"})
@TypeHint(typeNames = {"com.example.StringCapitalizer"})
```

```json
# reflect-config.json
...
    {
    "name": "com.example.ReflectionApplication",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
    },
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
```

```java
...
@NativeHint(
        types = {
                @TypeHint(types = {
                        com.example.StringReverser.class,
                        com.example.StringCapitalizer.class
                }, access = AccessBits.DECLARED_METHODS)
        }
)
...
```
```json
# reflect-config.json
...
    {
    "name": "com.example.Reflection",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true,
    "allDeclaredClasses": true
    },
    {
    "name": "com.example.ReflectionApplication",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
    },
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