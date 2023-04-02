package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._ReturningDml;

public abstract class _PostgreConsultant extends _SQLConsultant {


    private _PostgreConsultant() {
    }


    public static void assertInsert(final InsertStatement insert) {
        if (insert instanceof _Insert._DomainInsert || insert instanceof _Insert._ValuesInsert) {
            if (!(insert instanceof PostgreInserts.PostgreValueSyntaxInsertStatement)) {
                throw nonArmyStatement(insert);
            }
        } else if (insert instanceof _Insert._QueryInsert) {
            if (!(insert instanceof PostgreInserts.PostgreQueryInsertStatement)) {
                throw nonArmyStatement(insert);
            }
        } else {
            throw nonArmyStatement(insert);
        }
    }

    public static void assertUpdate(final UpdateStatement update) {
        if (update instanceof _ReturningDml) {
            if (!(update instanceof PostgreUpdates.PostgreReturningUpdateWrapper)) {
                throw nonArmyStatement(update);
            }
        } else if (!(update instanceof PostgreUpdates)) {
            throw nonArmyStatement(update);
        }

    }

    public static void assertDelete(final DeleteStatement stmt) {
        if (stmt instanceof _ReturningDml) {
            if (!(stmt instanceof PostgreDeletes.PostgreReturningDeleteWrapper)) {
                throw nonArmyStatement(stmt);
            }
        } else if (!(stmt instanceof PostgreDeletes)) {
            throw nonArmyStatement(stmt);
        }
    }


    public static void assertRowSet(final RowSet rowSet) {
        if (!(rowSet instanceof Query)) {
            if (!(rowSet instanceof PostgreSimpleValues
                    || rowSet instanceof PostgreSimpleValues.PostgreBracketValues
                    || rowSet instanceof SimpleValues.UnionValues
                    || rowSet instanceof SimpleValues.UnionSubValues)) {
                throw nonArmyStatement(rowSet);
            }
        } else if (rowSet instanceof Select) {
            if (!(rowSet instanceof PostgreQueries
                    || rowSet instanceof PostgreQueries.PostgreBracketQuery
                    || rowSet instanceof SimpleQueries.UnionSelect)) {
                throw nonArmyStatement(rowSet);
            }
        } else if (rowSet instanceof SubQuery) {
            if (!(rowSet instanceof PostgreQueries
                    || rowSet instanceof PostgreQueries.PostgreBracketQuery
                    || rowSet instanceof SimpleQueries.UnionSubQuery
                    || rowSet instanceof StandardQueries
                    || rowSet instanceof StandardQueries.StandardBracketQuery)) {
                throw nonArmyStatement(rowSet);
            }
        } else {
            throw nonArmyStatement(rowSet);
        }
    }


}
