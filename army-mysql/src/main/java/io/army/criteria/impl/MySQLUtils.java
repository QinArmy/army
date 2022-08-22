package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.SubQuery;
import io.army.criteria.TableItem;
import io.army.criteria.mysql.MySQLModifier;
import io.army.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

abstract class MySQLUtils extends CriteriaUtils {

    private MySQLUtils() {
    }


    static void assertItemWord(CriteriaContext criteriaContext, @Nullable ItemWord itemWord, TableItem tableItem) {
        if (itemWord == ItemWord.LATERAL && !(tableItem instanceof SubQuery)) {
            String m = "MySQL LATERAL support only %s" + SubQuery.class.getName();
            throw CriteriaContextStack.criteriaError(criteriaContext, m);
        } else if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(criteriaContext);
        }

    }

    @Deprecated
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

    static int selectModifier(final MySQLModifier modifier) {
        final int level;
        switch (modifier) {
            case ALL:
            case DISTINCT:
            case DISTINCTROW:
                level = 1;
                break;
            case HIGH_PRIORITY:
                level = 2;
                break;
            case STRAIGHT_JOIN:
                level = 3;
                break;
            case SQL_SMALL_RESULT:
                level = 4;
                break;
            case SQL_BIG_RESULT:
                level = 5;
                break;
            case SQL_BUFFER_RESULT:
                level = 6;
                break;
            case SQL_NO_CACHE:
                level = 7;
                break;
            case SQL_CALC_FOUND_ROWS:
                level = 8;
                break;
            default:
                level = -1;
        }
        return level;
    }

    static int insertModifier(final MySQLModifier modifier) {
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

    static int queryInsertModifier(final MySQLModifier modifier) {
        final int level;
        switch (modifier) {
            case LOW_PRIORITY:
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


    static int replaceModifier(final MySQLModifier modifier) {
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

    static int updateModifier(final MySQLModifier modifier) {
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

    static int deleteModifier(final MySQLModifier modifier) {
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

    static int loadDataModifier(final MySQLModifier modifier) {
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
