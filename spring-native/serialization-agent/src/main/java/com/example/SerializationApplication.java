package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.SerializationHint;

// not required when the Java Agent is configured and generates the configs
//@SerializationHint(
//		types = {
//				java.util.ArrayList.class,
//				java.lang.Long.class,
//				java.lang.Number.class
//		}
//)
@SpringBootApplication
public class SerializationApplication {
	public static void main(String[] args) {
		SpringApplication.run(Serialization.class, args);
	}
}
