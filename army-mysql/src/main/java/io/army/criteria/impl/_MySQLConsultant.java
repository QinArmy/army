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
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.mysql.MySQLLoadData;
import io.army.criteria.mysql.MySQLReplace;
import io.army.criteria.mysql.MySQLSet;
import io.army.criteria.standard.StandardQueries;
import io.army.criteria.standard._SQLConsultant;
import io.army.dialect.Database;

public abstract class _MySQLConsultant extends _SQLConsultant {


    /**
     * <p>
     * Assert insert is MySQL dialect {@link  InsertStatement} statement.
     *
     */
    public static void assertInsert(final InsertStatement insert) {
        if (insert instanceof _Insert._DomainInsert || insert instanceof _Insert._ValuesInsert) {
            if (!(insert instanceof MySQLInserts.MySQLValueSyntaxStatement)) {
                throw nonArmyStatement(insert);
            }
        } else if (insert instanceof _Insert._AssignmentInsert) {
            if (!(insert instanceof InsertSupports.AssignmentInsertStatement)) {
                throw nonArmyStatement(insert);
            }
        } else if (insert instanceof _Insert._QueryInsert) {
            if (!(insert instanceof InsertSupports.QuerySyntaxInsertStatement)) {
                throw nonArmyStatement(insert);
            }
        } else {
            throw nonArmyStatement(insert);
        }

    }


    /**
     * <p>
     * Assert insert is MySQL dialect {@link  InsertStatement} statement.
     *
     */
    public static void assertReplace(final MySQLReplace replace) {
        if (replace instanceof _Insert._DomainInsert) {
            if (!(replace instanceof MySQLReplaces.DomainReplaceStatement)) {
                throw nonArmyStatement(replace);
            }
        } else if (replace instanceof _Insert._ValuesInsert) {
            if (!(replace instanceof MySQLReplaces.ValueReplaceStatement)) {
                throw nonArmyStatement(replace);
            }
        } else if (replace instanceof _Insert._AssignmentInsert) {
            if (!(replace instanceof MySQLReplaces.AssignmentReplaceStatement)) {
                throw nonArmyStatement(replace);
            }
        } else if (replace instanceof _Insert._QueryInsert) {
            if (!(replace instanceof MySQLReplaces.QueryReplaceStatement)) {
                throw nonArmyStatement(replace);
            }
        } else {
            throw nonArmyStatement(replace);
        }

    }


    public static void assertUpdate(final UpdateStatement update) {
        if (update instanceof _SingleUpdate) {
            if (!(update instanceof MySQLSingleUpdates)) {
                throw CriteriaUtils.instanceNotMatch(update, MySQLSingleUpdates.class);
            }
        } else if (!(update instanceof MySQLMultiUpdates)) {
            throw CriteriaUtils.instanceNotMatch(update, MySQLMultiUpdates.class);
        }

    }

    public static void assertDelete(final DeleteStatement delete) {
        if (delete instanceof _SingleDelete) {
            if (!(delete instanceof MySQLSingleDeletes)) {
                throw CriteriaUtils.instanceNotMatch(delete, MySQLSingleDeletes.class);
            }
        } else if (!(delete instanceof MySQLMultiDeletes)) {
            throw CriteriaUtils.instanceNotMatch(delete, MySQLMultiDeletes.class);
        }
    }

    public static void assertQuery(final Query query) {
        if (query instanceof Select) {
            if (!(query instanceof MySQLQueries
                    || query instanceof MySQLQueries.MySQLBracketQuery
                    || query instanceof SimpleQueries.UnionSelect)) {
                throw nonArmyStatement(query);
            }
        } else if (query instanceof SubQuery) {
            if (!(query instanceof MySQLQueries
                    || query instanceof MySQLQueries.MySQLBracketQuery
                    || query instanceof SimpleQueries.UnionSubQuery
                    || query instanceof StandardQueries
                    || query instanceof StandardQueries.StandardBracketQuery)) {
                throw nonArmyStatement(query);
            }
        } else {
            throw nonArmyStatement(query);
        }

    }


    public static void assertValues(final ValuesQuery values) {
        if (!(values instanceof MySQLSimpleValues
                || values instanceof MySQLSimpleValues.MySQLBracketValues
                || values instanceof SimpleValues.UnionValues
                || values instanceof SimpleValues.UnionSubValues)) {
            throw nonArmyStatement(values);
        }
    }

    public static void assertMySQLLoad(final MySQLLoadData load) {
        if (!(load instanceof MySQLLoads.MySQLLoadDataStatement)) {
            throw nonArmyStatement(load);
        }
    }

    public static void assertHint(Hint hint) {
        if (!(hint instanceof MySQLHints)) {
            throw MySQLUtils.illegalHint(hint);
        }
    }

    public static void assertWindow(final Window window) {
        if (!(window instanceof MySQLSupports.MySQLWindowImpl || window instanceof SQLWindow.SimpleWindow)) {
            throw illegalWindow(window);
        }
    }


    public static void assertNestedItems(final _NestedItems nestedItems) {
        if (!(nestedItems instanceof MySQLNestedJoins)) {
            throw illegalNestedItems(nestedItems, Database.MySQL);
        }

    }

    public static void assertSetStmt(final MySQLSet stmt) {
        if (!(stmt instanceof MySQLSets)) {
            throw nonArmyStatement(stmt);
        }
    }



}
