package com.example.graalvmdemo.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PersonController2 {

	@GetMapping(value="/without")
	String demo() {
		return "hello - without class proxy";
	}
	
	
}
