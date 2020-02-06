package io.army.beans;


/**
 * Simple factory facade for obtaining {@link BeanWrapper} instances,
 * in particular for {@link BeanWrapper} instances. Conceals the actual
 * target implementation classes then their extended public signature.
 *
 * @since 1.0
 */
public abstract class PropertyAccessorFactory {


    /**
     * Obtain a BeanWrapper for the given target object,
     * accessing properties in JavaBeans style.
     *
     * @param target the target object to wrap
     * @return the property accessor
     * @see BeanWrapperImpl
     */
    public static BeanWrapper forBeanPropertyAccess(Object target) {
        return new BeanWrapperImpl(target);
    }

    public static ReadonlyWrapper forReadonlyPropertyAccess(Object target){
        return new ReadonlyWrapperImpl(target);
    }


}
