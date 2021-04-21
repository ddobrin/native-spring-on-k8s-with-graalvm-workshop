# Dynamic Class Loading and Reflection

## Dynamic Classloading
Any class to be accessed by name at image run time must be enumerated at image build time. 

How does it work:
* A call to Class.forName("myClass”) must have myClass in a configuration file. 
* If the configuration file is used, but does not include a class that is requested for dynamic class loading, a `ClassNotFoundException` will be thrown, as if the class was not found on the classpath or was not accessible.

## Reflection
Individual classes, methods, and fields that should be accessible via reflection need to be known ahead-of-time. 

How does it work:
* Native Image tries to resolve these elements through a static analysis that detects calls to the Reflection API. 
* Where the analysis fails, the program elements reflectively accessed at runtime must be specified during native image generation.

The following aspects are included in this category:
* listing methods and fields of a class 
* invoking methods and accessing fields reflectively 
* using other classes in the package `java.lang.reflect`

Let's work through a sample based on `Reflection.java`:
```java
import java.lang.reflect.Method;

class StringReverser {
    static String reverse(String input) {
        return new StringBuilder(input).reverse().toString();
    }
}

class StringCapitalizer {
    static String capitalize(String input) {
        return input.toUpperCase();
    }
}

public class Reflection {
    public static void main(String[] args) throws ReflectiveOperationException {
        String className = args[0];
        String methodName = args[1];
        String input = args[2];

        Class<?> clazz = Class.forName(className);
        Method method = clazz.getDeclaredMethod(methodName, String.class);
        Object result = method.invoke(null, input);
        System.out.println(result);
    }
}
```

The main method invokes all methods whose names are passed as command line arguments. Run it normally and observe the output:
```shell
> java Reflection StringReverser reverse "what is new"
wen si tahw

> java Reflection StringReverser capitalize "what is new"
Exception in thread "main" java.lang.NoSuchMethodException: StringReverser.capitalize(java.lang.String)
	at java.base/java.lang.Class.getDeclaredMethod(Class.java:2475)
	at Reflection.main(Reflection.java:22)
```

As expected, the method `reverse` was found via reflection, but the non-existent method `capitalize` was not found for that Class.

Let's build a native image out of it:

```shell
> native-image --no-fallback Reflection
```

**NOTE**: *The `--no-fallback` option indicates to the native image that it should not revert and created a JVM image, in case the native image build would fail. Fallback images are regular JVM images which can be run using the usual `java Reflection` command*

Run the result and observe in the output that the class StringReverser could not be found, as it was not added to the native image by default
```shell
> ./reflection StringReverser reverse "what is new"
Exception in thread "main" java.lang.ClassNotFoundException: StringReverser
	at com.oracle.svm.core.hub.ClassForNameSupport.forName(ClassForNameSupport.java:60)
	at java.lang.Class.forName(DynamicHub.java:1247)
	at Reflection.main(Reflection.java:21)
```

To build a native image with reflective lookup operations, apply the tracing agent to write a configuration file to be later fed into the native image build together. The config file will provide `hints` to the native image builder in terms of classes to be added 

**NOTE**: Writing a complete reflection configuration file from scratch is possible, but tedious.
Therefore, GraalVM provides an agent for the Java HotSpot VM that produces a reflection configuration file by tracing all reflective lookup operations.
It will also record all usages of the Java Native Interface (JNI), Java Reflection, Dynamic Proxy objects (`java.lang.reflect.Proxy`), or class path resources (`Class.getResource`).

You can specify the agent like the following:

create the directory for the configuration:
```shell
> mkdir -p META-INF/native-image
```

Run the application:
```shell
# introduce the Java agent
# send the output to the config-output-dir
# created above: META-INF/native-image
> java -agentlib:native-image-agent=config-output-dir=META-INF/native-image Reflection StringReverser reverse "what is new"
```

Explore the created configuration:

```shell
> cat META-INF/native-image/reflect-config.json

[
    {
    "name":"StringReverser",
    "methods":[{"name":"reverse","parameterTypes":["java.lang.String"] }]
    }
]
```

At the time of running another class accessed via reflection, `StringCapitalizer`, we observe that the `capitalize` method can't be found, therefore we must run the app again. 
You can have multiple runs recorded and the config merged while specifying the `native-image-agent=config-merge-dir` option:

```shell
# config-merge-dir = merge the content in that dir with the existing content
> java -agentlib:native-image-agent=config-merge-dir=META-INF/native-image Reflection StringCapitalizer capitalize "what is new"

[
    {
    "name":"StringCapitalizer",
    "methods":[{"name":"capitalize","parameterTypes":["java.lang.String"] }]
    },
    {
    "name":"StringReverser",
    "methods":[{"name":"reverse","parameterTypes":["java.lang.String"] }]
    }
]
```

Building the native image now will make use of the provided configuration and configure the reflection for it, covering both StringReverser and StringCapitalizer classes.
```shell
> native-image --no-fallback Reflection
```

Run it and observe the output:
```shell
> ./reflection StringReverser reverse "what is new"
wen si tahw

> ./reflection StringCapitalizer capitalize "what is new"
WHAT IS NEW
```

This is a very convenient way to configure reflection and resources used by the application for building native images.

Next, we'll try to explore some more options how to configure the class initialization strategy for native images.

## Sources
* GraalVM documentation - [link](https://www.graalvm.org/reference-manual/native-image/BuildConfiguration/#assisted-configuration-of-native-image-builds) 