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

package io.army.executor;


import io.army.session.DataAccessException;
import io.army.session.OptionSpec;

/**
 * <p>This interface representing executor or {@link io.army.stmt.Stmt}.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code io.army.sync.executor.SyncStmtExecutor}</li>
 *     <li>{@code io.army.reactive.executor.ReactiveStmtExecutor}</li>
 * </ul>
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link io.army.session.CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * @see ExecutorFactory
 * @since 0.6.0
 */
public interface StmtExecutor extends OptionSpec, DriverSpiHolder {


    /**
     * <p>
     * Session identifier(non-unique, for example : database server cluster),probably is following :
     *     <ul>
     *         <li>server process id</li>
     *         <li>server thread id</li>
     *         <li>other identifier</li>
     *     </ul>
     *     <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     * <br/>
     *
     * @return session identifier
     * @throws DataAccessException throw when underlying database session have closed.
     */
    long sessionIdentifier() throws DataAccessException;


    /**
     * @return true : underlying database session in transaction block.
     * @throws DataAccessException throw when underlying database session have closed.
     */
    boolean inTransaction() throws DataAccessException;

    boolean isSameFactory(StmtExecutor s);

    /**
     * override {@link Object#toString()}
     *
     * @return driver info, contain : <ol>
     * <li>implementation class name</li>
     * <li>session name</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();

}
