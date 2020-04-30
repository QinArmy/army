package io.army.boot;

public interface SmartSessionFactoryInitializer {

    void onStartup(SessionFactoryInitializer initializer);

}
