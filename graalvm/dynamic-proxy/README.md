# Dynamic Proxy in Native Images

Java dynamic proxies, implemented by `java.lang.reflect.Proxy`, provide a mechanism enabling object level access 
control, by routing all method invocations through `java.lang.reflect.InvocationHandler`. 
Dynamic proxy classes are generated from a list of interfaces.

Native Image does not provide tooling for generating and interpreting bytecodes at run time. 
Therefore, all dynamic proxy classes need to be generated at native image build time.

One of the language features that require explicit configuration besides Reflection are JDK proxies. 
We can register the classes which will be proxied at runtime by using the same javaagent as in the dynamic classloading and reflection sample.

Let's consider the following `DynamicProxy` Java class:
```java
import java.lang.reflect.Proxy;
import java.util.Map;

public class DynamicProxy {
    public static void main(String[] args) throws Exception {
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
The main method creates a JDK proxy for the class the name passed in via arguments. 

**Note**: if we use class literals, static analysis is smart enough to figure it out and include the necessary classes itself.

Let's run the sample:
```
> javac DynamicProxy.java

> java DynamicProxy java.util.Map
Invoking method: <get> in a <java.util.Map>. Output: 42
Invoking method: <put> in a <java.util.Map>
Expected an exception, got an exception
```
As expected the program produces "Output: 42" while invoking get() and then receives the exception and logs it as it is executing a put() method. 

When building a native image, the build process succeeds, however the execution fails, as the `java.util.Map` could not be found in the native image.
```shell
> native-image --no-fallback DynamicProxy

> ./dynamicproxy java.util.Map
Exception in thread "main" java.lang.ClassNotFoundException: java.util.Map
	at com.oracle.svm.core.hub.ClassForNameSupport.forName(ClassForNameSupport.java:60)
	at java.lang.Class.forName(DynamicHub.java:1247)
	at DynamicProxy.main(DynamicProxy.java:13)
```

Let's generate now the configuration using the assisted configuration agent:
```shell
> mkdir -p META-INF/native-image

> java -agentlib:native-image-agent=config-output-dir=META-INF/native-image DynamicProxy java.util.Map
Invoking method: <get> in a <java.util.Map>. Output: 42
Invoking method: <put> in a <java.util.Map>
Expected an exception, got an exception
```

The `META-INF/native-image` directory now has the configuration files. We expect 2 files to have content generated for:
* reflection-config.json - Map created using Class.forName()
* proxy-config.json - proxied Map

```shell
> cat META-INF/native-image/reflect-config.json
[
    {
      "name":"java.util.Map"
    }
]

> cat META-INF/native-image/proxy-config.json
[
  ["java.util.Map"]
]
```

While the configuration for proxies is very straightforward, the best practice is to generate it by running the application with the javaagent, 
as you might have a lot more elements to configure than in this sample code.

Native image build will be successful, and we can now observe that the invocation succeeds as well:
```shell
> native-image --no-fallback DynamicProxy

> ./dynamicproxy java.util.Map
Invoking method: <get> in a <java.util.Map>. Output: 42
Invoking method: <put> in a <java.util.Map>
Expected an exception, got an exception
```

