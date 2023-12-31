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
 * representing Unary SQL Operator
 */
enum UnaryExpOperator implements Operator.SqlUnaryExpOperator {

    NEGATE(" -"),
    BITWISE_NOT(" ~");


    final String spaceOperator;

    UnaryExpOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final String spaceRender(final Database database) {
        final String spaceSign;
        switch (this) {
            case NEGATE:
            case BITWISE_NOT:
                spaceSign = this.spaceOperator;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);
        }
        return spaceSign;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
