package io.army.env;


import javax.annotation.Nullable;

import io.army.session.SessionFactory;

/**
 * Interface representing the environment in which Army is running.
 *
 * @see SessionFactory
 * @since 1.0
 */
public interface ArmyEnvironment {

    @Nullable
    <T> T get(ArmyKey<T> key) throws IllegalStateException;

    <T> T getRequired(ArmyKey<T> key) throws IllegalStateException;

    <T> T getOrDefault(ArmyKey<T> key) throws IllegalStateException;

}
