package com.example;
import java.nio.charset.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ClassInit implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
        First.second.printIt();
    }
}

class First {
    public static Second second = new Second();

    //Uncomment this section for part 2 of the exercise
    public static Thread t;

    // Uncomment for Part 2
//    static {
//        t = new Thread(()-> {
//            try {
//                System.out.println("Sleep for 10s...");
//                Thread.sleep(10_000);
//                System.out.println("Done...");
//            } catch (Exception e){}
//        });
//        t.start();
//    }
}

class Second {
    private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");

    public void printIt() {
        System.out.println("Unicode 32 bit CharSet: " + UTF_32_LE);
    }
}


