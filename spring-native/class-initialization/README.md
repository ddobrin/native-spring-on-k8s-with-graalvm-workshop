# Class initialization strategy with Spring Native Images

This application builds upon the concepts presented in the GraalVM chapter on Class Initialization and shows how Spring Native provides support 
for compiling Spring applications to native executables using the GraalVM native-image compiler. 
<br><br>
Please revisit the **[Class Initialization Strategy doc](../../graalvm/class-initialization/README.md)** 

## Part 1
We're leveraging the same ClassInit class, now packaged in a simple Spring Boot command-line runner:
```java
package com.example;
import java.nio.charset.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ClassInit implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
        First.second.printIt();
    }
}

class First {
    public static Second second = new Second();

    //Uncomment this section for part 2 of the exercise
    public static Thread t;

    // Uncomment for Part 2
//    static {
//        t = new Thread(()-> {
//            try {
//                System.out.println("Sleep for 10s...");
//                Thread.sleep(10_000);
//                System.out.println("Done...");
//            } catch (Exception e){}
//        });
//        t.start();
//    }
}

class Second {
    private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");

    public void printIt() {
        System.out.println("Unicode 32 bit CharSet: " + UTF_32_LE);
    }
}
```

```shell 
> ./mvnw clean package

> java -jar target/class-init-0.0.1-SNAPSHOT.jar 
2021-04-28 11:53:55.970  INFO 14145 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-28 11:53:56.035  INFO 14145 --- [           main] com.example.ClassInitApplication         : Starting ClassInitApplication v0.0.1-SNAPSHOT using Java 11.0.10 on ddobrin-a01.vmware.com with PID 14145 (/Users/dandobrin/work/native/start.spring/classinit/target/class-init-0.0.1-SNAPSHOT.jar started by dandobrin in /Users/dandobrin/work/native/start.spring/classinit)
2021-04-28 11:53:56.036  INFO 14145 --- [           main] com.example.ClassInitApplication         : No active profile set, falling back to default profiles: default
2021-04-28 11:53:56.144  INFO 14145 --- [           main] com.example.ClassInitApplication         : Started ClassInitApplication in 0.44 seconds (JVM running for 0.798)
Unicode 32 bit CharSet: UTF-32LE
```

Let's build a Docker image from the app and observe it fail:
```shell
> ./mvnw clean package spring-boot:build-image

> docker run --rm class-init:0.0.1-SNAPSHOT
2021-04-29 14:31:54.823  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 14:31:54.825  INFO 1 --- [           main] com.example.ClassInitApplication         : Starting ClassInitApplication using Java 11.0.10 on 8107a3ed7235 with PID 1 (/workspace/com.example.ClassInitApplication started by cnb in /workspace)
2021-04-29 14:31:54.825  INFO 1 --- [           main] com.example.ClassInitApplication         : No active profile set, falling back to default profiles: default
2021-04-29 14:31:54.827  INFO 1 --- [           main] com.example.ClassInitApplication         : Started ClassInitApplication in 0.01 seconds (JVM running for 0.014)
2021-04-29 14:31:54.827 ERROR 1 --- [           main] o.s.boot.SpringApplication               : Application run failed

java.lang.ExceptionInInitializerError: null
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.initialize(ClassInitializationInfo.java:315) ~[na:na]
	at com.example.First.<clinit>(ClassInit.java:17) ~[na:na]
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.invokeClassInitializer(ClassInitializationInfo.java:375) ~[na:na]
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.initialize(ClassInitializationInfo.java:295) ~[na:na]
	at com.example.ClassInit.run(ClassInit.java:12) ~[com.example.ClassInitApplication:na]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:819) ~[com.example.ClassInitApplication:2.4.5]
	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:803) ~[com.example.ClassInitApplication:2.4.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:346) ~[com.example.ClassInitApplication:2.4.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1340) ~[com.example.ClassInitApplication:2.4.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1329) ~[com.example.ClassInitApplication:2.4.5]
	at com.example.ClassInitApplication.main(ClassInitApplication.java:21) ~[com.example.ClassInitApplication:na]
Caused by: java.nio.charset.UnsupportedCharsetException: UTF-32LE
	at java.nio.charset.Charset.forName(Charset.java:529) ~[na:na]
	at com.example.Second.<clinit>(ClassInit.java:36) ~[na:na]
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.invokeClassInitializer(ClassInitializationInfo.java:375) ~[na:na]
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.initialize(ClassInitializationInfo.java:295) ~[na:na]
	... 10 common frames omitted
```

To correct the problem, we will use one of the @NativeHint's provided by Spring Native, an `@InitializationHint`
```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.InitializationHint;
import org.springframework.nativex.hint.InitializationTime;
import org.springframework.nativex.hint.NativeHint;

@NativeHint(
		initialization = @InitializationHint(types = {
				com.example.First.class,
				com.example.Second.class
		}, initTime = InitializationTime.BUILD))
@SpringBootApplication
public class ClassInitApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClassInit.class, args);
	}
}
```

Running now the Spring AOT plugin will generate the necessary config files. The plugin is automatically executed when building a Spring Native Image:
```shell
> ./mvnw clean package spring-aot:generate
```

```json
# reflect-config.json
...
    {
    "name": "com.example.ClassInit",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true,
    "allDeclaredClasses": true
    },
    {
    "name": "com.example.ClassInitApplication",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
    },
...
```

Note that the @InitializationHint adds the `First` and `Second` classes to the list of classes to be initialized at buildtime: `--initialize-at-build-time=com.example.First,com.example.Second`:
```properties
Args = --allow-incomplete-classpath --report-unsupported-elements-at-runtime --no-fallback --no-server --install-exit-handlers -H:+InlineBeforeAnalysis \
--initialize-at-build-time=com.example.First,com.example.Second,com.google.protobuf.Extension,com.google.protobuf.ExtensionLite,com.google.protobuf.ExtensionRegistry,org.aopalliance.aop.Advice,org.apache.commons.logging.LogAdapter,org.apache.commons.logging.LogAdapter$1,org.apache.commons.logging.LogAdapter$Log4jLog,org.apache.commons.logging.LogAdapter$Slf4jLocationAwareLog,org.apache.commons.logging.LogFactory,org.slf4j.Logger,org.slf4j.LoggerFactory,org.slf4j.MDC,org.slf4j.event.EventRecodingLogger,org.slf4j.event.SubstituteLoggingEvent,org.slf4j.helpers.FormattingTuple,org.slf4j.helpers.MessageFormatter,org.slf4j.helpers.NOPLogger,org.slf4j.helpers.NOPLoggerFactory,org.slf4j.helpers.SubstituteLogger,org.slf4j.helpers.SubstituteLoggerFactory,org.slf4j.helpers.Util,org.slf4j.impl.StaticLoggerBinder,org.slf4j.spi.LocationAwareLogger,org.springframework.aop.Advisor,org.springframework.aop.Advisor$1,org.springframework.aop.TargetSource,org.springframework.aop.framework.Advised,org.springframework.aot.StaticSpringFactories,org.springframework.beans.CachedIntrospectionResults,org.springframework.beans.PropertyEditorRegistrySupport,org.springframework.beans.factory.xml.XmlBeanDefinitionReader,org.springframework.boot.BeanDefinitionLoader,org.springframework.boot.autoconfigure.cache.CacheConfigurations,org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener,org.springframework.boot.logging.LoggingSystem,org.springframework.boot.logging.java.JavaLoggingSystem$Factory,org.springframework.boot.logging.log4j2.Log4J2LoggingSystem$Factory,org.springframework.boot.logging.logback.LogbackLoggingSystem,org.springframework.boot.logging.logback.LogbackLoggingSystem$Factory,org.springframework.cloud.function.web.function.FunctionEndpointInitializer,org.springframework.context.annotation.CommonAnnotationBeanPostProcessor,org.springframework.context.event.EventListenerMethodProcessor,org.springframework.context.support.AbstractApplicationContext,org.springframework.core.DecoratingProxy,org.springframework.core.DefaultParameterNameDiscoverer,org.springframework.core.KotlinDetector,org.springframework.core.NativeDetector,org.springframework.core.ReactiveAdapterRegistry,org.springframework.core.ResolvableType,org.springframework.core.SpringProperties,org.springframework.core.annotation.AnnotationFilter,org.springframework.core.annotation.AnnotationFilter$1,org.springframework.core.annotation.AnnotationFilter$2,org.springframework.core.annotation.AnnotationUtils,org.springframework.core.annotation.PackagesAnnotationFilter,org.springframework.core.annotation.TypeMappedAnnotations,org.springframework.core.io.support.PropertiesLoaderUtils,org.springframework.core.io.support.ResourcePropertiesPersister,org.springframework.core.io.support.SpringFactoriesLoader,org.springframework.format.annotation.DateTimeFormat$ISO,org.springframework.http.HttpStatus,org.springframework.http.MediaType,org.springframework.http.codec.CodecConfigurerFactory,org.springframework.http.codec.support.BaseDefaultCodecs,org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter,org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter,org.springframework.jdbc.datasource.ConnectionProxy,org.springframework.jdbc.support.JdbcAccessor,org.springframework.jdbc.support.JdbcTransactionManager,org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration,org.springframework.nativex.substitutions.boot.NativeSpringBootVersion,org.springframework.transaction.annotation.Isolation,org.springframework.transaction.annotation.Propagation,org.springframework.util.Assert,org.springframework.util.ClassUtils,org.springframework.util.CollectionUtils,org.springframework.util.ConcurrentReferenceHashMap,org.springframework.util.DefaultPropertiesPersister,org.springframework.util.LinkedCaseInsensitiveMap,org.springframework.util.MimeType,org.springframework.util.ReflectionUtils,org.springframework.util.StringUtils,org.springframework.util.unit.DataSize,org.springframework.util.unit.DataUnit,org.springframework.web.client.RestTemplate,org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport,org.springframework.web.servlet.function.support.RouterFunctionMapping,org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver,org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter,org.springframework.web.socket.sockjs.transport.TransportHandlingSockJsService,ch.qos.logback.classic,ch.qos.logback.classic.util,ch.qos.logback.core,org.apache.logging.log4j,org.apache.logging.slf4j,org.jboss.logging,org.springframework.core.env
```

Build the image and observe a successful behavior:
```shell
> ./mvnw clean package spring-boot:build-image

> docker run --rm class-init:0.0.1-SNAPSHOT
2021-04-29 14:43:07.930  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 14:43:07.932  INFO 1 --- [           main] com.example.ClassInitApplication         : Starting ClassInitApplication using Java 11.0.10 on 4ba67929319c with PID 1 (/workspace/com.example.ClassInitApplication started by cnb in /workspace)
2021-04-29 14:43:07.932  INFO 1 --- [           main] com.example.ClassInitApplication         : No active profile set, falling back to default profiles: default
2021-04-29 14:43:07.934  INFO 1 --- [           main] com.example.ClassInitApplication         : Started ClassInitApplication in 0.01 seconds (JVM running for 0.013)
Unicode 32 bit CharSet: UTF-32LE
```

## Part 2
Let's modify the code to add the creation of a Thread within class First, with a sleep of 10 seconds. 
You can uncomment /* Part 2 */ in the ClassInit sample class and uncomment the NativeHint in `ClassInitApplication` 
to initialize only the `Second` class at build-time:
```java
@NativeHint(
        initialization = @InitializationHint(types = {
                com.example.Second.class
        }, initTime = InitializationTime.BUILD))
@SpringBootApplication
public class ClassInitApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClassInit.class, args);
    }
}
```

We can now run the Spring AOT plugin and observe the changes:
```shell
> ./mvnw clean package spring-aot:generate
```

```json
// reflect-config.json
    {
      "name": "com.example.ClassInit",
      "allDeclaredFields": true,
      "allDeclaredConstructors": true,
      "allDeclaredMethods": true,
      "allPublicMethods": true,
      "allDeclaredClasses": true
    },
    {
    "name": "com.example.ClassInitApplication",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
    },
```
Only the `Second` class has been added to the build arguments
```properties
Args = --allow-incomplete-classpath --report-unsupported-elements-at-runtime --no-fallback --no-server --install-exit-handlers -H:+InlineBeforeAnalysis \
--initialize-at-build-time=com.example.Second,com.google.protobuf.Extension,com.google.protobuf.ExtensionLite,com.google.protobuf.ExtensionRegistry,org.aopalliance.aop.Advice,org.apache.commons.logging.LogAdapter,org.apache.commons.logging.LogAdapter$1,org.apache.commons.logging.LogAdapter$Log4jLog,org.apache.commons.logging.LogAdapter$Slf4jLocationAwareLog,org.apache.commons.logging.LogFactory,org.slf4j.Logger,org.slf4j.LoggerFactory,org.slf4j.MDC,org.slf4j.event.EventRecodingLogger,org.slf4j.event.SubstituteLoggingEvent,org.slf4j.helpers.FormattingTuple,org.slf4j.helpers.MessageFormatter,org.slf4j.helpers.NOPLogger,org.slf4j.helpers.NOPLoggerFactory,org.slf4j.helpers.SubstituteLogger,org.slf4j.helpers.SubstituteLoggerFactory,org.slf4j.helpers.Util,org.slf4j.impl.StaticLoggerBinder,org.slf4j.spi.LocationAwareLogger,org.springframework.aop.Advisor,org.springframework.aop.Advisor$1,org.springframework.aop.TargetSource,org.springframework.aop.framework.Advised,org.springframework.aot.StaticSpringFactories,org.springframework.beans.CachedIntrospectionResults,org.springframework.beans.PropertyEditorRegistrySupport,org.springframework.beans.factory.xml.XmlBeanDefinitionReader,org.springframework.boot.BeanDefinitionLoader,org.springframework.boot.autoconfigure.cache.CacheConfigurations,org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener,org.springframework.boot.logging.LoggingSystem,org.springframework.boot.logging.java.JavaLoggingSystem$Factory,org.springframework.boot.logging.log4j2.Log4J2LoggingSystem$Factory,org.springframework.boot.logging.logback.LogbackLoggingSystem,org.springframework.boot.logging.logback.LogbackLoggingSystem$Factory,org.springframework.cloud.function.web.function.FunctionEndpointInitializer,org.springframework.context.annotation.CommonAnnotationBeanPostProcessor,org.springframework.context.event.EventListenerMethodProcessor,org.springframework.context.support.AbstractApplicationContext,org.springframework.core.DecoratingProxy,org.springframework.core.DefaultParameterNameDiscoverer,org.springframework.core.KotlinDetector,org.springframework.core.NativeDetector,org.springframework.core.ReactiveAdapterRegistry,org.springframework.core.ResolvableType,org.springframework.core.SpringProperties,org.springframework.core.annotation.AnnotationFilter,org.springframework.core.annotation.AnnotationFilter$1,org.springframework.core.annotation.AnnotationFilter$2,org.springframework.core.annotation.AnnotationUtils,org.springframework.core.annotation.PackagesAnnotationFilter,org.springframework.core.annotation.TypeMappedAnnotations,org.springframework.core.io.support.PropertiesLoaderUtils,org.springframework.core.io.support.ResourcePropertiesPersister,org.springframework.core.io.support.SpringFactoriesLoader,org.springframework.format.annotation.DateTimeFormat$ISO,org.springframework.http.HttpStatus,org.springframework.http.MediaType,org.springframework.http.codec.CodecConfigurerFactory,org.springframework.http.codec.support.BaseDefaultCodecs,org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter,org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter,org.springframework.jdbc.datasource.ConnectionProxy,org.springframework.jdbc.support.JdbcAccessor,org.springframework.jdbc.support.JdbcTransactionManager,org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration,org.springframework.nativex.substitutions.boot.NativeSpringBootVersion,org.springframework.transaction.annotation.Isolation,org.springframework.transaction.annotation.Propagation,org.springframework.util.Assert,org.springframework.util.ClassUtils,org.springframework.util.CollectionUtils,org.springframework.util.ConcurrentReferenceHashMap,org.springframework.util.DefaultPropertiesPersister,org.springframework.util.LinkedCaseInsensitiveMap,org.springframework.util.MimeType,org.springframework.util.ReflectionUtils,org.springframework.util.StringUtils,org.springframework.util.unit.DataSize,org.springframework.util.unit.DataUnit,org.springframework.web.client.RestTemplate,org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport,org.springframework.web.servlet.function.support.RouterFunctionMapping,org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver,org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter,org.springframework.web.socket.sockjs.transport.TransportHandlingSockJsService,ch.qos.logback.classic,ch.qos.logback.classic.util,ch.qos.logback.core,org.apache.logging.log4j,org.apache.logging.slf4j,org.jboss.logging,org.springframework.core.env
```

We can build the image and observe successful behavior in the app:
```shell
> ./mvnw clean package spring-boot:build-image

> docker run --rm class-init:0.0.1-SNAPSHOT
2021-04-29 14:58:53.025  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-29 14:58:53.026  INFO 1 --- [           main] com.example.ClassInitApplication         : Starting ClassInitApplication using Java 11.0.10 on dac594dea4ed with PID 1 (/workspace/com.example.ClassInitApplication started by cnb in /workspace)
2021-04-29 14:58:53.026  INFO 1 --- [           main] com.example.ClassInitApplication         : No active profile set, falling back to default profiles: default
2021-04-29 14:58:53.028  INFO 1 --- [           main] com.example.ClassInitApplication         : Started ClassInitApplication in 0.012 seconds (JVM running for 0.015)
Unicode 32 bit CharSet: UTF-32LE
Sleep for 10s...
Done...
```