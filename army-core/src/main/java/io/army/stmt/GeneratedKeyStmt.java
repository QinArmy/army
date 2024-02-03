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

package io.army.stmt;

import io.army.meta.PrimaryFieldMeta;

/**
 * <p>
 * This interface representing a insert statement that has auto increment id.
*/
public interface GeneratedKeyStmt extends SimpleStmt, SingleSqlStmt.IdSelectionIndexSpec {

    int rowSize();

    void setGeneratedIdValue(int indexBasedZero, Object idValue);

    PrimaryFieldMeta<?> idField();

    /**
     * @return <ul>
     * <li>If {@link #selectionList()} is empty, negative</li>
     * <li>Else index</li>
     * </ul>
     */
    int idSelectionIndex();

    boolean hasConflictClause();

    boolean isArmyAppendReturning();


}
