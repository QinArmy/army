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

import io.army.criteria.Selection;

import java.util.List;


/**
 * <p>This interface representing  single sql statement.
 * <p>This interface is base interface of following :
 *     <ul>
 *         <li>{@link SimpleStmt}</li>
 *         <li>{@link BatchStmt}</li>
 *     </ul>
 * * @see PairStmt
 *
 * @since 0.6.0
 */
public interface SingleSqlStmt extends Stmt {

    String sqlText();

    /**
     * @return a unmodifiable list
     */
    List<? extends Selection> selectionList();


    interface IdSelectionIndexSpec {

        /**
         * @see GeneratedKeyStmt#idSelectionIndex()
         * @see TwoStmtQueryStmt#idSelectionIndex()
         */
        int idSelectionIndex();
    }


}
