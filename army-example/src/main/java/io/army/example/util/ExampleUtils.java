package io.army.example.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ExampleUtils {

    private ExampleUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isMyLocal() {
        final Path path;
        path = Paths.get(System.getProperty("user.dir"), "src/main/java/io/army/example/my");
        return Files.exists(path);
    }


}
