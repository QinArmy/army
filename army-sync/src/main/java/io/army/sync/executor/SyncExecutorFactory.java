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

package io.army.sync.executor;


import io.army.session.DataAccessException;
import io.army.session.Option;
import io.army.session.executor.ExecutorFactory;

import java.util.function.Function;

/**
 * <p>This interface representing blocking {@link SyncExecutor} factory.
 *
 * @since 0.6.0
 */
public interface SyncExecutorFactory extends ExecutorFactory, AutoCloseable {


    @Override
    SyncMetaExecutor metaExecutor(Function<Option<?>, ?> optionFunc) throws DataAccessException;

    @Override
    SyncLocalExecutor localExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> optionFunc) throws DataAccessException;

    @Override
    SyncRmExecutor rmExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> optionFunc) throws DataAccessException;

    /**
     * <p>
     * close {@link SyncExecutorFactory},but don't close underlying data source(eg:{@code  javax.sql.DataSource}).
     *     */
    void close() throws DataAccessException;

}
