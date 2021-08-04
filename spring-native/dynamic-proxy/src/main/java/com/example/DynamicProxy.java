package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.Map;

//@Profile("!test")
@Component
public class DynamicProxy implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        String className = args[0];

        // A proxy instance serviced by an invocation handler is created via a factory method call on the java.lang.reflect.Proxy class
        // Since InvocationHandler is a functional interface, let's the handler inline using lambda expressions
        Map proxyInstance = (Map) Proxy.newProxyInstance(
                DynamicProxy.class.getClassLoader(),
                new Class[] {
                        Class.forName(className)
                },
                (proxy, method, methodArgs) -> {
                    if (method.getName().equals("get")) {
                        return 42;
                    }  else {
                        throw new UnsupportedOperationException(
                                "Unsupported method: " + method.getName());
                    }
                });

        // successful invocation of a get() operation
        int fortytwo = (int) proxyInstance.get("hello"); // 42
        System.out.println("Invoking method: <get> in a <java.util.Map>. Output: " + fortytwo);

        // failed proxy invocation, as put() is not a supported operation in the proxy
        try {
            System.out.println("Invoking method: <put> in a <java.util.Map>");
            proxyInstance.put("hello", "world"); // exception
        } catch (Exception e) {
            System.out.println("Expected an exception, got an exception");
            return;
        }

        // some unexpected exception
        throw new RuntimeException("Expected an exception, didn't get one");
    }
}