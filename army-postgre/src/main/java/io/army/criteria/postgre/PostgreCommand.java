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

package io.army.criteria.postgre;

import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.SQLs;

import java.util.function.Consumer;

public interface PostgreCommand extends PostgreStatement {


    interface _SetClause {

        _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.SymbolEqual equal, Object value);

        _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.SymbolEqual equal, Object value1, Object value2);

        _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.SymbolEqual equal, Object value1, Object value2, Object value3);

        _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.SymbolEqual equal, Object value1, Object value2, Object value3, Object value4);

        _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.WordTo to, Consumer<Consumer<Object>> consumer);

    }

}
