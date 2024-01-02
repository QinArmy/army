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

import io.army.criteria.SqlField;
import io.army.meta.SingleTableMeta;

public interface _MultiUpdateContext extends _UpdateContext, _MultiTableStmtContext, _SetClauseContext {


    /**
     * <p>
     *     <ul>
     *         <li>dataField table is {@link  SingleTableMeta},return table alias</li>
     *         <li>dataField table is {@link  io.army.meta.ChildTableMeta} return {@link  io.army.meta.ParentTableMeta} alias</li>
     *     </ul>
     *
     */
    String singleTableAliasOf(SqlField dataField);


}
