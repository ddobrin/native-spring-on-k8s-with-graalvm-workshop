# Software Prerequisites Setup and Environment Validation

This setup exercise requires the completion of 3 steps:
1. Install Prequisites
2. Environment Validation 
3. Build and run a validation app

## Context
The workshop intends to illustrate how to build Spring Native applications, using directly the GraalVM, or while leveraging AdoptOpenJDK and Cloud-Native Buildpacks. While GraalVM offers both JIT as well as AOT builds, and we'll use it to teach concepts, we wish to illustrate cloud-native buildpack concepts while setting up a second JVM, independent of GraalVM: an OpenJDK version.

It is recommended to leverage  SDKMan for managing parallel versions of a Software Development Kit, or alternatively jEnv.
If you prefer to setting up the path to the executable yourself in the IDE or Terminal window, please do so.
Use whichever approach you're most comfortable with.

## 1. Prequisites 

* SDK Managers
    * SDKMan - managing parallel versions of multiple Software Development Kits[[Install]](https://sdkman.io/) - `recommended`
    * jEnv - Java SDK Manager [[Install]](https://www.jenv.be/)
* Java and Maven
    * Java 11 OpenJDK - install with [AdoptOpenJDK downloads](https://adoptopenjdk.net/installation.html) or [SDK Manager - recommended](https://sdkman.io/jdks#AdoptOpenJDK)
    * GraalVM 21.0.0.r11 - install with [GraalVM download](https://www.graalvm.org/) or [SDK Manager - recommended](https://sdkman.io/jdks#Oracle)
    * Maven [[Install]](https://maven.apache.org/install.html)
* Docker Utilities
    * Docker CLI and docker-compose [[Install]](https://www.docker.com/products/docker-desktop)
    * Dive – utility to explore Docker images and the layer contents of your Docker/OCI images [[Install]](https://github.com/wagoodman/dive)
* Kubernetes
    * Local K8s cluster in Docker Desktop [[Install]]((https://www.docker.com/products/docker-desktop)) or Minikube [[Install]](https://minikube.sigs.k8s.io/docs/start/). Alternatively, use a K8s cluster in a Public Cloud environment
    * Kubectl [[Install]](https://kubernetes.io/docs/tasks/tools/)
    * k9s - terminal UI to interact with your Kubernetes clusters [[Install]](https://github.com/derailed/k9s)
* Favourite Java IDE

## 2. Environment Validation

Clone the repo and navigate to the setup folder:
```shell
> git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm

> cd setup
> ls -l

-rw-r--r--  1 dandobrin  staff  1977  9 Apr 10:56 Helloworkshop.java
-rw-r--r--  1 dandobrin  staff  1148  9 Apr 12:12 README.md
```

* SDKMan

Let's validate that the SDK Manager is properly installed. This README provides instructions for using SDKMan, however you can use other tools or manual operations of your choice.

```shell
> sdk list java
```

* Install AdoptOpenJDK and GraalVM
```shell
> sdk install java 11.0.10.hs-adpt 
> sdk install java  21.0.0.2.r11-grl 

# Validate with:
> sdk list java | grep 11.0.10.hs-adp
Ex.: | >>> | 11.0.10.hs   | adpt    | installed  | 11.0.10.hs-adpt     

> sdk list java | grep  21.0.0.2.r11-grl
Ex.:  |     | 21.0.0.2.r11 | grl     | installed  | 21.0.0.2.r11-grl    

# Switch between Java versions

# Use AdoptOpenJDK
> sdk use java 11.0.10.hs-adpt

# Use GraalVM
> sdk use java  21.0.0.2.r11-grl

# please validate that you have installed the `native-image` executable
> gu install native-image
```

With Java installed, let's move to validating that utilities are properly installed.

* Maven 

```shell
> mvn -version

Ex.: Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /opt/apache-maven
Java version: 11.0.10, vendor: AdoptOpenJDK, runtime: /Users/dandobrin/.sdkman/candidates/java/11.0.10.hs-adpt
Default locale: en_CA, platform encoding: UTF-8
OS name: "mac os x", version: "10.15.7", arch: "x86_64", family: "mac"
```
* Docker Desktop

```shell
> docker version

Ex.: 
Client: Docker Engine - Community
 Cloud integration: 1.0.9
 Version:           20.10.5
 API version:       1.41
...
Server: Docker Engine - Community
 Engine:
  Version:          20.10.5
  API version:      1.41 (minimum version 1.12)
...

# List installed Docker images
> docker images
```

* Dive utility

```shell
# pull some sample images and open them with `dive`
> docker pull triathlonguy/hello-function-jvm:0.0.1
> docker pull triathlonguy/hello-function-native:0.0.1

> docker images | grep hello
Ex.:
triathlonguy/hello-function-native   0.0.1                                                   015e5e171e9c   41 years ago    103MB
triathlonguy/hello-function-jvm      0.0.1                                                   b18aa598b337   41 years ago    262MB

# open an image with dive using the container id
Ex.:
> dive 015e5e171e9c
```

* Local Kubernetes

Enable Kubernetes in the Docker-desktop preferences then set up the kubectl CLI and test the node
```shell
> kubectl get nodes 

Observe 1 node in docker-dekstop or minikube or the number of nodes for your cluster when running in the public cloud
```

* Kubectl CLI

Install the `kubectl` CLI and validate it against the cluster
```shell
> kubectl version
Ex.: 
Client Version: version.Info{Major:"1", Minor:"21", GitVersion:"v1.21.0", GitCommit:"cb303e613a121a29364f75cc67d3d580833a7479", GitTreeState:"clean", BuildDate:"2021-04-08T21:15:16Z", GoVersion:"go1.16.3", Compiler:"gc", Platform:"darwin/amd64"}
Server Version: version.Info{Major:"1", Minor:"18+", GitVersion:"v1.18.16-gke.302", GitCommit:"2e4de00ee51e92a708578409838fd37044b00902", GitTreeState:"clean", BuildDate:"2021-03-08T22:06:14Z", GoVersion:"go1.13.15b4", Compiler:"gc", Platform:"linux/amd64"}
```

* K9s
```shell
# run kubectl against the pods in the cluster against the default namespace
> kubectl get pod

# observe the same pods in k9s when opening the app
> k9s
```

## 3. Build and run a validation app

* Build and run the app with AdoptOpenJDK

```shell
# set AdoptOpenJDK
> sdk use java 11.0.10.hs-adpt

# compile and run the app
> javac Helloworkshop.java
> java Helloworkshop

Observe the Java version and vendor information:
java.vendor.version: AdoptOpenJDK
java.vm.version: 11.0.10+9
```

* Build and run the app with GraalVM

```shell
# Use GraalVM
> sdk use java  21.0.0.2.r11-grl

# compile and run the app
> javac Helloworkshop.java
> native-image Helloworkshop
> ./helloworkshop

Observe the Java version and vendor information:
java.vm.vendor: Oracle Corporation
java.vm.name: Substrate VM
java.vm.specification.version: 11
java.vm.specification.name: Java Virtual Machine Specification
java.vm.specification.vendor: Oracle Corporation
java.vm.version: GraalVM 21.0.0.2 Java 11
```

# You are now ready to go !