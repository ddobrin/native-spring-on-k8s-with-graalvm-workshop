



```shell
> ./mvnw clean package spring-aot:generate 

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
  {
    "pattern": "src/data/app-resources.*"
  }
```

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

```shell
# build the image
> ./mvnw clean package spring-boot:build-image

> docker run --rm resource-access:0.0.1-SNAPSHOT
Reading config.threads property: 10
Reading config.load property: 200

```