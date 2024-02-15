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

package io.army.session;

import io.army.criteria.Selection;
import io.army.session.record.ResultStates;

/**
 * <p>This interface representing database cursor.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link StmtCursor},it's produced by statement,army know {@link Selection} list</li>
 *     <li>{@link ProcCursor},it's produced by procedure,army don't know {@link Selection} list</li>
 *     <li>{@code io.army.sync.SyncCursor} blocking cursor</li>
 *     <li>{@code io.army.reactive.ReactiveCursor} reactive cursor</li>
 * </ul>
 *
 * @see ResultStates#valueOf(Option)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 0.6.0
 */
public interface Cursor extends CloseableSpec, OptionSpec {


    /**
     * <p>Get cursor name from DECLARE cursor statement or stored procedure.
     * <p>Postgre example 1 : my_cursor -> my_cursor
     * <p>Postgre example 2 : myCursor -> myCursor
     *
     * @see StmtCursor#safeName()
     */
    String name();


    /**
     * Get The {@link Session} that create this instance.
     *
     * @return The {@link Session} that create this instance.
     */
    Session session();


}
