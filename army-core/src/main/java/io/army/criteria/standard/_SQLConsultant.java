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

package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._NestedItems;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLType;

import javax.annotation.Nullable;

public abstract class _SQLConsultant {
    // consultant

    _SQLConsultant() {
        throw new UnsupportedOperationException();
    }


    public static void assertExpression(final Expression expression) {
        if (!(expression instanceof ArmyExpression)) {
            throw new CriteriaException("Illegal Expression");
        }
    }


    public static void assertStandardQuery(final Query query) {
        final boolean match;
        if (query instanceof BatchSelect) {
            match = query instanceof StandardQueries.StandardBatchSimpleSelect
                    || query instanceof SimpleQueries.UnionBatchSelect
                    || query instanceof StandardQueries.StandardBatchBracketSelect;
        } else {
            match = query instanceof StandardQueries
                    || query instanceof StandardQueries.StandardBracketQuery
                    || query instanceof SimpleQueries.UnionSelect
                    || query instanceof SimpleQueries.UnionSubQuery;
        }

        if (!match) {
            throw CriteriaUtils.nonArmyStatement(query);
        }
    }

    public static boolean isStandardRightQuery(final RowSet rowSet) {
        return rowSet instanceof StandardQueries
                || rowSet instanceof StandardQueries.StandardBracketQuery;
    }

    public static void assertStandardCte(_Cte cte) {
        if (cte instanceof CriteriaContexts.RecursiveCte) {
            return;
        }
        if (!(cte instanceof CteImpl)) {
            throw CriteriaUtils.illegalCteImpl(cte);
        }
    }

    public static void assertUnionRowSet(final RowSet rowSet) {
        if (!(rowSet instanceof OrderByClause.UnionRowSet)) {
            throw CriteriaUtils.nonArmyStatement(rowSet);
        }
    }


    public static void assertStandardUpdate(UpdateStatement update) {
        if (!(update instanceof StandardUpdates)) {
            throw CriteriaUtils.nonArmyStatement(update);
        }

    }

    public static void assertStandardDelete(DeleteStatement delete) {
        if (!(delete instanceof StandardDeletes)) {
            throw CriteriaUtils.nonArmyStatement(delete);
        }
    }

    public static void assertStandardInsert(final InsertStatement insert) {
        if (insert instanceof _Insert._DomainInsert) {
            if (!(insert instanceof StandardInserts.DomainsInsertStatement)) {
                throw CriteriaUtils.instanceNotMatch(insert, StandardInserts.DomainsInsertStatement.class);
            }
        } else if (insert instanceof _Insert._ValuesInsert) {
            if (!(insert instanceof StandardInserts.ValueInsertStatement)) {
                throw CriteriaUtils.instanceNotMatch(insert, StandardInserts.ValueInsertStatement.class);
            }
        } else if (insert instanceof _Insert._QueryInsert) {
            if (!(insert instanceof StandardInserts.QueryInsertStatement)) {
                throw CriteriaUtils.instanceNotMatch(insert, StandardInserts.QueryInsertStatement.class);
            }
        } else {
            throw new CriteriaException("Not standard insert statement");
        }

    }

    public static void assertStandardWindow(final Window window) {
        if (!(window instanceof StandardQueries.StandardWindow || window instanceof SQLWindow.SimpleWindow)) {
            throw CriteriaUtils.illegalWindow(window);
        }
    }

    public static void assertStandardNestedItems(@Nullable _NestedItems nestedItems) {
        if (!(nestedItems instanceof StandardNestedJoins)) {
            throw CriteriaUtils.illegalNestedItems(nestedItems, null);
        }
    }

    public static TypeDef._TypeDefCharacterSetSpec precision(final DataType dataType, final boolean textType,
                                                             final long precision, final long maxValue) {
        return TypeDefs.precision(dataType, textType, precision, maxValue);
    }

    public static TypeDef precisionAndScale(final DataType dataType, final int precision, int scale, int maxPrecision, int maxScale) {
        return TypeDefs.precisionAndScale(dataType, precision, scale, maxPrecision, maxScale);
    }


    public static CriteriaException dontSupportPrecision(SQLType type) {
        return ContextStack.clearStackAndCriteriaError(String.format("%s don't support precision", type));
    }

    public static CriteriaException dontSupportPrecisionAndScale(SQLType type) {
        return ContextStack.clearStackAndCriteriaError(String.format("%s don't support precision and scale", type));
    }


    /*-------------------below protected methods -------------------*/


}
