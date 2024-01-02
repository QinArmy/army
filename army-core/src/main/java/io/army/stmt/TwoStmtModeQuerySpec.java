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

/**
 * <p>This interface representing single sql statement that is a part of two sql statement.
 * <p>This interface is base interface of following :
 *     <ul>
 *         <li>{@link TwoStmtQueryStmt}</li>
 *         <li>{@link TwoStmtBatchQueryStmt}</li>
 *     </ul>
 * * @since 0.6.0
 */
public interface TwoStmtModeQuerySpec extends SingleSqlStmt.IdSelectionIndexSpec {

    int maxColumnSize();


}
