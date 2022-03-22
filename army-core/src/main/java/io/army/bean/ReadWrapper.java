package io.army.bean;

import io.army.lang.Nullable;

public interface ReadWrapper {

    /**
     * Determine whether the specified property is readable.
     * <p>Returns {@code false} if the property doesn'field exist.
     *
     * @param propertyName the property to check
     *                     (may be a nested path then/or an indexed/mapped property)
     * @return whether the property is readable
     */
    boolean isReadable(String propertyName);

    /**
     * Get the current value of the specified property.
     *
     * @param propertyName the name of the property to get the value of
     *                     (may be a nested path then/or an indexed/mapped property)
     * @return the value of the property
     * @throws InvalidPropertyException if there is no such property or
     *                                  if the property isn'field readable
     * @throws PropertyAccessException  if the property was valid but the
     *                                  accessor method failed
     */
    @Nullable
    Object get(String propertyName) throws ObjectAccessException;


    /**
     * Return the type of the wrapped bean instance.
     */
    Class<?> getWrappedClass();

    ObjectAccessor getObjectAccessor();

}
