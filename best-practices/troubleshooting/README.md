


```java

# intercept an application event
...
org.springframework.boot.context.event.ApplicationReadyEvent[source=org.springframework.boot.SpringApplication@264c5d07]
...
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
            UUID uuid = load("com.example.demo.UUID");
            log.info("Assign unique ID to a Bear: " + uuid.uid);
            log.info("Invoke talk(). Proxied message: " + bear.talk());
            log.info("Invoke eat(). Proxied message: " + bear.eat());
    
            log.info("Serialize the ID assigned to the bear to the file: bear-id");
            var ids = new ArrayList<String>();
            ids.add(uuid.toString());
                
            try (ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream("bear-id"))) {
                oss.writeObject(ids);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
```

```shell
2021-05-03 13:27:04.967  INFO 57692 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 0.398 seconds (JVM running for 0.653)
2021-05-03 13:27:04.986  INFO 57692 --- [           main] com.example.demo.Initializer             : Assign unique ID to a Bear: 96da5e22-fb30-48a3-affa-234596b19158
2021-05-03 13:27:04.988  INFO 57692 --- [           main] com.example.demo.DemoApplication         : Before interception...
2021-05-03 13:27:04.989  INFO 57692 --- [           main] com.example.demo.Initializer             : Invoke talk(). Proxied message: Proxied method: Bear talks() ... After interception...
2021-05-03 13:27:04.989  INFO 57692 --- [           main] com.example.demo.DemoApplication         : Before interception...
2021-05-03 13:27:04.989  INFO 57692 --- [           main] com.example.demo.Initializer             : Invoke eat(). Proxied message: No method intercepted. Bear must be eat()ing ... After interception...
2021-05-03 13:27:04.989  INFO 57692 --- [           main] com.example.demo.Initializer             : Serialize the ID assigned to the bear to the file: bear-id
```

Build a native image and troubleshoot the failures:
```shell
> ./mvnw clean spring-boot:build-image 

> docker run --rm troubleshooting:0.0.1-SNAPSHOT

Fails with error:
org.springframework.beans.factory.UnsatisfiedDependencyException: 
  Error creating bean with name 'initializer' defined in class path resource [com/example/demo/Initializer.class]: 
    Unsatisfied dependency expressed through constructor parameter 0; 
      nested exception is org.springframework.beans.factory.BeanCreationException: 
        Error creating bean with name 'bear' defined in com.example.demo.DemoApplication: 
          Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: 
            Failed to instantiate [com.example.demo.Bear]: 
              Factory method 'bear' threw exception; 
                nested exception is com.oracle.svm.core.jdk.UnsupportedFeatureError: 
                  Proxy class defined by interfaces 
                  [interface com.example.demo.Bear, interface org.springframework.aop.SpringProxy, interface org.springframework.aop.framework.Advised, interface org.springframework.core.DecoratingProxy] not found. 
                  Generating proxy classes at runtime is not supported. 
                  Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. 
                  To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.
```

Spring Native indicates how to correct this error:
* Add a ProxyHint with the following 4 classes to be proxied (in the listed order !!!)
* Add manually `-H:DynamicProxyConfigurationFiles=<comma-separated-config-files>` and `-H:DynamicProxyConfigurationResources=<comma-separated-config-resources>` to the `pom.xml and configure the proxy resources

Let's add a `@ProxyHint` and observe the proxy resources being generated:

```java
@ProxyHint(typeNames = {
		"com.example.demo.Bear",
		"org.springframework.aop.SpringProxy",
		"org.springframework.aop.framework.Advised",
		"org.springframework.core.DecoratingProxy"
})
@Log4j2
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
```

The `proxy-config.json` will reflect the generated proxy resources:
```json
    [
      "com.example.demo.Bear",
      "org.springframework.aop.SpringProxy",
      "org.springframework.aop.framework.Advised",
      "org.springframework.core.DecoratingProxy"
    ],
 
```

Build and run again:
```shell
> ./mvnw clean spring-boot:build-image 

> docker run --rm troubleshooting:0.0.1-SNAPSHOT

java.lang.ClassNotFoundException: com.example.demo.UUID
	at com.oracle.svm.core.hub.ClassForNameSupport.forName(ClassForNameSupport.java:60) ~[na:na]
	at java.lang.Class.forName(DynamicHub.java:1247) ~[na:na]
	at com.example.demo.Initializer.load(DemoApplication.java:84) ~[com.example.demo.DemoApplication:na]
	at com.example.demo.Initializer.onApplicationEvent(DemoApplication.java:90) ~[com.example.demo.DemoApplication:na]
```

In this case, it indicates that a class could not be found, therefore we have to add a `@ReflectionHint` for the missing class or manually change the configuration in `reflection-proxy.json`:
```java
@ProxyHint(typeNames = {
		"com.example.demo.Bear",
		"org.springframework.aop.SpringProxy",
		"org.springframework.aop.framework.Advised",
		"org.springframework.core.DecoratingProxy"
})
@TypeHint(typeNames = {"com.example.demo.UUID"})
@Log4j2
@SpringBootApplication
public class DemoApplication {
...
```

We observe the last error for this demo application at this time:
```shell
> ./mvnw clean spring-boot:build-image 

> docker run --rm troubleshooting:0.0.1-SNAPSHOT

com.oracle.svm.core.jdk.UnsupportedFeatureError: The offset of private int java.util.ArrayList.size is accessed without the field being first registered as unsafe accessed. Please register the field as unsafe accessed. You can do so with a reflection configuration that contains an entry for the field with the attribute "allowUnsafeAccess": true. Such a configuration file can be generated for you. Read BuildConfiguration.md and Reflection.md for details.
	at com.oracle.svm.core.util.VMError.unsupportedFeature(VMError.java:87) ~[na:na]
	at jdk.internal.misc.Unsafe.objectFieldOffset(Unsafe.java:50) ~[na:na]
	at java.io.ObjectStreamClass$FieldReflector.<init>(ObjectStreamClass.java:2002) ~[na:na]
	at java.io.ObjectStreamClass.getReflector(ObjectStreamClass.java:2266) ~[na:na]
	at java.io.ObjectStreamClass.<init>(ObjectStreamClass.java:534) ~[na:na]
	at java.io.ObjectStreamClass.lookup(ObjectStreamClass.java:381) ~[na:na]
	at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1135) ~[na:na]
	at java.io.ObjectOutputStream.writeObject(ObjectOutputStream.java:349) ~[na:na]
-->	at com.example.demo.Initializer.onApplicationEvent(DemoApplication.java:99) ~[com.example.demo.DemoApplication:na]
	at com.example.demo.Initializer.onApplicationEvent(DemoApplication.java:74) ~[com.example.demo.DemoApplication:na]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:176) ~[na:na]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:169) ~[na:na]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:143) ~[na:na]
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:421) ~[na:na]
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:378) ~[na:na]
	at org.springframework.boot.context.event.EventPublishingRunListener.running(EventPublishingRunListener.java:111) ~[com.example.demo.DemoApplication:2.4.5]
	at org.springframework.boot.SpringApplicationRunListeners.lambda$running$6(SpringApplicationRunListeners.java:79) ~[na:na]
	at java.util.ArrayList.forEach(ArrayList.java:1541) ~[com.example.demo.DemoApplication:na]
	at org.springframework.boot.SpringApplicationRunListeners.doWithListeners(SpringApplicationRunListeners.java:117) ~[na:na]
	...
```

We can observe the first error occurring 

```json
  {
    "name": "java.util.ArrayList"
  }
```

```java
// Uncomment for fixing: Proxy hint
@ProxyHint(typeNames = {
		"com.example.demo.Bear",
		"org.springframework.aop.SpringProxy",
		"org.springframework.aop.framework.Advised",
		"org.springframework.core.DecoratingProxy"
})

// Uncomment for fixing Refelection hint
@TypeHint(typeNames = {"com.example.demo.UUID"})

// Uncomment for fixing Serialization hint
@SerializationHint(
		types = {
				java.util.ArrayList.class
		})
@Log4j2
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
...
```

```shell
ddobrin-a01:troubleshooting dandobrin$ docker run --rm troubleshooting:0.0.1-SNAPSHOT
2021-05-03 18:27:02.906  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-05-03 18:27:02.907  INFO 1 --- [           main] com.example.demo.DemoApplication         : Starting DemoApplication using Java 11.0.10 on d0488b3b8259 with PID 1 (/workspace/com.example.demo.DemoApplication started by cnb in /workspace)
2021-05-03 18:27:02.908  INFO 1 --- [           main] com.example.demo.DemoApplication         : No active profile set, falling back to default profiles: default
2021-05-03 18:27:02.921  INFO 1 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 0.024 seconds (JVM running for 0.027)
2021-05-03 18:27:02.922  INFO 1 --- [           main] com.example.demo.Initializer             : Assign unique ID to a Bear: a957210f-03aa-4637-a32f-6dfafa45d9cf
2021-05-03 18:27:02.922  INFO 1 --- [           main] com.example.demo.DemoApplication         : Before interception...
2021-05-03 18:27:02.922  INFO 1 --- [           main] com.example.demo.Initializer             : Invoke talk(). Proxied message: Proxied method: Bear talks() ... After interception...
2021-05-03 18:27:02.922  INFO 1 --- [           main] com.example.demo.DemoApplication         : Before interception...
2021-05-03 18:27:02.922  INFO 1 --- [           main] com.example.demo.Initializer             : Invoke eat(). Proxied message: No method intercepted. Bear must be eat()ing ... After interception...
2021-05-03 18:27:02.922  INFO 1 --- [           main] com.example.demo.Initializer             : Serialize the ID assigned to the bear to the file: bear-id
```