package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

abstract class MySQLUtils extends CriteriaUtils {

    private MySQLUtils() {
    }

    static Dialect defaultDialect(Statement statement) {
        return statement instanceof _MySQLWithClause ? Dialect.MySQL80 : Dialect.MySQL57;
    }

    static void validateDialect(Statement statement, Dialect dialect) {
        if (dialect.database() != Database.MySQL) {
            throw _Exceptions.stmtDontSupportDialect(dialect);
        }
        if (statement instanceof _MySQLWithClause && dialect.version() < Dialect.MySQL80.version()) {
            throw _Exceptions.stmtDontSupportDialect(dialect);
        }
    }


    static boolean isNotUpdateModifier(final MySQLWords modifier) {
        final boolean match;
        switch (modifier) {
            case LOW_PRIORITY:
            case IGNORE:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }

    static boolean isNotDeleteModifier(final MySQLWords modifier) {
        final boolean match;
        switch (modifier) {
            case LOW_PRIORITY:
            case QUICK:
            case IGNORE:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }

    static List<String> asStringList(final @Nullable List<String> partitionList, Supplier<CriteriaException> supplier) {
        if (partitionList == null) {
            throw supplier.get();
        }
        final int size = partitionList.size();
        List<String> list;
        switch (size) {
            case 0:
                throw supplier.get();
            case 1:
                list = Collections.singletonList(partitionList.get(0));
                break;
            default: {
                list = new ArrayList<>(partitionList.size());
                list.addAll(partitionList);
                list = Collections.unmodifiableList(list);
            }

        }
        return list;
    }


    static CriteriaException indexListIsEmpty() {
        return new CriteriaException("index list must not empty.");
    }

    static CriteriaException partitionListIsEmpty() {
        return new CriteriaException("partition list must not empty");
    }

    static CriteriaException lockOfTableAliasListIsEmpty() {
        return new CriteriaException("lock of table alias list must not empty");
    }


    static CriteriaException intoVarListNotEmpty() {
        return new CriteriaException("variable name list must not empty in MySQL INTO clause.");
    }


}
