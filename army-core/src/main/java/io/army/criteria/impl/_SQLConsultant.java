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

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._NestedItems;
import io.army.dialect.Database;
import io.army.util.ClassUtils;

import javax.annotation.Nullable;

public abstract class _SQLConsultant {
    // consultant

    _SQLConsultant() {
        throw new UnsupportedOperationException();
    }


    public static void assertStandardQuery(final Query query) {
        if (!(query instanceof StandardQueries
                || query instanceof StandardQueries.StandardBracketQuery
                || query instanceof SimpleQueries.UnionSelect
                || query instanceof SimpleQueries.UnionSubQuery)) {
            throw nonArmyStatement(query);
        }
    }

    public static void assertUnionRowSet(final RowSet rowSet) {
        if (!(rowSet instanceof OrderByClause.UnionRowSet)) {
            throw nonArmyStatement(rowSet);
        }
    }


    public static void assertStandardUpdate(UpdateStatement update) {
        if (!(update instanceof StandardUpdates)) {
            throw nonArmyStatement(update);
        }

    }

    public static void assertStandardDelete(DeleteStatement delete) {
        if (!(delete instanceof StandardDeletes)) {
            throw nonArmyStatement(delete);
        }
    }

    public static void assertStandardInsert(final InsertStatement insert) {
        if (insert instanceof _Insert._DomainInsert) {
            if (!(insert instanceof StandardInserts.DomainsInsertStatement)) {
                throw instanceNotMatch(insert, StandardInserts.DomainsInsertStatement.class);
            }
        } else if (insert instanceof _Insert._ValuesInsert) {
            if (!(insert instanceof StandardInserts.ValueInsertStatement)) {
                throw instanceNotMatch(insert, StandardInserts.ValueInsertStatement.class);
            }
        } else if (insert instanceof _Insert._QueryInsert) {
            if (!(insert instanceof StandardInserts.QueryInsertStatement)) {
                throw instanceNotMatch(insert, StandardInserts.QueryInsertStatement.class);
            }
        } else {
            throw new CriteriaException("Not standard insert statement");
        }

    }

    public static void assertStandardNestedItems(@Nullable _NestedItems nestedItems) {
        if (!(nestedItems instanceof StandardNestedJoins)) {
            throw illegalNestedItems(nestedItems, null);
        }
    }

    protected static CriteriaException instanceNotMatch(Statement statement, Class<?> statementClass) {
        String m = String.format("%s isn't instance of %s"
                , ClassUtils.safeClassName(statement), statementClass.getName());
        throw new CriteriaException(m);
    }


    static CriteriaException illegalNestedItems(@Nullable _NestedItems nestedItem, @Nullable Database database) {
        String m = String.format("Illegal %s %s for %s"
                , _NestedItems.class.getName()
                , ClassUtils.safeClassName(nestedItem)
                , database == null ? "standard" : database);
        throw new CriteriaException(m);
    }

    static CriteriaException illegalCteImpl(@Nullable _Cte cte) {
        return new CriteriaException(String.format("Illegal Cte %s", cte));
    }

    static CriteriaException nonArmyStatement(Statement statement) {
        String m = String.format("%s isn't army implementation", ClassUtils.safeClassName(statement));
        return new CriteriaException(m);
    }

    static CriteriaException illegalWindow(@Nullable Window window) {
        String m = String.format("Illegal window[%s]", ClassUtils.safeClassName(window));
        return new CriteriaException(m);
    }

    static CriteriaException illegalSqlElement(@Nullable SQLElement element) {
        String m = String.format("Illegal SQLElement[%s]", ClassUtils.safeClassName(element));
        return new CriteriaException(m);
    }


}
