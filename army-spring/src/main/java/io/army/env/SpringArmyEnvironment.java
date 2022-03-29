package io.army.env;

import org.springframework.core.env.Environment;

public final class SpringArmyEnvironment implements ArmyEnvironment {

    private final String prefix;

    private final org.springframework.core.env.Environment env;

    public SpringArmyEnvironment(String factoryName, Environment env) {
        this.prefix = "army." + factoryName + ".";
        this.env = env;
    }


    @Override
    public <T> T get(ArmyKey<T> key) {
        return this.env.getProperty(this.prefix + key.name, key.javaType);
    }

    @Override
    public <T> T getOrDefault(final ArmyKey<T> key) {
        final T defaultValue;
        defaultValue = key.defaultValue;
        if (defaultValue == null) {
            throw new IllegalArgumentException(String.format("%s no default value.", key.name));
        }
        T value;
        value = this.env.getProperty(this.prefix + key.name, key.javaType);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

}
