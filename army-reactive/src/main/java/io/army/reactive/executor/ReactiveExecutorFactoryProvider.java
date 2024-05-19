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

package io.army.reactive.executor;

import io.army.dialect.Database;
import io.army.executor.ExecutorEnv;
import io.army.executor.ExecutorFactoryProvider;
import io.army.meta.ServerMeta;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This interface representing the provider of {@link ReactiveExecutorFactory}.
 * <p>This interface extends {@link ExecutorFactoryProvider} and
 * This interface have overridden following methods :
 * <ul>
 *     <li>{@link #createServerMeta(Function)}</li>
 *     <li>{@link #createFactory(ExecutorEnv)}</li>
 * </ul>
 * for reactor.
 *
 * @since 0.6.0
 */
public interface ReactiveExecutorFactoryProvider extends ExecutorFactoryProvider {

    @Override
    Mono<ServerMeta> createServerMeta(@Nullable Function<String, Database> func);


    @Override
    Mono<ReactiveExecutorFactory> createFactory(ExecutorEnv env);


}
