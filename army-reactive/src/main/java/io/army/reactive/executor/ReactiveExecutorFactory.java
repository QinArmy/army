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

import io.army.reactive.ReactiveCloseable;
import io.army.session.Option;
import io.army.session.executor.ExecutorFactory;
import reactor.core.publisher.Mono;

import java.util.function.Function;


/**
 * <p>This interface representing {@link ReactiveExecutor} factory.
 * <p>This interface extends {@link ExecutorFactory} and have overridden following methods:
 * <ul>
 *     <li>{@link #metaExecutor(Function)}</li>
 *     <li>{@link #localExecutor(String, boolean, Function)}</li>
 *     <li>{@link #rmExecutor(String, boolean, Function)}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface ReactiveExecutorFactory extends ExecutorFactory, ReactiveCloseable {


    @Override
    Mono<ReactiveMetaExecutor> metaExecutor(Function<Option<?>, ?> func);

    @Override
    Mono<ReactiveLocalExecutor> localExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> func);

    @Override
    Mono<ReactiveRmExecutor> rmExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> func);

}
