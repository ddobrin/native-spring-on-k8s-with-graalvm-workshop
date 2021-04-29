# native-spring-on-k8s-with-graalvm-workshop

This repo contains all the materials required for the `Native Spring on K8s with GraalVM` workshop.

The workshop is designed to help Spring Boot developers build new / modernize existing applications using Spring Native and deploying them in Kubernetes. 
Basic knowledge of Spring Boot and Kubernetes is assumed for succesful completion.

---------
## Context

This content contains sample code supporting the workshop agenda, with a focus on building Native Applications using Spring Native and GraalVM.

The content is organized around a number of independent sections, each with a `README.md` file explaining how to build and run the samples, 
or pointing out areas of interest, for further exploration. 

The main focus areas are:
* [Prerequisite Software Setup and Validation](setup/README.md) 
* Understanding [GraalVM](graalvm/README.md)
* Building [Spring Native](spring-native/README.md) Applications
* [Modernizing](modernize/README.md) an app to use Spring Native
* [Complete](complete/README.md) Spring Native examples

### `Highly recommended:`
Please follow the [environment setup and validation section](setup/README.md) `prior` to the start of the workshop.

---------
## Workshop - Detailed Agenda

1. Introductions
2. [Setup Validation](setup/README.md) - say "Hello Workshop" 
    * **[Hands-on Lab #1]**
3. Demystifying Native Images
    * Let's talk about JVM vs Native
    * Introducing ahead-of-time compilation (AOT)
    * Making close-world assumptions
4. [Understanding GraalVM](graalvm/README.md)
    * GraalVM Architecture
    * Native Image technology for ahead-of-time compilation
    * Build configuration for a native image build process
        * How to configure Native Image Builds using the Java Agent -- **[Demo](graalvm/README.md#Demo)** 
    * Initialization
        * Runtime vs Build-Time Initialization
        * Understanding the Class initialization strategy for Native Images
            * **[Hands-on Lab #2](graalvm/README.md#Lab)** 
    * AOT compilation limitations - what do I need to know ?
        * Dynamic Class Loading, Reflection, Dynamic Proxies, Accessing Resources, Serialization
        * Mitigating AOT limitations -- **[Demo](graalvm/README.md#Demo)** 
    * Building and Containerizing native images with the GraalVM Maven plugin and Docker -- **[Demo](graalvm/README.md#Demo)**       
    * Visualization Tools
        * GraalVM Dashboard -- **[Demo](graalvm/README.md#Demo)** 
5. [Building Spring Native Applications](spring-native/README.md)
    * What is Spring Native and why use it ?
    * AOT compilation limitations - how do Native Hints in Spring help bypass them ? -- **[Demo](spring-native/README.md#Demo)**
    * Spring Native's limitations - what do I need to know ?
    * Building with the Spring AOT Maven plugin 
    * Cloud Native Buildpacks -support source-to-image for native images 
        * Building, containerizing and running a Spring Native app and **_diving_** into the built image
            * **[Hands-on #3](spring-native/README.md#Lab)**
    * Troubleshooting tips
6. [Modernizing](modernize/README.md) an app to leverage Spring Native
    * Identifying AOT limitations in the app
    * Addressing them step-by-step - **[Demo]**
7. Best practices for writing code for native images   
   * for building Spring Native apps - **[Demo]**
8. Spring Native Roadmap

#### Appendix:
   * [Complete Spring Native examples](complete/README.md)

