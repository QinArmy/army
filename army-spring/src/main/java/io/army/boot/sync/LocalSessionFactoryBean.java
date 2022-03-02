package io.army.boot.sync;


import io.army.env.ArmyEnvironment;
import io.army.env.SpringArmyEnvironment;
import io.army.sync.FactoryBuilder;
import io.army.sync.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.List;

/**
 * {@link FactoryBean} that creates Army {@link SessionFactory}. This is the usual
 * way to set up a shared Army SessionFactory in a Spring application context; the
 * SessionFactory can then be passed to data access objects via dependency injection.
 *
 * @since 1.0
 */
public class LocalSessionFactoryBean implements FactoryBean<SessionFactory>
        , InitializingBean, BeanNameAware, ApplicationContextAware, DisposableBean {

    private String beanName;

    private String catalog = "";

    private String schema = "";

    private String factoryName;

    private Object dataSource;

    private String dataSourceBeanName;

    private ArmyEnvironment armyEnvironment;

    private List<String> packageList;

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
        final String factoryName;
        factoryName = this.getSessionFactoryName();

        this.sessionFactory = FactoryBuilder.builder()
                .name(factoryName)
                .datasource(this.getDataSource())
                .packagesToScan(this.packageList)
                .environment(this.getArmyEnvironment(factoryName))

                .schema(this.catalog, this.schema)
                .build();

    }


    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public SessionFactory getObject() {
        return this.sessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return SessionFactory.class;
    }

    @Override
    public void destroy() {
        final SessionFactory sessionFactory = this.sessionFactory;
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /*################################## blow setter method ##################################*/

    public LocalSessionFactoryBean setDataSource(Object dataSource) {
        this.dataSource = dataSource;
        return this;
    }


    public LocalSessionFactoryBean setPackagesToScan(List<String> packageList) {
        this.packageList = packageList;
        return this;
    }


    public LocalSessionFactoryBean setDataSourceBeanName(String dataSourceBeanName) {
        this.dataSourceBeanName = dataSourceBeanName;
        return this;
    }

    public LocalSessionFactoryBean setArmyEnvironment(ArmyEnvironment armyEnvironment) {
        this.armyEnvironment = armyEnvironment;
        return this;
    }

    public LocalSessionFactoryBean setSchema(String catalog, String schema) {
        this.catalog = catalog;
        this.schema = schema;
        return this;
    }

    public LocalSessionFactoryBean setFactoryName(String factoryName) {
        this.factoryName = factoryName;
        return this;
    }


    /*################################## blow private method ##################################*/

    private String getSessionFactoryName() {
        String factoryName = this.factoryName;
        if (factoryName == null) {
            int index = this.beanName.lastIndexOf(SessionFactory.class.getSimpleName());
            if (index > 0) {
                // drop SessionFactory suffix.
                factoryName = this.beanName.substring(0, index);
            } else {
                factoryName = this.beanName;
            }
        }
        return factoryName;
    }

    private Object getDataSource() {
        Object dataSource;
        dataSource = this.dataSource;
        if (dataSource == null) {
            if (this.dataSourceBeanName == null) {
                String m = String.format("Not specified %s bean.", DataSource.class.getName());
                throw new IllegalStateException(m);
            }
            dataSource = applicationContext.getBean(this.dataSourceBeanName);
        }
        return dataSource;
    }

    private ArmyEnvironment getArmyEnvironment(String factoryName) {
        ArmyEnvironment armyEnvironment = this.armyEnvironment;
        if (armyEnvironment == null) {
            armyEnvironment = new SpringArmyEnvironment(factoryName, this.applicationContext.getEnvironment());
        }
        return armyEnvironment;
    }


    /*################################## blow package static method ##################################*/


}
