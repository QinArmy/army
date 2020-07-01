package io.army.boot;

public interface SessionFactoryInitializer {

    /**
     * don't close {@link java.sql.Connection}
     */
    void onStartup();


}
