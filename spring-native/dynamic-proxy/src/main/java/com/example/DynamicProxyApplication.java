package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.TypeHint;

@JdkProxyHint(typeNames = {
		"java.util.Map"
})
@TypeHint(typeNames = {
		"java.util.Map"
})
@SpringBootApplication
public class DynamicProxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(DynamicProxy.class, args);
	}
}
