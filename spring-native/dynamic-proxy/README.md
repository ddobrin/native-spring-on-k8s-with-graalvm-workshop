

```shell
> ./mvnw clean package

> java -jar target/dynamic-proxy-0.0.1-SNAPSHOT.jar java.util.Map
2021-04-29 08:05:00.578  INFO 96342 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 08:05:00.648  INFO 96342 --- [           main] com.example.DynamicProxyApplication      : Starting DynamicProxyApplication v0.0.1-SNAPSHOT using Java 11.0.10 on ddobrin-a01.vmware.com with PID 96342 (/Users/dandobrin/work/native/start.spring/dynamic-proxy/target/dynamic-proxy-0.0.1-SNAPSHOT.jar started by dandobrin in /Users/dandobrin/work/native/start.spring/dynamic-proxy)
2021-04-29 08:05:00.649  INFO 96342 --- [           main] com.example.DynamicProxyApplication      : No active profile set, falling back to default profiles: default
2021-04-29 08:05:00.734  INFO 96342 --- [           main] com.example.DynamicProxyApplication      : Started DynamicProxyApplication in 0.431 seconds (JVM running for 0.778)
Invoking method: <get> in a <java.util.Map>. Output: 42
Invoking method: <put> in a <java.util.Map>
Expected an exception, got an exception
```

Adding the @ProxyHint, to address the Proxy creation in `Map proxyInstance = (Map) Proxy.newProxyInstance(`
```java
@ProxyHint(typeNames = {
		"java.util.Map"
})
```

Adding the @TypeHint, to address the creation of the java.util.Map Class instance in a @TypeHint in ` Class.forName(className)`
```java
@TypeHint(typeNames = {
		"java.util.Map"
})
```

```shell
> ./mvnw clean package spring-aot:generate
```

Observe the proxy-config.json and reflect-config.json files
```json
// proxy-config.json
...
    [
    "java.util.Map"
    ],
...

// reflect-config.json
...
    {
    "name": "java.util.Map",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
    },
...
```

The execution of the app is now successful:
```shell
docker run --rm dynamic-proxy:0.0.1-SNAPSHOT java.util.Map
2021-04-29 13:11:33.192  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 13:11:33.194  INFO 1 --- [           main] com.example.DynamicProxyApplication      : Starting DynamicProxyApplication using Java 11.0.10 on b64b1f980646 with PID 1 (/workspace/com.example.DynamicProxyApplication started by cnb in /workspace)
2021-04-29 13:11:33.194  INFO 1 --- [           main] com.example.DynamicProxyApplication      : No active profile set, falling back to default profiles: default
2021-04-29 13:11:33.195  INFO 1 --- [           main] com.example.DynamicProxyApplication      : Started DynamicProxyApplication in 0.011 seconds (JVM running for 0.013)
Invoking method: <get> in a <java.util.Map>. Output: 42
Invoking method: <put> in a <java.util.Map>
Expected an exception, got an exception
```

Building the image with the proxy hint generated and observe that the application is successful
```shell
> ./mvnw clean package spring-boot:build-image


```