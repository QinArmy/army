package io.army.env;


import io.army.GenericSessionFactory;
import io.army.beans.ArmyBean;
import io.army.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface representing the environment in which Army is running.
 *
 * @see GenericSessionFactory
 * @since 1.0
 */
public interface ArmyEnvironment {
    /**
     * Return whether the given property key is available for resolution,
     * i.e. if the value for the given key is not {@code null}.
     */
    boolean containsProperty(String key);

    /**
     * the the property value showSQL:{@code value1,value2,...,valuen}
     */
    boolean containsValue(String key, String targetValue);

    boolean isOn(String key);

    boolean isOff(String key);

    @Nullable
    <T extends ArmyBean> T getBean(String name, Class<T> beanClass);

    <T extends ArmyBean> T getRequiredBean(String name, Class<T> beanClass) throws BeansException;

    /**
     * @return a unmodifiable map
     */
    Map<String, ArmyBean> getAllBean();

    /**
     *
     */
    boolean isOffDuration(String key);

    boolean isOnDuration(String key);

    /**
     * Return the property value associated with the given key,
     * or {@code null} if the key cannot be resolved.
     *
     * @param key the property name to resolve
     * @see #getProperty(String, String)
     * @see #getProperty(String, Class)
     * @see #getRequiredProperty(String)
     */
    @Nullable
    String getProperty(String key);

    /**
     * Return the property value associated with the given key, or
     * {@code defaultValue} if the key cannot be resolved.
     *
     * @param key          the property name to resolve
     * @param defaultValue the default value to return if no value is found
     * @see #getRequiredProperty(String)
     * @see #getProperty(String, Class)
     */
    String getProperty(String key, String defaultValue);

    /**
     * Return the property value associated with the given key,
     * or {@code null} if the key cannot be resolved.
     *
     * @param key        the property name to resolve
     * @param targetType the expected type of the property value
     * @see #getRequiredProperty(String, Class)
     */
    @Nullable
    <T> T getProperty(String key, Class<T> targetType);

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty list the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param targetArrayType the expected type of the property value
     * @return a  list
     * @see #getRequiredProperty(String, Class)
     */
    <T> List<T> getPropertyList(String key, Class<T[]> targetArrayType);

    /**
     * Return the property value associated with the given key,
     * or {@link Collections#emptyList()} if the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param targetArrayType the expected type of the property value
     * @param defaultList     the default list to return if no value is found
     * @return a  list
     * @see #getRequiredProperty(String, Class)
     */
    <T> List<T> getPropertyList(String key, Class<T[]> targetArrayType, List<T> defaultList);

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty set the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param targetArrayType the expected type of the property value
     * @return a  list
     * @see #getRequiredProperty(String, Class)
     */
    <T> Set<T> getPropertySet(String key, Class<T[]> targetArrayType);

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty set the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param targetArrayType the expected type of the property value
     * @param defaultSet      the default set to return if no value is found
     * @return a  list
     * @see #getRequiredProperty(String, Class)
     */
    <T> Set<T> getPropertySet(String key, Class<T[]> targetArrayType, Set<T> defaultSet);

    /**
     * Return the property value associated with the given key,
     * or {@code defaultValue} if the key cannot be resolved.
     *
     * @param key          the property name to resolve
     * @param targetType   the expected type of the property value
     * @param defaultValue the default value to return if no value is found
     * @see #getRequiredProperty(String, Class)
     */
    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    /**
     * Return the property value associated with the given key (never {@code null}).
     *
     * @throws IllegalStateException if the key cannot be resolved
     * @see #getRequiredProperty(String, Class)
     */
    String getRequiredProperty(String key) throws IllegalStateException;

    /**
     * Return the property value associated with the given key, converted to the given
     * targetType (never {@code null}).
     *
     * @throws IllegalStateException if the given key cannot be resolved
     */
    <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty list the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param targetArrayType the expected type of the property value
     * @return a  list
     * @throws IllegalStateException if the given key cannot be resolved
     * @see #getRequiredProperty(String, Class)
     */
    <T> List<T> getRequiredPropertyList(String key, Class<T[]> targetArrayType) throws IllegalStateException;

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty set the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param targetArrayType the expected type of the property value
     * @return a  set
     * @throws IllegalStateException if the given key cannot be resolved
     * @see #getRequiredProperty(String, Class)
     */
    <T> Set<T> getRequiredPropertySet(String key, Class<T[]> targetArrayType) throws IllegalStateException;

}
