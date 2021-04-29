package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.InitializationHint;
import org.springframework.nativex.hint.InitializationTime;
import org.springframework.nativex.hint.NativeHint;

//Part 1
//@NativeHint(
//		initialization = @InitializationHint(types = {
//				com.example.First.class,
//				com.example.Second.class
//		}, initTime = InitializationTime.BUILD))

//Part 2
//@NativeHint(
//		initialization = @InitializationHint(types = {
//				com.example.Second.class
//		}, initTime = InitializationTime.BUILD))
@SpringBootApplication
public class ClassInitApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClassInit.class, args);
	}
}
