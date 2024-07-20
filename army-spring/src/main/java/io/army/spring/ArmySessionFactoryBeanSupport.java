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

package io.army.spring;


import io.army.advice.FactoryAdvice;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.executor.ExecutorFactoryProvider;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link FactoryBean} that creates Army {@link io.army.session.SessionFactory}. This is the usual
 * way to set up a shared Army SessionFactory in a Spring application context; the
 * SessionFactory can then be passed to data access objects via dependency injection.
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link io.army.spring.sync.ArmySyncSessionFactoryBean}</li>
 *     <li>{@link io.army.spring.reactive.ArmyReactiveSessionFactoryBean}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public abstract class ArmySessionFactoryBeanSupport implements InitializingBean,
        ApplicationContextAware, BeanNameAware, DisposableBean {

    private String catalog = "";

    private String schema = "";

    private String factoryName;

    private Object dataSource;

    private String dataSourceBeanName;

    private ArmyEnvironment armyEnvironment;

    private List<String> packageList;

    private Collection<FactoryAdvice> factoryAdviceCollection;

    private FieldGeneratorFactory fieldGeneratorFactory;

    private String fieldGeneratorFactoryBean;

    private Function<String, Database> nameToDatabaseFunc;

    private Consumer<ExecutorFactoryProvider> executorFactoryProviderValidator;

    private Function<Class<?>, Function<Object, ?>> columnConverterFunc;

    private ApplicationContext applicationContext;


    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public final void setBeanName(String s) {
        if (this.factoryName == null) {
            this.factoryName = s;
        }
    }




    /*################################## blow setter method ##################################*/

    public final ArmySessionFactoryBeanSupport setDataSource(Object dataSource) {
        this.dataSource = dataSource;
        return this;
    }


    public final ArmySessionFactoryBeanSupport setPackagesToScan(List<String> packageList) {
        this.packageList = packageList;
        return this;
    }


    public final ArmySessionFactoryBeanSupport setDataSourceBeanName(String dataSourceBeanName) {
        this.dataSourceBeanName = dataSourceBeanName;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setArmyEnvironment(ArmyEnvironment armyEnvironment) {
        this.armyEnvironment = armyEnvironment;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setSchema(String catalog, String schema) {
        this.catalog = catalog;
        this.schema = schema;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setFactoryName(String factoryName) {
        this.factoryName = factoryName;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setFieldGeneratorFactory(FieldGeneratorFactory fieldGeneratorFactory) {
        this.fieldGeneratorFactory = fieldGeneratorFactory;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setFieldGeneratorFactoryBean(String fieldGeneratorFactoryBean) {
        this.fieldGeneratorFactoryBean = fieldGeneratorFactoryBean;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setFactoryAdviceCollection(@Nullable Collection<FactoryAdvice> factoryAdviceCollection) {
        this.factoryAdviceCollection = factoryAdviceCollection;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setNameToDatabaseFunc(@Nullable Function<String, Database> nameToDatabaseFunc) {
        this.nameToDatabaseFunc = nameToDatabaseFunc;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setExecutorFactoryProviderValidator(@Nullable Consumer<ExecutorFactoryProvider> executorFactoryProviderValidator) {
        this.executorFactoryProviderValidator = executorFactoryProviderValidator;
        return this;
    }

    public final ArmySessionFactoryBeanSupport setColumnConverterFunc(Function<Class<?>, Function<Object, ?>> columnConverterFunc) {
        this.columnConverterFunc = columnConverterFunc;
        return this;
    }

    /*################################## blow private method ##################################*/


    protected final Object getDataSource() {
        Object dataSource;
        dataSource = this.dataSource;
        if (dataSource == null) {
            final String dataSourceBeanName = this.dataSourceBeanName;
            if (dataSourceBeanName == null) {
                throw new IllegalStateException("Not specified DataSource bean.");
            }
            dataSource = this.applicationContext.getBean(dataSourceBeanName);
        }
        return dataSource;
    }

    protected final ArmyEnvironment getArmyEnvironment() {
        ArmyEnvironment armyEnvironment = this.armyEnvironment;
        if (armyEnvironment == null) {
            armyEnvironment = SpringArmyEnvironment.create(getFactoryName(), this.applicationContext.getEnvironment());
        }
        return armyEnvironment;
    }

    @Nullable
    protected final FieldGeneratorFactory getFieldGeneratorFactory() {
        FieldGeneratorFactory fieldGeneratorFactory = this.fieldGeneratorFactory;
        if (fieldGeneratorFactory == null) {
            final String beanName = this.fieldGeneratorFactoryBean;
            if (beanName != null) {
                fieldGeneratorFactory = this.applicationContext.getBean(beanName, FieldGeneratorFactory.class);
            }
        }
        return fieldGeneratorFactory;
    }

    protected final String getFactoryName() {
        final String name = this.factoryName;
        Assert.state(name != null, "factoryName is null");
        return name;
    }

    protected final List<String> getPackageList() {
        final List<String> list = this.packageList;
        Assert.state(list != null, "packageList is null");
        return list;
    }

    protected final String getCatalog() {
        final String name = this.catalog;
        Assert.state(name != null, "catalog is null");
        return name;
    }

    protected final String getSchema() {
        final String name = this.schema;
        Assert.state(name != null, "schema is null");
        return name;
    }

    @Nullable
    protected final Collection<FactoryAdvice> getFactoryAdviceCollection() {
        return this.factoryAdviceCollection;
    }

    @Nullable
    protected final Function<String, Database> getNameToDatabaseFunc() {
        return this.nameToDatabaseFunc;
    }

    @Nullable
    protected final Consumer<ExecutorFactoryProvider> getExecutorFactoryProviderValidator() {
        return this.executorFactoryProviderValidator;
    }

    @Nullable
    protected final Function<Class<?>, Function<Object, ?>> getColumnConverterFunc() {
        return this.columnConverterFunc;
    }

    /*################################## blow package static method ##################################*/


}
