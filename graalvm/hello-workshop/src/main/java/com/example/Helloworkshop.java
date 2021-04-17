package com.example;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class Helloworkshop {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        System.out.println("\n***** Hello, Workshop Participants! *****");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Today's date: " + dtf.format(now));

        System.out.println("\n***** Java Vendor version *****");
        Properties props = System.getProperties();
        props.keySet().stream()
                .filter(key -> key.toString().startsWith("java.vendor"))
                .map(key -> key + ": " + props.getProperty(key.toString()))
                .forEach(System.out::println);

        System.out.println("\n***** Java VM version *****");
        props.keySet().stream()
                .filter(key -> key.toString().startsWith("java.vm"))
                .map(key -> key + ": " + props.getProperty(key.toString()))
                .forEach(System.out::println);

        System.out.println("\n***** Java Runtime Version *****");
        Runtime.Version version = java.lang.Runtime.version();
        System.out.println("Java Version = "+version);
        System.out.println("Java Version Feature Element = "+version.feature());
        System.out.println("Java Version Interim Element = "+version.interim());
        System.out.println("Java Patch Element Version = "+version.patch());
        System.out.println("Java Update Element Version = "+version.update());
        System.out.println("Java Version Build = "+version.build().get());
        System.out.println("Java Pre-Release Info = "+version.pre().orElse("NA"));

        System.out.println("\n***** done *****");
    }
}
