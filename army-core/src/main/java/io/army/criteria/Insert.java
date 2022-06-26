package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

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

    interface _MigrationOptionClause<OR> {

        OR migration(boolean migration);
    }

    /**
     * @since 1.0
     */
    interface _OptionClause<OR> extends _MigrationOptionClause<OR> {

        OR nullHandle(NullHandleMode mode);
    }


    interface _ComplexColumnClause<T extends IDomain, IR> extends _RightParenClause<IR> {

        _ComplexColumnClause<T, IR> comma(FieldMeta<? super T> field);

    }

    interface _ComplexColumnListClause<C, T extends IDomain, IR> {

        _RightParenClause<IR> leftParen(Consumer<Consumer<FieldMeta<? super T>>> consumer);

        _RightParenClause<IR> leftParen(BiConsumer<C, Consumer<FieldMeta<? super T>>> consumer);

        _ComplexColumnClause<T, IR> leftParen(FieldMeta<? super T> field);

    }

    interface _SingleColumnClause<T extends IDomain, IR> extends _RightParenClause<IR> {

        _SingleColumnClause<T, IR> comma(FieldMeta<T> field);

    }

    interface _SingleColumnListClause<C, T extends IDomain, IR> {

        _RightParenClause<IR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer);

        _RightParenClause<IR> leftParen(BiConsumer<C, Consumer<FieldMeta<T>>> consumer);

        _SingleColumnClause<T, IR> leftParen(FieldMeta<T> field);

    }


    /**
     * @since 1.0
     */
    interface _CommonExpClause<C, T extends IDomain, CR> {

        CR common(FieldMeta<? super T> field, @Nullable Object value);

        CR commonLiteral(FieldMeta<? super T> field, @Nullable Object value);

        CR commonExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function);

        CR commonExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier);

        CR commonDefault(FieldMeta<? super T> field);

        CR commonNull(FieldMeta<? super T> field);

        CR ifCommon(FieldMeta<? super T> field, Supplier<?> supplier);

        CR ifCommon(FieldMeta<? super T> field, Function<C, ?> function);

        CR ifCommon(FieldMeta<? super T> field, Function<String, ?> function, String keyName);

        CR ifCommonLiteral(FieldMeta<? super T> field, Supplier<?> supplier);

        CR ifCommonLiteral(FieldMeta<? super T> field, Function<C, ?> function);

        CR ifCommonLiteral(FieldMeta<? super T> field, Function<String, ?> function, String keyName);

    }

    /**
     * @since 1.0
     */
    interface _ValueClause<C, T extends IDomain, VR> {

        VR value(T domain);

        VR value(Function<C, T> function);

        VR value(Supplier<T> supplier);

        VR value(Function<String, Object> function, String keyName);

        VR values(List<T> domainList);

        VR values(Function<C, List<T>> function);

        VR values(Supplier<List<T>> supplier);

        VR values(Function<String, Object> function, String keyName);
    }

    interface _OnDuplicateKeyUpdateClause<UR> {

        UR onDuplicateKeyUpdate();

    }


    interface _OnDuplicateKeySetClause<C, F extends TableField, UR> {


        /**
         * @see SQLs#itemPair(DataField, Object)
         */
        UR setPairs(Consumer<Consumer<ItemPair>> consumer);

        /**
         * @see SQLs#itemPair(DataField, Object)
         */
        UR setPairs(BiConsumer<C, Consumer<ItemPair>> consumer);

        UR setExp(F field, Supplier<? extends Expression> supplier);

        UR setExp(F field, Function<C, ? extends Expression> function);

        UR setDefault(F field);

        UR setNull(F field);

        UR set(F field, @Nullable Object value);

        UR setLiteral(F field, @Nullable Object value);


        UR set(F field, BiFunction<DataField, Object, ItemPair> operator, Object value);

        UR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier);

        UR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function);

        UR setLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Object value);

    }


    interface _OnDuplicateKeyAliasSetClause<C, UR> {

        UR setExp(String columnAlias, Supplier<? extends Expression> supplier);

        UR setExp(String columnAlias, Function<C, ? extends Expression> function);

        UR setDefault(String columnAlias);

        UR setNull(String columnAlias);

        UR set(String columnAlias, @Nullable Object value);

        UR setLiteral(String columnAlias, @Nullable Object value);


        UR set(String columnAlias, BiFunction<DataField, Object, ItemPair> operator, Object value);

        UR setExp(String columnAlias, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier);

        UR setExp(String columnAlias, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function);

        UR setLiteral(String columnAlias, BiFunction<DataField, Object, ItemPair> operator, Object value);

    }


    interface _StandardLiteralOptionSpec<C>
            extends _PreferLiteralClause<_StandardOptionSpec<C>>, _StandardOptionSpec<C> {

    }

    /**
     * @since 1.0
     */
    interface _StandardOptionSpec<C>
            extends _StandardInsertIntoClause<C>, _OptionClause<_StandardInsertIntoClause<C>> {

    }


    /**
     * @since 1.0
     */
    interface _StandardInsertIntoClause<C> {

        <T extends IDomain> _StandardColumnsSpec<C, T> insertInto(TableMeta<T> table);

    }

    interface _StandardColumnsSpec<C, T extends IDomain>
            extends _ComplexColumnListClause<C, T, _StandardValueSpec<C, T>>, _StandardValueSpec<C, T> {

    }

    /**
     * @since 1.0
     */
    interface _StandardValueSpec<C, T extends IDomain> extends _CommonExpClause<C, T, _StandardValueSpec<C, T>>
            , _ValueClause<C, T, _InsertSpec> {


    }


    interface _SubQueryClause<C, SR> {

        SR space(Supplier<? extends SubQuery> supplier);

        SR space(Function<C, ? extends SubQuery> function);
    }

    interface _StandardSubQueryInsertClause<C> {

        <T extends IDomain> _StandardSingleColumnsSpec<C, T> insertInto(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _StandardParentColumnsSpec<C, P, T> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _StandardSingleColumnsSpec<C, T extends IDomain>
            extends _SingleColumnListClause<C, T, _StandardSubQuerySpec<C>> {

    }


    interface _StandardSubQuerySpec<C> extends _SubQueryClause<C, Insert._InsertSpec> {


    }

    interface _StandardParentColumnsSpec<C, P extends IDomain, T extends IDomain>
            extends _SingleColumnListClause<C, P, _StandardParentSubQueryClause<C, T>> {

    }

    interface _StandardParentSubQueryClause<C, T extends IDomain>
            extends _SubQueryClause<C, _StandardSingleColumnsSpec<C, T>> {

    }


}
