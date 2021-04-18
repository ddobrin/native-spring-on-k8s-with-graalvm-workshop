# Serialization example

```shell
> javac Serialization.java 
> java Serialization
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

# build native image - do not create a fallback class in case of failure
> native-image --no-fallback Serialization

# observe that ArrayList has not been found in the native image
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


> mkdir -p config/META-INF/native-image

# GraalVM agent tracks all usages of dynamic features of an execution on a regular Java VM
# run the application with a 
> java -agentlib:native-image-agent=config-output-dir=config/META-INF/native-image Serialization

# observe the files created under /config/META-INF/native-image
> ls -l config/META-INF/native-image/
total 40
-rw-r--r--  1 dandobrin  staff    4 18 Apr 17:38 jni-config.json
-rw-r--r--  1 dandobrin  staff    4 18 Apr 17:38 proxy-config.json
-rw-r--r--  1 dandobrin  staff    4 18 Apr 17:38 reflect-config.json
-rw-r--r--  1 dandobrin  staff   53 18 Apr 17:38 resource-config.json
-rw-r--r--  1 dandobrin  staff  115 18 Apr 17:38 serialization-config.json

# observe the serialization-config.json
> cat config/META-INF/native-image/serialization-config.json 
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

# build the native image
> native-image -cp config:. Serialization

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

