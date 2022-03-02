package io.army.boot.sync;


import io.army.beans.ArmyBean;
import io.army.env.ArmyConfigurableArmyEnvironment;
import io.army.env.ArmyEnvironment;
import io.army.env.SpringEnvironmentAdaptor;
import io.army.lang.Nullable;
import io.army.sync.FactoryBuilder;
import io.army.sync.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.List;

/**
 * {@link FactoryBean} that creates a Army {@link SessionFactory}. This is the usual
 * way to set up a shared Army SessionFactory in a Spring application context; the
 * SessionFactory can then be passed to data access objects via dependency injection.
 *
 * @since 1.0
 */
public class LocalSessionFactoryBean implements FactoryBean<SessionFactory>
        , InitializingBean, BeanNameAware, ApplicationContextAware {

    private String beanName;

    private DataSource dataSource;

    private String dataSourceBeanName;

    private ArmyConfigurableArmyEnvironment environment;

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
        DataSource dataSource;
        dataSource = this.dataSource;
        if (dataSource == null) {
            if (this.dataSourceBeanName == null) {
                String m = String.format("Not specified %s bean.", DataSource.class.getName());
                throw new IllegalStateException(m);
            }
            dataSource = applicationContext.getBean(DataSource.class, this.dataSourceBeanName);
        }
        this.sessionFactory = FactoryBuilder.builder()
                .name(this.beanName)
                .datasource(dataSource)
                .packagesToScan(this.packageList)
                .build();

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


    public LocalSessionFactoryBean packagesToScan(List<String> packageList) {
        this.packageList = packageList;
        return this;
    }

    /**
     * If {@link #setDataSource(DataSource) } invoked by developer ,this will ignore.
     */
    public LocalSessionFactoryBean setDataSourceBeanName(String dataSourceBeanName) {
        this.dataSourceBeanName = dataSourceBeanName;
        return this;
    }

    public LocalSessionFactoryBean setEnvironment(ArmyConfigurableArmyEnvironment environment) {
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

    private DataSource obtainDataSource() {
        DataSource returnDataSource = this.dataSource;
        if (returnDataSource == null) {
            if (this.dataSourceBeanName == null) {
                throw new IllegalArgumentException("must specified dataSource or dataSourceBeanName");
            }
            returnDataSource = applicationContext.getBean(this.dataSourceBeanName, DataSource.class);
        }
        return returnDataSource;
    }

    /*################################## blow package static method ##################################*/

    static ArmyEnvironment obtainEnvironment(@Nullable ArmyConfigurableArmyEnvironment armyEnvironment
            , ApplicationContext applicationContext) {
        ArmyConfigurableArmyEnvironment environment = armyEnvironment;
        if (environment == null) {
            environment = new SpringEnvironmentAdaptor(applicationContext.getEnvironment());
        }
        environment.addBeansIfNotExists(applicationContext.getBeansOfType(ArmyBean.class));
        return environment;
    }
}
