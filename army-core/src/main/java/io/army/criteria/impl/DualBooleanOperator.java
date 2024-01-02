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


import io.army.dialect.Database;
import io.army.util._Exceptions;

/**
 * Interface representing sql dual operator.
 */
enum DualBooleanOperator implements Operator.SqlDualBooleanOperator {


    EQUAL(" ="),
    NOT_EQUAL(" !="),

    /**
     * MySQL
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/comparison-operators.html#operator_equal-to">NULL-safe equal.</a>
     */
    NULL_SAFE_EQUAL(" <=>"),

    LESS(" <"),
    LESS_EQUAL(" <="),
    GREATER_EQUAL(" >="),
    GREATER(" >"),


    LIKE(" LIKE"),
    NOT_LIKE(" NOT LIKE"),
    SIMILAR_TO(" SIMILAR TO"), // currently,postgre only
    NOT_SIMILAR_TO(" NOT SIMILAR TO"); // currently,postgre only


    final String spaceOperator;

    /**
     * @param spaceOperator space and sign
     */
    DualBooleanOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final String spaceRender(final Database database) {
        if (this != NULL_SAFE_EQUAL) {
            return this.spaceOperator;
        }
        final String operator;
        switch (database) {
            case MySQL:
                operator = " <=>";
                break;
            case PostgreSQL:
            case H2:
                operator = " IS NOT DISTINCT FROM";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return operator;
    }



    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
