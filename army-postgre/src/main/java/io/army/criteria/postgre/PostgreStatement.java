package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
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

        RR repeatable(BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        RR ifRepeatable(Supplier<Expression> supplier);

        RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, @Nullable Number seedValue);

        RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, Supplier<Number> supplier);

        RR ifRepeatable(BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _TableSampleClause<TR> {

        TR tableSample(Expression method);

        TR tableSample(String methodName, Expression argument);

        TR tableSample(String methodName, Consumer<Consumer<Expression>> consumer);

        <E> TR tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method
                , BiFunction<MappingType, E, Expression> valueOperator, E argument);

        <E> TR tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method
                , BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier);

        TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        TR ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer);

        <E> TR ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method
                , BiFunction<MappingType, E, Expression> valueOperator, @Nullable E argument);

        <E> TR ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method
                , BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier);

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
        //TODO add nested function
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


    interface _CyclePathColumnClause<I extends Item> {

        _AsCteClause<I> using(String cyclePathColumnName);

    }

    interface _CycleToMarkValueSpec<I extends Item> extends _CyclePathColumnClause<I> {

        _CyclePathColumnClause<I> to(Expression cycleMarkValue, SQLs.WordDefault wordDefault, Expression cycleMarkDefault);

        _CyclePathColumnClause<I> to(Consumer<BiConsumer<Expression, Expression>> consumer);

        _CyclePathColumnClause<I> ifTo(Consumer<BiConsumer<Expression, Expression>> consumer);

    }

    interface _SetCycleMarkColumnClause<I extends Item> {

        _CycleToMarkValueSpec<I> set(String cycleMarkColumnName);
    }

    interface _CteCycleSpec<I extends Item> extends _AsCteClause<I> {

        _SetCycleMarkColumnClause<I> cycle(String columnName);

        _SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2);

        _SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2, String columnName3);

        _SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2, String columnName3, String columnName4);

        _SetCycleMarkColumnClause<I> cycle(Consumer<Consumer<String>> consumer);

        _SetCycleMarkColumnClause<I> ifCycle(Consumer<Consumer<String>> consumer);


    }


    interface _SetSearchSeqColumnClause<I extends Item> {

        _CteCycleSpec<I> set(String searchSeqColumnName);

    }

    interface _SearchFirstByClause<I extends Item> {

        _SetSearchSeqColumnClause<I> firstBy(String columnName);

        _SetSearchSeqColumnClause<I> firstBy(String columnName1, String columnName2);

        _SetSearchSeqColumnClause<I> firstBy(String columnName1, String columnName2, String columnName3);

        _SetSearchSeqColumnClause<I> firstBy(String columnName1, String columnName2, String columnName3, String columnName4);

        _SetSearchSeqColumnClause<I> firstBy(Consumer<Consumer<String>> consumer);

    }

    interface _CteSearchSpec<I extends Item> extends _CteCycleSpec<I> {

        _SearchFirstByClause<I> searchBreadth();

        _SearchFirstByClause<I> searchDepth();

        _SearchFirstByClause<I> searchBreadth(BooleanSupplier predicate);

        _SearchFirstByClause<I> searchDepth(BooleanSupplier predicate);

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticCteComplexCommandSpec<I extends Item>
            extends PostgreQuery._PostgreSelectClause<_CteSearchSpec<I>>
            , PostgreInsert._StaticSubOptionSpec<_AsCteClause<I>>
            , PostgreUpdate._SingleUpdateClause<_AsCteClause<I>, _AsCteClause<I>>
            , PostgreDelete._SingleDeleteClause<_AsCteClause<I>, _AsCteClause<I>> {

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


    interface _AsCommandClause<I extends Item> extends Item {

        I asCommand();
    }


}
