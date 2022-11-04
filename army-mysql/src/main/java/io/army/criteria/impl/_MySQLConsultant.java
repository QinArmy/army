package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.mysql.MySQLLoadData;
import io.army.criteria.mysql.MySQLReplace;
import io.army.dialect.Database;
import io.army.util._ClassUtils;

public abstract class _MySQLConsultant extends _SQLConsultant {


    /**
     * <p>
     * Assert insert is MySQL dialect {@link  Insert} statement.
     * </p>
     */
    public static void assertInsert(final Insert insert) {
        if (insert instanceof _Insert._DomainInsert || insert instanceof _Insert._ValuesInsert) {
            if (!(insert instanceof MySQLInserts.MySQLValueSyntaxStatement)) {
                throw instanceNotMatch(insert, MySQLInserts.MySQLValueSyntaxStatement.class);
            }
        } else if (insert instanceof _Insert._AssignmentInsert) {
            if (!(insert instanceof MySQLInserts.AssignmentsInsertStatement)) {
                throw instanceNotMatch(insert, MySQLInserts.AssignmentsInsertStatement.class);
            }
        } else if (insert instanceof _Insert._QueryInsert) {
            if (!(insert instanceof MySQLInserts.MySQLQueryInsertStatement)) {
                throw instanceNotMatch(insert, MySQLInserts.MySQLQueryInsertStatement.class);
            }
        } else {
            throw new CriteriaException("Not MySQL dialect insert statement.");
        }

    }


    /**
     * <p>
     * Assert insert is MySQL dialect {@link  Insert} statement.
     * </p>
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
            if (!(replace instanceof MySQLReplaces.PrimaryAssignmentReplaceStatement)) {
                throw nonArmyStatement(replace);
            }
        } else if (replace instanceof _Insert._QueryInsert) {
            if (!(replace instanceof MySQLReplaces.PrimaryQueryReplaceStatement)) {
                throw nonArmyStatement(replace);
            }
        } else {
            throw nonArmyStatement(replace);
        }

    }


    public static void assertUpdate(final Update update) {
        if (update instanceof _SingleUpdate) {
            if (!(update instanceof MySQLSingleUpdate)) {
                throw instanceNotMatch(update, MySQLSingleUpdate.class);
            }
        } else if (!(update instanceof MySQLMultiUpdate)) {
            throw instanceNotMatch(update, MySQLMultiUpdate.class);
        }

    }

    public static void assertDelete(final Delete delete) {
        if (delete instanceof _SingleDelete) {
            if (!(delete instanceof MySQLSingleDelete)) {
                throw instanceNotMatch(delete, MySQLSingleDelete.class);
            }
        } else if (!(delete instanceof MySQLMultiDelete)) {
            throw instanceNotMatch(delete, MySQLMultiDelete.class);
        }
    }

    public static void assertQuery(final Query query) {
        if (!(query instanceof MySQLQueries
                || query instanceof MySQLQueries.MySQLBracketQuery
                || query instanceof SimpleQueries.UnionSelect
                || query instanceof SimpleQueries.UnionSubQuery)) {
            throw nonArmyStatement(query);
        }

    }


    public static void assertValues(final RowSet.DqlValues values) {
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

    public static void assertWindow(Window window) {
        if (!WindowClause.isStandardWindow(window)) {
            throw new CriteriaException("Illegal window.");
        }
    }

    public static void assertMySQLCte(final _Cte cte) {
        if (!(cte instanceof SQLs.CteImpl)) {
            throw illegalCteImpl(cte);
        }
        final SubStatement subStatement;
        subStatement = cte.subStatement();
        if (!(subStatement instanceof MySQLQueries
                || subStatement instanceof MySQLQueries.MySQLBracketQuery
                || subStatement instanceof SimpleQueries.UnionSubQuery)) {
            throw illegalCteImpl(cte);
        }

    }


    public static void assertNestedItems(final NestedItems nestedItems) {
        if (!(nestedItems instanceof MySQLNestedJoins)) {
            throw illegalNestedItems(nestedItems, Database.MySQL);
        }

    }

    public static void assertJsonTable(final TabularItem jsonTable) {
        if (!(jsonTable instanceof MySQLFunctions.JsonTable)) {
            String m = String.format("%s isn't instance of %s", _ClassUtils.safeClassName(jsonTable)
                    , MySQLFunctions.JsonTable.class);
            throw new CriteriaException(m);
        }

    }


}
