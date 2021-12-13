package io.army.cache;

import io.army.beans.DomainReadonlyWrapper;
import io.army.beans.ObjectAccessorFactory;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.session.GenericSessionFactory;
import io.army.util.Assert;
import io.army.util.ClassUtils;
import io.qinarmy.util.Pair;
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
    public Pair<IDomain, DomainUpdateAdvice> createDomainProxy(final IDomain domain) {
        final TableMeta<?> tableMeta = this.sessionFactory.tableMeta(domain.getClass());
        Assert.notNull(tableMeta, () -> String.format("not found TableMeta for domain[%s]", domain.getClass().getName()));
        DomainReadonlyWrapper domainWrapper = ObjectAccessorFactory.forDomainReadonlyPropertyAccess(
                domain, tableMeta);

        // 1. obtain pointcut
        final DomainSetterPointcut pointcut = this.tableSetterPointcutMap.get(domainWrapper.tableMeta());
        if (pointcut == null) {
            throw new IllegalArgumentException(String.format("TableMeta[%s] no DomainSetterPointcut"
                    , domainWrapper.tableMeta()));
        }
        //  assert target object and pointcut match
        if (pointcut.tableMeta().javaType() != domain.getClass()) {
            throw new IllegalArgumentException(String.format("domainWrapper[%s] wrapped instance isn't %s type."
                    , domainWrapper, pointcut.tableMeta().javaType().getName()));
        }
        //2. create advice
        final DomainSetterInterceptor advice = DomainSetterInterceptor.build(domainWrapper, pointcut);

        //3. create aop config
        final ProxyCreatorSupport config = new ProxyCreatorSupport();
        config.setTarget(domain);
        config.setProxyTargetClass(true);
        config.addAdvisor(new DefaultPointcutAdvisor(pointcut, advice));
        config.setFrozen(true);

        // 4. create proxy object
        final IDomain proxy = (IDomain) config.getAopProxyFactory().createAopProxy(config)
                .getProxy(ClassUtils.getDefaultClassLoader());

        return new Pair<>(proxy, advice);
    }
}
