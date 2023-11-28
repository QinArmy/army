package io.army.spring.reactive;

import io.army.reactive.ReactiveFactoryBuilder;
import io.army.reactive.ReactiveSessionFactory;
import io.army.spring.ArmySessionFactoryBeanSupport;
import org.springframework.beans.factory.FactoryBean;

public class ArmyReactiveSessionFactoryBean extends ArmySessionFactoryBeanSupport
        implements FactoryBean<ReactiveSessionFactory> {


    private ReactiveSessionFactory sessionFactory;

    public ArmyReactiveSessionFactoryBean() {
    }

    @Override
    public final void afterPropertiesSet() throws Exception {

        this.sessionFactory = ReactiveFactoryBuilder.builder()

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

                .build()
                .block();
    }

    @Override
    public final void destroy() {
        final ReactiveSessionFactory sessionFactory = this.sessionFactory;
        if (sessionFactory != null) {
            this.sessionFactory = null;
            sessionFactory.close()
                    .block();
        }
    }

    @Override
    public final ReactiveSessionFactory getObject() {
        return this.sessionFactory;
    }

    @Override
    public final Class<?> getObjectType() {
        return ReactiveSessionFactory.class;
    }


}
