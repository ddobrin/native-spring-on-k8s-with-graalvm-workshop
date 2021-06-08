# Dynamic Proxies in Spring Native Images

This application builds upon the concepts presented in the GraalVM chapter on Dynamic Proxies and shows how Spring Native provides support
for compiling Spring applications to native executables using the GraalVM native-image compiler.
<br><br>
Please revisit the **[Dynamic Proxy doc](../../graalvm/dynamic-proxy/README.md)**

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/spring-native/dynamic-proxy
```
----

## The sample

We're leveraging the same `DynamicProxy` class, now packaged in a simple Spring Boot command-line runner.
Note that we are not running unit tests and have therefore bypassing them with a Spring Profile.
```java
package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.Map;

@Profile("!test")
@Component
public class DynamicProxy implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        String className = args[0];

        // A proxy instance serviced by an invocation handler is created via a factory method call on the java.lang.reflect.Proxy class
        // Since InvocationHandler is a functional interface, let's the handler inline using lambda expressions
        Map proxyInstance = (Map) Proxy.newProxyInstance(
                DynamicProxy.class.getClassLoader(),
                new Class[] {
                        Class.forName(className)
                },
                (proxy, method, methodArgs) -> {
                    if (method.getName().equals("get")) {
                        return 42;
                    }  else {
                        throw new UnsupportedOperationException(
                                "Unsupported method: " + method.getName());
                    }
                });

        // successful invocation of a get() operation
        int fortytwo = (int) proxyInstance.get("hello"); // 42
        System.out.println("Invoking method: <get> in a <java.util.Map>. Output: " + fortytwo);

        // failed proxy invocation, as put() is not a supported operation in the proxy
        try {
            System.out.println("Invoking method: <put> in a <java.util.Map>");
            proxyInstance.put("hello", "world"); // exception
        } catch (Exception e) {
            System.out.println("Expected an exception, got an exception");
            return;
        }

        // some unexpected exception
        throw new RuntimeException("Expected an exception, didn't get one");
    }
}
```

Native Image does not provide tooling for generating and interpreting bytecodes at run time.
Therefore, all dynamic proxy classes need to be generated at native image build time.

One of the language features that require explicit configuration besides Reflection are JDK proxies.

When using Spring Native, we will be using a `@JdkProxyHint`, to indicate to the GraalVM compiler which configuration is required to be generated.
<br>
Let's add the @JdkProxyHint, to address the Proxy creation in `Map proxyInstance = (Map) Proxy.newProxyInstance(`
```java
@JdkProxyHint(typeNames = {
		"java.util.Map"
})
```

Let's also add the @TypeHint, to address the creation of the java.util.Map Class instance in a @TypeHint in ` Class.forName(className)`
```java
@TypeHint(typeNames = {
		"java.util.Map"
})
```

The `DynamicProxyApplication` is now fully configured:
```java
@JdkProxyHint(typeNames = {
		"java.util.Map"
})
@TypeHint(typeNames = {
		"java.util.Map"
})
@SpringBootApplication
public class DynamicProxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(DynamicProxy.class, args);
	}
}
```

We can run the Spring AOT plugin and observe the generated config files:
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

Build the image with the proxy hint generated and observe that the application is successful:
```shell
> ./mvnw clean package spring-boot:build-image

> docker run --rm dynamic-proxy:0.0.1-SNAPSHOT java.util.Map
2021-04-29 15:58:35.191  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 15:58:35.193  INFO 1 --- [           main] com.example.DynamicProxyApplication      : Starting DynamicProxyApplication using Java 11.0.10 on 6ef3a0e6a2ec with PID 1 (/workspace/com.example.DynamicProxyApplication started by cnb in /workspace)
2021-04-29 15:58:35.193  INFO 1 --- [           main] com.example.DynamicProxyApplication      : No active profile set, falling back to default profiles: default
2021-04-29 15:58:35.195  INFO 1 --- [           main] com.example.DynamicProxyApplication      : Started DynamicProxyApplication in 0.014 seconds (JVM running for 0.016)
Invoking method: <get> in a <java.util.Map>. Output: 42
Invoking method: <put> in a <java.util.Map>
Expected an exception, got an exception
```
