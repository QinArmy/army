package io.army.cache;

import io.army.GenericSessionFactory;
import io.army.beans.DomainWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.ClassUtils;
import io.army.util.Pair;
import org.springframework.aop.framework.ProxyCreatorSupport;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.util.HashMap;
import java.util.Map;

final class DomainProxyFactoryImpl implements DomainProxyFactory {

    static DomainProxyFactoryImpl build(GenericSessionFactory sessionFactory) {
        Map<TableMeta<?>, DomainSetterPointcut> tableSetterPointcutMap = new HashMap<>();
        for (TableMeta<?> tableMeta : sessionFactory.tableMetaMap().values()) {
            if (!tableMeta.immutable()) {
                tableSetterPointcutMap.put(tableMeta, SetterMethodMatcherPointcut.build(tableMeta));
            }
        }
        return new DomainProxyFactoryImpl(sessionFactory, tableSetterPointcutMap);
    }


    private final GenericSessionFactory sessionFactory;

    private final Map<TableMeta<?>, DomainSetterPointcut> tableSetterPointcutMap;

    private DomainProxyFactoryImpl(GenericSessionFactory sessionFactory
            , Map<TableMeta<?>, DomainSetterPointcut> tableSetterPointcutMap) {
        this.sessionFactory = sessionFactory;
        this.tableSetterPointcutMap = tableSetterPointcutMap;
    }

    @Override
    public Pair<IDomain, DomainUpdateAdvice> createDomainProxy(IDomain domain) {
        DomainWrapper domainWrapper = PropertyAccessorFactory.forDomainPropertyAccess(
                domain, this.sessionFactory.tableMeta(domain.getClass()));

        // 1. obtain pointcut
        final DomainSetterPointcut pointcut = this.tableSetterPointcutMap.get(domainWrapper.tableMeta());
        if (pointcut == null) {
            throw new IllegalArgumentException(String.format("FieldMeta[%s] no DomainSetterPointcut"
                    , domainWrapper.tableMeta()));
        }
        //2. create advice
        final DomainSetterInterceptor advice = DomainSetterInterceptor.build(domainWrapper, pointcut);
        // 3. obtain target object
        final Object target = domainWrapper.getWrappedInstance();
        if (!pointcut.tableMeta().javaType().isInstance(target)) {
            throw new IllegalArgumentException(String.format("domainWrapper[%s] wrapped instance isn't %s type."
                    , domainWrapper, pointcut.tableMeta().javaType().getName()));
        }
        //4. create aop config
        final ProxyCreatorSupport config = new ProxyCreatorSupport();
        config.setTarget(target);
        config.setProxyTargetClass(true);
        config.addAdvisor(new DefaultPointcutAdvisor(pointcut, advice));
        config.setFrozen(true);

        // 5. create proxy object
        final IDomain proxy = (IDomain) config.getAopProxyFactory().createAopProxy(config)
                .getProxy(ClassUtils.getDefaultClassLoader());

        return new Pair<>(proxy, advice);
    }
}
