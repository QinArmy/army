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
import io.army.result.Direction;
import io.army.option.Option;
import io.army.result.StmtCursor;
import io.army.result.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking {@link StmtCursor} that is produced by {@link io.army.criteria.Statement},army know the {@link Selection} list.
 *
 * @see ResultStates#valueOf(Option)
 * @see SyncStmtCursor#SYNC_STMT_CURSOR
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 0.6.0
 */
public interface SyncStmtCursor extends StmtCursor, SyncCursor {

    /**
     * <p>When this option is supported by {@link ResultStates},this option representing the statement that produce
     * the {@link ResultStates} declare a cursor. For example : execute postgre DECLARE command.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
     */
    Option<SyncStmtCursor> SYNC_STMT_CURSOR = Option.from("SYNC STMT CURSOR", SyncStmtCursor.class);


    @Nullable
    <R> R next(Class<R> resultClass);

    @Nullable
    <R> R nextObject(Supplier<R> constructor);

    @Nullable
    <R> R fetchOne(Direction direction, Class<R> resultClass, Consumer<ResultStates> consumer);

    @Nullable
    <R> R fetchOneObject(Direction direction, Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Stream<R> fetch(Direction direction, Class<R> resultClass, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchObject(Direction direction, Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Stream<R> fetch(Direction direction, long count, Class<R> resultClass, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchObject(Direction direction, long count, Supplier<R> constructor, Consumer<ResultStates> consumer);


}
