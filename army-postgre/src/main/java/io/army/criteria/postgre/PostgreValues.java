package io.army.criteria.postgre;

import io.army.criteria.*;

public interface PostgreValues extends PostgreStatement, RowSet.DqlValues {

    interface _ValuesStaticOrderByCommaClause<OR> extends _StaticOrderByCommaClause<OR> {
        //TODO add using operator
    }

    interface _ValuesStaticOrderByClause<OR> extends _StaticOrderByClause<OR> {
        //TODO add using operator
    }


    interface _UnionSpec<I extends Item> extends Query._QueryUnionClause<_QueryWithComplexSpec<I>>
            , Query._QueryIntersectClause<_QueryWithComplexSpec<I>>
            , Query._QueryExceptClause<_QueryWithComplexSpec<I>> {

    }

    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_AsValuesClause<I>>, _AsValuesClause<I> {

    }

    interface _UnionOffsetSpec<I extends Item> extends _QueryOffsetClause<_UnionFetchSpec<I>>, _UnionFetchSpec<I> {

    }


    interface _UnionLimitSpec<I extends Item> extends _RowCountLimitAllClause<_UnionOffsetSpec<I>>
            , _UnionOffsetSpec<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item>
            extends _ValuesStaticOrderByCommaClause<_UnionOrderByCommaSpec<I>>
            , _UnionLimitSpec<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _ValuesStaticOrderByClause<_UnionOrderByCommaSpec<I>>
            , _UnionLimitSpec<I>, _UnionSpec<I> {

    }


    interface _FetchSpec<I extends Item> extends _QueryFetchClause<_AsValuesClause<I>>, _AsValuesClause<I> {

    }

    interface _OffsetSpec<I extends Item> extends _QueryOffsetClause<_FetchSpec<I>>, _FetchSpec<I> {

    }

    interface _LimitSpec<I extends Item> extends _RowCountLimitAllClause<_OffsetSpec<I>>, _OffsetSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item> extends _ValuesStaticOrderByCommaClause<_OrderByCommaSpec<I>>
            , _LimitSpec<I> {

    }


    interface _OrderBySpec<I extends Item> extends _ValuesStaticOrderByClause<_OrderByCommaSpec<I>>
            , _DynamicOrderByClause<SortItems, _LimitSpec<I>>
            , _LimitSpec<I>, _UnionSpec<I> {

    }


    interface _PostgreValuesLeftParenClause<I extends Item>
            extends Values._StaticValueLeftParenClause<_ValuesLeftParenSpec<I>> {

    }

    interface _ValuesLeftParenSpec<I extends Item> extends _PostgreValuesLeftParenClause<I>
            , _OrderBySpec<I> {

    }


    interface _PostgreValuesClause<I extends Item>
            extends Values._StaticValuesClause<_PostgreValuesLeftParenClause<I>>
            , Values._DynamicValuesClause<_OrderBySpec<I>> {

    }

    interface ValuesSpec<I extends Item> extends _PostgreValuesClause<I>
            , _LeftParenClause<ValuesSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }


    interface _MinWithSpec<I extends Item> extends _PostgreDynamicWithClause<ValuesSpec<I>>
            , ValuesSpec<I> {

    }


    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _PostgreValuesClause<I> {

    }

    interface _WithSpec<I extends Item> extends _MinWithSpec<I>
            , _StaticWithClause<_StaticCteLeftParenSpec<_CteComma<I>>> {

    }

    interface _DynamicSubMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_MinWithSpec<I>>
            , _MinWithSpec<I> {

    }

    interface _DynamicCteValuesSpec
            extends _SimpleCteLeftParenSpec<_DynamicSubMaterializedSpec<_AsCteClause<PostgreCtes>>> {

    }


    interface _QueryComplexSpec<I extends Item> extends PostgreQuery._PostgreSelectClause<I>
            , _PostgreValuesClause<I>
            , _LeftParenClause<_QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _QueryComplexSpec<I>
            , _PostgreDynamicWithClause<_QueryComplexSpec<I>> {

    }


}
