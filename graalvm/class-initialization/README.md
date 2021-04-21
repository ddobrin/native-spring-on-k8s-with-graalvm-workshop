# Class initialization strategy for GraalVM native images

It is very important for us to understand one of the most misunderstood features of native image: the class initialization strategy.

Classes in an application need to be initialized before being used. The lifecycle of the native image is split into two parts: build time and run time.

By default, classes are initialized at runtime. Sometimes it makes sense for optimization or other pourposes to initialize some classes at build time.

Let's explore an example application consisting of a few classes in order to:
* get a better understanding of the implications of initialization at runtime or build time 
* how to configure the initialization strategy

## Part 1
Let's start from a simple Java app:
```java
import java.nio.charset.*;
import java.util.UUID;

public class ClassInit {
    public static void main(String[] args) {
        First.second.printIt();
    }
}

class First {
    public static Second second = new Second();
}

class Second {
    private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");

    public void printIt() {
        System.out.println("Unicode 32 bit CharSet: " + UTF_32_LE);
    }
}
```

It consists of 3 classes: ClassInit, calling `First.second.prinIt()`, with First holding a reference to a second instance of Class Second, which in turn 
holds a reference to a CharSet in a static field, after loading a non-standard CharSet - UTF-32LE.

Let's run it and explore the output:
```shell
> javac ClassInit.java

> java ClassInit
Unicode 32 bit CharSet: UTF-32LE
```

Let's now build a native image out of it, run it, and explore the output:
```shell 
> native-image -cp . ClassInit

> ./classinit
```

It breaks with the following exception, because the Charset "UTF_32_LE", as non-standard, is not by default included in the native image can't be found:
```
Exception in thread "main" java.lang.ExceptionInInitializerError
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.initialize(ClassInitializationInfo.java:315)
	at First.<clinit>(ClassInit.java:11)
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.invokeClassInitializer(ClassInitializationInfo.java:375)
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.initialize(ClassInitializationInfo.java:295)
	at ClassInit.main(ClassInit.java:6)
Caused by: java.nio.charset.UnsupportedCharsetException: UTF-32LE
	at java.nio.charset.Charset.forName(Charset.java:529)
	at Second.<clinit>(ClassInit.java:30)
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.invokeClassInitializer(ClassInitializationInfo.java:375)
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.initialize(ClassInitializationInfo.java:295)
	... 4 more
```

There is a lesser documented feature in GraalVM to resolve this issue by including all charsets, which you can test if you like:
```shell 
> native-image -H:+AddAllCharsets ClassInit
```

It does help us understand the Dynamic Class Loading issue we are facing and how to address them. 
We're more interested in the class init details at this time. 

To track which classes were initialized and why, one can use the flag `-H:+PrintClassInitialization`. 
This flag greatly helps to configure the image build to work as intended. The goal is to have as many classes as possible initialized at build time, yet keep the correct semantics of the program !

Let's use the `-H:+PrintClassInitialization` argument to check how are classes initialized:
```shell 
> native-image -H:+PrintClassInitialization ClassInit
```

Check the output, as generated in the reports sub-folder
```shell
> tree reports/

reports/
├── build_time_classes_20210421_131831.txt
├── initializer_configuration_20210421_131824.txt
├── initializer_dependencies_20210421_131831.dot
├── rerun_classes_20210421_131831.txt
├── run_time_classes_20210421_131831.txt
└── safe_classes_20210421_131831.txt

> cat reports/run_time_classes_*
First
Second
apple.security.KeychainStore
com.sun.jndi.ldap.Connection
javax.xml.datatype.DatatypeFactory
javax.xml.stream.FactoryFinder
javax.xml.transform.FactoryFinder

> cat reports/build_time_classes_*
ClassInit
boolean
boolean[]
boolean[][]
byte
...
```

**NOTEs**: 
* All class-initialization code (static initializers and static field initialization) of the application you build an image for is executed at image run time by default. 
* Sometimes it is beneficial to allow class initialization code to get executed at image build time for faster startup (e.g., if some static fields get initialized to run-time independent data) or to load specific data, as in our case UTF-32.

Observe that: 
* ClassInit can be found in the list of class initialized at build-time, it has the main() method inside
* First and Second can be found as expected in the class initialized at runtime

Now, initializing the class `Second` means running it's `<clinit>` to initialize the static field, therefore it tries to load the charset and breaks at runtime.

**NOTE**: initialization can be controlled with the following native-image options:
```shell
--initialize-at-build-time=<comma-separated list of packages and classes>
--initialize-at-run-time=<comma-separated list of packages and classes>
```
What we can do in this case is move the initialization to build-time, which will succeed because build time is a Java process in itself and it'll load the CharSet without any problems.
```shell
> native-image -H:+PrintClassInitialization --initialize-at-build-time=First,Second ClassInit

> ./classinit 
Unicode 32 bit CharSet: UTF-32LE

> cat reports/build_time_classes_* 
ClassInit
First
Second
...
```
The classes are initialized at build time, the Charset instance is written out to the image heap and can be used at runtime.

## Part 2
Sometimes objects instantiated during the build class initialization cannot be written out and used at runtime:
* opened files
* running threads
* opened network sockets
* Random instances

If the analysis sees them in the image heap -- it'll notify you and ask to initialize the classes holding them at runtime.

For example, let's modify the code to add the creation of a Thread within class First, with a sleep of 10 seconds. You can uncomment /* Part 2 */ in the ClassInit sample class: 
```java
import java.nio.charset.*;
import java.util.UUID;

public class ClassInit {
    public static void main(String[] args) {
        First.second.printIt();
    }
}

class First {
    public static Second second = new Second();

    // part 2 of the exercise
    public static Thread t;

    static {
        t = new Thread(()-> {
            try {
                System.out.println("Sleep for 10s...");
                Thread.sleep(10_000);
                System.out.println("Done...");
            } catch (Exception e){}
        });
        t.start();
    } 
}

class Second {
    private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");

    public void printIt() {
        System.out.println("Unicode 32 bit CharSet: " + UTF_32_LE);
    }
}
```

Building the native image like before will fail, please observe how, it's quite interesting:
```shell
> javac ClassInit.java 

> java ClassInit
Sleep for 10s...
Unicode 32 bit CharSet: UTF-32LE
Done...

> native-image --no-fallback -H:+PrintClassInitialization --initialize-at-build-time=First,Second ClassInit

> ./classinit 
Unicode 32 bit CharSet: UTF-32LE
```

Observe that the running thread could not be written out, therefore we have `incorrect behavior`.

Balancing initialization can be a bit tricky, this is why by default GraalVM initializes classes at runtime. 

So for this example to work correctly, we should have only `Second` to be initialized at build time.
```shell
> native-image --no-fallback -H:+PrintClassInitialization --initialize-at-build-time=Second ClassInit
```

Running it will take 10 seconds now because of the `Thread.sleep`
```shell
> ./classinit
Unicode 32 bit CharSet: UTF-32LE
Sleep for 10s...
Done...

```

Correspondingly, you can use the `--initialize-at-run-time=package.MyClass` option to make classes be initialized at runtime.

 #### `lab end`