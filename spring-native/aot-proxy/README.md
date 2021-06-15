# AOT Class Proxy in Spring Native Images

Spring features require sometime a proxy to extend directly a class. In such cases, a JDK Proxy (a class generated to implement a set of interfaces) is not.

For example, intercepting the `demoMethod()` in the following class:
```java
@RestController
public class PersonController {

	@GetMapping(value="/with")
	public String demo() {
		return "hello - proxied";
	}
	
}
```

An aspect, as declared in the code below, will intercept the method, introduce its own logic, then delegate to the intercepted method, 
however must extend the class dynamically at runtime using CGLIB, which is different to a JDK Proxy:
```java
@Aspect
@Component
public class LoggableAspect {

	private static final Logger logger = LoggerFactory.getLogger(LoggableAspect.class);
	 
    @Around("demoMethod()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("I'm working!... Go watch a demo...");
        return joinPoint.proceed();
    }

    @Pointcut("execution(* com.example.graalvmdemo.rest.PersonController.demo(..))")
    private void demoMethod(){}
	
}
```


----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/spring-native/aot-proxy
```
----

## The sample

As of Spring Native 0.10.0, the concept of @AotProxyHint is introduced.

If we were to compare it with the @JdkProxyHint, we note a number of differences:
* we have to determine which class proxies must be generated at build time, as we can't define a proxy dynamically at runtime
* the class proxies must be generated and included in the image
* the generated proxies for the classes annotated with a `AotProxyHint` will be stored in the `target/generated-sources/spring-aot` folder with a special infix `$$`, for which Spring itself is looking for, for class based proxies.

These classes will automatically be loaded at runtime and do not require for them to be listed in the `proxy-config.json` file
```shell
> tree target/generated-sources/spring-aot/src/main/resources/com/example/graalvmdemo/rest
target/generated-sources/spring-aot/src/main/resources/com/example/graalvmdemo/rest
├── PersonController$$SpringProxy$ce8dafe7$aux$1.class
├── PersonController$$SpringProxy$ce8dafe7$aux$2.class
├── PersonController$$SpringProxy$ce8dafe7$aux$3.class
├── PersonController$$SpringProxy$ce8dafe7$aux$4.class
├── PersonController$$SpringProxy$ce8dafe7$aux$5.class
└── PersonController$$SpringProxy$ce8dafe7.class
```

Let's define a class proxy hint for the PersonController class:
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.ProxyBits;

@AotProxyHint(targetClass=com.example.graalvmdemo.rest.PersonController.class,
              proxyFeatures = ProxyBits.IS_STATIC)
@SpringBootApplication
public class GraalvmDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraalvmDemoApplication.class, args);
	}

}
```

Let's built the JVM app and observe its behaviour:
```shell
> ./mvnw clean package

> java -jar target/aot-proxy-0.0.1-SNAPSHOT.ja

Access the 2 endpoints at:
> http://localhost:8080/with
Observe: hello - proxied

and
 
> http://localhost:8080/without
Observe: hello - without class proxy
```

You can run the Spring AOT plugin and observe that correct configurations have been generated:
```shell
> ./mvnw clean package spring-aot:generate

> tree target/generated-sources/spring-aot/src/main/resources/
target/generated-sources/spring-aot/src/main/resources
└── META-INF
    ├── native-image
    │   └── org.springframework.aot
    │       └── spring-aot
    │           ├── native-image.properties
    │           ├── proxy-config.json
    │           ├── reflect-config.json
    │           ├── resource-config.json
    │           └── serialization-config.json
    └── spring.components
````

We can now build the image and observe an incorrect behaviour, if the @AotProxyHint has not been specified (let's comment it out):
```shell
> ./mvnw clean package spring-boot:build-image

> docker run --rm aot-proxy:0.0.1-SNAPSHOT
Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
2021-06-15 18:46:28.434 ERROR 1 --- [           main] o.s.boot.SpringApplication               : Application run failed

org.springframework.context.ApplicationContextException: Unable to start web server; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration$EmbeddedTomcat': Initialization of bean failed; nested exception is org.aspectj.weaver.BCException: AspectJ internal error

        at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:163) ~[na:na]
        at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:577) ~[na:na]
        at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:145) ~[na:na]
        at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754) ~[com.example.graalvmdemo.GraalvmDemoApplication:na]
        at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:434) ~[com.example.graalvmdemo.GraalvmDemoApplication:na]
        at org.springframework.boot.SpringApplication.run(SpringApplication.java:338) ~[com.example.graalvmdemo.GraalvmDemoApplication:na]
        at org.springframework.boot.SpringApplication.run(SpringApplication.java:1343) ~[com.example.graalvmdemo.GraalvmDemoApplication:na]
        at org.springframework.boot.SpringApplication.run(SpringApplication.java:1332) ~[com.example.graalvmdemo.GraalvmDemoApplication:na]
        at com.example.graalvmdemo.GraalvmDemoApplication.main(GraalvmDemoApplication.java:13) ~[com.example.graalvmdemo.GraalvmDemoApplication:na]

```

When the @AotProxyHint is enabled, the correct behavior is observed, once you start the application.