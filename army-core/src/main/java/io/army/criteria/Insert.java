package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

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
     * This interface representing the option prefer output literal when output domain column,but not contain
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


    interface _ColumnListClause<C, T, RR> {

        Statement._RightParenClause<RR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer);

        Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<FieldMeta<T>>> consumer);

        Statement._RightParenClause<RR> leftParen(FieldMeta<T> field);

        _StaticColumnDualClause<T, RR> leftParen(FieldMeta<T> field1, FieldMeta<T> field2);

    }


    interface _StaticColumnDualClause<T, IR> extends Statement._RightParenClause<IR> {

        Statement._RightParenClause<IR> comma(FieldMeta<T> field);

        _StaticColumnDualClause<T, IR> comma(FieldMeta<T> field1, FieldMeta<T> field2);

    }


    /**
     * @since 1.0
     */
    interface _ColumnDefaultClause<C, T, CR> {

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
    interface _DomainValueClause<C, P, VR> {

        <T extends P> VR value(T domain);

        <T extends P> VR value(Function<C, T> function);

        <T extends P> VR value(Supplier<T> supplier);

        <T extends P> VR values(List<T> domainList);

        <T extends P> VR values(Function<C, List<T>> function);

        <T extends P> VR values(Supplier<List<T>> supplier);

    }


    interface _StaticValuesClause<VR> {

        VR values();

    }

    interface _DynamicValuesClause<C, T, VR> {

        VR values(Consumer<PairsConstructor<T>> consumer);

        VR values(BiConsumer<C, PairsConstructor<T>> consumer);
    }


    interface _ChildPartClause<CR> {

        CR child();

    }


    interface _StaticValueLeftParenClause<C, T, RR> {

        _StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, Expression value);

        _StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, Supplier<?> supplier);

        _StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, Function<C, ?> function);

        _StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, Function<String, ?> function, String keyName);

        _StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, @Nullable Object value);

        _StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Supplier<?> supplier);

        _StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);


    }

    interface _StaticColumnValueClause<C, T, RR> extends Statement._RightParenClause<RR> {

        _StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, Expression value);

        _StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, Supplier<?> supplier);

        _StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, Function<C, ?> function);

        _StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, Function<String, ?> function, String keyName);

        _StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, @Nullable Object value);

        _StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Supplier<?> supplier);

        _StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

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
     */
    interface _AssignmentSetClause<C, T, SR> {

        SR setPair(Consumer<PairConsumer<T>> consumer);

        SR setPair(BiConsumer<C, PairConsumer<T>> consumer);

        SR set(FieldMeta<T> field, @Nullable Object value);

        SR setLiteral(FieldMeta<T> field, @Nullable Object value);

        SR setExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        SR setExp(FieldMeta<T> field, Function<C, ? extends Expression> function);

        SR ifSet(FieldMeta<T> field, Supplier<?> supplier);

        SR ifSet(FieldMeta<T> field, Function<C, ?> function);

        SR ifSet(FieldMeta<T> field, Function<String, ?> function, String keyName);

        SR ifSetLiteral(FieldMeta<T> field, Supplier<?> supplier);

        SR ifSetLiteral(FieldMeta<T> field, Function<C, ?> function);

        SR ifSetLiteral(FieldMeta<T> field, Function<String, ?> function, String keyName);


    }


    interface _CommaFieldValuePairClause<C, T, SR> {

        SR comma(FieldMeta<T> field, @Nullable Object value);

        SR commaLiteral(FieldMeta<T> field, @Nullable Object value);

        SR commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        SR commaExp(FieldMeta<T> field, Function<C, ? extends Expression> function);

    }


    interface _SpaceSubQueryClause<C, SR> {

        SR space(Supplier<? extends SubQuery> supplier);

        SR space(Function<C, ? extends SubQuery> function);
    }




    /*-------------------below standard domain insert syntax interface-------------------*/


    interface _StandardValueStaticLeftParenClause<C, T>
            extends _StaticValueLeftParenClause<C, T, _StandardValueStaticLeftParenSpec<C, T>> {

    }


    interface _StandardValueStaticLeftParenSpec<C, T>
            extends _StandardValueStaticLeftParenClause<C, T>, _InsertSpec {

    }

    interface _StandardValuesColumnDefaultSpec<C, T>
            extends _ColumnDefaultClause<C, T, _StandardValuesColumnDefaultSpec<C, T>>
            , _DomainValueClause<C, T, _InsertSpec>
            , _DynamicValuesClause<C, T, _InsertSpec>
            , _StaticValuesClause<_StandardValueStaticLeftParenClause<C, T>> {

    }


    interface _StandardInsertQuery extends StandardQuery, _InsertSpec {

    }

    interface _StandardComplexColumnDefaultSpec<C, T> extends _StandardValuesColumnDefaultSpec<C, T>
            , Insert._SpaceSubQueryClause<C, _InsertSpec> {

        StandardQuery._StandardSelectClause<C, _StandardInsertQuery> space();
    }

    interface _StandardColumnListSpec<C, T>
            extends _ColumnListClause<C, T, _StandardComplexColumnDefaultSpec<C, T>>
            , _StandardValuesColumnDefaultSpec<C, T> {

    }


    interface _StandardChildInsertIntoClause<C, P> {

        <T> _StandardColumnListSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _StandardChildSpec<C, P> extends _ChildPartClause<_StandardChildInsertIntoClause<C, P>>
            , _InsertSpec {

    }

    interface _StandardParentValueStaticLeftParenClause<C, P>
            extends _StaticValueLeftParenClause<C, P, _StandardParentValueStaticLeftParenSpec<C, P>> {

    }


    interface _StandardParentValueStaticLeftParenSpec<C, P>
            extends _StandardParentValueStaticLeftParenClause<C, P>, _StandardChildSpec<C, P> {

    }

    interface _StandardParentValuesColumnDefaultSpec<C, P>
            extends _ColumnDefaultClause<C, P, _StandardParentValuesColumnDefaultSpec<C, P>>
            , _DomainValueClause<C, P, _StandardChildSpec<C, P>>
            , _DynamicValuesClause<C, P, _StandardChildSpec<C, P>>
            , _StaticValuesClause<_StandardParentValueStaticLeftParenClause<C, P>> {

    }

    interface _StandardParentInsertQuery<C, P> extends StandardQuery, _StandardChildSpec<C, P> {

    }

    interface _StandardParentComplexColumnDefaultSpec<C, P> extends _StandardParentValuesColumnDefaultSpec<C, P>
            , Insert._SpaceSubQueryClause<C, _StandardChildSpec<C, P>> {

        StandardQuery._StandardSelectClause<C, _StandardParentInsertQuery<C, P>> space();
    }


    interface _StandardParentColumnListSpec<C, P>
            extends _ColumnListClause<C, P, _StandardParentComplexColumnDefaultSpec<C, P>>
            , _StandardParentValuesColumnDefaultSpec<C, P> {

    }


    interface _StandardDomainInsertIntoClause<C> {

        <T> _StandardColumnListSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T> _StandardParentColumnListSpec<C, T> insertInto(ParentTableMeta<T> table);
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


}
