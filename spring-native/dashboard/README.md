# Java Function Demo for the GraalVM Dashboard

This application builds upon the concepts presented in the GraalVM chapter on using the GraalVM Dashboard.
<br><br>
Please revisit the **[GraalVM Dashboard Doc](../../graalvm/multithreading-demo/README.md)**

The GraalVM plugin will gather the diagnostic information during the native image build and write that data to a dump file in the target directory,
while specifying the following two flags:
* -H:+DashboardAll - dump all available data
* -H:DashboardDump=../image-dump/function-sample-dump - location of the dump file

To collect dump info for visualization in GraalVM for the Spring Function used in the [Hands-on-Lab](../spring-native-app/README.md), 
all you have to do is update the `pom.xml` file: 
```xml
<!--Update the <native> profile-->
...
<profile>
    <id>native</id>
    <build>
        <plugins>
            <plugin>
                <groupId>org.graalvm.nativeimage</groupId>
                <artifactId>native-image-maven-plugin</artifactId>
                <version>21.0.0.2</version>
                <configuration>
                    <mainClass>com.example.hello.SpringFunctionApplication</mainClass>
                    <buildArgs>-Dspring.native.remove-yaml-support=true
                        -Dspring.spel.ignore=true
                        -Dspring.xml.ignore=true
                        -H:DashboardDump=../image-dump/function-sample-dump
                        -H:+DashboardAll</buildArgs>
                    <imageName>${project.artifactId}</imageName>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>native-image</goal>
                        </goals>
...
```
Build the native image and observe the BGV file being created:
```shell
> ./mvnw clean package -Pnative

> tree image-dump/
image-dump/
└── function-sample-dump.bgv
```

**NOTE**: A sample file has been uploaded for convenience in the `/image-dump` folder. 
Follow the steps in **[GraalVM Dashboard Doc](../../graalvm/multithreading-demo/README.md)** to visualize the dump file.