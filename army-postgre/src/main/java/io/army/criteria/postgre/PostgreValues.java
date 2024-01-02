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
import io.army.criteria.Values;
import io.army.criteria.ValuesQuery;

public interface PostgreValues extends PostgreStatement, ValuesQuery {


    interface _UnionSpec<I extends Item> extends _StaticUnionClause<_QueryWithComplexSpec<I>>,
            _StaticIntersectClause<_QueryWithComplexSpec<I>>,
            _StaticExceptClause<_QueryWithComplexSpec<I>> {

    }

    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_AsValuesClause<I>>, _AsValuesClause<I> {

    }

    interface _UnionOffsetSpec<I extends Item> extends _QueryOffsetClause<_UnionFetchSpec<I>>, _UnionFetchSpec<I> {

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

    interface _OffsetSpec<I extends Item> extends _QueryOffsetClause<_FetchSpec<I>>, _FetchSpec<I> {

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


    interface _PostgreValuesLeftParenClause<I extends Item>
            extends Values._StaticValueLeftParenClause<_ValuesLeftParenSpec<I>> {

    }

    interface _ValuesLeftParenSpec<I extends Item> extends _PostgreValuesLeftParenClause<I>, _OrderBySpec<I> {

    }


    interface _PostgreValuesClause<I extends Item>
            extends Values._StaticValuesClause<_PostgreValuesLeftParenClause<I>>,
            Values._DynamicValuesClause<_OrderBySpec<I>> {

    }

    interface _ValuesSpec<I extends Item> extends _PostgreValuesClause<I>,
            _DynamicParensRowSetClause<_WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }

    interface _WithSpec<I extends Item> extends _PostgreDynamicWithClause<_ValuesSpec<I>>,
            PostgreQuery._PostgreStaticWithClause<_ValuesSpec<I>>,
            _ValuesSpec<I> {

    }

    interface _ValuesDynamicCteAsClause extends _PostgreDynamicCteAsClause<_WithSpec<_CommaClause<PostgreCtes>>,
            _CommaClause<PostgreCtes>> {

    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_ValuesDynamicCteAsClause>, _ValuesDynamicCteAsClause {

    }


    interface _QueryComplexSpec<I extends Item> extends PostgreQuery._PostgreSelectClause<I>,
            _PostgreValuesClause<I>,
            _DynamicParensRowSetClause<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _QueryComplexSpec<I>,
            _PostgreDynamicWithClause<_QueryComplexSpec<I>>,
            PostgreQuery._PostgreStaticWithClause<_QueryComplexSpec<I>> {

    }


}
