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





    static List<String> asStringList(final @Nullable List<String> partitionList, Supplier<CriteriaException> supplier) {
        if (partitionList == null) {
            throw CriteriaContextStack.criteriaError(supplier);
        }
        final int size = partitionList.size();
        List<String> list;
        switch (size) {
            case 0:
                throw CriteriaContextStack.criteriaError(supplier);
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

    static int insertModifier(final MySQLWords modifier) {
        final int level;
        switch (modifier) {
            case LOW_PRIORITY:
            case DELAYED:
            case HIGH_PRIORITY:
                level = 1;
                break;
            case IGNORE:
                level = 2;
                break;
            default:
                level = -1;
        }
        return level;
    }

    static int replaceModifier(final MySQLWords modifier) {
        final int level;
        switch (modifier) {
            case LOW_PRIORITY:
            case DELAYED:
                level = 1;
                break;
            default:
                level = -1;
        }
        return level;
    }

    static int updateModifier(final MySQLWords modifier) {
        final int level;
        switch (modifier) {
            case LOW_PRIORITY:
                level = 1;
                break;
            case IGNORE:
                level = 2;
                break;
            default:
                level = -1;
        }
        return level;
    }

    static int deleteModifier(final MySQLWords modifier) {
        final int level;
        switch (modifier) {
            case LOW_PRIORITY:
                level = 1;
                break;
            case QUICK:
                level = 2;
                break;
            case IGNORE:
                level = 3;
                break;
            default:
                level = -1;
        }
        return level;
    }

    static int loadDataModifier(final MySQLWords modifier) {
        final int level;
        switch (modifier) {
            case LOW_PRIORITY:
            case CONCURRENT:
                level = 1;
                break;
            case LOCAL:
                level = 2;
                break;
            default:
                level = -1;
        }
        return level;
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
