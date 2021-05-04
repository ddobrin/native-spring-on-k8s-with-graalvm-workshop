# Accessing Resources in Native Images

The native image builder will not integrate any of the resources which are on the CLASSPaTH while generating the final image. 
To make calls such as `Class.getResource()`, `Class.getResourceAsStream()`, or the corresponding ClassLoader methods, 
return specific resources, instead of `null`, the resources that should be accessible at image run time must be specified explicitly.

Resources to be included or excluded can be specified in 2 ways:
* by providing the resource-config.json, created manually or generated using the javaagent
* by specifying individual resources directly 

Let's start with the `ResourceAccess` class which reads a `config.properties` file:
```java
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceAccess {
    public static void main(String[] args) {
        Properties prop = new Properties();

        // read properties
        try (InputStream inputStream = ResourceAccess.class
                                        .getClassLoader()
                                        .getResourceAsStream("config.properties")) {
            prop.load(inputStream);
            System.out.println("Reading config.threads property: " + prop.getProperty("config.threads"));
            System.out.println("Reading config.load property: " + prop.getProperty("config.load"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Compile and run the class with the Java:
```shell
> cat config.properties 
config.threads=10
config.load=200

> javac ResourceAccess.java

> java ResourceAccess
Reading config.threads property: 10
Reading config.load property: 200
```

Let's try and build a native image and observe that resources can't be loaded at runtime:
```shell
> native-image ResourceAccess

> ./resourceaccess 
Exception in thread "main" java.lang.NullPointerException: inStream parameter is null
	at java.util.Objects.requireNonNull(Objects.java:246)
	at java.util.Properties.load(Properties.java:406)
	at ResourceAccess.main(ResourceAccess.java:12)
```

**First option:** using the javaagent
```shell
> mkdir -p META-INF/native-image

> java -agentlib:native-image-agent=config-output-dir=META-INF/native-image ResourceAccess
Reading config.threads property: 10
Reading config.load property: 200

# observe that config.properties has been added to the resource-config file
> cat META-INF/native-image/resource-config.json 
{
  "resources":{
  "includes":[{"pattern":"\\Qconfig.properties\\E"}]},
  "bundles":[]
}
```

Let's run the app and observe correct behaviour: 
```shell
> native-image --no-fallback  ResourceAccess

> ./resourceaccess 
Reading config.threads property: 10
Reading config.load property: 200
```

**Alternatively:** individual resource paths can also be specified directly to `native-image`:
```shell
# let's remove the previously created config resources
> rm -rf META-INF/native-image

> native-image -H:IncludeResources=config.properties ResourceAccess

> ./resourceaccess 
Reading config.threads property: 10
Reading config.load property: 200
```

## Sources
* GraalVM documentation - [link](https://www.graalvm.org/reference-manual/native-image/Resources/) 
