# native-spring-on-k8s-with-graalvm-workshop

# native-spring-on-k8s-with-graalvm
This repo contains all the materials required for the `Native Spring on K8s with GraalVM` workshop.

The workshop is designed to help Spring Boot developers build new / modernize existing applications using Spring Native and deploying them in Kubernetes. Basic knowledge of Spring Boot and Kubernetes is assumed for succcesful completion.

---------
## Repository layout

The repo contains sample code suporting the workshop agenda and illustrates how to build Native Applications using Spring Native Beta and GraalVM.

The content is organized around the following sections, each with a `README.md` file explaining how to build and run the samples or interesting areas to explore further.
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
2. Setup Validation - say "Hello Workshop"
    * **[Hands-on #1]** [x]
3. Demystifying Native Images
    * Let's talk about JVM vs Native
    * Introducing ahead-of-time compilation (AOT)
    * Making close-world assumptions
4. Understanding GraalVM
    * GraalVM Architecture
    * Native Image technology for ahead-of-time compilation
    * Build configuration for a native image build process
        * **[Demo]** - The simplest way to configure GraalVM Native Image [x]
        * **[Hands-on #2]** - Understanding Reflection and the Class initialization strategy using a simple Native app
    * AOT compilation limitations - what do I need to know 
        * Class Metadata, Reflection, Dynamic Class Loading, Dynamic Proxies, Accessing Resources, Serialization
        * **[Demo]** - Mitigate GraalVM AOT limitations
    * Debugging and Monitoring Tools
        * **[Demo]** - VisualVM and the GraalVM Dashboard
    * Building with the GraalVM Maven plugin
5. Building Spring Native Applications
    * What is Spring Native and why use it?
    * AOT compilation limitations - how do Native Hints in Spring bypass them?
    * Spring Native's limitations - what do I need to know?
    * Building with the Spring AOT Maven plugin 
    * Cloud Native Buildpacks -support source-to-image for native images 
        * **[Hands-on #3]** - Building and running a Spring Native app and **_diving_** into the built image
6. Modernize an app to use Spring Native
    * Identifying limitations 
    * Addressing them step-by-step
        * **[Demo]**
7. Troubleshooting tips 
8. Best practice - building native-friendly Spring libs
    * **[Demo]**
9. Spring Native Roadmap

