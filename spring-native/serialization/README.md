

```shell
> ./mvnw clean package

> java -jar target/serialization-0.0.1-SNAPSHOT.jar 
2021-04-28 14:05:16.192  INFO 28929 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-28 14:05:16.286  INFO 28929 --- [           main] com.example.SerializationApplication     : Starting SerializationApplication v0.0.1-SNAPSHOT using Java 11.0.10 on ddobrin-a01.vmware.com with PID 28929 (/Users/dandobrin/work/native/start.spring/serialization/target/serialization-0.0.1-SNAPSHOT.jar started by dandobrin in /Users/dandobrin/work/native/start.spring/serialization)
2021-04-28 14:05:16.286  INFO 28929 --- [           main] com.example.SerializationApplication     : No active profile set, falling back to default profiles: default
2021-04-28 14:05:16.428  INFO 28929 --- [           main] com.example.SerializationApplication     : Started SerializationApplication in 0.596 seconds (JVM running for 1.137)
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

```shell
> ./mvnw clean package spring-aot:generate

> ./mvnw clean package spring-boot:build-image

> docker run --rm serialization:0.0.1-SNAPSHOT
2021-04-28 18:28:40.360  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-28 18:28:40.361  INFO 1 --- [           main] com.example.SerializationApplication     : Starting SerializationApplication using Java 11.0.10 on c5955eeb1bc1 with PID 1 (/workspace/com.example.SerializationApplication started by cnb in /workspace)
2021-04-28 18:28:40.361  INFO 1 --- [           main] com.example.SerializationApplication     : No active profile set, falling back to default profiles: default
2021-04-28 18:28:40.363  INFO 1 --- [           main] com.example.SerializationApplication     : Started SerializationApplication in 0.012 seconds (JVM running for 0.014)
2021-04-28 18:28:40.364 ERROR 1 --- [           main] o.s.boot.SpringApplication               : Application run failed

com.oracle.svm.core.jdk.UnsupportedFeatureError: The offset of private int java.util.ArrayList.size is accessed without the field being first registered as unsafe accessed. Please register the field as unsafe accessed. You can do so with a reflection configuration that contains an entry for the field with the attribute "allowUnsafeAccess": true. Such a configuration file can be generated for you. Read BuildConfiguration.md and Reflection.md for details.
	at com.oracle.svm.core.util.VMError.unsupportedFeature(VMError.java:87) ~[na:na]
	at jdk.internal.misc.Unsafe.objectFieldOffset(Unsafe.java:50) ~[na:na]
	at java.io.ObjectStreamClass$FieldReflector.<init>(ObjectStreamClass.java:2002) ~[na:na]
	at java.io.ObjectStreamClass.getReflector(ObjectStreamClass.java:2266) ~[na:na]
	at java.io.ObjectStreamClass.<init>(ObjectStreamClass.java:534) ~[na:na]
	at java.io.ObjectStreamClass.lookup(ObjectStreamClass.java:381) ~[na:na]
	at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1135) ~[na:na]
	at java.io.ObjectOutputStream.writeObject(ObjectOutputStream.java:349) ~[na:na]
	at com.example.Serialization.run(Serialization.java:34) ~[com.example.SerializationApplication:na]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:819) ~[com.example.SerializationApplication:2.4.5]
	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:803) ~[com.example.SerializationApplication:2.4.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:346) ~[com.example.SerializationApplication:2.4.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1340) ~[com.example.SerializationApplication:2.4.5]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1329) ~[com.example.SerializationApplication:2.4.5]
	at com.example.SerializationApplication.main(SerializationApplication.java:9) ~[com.example.SerializationApplication:na]
```

```java
@SerializationHint(
		types = {
				java.util.ArrayList.class,
				java.lang.Long.class,
				java.lang.Number.class
		}
)
```


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

```shell
> ./mvnw clean package spring-boot:build-image

> docker run --rm serialization:0.0.1-SNAPSHOT
2021-04-28 18:42:06.085  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-04-28 18:42:06.086  INFO 1 --- [           main] com.example.SerializationApplication     : Starting SerializationApplication using Java 11.0.10 on b5e64ce3579a with PID 1 (/workspace/com.example.SerializationApplication started by cnb in /workspace)
2021-04-28 18:42:06.086  INFO 1 --- [           main] com.example.SerializationApplication     : No active profile set, falling back to default profiles: default
2021-04-28 18:42:06.088  INFO 1 --- [           main] com.example.SerializationApplication     : Started SerializationApplication in 0.012 seconds (JVM running for 0.014)
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