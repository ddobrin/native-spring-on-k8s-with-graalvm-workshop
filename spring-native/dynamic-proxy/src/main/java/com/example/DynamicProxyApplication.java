package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.ProxyHint;
import org.springframework.nativex.hint.TypeHint;

@ProxyHint(typeNames = {
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
