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


import io.army.criteria.Expression;
import io.army.executor.DataAccessException;
import io.army.stmt.Stmt;

/**
 * Throw when satisfy following all conditions :
 * <ul>
 *     <li>domain contain version field</li>
 *     <li>{@link io.army.criteria.Statement} WHERE clause contain version predicate with {@link io.army.criteria.TableField#equal(Expression)}</li>
 *     <li>update affectedRows is zero</li>
 * </ul>
 *
 * @see Stmt#hasOptimistic()
 * @since 0.6.0
 */
public final class OptimisticLockException extends DataAccessException {


    public OptimisticLockException(String message) {
        super(message);
    }

}
