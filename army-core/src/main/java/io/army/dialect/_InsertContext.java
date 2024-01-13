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

import io.army.criteria.LiteralMode;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;

import javax.annotation.Nullable;

public interface _InsertContext extends _DmlContext, _SetClauseContext {

    TableMeta<?> insertTable();

    LiteralMode literalMode();

    @Nullable
    String tableAlias();

    @Nullable
    String safeTableAlias();

    @Nullable
    String safeTableName();

    /**
     * For conflict clause
     */
    String safeTableAliasOrSafeTableName();

    @Nullable
    String rowAlias();

    @Nullable
    String safeRowAlias();

    boolean hasConditionPredicate();


    void appendConditionPredicate(boolean firstPredicate);

    @Override
    _InsertContext parentContext();

    /**
     * just for {@link #appendField(FieldMeta)} in on conflict clause.
     *
     * @param output default false
     */
    void outputFieldTableAlias(boolean output);

    @Override
    SimpleStmt build();

    interface _ColumnListSpec {

        void appendFieldList();

    }

    interface _ReturningIdSpec {
        void appendReturnIdIfNeed();

    }

    interface _AssignmentsSpec extends _ReturningIdSpec {

        void appendAssignmentClause();
    }


    interface _ValueSyntaxSpec extends _ColumnListSpec, _ReturningIdSpec {

        void appendValueList();

    }

    interface _QuerySyntaxSpec extends _ColumnListSpec {

        void appendSubQuery();
    }


}
