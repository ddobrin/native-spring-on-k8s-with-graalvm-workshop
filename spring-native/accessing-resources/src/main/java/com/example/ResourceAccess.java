package com.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.context.annotation.Profile;
import org.springframework.nativex.hint.ResourceHint;

@ResourceHint(
        patterns = {
                "src/data/app-resources.*"
        }
)
//@Profile("!test")
@SpringBootApplication
public class ResourceAccess {
    public static void main(String[] args) {
        Properties prop = new Properties();

        // read properties
        try (InputStream inputStream = ResourceAccess.class
                                        .getClassLoader()
                                        .getResourceAsStream("src/data/app-resources.properties")) {
            prop.load(inputStream);
            System.out.println("Reading config.threads property: " + prop.getProperty("config.threads"));
            System.out.println("Reading config.load property: " + prop.getProperty("config.load"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
