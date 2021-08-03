
# Building and Containerizing Native Images with the GraalVM Maven plugin and Docker

----
## The code

This workshop repository can be cloned to your machine as follows:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm
```

**`Full example code is provided for this section !`**

This example sample is relative to the repository root:
```shell
<repo-root>/graalvm/hello-workshop
```
----

## The sample

## Building a JVM image with the Maven plugin
```shell
# building a JVM message
> ./mvnw clean package

> ls -lart target
...
-rw-r--r--   1 dandobrin  staff  2820 Aug  3 15:38 hello-workshop-0.0.1-jar-with-dependencies.jar
...

> java -jar target/hello-workshop-0.0.1-jar-with-dependencies.jar 

***** Hello, Workshop Participants! *****
...
***** done *****
```

The GraalVM Maven plugin:
```xml
   <profiles>
    <profile>
        <id>native</id>
        <properties>
            <repackage.classifier>exec</repackage.classifier>
            <native-buildtools.version>0.9.1</native-buildtools.version>
        </properties>
        <dependencies>
            <dependency>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>junit-platform-native</artifactId>
                <version>${native-buildtools.version}</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <version>${native-buildtools.version}</version>
                    <executions>
                        <execution>
                            <id>test-native</id>
                            <phase>test</phase>
                            <goals>
                                <goal>test</goal>
                            </goals>
                        </execution>
                        <execution>
                            <id>build-native</id>
                            <phase>package</phase>
                            <goals>
                                <goal>build</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <skip>false</skip>
                        <imageName>helloworkshop</imageName>
                        <buildArgs>--no-fallback</buildArgs>
                        <mainClass>com.example.Helloworkshop</mainClass>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
  </profiles>
```

To check whether the native toolchain is accessible, then show native toolchain information and image’s build settings:
```shell
> native-image --native-image-info -jar target/hello-workshop-0.0.1-jar-with-dependencies.jar 
[hello-workshop-0.0.1-jar-with-dependencies:60432]    classlist:     879.57 ms,  0.96 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]        (cap):   1,883.99 ms,  0.96 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]        setup:   3,003.31 ms,  0.96 GB
# Building image for target platform: org.graalvm.nativeimage.Platform$DARWIN_AMD64
# Using native toolchain:
#   Name: LLVM (clang)
#   Vendor: apple
#   Version: 11.0.0
#   Target architecture: x86_64
#   Path: /usr/bin/cc
# Using CLibrary: com.oracle.svm.core.c.libc.NoLibC
[hello-workshop-0.0.1-jar-with-dependencies:60432]     (clinit):     142.86 ms,  1.74 GB
# Static libraries:
#   ../../../../../.sdkman/candidates/java/21.2.0.r11-grl/lib/svm/clibraries/darwin-amd64/liblibchelper.a
#   ../../../../../.sdkman/candidates/java/21.2.0.r11-grl/lib/static/darwin-amd64/libnet.a
#   ../../../../../.sdkman/candidates/java/21.2.0.r11-grl/lib/svm/clibraries/darwin-amd64/libdarwin.a
#   ../../../../../.sdkman/candidates/java/21.2.0.r11-grl/lib/static/darwin-amd64/libnio.a
#   ../../../../../.sdkman/candidates/java/21.2.0.r11-grl/lib/static/darwin-amd64/libjava.a
#   ../../../../../.sdkman/candidates/java/21.2.0.r11-grl/lib/static/darwin-amd64/libfdlibm.a
#   ../../../../../.sdkman/candidates/java/21.2.0.r11-grl/lib/static/darwin-amd64/libzip.a
#   ../../../../../.sdkman/candidates/java/21.2.0.r11-grl/lib/svm/clibraries/darwin-amd64/libjvm.a
# Other libraries: z,-framework Foundation,pthread,dl
[hello-workshop-0.0.1-jar-with-dependencies:60432]   (typeflow):   2,221.91 ms,  1.74 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]    (objects):   1,970.68 ms,  1.74 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]   (features):     287.78 ms,  1.74 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]     analysis:   4,822.90 ms,  1.74 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]     universe:     586.89 ms,  1.74 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]      (parse):     531.13 ms,  1.76 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]     (inline):     714.53 ms,  2.33 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]    (compile):   5,101.78 ms,  2.40 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]      compile:   6,859.52 ms,  2.40 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]        image:   1,321.93 ms,  2.40 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]        write:     320.48 ms,  2.40 GB
[hello-workshop-0.0.1-jar-with-dependencies:60432]      [total]:  17,978.14 ms,  2.40 GB
```

Building a native image with the GraalVM Native Image plugin:
```shell
# build a native image, uses the <native> profile in the pom.xml file
>  ./mvnw clean package -Pnative

> ls -lart target
...
-rw-r--r--   1 dandobrin  staff      2820 Aug  3 15:51 hello-workshop-0.0.1-jar-with-dependencies.jar
-rwxr-xr-x   1 dandobrin  staff  11424108 Aug  3 15:51 helloworkshop
...

# execute the native image, observe that it indicates the GraalVM as the JVM version
> ./target/helloworkshop 
***** Hello, Workshop Participants! *****
Today's date: 2021/08/03 15:55:39

***** Java Vendor version *****
java.vendor.url: https://www.graalvm.org/
java.vendor: Oracle Corporation

***** Java VM version *****
java.vm.vendor: Oracle Corporation
java.vm.name: Substrate VM
java.vm.specification.version: 11
java.vm.specification.name: Java Virtual Machine Specification
java.vm.specification.vendor: Oracle Corporation
java.vm.version: GraalVM 21.2.0 Java 11 CE

***** Java Runtime Version *****
Java Version = 11.0.12+6-jvmci-21.2-b08
Java Version Feature Element = 11
Java Version Interim Element = 0
Java Patch Element Version = 0
Java Update Element Version = 12
Java Version Build = 6
Java Pre-Release Info = NA

***** done *****
```

## Containerizing a Native Image

Let's build a Dockerfile for building the native image file. Building Dockerfiles is complicated, and we will explore in 
the Spring Native chapter of the workshop how to leverage Cloud Native Buildpacks for building lightweight Docker images:
```shell
FROM ghcr.io/graalvm/graalvm-ce:java11-21.2.0 as builder

WORKDIR /app
COPY . /app

# install the native image builder
RUN gu install native-image

# BEGIN PRE-REQUISITES FOR STATIC NATIVE IMAGES FOR GRAAL
# SEE: https://github.com/oracle/graal/blob/master/substratevm/StaticImages.md
ARG RESULT_LIB="/staticlibs"

RUN mkdir ${RESULT_LIB} && \
    curl -L -o musl.tar.gz https://musl.libc.org/releases/musl-1.2.1.tar.gz && \
    mkdir musl && tar -xvzf musl.tar.gz -C musl --strip-components 1 && cd musl && \
    ./configure --disable-shared --prefix=${RESULT_LIB} && \
    make && make install && \
    cp /usr/lib/gcc/x86_64-redhat-linux/8/libstdc++.a ${RESULT_LIB}/lib/

ENV PATH="$PATH:${RESULT_LIB}/bin"
ENV CC="musl-gcc"

RUN curl -L -o zlib.tar.gz https://zlib.net/zlib-1.2.11.tar.gz && \
   mkdir zlib && tar -xvzf zlib.tar.gz -C zlib --strip-components 1 && cd zlib && \
   ./configure --static --prefix=${RESULT_LIB} && \
    make && make install
#END PRE-REQUISITES FOR STATIC NATIVE IMAGES FOR GRAAL

RUN curl -L -o xz.rpm https://www.rpmfind.net/linux/centos/8-stream/BaseOS/x86_64/os/Packages/xz-5.2.4-3.el8.x86_64.rpm
RUN rpm -iv xz.rpm

RUN curl -L -o upx-3.96-amd64_linux.tar.xz https://github.com/upx/upx/releases/download/v3.96/upx-3.96-amd64_linux.tar.xz
RUN tar -xvf upx-3.96-amd64_linux.tar.xz

# build the archive
RUN ./mvnw compile jar:jar

# build the native image, with the Helloworkshop as the main entrypoint
RUN native-image \
  --static \
  --libc=musl \
  --no-fallback \
  --no-server \
  --install-exit-handlers \
  -H:Name=webapp \
  -cp /app/target/*jar-with-dependencies.jar \
  com.example.Helloworkshop

# try to compress the image more with using the UPX compression tool
RUN upx-3.96-amd64_linux/upx -7 /app/webapp

# build from a lightweight Docker image
FROM scratch

COPY --from=builder /app/webapp /webapp

ENTRYPOINT ["/webapp"]
```

Let's build the Docker image with the native helloworkshop app packaged inside
```shell
> docker build . -t hello-workshop:graalvm-with-upx
```

The native image can be compressed inside the Docker image using the UP utility. You can enable/disable this feature in the Dockerfile:
```shell
# try to compress the image more with using the UPX compression tool
RUN upx-3.96-amd64_linux/upx -7 /app/webapp
```

We can observe the small size of the image, with or without compressing the image with UPX:
```shell
> docker images | grep graalvm
hello-workshop                                   graalvm-with-upx                                        1aa439dfbf0c   About a minute ago   2.8MB
hello-workshop                                   graalvm-no-upx                                          65cfe1d80270   8 days ago           10.6MB
```

## Sources
* GraalVM documentation - [link](https://www.graalvm.org/docs/getting-started/container-images/)


