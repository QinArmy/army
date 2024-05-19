/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.standard.SQLs;
import io.army.mapping.MappingType;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PostgreStatement extends DialectStatement {


    interface _FuncColumnDefinitionSpaceClause {


        FuncColumnDefCommaClause space(String name, MappingType type);

    }


    interface _FuncColumnDefinitionParensClause<R> {

        R parens(Consumer<_FuncColumnDefinitionSpaceClause> consumer);
    }


    interface _FuncColumnDefinitionAsClause<R> extends _AsClause<_FuncColumnDefinitionParensClause<R>> {

    }


    interface _PostgreDynamicJoinCrossClause<JD> extends _DynamicJoinClause<PostgreJoins, JD>,
            _DynamicCrossJoinClause<PostgreCrosses, JD> {

    }


    interface _PostgreCrossClause<FT, FS> extends _CrossJoinModifierClause<FT, _AsClause<FS>> {

    }

    interface _PostgreJoinNestedClause<R extends Item> extends _JoinNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    interface _PostgreCrossNestedClause<R extends Item> extends _CrossJoinNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    interface _PostgreFromNestedClause<R extends Item> extends _FromNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    interface _PostgreUsingNestedClause<R extends Item> extends _UsingNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    interface _PostgreDynamicNestedClause<R extends Item>
            extends _DynamicTabularNestedClause<_NestedLeftParenSpec<R>, R> {

    }

    interface _PostgreUsingClause<FT, FS> extends _UsingModifierClause<FT, _AsClause<FS>> {

    }

    interface _PostgreFromUndoneFuncClause<R extends Item>
            extends _FromModifierUndoneFunctionClause<_FuncColumnDefinitionAsClause<R>> {


    }

    interface _PostgreUsingUndoneFuncClause<R extends Item>
            extends _UsingModifierUndoneFunctionClause<_FuncColumnDefinitionAsClause<R>> {


    }

    interface _PostgreJoinUndoneFuncClause<R extends Item>
            extends _JoinModifierUndoneFunctionClause<_FuncColumnDefinitionAsClause<R>> {


    }

    interface _PostgreCrossUndoneFuncClause<R extends Item>
            extends _CrossModifierUndoneFunctionClause<_FuncColumnDefinitionAsClause<R>> {

    }

    interface _PostgreNestedLeftParenUndoneFuncClause<R extends Item>
            extends _NestedLeftParenModifierUndoneFunctionClause<_FuncColumnDefinitionAsClause<R>> {

    }

    interface _PostgreTabularSpaceUndoneFuncClause<R extends Item>
            extends _DynamicTabularModifierUndoneFunctionClause<_FuncColumnDefinitionAsClause<R>> {

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

        R tableSample(BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                      BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument);

        <E> R tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                          BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier);

        R tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                      BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                      String keyName);

        R ifTableSample(Supplier<Expression> supplier); //TODO add function

        <E> R ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                            BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier);

        R ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                        BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                        String keyName);

    }


    interface _TableSampleClause<R> extends _StaticTableSampleClause<R> {

    }



    interface _PostgreNestedJoinClause<I extends Item>
            extends _JoinModifierClause<_NestedTableSampleOnSpec<I>, _AsClause<_NestedParensOnSpec<I>>>,
            _PostgreJoinUndoneFuncClause<_NestedOnSpec<I>>,
            _PostgreCrossClause<_NestedTableSampleCrossSpec<I>, _NestedParensCrossSpec<I>>,
            _PostgreCrossUndoneFuncClause<_NestedJoinSpec<I>>,
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

    interface _NestedParensOnSpec<I extends Item> extends _OptionalParensStringClause<_NestedOnSpec<I>>, _NestedOnSpec<I> {

    }

    interface _NestedParensCrossSpec<I extends Item> extends _OptionalParensStringClause<_NestedJoinSpec<I>>,
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

    interface _NestedParensJoinSpec<I extends Item> extends _OptionalParensStringClause<_PostgreNestedJoinClause<I>>,
            _PostgreNestedJoinClause<I> {

    }

    interface _NestedLeftParenSpec<I extends Item>
            extends _NestedLeftParenModifierClause<_NestedTableSampleJoinSpec<I>, _AsClause<_NestedParensJoinSpec<I>>>,
            _PostgreNestedLeftParenUndoneFuncClause<_PostgreNestedJoinClause<I>>,
            _LeftParenCteClause<_PostgreNestedJoinClause<I>>,
            _LeftParenNestedClause<_NestedLeftParenSpec<_PostgreNestedJoinClause<I>>, _PostgreNestedJoinClause<I>> {
    }


    interface _DynamicRepeatableOnSpec extends _RepeatableClause<_OnClause<_DynamicJoinSpec>>, _OnClause<_DynamicJoinSpec> {

    }

    interface _DynamicTableSampleOnSpec extends _TableSampleClause<_DynamicRepeatableOnSpec>,
            _OnClause<_DynamicJoinSpec> {

    }


    interface _DynamicJoinSpec
            extends _JoinModifierClause<_DynamicTableSampleOnSpec, _AsParensOnClause<_DynamicJoinSpec>>,
            _PostgreJoinUndoneFuncClause<_OnClause<_DynamicJoinSpec>>,
            _CrossJoinModifierClause<_DynamicTableSampleJoinSpec, _AsClause<_DynamicParensJoinSpec>>,
            _JoinCteClause<_OnClause<_DynamicJoinSpec>>,
            _CrossJoinCteClause<_DynamicJoinSpec>,
            _PostgreCrossUndoneFuncClause<_DynamicJoinSpec>,
            _PostgreJoinNestedClause<_OnClause<_DynamicJoinSpec>>,
            _PostgreCrossNestedClause<_DynamicJoinSpec>,
            _PostgreDynamicJoinCrossClause<_DynamicJoinSpec> {

    }

    interface _DynamicParensJoinSpec extends _OptionalParensStringClause<_DynamicJoinSpec>, _DynamicJoinSpec {

    }

    interface _DynamicTableRepeatableJoinSpec extends _RepeatableClause<_DynamicJoinSpec>, _DynamicJoinSpec {

    }

    interface _DynamicTableSampleJoinSpec extends _TableSampleClause<_DynamicTableRepeatableJoinSpec>,
            _DynamicJoinSpec {

    }


    interface _PostgreFromClause<FT, FS> extends _FromModifierClause<FT, _AsClause<FS>> {

    }


    interface _PostgreDynamicWithClause<WE extends Item> extends _DynamicWithClause<PostgreCtes, WE> {

    }


    interface _PostgreDynamicCteAsClause<T extends Item, R extends Item> extends _DynamicCteAsClause<T, R> {

        R as(@Nullable SQLs.WordMaterialized modifier, Function<T, R> function);

    }


}
