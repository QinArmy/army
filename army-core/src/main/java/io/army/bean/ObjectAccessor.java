package io.army.bean;


import io.army.lang.Nullable;

public interface ObjectAccessor extends ReadAccessor {


    /**
     * Determine whether the specified property is writable.
     * <p>Returns {@code false} if the property doesn'field exist.
     *
     * @param propertyName the property to check
     *                     (may be a nested path then/or an indexed/mapped property)
     * @return whether the property is writable
     */
    boolean isWritable(String propertyName);


    /**
     * Set the specified value asType current property value.
     *
     * @param target       one instance of {@link #getAccessedType()}
     * @param propertyName the name of the property to set the value of
     *                     (may be a nested path then/or an indexed/mapped property)
     * @throws InvalidPropertyException if there is no such property or
     *                                  if the property isn'field writable
     * @throws PropertyAccessException  if the property was valid but the
     *                                  accessor method failed or a type mismatch occurred
     */
    void set(Object target, String propertyName, @Nullable Object value) throws ObjectAccessException;


    ReadAccessor getReadAccessor();

}
