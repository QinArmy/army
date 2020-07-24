package io.army.boot.sync;

public interface SmartSessionFactoryInitializer {

    void onStartup(SessionFactoryInitializer initializer);

}
