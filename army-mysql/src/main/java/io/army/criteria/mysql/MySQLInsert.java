package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLFunctions;
import io.army.criteria.impl.MySQLs;
import io.army.lang.Nullable;
import io.army.meta.*;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
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
     * @see MySQLFunctions#values(FieldMeta)
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
     * @see MySQLFunctions#values(FieldMeta)
     */
    interface _OnDuplicateKeyUpdateSpec<I extends Item, T> extends _DmlInsertClause<I> {
        //TODO MySQLFunctions#values(FieldMeta)
        _StaticConflictUpdateClause<I, T> onDuplicateKey();

        _DmlInsertClause<I> onDuplicateKeyUpdate(Consumer<ItemPairs<FieldMeta<T>>> consumer);

        _DmlInsertClause<I> ifOnDuplicateKeyUpdate(Consumer<ItemPairs<FieldMeta<T>>> consumer);
    }


    interface _OnAsRowAliasSpec<I extends Item, T> extends _OnDuplicateKeyUpdateSpec<I, T> {

        _OnDuplicateKeyUpdateSpec<I, T> as(String rowAlias);

    }



    /*-------------------below insert syntax interfaces  -------------------*/

    interface _MySQLStaticValuesLeftParenClause<I extends Item, T>
            extends Insert._StaticValueLeftParenClause<T, _StaticValuesLeftParenSpec<I, T>> {

    }

    interface _StaticValuesLeftParenSpec<I extends Item, T> extends _MySQLStaticValuesLeftParenClause<I, T>
            , _OnAsRowAliasSpec<I, T> {

    }

    interface _ValuesColumnDefaultSpec<I extends Item, T>
            extends Insert._ColumnDefaultClause<T, _ValuesColumnDefaultSpec<I, T>>
            , Insert._DomainValueClause<T, _OnAsRowAliasSpec<I, T>>
            , Insert._StaticValuesClause<_MySQLStaticValuesLeftParenClause<I, T>>
            , Insert._DynamicValuesClause<T, _OnAsRowAliasSpec<I, T>> {

    }

    interface _ComplexColumnDefaultSpec<I extends Item, T> extends _ValuesColumnDefaultSpec<I, T>
            , _StaticSpaceClause<MySQLQuery._WithSpec<_OnAsRowAliasSpec<I, T>>> {

    }

    interface _MySQLStaticAssignmentClause<I extends Item, T>
            extends Insert._StaticAssignmentSetClause<T, _StaticAssignmentSpec<I, T>> {

    }

    interface _StaticAssignmentSpec<I extends Item, T> extends _MySQLStaticAssignmentClause<I, T>
            , _OnAsRowAliasSpec<I, T> {

    }

    interface _ColumnListSpec<I extends Item, T> extends Insert._ColumnListClause<T, _ComplexColumnDefaultSpec<I, T>>
            , _ValuesColumnDefaultSpec<I, T>
            , _MySQLStaticAssignmentClause<I, T>
            , Insert._DynamicAssignmentSetClause<T, _OnAsRowAliasSpec<I, T>> {

    }

    interface _PartitionSpec<I extends Item, T> extends _PartitionClause<_ColumnListSpec<I, T>>
            , _ColumnListSpec<I, T> {

    }


    interface _ChildIntoClause<I extends Item, P> {

        <T> _PartitionSpec<I, T> into(ComplexTableMeta<P, T> table);

    }

    interface _ChildInsertIntoSpec<I extends Item, P> extends _InsertClause<_ChildIntoClause<I, P>> {

        <T> _PartitionSpec<I, T> insertInto(ComplexTableMeta<P, T> table);
    }


    interface _PrimaryIntoClause<I extends Item> {

        <T> _PartitionSpec<I, T> into(SingleTableMeta<T> table);


        <P> _PartitionSpec<Insert._ParentInsert<_ChildInsertIntoSpec<I, P>>, P> into(ParentTableMeta<P> table);

    }


    interface _PrimaryInsertIntoSpec<I extends Item> extends _InsertClause<_PrimaryIntoClause<I>>, Item {

        <T> _PartitionSpec<I, T> insertInto(SimpleTableMeta<T> table);


        <P> _PartitionSpec<Insert._ParentInsert<_ChildInsertIntoSpec<I, P>>, P> insertInto(ParentTableMeta<P> table);

    }


    interface _PrimaryPreferLiteralSpec<I extends Item> extends Insert._PreferLiteralClause<_PrimaryInsertIntoSpec<I>>
            , _PrimaryInsertIntoSpec<I> {

    }

    interface _PrimaryNullOptionSpec<I extends Item> extends Insert._NullOptionClause<_PrimaryPreferLiteralSpec<I>>
            , _PrimaryPreferLiteralSpec<I> {

    }

    interface _PrimaryOptionSpec<I extends Item> extends Insert._MigrationOptionClause<_PrimaryNullOptionSpec<I>>
            , _PrimaryNullOptionSpec<I> {

    }


}
