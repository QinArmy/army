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

package io.army.session;

import io.army.advice.FactoryAdvice;
import io.army.codec.JsonCodec;
import io.army.codec.XmlCodec;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.executor.ExecutorEnv;
import io.army.executor.ExecutorFactoryProvider;
import io.army.generator.FieldGeneratorFactory;
import io.army.option.Option;
import io.army.result.ResultRecord;

import io.army.lang.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>This interface representing the builder spec of {@link SessionFactory} .
 * <p>This interface is base interface of all factory builder.
 * <p>package interface
 *
 * @param <B> factory builder java type,it is the sub interface of this interface
 * @param <R> sync session factory or Mono
 * @since 0.6.0
 */
public sealed interface FactoryBuilder<B, R> permits PackageFactoryBuilder {


    /**
     * <p>Required.
     *
     * @param sessionFactoryName non-empty
     * @return <strong>this</strong>
     */
    B name(String sessionFactoryName);

    /**
     * <p>Required.
     *
     * @return <strong>this</strong>
     */
    B environment(ArmyEnvironment environment);

    /**
     * <p>Required.
     * <p>dataSource can be the instance of {@link io.army.datasource.ReadWriteSplittingDataSource}.
     *
     * @return <strong>this</strong>
     * @see io.army.datasource.ReadWriteSplittingDataSource
     */
    B datasource(Object dataSource);

    /**
     * <p>Required.
     *
     * @return <strong>this</strong>
     */
    B packagesToScan(List<String> packageList);

    /**
     * <p>Optional.
     *
     * @param catalog catalog or empty
     * @param schema  schema or empty
     * @return <strong>this</strong>
     */
    B schema(String catalog, String schema);

    /*
     * <p>
     * Optional.
     *
     */
    //   B fieldCodecs(Collection<FieldCodec> fieldCodecs);

    /**
     * <p>Optional.
     *
     * @return <strong>this</strong>
     */
    B jsonCodec(@Nullable JsonCodec codec);

    /**
     * <p>Optional.
     *
     * @return <strong>this</strong>
     */
    B xmlCodec(@Nullable XmlCodec codec);

    /**
     * <p>Optional.
     *
     * @return <strong>this</strong>
     */
    B factoryAdvice(@Nullable Collection<FactoryAdvice> factoryAdvices);


    /**
     * <p>Optional.
     *
     * @return <strong>this</strong>
     */
    B fieldGeneratorFactory(@Nullable FieldGeneratorFactory factory);


    /**
     * <p>Optional.
     * See
     * <ul>
     *     <li>{@link ExecutorFactoryProvider#createServerMeta(Function)}</li>
     *     <li>{@link Database#mapToDatabase(String, Function)}</li>
     * </ul>
     *
     * @return <strong>this</strong>
     */
    B nameToDatabaseFunc(@Nullable Function<String, Database> function);

    /**
     * <p>Optional.
     * <p>Set a consumer for validating {@link ExecutorFactoryProvider} is the instance which you want.
     * <p>See {@code io.army.env.SyncKey#EXECUTOR_PROVIDER} and  see {@code io.army.env.ReactiveKey#EXECUTOR_PROVIDER}
     *
     * @return <strong>this</strong>
     */
    B executorFactoryProviderValidator(@Nullable Consumer<ExecutorFactoryProvider> consumer);

    /**
     * <p>Optional.
     * <p>See {@link ResultRecord#get(int, Class)} and {@link ExecutorEnv#converterFunc()}
     *
     * @return <strong>this</strong>
     */
    B columnConverterFunc(@Nullable Function<Class<?>, Function<Object, ?>> converterFunc);

    <T> B dataSourceOption(Option<T> option, @Nullable T value);

    /**
     * <p>Create {@link SessionFactory} instance
     *
     * @return <ul>
     * <li>sync api : {@link SessionFactory} instance</li>
     * <li>reactive api : Mono of {@link SessionFactory} instance</li>
     * </ul>
     * @throws SessionFactoryException throw (emit) when
     *                                 <ul>
     *                                     <li>required properties absent</li>
     *                                     <li>name duplication</li>
     *                                     <li>access database occur error</li>
     *                                     <li>properties error</li>
     *                                 </ul>
     */
    R build();

}
