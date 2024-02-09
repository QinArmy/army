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

package io.army.dialect;

import io.army.criteria.SQLParam;
import io.army.stmt.Stmt;

/**
 * <p>
 * Package interface ,This interface representing a statement context,extends {@link _SqlContext}
 * and add method for the implementation of {@link DialectParser}.
 *
 * @since 0.6.0
 */
public interface _StmtContext extends _SqlContext {

    /**
     * for appending updateTime field in set clause.
     *
     * @return true : currently exists {@link  SQLParam} in context
     * @see #appendParam(SQLParam)
     */
    boolean hasParam();

    boolean hasNamedLiteral();

    boolean hasLiteral();

    boolean isUpdateTimeOutputParam();


    boolean hasOptimistic();


    Stmt build();

}
