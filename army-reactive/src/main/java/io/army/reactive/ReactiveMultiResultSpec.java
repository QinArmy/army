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

package io.army.reactive;

import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultItem;
import io.army.session.record.ResultStates;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ReactiveMultiResultSpec {

    <R> Flux<R> nextQuery(Class<R> resultClass);

    <R> Flux<R> nextQuery(Class<R> resultClass, Consumer<ResultStates> consumer);


    <R> Flux<Optional<R>> nextQueryOptional(Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<Optional<R>> nextQueryOptional(Class<R> resultClass, Consumer<ResultStates> consumer);


    <R> Flux<R> nextQueryObject(Supplier<R> constructor);

    <R> Flux<R> nextQueryObject(Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Flux<R> nextQueryRecord(Function<CurrentRecord, R> function);

    <R> Flux<R> nextQueryRecord(Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);


    Flux<ResultItem> nextQueryAsFlux();


}
