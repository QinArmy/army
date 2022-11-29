package io.army.criteria.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;

import java.util.function.Consumer;
import java.util.function.Function;

public interface MySQLStatement extends DialectStatement {

    interface _MySQLDynamicWithClause<WE> extends _DynamicWithClause<MySQLCtes, WE> {

    }


    interface _MySQLFromClause<FT, FS> extends _FromModifierTabularClause<FT, FS> {

    }


    interface _PartitionClause_0<PR> {

        _LeftParenStringQuadraOptionalSpec<PR> partition();

    }

    interface _PartitionClause<R> {

        R partition(String first, String... rest);

        R partition(Consumer<Consumer<String>> consumer);

        R ifPartition(Consumer<Consumer<String>> consumer);

    }

    interface _PartitionAsClause<R> extends _PartitionClause<_AsClause<R>> {

    }

    interface _PartitionAndAsClause_0<AR> extends _PartitionClause_0<_AsClause<AR>> {

    }


    interface _MySQLJoinNestedClause<JN> extends _JoinNestedClause<JN>, _StraightJoinNestedClause<JN> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *         <li>{@link _JoinClause }</li>
     *         <li>{@link  _StraightJoinClause}</li>
     *     </ul>
     * </p>
     *
     * @param <JT> next clause java type
     * @param <JS> next clause java type
     * @since 1.0
     */
    interface _MySQLJoinClause<JT, JS> extends Statement._JoinModifierTabularClause<JT, JS>,
            DialectStatement._StraightJoinModifierTabularClause<JT, JS> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *         <li>{@link _JoinCteClause }</li>
     *         <li>{@link  _StraightJoinCteClause}</li>
     *     </ul>
     * </p>
     */
    interface _MySQLJoinCteClause<JC> extends _JoinCteClause<JC>, _StraightJoinCteClause<JC> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *         <li>{@link _DialectJoinClause }</li>
     *         <li>{@link  _DialectStraightJoinClause}</li>
     *     </ul>
     * </p>
     *
     * @param <JP> next clause java type
     * @since 1.0
     */
    interface _MySQLDialectJoinClause<JP> extends DialectStatement._DialectJoinClause<JP>,
            DialectStatement._DialectStraightJoinClause<JP> {

    }

    @Deprecated
    interface _MySQLCrossJoinClause<FT, FS> extends Statement._CrossJoinModifierTabularClause<FT, FS>,
            DialectStatement._CrossJoinCteClause<FS> {
    }

    interface _MySQLDynamicJoinClause<JD> extends _DynamicJoinClause<MySQLJoins, JD>,
            _DynamicStraightJoinClause<MySQLJoins, JD> {

    }

    interface _MySQLDynamicCrossJoinClause<JD> extends _DynamicCrossJoinClause<MySQLCrosses, JD> {

    }


    interface _IndexForJoinSpec<R> extends _ParensStringClause<R> {

        _ParensStringClause<R> forJoin();

    }

    interface _IndexForOrderBySpec<R> extends _ParensStringClause<R> {

        _ParensStringClause<R> forOrderBy();

    }


    interface _IndexHintClause<R> {

        _ParensStringClause<R> useIndex();

        _ParensStringClause<R> ignoreIndex();

        _ParensStringClause<R> forceIndex();
    }


    interface _IndexHintForJoinClause<RR> extends _IndexHintClause<RR> {

        @Override
        _IndexForJoinSpec<RR> useIndex();

        @Override
        _IndexForJoinSpec<RR> ignoreIndex();

        @Override
        _IndexForJoinSpec<RR> forceIndex();

    }


    interface _IndexHintForOrderByClause<RR> extends _IndexHintClause<RR> {

        @Override
        _IndexForOrderBySpec<RR> useIndex();

        @Override
        _IndexForOrderBySpec<RR> ignoreIndex();

        @Override
        _IndexForOrderBySpec<RR> forceIndex();

    }


    interface _IndexPurposeBySpec<R> extends _IndexForJoinSpec<R>, _IndexForOrderBySpec<R> {

        _ParensStringClause<R> forGroupBy();

    }


    interface _QueryIndexHintClause<R> extends _IndexHintForJoinClause<R>, _IndexHintForOrderByClause<R> {

        @Override
        _IndexPurposeBySpec<R> useIndex();

        @Override
        _IndexPurposeBySpec<R> ignoreIndex();

        @Override
        _IndexPurposeBySpec<R> forceIndex();

    }


    interface _MySQLNestedJoinClause<I extends Item>
            extends _MySQLJoinClause<_NestedIndexHintOnSpec<I>, _DerivedAsClause<_NestedOnSpec<I>>>,
            _CrossJoinModifierTabularClause<_NestedIndexHintCrossSpec<I>, _DerivedAsClause<_NestedJoinSpec<I>>>,
            _MySQLJoinCteClause<_NestedOnSpec<I>>,
            _CrossJoinCteClause<_NestedJoinSpec<I>>,
            _MySQLJoinNestedClause<_NestedLeftParenSpec<_NestedOnSpec<I>>>,
            _CrossJoinNestedClause<_NestedLeftParenSpec<_NestedJoinSpec<I>>>,
            _MySQLDynamicJoinClause<_NestedJoinSpec<I>>,
            _MySQLDynamicCrossJoinClause<_NestedJoinSpec<I>>,
            _MySQLDialectJoinClause<_NestedPartitionOnSpec<I>>,
            _DialectCrossJoinClause<_NestedPartitionCrossSpec<I>> {

    }


    interface _NestedJoinSpec<I extends Item> extends _MySQLNestedJoinClause<I>
            , _RightParenClause<I> {

    }

    interface _NestedOnSpec<I extends Item> extends _OnClause<_NestedJoinSpec<I>>, _NestedJoinSpec<I> {

    }

    interface _NestedIndexHintOnSpec<I extends Item> extends _QueryIndexHintClause<_NestedIndexHintOnSpec<I>>
            , _NestedOnSpec<I> {

    }

    interface _NestedPartitionOnSpec<I extends Item> extends _PartitionAsClause<_NestedIndexHintOnSpec<I>> {

    }

    interface _NestedIndexHintCrossSpec<I extends Item> extends _QueryIndexHintClause<_NestedIndexHintCrossSpec<I>>
            , _NestedJoinSpec<I> {

    }

    interface _NestedPartitionCrossSpec<I extends Item> extends _PartitionAsClause<_NestedIndexHintCrossSpec<I>> {

    }

    interface _NestedIndexHintJoinSpec<I extends Item> extends _QueryIndexHintClause<_NestedIndexHintJoinSpec<I>>
            , _MySQLNestedJoinClause<I> {

    }

    interface _NestedPartitionJoinSpec<I extends Item> extends _PartitionAsClause<_NestedIndexHintJoinSpec<I>> {

    }


    interface _NestedLeftParenSpec<I extends Item>
            extends _NestedLeftParenModifierTabularClause<_NestedIndexHintJoinSpec<I>, _DerivedAsClause<_MySQLNestedJoinClause<I>>>,
            _LeftParenCteClause<_MySQLNestedJoinClause<I>>,
            _NestedDialectLeftParenClause<_NestedPartitionJoinSpec<I>>,
            _LeftParenClause<_NestedLeftParenSpec<_MySQLNestedJoinClause<I>>> {

    }

    interface _DynamicIndexHintOnClause extends _QueryIndexHintClause<_DynamicIndexHintOnClause>,
            _OnClause<_DynamicJoinSpec> {

    }

    interface _DynamicPartitionOnClause extends _PartitionAsClause<_DynamicIndexHintOnClause> {

    }


    interface _DynamicJoinSpec
            extends _MySQLJoinClause<_DynamicIndexHintOnClause, _DerivedAsClause<_OnClause<_DynamicJoinSpec>>>,
            _CrossJoinModifierTabularClause<_DynamicIndexHintJoinClause, _DerivedAsClause<_DynamicJoinSpec>>,
            _JoinCteClause<_OnClause<_DynamicJoinSpec>>,
            _StraightJoinCteClause<_OnClause<_DynamicJoinSpec>>,
            _CrossJoinCteClause<_DynamicJoinSpec>,
            _MySQLJoinNestedClause<_NestedLeftParenSpec<_OnClause<_DynamicJoinSpec>>>,
            _CrossJoinNestedClause<_NestedLeftParenSpec<_DynamicJoinSpec>>,
            _MySQLDynamicJoinClause<_DynamicJoinSpec>,
            _MySQLDynamicCrossJoinClause<_DynamicJoinSpec>,
            _MySQLDialectJoinClause<_DynamicPartitionOnClause>,
            _DialectCrossJoinClause<_DynamicPartitionJoinClause> {

    }

    interface _DynamicIndexHintJoinClause extends _QueryIndexHintClause<_DynamicIndexHintJoinClause>, _DynamicJoinSpec {

    }

    interface _DynamicPartitionJoinClause extends _PartitionAsClause<_DynamicIndexHintJoinClause> {

    }


    interface _StaticCteAsClause<I extends Item> {
        I as(Function<MySQLQuery._SelectSpec<_AsCteClause<I>>, I> function);
    }


    interface _StaticCteParensSpec<I extends Item>
            extends _ParensStringClause<_StaticCteAsClause<I>>, _StaticCteAsClause<I> {

    }


    interface _MultiStmtSemicolonSpec extends _MultiStmtSpec {

        void semicolon();

    }


}
