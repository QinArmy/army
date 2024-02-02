package io.army.session;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class MyPaths {

    private MyPaths() {
        throw new UnsupportedOperationException();
    }


    public static Path testResourcesPath() {
        final Path path;
        path = Paths.get(System.getProperty("user.dir"), "src/test/resources");
        return path;
    }

    public static Path myLocal(String path) {
        return Paths.get(testResourcesPath().toString(), "my-local", path);
    }

    public static boolean isMyLocal() {
        final Path path;
        path = Paths.get(testResourcesPath().toString(), "my-local");
        return Files.exists(path);
    }


}
