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

import io.army.dialect.Database;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.executor.DataAccessException;
import io.army.executor.ExecutorFactoryProvider;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This interface representing provider of blocking executor.
 */
public interface SyncExecutorFactoryProvider extends ExecutorFactoryProvider {

    @Override
    ServerMeta createServerMeta(@Nullable Function<String, Database> func) throws DataAccessException;

    /**
     * @throws IllegalStateException    throw when invoke this method before {@link #createServerMeta(Function)}
     * @throws IllegalArgumentException throw when {@link  MappingEnv#serverMeta()} not match.
     */
    @Override
    SyncExecutorFactory createFactory(ExecutorEnv env) throws DataAccessException;



}
