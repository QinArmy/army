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

package io.army.criteria.postgre;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.Values;
import io.army.criteria.ValuesQuery;


/**
 * @see <a href="https://www.postgresql.org/docs/current/sql-values.html">VALUES Statement</a>
 */
public interface PostgreValues extends PostgreStatement, ValuesQuery {


    interface _UnionSpec<I extends Item> extends _StaticUnionClause<_QueryComplexSpec<I>>,
            _StaticIntersectClause<_QueryComplexSpec<I>>,
            _StaticExceptClause<_QueryComplexSpec<I>> {

    }

    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_AsValuesClause<I>>, _AsValuesClause<I> {

    }

    interface _UnionOffsetSpec<I extends Item> extends _QueryFetchOffsetClause<_UnionFetchSpec<I>>, _UnionFetchSpec<I> {

    }


    interface _UnionLimitSpec<I extends Item> extends _RowCountLimitAllClause<_UnionOffsetSpec<I>>,
            _UnionOffsetSpec<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_UnionOrderByCommaSpec<I>>,
            _UnionLimitSpec<I> {

    }


    interface _UnionOrderBySpec<I extends Item> extends _StaticOrderByClause<_UnionOrderByCommaSpec<I>>,
            _DynamicOrderByClause<_UnionLimitSpec<I>>,
            _UnionLimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _FetchSpec<I extends Item> extends _QueryFetchClause<_AsValuesClause<I>>, _AsValuesClause<I> {

    }

    interface _OffsetSpec<I extends Item> extends _QueryFetchOffsetClause<_FetchSpec<I>>, _FetchSpec<I> {

    }

    interface _LimitSpec<I extends Item> extends _RowCountLimitAllClause<_OffsetSpec<I>>, _OffsetSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

    }


    interface _OrderBySpec<I extends Item> extends _StaticOrderByClause<_OrderByCommaSpec<I>>,
            _DynamicOrderByClause<_LimitSpec<I>>,
            _LimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _StaticValuesRowClause<I extends Item> extends Values._ValuesRowParensClause<_StaticValuesRowCommaSpec<I>> {

    }


    interface _StaticValuesRowCommaSpec<I extends Item> extends Statement._CommaClause<_StaticValuesRowClause<I>>,
            _OrderBySpec<I> {

    }


    interface _PostgreValuesClause<I extends Item>
            extends Values._StaticValuesClause<_StaticValuesRowClause<I>>,
            Values._DynamicValuesRowParensClause<_OrderBySpec<I>> {

    }


    interface ValuesSpec<I extends Item> extends _PostgreValuesClause<I>,
            _DynamicParensRowSetClause<ValuesSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    interface _QueryComplexSpec<I extends Item> extends PostgreQuery._PostgreSelectClause<I>,
            _PostgreValuesClause<I>,
            _DynamicParensRowSetClause<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _QueryComplexSpec<I>,
            _PostgreDynamicWithClause<PostgreQuery._PostgreSelectClause<I>>,
            PostgreQuery._PostgreStaticWithClause<PostgreQuery._PostgreSelectClause<I>> {

    }


}
