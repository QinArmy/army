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
import io.army.session.Option;
import io.army.session.ProcCursor;
import io.army.session.record.ResultStates;


/**
 * <p>This interface representing blocking {@link ProcCursor} that is produced by store procedure,army don't know the {@link Selection} list.
 *
 * @see ResultStates#valueOf(Option)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 0.6.0
 */
public interface SyncProcCursor extends ProcCursor, SyncCursor {

    /**
     * <p>
     * When this option is supported by {@link ResultStates},this option representing the statement that produce
     * the {@link ResultStates} declare a cursor. For example : CALL procedure
     * <br/>
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
     */
    Option<SyncProcCursor> SYNC_PROC_CURSOR = Option.from("SYNC PROC CURSOR", SyncProcCursor.class);


}
