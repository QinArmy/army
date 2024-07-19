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

import io.army.criteria.Selection;
import io.army.option.Option;

import java.util.List;

/**
 * <p>This interface representing the database cursor that is produced by {@link io.army.criteria.Statement},army know the {@link Selection} list.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code  io.army.sync.SyncStmtCursor}</li>
 *     <li>{@code io.army.reactive.ReactiveStmtCursor}</li>
 * </ul>
 *
 * @see ResultStates#valueOf(Option)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 0.6.0
 */
public interface StmtCursor extends Cursor {

    /**
     * <p>Get safe cursor name.
     * <p>Postgre example 1 : my_cursor -> my_cursor
     * <p>Postgre example 2 : myCursor -> "myCursor"
     *
     * @see #name()
     */
    String safeName();


    List<? extends Selection> selectionList();

    Selection selection(int indexBasedZero);

    Selection selection(String name);


}
