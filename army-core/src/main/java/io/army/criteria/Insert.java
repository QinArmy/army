package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing INSERT statement.
 * </p>
 *
 * @since 1.0
 */
public interface Insert extends DmlStatement, DmlStatement.DmlInsert {


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
    interface _InsertSpec extends DmlStatement._DmlInsertSpec<Insert> {

    }


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
    interface _NullOptionClause<OR> {

        OR nullHandle(NullHandleMode mode);
    }


    /**
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    interface _ColumnListClause<C, F extends TableField, RR> {

        Statement._RightParenClause<RR> leftParen(Consumer<Consumer<F>> consumer);

        Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<F>> consumer);

        Statement._RightParenClause<RR> leftParen(F field);

        _StaticColumnDualClause<F, RR> leftParen(F field1, F field2);

    }


    interface _StaticColumnDualClause<F extends TableField, IR> extends Statement._RightParenClause<IR> {

        Statement._RightParenClause<IR> comma(F field);

        _StaticColumnDualClause<F, IR> comma(F field1, F field2);

    }


    /**
     * @since 1.0
     */
    interface _ColumnDefaultClause<C, F extends TableField, CR> {

        CR defaultValue(F field, @Nullable Object value);

        CR defaultLiteral(F field, @Nullable Object value);

        CR defaultExp(F field, Function<C, ? extends Expression> function);

        CR defaultExp(F field, Supplier<? extends Expression> supplier);

        CR defaultNull(F field);

        CR ifDefault(F field, Supplier<?> supplier);

        CR ifDefault(F field, Function<C, ?> function);

        CR ifDefault(F field, Function<String, ?> function, String keyName);

        CR ifDefaultLiteral(F field, Supplier<?> supplier);

        CR ifDefaultLiteral(F field, Function<C, ?> function);

        CR ifDefaultLiteral(F field, Function<String, ?> function, String keyName);

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


    interface _StaticValueClause<C, F extends TableField, VR> {

        _StaticValueLeftParenClause<C, F, VR> value();

    }

    interface _DynamicValueClause<C, F extends TableField, VR> {

        VR value(Consumer<PairConsumer<F>> consumer);

        VR value(BiConsumer<C, PairConsumer<F>> consumer);

    }

    interface _StaticValuesClause<C, F extends TableField, VR> {

        _StaticValueLeftParenClause<C, F, VR> values();

    }

    interface _DynamicValuesClause<C, F extends TableField, VR> {

        VR values(Consumer<PairsConstructor<F>> consumer);

        VR values(BiConsumer<C, PairsConstructor<F>> consumer);
    }


    interface _ChildPartClause<CR> {

        CR child();

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

    /**
     * <p>
     * This interface representing SET assignment_list clause in INSERT statement.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    interface _AssignmentSetClause<C, F extends TableField, SR> {

        SR setPair(Consumer<Consumer<ItemPair>> consumer);

        SR setPair(BiConsumer<C, Consumer<ItemPair>> consumer);

        SR set(F field, @Nullable Object value);

        SR setLiteral(F field, @Nullable Object value);

        SR setExp(F field, Supplier<? extends Expression> supplier);

        SR setExp(F field, Function<C, ? extends Expression> function);

        SR ifSet(F field, Supplier<?> supplier);

        SR ifSet(F field, Function<C, ?> function);

        SR ifSet(F field, Function<String, ?> function, String keyName);

        SR ifSetLiteral(F field, Supplier<?> supplier);

        SR ifSetLiteral(F field, Function<C, ?> function);

        SR ifSetLiteral(F field, Function<String, ?> function, String keyName);


    }


    interface _CommaFieldValuePairClause<C, F extends TableField, SR> {

        SR comma(F field, @Nullable Object value);

        SR commaLiteral(F field, @Nullable Object value);

        SR commaExp(F field, Supplier<? extends Expression> supplier);

        SR commaExp(F field, Function<C, ? extends Expression> function);

    }


    interface _CommaAliasValuePairClause<C, SR> {

        SR comma(String columnAlias, @Nullable Object value);

        SR commaLiteral(String columnAlias, @Nullable Object value);

        SR commaExp(String columnAlias, Supplier<? extends Expression> supplier);

        SR commaExp(String columnAlias, Function<C, ? extends Expression> function);

    }


    interface _SpaceSubQueryClause<C, SR> {

        SR space(Supplier<? extends SubQuery> supplier);

        SR space(Function<C, ? extends SubQuery> function);
    }



    /*-------------------below standard domain insert syntax interface-------------------*/

    interface _StandardDomainDefaultSpec<C, T extends IDomain>
            extends _ColumnDefaultClause<C, FieldMeta<T>, _StandardDomainDefaultSpec<C, T>>
            , _DomainValueClause<C, T, _InsertSpec> {

    }

    interface _StandardDomainColumnsSpec<C, T extends IDomain>
            extends _ColumnListClause<C, FieldMeta<T>, _StandardDomainDefaultSpec<C, T>>
            , _StandardDomainDefaultSpec<C, T> {

    }


    interface _StandardChildInsertIntoClause<C, P extends IDomain> {

        <T extends IDomain> _StandardDomainColumnsSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _StandardParentDomainDefaultSpec<C, P extends IDomain>
            extends _ColumnDefaultClause<C, FieldMeta<P>, _StandardParentDomainDefaultSpec<C, P>>
            , _DomainValueClause<C, P, _InsertSpec>
            , _ChildPartClause<_StandardChildInsertIntoClause<C, P>> {

    }


    interface _StandardParentDomainColumnsSpec<C, P extends IDomain>
            extends _ColumnListClause<C, FieldMeta<P>, _StandardParentDomainDefaultSpec<C, P>>
            , _StandardParentDomainDefaultSpec<C, P> {

    }


    interface _StandardDomainInsertIntoClause<C> {

        <T extends IDomain> _StandardDomainColumnsSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _StandardParentDomainColumnsSpec<C, T> insertInto(ParentTableMeta<T> table);
    }


    interface _StandardDomainPreferLiteralSpec<C> extends _PreferLiteralClause<_StandardDomainInsertIntoClause<C>>
            , _StandardDomainInsertIntoClause<C> {

    }

    interface _StandardDomainNullOptionSpec<C> extends _NullOptionClause<_StandardDomainPreferLiteralSpec<C>>
            , _StandardDomainPreferLiteralSpec<C> {

    }

    interface _StandardDomainOptionSpec<C> extends _MigrationOptionClause<_StandardDomainNullOptionSpec<C>>
            , _StandardDomainNullOptionSpec<C> {

    }


    /*-------------------below standard value insert syntax interface -------------------*/

    /**
     * @since 1.0
     */
    interface _StandardValuesSpec<C, F extends TableField> extends _StaticValuesClause<C, F, _InsertSpec>
            , _DynamicValuesClause<C, F, _InsertSpec> {

    }

    /**
     * <p>
     * This interface is base interface of below:
     * <ul>
     *     <li>{@link _StandardValueColumnsSpec}</li>
     * </ul>
     * This interface is returned by below clause:
     * <ul>
     *     <li>{@link _StandardValueInsertIntoClause}</li>
     *     <li>{@link _StandardValueColumnsSpec}</li>
     * </ul>
     * </p>
     */
    interface _StandardValueDefaultSpec<C, F extends TableField>
            extends _ColumnDefaultClause<C, F, _StandardValueDefaultSpec<C, F>>, _StandardValuesSpec<C, F> {

    }


    interface _StandardValueColumnsSpec<C, F extends TableField>
            extends _ColumnListClause<C, F, _StandardValueDefaultSpec<C, F>>, _StandardValueDefaultSpec<C, F> {

    }

    interface _StandardValueChildInsertIntoClause<C, P extends IDomain> {

        <T extends IDomain> _StandardValueColumnsSpec<C, FieldMeta<T>> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _StandardValueChildSpec<C, P extends IDomain>
            extends _ChildPartClause<_StandardValueChildInsertIntoClause<C, P>>
            , _InsertSpec {

    }

    interface _StandardParentStaticValuesSpec<C, P extends IDomain>
            extends _StaticValueLeftParenClause<C, FieldMeta<P>, _StandardParentStaticValuesSpec<C, P>>
            , _StandardValueChildSpec<C, P> {

    }


    interface _StandardParentValuesSpec<C, P extends IDomain>
            extends _StaticValuesClause<C, FieldMeta<P>, _StandardParentStaticValuesSpec<C, P>>
            , _DynamicValuesClause<C, FieldMeta<P>, _StandardValueChildSpec<C, P>> {

    }

    interface _StandardParentValueDefaultSpec<C, P extends IDomain>
            extends _ColumnDefaultClause<C, FieldMeta<P>, _StandardParentValueDefaultSpec<C, P>>
            , _StandardParentValuesSpec<C, P> {

    }

    interface _StandardParentValueColumnsSpec<C, P extends IDomain>
            extends _ColumnListClause<C, FieldMeta<P>, _StandardParentValueDefaultSpec<C, P>>
            , _StandardParentValueDefaultSpec<C, P> {

    }

    /**
     * @since 1.0
     */
    interface _StandardValueInsertIntoClause<C> {

        <T extends IDomain> _StandardValueColumnsSpec<C, FieldMeta<T>> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _StandardParentValueColumnsSpec<C, T> insertInto(ParentTableMeta<T> table);

    }


    interface _StandardValueNullOptionSpec<C> extends _NullOptionClause<_StandardValueInsertIntoClause<C>>
            , _StandardValueInsertIntoClause<C> {

    }

    interface _StandardValueOptionSpec<C> extends _MigrationOptionClause<_StandardValueNullOptionSpec<C>>
            , _StandardValueNullOptionSpec<C> {

    }



    /*-------------------below standard sub query insert syntax interface -------------------*/


    interface _StandardSingleColumnsClause<C, F extends TableField>
            extends _ColumnListClause<C, F, _SpaceSubQueryClause<C, _InsertSpec>> {

    }

    interface _StandardQueryChildInsertIntoClause<C, P extends IDomain> {

        <T extends IDomain> _StandardSingleColumnsClause<C, FieldMeta<T>> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _StandardQueryChildPartSpec<C, P extends IDomain>
            extends _ChildPartClause<_StandardQueryChildInsertIntoClause<C, P>>
            , _InsertSpec {

    }

    interface _StandardParentSubQueryClause<C, P extends IDomain>
            extends _SpaceSubQueryClause<C, _StandardQueryChildPartSpec<C, P>> {

    }

    interface _StandardParentColumnsClause<C, P extends IDomain>
            extends _ColumnListClause<C, FieldMeta<P>, _StandardParentSubQueryClause<C, P>> {

    }

    interface _StandardSubQueryInsertClause<C> {

        <T extends IDomain> _StandardSingleColumnsClause<C, FieldMeta<T>> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _StandardParentColumnsClause<C, T> insertInto(ParentTableMeta<T> table);

    }


}
