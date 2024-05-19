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

package io.army.criteria;

import io.army.criteria.dialect.Returnings;
import io.army.criteria.standard.SQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DialectStatement extends Statement {


    interface _StaticReturningCommaClause<R extends Item> extends Item {

        R comma(Selection selection);

        R comma(Selection selection1, Selection selection2);

        R comma(Function<String, Selection> function, String alias);

        R comma(Function<String, Selection> function1, String alias1,
                Function<String, Selection> function2, String alias2);

        R comma(Function<String, Selection> function, String alias, Selection selection);

        R comma(Selection selection, Function<String, Selection> function, String alias);

        R comma(TableField field1, TableField field2, TableField field3);

        R comma(TableField field1, TableField field2, TableField field3, TableField field4);
    }

    interface _StaticDmlReturningCommaClause<R extends Item> extends _StaticReturningCommaClause<R> {

        R comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);

        R comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> R comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                    String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);
    }


    interface _StaticInsertReturningCommaClause<R extends Item> extends _StaticReturningCommaClause<R> {

        R comma(TableMeta<?> insertTable);
    }

    interface _StaticReturningClause<R extends Item> {

        R returning(Selection selection);

        R returning(Selection selection1, Selection selection2);

        R returning(Function<String, Selection> function, String alias);

        R returning(Function<String, Selection> function1, String alias1,
                    Function<String, Selection> function2, String alias2);

        R returning(Function<String, Selection> function, String alias, Selection selection);

        R returning(Selection selection, Function<String, Selection> function, String alias);

        R returning(TableField field1, TableField field2, TableField field3);

        R returning(TableField field1, TableField field2, TableField field3, TableField field4);
    }


    interface _StaticDmlReturningClause<R extends Item> extends _StaticReturningClause<R> {

        R returning(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);

        R returning(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> R returning(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                        String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);
    }


    interface _StaticInsertReturningClause<R extends Item> extends _StaticReturningClause<R> {

        R returning(TableMeta<?> insertTable);
    }


    interface _DynamicReturningClause<R> {

        R returningAll();

        R returning(Consumer<Returnings> consumer);

    }




    interface _WhereCurrentOfClause<R> {

        R whereCurrentOf(String cursorName);
    }


    /**
     * <p>
     * This interface representing STRAIGHT JOIN clause
     * * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * *
     *
     * @param <JT> same with JT with the JT of {@link _JoinClause}
     * @param <JS> same with JT with the JS of {@link _JoinClause}
     * @see _CrossJoinClause
     * @since 0.6.0
     */
    interface _StraightJoinClause<JT, JS> {

        JT straightJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JS straightJoin(DerivedTable derivedTable);

        <T extends DerivedTable> JS straightJoin(Supplier<T> supplier);


    }

    interface _StraightJoinModifierTabularClause<JT, JS> extends _StraightJoinClause<JT, JS> {

        JS straightJoin(@Nullable SQLs.DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> JS straightJoin(@Nullable SQLs.DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _StraightJoinUndoneFunctionClause<R> {

        R straightJoin(UndoneFunction func);

    }

    interface _StraightJoinModifierUndoneFunctionClause<R> extends _StraightJoinUndoneFunctionClause<R> {

        R straightJoin(@Nullable SQLs.DerivedModifier modifier, UndoneFunction func);

    }


    /**
     * <p>
     * This interface representing dialect join clause.
     * * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * *
     *
     * @param <JP> next clause java type
     * @since 0.6.0
     */
    interface _DialectJoinClause<JP> {

        JP leftJoin(TableMeta<?> table);

        JP join(TableMeta<?> table);

        JP rightJoin(TableMeta<?> table);

        JP fullJoin(TableMeta<?> table);

    }

    /**
     * <p>
     * This interface representing dialect STRAIGHT JOIN clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @param <JP> same with the JP of {@link _DialectJoinClause}
     * @see _DialectJoinClause
     * @since 0.6.0
     */
    interface _DialectStraightJoinClause<JP> {

        JP straightJoin(TableMeta<?> table);

    }


    /**
     * <p>This interface representing dialect CROSS JOIN clause.
     *  <p><strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *
     * @since 0.6.0
     */
    interface _DialectCrossJoinClause<FP> {

        FP crossJoin(TableMeta<?> table);


    }


    /**
     * <p>
     * This interface representing STRAIGHT JOIN CTE clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @param <JS> same with the JS of {@link _JoinClause}
     * @since 0.6.0
     */
    interface _StraightJoinCteClause<JS> {

        JS straightJoin(String cteName);

        JS straightJoin(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _StraightJoinModifierCteClause<JS> extends _StraightJoinCteClause<JS> {

        JS straightJoin(SQLs.DerivedModifier modifier, String cteName);

        JS straightJoin(SQLs.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

    }


    /**
     * <p>
     * This interface representing dialect left bracket clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @param <LP> next clause java type
     * @since 0.6.0
     */
    interface _DialectLeftParenClause<LP> {

        LP leftParen(TableMeta<?> table);
    }

    /**
     * <p>
     * This interface representing  left bracket cte clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @param <R> same with the LS of {@link _NestedLeftParenClause}
     * @since 0.6.0
     */
    interface _LeftParenCteClause<R> {

        R leftParen(String cteName);

        R leftParen(String cteName, SQLs.WordAs wordAs, String alias);
    }


}
