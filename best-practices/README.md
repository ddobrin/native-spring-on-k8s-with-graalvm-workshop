# Best practices for Troubleshooting and Designing Native-friendly Spring apps / libraries

While Spring Native strives to provide an `unchanged` experience when writing Spring Boot applications, regardless of 
whether they are building a JVM or Native Image, 
there are several AOT limitations which developers might encounter and need to be aware on how to resolve them.

On the other hand, best practices on code reuse apply to the Native apps ecosystem and can significantly 
improve consistency, reduce development time and troubleshooting efforts.

This section provides a number of samples which should help you with day-to-day Spring Native development in large(r) scale projects.
* `troubleshooting`:
    * step-by-step example of identifying and fixing AOT limitations which the Spring AOT plugin can't automatically resolve -- **[Demo/Homework](troubleshooting/README.md)**
* `shared-hints`
    * building a library of hints, shareable in large(r) scale projects -- **[Demo/Homework](shared-hints/README.md)**
* `demo-share-hints`:
    * a complete application, reusing native hints as `shared hints`, to be used as a guideline for enterprise developments -- **[Demo/Homework](demo-shared-hints/README.md)** 