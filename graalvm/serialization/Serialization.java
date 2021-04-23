import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Serialization {
    private static final String filename = "serialized_objects_in_stream";

    static Stream<Long> fibonacciStream() {
        return Stream.iterate(new long[]{0, 1}, (f) -> new long[]{f[0] + f[1], f[0]}).map(f -> f[0]);
    }

    public static void main(String[] args) throws Exception {
        List<Long> fib10 = fibonacciStream().limit(10).collect(Collectors.toList());
        try (ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream(filename))) {
            oss.writeObject(fib10);
        }
        Object deserializedFib1000;
        try (ObjectInputStream oss = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFib1000 = oss.readObject();
        }
        System.out.println("Serialized list matches Deserialized list: " + fib10.equals(deserializedFib1000));

        System.out.println("Print the first 10 Fibonacci numbers in the sequence");
        fib10.forEach(System.out::println);
    }
}
