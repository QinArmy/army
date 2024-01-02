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

package io.army.datasource;


import io.army.session.Option;

import java.util.function.Function;


/**
 * <p>This interface representing routing DataSource for Read/Write Splitting.
 *
 * @param <R> the java type of DataSource
 * @since 0.6.0
 */
public interface ReadWriteSplittingDataSource<R> {

    /**
     * <p>Select one read write DataSource
     * <p>Above options is passed by application developer with following methods :
     * <ul>
     *     <li>{@link io.army.session.SessionFactory.SessionBuilderSpec#dataSourceOption(Option, Object)} </li>
     *     <li>{@link io.army.session.FactoryBuilderSpec#dataSourceOption(Option, Object)}</li>
     * </ul>
     *
     * @param func option function, default {@link Option#EMPTY_FUNC}
     * @return a DataSource that can support read/write
     */
    R readWriteDataSource(final Function<Option<?>, ?> func);


    /**
     * <p>Select one readonly DataSource
     * <p>The implementation of this method perhaps support some of following :
     * <ul>
     *     <li>{@link Option#TIMEOUT_MILLIS} : transaction timeout</li>
     *     <li>{@link Option#LOCK_TIMEOUT_MILLIS : max lock time}</li>
     * </ul>
     * above options can help application to select optimal DataSource.
     * <p>Above options is passed by application developer with following methods :
     * <ul>
     *     <li>{@link io.army.session.SessionFactory.SessionBuilderSpec#dataSourceOption(Option, Object)} </li>
     *     <li>{@link io.army.session.FactoryBuilderSpec#dataSourceOption(Option, Object)}</li>
     * </ul>l
     *
     * @param func option function, default {@link Option#EMPTY_FUNC}
     * @return a readonly DataSource
     */
    R readOnlyDataSource(final Function<Option<?>, ?> func);


}
