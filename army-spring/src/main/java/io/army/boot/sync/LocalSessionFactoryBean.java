package io.army.boot.sync;


import io.army.SessionFactory;
import io.army.boot.SessionFactoryBuilder;
import io.army.context.spi.SpringCurrentSessionContext;
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
        , InitializingBean, DisposableBean, ApplicationContextAware, BeanNameAware {

    private String beanName;

    private ApplicationContext applicationContext;

    private SessionFactoryBuilder sessionFactoryBuilder;

    private SessionFactory sessionFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.sessionFactory = this.sessionFactoryBuilder
                .currentSessionContext(SpringCurrentSessionContext.class)
                .build();
    }

    @Override
    public void destroy() throws Exception {
        if (this.sessionFactory != null) {
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
