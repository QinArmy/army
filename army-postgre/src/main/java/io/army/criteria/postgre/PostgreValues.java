package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.Postgres;
import io.army.lang.Nullable;

import java.util.function.Function;

public interface PostgreValues extends PostgreStatement, ValuesQuery {

    interface _ValuesStaticOrderByCommaClause<OR> extends _StaticOrderByCommaClause<OR> {
        //TODO add using operator
    }

    interface _ValuesStaticOrderByClause<OR> extends _StaticOrderByClause<OR> {
        //TODO add using operator
    }


    interface _UnionSpec<I extends Item> extends Query._QueryUnionClause<_QueryWithComplexSpec<I>>,
            Query._QueryIntersectClause<_QueryWithComplexSpec<I>>,
            Query._QueryExceptClause<_QueryWithComplexSpec<I>> {

    }

    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_AsValuesClause<I>>, _AsValuesClause<I> {

    }

    interface _UnionOffsetSpec<I extends Item> extends _QueryOffsetClause<_UnionFetchSpec<I>>, _UnionFetchSpec<I> {

    }


    interface _UnionLimitSpec<I extends Item> extends _RowCountLimitAllClause<_UnionOffsetSpec<I>>,
            _UnionOffsetSpec<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item>
            extends _ValuesStaticOrderByCommaClause<_UnionOrderByCommaSpec<I>>, _UnionLimitSpec<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _ValuesStaticOrderByClause<_UnionOrderByCommaSpec<I>>,
            _UnionLimitSpec<I>, _UnionSpec<I> {

    }


    interface _FetchSpec<I extends Item> extends _QueryFetchClause<_AsValuesClause<I>>, _AsValuesClause<I> {

    }

    interface _OffsetSpec<I extends Item> extends _QueryOffsetClause<_FetchSpec<I>>, _FetchSpec<I> {

    }

    interface _LimitSpec<I extends Item> extends _RowCountLimitAllClause<_OffsetSpec<I>>, _OffsetSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item> extends _ValuesStaticOrderByCommaClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

    }


    interface _OrderBySpec<I extends Item> extends _ValuesStaticOrderByClause<_OrderByCommaSpec<I>>,
            _DynamicOrderByClause<SortItems, _LimitSpec<I>>, _LimitSpec<I>, _UnionSpec<I> {

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
            _LeftParenClause<_ValuesSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _WithSpec<I extends Item> extends _PostgreDynamicWithClause<_ValuesSpec<I>>,
            PostgreQuery._PostgreStaticWithClause<_ValuesSpec<I>>,
            _ValuesSpec<I> {

    }

    interface _DynamicCteAsClause {

        PostgreCtes as(Function<_WithSpec<PostgreCtes>, PostgreCtes> function);

        PostgreCtes as(@Nullable Postgres.WordMaterialized modifier,
                       Function<_WithSpec<PostgreCtes>, PostgreCtes> function);

    }

    interface _DynamicCteParensSpec extends _ParensStringClause<_DynamicCteAsClause>, _DynamicCteAsClause {

    }


    interface _QueryComplexSpec<I extends Item> extends PostgreQuery._PostgreSelectClause<I>,
            _PostgreValuesClause<I>,
            _LeftParenClause<_QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _QueryComplexSpec<I>,
            _PostgreDynamicWithClause<_QueryComplexSpec<I>>,
            PostgreQuery._PostgreStaticWithClause<_QueryComplexSpec<I>>,
            Query._DynamicParensRowSetClause<_UnionOrderBySpec<I>> {

    }


}
