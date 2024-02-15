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

package io.army.sync;

import io.army.criteria.Selection;
import io.army.session.Cursor;
import io.army.session.DataAccessException;
import io.army.session.Direction;
import io.army.session.Option;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultItem;
import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * <p>This interface representing blocking {@link Cursor}.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link SyncStmtCursor},it's produced by statement,army know {@link Selection} list</li>
 *     <li>{@link SyncProcCursor},it's produced by procedure,army don't know {@link Selection} list</li>
 * </ul>
 *
 * @see ResultStates#valueOf(Option)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 0.6.0
 */
public interface SyncCursor extends Cursor, AutoCloseable {


    @Override
    SyncSession session();


    @Nullable
    <R> R nextRecord(Function<CurrentRecord, R> function);

    @Nullable
    <R> R fetchOneRecord(Direction direction, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchRecord(Direction direction, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchRecord(Direction direction, long count, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    Stream<ResultItem> fetchRecord(Direction direction);

    Stream<ResultItem> fetchRecord(Direction direction, long count);

    ResultStates move(Direction direction);

    ResultStates move(Direction direction, long count);


    @Override
    void close() throws DataAccessException;
}
