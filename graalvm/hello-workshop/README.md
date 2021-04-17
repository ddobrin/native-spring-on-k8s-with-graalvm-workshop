


To check whether the native toolchiaan is accessible:
```shell
> native-image --native-image-info -jar hello-workshop-0.0.1-jar-with-dependencies.jar 

[hello-workshop-0.0.1-jar-with-dependencies:94652]    classlist:   1,261.95 ms,  0.96 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]        (cap):   3,853.83 ms,  0.96 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]        setup:   5,158.90 ms,  0.96 GB
# Building image for target platform: org.graalvm.nativeimage.Platform$DARWIN_AMD64
# Using native toolchain:
#   Name: LLVM (clang)
#   Vendor: apple
#   Version: 11.0.0
#   Target architecture: x86_64
#   Path: /usr/bin/cc
# Using CLibrary: com.oracle.svm.core.c.libc.NoLibC
[hello-workshop-0.0.1-jar-with-dependencies:94652]     (clinit):     178.49 ms,  1.20 GB
# Static libraries:
#   ../../../../../../.sdkman/candidates/java/21.0.0.2.r11-grl/lib/svm/clibraries/darwin-amd64/liblibchelper.a
#   ../../../../../../.sdkman/candidates/java/21.0.0.2.r11-grl/lib/static/darwin-amd64/libnet.a
#   ../../../../../../.sdkman/candidates/java/21.0.0.2.r11-grl/lib/svm/clibraries/darwin-amd64/libdarwin.a
#   ../../../../../../.sdkman/candidates/java/21.0.0.2.r11-grl/lib/static/darwin-amd64/libnio.a
#   ../../../../../../.sdkman/candidates/java/21.0.0.2.r11-grl/lib/static/darwin-amd64/libjava.a
#   ../../../../../../.sdkman/candidates/java/21.0.0.2.r11-grl/lib/static/darwin-amd64/libfdlibm.a
#   ../../../../../../.sdkman/candidates/java/21.0.0.2.r11-grl/lib/static/darwin-amd64/libzip.a
#   ../../../../../../.sdkman/candidates/java/21.0.0.2.r11-grl/lib/svm/clibraries/darwin-amd64/libjvm.a
# Other libraries: -framework Foundation,pthread,dl,z
[hello-workshop-0.0.1-jar-with-dependencies:94652]   (typeflow):   3,320.67 ms,  1.20 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]    (objects):   4,068.33 ms,  1.20 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]   (features):     160.52 ms,  1.20 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]     analysis:   7,892.99 ms,  1.20 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]     universe:     385.14 ms,  1.22 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]      (parse):     724.70 ms,  1.22 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]     (inline):   1,679.04 ms,  1.67 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]    (compile):   7,052.56 ms,  2.26 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]      compile:   9,946.68 ms,  2.26 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]        image:   1,269.78 ms,  2.26 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]        write:     350.06 ms,  2.26 GB
[hello-workshop-0.0.1-jar-with-dependencies:94652]      [total]:  26,402.78 ms,  2.26 GB
```