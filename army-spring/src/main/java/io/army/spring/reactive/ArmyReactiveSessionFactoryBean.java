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
