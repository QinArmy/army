package io.army.beans;


import io.army.lang.Nullable;

public interface BeanWrapper  {

    /**
     * Determine whether the specified property is readable.
     * <p>Returns {@code false} if the property doesn't exist.
     * @param propertyName the property to check
     * (may be a nested path and/or an indexed/mapped property)
     * @return whether the property is readable
     */
    boolean isReadableProperty(String propertyName);

    /**
     * Determine whether the specified property is writable.
     * <p>Returns {@code false} if the property doesn't exist.
     * @param propertyName the property to check
     * (may be a nested path and/or an indexed/mapped property)
     * @return whether the property is writable
     */
    boolean isWritableProperty(String propertyName);

    /**
     * Specify a limit for array and collection auto-growing.
     * <p>Default is unlimited on a plain BeanWrapper.
     * @since 4.1
     */
    void setAutoGrowCollectionLimit(int autoGrowCollectionLimit);

    /**
     * Return the limit for array and collection auto-growing.
     * @since 4.1
     */
    int getAutoGrowCollectionLimit();

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
     * Set the specified value as current property value.
     * @param propertyName the name of the property to set the value of
     * (may be a nested path and/or an indexed/mapped property)
     * @param value the new value
     * @throws InvalidPropertyException if there is no such property or
     * if the property isn't writable
     * @throws PropertyAccessException if the property was valid but the
     * accessor method failed or a type mismatch occurred
     */
    void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException;


    /**
     * Return the bean instance wrapped by this object.
     */
    Object getWrappedInstance();

    /**
     * Return the type of the wrapped bean instance.
     */
    Class<?> getWrappedClass();


}
