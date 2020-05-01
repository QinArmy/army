package io.army.boot.sync;


import io.army.SessionFactory;
import io.army.boot.SessionFactoryBuilder;
import io.army.boot.SessionFactoryInterceptor;
import io.army.codec.FieldCodec;
import io.army.env.SpringEnvironmentAdaptor;
import io.army.interceptor.DomainInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * {@link FactoryBean} that creates a Army {@link SessionFactory}. This is the usual
 * way to set up a shared Army SessionFactory in a Spring application context; the
 * SessionFactory can then be passed to data access objects via dependency injection.
 *
 * @since 1.0
 */
public class LocalSessionFactoryBean implements FactoryBean<SessionFactory>
        , InitializingBean, DisposableBean, BeanNameAware, ApplicationContextAware {

    private String beanName;

    private SessionFactoryBuilder sessionFactoryBuilder;

    private SessionFactory sessionFactory;

    private ApplicationContext applicationContext;


    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        this.sessionFactory = this.sessionFactoryBuilder
                .name(beanName)
                .environment(new SpringEnvironmentAdaptor(applicationContext.getEnvironment()))
                .domainInterceptor(applicationContext.getBeansOfType(DomainInterceptor.class).values())
                .fieldCodecs(applicationContext.getBeansOfType(FieldCodec.class).values())

                .interceptorList(applicationContext.getBeansOfType(SessionFactoryInterceptor.class).values())
                .build();
    }

    @Override
    public void destroy() {
        if (this.sessionFactory != null && !this.sessionFactory.closed()) {
            this.sessionFactory.close();
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public SessionFactory getObject() throws Exception {
        return this.sessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return SessionFactory.class;
    }


    /*################################## blow setter method ##################################*/

    public LocalSessionFactoryBean setSessionFactoryBuilder(SessionFactoryBuilder sessionFactoryBuilder) {
        this.sessionFactoryBuilder = sessionFactoryBuilder;
        return this;
    }
}
