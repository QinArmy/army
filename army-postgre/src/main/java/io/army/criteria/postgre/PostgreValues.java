package io.army.criteria.postgre;

import io.army.criteria.*;

public interface PostgreValues extends PostgreStatement, RowSet.DqlValues {

    interface _ValuesStaticOrderByCommaClause<OR> extends _StaticOrderByCommaClause<OR> {
        //TODO add using operator
    }

    interface _ValuesStaticOrderByClause<OR> extends _StaticOrderByClause<OR> {
        //TODO add using operator
    }


    interface _UnionSpec<I extends Item> extends Query._QueryUnionClause<_UnionAndQuerySpec<I>>
            , Query._QueryIntersectClause<_UnionAndQuerySpec<I>>
            , Query._QueryExceptClause<_UnionAndQuerySpec<I>> {

    }


    interface _UnionLimitSpec<I extends Item> extends _AsValuesClause<I> {

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


    interface _PostgreStaticRowLeftParenClause<I extends Item>
            extends Values._StaticValueLeftParenClause<_StaticRowLeftParenSpec<I>> {

    }

    interface _StaticRowLeftParenSpec<I extends Item> extends _PostgreStaticRowLeftParenClause<I>
            , _OrderBySpec<I> {

    }


    interface _PostgreValuesClause<I extends Item>
            extends Values._StaticValuesClause<_PostgreStaticRowLeftParenClause<I>>
            , Values._DynamicValuesClause<_OrderBySpec<I>> {

    }


    interface _MinWithSpec<I extends Item> extends _PostgreDynamicWithClause<_PostgreValuesClause<I>>
            , _PostgreValuesClause<I> {

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
            extends _SimpleCteLeftParenSpec<_DynamicSubMaterializedSpec<_AsCteClause<PostgreCteBuilder>>> {

    }


    interface _UnionAndQuerySpec<I extends Item> extends _WithSpec<I>
            , Query._LeftParenClause<_UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }


}
