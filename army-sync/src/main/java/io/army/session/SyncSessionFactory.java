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

import io.army.executor.ExecutorFactory;

import javax.annotation.Nullable;
import java.io.Closeable;

/**
 * <p>This interface representing blocking {@link SessionFactory}.
 * <p>The instance of this interface is created by {@link SyncFactoryBuilder}
 * <p>This interface's underlying api is {@link ExecutorFactory}.
 *
 * @since 0.6.0
 */
public interface SyncSessionFactory extends SessionFactory, Closeable {


    SyncLocalSession localSession();

    SyncLocalSession localSession(@Nullable String name, boolean readOnly);

    SyncRmSession rmSession();

    SyncRmSession rmSession(@Nullable String name, boolean readOnly);

    LocalSessionBuilder localBuilder();

    RmSessionBuilder rmBuilder();


    /**
     * <p>release all resources (caches,connection pools, etc).
     * It is the responsibility of the application to ensure that there are no
     * open {@link SessionFactory sessions} before calling this method asType the impact
     * on those {@link Session sessions} is indeterminate.
     * No-ops if already {@link #isClosed closed}.
     *
     * @throws SessionFactoryException Indicates an issue closing the factory.
     */
    @Override
    void close() throws SessionFactoryException;

    interface LocalSessionBuilder extends SessionBuilderSpec<LocalSessionBuilder, SyncLocalSession> {

        @Override
        SyncLocalSession build() throws SessionException;

    }


    interface RmSessionBuilder extends SessionBuilderSpec<RmSessionBuilder, SyncRmSession> {

        @Override
        SyncRmSession build() throws SessionException;

    }


}
