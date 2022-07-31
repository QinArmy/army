package io.army.boot.sync;


import io.army.env.ArmyEnvironment;
import io.army.env.SpringArmyEnvironment;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;
import io.army.sync.LocalFactoryBuilder;
import io.army.sync.LocalSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.List;

/**
 * {@link FactoryBean} that creates Army {@link LocalSessionFactory}. This is the usual
 * way to set up a shared Army SessionFactory in a Spring application context; the
 * SessionFactory can then be passed to data access objects via dependency injection.
 *
 * @since 1.0
 */
public class LocalSessionFactoryBean implements FactoryBean<LocalSessionFactory>
        , InitializingBean, ApplicationContextAware, DisposableBean {

    private String catalog = "";

    private String schema = "";

    private String factoryName;

    private Object dataSource;

    private String dataSourceBeanName;

    private ArmyEnvironment armyEnvironment;

    private List<String> packageList;

    private FieldGeneratorFactory fieldGeneratorFactory;

    private String fieldGeneratorFactoryBean;

    private LocalSessionFactory sessionFactory;

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {


        this.sessionFactory = LocalFactoryBuilder.builder()
                .name(this.factoryName)
                .datasource(this.getDataSource())
                .packagesToScan(this.packageList)
                .environment(this.getArmyEnvironment(factoryName))

                .schema(this.catalog, this.schema)
                .fieldGeneratorFactory(this.getFieldGeneratorFactory())
                .build();
    }


    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public LocalSessionFactory getObject() {
        return this.sessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return LocalSessionFactory.class;
    }

    @Override
    public void destroy() {
        final LocalSessionFactory sessionFactory = this.sessionFactory;
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

    public LocalSessionFactoryBean setFieldGeneratorFactory(FieldGeneratorFactory fieldGeneratorFactory) {
        this.fieldGeneratorFactory = fieldGeneratorFactory;
        return this;
    }

    public LocalSessionFactoryBean setFieldGeneratorFactoryBean(String fieldGeneratorFactoryBean) {
        this.fieldGeneratorFactoryBean = fieldGeneratorFactoryBean;
        return this;
    }

    /*################################## blow private method ##################################*/


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

    @Nullable
    private FieldGeneratorFactory getFieldGeneratorFactory() {
        FieldGeneratorFactory fieldGeneratorFactory = this.fieldGeneratorFactory;
        if (fieldGeneratorFactory == null) {
            String beanName = this.fieldGeneratorFactoryBean;
            if (beanName != null) {
                fieldGeneratorFactory = this.applicationContext.getBean(beanName, FieldGeneratorFactory.class);
            }
        }
        return fieldGeneratorFactory;
    }


    /*################################## blow package static method ##################################*/


}
