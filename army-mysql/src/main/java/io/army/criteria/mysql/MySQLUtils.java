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

package io.army.criteria.mysql;

import io.army.criteria.CriteriaException;
import io.army.criteria.impl.ContextStack;
import io.army.criteria.impl.CriteriaContext;
import io.army.criteria.impl.CriteriaUtils;
import io.army.dialect.MySQLDialect;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

abstract class MySQLUtils extends CriteriaUtils {

    private MySQLUtils() {
    }

    /**
     * reference last dialect
     */
    static final MySQLDialect DIALECT = MySQLDialect.MySQL80;


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

    static int selectModifier(final MySQLs.Modifier modifier) {
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

    static int insertModifier(final MySQLs.Modifier modifier) {
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

    static int updateModifier(final MySQLs.Modifier modifier) {
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

    static int deleteModifier(final MySQLs.Modifier modifier) {
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

    static int loadDataModifier(final MySQLs.Modifier modifier) {
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

    static void assertUserVar(final @Nullable String name) {
        if (name == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (name.length() == 0) {
            throw ContextStack.clearStackAnd(_Exceptions::varNameNoText);
        } else if (name.charAt(0) == '@') {
            throw userVariableFirstCharIsAt(name);
        }
    }


    @Deprecated
    static CriteriaException partitionListIsEmpty(CriteriaContext context) {
        return ContextStack.criteriaError(context, "you don't add any partition");
    }

    static CriteriaException partitionListIsEmpty() {
        return ContextStack.clearStackAndCriteriaError("you don't add any partition");
    }

    static CriteriaException userVariableFirstCharIsAt(String varName) {
        return ContextStack.clearStackAnd(_Exceptions::userVariableFirstCharIsAt, varName);
    }


}
