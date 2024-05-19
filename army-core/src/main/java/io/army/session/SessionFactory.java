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

import io.army.criteria.Visible;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.executor.ExecutorFactory;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * <p>This interface is base interface of following:
 * <ul>
 *     <li>{@code  io.army.sync.SyncSessionFactory}</li>
 *     <li>{@code io.army.reactive.ReactiveSessionFactory}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface SessionFactory extends CloseableSpec, OptionSpec {

    String name();

    ArmyEnvironment environment();

    ZoneOffset zoneOffset();

    SchemaMeta schemaMeta();

    ServerMeta serverMeta();

    Database serverDatabase();

    Map<Class<?>, TableMeta<?>> tableMap();

    @Nullable
    <T> TableMeta<T> getTable(Class<T> domainClass);

    AllowMode visibleMode();

    AllowMode queryInsertMode();

    /**
     * See {@link ExecutorFactory#driverSpiName()}
     */
    String driverSpiName();


    boolean isSupportSavePoints();

    /**
     * @see ExecutorFactory#isResultItemDriverSpi()
     */
    boolean isResultItemDriverSpi();


    boolean isReadonly();


    boolean isReactive();

    boolean isSync();


    interface SessionBuilderSpec<B, R> {

        B name(@Nullable String name);

        /**
         * Optional,default is {@link SessionFactory#isReadonly()}
         */
        B readonly(boolean readonly);

        B allowQueryInsert(boolean allow);

        B visibleMode(Visible visible);

        <T> B dataSourceOption(Option<T> option, @Nullable T value);

        R build();

    }


}
