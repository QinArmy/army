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

import io.army.criteria.SqlField;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.util._Exceptions;

public enum AssignOperator {

    PLUS_EQUAL(" +="),
    MINUS_EQUAL(" -="),

    TIMES_EQUAL(" *="),
    DIVIDE_EQUAL(" /="),

    MODE_EQUAL(" %=");

    final String spaceSign;

    AssignOperator(String spaceSign) {
        this.spaceSign = spaceSign;
    }


    public final void appendAppropriateExpressionOperator(final StringBuilder sqlBuilder) {
        switch (this) {
            case PLUS_EQUAL:
                sqlBuilder.append(DualExpOperator.PLUS.spaceOperator);
                break;
            case MINUS_EQUAL:
                sqlBuilder.append(DualExpOperator.MINUS.spaceOperator);
                break;
            case TIMES_EQUAL:
                sqlBuilder.append(DualExpOperator.TIMES.spaceOperator);
                break;
            case DIVIDE_EQUAL:
                sqlBuilder.append(DualExpOperator.DIVIDE.spaceOperator);
                break;
            case MODE_EQUAL:
                sqlBuilder.append(DualExpOperator.MOD.spaceOperator);
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);
        }
    }


    public final void appendOperator(final SqlField field, final StringBuilder sqlBuilder, final _SqlContext context) {
        switch (context.database()) {
            case MySQL:
            case PostgreSQL:
            case SQLite: {
                sqlBuilder.append(_Constant.SPACE_EQUAL);
                ((_SelfDescribed) field).appendSql(sqlBuilder, context);
                appendAppropriateExpressionOperator(sqlBuilder);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(context.database());
        }

    }


}
