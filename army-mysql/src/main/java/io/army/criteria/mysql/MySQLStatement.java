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

import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.meta.TableMeta;

import java.util.function.Consumer;

public interface MySQLStatement extends DialectStatement {

    interface _MySQLDynamicWithClause<WE extends Item> extends _DynamicWithClause<MySQLCtes, WE> {

    }

    interface _MySQLStaticWithClause<I extends Item> extends _StaticWithClause<MySQLQuery._StaticCteParensSpec<I>> {

    }

    interface _MySQLUsingTableClause<R extends Item> {

        R using(TableMeta<?> table);
    }

    interface _MySQLFromClause<FT, FS> extends _FromModifierTabularClause<FT, _AsClause<FS>> {

    }

    interface _MySQLFromNestedClause<R extends Item> extends _FromNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    interface _MySQLUsingNestedClause<R extends Item> extends _UsingNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    interface _MySQLDynamicNestedClause<R extends Item>
            extends _DynamicTabularNestedClause<_NestedLeftParenSpec<R>, R> {

    }


    @Deprecated
    interface _PartitionClause_0<PR> {

        _LeftParenStringQuadraOptionalSpec<PR> partition();

    }

    interface _PartitionClause<R> extends Item {

        R partition(String first, String... rest);

        R partition(Consumer<Consumer<String>> consumer);

        R ifPartition(Consumer<Consumer<String>> consumer);

    }

    interface _PartitionAsClause<R> extends _PartitionClause<_AsClause<R>> {

    }

    @Deprecated
    interface _PartitionAndAsClause_0<AR> extends _PartitionClause_0<_AsClause<AR>> {

    }


    /**
     * @param <R> the java type that {@link _RightParenClause#rightParen()} return type
     */
    interface _MySQLJoinNestedClause<R extends Item> extends _JoinNestedClause<_NestedLeftParenSpec<R>, R>,
            _StraightJoinNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    /**
     * @param <R> the java type that {@link _RightParenClause#rightParen()} return type
     */
    interface _MySQLCrossNestedClause<R extends Item> extends _CrossJoinNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *         <li>{@link _JoinClause }</li>
     *         <li>{@link  _StraightJoinClause}</li>
     *     </ul>
     *
     *
     * @param <JT> next clause java type
     * @param <JS> next clause java type
     * @since 0.6.0
     */
    interface _MySQLJoinClause<JT, JS> extends _JoinModifierTabularClause<JT, JS>,
            _StraightJoinModifierTabularClause<JT, JS> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *         <li>{@link _JoinCteClause }</li>
     *         <li>{@link  _StraightJoinCteClause}</li>
     *     </ul>
     *
     */
    interface _MySQLJoinCteClause<JC> extends _JoinCteClause<JC>, _StraightJoinCteClause<JC> {

    }

    interface _MySQLCrossClause<FT, FS> extends _CrossJoinModifierTabularClause<FT, _AsClause<FS>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     * <ul>
     *     <li>{@link _DialectJoinClause }</li>
     *     <li>{@link  _DialectStraightJoinClause}</li>
     * </ul>
     *
     * @param <JP> next clause java type
     * @since 0.6.0
     */
    interface _MySQLDialectJoinClause<JP> extends DialectStatement._DialectJoinClause<JP>,
            DialectStatement._DialectStraightJoinClause<JP> {

    }

    @Deprecated
    interface _MySQLCrossJoinClause<FT, FS> extends Statement._CrossJoinModifierTabularClause<FT, FS>,
            DialectStatement._CrossJoinCteClause<FS> {
    }

    interface _MySQLDynamicJoinCrossClause<JD> extends _DynamicJoinClause<MySQLJoins, JD>,
            _DynamicStraightJoinClause<MySQLJoins, JD>,
            _DynamicCrossJoinClause<MySQLCrosses, JD> {

    }

    @Deprecated
    interface _MySQLDynamicCrossJoinClause<JD> extends _DynamicCrossJoinClause<MySQLCrosses, JD> {

    }


    interface _IndexForJoinSpec<R> extends _ParensStringClause<R> {

        _ParensStringClause<R> forJoin();

    }

    interface _IndexForOrderBySpec<R> extends _ParensStringClause<R> {

        _ParensStringClause<R> forOrderBy();

    }


    interface _StaticIndexHintClause<R> extends Item {

        _ParensStringClause<R> useIndex();

        _ParensStringClause<R> ignoreIndex();

        _ParensStringClause<R> forceIndex();


    }

    interface _DynamicIndexHintClause<T extends Item, R extends Item> extends Item {

        R ifUseIndex(Consumer<T> consumer);

        R ifIgnoreIndex(Consumer<T> consumer);

        R ifForceIndex(Consumer<T> consumer);

    }


    interface _IndexHintForJoinClause<R> extends _StaticIndexHintClause<R> {

        @Override
        _IndexForJoinSpec<R> useIndex();

        @Override
        _IndexForJoinSpec<R> ignoreIndex();

        @Override
        _IndexForJoinSpec<R> forceIndex();

    }


    interface _IndexHintForOrderByClause<R> extends _StaticIndexHintClause<R> {

        @Override
        _IndexForOrderBySpec<R> useIndex();

        @Override
        _IndexForOrderBySpec<R> ignoreIndex();

        @Override
        _IndexForOrderBySpec<R> forceIndex();

    }


    interface _IndexPurposeBySpec<R> extends _IndexForJoinSpec<R>, _IndexForOrderBySpec<R> {

        _ParensStringClause<R> forGroupBy();

    }


    interface _QueryIndexHintSpec<R extends Item> extends _IndexHintForJoinClause<R>, _IndexHintForOrderByClause<R> {

        @Override
        _IndexPurposeBySpec<R> useIndex();

        @Override
        _IndexPurposeBySpec<R> ignoreIndex();

        @Override
        _IndexPurposeBySpec<R> forceIndex();

    }


    interface _MySQLNestedJoinClause<I extends Item>
            extends _MySQLJoinClause<_NestedIndexHintOnSpec<I>, _AsClause<_NestedParenOnSpec<I>>>,
            _MySQLCrossClause<_NestedIndexHintCrossSpec<I>, _NestedParenCrossSpec<I>>,
            _MySQLJoinCteClause<_NestedOnSpec<I>>,
            _CrossJoinCteClause<_NestedJoinSpec<I>>,
            _MySQLJoinNestedClause<_NestedOnSpec<I>>,
            _MySQLCrossNestedClause<_NestedJoinSpec<I>>,
            _MySQLDynamicJoinCrossClause<_NestedJoinSpec<I>>,
            _MySQLDialectJoinClause<_NestedPartitionOnSpec<I>>,
            _DialectCrossJoinClause<_NestedPartitionCrossSpec<I>> {

    }


    interface _NestedJoinSpec<I extends Item> extends _MySQLNestedJoinClause<I>, _RightParenClause<I> {

    }


    interface _NestedOnSpec<I extends Item> extends _OnClause<_NestedJoinSpec<I>>, _NestedJoinSpec<I> {

    }


    interface _NestedLeftParensJoinSpec<I extends Item> extends _OptionalParensStringClause<_MySQLNestedJoinClause<I>>,
            _MySQLNestedJoinClause<I> {

    }

    interface _NestedParenCrossSpec<I extends Item> extends _OptionalParensStringClause<_NestedJoinSpec<I>>,
            _NestedJoinSpec<I> {

    }

    interface _NestedParenOnSpec<I extends Item> extends _OptionalParensStringClause<_NestedOnSpec<I>>, _NestedOnSpec<I> {


    }

    interface _NestedIndexHintOnSpec<I extends Item> extends _QueryIndexHintSpec<_NestedIndexHintOnSpec<I>>,
            _DynamicIndexHintClause<_IndexPurposeBySpec<Object>, _NestedIndexHintOnSpec<I>>,
            _NestedOnSpec<I> {

    }

    interface _NestedPartitionOnSpec<I extends Item> extends _PartitionAsClause<_NestedIndexHintOnSpec<I>> {

    }

    interface _NestedIndexHintCrossSpec<I extends Item> extends _QueryIndexHintSpec<_NestedIndexHintCrossSpec<I>>,
            _NestedJoinSpec<I> {

    }

    interface _NestedPartitionCrossSpec<I extends Item> extends _PartitionAsClause<_NestedIndexHintCrossSpec<I>> {

    }

    interface _NestedIndexHintJoinSpec<I extends Item> extends _QueryIndexHintSpec<_NestedIndexHintJoinSpec<I>>
            , _MySQLNestedJoinClause<I> {

    }

    interface _NestedPartitionJoinSpec<I extends Item> extends _PartitionAsClause<_NestedIndexHintJoinSpec<I>> {

    }


    interface _NestedLeftParenSpec<I extends Item>
            extends _NestedLeftParenModifierTabularClause<_NestedIndexHintJoinSpec<I>, _AsClause<_NestedLeftParensJoinSpec<I>>>,
            _LeftParenCteClause<_MySQLNestedJoinClause<I>>,
            _NestedTableLeftParenClause<_NestedPartitionJoinSpec<I>>,
            _LeftParenNestedClause<_NestedLeftParenSpec<_MySQLNestedJoinClause<I>>, _MySQLNestedJoinClause<I>> {

    }

    interface _DynamicIndexHintOnClause extends _QueryIndexHintSpec<_DynamicIndexHintOnClause>,
            _DynamicIndexHintClause<_IndexPurposeBySpec<Object>, _DynamicIndexHintOnClause>,
            _OnClause<_DynamicJoinSpec> {

    }

    interface _DynamicPartitionOnClause extends _PartitionAsClause<_DynamicIndexHintOnClause> {

    }


    interface _DynamicJoinSpec
            extends _MySQLJoinClause<_DynamicIndexHintOnClause, _AsParensOnClause<_DynamicJoinSpec>>,
            _MySQLCrossClause<_DynamicIndexHintJoinClause, _DynamicJoinSpec>,
            _MySQLJoinCteClause<_OnClause<_DynamicJoinSpec>>,
            _CrossJoinCteClause<_DynamicJoinSpec>,
            _MySQLJoinNestedClause<_OnClause<_DynamicJoinSpec>>,
            _MySQLCrossNestedClause<_DynamicJoinSpec>,
            _MySQLDynamicJoinCrossClause<_DynamicJoinSpec>,
            _MySQLDialectJoinClause<_DynamicPartitionOnClause>,
            _DialectCrossJoinClause<_DynamicPartitionJoinClause> {

    }

    interface _DynamicIndexHintJoinClause extends _QueryIndexHintSpec<_DynamicIndexHintJoinClause>,
            _DynamicIndexHintClause<_IndexPurposeBySpec<Object>, _DynamicIndexHintJoinClause>,
            _DynamicJoinSpec {

    }

    interface _DynamicPartitionJoinClause extends _PartitionAsClause<_DynamicIndexHintJoinClause> {

    }


    interface _MultiStmtSemicolonSpec extends _MultiStmtSpec {

        void semicolon();

    }


}
