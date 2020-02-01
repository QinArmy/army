package io.army.beans;


import io.army.lang.Nullable;

public interface BeanWrapper  extends ReadonlyWrapper {



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


    ReadonlyWrapper getReadonlyWrapper();
}
