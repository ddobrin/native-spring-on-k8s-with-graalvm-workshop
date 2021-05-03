package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.ProxyHint;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

// Uncomment for fixing: Proxy hint
@ProxyHint(typeNames = {
		"com.example.demo.Bear",
		"org.springframework.aop.SpringProxy",
		"org.springframework.aop.framework.Advised",
		"org.springframework.core.DecoratingProxy"
})

// Uncomment for fixing Refelection hint
@TypeHint(typeNames = {"com.example.demo.UUID"})

// Uncomment for fixing Serialization hint
@SerializationHint(
		types = {
				java.util.ArrayList.class
		})
@Log4j2
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	Bear bear() {
		var pfb = new ProxyFactoryBean();
		pfb.addInterface(Bear.class);
		pfb.addAdvice((MethodInterceptor) method -> {
			log.info("Before interception...");

			// intercepts the talk() method
			if (method.getMethod().getName().equals("talk"))
				return "Proxied method: Bear talks() ... After interception...";

			// proceeds to the eat() method - for this sample
			return "No method intercepted. Bear must be eat()ing ... After interception...";
		});
		return (Bear) pfb.getObject();
	}
}

interface Bear {
	String talk();
	String eat();
}

class UUID {
	String uid = java.util.UUID.randomUUID().toString();
}

@Log4j2
@Component
@RequiredArgsConstructor
class Initializer implements ApplicationListener<ApplicationReadyEvent> {

	private final Bear bear;

	@SneakyThrows
	@SuppressWarnings("unchecked")
	<T> T load(String clazz) {
		Class<?> aClass = Class.forName(clazz);
		return (T) aClass.getDeclaredConstructor().newInstance();
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		UUID uuid = load("com.example.demo.UUID");
		log.info("Assign unique ID to a Bear: " + uuid.uid);
		log.info("Invoke talk(). Proxied message: " + bear.talk());
		log.info("Invoke eat(). Proxied message: " + bear.eat());

		log.info("Serialize the ID assigned to the bear to the file: bear-id");
		var ids = new ArrayList<String>();
		ids.add(uuid.toString());
		try (ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream("bear-id"))) {
			oss.writeObject(ids);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


