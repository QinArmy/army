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

package io.army.criteria.mysql;


import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.MySQLs;

import javax.annotation.Nullable;

/**
 * <p>This interface representing MySQL SET statement.
 * <p>More document see {@link MySQLs#setStmt()}.
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">SET Syntax for Variable Assignment</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/server-system-variables.html">Server System Variables</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/user-variables.html">User-Defined Variables</a>
 * @since 0.6.6
 */
public interface MySQLSet extends MySQLStatement {


    interface _SetCommaClause extends _AsCommandClause<DmlCommand> {


        _SetCommaClause comma(MySQLs.VarScope scope, String name, @Nullable Object value);

        _SetCommaClause comma(MySQLs.VarScope scope1, String name1, @Nullable Object value1, MySQLs.VarScope scope2, String name2, @Nullable Object value2);

    }


    interface _SetClause {

        _SetCommaClause set(MySQLs.VarScope scope, String name, @Nullable Object value);


        _SetCommaClause set(MySQLs.VarScope scope1, String name1, @Nullable Object value1, MySQLs.VarScope scope2, String name2, @Nullable Object value2);


    }


}
