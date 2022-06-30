package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This interface representing INSERT statement.
 * </p>
 *
 * @since 1.0
 */
public interface Insert extends DmlStatement, DmlStatement.DmlInsert {



    /*################################## blow interfaces  ##################################*/

    /**
     * <p>
     * This interface representing the capacity ending INSERT statement.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _InsertSpec {

        Insert asInsert();

    }

    /*################################## blow multiInsert interfaces ##################################*/

    /**
     * <p>
     * This interface representing the option prefer output literal when output {@link IDomain} column,but not contain
     * comment expression
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _PreferLiteralClause<PO> {
        PO preferLiteral(boolean prefer);
    }

    /**
     * <p>
     * This clause mutual exclusion with {@link  _NullOptionClause}
     * </p>
     */
    interface _MigrationOptionClause<OR> {

        OR migration(boolean migration);
    }

    /**
     * <p>
     * This clause mutual exclusion with {@link  _MigrationOptionClause}
     * </p>
     *
     * @since 1.0
     */
    interface _NullOptionClause<OR> {

        OR nullHandle(NullHandleMode mode);
    }


    /**
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    interface _ColumnListClause<C, F extends TableField, RR> {

        _RightParenClause<RR> leftParen(Consumer<Consumer<F>> consumer);

        _RightParenClause<RR> leftParen(BiConsumer<C, Consumer<F>> consumer);

        _StaticColumnClause<F, RR> leftParen(F field);

    }

    /**
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    interface _StaticColumnClause<F extends TableField, IR> extends _RightParenClause<IR> {

        _StaticColumnClause<F, IR> comma(F field);

    }


    /**
     * @since 1.0
     */
    interface _CommonExpClause<C, F extends TableField, CR> {

        CR common(F field, @Nullable Object value);

        CR commonLiteral(F field, @Nullable Object value);

        CR commonExp(F field, Function<C, ? extends Expression> function);

        CR commonExp(F field, Supplier<? extends Expression> supplier);

        CR commonDefault(F field);

        CR commonNull(F field);

        CR ifCommon(F field, Supplier<?> supplier);

        CR ifCommon(F field, Function<C, ?> function);

        CR ifCommon(F field, Function<String, ?> function, String keyName);

        CR ifCommonLiteral(F field, Supplier<?> supplier);

        CR ifCommonLiteral(F field, Function<C, ?> function);

        CR ifCommonLiteral(F field, Function<String, ?> function, String keyName);

    }

    /**
     * @since 1.0
     */
    interface _DomainValueClause<C, T extends IDomain, VR> {

        VR value(T domain);

        VR value(Function<C, T> function);

        VR value(Supplier<T> supplier);

        VR value(Function<String, Object> function, String keyName);

        VR values(List<T> domainList);

        VR values(Function<C, List<T>> function);

        VR values(Supplier<List<T>> supplier);

        VR values(Function<String, Object> function, String keyName);
    }


    interface _StaticValueClause<VR> {

        VR value();

    }

    interface _DynamicValueClause<C, F extends TableField, VR> {

        VR value(Consumer<ColumnConsumer<F>> consumer);

        VR value(BiConsumer<C, ColumnConsumer<F>> consumer);

    }

    interface _StaticValuesClause<VR> {

        VR values();

    }

    interface _DynamicValuesClause<C, F extends TableField, VR> {

        VR values(Consumer<RowConstructor<F>> consumer);

        VR values(BiConsumer<C, RowConstructor<F>> consumer);
    }


    interface _StaticValueLeftParenClause<C, F extends TableField, VR> {

        _StaticColumnValueClause<C, F, VR> leftParen(F field, @Nullable Object value);

        _StaticColumnValueClause<C, F, VR> leftParenLiteral(F field, @Nullable Object value);

        _StaticColumnValueClause<C, F, VR> leftParenExp(F field, Supplier<? extends Expression> supplier);

        _StaticColumnValueClause<C, F, VR> leftParenExp(F field, Function<C, ? extends Expression> function);

    }

    interface _StaticColumnValueClause<C, F extends TableField, VR> extends Statement._RightParenClause<VR> {

        _StaticColumnValueClause<C, F, VR> comma(F field, @Nullable Object value);

        _StaticColumnValueClause<C, F, VR> commaLiteral(F field, @Nullable Object value);

        _StaticColumnValueClause<C, F, VR> commaExp(F field, Supplier<? extends Expression> supplier);

        _StaticColumnValueClause<C, F, VR> commaExp(F field, Function<C, ? extends Expression> function);

    }




    interface _OnDuplicateKeySetClause<C, F extends TableField, UR> {


        /**
         * @see SQLs#itemPair(DataField, Object)
         */
        UR commaPairs(Consumer<Consumer<ItemPair>> consumer);

        /**
         * @see SQLs#itemPair(DataField, Object)
         */
        UR commaPairs(BiConsumer<C, Consumer<ItemPair>> consumer);

        UR commaExp(F field, Supplier<? extends Expression> supplier);

        UR commaExp(F field, Function<C, ? extends Expression> function);

        UR commaDefault(F field);

        UR commaNull(F field);

        UR comma(F field, @Nullable Object value);

        UR commaLiteral(F field, @Nullable Object value);


        UR comma(F field, BiFunction<DataField, Object, ItemPair> operator, Object value);

        UR commaExp(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier);

        UR commaExp(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function);

        UR commaLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Object value);

    }


    interface _OnDuplicateKeyAliasSetClause<C, UR> {

        UR commaExp(String columnAlias, Supplier<? extends Expression> supplier);

        UR commaExp(String columnAlias, Function<C, ? extends Expression> function);

        UR commaDefault(String columnAlias);

        UR commaNull(String columnAlias);

        UR comma(String columnAlias, @Nullable Object value);

        UR commaLiteral(String columnAlias, @Nullable Object value);


        UR comma(String columnAlias, BiFunction<DataField, Object, ItemPair> operator, Object value);

        UR commaExp(String columnAlias, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier);

        UR commaExp(String columnAlias, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function);

        UR commaLiteral(String columnAlias, BiFunction<DataField, Object, ItemPair> operator, Object value);

    }

    interface _SpaceSubQueryClause<C, SR> {

        SR space(Supplier<? extends SubQuery> supplier);

        SR space(Function<C, ? extends SubQuery> function);
    }



    /*-------------------below standard domain insert syntax interface-------------------*/

    interface _StandardDomainColumnsSpec<C, T extends IDomain, F extends TableField>
            extends _ColumnListClause<C, F, _StandardDomainCommonExpSpec<C, T, F>>
            , _StandardDomainCommonExpSpec<C, T, F> {

    }

    interface _StandardDomainCommonExpSpec<C, T extends IDomain, F extends TableField>
            extends _CommonExpClause<C, F, _StandardDomainCommonExpSpec<C, T, F>>, _DomainValueClause<C, T, _InsertSpec> {

    }

    interface _StandardDomainInsertIntoClause<C> {

        <T extends IDomain> _StandardDomainColumnsSpec<C, T, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        <T extends IDomain> _StandardDomainColumnsSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table);
    }


    interface _StandardPreferLiteralSpec<C> extends _PreferLiteralClause<_StandardDomainInsertIntoClause<C>>
            , _StandardDomainInsertIntoClause<C> {

    }

    interface _StandardDomainOptionSpec<C> extends _NullOptionClause<_StandardPreferLiteralSpec<C>>
            , _MigrationOptionClause<_StandardPreferLiteralSpec<C>>, _StandardPreferLiteralSpec<C> {

    }


    /*-------------------below standard value insert syntax interface -------------------*/


    interface _StandardStaticValueLeftParenClause<C, F extends TableField>
            extends _StaticValueLeftParenClause<C, F, _InsertSpec> {

    }


    interface _StandardStaticValuesLeftParenClause<C, F extends TableField>
            extends _StaticValueLeftParenClause<C, F, _StandardStaticValuesLeftParenSpec<C, F>> {

    }

    interface _StandardStaticValuesLeftParenSpec<C, F extends TableField>
            extends _StandardStaticValuesLeftParenClause<C, F>, _InsertSpec {

    }

    /**
     * <p>
     * This interface is base interface of below:
     * <ul>
     *     <li>{@link _StandardColumnsSpec}</li>
     *     <li>{@link _StandardCommonExpSpec}</li>
     * </ul>
     * This interface is returned by below clause:
     * <ul>
     *     <li>{@link _StandardValueInsertIntoClause}</li>
     *     <li>{@link _StandardColumnsSpec}</li>
     *     <li>{@link _StandardCommonExpSpec}</li>
     * </ul>
     * </p>
     *
     * @since 1.0
     */
    interface _StandardValuesSpec<C, T extends IDomain, F extends TableField>
            extends _StaticValueClause<_StandardStaticValueLeftParenClause<C, F>>
            , _DynamicValueClause<C, F, _InsertSpec>, _StaticValuesClause<_StandardStaticValuesLeftParenClause<C, F>>
            , _DynamicValuesClause<C, F, _InsertSpec> {

    }

    /**
     * <p>
     * This interface is base interface of below:
     * <ul>
     *     <li>{@link _StandardColumnsSpec}</li>
     * </ul>
     * This interface is returned by below clause:
     * <ul>
     *     <li>{@link _StandardValueInsertIntoClause}</li>
     *     <li>{@link _StandardColumnsSpec}</li>
     * </ul>
     * </p>
     */
    interface _StandardCommonExpSpec<C, T extends IDomain, F extends TableField>
            extends _CommonExpClause<C, F, _StandardCommonExpSpec<C, T, F>>, _StandardValuesSpec<C, T, F> {

    }


    /**
     * <p>
     * This interface is returned by below method:
     * <ul>
     *     <li>{@link _StandardValueInsertIntoClause#insertInto(SingleTableMeta)}</li>
     *     <li>{@link _StandardValueInsertIntoClause#insertInto(ChildTableMeta)}</li>
     * </ul>
     * </p>
     */
    interface _StandardColumnsSpec<C, T extends IDomain, F extends TableField>
            extends _ColumnListClause<C, F, _StandardCommonExpSpec<C, T, F>>, _StandardCommonExpSpec<C, T, F> {

    }

    /**
     * @since 1.0
     */
    interface _StandardValueInsertIntoClause<C> {

        /**
         * <p>
         * This method name always same with {@link  _StandardDomainInsertIntoClause#insertInto(SingleTableMeta)}
         * </p>
         */
        <T extends IDomain> _StandardColumnsSpec<C, T, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        /**
         * <p>
         * This method name always same with {@link  _StandardDomainInsertIntoClause#insertInto(ChildTableMeta)}
         * </p>
         */
        <T extends IDomain> _StandardColumnsSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table);

    }


    interface _StandardValueOptionSpec<C> extends _MigrationOptionClause<_StandardValueInsertIntoClause<C>>
            , _NullOptionClause<_StandardValueInsertIntoClause<C>>, _StandardValueInsertIntoClause<C> {

    }



    /*-------------------below standard sub query insert syntax interface -------------------*/

    interface _StandardSingleColumnsClause<C, F extends TableField>
            extends _ColumnListClause<C, F, _StandardSpaceSubQueryClause<C>> {

    }


    interface _StandardSpaceSubQueryClause<C> extends _SpaceSubQueryClause<C, _InsertSpec> {

    }

    interface _StandardParentSubQueryClause<C, F extends TableField>
            extends _SpaceSubQueryClause<C, _StandardSingleColumnsClause<C, F>> {

    }

    interface _StandardParentColumnsClause<C, PF extends TableField, TF extends TableField>
            extends _ColumnListClause<C, PF, _StandardParentSubQueryClause<C, TF>> {

    }

    interface _StandardSubQueryInsertClause<C> {

        <T extends IDomain> _StandardSingleColumnsClause<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _StandardParentColumnsClause<C, FieldMeta<P>, FieldMeta<T>> insertInto(ComplexTableMeta<P, T> table);

    }


}
