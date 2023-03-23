package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.lang.Nullable;
import io.army.meta.*;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
 * @since 1.0
 */
public interface MySQLInsert extends MySQLStatement {


    interface _InsertClause<IR> extends Item {

        IR insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers);

    }


    interface _StaticConflictUpdateCommaClause<I extends Item, T> extends _DmlInsertClause<I> {

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, Expression value);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, R> valueOperator,
                                                                                Expression expression);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                     @Nullable Object value);

        <E> _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator,
                                                         Supplier<E> supplier);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                     Function<String, ?> function, String keyName);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                     BiFunction<FieldMeta<T>, Expression, Expression> valueOperator,
                                                     Expression expression);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                     BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                     Object value);

        <E> _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                         BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier);

        _StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                     BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                     Function<String, ?> function, String keyName);

        _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E> _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator,
                                                           Supplier<E> getter);

        _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                       Function<String, ?> function, String keyName);

        <E> _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                           BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> getter);

        _StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                       BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                       Function<String, ?> function, String keyName);

    }

    /**
     * @see MySQLs#values(FieldMeta)
     */
    interface _StaticConflictUpdateClause<I extends Item, T> {

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Expression value);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <R extends AssignmentItem> _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, R> valueOperator,
                                                                                 Expression expression);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                      @Nullable Object value);

        <E> _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator,
                                                          Supplier<E> supplier);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                      Function<String, ?> function, String keyName);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                      BiFunction<FieldMeta<T>, Expression, Expression> valueOperator,
                                                      Expression expression);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                      BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                      Object value);

        <E> _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                          BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier);

        _StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                      BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                      Function<String, ?> function, String keyName);

        _StaticConflictUpdateCommaClause<I, T> ifUpdate(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticConflictUpdateCommaClause<I, T> ifUpdate(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E> _StaticConflictUpdateCommaClause<I, T> ifUpdate(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator,
                                                            Supplier<E> getter);

        _StaticConflictUpdateCommaClause<I, T> ifUpdate(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                        Function<String, ?> function, String keyName);

        <E> _StaticConflictUpdateCommaClause<I, T> ifUpdate(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                            BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> getter);

        _StaticConflictUpdateCommaClause<I, T> ifUpdate(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                                                        BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                                                        Function<String, ?> function, String keyName);
    }


    /**
     * @see MySQLs#values(FieldMeta)
     */
    interface _OnDuplicateKeyUpdateSpec<I extends Item, T> extends _DmlInsertClause<I> {
        _StaticConflictUpdateClause<I, T> onDuplicateKey();

        _DmlInsertClause<I> onDuplicateKeyUpdate(Consumer<ItemPairs<FieldMeta<T>>> consumer);

        _DmlInsertClause<I> ifOnDuplicateKeyUpdate(Consumer<ItemPairs<FieldMeta<T>>> consumer);
    }


    interface _OnAsRowAliasSpec<I extends Item, T> extends _OnDuplicateKeyUpdateSpec<I, T> {

        _OnDuplicateKeyUpdateSpec<I, T> as(String rowAlias);

    }



    /*-------------------below insert syntax interfaces  -------------------*/

    interface _MySQLStaticValuesLeftParenClause<I extends Item, T>
            extends InsertStatement._StaticValueLeftParenClause<T, _StaticValuesLeftParenSpec<I, T>> {

    }

    interface _StaticValuesLeftParenSpec<I extends Item, T> extends _MySQLStaticValuesLeftParenClause<I, T>,
            _OnAsRowAliasSpec<I, T> {

    }

    interface _ValuesColumnDefaultSpec<I extends Item, T>
            extends InsertStatement._ColumnDefaultClause<T, _ValuesColumnDefaultSpec<I, T>>,
            InsertStatement._DomainValueClause<T, _OnAsRowAliasSpec<I, T>>,
            InsertStatement._StaticValuesClause<_MySQLStaticValuesLeftParenClause<I, T>>,
            InsertStatement._DynamicValuesClause<T, _OnAsRowAliasSpec<I, T>> {

    }

    interface _ComplexColumnDefaultSpec<I extends Item, T> extends _ValuesColumnDefaultSpec<I, T>,
            _StaticSpaceClause<MySQLQuery._WithSpec<_OnDuplicateKeyUpdateSpec<I, T>>>,
            InsertStatement._QueryInsertSpaceClause<MySQLQuery._WithSpec<_OnDuplicateKeyUpdateSpec<I, T>>, _OnDuplicateKeyUpdateSpec<I, T>> {

    }

    interface _MySQLStaticAssignmentClause<I extends Item, T>
            extends InsertStatement._StaticAssignmentSetClause<T, _StaticAssignmentSpec<I, T>> {

    }

    interface _StaticAssignmentSpec<I extends Item, T> extends _MySQLStaticAssignmentClause<I, T>,
            _OnAsRowAliasSpec<I, T> {

    }

    interface _ColumnListSpec<I extends Item, T>
            extends InsertStatement._ColumnListClause<T, _ComplexColumnDefaultSpec<I, T>>,
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
            _PrimaryNullOptionSpec {

    }


    /**
     * <p>
     * This interface representing INTO clause that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySingleIntoClause<I extends Item> {

        <T> _PartitionSpec<I, T> into(SingleTableMeta<T> table);

    }


    /**
     * <p>
     * This interface representing INSERT INTO spec that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySingleInsertIntoSpec<I extends Item> extends _InsertClause<_PrimarySingleIntoClause<I>>, Item {

        <T> _PartitionSpec<I, T> insertInto(SingleTableMeta<T> table);

    }

    /**
     * <p>
     * This interface representing {@link LiteralMode} spec that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySinglePreferLiteralSpec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_PrimarySingleInsertIntoSpec<I>>,
            _PrimarySingleInsertIntoSpec<I> {

    }


    /**
     * <p>
     * This interface representing {@link NullMode} spec that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySingleNullOptionSpec<I extends Item>
            extends InsertStatement._NullOptionClause<_PrimarySinglePreferLiteralSpec<I>>,
            _PrimarySinglePreferLiteralSpec<I> {

    }

    /**
     * <p>
     * This interface representing migration spec that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySingleOptionSpec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_PrimarySingleNullOptionSpec<I>>,
            _PrimarySingleNullOptionSpec<I> {

    }


}
