package io.army.spring.sync;

import io.army.spring.ArmySessionFactoryBeanSupport;
import io.army.sync.SyncFactoryBuilder;
import io.army.sync.SyncSessionFactory;
import org.springframework.beans.factory.FactoryBean;

public class ArmySyncSessionFactoryBean extends ArmySessionFactoryBeanSupport
        implements FactoryBean<SyncSessionFactory> {

    public static ArmySyncSessionFactoryBean create() {
        return new ArmySyncSessionFactoryBean();
    }


    private SyncSessionFactory sessionFactory;

    private ArmySyncSessionFactoryBean() {
    }

    @Override
    public final void afterPropertiesSet() {

        this.sessionFactory = SyncFactoryBuilder.builder()

                .name(getFactoryName())
                .environment(getArmyEnvironment())
                .datasource(getDataSource())
                .packagesToScan(getPackageList())

                .schema(getCatalog(), getSchema())
                .factoryAdvice(getFactoryAdviceCollection())
                .fieldGeneratorFactory(getFieldGeneratorFactory())
                .nameToDatabaseFunc(getNameToDatabaseFunc())

                .executorFactoryProviderValidator(getExecutorFactoryProviderValidator())
                .columnConverterFunc(getColumnConverterFunc())

                .build();

    }

    @Override
    public final void destroy() {
        final SyncSessionFactory sessionFactory = this.sessionFactory;
        if (sessionFactory != null) {
            this.sessionFactory = null;
            sessionFactory.close();
        }
    }

    @Override
    public final SyncSessionFactory getObject() {
        return this.sessionFactory;
    }

    @Override
    public final Class<?> getObjectType() {
        return SyncSessionFactory.class;
    }


}
