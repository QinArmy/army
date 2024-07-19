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

package io.army.result;

import io.army.ArmyException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;


public interface SyncMultiResult extends AutoCloseable {

    ResultType nextType() throws ArmyException;

    long updateCount() throws ArmyException;

    ResultStates updateStates() throws ArmyException;

    <R> Stream<R> query(Class<R> resultClass) throws ArmyException;

    <R> Stream<R> query(Class<R> resultClass, Consumer<ResultStates> consumer) throws ArmyException;

    <R> Stream<R> queryObject(Supplier<R> constructor) throws ArmyException;

    <R> Stream<R> queryObject(Supplier<R> constructor, Consumer<ResultStates> consumer) throws ArmyException;

    <R> Stream<R> queryRecord(Function<? super CurrentRecord, R> function) throws ArmyException;

    <R> Stream<R> queryRecord(Function<? super CurrentRecord, R> function, Consumer<ResultStates> consumer) throws ArmyException;




    @Override
    void close() throws ArmyException;


}
