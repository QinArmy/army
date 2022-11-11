package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.function.BiFunction;
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
public interface Insert extends DmlStatement, Statement.DmlInsert {


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
    @Deprecated
    interface _InsertSpec extends _DmlInsertClause<Insert> {

    }


    interface _ConflictUpdateCommaItemClause<T, UR> {

        UR comma(FieldMeta<T> field, Expression value);

        UR comma(FieldMeta<T> field, Supplier<Expression> supplier);

        <E> UR comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, @Nullable E value);

        <E> UR comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier);

        UR comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <E> UR comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator, BiFunction<FieldMeta<T>, E, Expression> valueOperator, @Nullable E value);

        <E> UR comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator, BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier);

        UR comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator, BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function, String keyName);
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
        PO literalMode(LiteralMode mode);
    }

    interface _MigrationOptionClause<OR> {

        OR migration(boolean migration);
    }

    /**
     * @since 1.0
     */
    interface _NullOptionClause<OR> {

        OR nullMode(NullMode mode);
    }


    interface _ColumnListClause<T, RR> {

        Statement._RightParenClause<RR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer);

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
    interface _ColumnDefaultClause<T, CR> {

        CR defaultValue(FieldMeta<T> field, Expression value);

        CR defaultValue(FieldMeta<T> field, Supplier<Expression> supplier);

        CR defaultValue(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E> CR defaultValue(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> operator, @Nullable E value);

        CR defaultValue(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> operator, Function<String, ?> function, String keyName);

    }


    /**
     * @since 1.0
     */
    interface _DomainValueClause<T, VR> {

        <TS extends T> VR value(TS domain);

        <TS extends T> VR value(Supplier<TS> supplier);

        <TS extends T> VR values(List<TS> domainList);

        <TS extends T> VR values(Supplier<List<TS>> supplier);

    }


    interface _StaticValuesClause<VR> {

        VR values();

    }

    interface _DynamicValuesClause<T, VR> {

        VR values(Consumer<ValuesConstructor<T>> consumer);

    }


    interface _ChildPartClause<CR> {

        CR child();

    }

    interface _ParentInsert<CT extends Item> extends Insert, _ChildPartClause<CT> {

    }


    interface _StaticValueLeftParenClause<T, RR> {

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, Expression value);

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function);

        <E> _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, @Nullable E value);

        <E> _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier);

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);


    }

    interface _StaticColumnValueClause<T, RR> extends Statement._RightParenClause<RR> {

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, Expression value);

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function);

        <E> _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, @Nullable E value);

        <E> _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier);

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

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
    interface _StaticAssignmentSetClause<T, SR> {

        SR set(FieldMeta<T> field, Expression value);

        SR set(FieldMeta<T> field, Supplier<Expression> supplier);

        SR set(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E> SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, @Nullable E value);

        <E> SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier);

        SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function, String keyName);


        SR ifSet(FieldMeta<T> field, Supplier<Expression> supplier);

        SR ifSet(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        <E> SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, @Nullable E value);

        <E> SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier);

        SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

    }


    interface _DynamicAssignmentSetClause<T, SD> {

        SD set(Consumer<Assignments<T>> consumer);

        SD ifSet(Consumer<Assignments<T>> consumer);
    }


    interface _CommaFieldValuePairClause<T, SR> {

        SR comma(FieldMeta<T> field, @Nullable Object value);

        SR commaLiteral(FieldMeta<T> field, @Nullable Object value);

        SR commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);


    }


}
