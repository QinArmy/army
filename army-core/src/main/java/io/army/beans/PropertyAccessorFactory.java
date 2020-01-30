package io.army.beans;


/**
 * Simple factory facade for obtaining {@link BeanWrapper} or {@link ConfigurablePropertyAccessor}instances,
 * in particular for {@link BeanWrapper} instances. Conceals the actual
 * target implementation classes and their extended public signature.
 *
 * @since 1.0
 */
public final class PropertyAccessorFactory {

    private PropertyAccessorFactory() {
    }


    /**
     * Obtain a BeanWrapper for the given target object,
     * accessing properties in JavaBeans style.
     * @param target the target object to wrap
     * @return the property accessor
     * @see BeanWrapperImpl
     */
    public static BeanWrapper forBeanPropertyAccess(Object target) {
        return new BeanWrapperImpl(target);
    }

    /**
     * Obtain a PropertyAccessor for the given target object,
     * accessing properties in direct field style.
     * @param target the target object to wrap
     * @return the property accessor
     * @see DirectFieldAccessor
     */
    public static ConfigurablePropertyAccessor forDirectFieldAccess(Object target) {
        return new DirectFieldAccessor(target);
    }

}
