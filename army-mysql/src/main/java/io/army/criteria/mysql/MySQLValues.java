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

import io.army.criteria.Item;
import io.army.criteria.RowSet;
import io.army.criteria.Statement;
import io.army.criteria.Values;
import io.army.criteria.impl.MySQLs;

/**
 * <p>This interface representing MySQL Values statement,this interface is base interface of below:
 * <ul>
 *     <li>MySQL {@link  io.army.criteria.Values}</li>
 *     <li>MySQL {@link io.army.criteria.SubValues}</li>
 * </ul>
 *
 * @see MySQLs#valuesStmt()
 * @see MySQLs#subValues()
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/values.html">VALUES Statement</a>
 * @since 0.6.0
 */
public interface MySQLValues extends MySQLStatement, RowSet {


    interface _UnionSpec<I extends Item>
            extends _StaticUnionClause<_ValueWithComplexSpec<I>>, _AsValuesClause<I> {

    }

    interface _UnionLimitSpec<I extends Item> extends Statement._RowCountLimitClause<_AsValuesClause<I>>,
            _AsValuesClause<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_UnionOrderByCommaSpec<I>>,
            _UnionLimitSpec<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _StaticOrderByClause<_UnionOrderByCommaSpec<I>>,
            _DynamicOrderByClause<_UnionLimitSpec<I>>,
            _UnionLimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _LimitSpec<I extends Item> extends Statement._RowCountLimitClause<_AsValuesClause<I>>,
            _AsValuesClause<I> {
    }

    interface _OrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_OrderByCommaSpec<I>>,
            _LimitSpec<I> {

    }

    interface _OrderBySpec<I extends Item> extends _StaticOrderByClause<_OrderByCommaSpec<I>>,
            _DynamicOrderByClause<_LimitSpec<I>>,
            _LimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _StaticValuesRowClause<I extends Item> extends Values._ValuesRowClause<_StaticValuesRowCommaSpec<I>> {

    }


    interface _StaticValuesRowCommaSpec<I extends Item> extends Statement._CommaClause<_StaticValuesRowClause<I>>,
            _OrderBySpec<I> {

    }

    interface _MySQLValuesClause<I extends Item> extends Values._StaticValuesClause<_StaticValuesRowClause<I>>,
            Values._DynamicValuesRowClause<_OrderBySpec<I>> {

    }


    interface _ValueSpec<I extends Item> extends _MySQLValuesClause<I>,
            _DynamicParensRowSetClause<_ValueSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    /**
     * <p>VALUES statement don't support WITH clause.
     */
    interface _SelectComplexCommandSpec<I extends Item> extends MySQLQuery._MySQLSelectClause<I>,
            _DynamicParensRowSetClause<_ValueWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    interface _ValueWithComplexSpec<I extends Item>
            extends _MySQLDynamicWithClause<_SelectComplexCommandSpec<I>>,
            _MySQLStaticWithClause<_SelectComplexCommandSpec<I>>,
            _SelectComplexCommandSpec<I>,
            _MySQLValuesClause<I> {

    }


}
