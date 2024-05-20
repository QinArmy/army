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

import io.army.criteria.DeleteStatement;
import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Hint;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>This interface representing MySQL delete statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/delete.html">DELETE Statement</a>
 * @since 0.6.0
 */
public interface MySQLDelete extends MySQLStatement {

    /**
     * <p>
     * This interface representing single-table DELETE clause for MySQL syntax.
     * * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * *
     *
     * @param <T> next clause java type
     * @since 0.6.0
     */
    interface _SingleDeleteClause<T> extends DeleteStatement._SingleDeleteClause<T> {


        DeleteStatement._SingleDeleteFromClause<T> delete(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.Statement._DmlRowCountLimitClause}</li>
     *          <li>{@link _DmlDeleteSpec}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _LimitSpec<I extends Item> extends Statement._DmlRowCountLimitClause<_DmlDeleteSpec<I>>,
            _DmlDeleteSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _StaticOrderByClause}</li>
     *          <li>{@link _LimitSpec}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _OrderBySpec<I extends Item> extends _StaticOrderByClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.Statement._WhereAndClause}</li>
     *          <li>{@link _OrderBySpec}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SingleWhereAndSpec<I extends Item> extends _WhereAndClause<_SingleWhereAndSpec<I>>, _OrderBySpec<I> {

    }

    /**
     * <p>
     * This interface representing WHERE clause for single-table DELETE syntax.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SingleWhereClause<I extends Item> extends _WhereClause<_OrderBySpec<I>, _SingleWhereAndSpec<I>> {

    }


    interface _SinglePartitionSpec<I extends Item> extends _PartitionClause<_SingleWhereClause<I>>,
            _SingleWhereClause<I> {

    }

    /**
     * <p>
     * This interface representing single-table DELETE clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SimpleSingleDeleteClause<I extends Item> extends _SingleDeleteClause<_SinglePartitionSpec<I>> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _DynamicWithClause}</li>
     *          <li>{@link _StaticWithClause}</li>
     *          <li>{@link _SimpleSingleDeleteClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SingleWithSpec<I extends Item>
            extends _MySQLDynamicWithClause<_SimpleSingleDeleteClause<I>>,
            _MySQLStaticWithClause<_SimpleSingleDeleteClause<I>>,
            _SimpleSingleDeleteClause<I> {

    }


    /*################################## blow multi-table delete api interface ##################################*/


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>{@link Statement._DmlDeleteSpec}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiWhereAndSpec<I extends Item> extends _WhereAndClause<_MultiWhereAndSpec<I>>, _DmlDeleteSpec<I> {

    }

    /**
     * /**
     * <p>
     * This interface representing WHERE clause multi-table DELETE syntax.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiWhereClause<I extends Item> extends _WhereClause<_DmlDeleteSpec<I>, _MultiWhereAndSpec<I>> {

    }


    interface _MultiIndexHintOnSpec<I extends Item>
            extends _IndexHintFoPurposeClause<_MultiIndexHintOnSpec<I>>,
            _OnClause<_MultiJoinSpec<I>> {

    }

    /**
     * /**
     * <p>
     * This interface representing PARTITION clause multi-table DELETE syntax.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiPartitionOnClause<I extends Item> extends _PartitionAsClause<_MultiIndexHintOnSpec<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>JOIN clause</li>
     *          <li>{@link MySQLDelete._MultiWhereClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiJoinSpec<I extends Item>
            extends _MySQLJoinClause<_MultiIndexHintOnSpec<I>, _AsParensOnClause<_MultiJoinSpec<I>>>,
            _MySQLCrossClause<_MultiIndexHintJoinSpec<I>, _ParensJoinSpec<I>>,
            _MySQLJoinCteClause<_OnClause<_MultiJoinSpec<I>>>,
            _CrossJoinCteClause<_MultiJoinSpec<I>>,
            _MySQLJoinNestedClause<_OnClause<_MultiJoinSpec<I>>>,
            _MySQLCrossNestedClause<_MultiJoinSpec<I>>,
            _MySQLDynamicJoinCrossClause<_MultiJoinSpec<I>>,
            _MySQLDialectJoinClause<_MultiPartitionOnClause<I>>,
            _DialectCrossJoinClause<_MultiPartitionJoinClause<I>>,
            _MultiWhereClause<I> {

    }

    interface _MultiIndexHintJoinSpec<I extends Item>
            extends _IndexHintFoPurposeClause<_MultiIndexHintJoinSpec<I>>,
            _MultiJoinSpec<I> {

    }

    interface _ParensJoinSpec<I extends Item> extends _OptionalParensStringClause<_MultiJoinSpec<I>>, _MultiJoinSpec<I> {

    }

    /**
     * /**
     * <p>
     * This interface representing PARTITION clause multi-table DELETE syntax.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiPartitionJoinClause<I extends Item> extends _PartitionAsClause<_MultiIndexHintJoinSpec<I>> {

    }


    interface _SimpleMultiDeleteUsingClause<I extends Item>
            extends _UsingModifierClause<_MultiIndexHintJoinSpec<I>, _AsClause<_ParensJoinSpec<I>>>,
            _UsingCteClause<_MultiJoinSpec<I>>,
            _MySQLUsingTableClause<_MultiPartitionJoinClause<I>>,
            _MySQLUsingNestedClause<_MultiJoinSpec<I>> {

    }


    interface _MultiDeleteFromTableClause<I extends Item>
            extends _MySQLFromClause<_MultiIndexHintJoinSpec<I>, _ParensJoinSpec<I>>,
            Query._FromTableClause<_MultiPartitionJoinClause<I>>,
            _MySQLFromNestedClause<_MultiJoinSpec<I>> {

    }


    interface _MultiDeleteFromAliasClause<I extends Item> {

        _SimpleMultiDeleteUsingClause<I> from(String alias);

        _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2);

        _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2, String alias3);

        _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2, String alias3, String alias4);

        _SimpleMultiDeleteUsingClause<I> from(List<String> aliasList);

        _SimpleMultiDeleteUsingClause<I> from(Consumer<Consumer<String>> consumer);

        _MultiDeleteFromTableClause<I> space(String alias);

        _MultiDeleteFromTableClause<I> space(String alias1, String alias2);

        _MultiDeleteFromTableClause<I> space(String alias1, String alias2, String alias3);

        _MultiDeleteFromTableClause<I> space(String alias1, String alias2, String alias3, String alias4);

        _MultiDeleteFromTableClause<I> space(List<String> aliasList);

        _MultiDeleteFromTableClause<I> space(Consumer<Consumer<String>> consumer);

    }


    /**
     * <p>
     * This interface representing DELETE clause for multi-table DELETE syntax.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MySQLMultiDeleteClause<I extends Item> extends Item {

        _MultiDeleteFromAliasClause<I> delete(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

        _MultiDeleteFromTableClause<I> delete(String alias);

        _MultiDeleteFromTableClause<I> delete(String alias1, String alias2);

        _MultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3);

        _MultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3, String alias4);

        _MultiDeleteFromTableClause<I> delete(List<String> aliasList);

        _MultiDeleteFromTableClause<I> delete(Consumer<Consumer<String>> consumer);

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>{@link _StaticWithClause}</li>
     *          <li>{@link _MySQLMultiDeleteClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiWithSpec<I extends Item> extends _MySQLDynamicWithClause<_MySQLMultiDeleteClause<I>>,
            _MySQLStaticWithClause<_MySQLMultiDeleteClause<I>>,
            _MySQLMultiDeleteClause<I> {

    }



}
