# Java Multithreading Demo for the GraalVM Dashboard

The Multithreading demo is provided to showcase the applicability of [GraalVM Dashboard](https://www.graalvm.org/docs/tools/dashboard/) - a web-based dashboard for visualizing arbitrary aspects of dynamic and static compilations in GraalVM, in particular, [Native Image](https://www.graalvm.org/reference-manual/native-image/).

Code: 
* The demo is a Java program which does synchronous and asynchronous thread execution.
* Each thread loops through exactly the same array of integers and generates a stream of pseudorandom numbers.
* The programs calculate the time taken to perform the task synchronously and asynchronously.

The Multithreading demo is comprised of two sub-projects, each built with Maven: 
* Multithreading Demo Large 
* Multithreading Demo Improved.

The _pom.xml_ file of each sub-project includes the [Native Image Maven plugin](https://www.graalvm.org/reference-manual/native-image/NativeImageMavenPlugin/), which instructs Maven to generate a native image of a JAR file with `all dependencies` included at the `mvn package` build step.

The plugin will gather the diagnostic information during the native image build and write that data to a dump file in the target directory:
* -H:+DashboardAll - dump all available data 
* -H:DashboardDump=../image-dump/multithread-dump-large - location of the dump file

**Tip:** For an immediate assessment of the capabilities of the GraalVM Dashboard, pre-built dashboard data has been made available for both demos, in the `image-dump` subfolder.

```xml
<plugin>
    <groupId>org.graalvm.nativeimage</groupId>
    <artifactId>native-image-maven-plugin</artifactId>
    <version>${graalvm.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>native-image</goal>
            </goals>
            <phase>package</phase>
        </execution>
    </executions>
    <configuration>
        <skip>false</skip>
        <imageName>multithreading-large</imageName>
        <buildArgs>
            <buildArg>--no-fallback</buildArg>
            <buildArg>--initialize-at-build-time</buildArg>
            <buildArg>-H:+DashboardAll</buildArg>
            <buildArg>-H:DashboardDump=../image-dump/multithread-dump-large</buildArg>
        </buildArgs>
        <mainClass>com.example.Multithreading</mainClass>
    </configuration>
</plugin>
```

## Multithreading Demo Large

1. Download or clone the repository and navigate into the _multithreading-demo/multithreading-demo-large_ directory:
```shell
> git clone git@github.com:ddobrin/native-spring-on-k8s-with-graalvm-workshop.git

> cd graalvm/multithreading-demo/multithreading-demo-large
```
2. Build the project:
```
# jvm build
> mvn clean package

# native image build
> mvn clean package -Pnative
```
3. Run the project on a JVM or as a native image:
```
> java -jar target/multithreading-0.0.1-jar-with-dependencies.jar

> ./target/multithreading-large
```

## Multithreading Demo Improved

Multithreading Demo Improved contains an enhanced version of the same program.

1. Download or clone the repository and navigate into the _multithreading-demo/multithreading-demo-improved_ directory:
```
> git clone git@github.com:ddobrin/native-spring-on-k8s-with-graalvm-workshop.git

> cd graalvm/multithreading-demo/multithreading-demo-improved
```
2. Build the project:
```
# jvm build
> mvn clean package

# native image build
> mvn clean package -Pnative
```
3. Run the project on a JVM or as a native image:
```
> java -jar target/multithreading-0.0.1-jar-with-dependencies.jar

> ./target/multithreading-improved
```