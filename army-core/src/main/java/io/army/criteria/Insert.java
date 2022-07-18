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


    interface _ColumnListClause<C, T extends IDomain, RR> {

        Statement._RightParenClause<RR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer);

        Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<FieldMeta<T>>> consumer);

        Statement._RightParenClause<RR> leftParen(FieldMeta<T> field);

        _StaticColumnDualClause<T, RR> leftParen(FieldMeta<T> field1, FieldMeta<T> field2);

    }


    interface _StaticColumnDualClause<T extends IDomain, IR> extends Statement._RightParenClause<IR> {

        Statement._RightParenClause<IR> comma(FieldMeta<T> field);

        _StaticColumnDualClause<T, IR> comma(FieldMeta<T> field1, FieldMeta<T> field2);

    }


    /**
     * @since 1.0
     */
    interface _ColumnDefaultClause<C, T extends IDomain, CR> {

        CR defaultValue(FieldMeta<T> field, @Nullable Object value);

        CR defaultLiteral(FieldMeta<T> field, @Nullable Object value);

        CR defaultExp(FieldMeta<T> field, Function<C, ? extends Expression> function);

        CR defaultExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        CR defaultNull(FieldMeta<T> field);

        CR ifDefaultValue(FieldMeta<T> field, Supplier<?> supplier);

        CR ifDefaultValue(FieldMeta<T> field, Function<C, ?> function);

        CR ifDefaultValue(FieldMeta<T> field, Function<String, ?> function, String keyName);

        CR ifDefaultLiteral(FieldMeta<T> field, Supplier<?> supplier);

        CR ifDefaultLiteral(FieldMeta<T> field, Function<C, ?> function);

        CR ifDefaultLiteral(FieldMeta<T> field, Function<String, ?> function, String keyName);

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


    interface _StaticValuesClause<C, T extends IDomain, VR> {

        _StaticValueLeftParenClause<C, T, VR> values();

    }

    interface _DynamicValuesClause<C, T extends IDomain, VR> {

        VR values(Consumer<PairsConstructor<FieldMeta<T>>> consumer);

        VR values(BiConsumer<C, PairsConstructor<FieldMeta<T>>> consumer);
    }


    interface _ChildPartClause<CR> {

        CR child();

    }


    interface _StaticValueLeftParenClause<C, T extends IDomain, VR> {

        _StaticColumnValueClause<C, T, VR> leftParen(FieldMeta<T> field, @Nullable Object value);

        _StaticColumnValueClause<C, T, VR> leftParenLiteral(FieldMeta<T> field, @Nullable Object value);

        _StaticColumnValueClause<C, T, VR> leftParenExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        _StaticColumnValueClause<C, T, VR> leftParenExp(FieldMeta<T> field, Function<C, ? extends Expression> function);


    }

    interface _StaticColumnValueClause<C, T extends IDomain, VR> extends Statement._RightParenClause<VR> {

        _StaticColumnValueClause<C, T, VR> comma(FieldMeta<T> field, @Nullable Object value);

        _StaticColumnValueClause<C, T, VR> commaLiteral(FieldMeta<T> field, @Nullable Object value);

        _StaticColumnValueClause<C, T, VR> commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        _StaticColumnValueClause<C, T, VR> commaExp(FieldMeta<T> field, Function<C, ? extends Expression> function);


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
            extends _ColumnDefaultClause<C, T, _StandardDomainDefaultSpec<C, T>>
            , _DomainValueClause<C, T, _InsertSpec> {

    }

    interface _StandardDomainColumnsSpec<C, T extends IDomain>
            extends _ColumnListClause<C, T, _StandardDomainDefaultSpec<C, T>>
            , _StandardDomainDefaultSpec<C, T> {

    }


    interface _StandardChildInsertIntoClause<C, P extends IDomain> {

        <T extends IDomain> _StandardDomainColumnsSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _StandardParentDomainDefaultSpec<C, P extends IDomain>
            extends _ColumnDefaultClause<C, P, _StandardParentDomainDefaultSpec<C, P>>
            , _DomainValueClause<C, P, _InsertSpec>
            , _ChildPartClause<_StandardChildInsertIntoClause<C, P>> {

    }


    interface _StandardParentDomainColumnsSpec<C, P extends IDomain>
            extends _ColumnListClause<C, P, _StandardParentDomainDefaultSpec<C, P>>
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


    interface _StandardValueStaticLeftParenSpec<C, T extends IDomain>
            extends _StaticValueLeftParenClause<C, T, _StandardValueStaticLeftParenSpec<C, T>>, _InsertSpec {

    }

    /**
     * @since 1.0
     */
    interface _StandardValuesSpec<C, T extends IDomain>
            extends _StaticValuesClause<C, T, _StandardValueStaticLeftParenSpec<C, T>>
            , _DynamicValuesClause<C, T, _InsertSpec> {

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
    interface _StandardValueDefaultSpec<C, T extends IDomain>
            extends _ColumnDefaultClause<C, T, _StandardValueDefaultSpec<C, T>>, _StandardValuesSpec<C, T> {

    }


    interface _StandardValueColumnsSpec<C, T extends IDomain>
            extends _ColumnListClause<C, T, _StandardValueDefaultSpec<C, T>>, _StandardValueDefaultSpec<C, T> {

    }

    interface _StandardValueChildInsertIntoClause<C, P extends IDomain> {

        <T extends IDomain> _StandardValueColumnsSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _StandardValueChildSpec<C, P extends IDomain>
            extends _ChildPartClause<_StandardValueChildInsertIntoClause<C, P>>
            , _InsertSpec {

    }

    interface _StandardParentStaticValuesSpec<C, P extends IDomain>
            extends _StaticValueLeftParenClause<C, P, _StandardParentStaticValuesSpec<C, P>>
            , _StandardValueChildSpec<C, P> {

    }


    interface _StandardParentValuesSpec<C, P extends IDomain>
            extends _StaticValuesClause<C, P, _StandardParentStaticValuesSpec<C, P>>
            , _DynamicValuesClause<C, P, _StandardValueChildSpec<C, P>> {

    }

    interface _StandardParentValueDefaultSpec<C, P extends IDomain>
            extends _ColumnDefaultClause<C, P, _StandardParentValueDefaultSpec<C, P>>
            , _StandardParentValuesSpec<C, P> {

    }

    interface _StandardParentValueColumnsSpec<C, P extends IDomain>
            extends _ColumnListClause<C, P, _StandardParentValueDefaultSpec<C, P>>
            , _StandardParentValueDefaultSpec<C, P> {

    }

    /**
     * @since 1.0
     */
    interface _StandardValueInsertIntoClause<C> {

        <T extends IDomain> _StandardValueColumnsSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _StandardParentValueColumnsSpec<C, T> insertInto(ParentTableMeta<T> table);

    }

    interface _StandardValuePreferLiteralSpec<C> extends _PreferLiteralClause<_StandardValueInsertIntoClause<C>>
            , _StandardValueInsertIntoClause<C> {

    }


    interface _StandardValueNullOptionSpec<C> extends _NullOptionClause<_StandardValuePreferLiteralSpec<C>>
            , _StandardValuePreferLiteralSpec<C> {

    }

    interface _StandardValueOptionSpec<C> extends _MigrationOptionClause<_StandardValueNullOptionSpec<C>>
            , _StandardValueNullOptionSpec<C> {

    }



    /*-------------------below standard sub query insert syntax interface -------------------*/


    interface _StandardSingleColumnsClause<C, T extends IDomain>
            extends _ColumnListClause<C, T, _SpaceSubQueryClause<C, _InsertSpec>> {

    }

    interface _StandardQueryChildInsertIntoClause<C, P extends IDomain> {

        <T extends IDomain> _StandardSingleColumnsClause<C, T> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _StandardQueryChildPartSpec<C, P extends IDomain>
            extends _ChildPartClause<_StandardQueryChildInsertIntoClause<C, P>>
            , _InsertSpec {

    }

    interface _StandardParentSubQueryClause<C, P extends IDomain>
            extends _SpaceSubQueryClause<C, _StandardQueryChildPartSpec<C, P>> {

    }

    interface _StandardParentColumnsClause<C, P extends IDomain>
            extends _ColumnListClause<C, P, _StandardParentSubQueryClause<C, P>> {

    }

    interface _StandardQueryInsertClause<C> {

        <T extends IDomain> _StandardSingleColumnsClause<C, T> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _StandardParentColumnsClause<C, T> insertInto(ParentTableMeta<T> table);

    }


}
