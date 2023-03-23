package io.army.criteria;

import io.army.criteria.dialect.SubQuery;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing primary INSERT statement.This interface is base interface of below:
 *     <ul>
 *         <li>{@link Insert}</li>
 *         <li>{@link io.army.criteria.dialect.ReturningInsert}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface InsertStatement extends DmlStatement {


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
    interface _InsertSpec extends _DmlInsertClause<InsertStatement> {

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


    interface _StaticColumnDualClause<T, IR> extends Statement._RightParenClause<IR> {

        Statement._RightParenClause<IR> comma(FieldMeta<T> field);

        _StaticColumnDualClause<T, IR> comma(FieldMeta<T> field1, FieldMeta<T> field2);

    }


    interface _StaticColumnQuadraClause<T, IR> extends Statement._RightParenClause<IR> {

        Statement._RightParenClause<IR> comma(FieldMeta<T> field);

        Statement._RightParenClause<IR> comma(FieldMeta<T> field1, FieldMeta<T> field2);

        Statement._RightParenClause<IR> comma(FieldMeta<T> field1, FieldMeta<T> field2, FieldMeta<T> field3);

        _StaticColumnQuadraClause<T, IR> comma(FieldMeta<T> field1, FieldMeta<T> field2, FieldMeta<T> field3,
                                               FieldMeta<T> field4);

    }


    interface _QueryInsertSpaceClause<T extends Item, R extends Item> {

        T space();

        R space(Function<T, R> function);

        R space(Supplier<SubQuery> supplier);

    }


    interface _ColumnListClause<T, R> {

        Statement._RightParenClause<R> leftParen(FieldMeta<T> field);

        _StaticColumnDualClause<T, R> leftParen(FieldMeta<T> field1, FieldMeta<T> field2);

        Statement._RightParenClause<R> leftParen(FieldMeta<T> field1, FieldMeta<T> field2, FieldMeta<T> field3);

        _StaticColumnQuadraClause<T, R> leftParen(FieldMeta<T> field1, FieldMeta<T> field2, FieldMeta<T> field3,
                                                  FieldMeta<T> field4);

        R parens(Consumer<Consumer<FieldMeta<T>>> consumer);

    }


    /**
     * @since 1.0
     */
    interface _ColumnDefaultClause<T, CR> {

        CR defaultValue(FieldMeta<T> field, Expression value);

        CR defaultValue(FieldMeta<T> field, Supplier<Expression> supplier);

        CR defaultValue(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        CR defaultValue(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, Expression> operator,
                        Expression expression);

        CR defaultValue(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> operator,
                        @Nullable Object value);

        <E> CR defaultValue(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> operator, Supplier<E> supplier);

        CR defaultValue(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> operator,
                        Function<String, ?> function, String keyName);

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

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field,
                                                  BiFunction<FieldMeta<T>, Expression, Expression> operator,
                                                  Expression expression);

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field,
                                                  BiFunction<FieldMeta<T>, Object, Expression> operator,
                                                  @Nullable Object value);


        <E> _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field,
                                                      BiFunction<FieldMeta<T>, E, Expression> operator,
                                                      Supplier<E> supplier);

        _StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field,
                                                  BiFunction<FieldMeta<T>, Object, Expression> operator,
                                                  Function<String, ?> function, String keyName);


    }

    interface _StaticColumnValueClause<T, RR> extends Statement._RightParenClause<RR> {

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, Expression value);

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, Supplier<Expression> supplier);

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function);

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field,
                                              BiFunction<FieldMeta<T>, Expression, Expression> operator,
                                              Expression expression);

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field,
                                              BiFunction<FieldMeta<T>, Object, Expression> operator,
                                              @Nullable Object value);


        <E> _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> operator, Supplier<E> supplier);

        _StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> operator, Function<String, ?> function, String keyName);

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

        SD sets(Consumer<Assignments<T>> consumer);

        SD ifSets(Consumer<Assignments<T>> consumer);
    }


    interface _CommaFieldValuePairClause<T, SR> {

        SR comma(FieldMeta<T> field, @Nullable Object value);

        SR commaLiteral(FieldMeta<T> field, @Nullable Object value);

        SR commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);


    }


}
