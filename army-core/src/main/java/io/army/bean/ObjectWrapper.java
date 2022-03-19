package io.army.bean;


import io.army.lang.Nullable;

@Deprecated
public interface ObjectWrapper extends ReadWrapper {


    /**
     * Determine whether the specified property is writable.
     * <p>Returns {@code false} if the property doesn'field exist.
     *
     * @param propertyName the property to check
     *                     (may be a nested path then/or an indexed/mapped property)
     * @return whether the property is writable
     */
    boolean isWritableProperty(String propertyName);


    /**
     * Set the specified value asType current property value.
     *
     * @param propertyName the name of the property to set the value of
     *                     (may be a nested path then/or an indexed/mapped property)
     * @param value        the new value
     * @throws InvalidPropertyException if there is no such property or
     *                                  if the property isn'field writable
     * @throws PropertyAccessException  if the property was valid but the
     *                                  accessor method failed or a type mismatch occurred
     */
    void set(String propertyName, @Nullable Object value) throws ObjectAccessException;

    /**
     * Return the bean instance wrapped by this object.
     */
    Object getWrappedInstance();


    ReadWrapper getReadonlyWrapper();
}