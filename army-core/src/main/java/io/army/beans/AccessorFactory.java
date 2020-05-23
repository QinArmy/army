package io.army.beans;


import io.army.criteria.impl.TableMetaFactory;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.BeanUtils;

import java.util.Map;

/**
 * Simple factory facade for obtaining {@link BeanWrapper} instances,
 * in particular for {@link BeanWrapper} instances. Conceals the actual
 * target implementation classes then their extended public signature.
 *
 * @since 1.0
 */
public abstract class AccessorFactory {


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


    public static DomainWrapper forDomainPropertyAccess(IDomain domain) {
        return new DomainWrapperImpl(domain);
    }

    public static DomainWrapper forDomainPropertyAccess(IDomain domain, TableMeta<?> tableMeta) {
        return new DomainWrapperImpl(domain, tableMeta);
    }

    static BeanWrapper forSimplePropertyAccess(Class<?> simpleType, String propertyName) {
        return new SimpleTypeWrapper(simpleType, propertyName);
    }

    public static BeanWrapper forBeanPropertyAccess(Class<?> beanClass) {
        BeanWrapper beanWrapper;
        final String simpleName = beanClass.getSimpleName();
        if (simpleName.equals("Pair")) {
            beanWrapper = new PairBeanWrapperImpl(beanClass);
        } else if (simpleName.equals("Tripe")) {
            beanWrapper = new TripeWrapperImpl(beanClass);
        } else {
            beanWrapper = new BeanWrapperImpl(BeanUtils.instantiateClass(beanClass));
        }
        return beanWrapper;
    }

    @SuppressWarnings("unchecked")
    public static ReadonlyWrapper forReadonlyAccess(Object target) {
        ReadonlyWrapper readonlyWrapper;
        if (target instanceof Map) {
            readonlyWrapper = new MapReadonlyWrapper((Map<String, Object>) target);
        } else {
            readonlyWrapper = new ReadonlyWrapperImpl(target);
            ;
        }
        return readonlyWrapper;
    }

    public static DomainReadonlyWrapper forDomainReadonlyPropertyAccess(IDomain domain) {
        return new DomainReadonlyWrapperImpl(domain, TableMetaFactory.getTableMeta(domain.getClass()));
    }


}
