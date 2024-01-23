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

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>This interface representing MySQL update statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/update.html">UPDATE Statement</a>
 * @since 0.6.0
 */
public interface MySQLUpdate extends MySQLStatement {


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link UpdateStatement._DmlUpdateSpec}</li>
     *          <li>method {@link Statement._DmlRowCountLimitClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _LimitSpec<I extends Item> extends Statement._DmlRowCountLimitClause<_DmlUpdateSpec<I>>,
            _DmlUpdateSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._LimitSpec}</li>
     *          <li>method {@link _StaticOrderByClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _OrderBySpec<I extends Item> extends _StaticOrderByClause<_OrderByCommaSpec<I>>,
            _LimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._OrderBySpec}</li>
     *          <li>method {@link Statement._WhereAndClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SingleWhereAndSpec<I extends Item> extends UpdateStatement._UpdateWhereAndClause<_SingleWhereAndSpec<I>>,
            _OrderBySpec<I> {

    }

    interface _SingleWhereClause<I extends Item> extends _WhereClause<_OrderBySpec<I>, _SingleWhereAndSpec<I>> {

    }

    /**
     * <p>
     * This interface representing SET clause for single-table UPDATE syntax.
     * * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * *
     *
     * @since 0.6.0
     */
    interface _SingleSetClause<I extends Item, T>
            extends UpdateStatement._StaticBatchSetClause<FieldMeta<T>, _SingleWhereSpec<I, T>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._BatchItemPairs<FieldMeta<T>>, _SingleWhereClause<I>> {
    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _SingleSetClause}</li>
     *          <li>method {@link Statement._WhereClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SingleWhereSpec<I extends Item, T> extends _SingleWhereClause<I>, _SingleSetClause<I, T> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForOrderByClause}</li>
     *          <li>method {@link _SingleSetClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SingleIndexHintSpec<I extends Item, T>
            extends MySQLQuery._IndexHintForOrderByClause<_SingleIndexHintSpec<I, T>>,
            _DynamicIndexHintClause<_IndexForOrderBySpec<Object>, _SingleIndexHintSpec<I, T>>,
            _SingleSetClause<I, T> {

    }

    interface _SinglePartitionClause<I extends Item, T> extends _PartitionAsClause<_SingleIndexHintSpec<I, T>> {

    }


    interface _SingleUpdateSpaceClause<I extends Item> {

        <T> _SingleIndexHintSpec<I, T> space(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias);

        <P> _SingleIndexHintSpec<I, P> space(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias);

        <T> _SinglePartitionClause<I, T> space(SingleTableMeta<T> table);

        <P> _SinglePartitionClause<I, P> space(ComplexTableMeta<P, ?> table);

    }

    interface _SingleUpdateClause<I extends Item> extends Item {

        _SingleUpdateSpaceClause<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

        <T> _SingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias);

        <P> _SingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias);

        <T> _SinglePartitionClause<I, T> update(SingleTableMeta<T> table);

        <P> _SinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table);

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>method {@link _SingleUpdateClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SingleWithSpec<I extends Item>
            extends _MySQLDynamicWithClause<_SingleUpdateClause<I>>,
            _MySQLStaticWithClause<_SingleUpdateClause<I>>,
            _SingleUpdateClause<I> {

    }





    /*################################## blow multi-table update api interface ##################################*/


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link UpdateStatement._UpdateWhereAndClause}</li>
     *          <li>{@link Statement._DmlUpdateSpec}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiWhereAndSpec<I extends Item> extends UpdateStatement._UpdateWhereAndClause<_MultiWhereAndSpec<I>>,
            _DmlUpdateSpec<I> {

    }

    /**
     * <p>
     * This interface representing SET clause for multi-table update clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiSetClause<I extends Item> extends UpdateStatement._StaticBatchSetClause<TableField, _MultiWhereSpec<I>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._BatchItemPairs<TableField>, _MultiWhereSpec<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereClause}</li>
     *          <li>{@link MySQLUpdate._MultiSetClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiWhereSpec<I extends Item> extends _WhereClause<_DmlUpdateSpec<I>, _MultiWhereAndSpec<I>>,
            _MultiSetClause<I> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _StaticIndexHintClause}</li>
     *          <li>method {@link Statement._AsClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiIndexHintOnSpec<I extends Item>
            extends _IndexHintForJoinClause<_MultiIndexHintOnSpec<I>>,
            _DynamicIndexHintClause<_IndexForJoinSpec<Object>, _MultiIndexHintOnSpec<I>>,
            _OnClause<_MultiJoinSpec<I>> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for multi-table update clause.
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
     *          <li>join clause</li>
     *          <li>{@link  _MultiSetClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     */
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
            _MultiSetClause<I> {


    }


    interface _ParensJoinSpec<I extends Item> extends _OptionalParensStringClause<_MultiJoinSpec<I>>, _MultiJoinSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForJoinClause}</li>
     *          <li>{@link _MultiJoinSpec}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiIndexHintJoinSpec<I extends Item>
            extends _IndexHintForJoinClause<_MultiIndexHintJoinSpec<I>>,
            _DynamicIndexHintClause<_IndexForJoinSpec<Object>, _MultiIndexHintJoinSpec<I>>,
            _MultiJoinSpec<I> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for multi-table update clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiPartitionJoinClause<I extends Item> extends _PartitionAsClause<_MultiIndexHintJoinSpec<I>> {

    }

    interface _MultiUpdateSpaceClause<I extends Item> {

        _MultiIndexHintJoinSpec<I> space(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        _AsClause<_ParensJoinSpec<I>> space(DerivedTable derivedTable);

        _AsClause<_ParensJoinSpec<I>> space(@Nullable SQLs.DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> space(Supplier<T> supplier);

        <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> space(@Nullable SQLs.DerivedModifier modifier, Supplier<T> supplier);

        _MultiJoinSpec<I> space(String cteName);

        _MultiJoinSpec<I> space(String cteName, SQLs.WordAs wordAs, String alias);

        _MultiJoinSpec<I> space(Function<MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>>, _MultiJoinSpec<I>> function);

        _MultiPartitionJoinClause<I> space(TableMeta<?> table);

    }


    /**
     * <p>
     * This interface representing multi-table UPDATE clause for MySQL syntax.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _SimpleMultiUpdateClause<I extends Item> extends Item {

        _MultiUpdateSpaceClause<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

        _MultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        _AsClause<_ParensJoinSpec<I>> update(DerivedTable derivedTable);

        _AsClause<_ParensJoinSpec<I>> update(@Nullable SQLs.DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> update(Supplier<T> supplier);

        <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> update(@Nullable SQLs.DerivedModifier modifier, Supplier<T> supplier);

        _MultiJoinSpec<I> update(String cteName);

        _MultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias);

        _MultiJoinSpec<I> update(Function<MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>>, _MultiJoinSpec<I>> function);

        _MultiPartitionJoinClause<I> update(TableMeta<?> table);


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>{@link _StaticWithClause}</li>
     *          <li>{@link _SimpleMultiUpdateClause}</li>
     *     </ul>
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @since 0.6.0
     */
    interface _MultiWithSpec<I extends Item> extends _MySQLDynamicWithClause<_SimpleMultiUpdateClause<I>>,
            _MySQLStaticWithClause<_SimpleMultiUpdateClause<I>>,
            _SimpleMultiUpdateClause<I> {

    }



}
