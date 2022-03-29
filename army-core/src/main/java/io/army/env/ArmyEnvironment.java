package io.army.env;


import io.army.lang.Nullable;
import io.army.session.GenericSessionFactory;

/**
 * Interface representing the environment in which Army is running.
 *
 * @see GenericSessionFactory
 * @since 1.0
 */
public interface ArmyEnvironment {

    @Nullable
    <T> T get(ArmyKey<T> key);

    <T> T getOrDefault(ArmyKey<T> key);

}
