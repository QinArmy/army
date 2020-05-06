package io.army.beans;


import io.army.criteria.impl.TableMetaFactory;
import io.army.domain.IDomain;
import io.army.util.BeanUtils;
import io.army.util.Pair;
import io.army.util.Triple;

/**
 * Simple factory facade for obtaining {@link ObjectWrapper} instances,
 * in particular for {@link ObjectWrapper} instances. Conceals the actual
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
    public static ObjectWrapper forBeanPropertyAccess(Object target) {
        return new BeanWrapperImpl(target);
    }

    public static DomainWrapper forDomainPropertyAccess(IDomain domain) {
        return new DomainWrapperImpl(domain);
    }

    static ObjectWrapper forSimplePropertyAccess(Class<?> simpleType, String propertyName) {
        return new SimpleTypeWrapper(simpleType, propertyName);
    }

    public static ObjectWrapper forBeanPropertyAccess(Class<?> beanClass) {
        ObjectWrapper objectWrapper;
        if (beanClass == Pair.class) {
            objectWrapper = new PairBeanWrapperImpl();
        } else if (beanClass == Triple.class) {
            objectWrapper = new TripeWrapperImpl();
        } else {
            objectWrapper = new BeanWrapperImpl(BeanUtils.instantiateClass(beanClass));
        }
        return objectWrapper;
    }

    public static ReadonlyWrapper forReadonlyPropertyAccess(Object target) {
        return new ReadonlyWrapperImpl(target);
    }

    public static DomainReadonlyWrapper forDomainReadonlyPropertyAccess(IDomain domain) {
        return new DomainReadonlyWrapperImpl(domain, TableMetaFactory.getTableMeta(domain.getClass()));
    }


}
