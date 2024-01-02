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

package io.army.criteria.dialect;


import io.army.criteria.DialectStatement;
import io.army.criteria.InsertStatement;
import io.army.criteria.Statement;
import io.army.criteria.SubStatement;

/**
 * @see InsertStatement
 * @see ReturningInsert
 * @see SubInsert
 * @since 0.6.0
 */
@Deprecated
public interface SubReturningInsert extends DialectStatement, Statement.DqlInsert, SubStatement {

    @Deprecated
    interface _SubReturningInsertSpec extends _DqlInsertClause<SubReturningInsert> {

    }


}
