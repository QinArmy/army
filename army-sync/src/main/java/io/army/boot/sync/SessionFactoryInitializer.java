package io.army.boot.sync;

public interface SessionFactoryInitializer {

    /**
     * don't close {@link java.sql.Connection}
     */
    void onStartup();


}
