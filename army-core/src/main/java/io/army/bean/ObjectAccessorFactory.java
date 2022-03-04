package io.army.bean;


import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.BeanUtils;
import io.army.util.PairBean;
import io.army.util.TripleBean;

import java.util.Map;

/**
 * Simple factory facade for obtaining {@link ObjectWrapper} instances,
 * in particular for {@link ObjectWrapper} instances. Conceals the actual
 * target implementation classes then their extended public signature.
 *
 * @since 1.0
 */
public abstract class ObjectAccessorFactory {


    /**
     * Obtain a ObjectWrapper for the given target object,
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

    public static DomainWrapper forDomainPropertyAccess(IDomain domain, TableMeta<?> tableMeta) {
        return new DomainWrapperImpl(domain, tableMeta);
    }

    static ObjectWrapper forSimplePropertyAccess(Class<?> simpleType, String propertyName) {
        return new SimpleTypeWrapper(simpleType, propertyName);
    }

    public static ObjectWrapper forBeanPropertyAccess(Class<?> beanClass) {
        ObjectWrapper beanWrapper;
        if (PairBean.class.isAssignableFrom(beanClass)) {
            beanWrapper = new PairBeanWrapperImpl(beanClass);
        } else if (TripleBean.class.isAssignableFrom(beanClass)) {
            beanWrapper = new TripeWrapperImpl(beanClass);
        } else {
            beanWrapper = new BeanWrapperImpl(BeanUtils.instantiateClass(beanClass));
        }
        return beanWrapper;
    }

    public static ObjectWrapper forMapAccess(Class<?> mapClass) {
        if (!Map.class.isAssignableFrom(mapClass)) {
            throw new IllegalArgumentException(String.format("mapClass[%s] isn't Map type", mapClass.getName()));
        }
        return new MapWrapperImpl(mapClass);
    }

    public static ObjectWrapper forIdAccess(Class<?> idClass) {
        return new IdPropertyWrapper(idClass);
    }

    @SuppressWarnings("unchecked")
    public static ReadWrapper forReadonlyAccess(Object target) {
        ReadWrapper readonlyWrapper;
        if (target instanceof Map) {
            readonlyWrapper = new MapReadonlyWrapper((Map<String, Object>) target);
        } else {
            readonlyWrapper = new ReadonlyWrapperImpl(target);
        }
        return readonlyWrapper;
    }

    public static DomainReadonlyWrapper forDomainReadonlyPropertyAccess(IDomain domain) {
        return null;
    }

    public static DomainReadonlyWrapper forDomainReadonlyPropertyAccess(IDomain domain, TableMeta<?> tableMeta) {
        return new DomainReadonlyWrapperImpl(domain, tableMeta);
    }


}
