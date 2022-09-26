package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

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
    interface _DomainValueClause<C, T, VR> {

        <TS extends T> VR value(TS domain);

        <TS extends T> VR value(Function<C, TS> function);

        <TS extends T> VR value(Supplier<TS> supplier);

        <TS extends T> VR values(List<TS> domainList);

        <TS extends T> VR values(Function<C, List<TS>> function);

        <TS extends T> VR values(Supplier<List<TS>> supplier);

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




}
