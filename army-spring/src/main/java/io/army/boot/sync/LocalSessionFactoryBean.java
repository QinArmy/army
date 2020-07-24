package io.army.boot.sync;


import io.army.codec.FieldCodec;
import io.army.env.Environment;
import io.army.env.SpringEnvironmentAdaptor;
import io.army.interceptor.DomainInterceptor;
import io.army.sync.SessionFactory;
import io.army.util.Assert;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;

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

    private DataSource dataSource;

    private Environment environment;

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
        Assert.state(this.dataSource != null, "dataSource is null");

        Environment environment = this.environment;
        if (environment == null) {
            environment = new SpringEnvironmentAdaptor(applicationContext.getEnvironment());
        }

        this.sessionFactory = SessionFactoryBuilder.builder()
                .name(getSessionFactoryName())
                .datasource(this.dataSource)
                .environment(environment)
                .domainInterceptor(applicationContext.getBeansOfType(DomainInterceptor.class).values())
                .fieldCodecs(applicationContext.getBeansOfType(FieldCodec.class).values())

                .factoryAdvice(applicationContext.getBeansOfType(SessionFactoryAdvice.class).values())
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

    public LocalSessionFactoryBean setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public LocalSessionFactoryBean setEnvironment(Environment environment) {
        this.environment = environment;
        return this;
    }

    /*################################## blow private method ##################################*/

    private String getSessionFactoryName() {
        // drop SessionFactory suffix.
        String factoryName;
        int index = this.beanName.lastIndexOf(SessionFactory.class.getSimpleName());
        if (index > 0) {
            factoryName = this.beanName.substring(0, index);
        } else {
            factoryName = this.beanName;
        }
        return factoryName;
    }
}
