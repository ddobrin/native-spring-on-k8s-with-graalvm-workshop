# Serialization in Native Images

Java serialization requires class metadata information in order to function and must be specified during the generation of a native image generation.

However, Java serialization has been a persistent source of security vulnerabilities. 
The Java architects have announced that the existing serialization mechanism will be replaced with a new mechanism avoiding these problems in the near future.

At this time, to support serialization in native images, class metadata must be provided in the `serialization-config.json` file, manually or collected using the javaagent.

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/graalvm/serialization
```
----

## The sample

```shell
> javac Serialization.java 

> java Serialization
java Serialization 
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

## observe after the run that the serialized file has been created: serialized_objects_in_stream
```

Let's build a native image, while not allowing for a fallback class to be created
```shell
# build native image - do not create a fallback class in case of failure
> native-image --no-fallback Serialization

# build is successful, however
# observe that ArrayList has not been found in the native image at runtime
> ./serialization 
Exception in thread "main" java.lang.ClassNotFoundException: java.util.ArrayList
	at com.oracle.svm.core.hub.ClassForNameSupport.forName(ClassForNameSupport.java:60)
	at java.lang.Class.forName(DynamicHub.java:1260)
	at java.io.ObjectInputStream.resolveClass(ObjectInputStream.java:756)
	at java.io.ObjectInputStream.readNonProxyDesc(ObjectInputStream.java:1995)
	at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1862)
	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2169)
	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1679)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:493)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:451)
	at Serialization.main(Serialization.java:23)
```

We will generate the configuration with the javaagent then rebuild the native image.
```shell
> mkdir -p META-INF/native-image

# GraalVM agent tracks all usages of dynamic features of an execution on a regular Java VM
# run the application with a 
> java -agentlib:native-image-agent=config-output-dir=META-INF/native-image Serialization

# observe the files created under /config/META-INF/native-image
> ls -m META-INF/native-image/
jni-config.json, proxy-config.json, reflect-config.json, resource-config.json, serialization-config.json

# observe the serialization-config.json
> cat META-INF/native-image/serialization-config.json 
[
  {
  "name":"java.lang.Long"
  },
  {
  "name":"java.lang.Number"
  },
  {
  "name":"java.util.ArrayList"
  }
]
```

Let's build the native image again and expect a correct behaviour.
```shell
# build the native image
> native-image Serialization

# observe that the application runs fine
> ./serialization 
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
## Sources
* GraalVM documentation - [link](https://www.graalvm.org/reference-manual/native-image/Limitations/#serialization) 

