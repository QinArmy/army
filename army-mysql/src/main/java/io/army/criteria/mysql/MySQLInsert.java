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
import io.army.meta.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This interface representing MySQL INSERT statement.
 * <p>More document see {@link MySQLs#singleInsert()}.
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
 * @since 0.6.0
 */
public interface MySQLInsert extends MySQLStatement {


    interface _InsertClause<IR> extends Item {

        IR insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers);

    }


    interface _StaticConflictUpdateCommaClause<I extends Item, T> extends _DmlInsertClause<I> {

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, Expression value);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, R> valueOperator, @Nullable E value);

        <K, V, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, R> valueOperator, Function<K, V> function, K key);

        <E, U, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                                                                                      BiFunction<FieldMeta<T>, E, U> valueOperator, @Nullable E value);


        <K, V, U, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                                                                                         BiFunction<FieldMeta<T>, V, U> valueOperator,
                                                                                         Function<K, V> function, K key);

        _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, R> valueOperator,
                                                                                     Supplier<E> getter);

        <K, V, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, R> valueOperator,
                                                                                        Function<K, V> function, K key);

        <E, U, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                                                                                        BiFunction<FieldMeta<T>, E, U> valueOperator, Supplier<E> getter);

        <K, V, U, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                                                                                           BiFunction<FieldMeta<T>, V, U> valueOperator,
                                                                                           Function<K, V> function, K key);

    }

    /**
     * @see MySQLs#values(FieldMeta)
     */
    interface _StaticConflictUpdateClause<I extends Item, T> {

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Expression value);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, R> valueOperator, @Nullable E value);

        <K, V, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, R> valueOperator, Function<K, V> function, K key);

        <E, U, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                                                                                       BiFunction<FieldMeta<T>, E, U> valueOperator, @Nullable E value);


        <K, V, U, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                                                                                          BiFunction<FieldMeta<T>, V, U> valueOperator,
                                                                                          Function<K, V> function, K key);

        _StaticConflictUpdateCommaClause<I, T> updateIf(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticConflictUpdateCommaClause<I, T> updateIf(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> updateIf(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, R> valueOperator,
                                                                                      Supplier<E> getter);

        <K, V, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> updateIf(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, R> valueOperator,
                                                                                         Function<K, V> function, K key);

        <E, U, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> updateIf(FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                                                                                         BiFunction<FieldMeta<T>, E, U> valueOperator, Supplier<E> getter);

        <K, V, U, R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> updateIf(FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                                                                                            BiFunction<FieldMeta<T>, V, U> valueOperator,
                                                                                            Function<K, V> function, K key);
    }


    /**
     * @see MySQLs#values(FieldMeta)
     */
    interface _OnDuplicateKeyUpdateSpec<I extends Item, T> extends _DmlInsertClause<I> {
        _StaticConflictUpdateClause<I, T> onDuplicateKey();

        _DmlInsertClause<I> onDuplicateKeyUpdate(Consumer<UpdateStatement._ItemPairs<FieldMeta<T>>> consumer);

        _DmlInsertClause<I> ifOnDuplicateKeyUpdate(Consumer<UpdateStatement._ItemPairs<FieldMeta<T>>> consumer);
    }


    interface _OnAsRowAliasSpec<I extends Item, T> extends _OnDuplicateKeyUpdateSpec<I, T> {

        _OnDuplicateKeyUpdateSpec<I, T> as(String rowAlias);

    }



    /*-------------------below insert syntax interfaces  -------------------*/

    interface _MySQLValuesStaticParensClause<I extends Item, T>
            extends InsertStatement._ValuesParensClause<T, _ValuesStaticParensCommaSpec<I, T>> {

    }

    interface _ValuesStaticParensCommaSpec<I extends Item, T>
            extends _CommaClause<_MySQLValuesStaticParensClause<I, T>>,
            _OnAsRowAliasSpec<I, T> {

    }

    interface _ValuesColumnDefaultSpec<I extends Item, T>
            extends InsertStatement._FullColumnDefaultClause<T, _ValuesColumnDefaultSpec<I, T>>,
            InsertStatement._DomainValuesClause<T, _OnAsRowAliasSpec<I, T>>,
            InsertStatement._StaticValuesClause<_MySQLValuesStaticParensClause<I, T>>,
            InsertStatement._DynamicValuesClause<T, _OnAsRowAliasSpec<I, T>> {

    }

    interface _ComplexColumnDefaultSpec<I extends Item, T> extends _ValuesColumnDefaultSpec<I, T>,
            InsertStatement._QueryInsertSpaceClause<MySQLQuery.WithSpec<_OnDuplicateKeyUpdateSpec<I, T>>, _OnDuplicateKeyUpdateSpec<I, T>> {

    }

    interface _MySQLStaticAssignmentClause<I extends Item, T>
            extends InsertStatement._StaticAssignmentSetClause<T, _StaticAssignmentSpec<I, T>> {

    }

    interface _StaticAssignmentSpec<I extends Item, T> extends _MySQLStaticAssignmentClause<I, T>,
            _OnAsRowAliasSpec<I, T> {

    }

    interface _ColumnListSpec<I extends Item, T>
            extends InsertStatement._ColumnListParensClause<T, _ComplexColumnDefaultSpec<I, T>>,
            _ComplexColumnDefaultSpec<I, T>,
            _ValuesColumnDefaultSpec<I, T>,
            _MySQLStaticAssignmentClause<I, T>,
            InsertStatement._DynamicAssignmentSetClause<T, _OnAsRowAliasSpec<I, T>> {

    }

    interface _PartitionSpec<I extends Item, T> extends _PartitionClause<_ColumnListSpec<I, T>>,
            _ColumnListSpec<I, T> {

    }


    interface _ChildIntoClause<P> {

        <T> _PartitionSpec<Insert, T> into(ComplexTableMeta<P, T> table);

    }

    interface _ChildInsertIntoSpec<P> extends _InsertClause<_ChildIntoClause<P>> {

        <T> _PartitionSpec<Insert, T> insertInto(ComplexTableMeta<P, T> table);
    }


    interface _PrimaryIntoClause {

        <T> _PartitionSpec<Insert, T> into(SimpleTableMeta<T> table);


        <P> _PartitionSpec<InsertStatement._ParentInsert<_ChildInsertIntoSpec<P>>, P> into(ParentTableMeta<P> table);

    }


    interface _PrimaryInsertIntoSpec extends _InsertClause<_PrimaryIntoClause>, Item {

        <T> _PartitionSpec<Insert, T> insertInto(SimpleTableMeta<T> table);


        <P> _PartitionSpec<InsertStatement._ParentInsert<_ChildInsertIntoSpec<P>>, P> insertInto(ParentTableMeta<P> table);

    }


    interface _PrimaryPreferLiteralSpec
            extends InsertStatement._PreferLiteralClause<_PrimaryInsertIntoSpec>,
            _PrimaryInsertIntoSpec {

    }

    interface _PrimaryNullOptionSpec
            extends InsertStatement._NullOptionClause<_PrimaryPreferLiteralSpec>,
            _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec
            extends InsertStatement._MigrationOptionClause<_PrimaryNullOptionSpec>,
            InsertStatement._IgnoreReturnIdsOptionClause<_PrimaryNullOptionSpec>,
            _PrimaryNullOptionSpec {

    }


    /**
     * <p>
     * This interface representing INTO clause that support only {@link SingleTableMeta}.
     */
    interface _PrimarySingleIntoClause<I extends Item> {

        <T> _PartitionSpec<I, T> into(SingleTableMeta<T> table);

    }


    /**
     * <p>
     * This interface representing INSERT INTO spec that support only {@link SingleTableMeta}.
     */
    interface _PrimarySingleInsertIntoSpec<I extends Item> extends _InsertClause<_PrimarySingleIntoClause<I>>, Item {

        <T> _PartitionSpec<I, T> insertInto(SingleTableMeta<T> table);

    }


    /**
     * <p>
     * This interface representing {@link LiteralMode} spec that support only {@link SingleTableMeta}.
     */
    interface _PrimarySinglePreferLiteralSpec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_PrimarySingleInsertIntoSpec<I>>,
            _PrimarySingleInsertIntoSpec<I> {

    }


    /**
     * <p>
     * This interface representing {@link NullMode} spec that support only {@link SingleTableMeta}.
     */
    interface _PrimarySingleNullOptionSpec<I extends Item>
            extends InsertStatement._NullOptionClause<_PrimarySinglePreferLiteralSpec<I>>,
            _PrimarySinglePreferLiteralSpec<I> {

    }

    /**
     * <p>
     * This interface representing migration spec that support only {@link SingleTableMeta}.
     */
    interface _PrimarySingleOptionSpec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_PrimarySingleNullOptionSpec<I>>,
            InsertStatement._IgnoreReturnIdsOptionClause<_PrimarySingleNullOptionSpec<I>>,
            _PrimarySingleNullOptionSpec<I> {

    }


}
