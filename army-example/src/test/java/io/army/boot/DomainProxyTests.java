package io.army.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class DomainProxyTests {

    private final static Logger LOG = LoggerFactory.getLogger(DomainProxyTests.class);

    @Test
    public void setterProxyObject() {
        /*Account account = new Account();
        Pointcut pointcut = SetterMethodMatcherPointcut.build(Account_.T);
        DomainSetterInterceptor interceptor = new DomainSetterInterceptor(PropertyAccessorFactory.forDomainReadonlyPropertyAccess(account));

        ProxyCreatorSupport config = new ProxyCreatorSupport();

        config.setTarget(account);
        config.setProxyTargetClass(true);
        config.addAdvisor(new DefaultPointcutAdvisor(pointcut, interceptor));
        config.setFrozen(true);

        Account proxyAccount = (Account) config.getAopProxyFactory()
                .createAopProxy(config).getProxy(ClassUtils.getDefaultClassLoader());

        proxyAccount.setAcceptTime(LocalDateTime.MAX);
        LOG.info("proxyAccount:{}",proxyAccount.getClass().getName());
        proxyAccount.setAcceptTime(LocalDateTime.now());
        LOG.info("id:{}",proxyAccount.getId());*/


    }
}
