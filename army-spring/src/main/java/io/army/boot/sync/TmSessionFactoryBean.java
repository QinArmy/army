package io.army.boot.sync;

import io.army.codec.FieldCodec;
import io.army.dialect.Database;
import io.army.env.ArmyConfigurableArmyEnvironment;
import io.army.interceptor.DomainAdvice;
import io.army.sync.SessionFactoryAdvice;
import io.army.sync.TmSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.XADataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.army.boot.sync.LocalSessionFactoryBean.obtainEnvironment;

/**
 * {@link FactoryBean} that creates a Army {@link io.army.sync.TmSessionFactory}. This is the usual
 * way to set up a shared Army TmSessionFactory in a Spring application context; the
 * SessionFactory can then be passed to data access objects via dependency injection.
 *
 * @since 1.0
 */
public class TmSessionFactoryBean implements FactoryBean<TmSessionFactory>
        , InitializingBean, BeanNameAware, ApplicationContextAware {

    private String beanName;

    private ArmyConfigurableArmyEnvironment environment;

    private int tableCountPerDatabase = 1;

    private List<XADataSource> dataSourceList;

    private List<String> dataSourceBeanNameList;

    private Map<Integer, Database> databaseMap;

    private TmSessionFactory tmSessionFactory;

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
    public void afterPropertiesSet() throws Exception {
        this.tmSessionFactory = TmSessionFactionBuilder.builder(true)

                .name(obtainSessionFactoryName())
                .dataSourceList(obtainDataSourceList())
                .environment(obtainEnvironment(this.environment, applicationContext))
                .domainInterceptor(this.applicationContext.getBeansOfType(DomainAdvice.class).values())

                .fieldCodecs(this.applicationContext.getBeansOfType(FieldCodec.class).values())
                .factoryAdvice(this.applicationContext.getBeansOfType(SessionFactoryAdvice.class).values())
                .tableCountPerDatabase(this.tableCountPerDatabase)
                .databaseMap(this.databaseMap)

                .build();
    }

    @Override
    public TmSessionFactory getObject() throws Exception {
        return this.tmSessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return TmSessionFactory.class;
    }


    /*################################## blow setter method ##################################*/

    public TmSessionFactoryBean setEnvironment(ArmyConfigurableArmyEnvironment environment) {
        this.environment = environment;
        return this;
    }

    public TmSessionFactoryBean setTableCountPerDatabase(int tableCountPerDatabase) {
        this.tableCountPerDatabase = tableCountPerDatabase;
        return this;
    }

    public TmSessionFactoryBean setDataSourceList(List<XADataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
        return this;
    }

    /**
     * If {@link #setDataSourceList(List) } invoked by developer ,this will ignore.
     */
    public TmSessionFactoryBean setDataSourceBeanNameList(List<String> dataSourceBeanNameList) {
        this.dataSourceBeanNameList = dataSourceBeanNameList;
        return this;
    }

    public TmSessionFactoryBean setDatabaseMap(Map<Integer, Database> databaseMap) {
        this.databaseMap = databaseMap;
        return this;
    }

    private List<XADataSource> obtainDataSourceList() {
        List<XADataSource> list = this.dataSourceList;
        if (list != null) {
            return list;
        }
        if (this.dataSourceBeanNameList == null) {
            throw new IllegalArgumentException("not specified dataSourceList or dataSourceBeanNameList");
        }
        list = new ArrayList<>(this.dataSourceBeanNameList.size());
        for (String beanName : this.dataSourceBeanNameList) {
            list.add(this.applicationContext.getBean(beanName, XADataSource.class));
        }
        return list;
    }

    private String obtainSessionFactoryName() {
        // drop SessionFactory suffix.
        String factoryName;
        int index = this.beanName.lastIndexOf("TmSessionFactory");
        if (index > 0) {
            factoryName = this.beanName.substring(0, index);
        } else {
            index = this.beanName.lastIndexOf("SessionFactory");
            if (index > 0) {
                factoryName = this.beanName.substring(0, index);
            } else {
                factoryName = this.beanName;
            }
        }
        return factoryName;
    }

}
