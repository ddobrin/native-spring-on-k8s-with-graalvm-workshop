# Designing a native-friendly Spring library

Best practices on code reuse apply to the Native apps ecosystem as well and can significantly
improve consistency, reduce development time and troubleshooting efforts

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/best-practices/shared-hints
```
----

## The sample
Let's use as an example the `Demo` application in the [Troubleshooting](../troubleshooting/README.md) section and abstract all @NativeHints 
into a separate `hints library`, now shareable across multiple applications.

We'll use this shared library in the [Demo with Shared Hints](../demo-shared-hints/README.md) section, for illustration.

NativeConfiguration:
* if there is already a NativeConfiguration implementation for this configuration, it can be augmented with the extra type info. 
* if there is none, one must be created, implementing the `NativeConfiguration` interface, and all required native hints can be attached to it.

The class must be added to `META-INF/services/org.springframework.nativex.extension.NativeConfiguration`, from where regular class loading will pick it up.
```java
// META-INF/services/org.springframework.nativex.extension.NativeConfiguration
hints.SharedHints
```

All hints we wish to reuse are provided in the `SharedHints` class: 
```java
@JdkProxyHint(typeNames = {
    "com.example.demo.Bear",
    "org.springframework.aop.SpringProxy",
    "org.springframework.aop.framework.Advised",
    "org.springframework.core.DecoratingProxy"
})
@TypeHint(typeNames = {"com.example.demo.UUID"})
@SerializationHint(
    types = {
        java.util.ArrayList.class
    })

public class SharedHints implements NativeConfiguration {
	@Override
	public List<HintDeclaration> computeHints(TypeSystem typeSystem) {
        // Sometimes the necessary configuration is hard to statically declare and needs a more dynamic approach. 
        // For example, the interfaces involved in a proxy hint might need something to be checked beyond the simple presence of a class. 
        // In this case the method computeHints can be implemented which allows computation of hints in a more dynamic way, 
        // which are then combined with those statically declared via annotations
        
		// create Dynamic Hints here as an exercise
		return Collections.emptyList();
	}
}
```

The library must be built and made available in the Maven repo. In this case, we install it in the local `.m2` repo:
```shell
> ./mvnw clean install

...
[INFO] --- maven-install-plugin:2.5.2:install (default-install) @ shared-hints ---
[INFO] Installing /Users/dandobrin/.../shared-hints/target/shared-hints-0.0.1.jar to /Users/dandobrin/.m2/repository/com/example/shared-hints/0.0.1/shared-hints-0.0.1.jar
[INFO] Installing /Users/dandobrin/.../shared-hints/pom.xml to /Users/dandobrin/.m2/repository/com/example/shared-hints/0.0.1/shared-hints-0.0.1.pom
,,,
```

To reuse the shared hints, all we need to do is add the new library as a shared dependency to the Spring AOT plugin config, in any application:
```xml
            <plugin>
                <groupId>org.springframework.experimental</groupId>
                <artifactId>spring-aot-maven-plugin</artifactId>
                <version>${spring-native.version}</version>
                <dependencies>
                    <dependency>
                        <groupId>com.example</groupId>
                        <artifactId>shared-hints</artifactId>
                        <version>0.0.1</version>
                    </dependency>
                </dependencies>

```