package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;

import java.util.function.*;

public interface PostgreStatement extends DialectStatement {


    interface _PostgreJoinClause<JT, JS> extends _JoinModifierClause<JT, JS>
            , _JoinCteClause<JS> {

    }

    interface _PostgreCrossJoinClause<FT, FS> extends _CrossJoinModifierClause<FT, FS>
            , _CrossJoinCteClause<FS> {

    }

    interface _PostgreDynamicJoinClause<JD> extends _DynamicJoinClause<PostgreJoins, JD> {

    }

    interface _PostgreDynamicCrossJoinClause<JD> extends _DynamicCrossJoinClause<PostgreCrosses, JD> {

    }


    interface _RepeatableClause<RR> {

        RR repeatable(Expression seed);

        RR repeatable(Supplier<Expression> supplier);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Number seedValue);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Supplier<Number> supplier);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Function<String, ?> function, String keyName);

        RR ifRepeatable(Supplier<Expression> supplier);

        RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, @Nullable Number seedValue);

        RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, Supplier<Number> supplier);

        RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _TableSampleClause<TR> {

        TR tableSample(String methodName, Consumer<Consumer<Expression>> consumer);

        <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, T argument);

        <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier);

        TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        TR ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer);

        <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, T argument);

        <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier);

        TR ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }


    interface _CteMaterializedClause<R> {

        R materialized();

        R notMaterialized();

        R ifMaterialized(BooleanSupplier predicate);

        R ifNotMaterialized(BooleanSupplier predicate);
    }


    interface _PostgreNestedJoinClause<I extends Item>
            extends _PostgreJoinClause<_NestedTableSampleOnSpec<I>, _NestedOnSpec<I>>
            , _PostgreCrossJoinClause<_NestedTableSampleCrossSpec<I>, _NestedJoinSpec<I>>
            , _JoinNestedClause<_NestedLeftParenSpec<_NestedOnSpec<I>>>
            , _CrossJoinNestedClause<_NestedLeftParenSpec<_NestedJoinSpec<I>>>
            , _PostgreDynamicJoinClause<_NestedJoinSpec<I>>
            , _PostgreDynamicCrossJoinClause<_NestedJoinSpec<I>> {

    }


    interface _NestedJoinSpec<I extends Item> extends _PostgreNestedJoinClause<I>
            , _RightParenClause<I> {

    }

    interface _NestedOnSpec<I extends Item> extends _OnClause<_NestedJoinSpec<I>>, _NestedJoinSpec<I> {

    }

    interface _NestedRepeatableOnClause<I extends Item> extends _RepeatableClause<_NestedOnSpec<I>>
            , _NestedOnSpec<I> {

    }

    interface _NestedTableSampleOnSpec<I extends Item> extends _TableSampleClause<_NestedRepeatableOnClause<I>>
            , _NestedOnSpec<I> {

    }


    interface _NestedRepeatableCrossClause<I extends Item> extends _RepeatableClause<_NestedJoinSpec<I>>
            , _NestedJoinSpec<I> {

    }

    interface _NestedTableSampleCrossSpec<I extends Item> extends _TableSampleClause<_NestedRepeatableCrossClause<I>>
            , _NestedJoinSpec<I> {

    }

    interface _NestedRepeatableJoinClause<I extends Item> extends _RepeatableClause<_PostgreNestedJoinClause<I>>
            , _PostgreNestedJoinClause<I> {

    }

    interface _NestedTableSampleJoinSpec<I extends Item> extends _TableSampleClause<_NestedRepeatableJoinClause<I>>
            , _PostgreNestedJoinClause<I> {

    }


    interface _PostgreNestedLeftParenClause<LT, LS> extends _LeftParenModifierClause<LT, LS>
            , _LeftParenCteClause<LS> {

    }

    interface _NestedLeftParenSpec<I extends Item>
            extends _PostgreNestedLeftParenClause<_NestedTableSampleJoinSpec<I>, _PostgreNestedJoinClause<I>>
            , Query._LeftParenClause<_NestedLeftParenSpec<_PostgreNestedJoinClause<I>>> {

    }


    interface _DynamicRepeatableOnSpec extends _RepeatableClause<_OnClause<_DynamicJoinSpec>>
            , _OnClause<_DynamicJoinSpec> {

    }

    interface _DynamicTableSampleOnSpec extends _TableSampleClause<_DynamicRepeatableOnSpec>
            , _OnClause<_DynamicJoinSpec> {

    }


    interface _DynamicJoinSpec
            extends _PostgreJoinClause<_DynamicTableSampleOnSpec, _OnClause<_DynamicJoinSpec>>
            , _PostgreCrossJoinClause<_DynamicTableSampleJoinSpec, _DynamicJoinSpec>
            , _JoinNestedClause<_NestedLeftParenSpec<_OnClause<_DynamicJoinSpec>>>
            , _CrossJoinNestedClause<_NestedLeftParenSpec<_DynamicJoinSpec>>
            , _PostgreDynamicJoinClause<_DynamicJoinSpec>
            , _PostgreDynamicCrossJoinClause<_DynamicJoinSpec> {

    }

    interface _DynamicTableRepeatableJoinSpec extends _RepeatableClause<_DynamicJoinSpec>
            , _DynamicJoinSpec {

    }

    interface _DynamicTableSampleJoinSpec extends _TableSampleClause<_DynamicTableRepeatableJoinSpec>
            , _DynamicJoinSpec {

    }


    interface _PostgreFromClause<FT, FS> extends _FromModifierClause<FT, FS>
            , _FromCteClause<FS> {
        //TODO add dialect function tabular
    }


    interface _PostgreDynamicWithClause<SR>
            extends _DynamicWithClause<PostgreCteBuilder, SR> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticCteComplexCommandSpec<I extends Item>
            extends PostgreQuery._PostgreSelectClause<PostgreQuery._CteSearchSpec<I>>
            , PostgreInsert._StaticSubOptionSpec<_AsCteClause<I>> {

    }


    interface _StaticCteMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_StaticCteComplexCommandSpec<I>>
            , _StaticCteComplexCommandSpec<I> {

    }

    interface _StaticCteAsClause<I extends Item>
            extends Statement._StaticAsClaus<_StaticCteMaterializedSpec<I>> {

    }

    interface _StaticCteLeftParenSpec<I extends Item>
            extends Statement._LeftParenStringQuadraSpec<_StaticCteAsClause<I>>
            , _StaticCteAsClause<I> {

    }


}
