/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.spring.sync;

import io.army.session.SyncFactoryBuilder;
import io.army.session.SyncSessionFactory;
import io.army.spring.ArmySessionFactoryBeanSupport;
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
