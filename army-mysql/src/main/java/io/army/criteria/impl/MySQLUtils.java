package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Query;
import io.army.criteria.SubQuery;
import io.army.criteria.TabularItem;
import io.army.criteria.mysql.MySQLCastType;
import io.army.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

abstract class MySQLUtils extends CriteriaUtils {

    private MySQLUtils() {
    }


    static void assertItemWord(CriteriaContext criteriaContext, @Nullable ItemWord itemWord, TabularItem tableItem) {
        if (itemWord == ItemWord.LATERAL && !(tableItem instanceof SubQuery)) {
            String m = "MySQL LATERAL support only %s" + SubQuery.class.getName();
            throw ContextStack.criteriaError(criteriaContext, m);
        } else if (itemWord != null) {
            throw ContextStack.castCriteriaApi(criteriaContext);
        }

    }

    @Deprecated
    static List<String> asStringList(final @Nullable List<String> partitionList, Supplier<CriteriaException> supplier) {
        if (partitionList == null) {
            throw ContextStack.criteriaError(supplier);
        }
        final int size = partitionList.size();
        List<String> list;
        switch (size) {
            case 0:
                throw ContextStack.criteriaError(supplier);
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

    static boolean isSingleParamType(MySQLCastType type) {
        final boolean match;
        switch (type) {
            case BINARY:
            case CHAR:
            case NCHAR:
            case TIME:
            case DATETIME:
            case DECIMAL:
            case FLOAT:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    static int selectModifier(final MySQLSyntax.Modifier modifier) {
        final int level;
        if (modifier == MySQLs.ALL
                || modifier == MySQLs.DISTINCT
                || modifier == MySQLs.DISTINCTROW) {
            level = 1;
        } else if (modifier == MySQLs.HIGH_PRIORITY) {
            level = 2;
        } else if (modifier == MySQLs.STRAIGHT_JOIN) {
            level = 3;
        } else if (modifier == MySQLs.SQL_SMALL_RESULT) {
            level = 4;
        } else if (modifier == MySQLs.SQL_BIG_RESULT) {
            level = 5;
        } else if (modifier == MySQLs.SQL_BUFFER_RESULT) {
            level = 6;
        } else if (modifier == MySQLs.SQL_NO_CACHE) {
            level = 7;
        } else if (modifier == MySQLs.SQL_CALC_FOUND_ROWS) {
            level = 8;
        } else {
            level = -1;
        }
        return level;
    }

    static int insertModifier(final MySQLSyntax.Modifier modifier) {
        final int level;
        if (modifier == MySQLs.LOW_PRIORITY
                || modifier == MySQLs.DELAYED
                || modifier == MySQLs.HIGH_PRIORITY) {
            level = 1;
        } else if (modifier == MySQLs.IGNORE) {
            level = 2;
        } else {
            level = -1;
        }
        return level;
    }

    static int replaceModifier(final MySQLs.Modifier modifier) {
        final int level;
        if (modifier == MySQLs.LOW_PRIORITY
                || modifier == MySQLs.DELAYED) {
            level = 1;
        } else {
            level = -1;
        }
        return level;
    }

    static int updateModifier(final MySQLSyntax.Modifier modifier) {
        final int level;
        if (modifier == MySQLs.LOW_PRIORITY) {
            level = 1;
        } else if (modifier == MySQLs.IGNORE) {
            level = 2;
        } else {
            level = -1;
        }
        return level;
    }

    static int deleteModifier(final MySQLSyntax.Modifier modifier) {
        final int level;
        if (modifier == MySQLs.LOW_PRIORITY) {
            level = 1;
        } else if (modifier == MySQLs.QUICK) {
            level = 2;
        } else if (modifier == MySQLs.IGNORE) {
            level = 3;
        } else {
            level = -1;
        }
        return level;
    }

    static int loadDataModifier(final MySQLSyntax.Modifier modifier) {
        final int level;
        if (modifier == MySQLs.LOW_PRIORITY
                || modifier == MySQLs.CONCURRENT) {
            level = 1;
        } else if (modifier == MySQLs.LOCAL) {
            level = 2;
        } else {
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

    static CriteriaException dontSupportTabularModifier(CriteriaContext context, Query.TabularModifier modifier) {
        String m = String.format("MySQL don't support modifier[%s]", modifier);
        return ContextStack.criteriaError(context, m);
    }


}
