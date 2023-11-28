package io.army.spring;

import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import org.springframework.core.env.Environment;

public final class SpringArmyEnvironment implements ArmyEnvironment {

    public static SpringArmyEnvironment create(String factoryName, Environment env) {
        return new SpringArmyEnvironment(factoryName, env);
    }

    private static final String ARMY_PERIOD = "army.";

    private final String prefix;

    private final Environment env;

    /**
     * private constructor
     */
    private SpringArmyEnvironment(String factoryName, Environment env) {
        this.prefix = ARMY_PERIOD + factoryName + '.';
        this.env = env;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final ArmyKey<T> key) {
        final Environment env = this.env;

        T value;
        if (key == ArmyKey.DIALECT) {
            final Database database;
            database = getRequired(ArmyKey.DATABASE);

            String dialectName;
            dialectName = env.getProperty(this.prefix + key.name, String.class);
            if (dialectName == null) {
                dialectName = env.getRequiredProperty(ARMY_PERIOD + key.name, String.class);
            }
            try {
                value = (T) database.dialectOf(dialectName);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(e);
            }
        } else if ((value = env.getProperty(this.prefix + key.name, key.javaType)) == null) {
            value = env.getProperty(ARMY_PERIOD + key.name, key.javaType);
        }
        return value;
    }

    @Override
    public <T> T getRequired(ArmyKey<T> key) throws IllegalStateException {
        final T value;
        value = get(key);
        if (value == null) {
            String m = String.format("value of %s is null", key.name);
            throw new IllegalStateException(m);
        }
        return value;
    }

    @Override
    public <T> T getOrDefault(final ArmyKey<T> key) {
        final T value;
        value = get(key);
        if (value != null) {
            return value;
        }
        final T defaultValue;
        defaultValue = key.defaultValue;
        if (defaultValue == null) {
            throw new IllegalArgumentException(String.format("%s no default value.", key.name));
        }
        return defaultValue;
    }

}
