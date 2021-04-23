# native-spring-on-k8s-with-graalvm-workshop

This repo contains all the materials required for the `Native Spring on K8s with GraalVM` workshop.

The workshop is designed to help Spring Boot developers build new / modernize existing applications using Spring Native and deploying them in Kubernetes. Basic knowledge of Spring Boot and Kubernetes is assumed for succcesful completion.

---------
## Repository layout

This repo contains sample code supporting the workshop agenda, with a focus on building Native Applications using Spring Native and GraalVM.

The content is organized around a number of independent sections, each with a `README.md` file explaining how to build and run the samples or pointing out interesting areas for further exploration.
* Environment - [Prerequisite Software Setup and Validation](setup/README.md) 
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
    * **[Hands-on Lab #1]** [x]
3. Demystifying Native Images
    * Let's talk about JVM vs Native
    * Introducing ahead-of-time compilation (AOT)
    * Making close-world assumptions
4. [Understanding GraalVM](graalvm/README.md)
    * GraalVM Architecture
    * Native Image technology for ahead-of-time compilation
    * Build configuration for a native image build process
        * How to configure Native Image Builds using the Java Agent -- **[Demo](graalvm/README.md#Demo)** [x]
        * Runtime vs Build-Time Initialization
        * Understanding the Class initialization strategy for Native Images 
            * **[Hands-on Lab #2](graalvm/README.md#Lab)** [x]
    * AOT compilation limitations - what do I need to know ?
        * Dynamic Class Loading, Reflection, [x] Dynamic Proxies, Accessing Resources, Serialization
        * Mitigating GraalVM AOT limitations -- **[Demo]** 
    * Debugging and Monitoring Tools
        * VisualVM and the GraalVM Dashboard -- **[Demo]** [x]
    * Building with the GraalVM Maven plugin
5. [Building Spring Native Applications](spring-native/README.md)
    * What is Spring Native and why use it ?
    * AOT compilation limitations - how do Native Hints in Spring bypass them ?
    * Spring Native's limitations - what do I need to know ?
    * Building with the Spring AOT Maven plugin 
    * Cloud Native Buildpacks -support source-to-image for native images 
        * Building and running a Spring Native app and **_diving_** into the built image - **[Hands-on #3]**
    * Troubleshooting tips
6. [Modernizing](modernize/README.md) an app to leverage Spring Native
    * Identifying AOT limitations in the app
    * Addressing them step-by-step - **[Demo]**
7. Best practices for writing code for native images   
   * for building Spring Native apps - **[Demo]**
8. Spring Native Roadmap

#### Appendix:
   * [Complete Spring Native examples](complete/README.md)

