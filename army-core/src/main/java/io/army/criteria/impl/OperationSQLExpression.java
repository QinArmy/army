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


import io.army.criteria.CompoundPredicate;
import io.army.criteria.SQLColumnSet;
import io.army.criteria.SubQuery;
import io.army.criteria.standard.SQLs;

/**
 * <p>
 * This class is a abstract implementation of {@link io.army.criteria.SQLExpression}. This class is base class of :
 * <ul>
 *     <li>{@link OperationExpression}</li>
 *     <li>{@link OperationRowExpression}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public abstract class OperationSQLExpression implements ArmySQLExpression {

    /**
     * package constructor
     */
    protected OperationSQLExpression() {
    }

    @Override
    public final CompoundPredicate equalAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, SQLs.ANY, subQuery);
    }

    @Override
    public final CompoundPredicate equalSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, SQLs.SOME, subQuery);
    }

    @Override
    public final CompoundPredicate equalAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, SQLs.ALL, subQuery);
    }

    @Override
    public final CompoundPredicate notEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, SQLs.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate notEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, SQLs.SOME, subQuery);
    }

    @Override
    public final CompoundPredicate notEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, SQLs.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate lessAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, SQLs.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate lessSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, SQLs.SOME, subQuery);
    }

    @Override
    public final CompoundPredicate lessAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, SQLs.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, SQLs.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, SQLs.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, SQLs.ALL, subQuery);
    }

    @Override
    public final CompoundPredicate greaterAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, SQLs.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate greaterSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, SQLs.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate greaterAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, SQLs.ALL, subQuery);
    }

    @Override
    public final CompoundPredicate greaterEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, SQLs.ANY, subQuery);
    }

    @Override
    public final CompoundPredicate greaterEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, SQLs.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate greaterEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, SQLs.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate in(SQLColumnSet row) {
        return Expressions.inPredicate(this, false, row);
    }

    @Override
    public final CompoundPredicate notIn(SQLColumnSet row) {
        return Expressions.inPredicate(this, true, row);
    }


}
