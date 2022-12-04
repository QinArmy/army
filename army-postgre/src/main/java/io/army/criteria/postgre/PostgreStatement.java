package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.impl.SQLs;
import io.army.mapping.MappingType;

import java.util.function.*;

public interface PostgreStatement extends DialectStatement {


    @Deprecated
    interface _PostgreJoinClause<JT, JS> extends _JoinModifierClause<JT, JS>
            , _JoinCteClause<JS> {

    }

    @Deprecated
    interface _PostgreCrossJoinClause<FT, FS> extends _CrossJoinModifierClause<FT, FS>
            , _CrossJoinCteClause<FS> {

    }

    interface _PostgreDynamicJoinCrossClause<JD> extends _DynamicJoinClause<PostgreJoins, JD>,
            _DynamicCrossJoinClause<PostgreCrosses, JD> {

    }

    @Deprecated
    interface _PostgreDynamicCrossJoinClause<JD> extends _DynamicCrossJoinClause<PostgreCrosses, JD> {

    }

    interface _PostgreCrossClause<FT, FS> extends _CrossJoinModifierClause<FT, _AsClause<FS>> {

    }

    interface _PostgreJoinNestedClause<JN extends Item> extends _JoinNestedClause<_NestedLeftParenSpec<JN>> {

    }

    interface _PostgreCrossNestedClause<CN extends Item> extends _CrossJoinNestedClause<_NestedLeftParenSpec<CN>> {

    }

    interface _PostgreFromNestedClause<FN extends Item> extends _FromNestedClause<_NestedLeftParenSpec<FN>> {

    }


    interface _RepeatableClause<R> {

        R repeatable(Expression seed);

        R repeatable(Supplier<Expression> supplier);

        R repeatable(Function<Number, Expression> valueOperator, Number seedValue);

        <E extends Number> R repeatable(Function<E, Expression> valueOperator, Supplier<E> supplier);

        R repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        R ifRepeatable(Supplier<Expression> supplier);

        <E extends Number> R ifRepeatable(Function<E, Expression> valueOperator, Supplier<E> supplier);

        R ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _StaticTableSampleClause<R> {

        R tableSample(Expression method);

        R tableSample(String methodName, Expression argument);

        R tableSample(String methodName, Consumer<Consumer<Expression>> consumer);

        R tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                      BiFunction<MappingType, Object, Expression> valueOperator, Object argument);

        R tableSample(BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                      BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument);

        <E> R tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                          BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier);

        R tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                      BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                      String keyName);

        R ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer);

        <E> R ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                            BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier);

        R ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                        String keyName);


    }


    interface _TableSampleClause<R> extends _StaticTableSampleClause<R> {

    }


    interface _CteMaterializedClause<R> {

        R materialized();

        R notMaterialized();

        R ifMaterialized(BooleanSupplier predicate);

        R ifNotMaterialized(BooleanSupplier predicate);
    }

    interface _PostgreNestedJoinClause<I extends Item>
            extends _JoinModifierClause<_NestedTableSampleOnSpec<I>, _AsClause<_NestedParensOnSpec<I>>>,
            _PostgreCrossClause<_NestedTableSampleCrossSpec<I>, _NestedParensCrossSpec<I>>,
            _JoinCteClause<_NestedOnSpec<I>>,
            _CrossJoinCteClause<_NestedJoinSpec<I>>,
            _PostgreJoinNestedClause<_NestedOnSpec<I>>,
            _PostgreCrossNestedClause<_NestedJoinSpec<I>>,
            _PostgreDynamicJoinCrossClause<_NestedJoinSpec<I>> {

    }


    interface _NestedJoinSpec<I extends Item> extends _PostgreNestedJoinClause<I>, _RightParenClause<I> {

    }

    interface _NestedOnSpec<I extends Item> extends _OnClause<_NestedJoinSpec<I>>, _NestedJoinSpec<I> {

    }

    interface _NestedParensOnSpec<I extends Item> extends _ParensStringClause<_NestedOnSpec<I>>, _NestedOnSpec<I> {

    }

    interface _NestedParensCrossSpec<I extends Item> extends _ParensStringClause<_NestedJoinSpec<I>>,
            _NestedJoinSpec<I> {

    }

    interface _NestedRepeatableOnClause<I extends Item> extends _RepeatableClause<_NestedOnSpec<I>>,
            _NestedOnSpec<I> {

    }

    interface _NestedTableSampleOnSpec<I extends Item> extends _TableSampleClause<_NestedRepeatableOnClause<I>>,
            _NestedOnSpec<I> {

    }


    interface _NestedRepeatableCrossClause<I extends Item> extends _RepeatableClause<_NestedJoinSpec<I>>,
            _NestedJoinSpec<I> {

    }

    interface _NestedTableSampleCrossSpec<I extends Item>
            extends _TableSampleClause<_NestedRepeatableCrossClause<I>>, _NestedJoinSpec<I> {

    }

    interface _NestedRepeatableJoinClause<I extends Item> extends _RepeatableClause<_PostgreNestedJoinClause<I>>,
            _PostgreNestedJoinClause<I> {

    }

    interface _NestedTableSampleJoinSpec<I extends Item>
            extends _TableSampleClause<_NestedRepeatableJoinClause<I>>, _PostgreNestedJoinClause<I> {

    }

    interface _NestedParensJoinSpec<I extends Item> extends _ParensStringClause<_PostgreNestedJoinClause<I>>,
            _PostgreNestedJoinClause<I> {

    }

    interface _NestedLeftParenSpec<I extends Item>
            extends _NestedLeftParenModifierClause<_NestedTableSampleJoinSpec<I>, _AsClause<_NestedParensJoinSpec<I>>>,
            _LeftParenCteClause<_PostgreNestedJoinClause<I>>,
            _LeftParenClause<_NestedLeftParenSpec<_PostgreNestedJoinClause<I>>> {
    }


    interface _DynamicRepeatableOnSpec extends _RepeatableClause<_OnClause<_DynamicJoinSpec>>, _OnClause<_DynamicJoinSpec> {

    }

    interface _DynamicTableSampleOnSpec extends _TableSampleClause<_DynamicRepeatableOnSpec>,
            _OnClause<_DynamicJoinSpec> {

    }


    interface _DynamicJoinSpec
            extends _JoinModifierClause<_DynamicTableSampleOnSpec, _AsParensOnClause<_DynamicJoinSpec>>,
            _CrossJoinModifierClause<_DynamicTableSampleJoinSpec, _AsClause<_DynamicParensJoinSpec>>,
            _JoinCteClause<_OnClause<_DynamicJoinSpec>>,
            _CrossJoinCteClause<_DynamicJoinSpec>,
            _PostgreJoinNestedClause<_OnClause<_DynamicJoinSpec>>,
            _PostgreCrossNestedClause<_DynamicJoinSpec>,
            _PostgreDynamicJoinCrossClause<_DynamicJoinSpec> {

    }

    interface _DynamicParensJoinSpec extends _ParensStringClause<_DynamicJoinSpec>, _DynamicJoinSpec {

    }

    interface _DynamicTableRepeatableJoinSpec extends _RepeatableClause<_DynamicJoinSpec>, _DynamicJoinSpec {

    }

    interface _DynamicTableSampleJoinSpec extends _TableSampleClause<_DynamicTableRepeatableJoinSpec>,
            _DynamicJoinSpec {

    }


    interface _PostgreFromClause<FT, FS> extends _FromModifierClause<FT, _AsClause<FS>> {

    }


    interface _PostgreDynamicWithClause<SR> extends _DynamicWithClause<PostgreCtes, SR> {

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


    interface _StaticCteSelectSpec<I extends Item>
            extends PostgreQuery._PostgreSelectClause<I>
            , _LeftParenClause<_StaticCteSelectSpec<_RightParenClause<PostgreQuery._UnionOrderBySpec<I>>>> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticCteComplexCommandSpec<I extends Item>
            extends _StaticCteSelectSpec<_CteSearchSpec<I>>
            , PostgreInsert._StaticSubOptionSpec<_AsCteClause<I>>
            , PostgreUpdate._SingleUpdateClause<_AsCteClause<I>, _AsCteClause<I>>
            , PostgreDelete._SingleDeleteClause<_AsCteClause<I>, _AsCteClause<I>>
            , PostgreValues._PostgreValuesClause<_AsCteClause<I>> {

    }


    interface _StaticCteMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_StaticCteComplexCommandSpec<I>>
            , _StaticCteComplexCommandSpec<I> {

    }

    interface _StaticCteAsClause<I extends Item>
            extends Statement._StaticAsClaus<_StaticCteMaterializedSpec<I>> {

    }

    interface _StaticCteLeftParenSpec<I extends Item>
            extends Statement._LeftParenStringQuadraSpec<_StaticCteAsClause<I>>,
            _StaticCteAsClause<I> {

    }

    interface _PostgreStaticWithClause<I extends Item> extends _StaticWithClause<_StaticCteLeftParenSpec<I>> {

    }

    interface _PostgreStaticCteCommaClause<I extends Item> extends _StaticWithCommaClause<_StaticCteLeftParenSpec<I>> {

    }


}
