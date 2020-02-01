package io.army.beans;

import io.army.lang.Nullable;

public interface ReadonlyWrapper {

    /**
     * Determine whether the specified property is readable.
     * <p>Returns {@code false} if the property doesn't exist.
     * @param propertyName the property to check
     * (may be a nested path and/or an indexed/mapped property)
     * @return whether the property is readable
     */
    boolean isReadableProperty(String propertyName);


    /**
     * Determine the property type for the specified property,
     * either checking the property descriptor or checking the value
     * in case of an indexed or mapped element.
     * @param propertyName the property to check
     * (may be a nested path and/or an indexed/mapped property)
     * @return the property type for the particular property,
     * or {@code null} if not determinable
     * @throws PropertyAccessException if the property was valid but the
     * accessor method failed
     */
    @Nullable
    Class<?> getPropertyType(String propertyName) throws BeansException;
    /**
     * Get the current value of the specified property.
     * @param propertyName the name of the property to get the value of
     * (may be a nested path and/or an indexed/mapped property)
     * @return the value of the property
     * @throws InvalidPropertyException if there is no such property or
     * if the property isn't readable
     * @throws PropertyAccessException if the property was valid but the
     * accessor method failed
     */
    @Nullable
    Object getPropertyValue(String propertyName) throws BeansException;


    /**
     * Return the bean instance wrapped by this object.
     */
    Object getWrappedInstance();

    /**
     * Return the type of the wrapped bean instance.
     */
    Class<?> getWrappedClass();
}
