package io.army.criteria.impl;

import io.army.criteria.RowSet;
import io.army.criteria.Select;
import io.army.criteria.SubValues;
import io.army.criteria.Values;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.postgre.PostgreValues;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Database;

import java.util.function.Supplier;

abstract class PostgreUtils extends CriteriaUtils {

    private PostgreUtils() {
    }


    static int selectModifier(final PostgreSyntax.Modifier modifier) {
        final int level;
        if (modifier == Postgres.ALL
                || modifier == Postgres.DISTINCT) {
            level = 0;
        } else {
            level = -1;
        }
        return level;
    }


    static <S extends RowSet> RowSet primaryRowSetFromParens(CriteriaContext context, Supplier<S> supplier) {
        final RowSet rowSet;
        rowSet = ContextStack.unionQuerySupplier(supplier);
        if (!(rowSet instanceof Select || rowSet instanceof Values)) {
            throw CriteriaUtils.unknownRowSet(context, rowSet, Database.PostgreSQL);
        } else if (!(rowSet instanceof PostgreQuery
                || rowSet instanceof StandardQuery
                || rowSet instanceof SimpleQueries.UnionSelect
                || rowSet instanceof PostgreValues
                || rowSet instanceof SimpleValues.UnionValues)) {
            throw CriteriaUtils.unknownRowSet(context, rowSet, Database.PostgreSQL);
        }
        return rowSet;
    }

    static <S extends RowSet> RowSet subRowSetFromParens(CriteriaContext context, Supplier<S> supplier) {
        final RowSet rowSet;
        rowSet = ContextStack.unionQuerySupplier(supplier);
        if (!(rowSet instanceof SubQuery || rowSet instanceof SubValues)) {
            throw CriteriaUtils.unknownRowSet(context, rowSet, Database.PostgreSQL);
        } else if (!(rowSet instanceof PostgreQuery
                || rowSet instanceof StandardQuery
                || rowSet instanceof SimpleQueries.UnionSubQuery
                || rowSet instanceof PostgreValues
                || rowSet instanceof SimpleValues.UnionSubValues)) {
            throw CriteriaUtils.unknownRowSet(context, rowSet, Database.PostgreSQL);
        }
        return rowSet;
    }


}
