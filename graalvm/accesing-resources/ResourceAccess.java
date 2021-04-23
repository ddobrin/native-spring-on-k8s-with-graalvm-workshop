import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceAccess {
    public static void main(String[] args) {
        Properties prop = new Properties();

        // read properties
        try (InputStream inputStream = ResourceAccess.class
                                        .getClassLoader()
                                        .getResourceAsStream("config.properties")) {
            prop.load(inputStream);
            System.out.println("Reading config.threads property: " + prop.getProperty("config.threads"));
            System.out.println("Reading config.load property: " + prop.getProperty("config.load"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
