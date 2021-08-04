#!/bin/bash -e

export PATH=/home/ubuntu/graalvm-ce-java11-21.2.0/bin:$PATH
export JAVA_HOME=/home/ubuntu/graalvm-ce-java11-21.2.0

time imgpkg pull -i ghcr.io/ddobrin/lab-sp-one-intro-m2:latest -o $HOME/.m2

git clone https://github.com/ddobrin/native-spring-on-k8s-with-graalvm-workshop.git
cd native-spring-on-k8s-with-graalvm-workshop