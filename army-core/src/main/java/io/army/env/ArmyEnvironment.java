package io.army.env;


import io.army.beans.ArmyBean;
import io.army.lang.Nullable;
import io.army.session.GenericSessionFactory;

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
    @Deprecated
    boolean containsProperty(String key);

    /**
     * the the property value showSQL:{@code value1,value2,...,valuen}
     */
    @Deprecated
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
     * @see #get(String, String)
     * @see #get(String, Class)
     * @see #getNonNull(String)
     */
    @Nullable
    String get(String key);

    /**
     * Return the property value associated with the given key, or
     * {@code defaultValue} if the key cannot be resolved.
     *
     * @param key          the property name to resolve
     * @param defaultValue the default value to return if no value is found
     * @see #getNonNull(String)
     * @see #get(String, Class)
     */
    String get(String key, String defaultValue);

    /**
     * Return the property value associated with the given key,
     * or {@code null} if the key cannot be resolved.
     *
     * @param key        the property name to resolve
     * @param targetType the expected type of the property value
     * @see #getNonNull(String, Class)
     */
    @Nullable
    <T> T get(String key, Class<T> targetType);

    <T> T getNonNull(String key, Class<T> resultClass);


    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty list the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param targetArrayType the expected type of the property value
     * @return a  list
     * @see #getNonNull(String, Class)
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
     * @see #getNonNull(String, Class)
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
     * @see #getNonNull(String, Class)
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
     * @see #getNonNull(String, Class)
     */
    <T> Set<T> getPropertySet(String key, Class<T[]> targetArrayType, Set<T> defaultSet);

    /**
     * Return the property value associated with the given key,
     * or {@code defaultValue} if the key cannot be resolved.
     *
     * @param key          the property name to resolve
     * @param targetType   the expected type of the property value
     * @param defaultValue the default value to return if no value is found
     * @see #getNonNull(String, Class)
     */
    <T> T get(String key, Class<T> targetType, T defaultValue);

    /**
     * Return the property value associated with the given key (never {@code null}).
     *
     * @throws IllegalStateException if the key cannot be resolved
     * @see #getNonNull(String, Class)
     */
    String getNonNull(String key) throws IllegalStateException;

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty list the key cannot be resolved.
     *
     * @param key          the property name to resolve
     * @param elementClass the expected type of the property value
     * @return a  list
     * @throws IllegalStateException if the given key cannot be resolved
     * @see #getNonNull(String, Class)
     */
    <T> List<T> getList(String key, Class<T> elementClass) throws IllegalStateException;

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty set the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param targetArrayType the expected type of the property value
     * @return a  set
     * @throws IllegalStateException if the given key cannot be resolved
     * @see #getNonNull(String, Class)
     */
    <T> Set<T> getRequiredPropertySet(String key, Class<T[]> targetArrayType) throws IllegalStateException;


}
