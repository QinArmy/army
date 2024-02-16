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

import io.army.criteria.*;
import io.army.criteria.dialect.ReturningInsert;
import io.army.meta.*;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing postgre INSERT statement.
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-insert.html">Postgre INSERT syntax</a>
 * @since 0.6.0
 */
public interface PostgreInsert extends PostgreStatement {

    interface _OverridingValueClause<R> {

        R overridingSystemValue();

        R overridingUserValue();

        R ifOverridingSystemValue(BooleanSupplier predicate);

        R ifOverridingUserValue(BooleanSupplier predicate);
    }

    interface _StaticReturningCommaSpec<Q extends Item>
            extends _StaticInsertReturningCommaClause<_StaticReturningCommaSpec<Q>>,
            _DqlInsertClause<Q> {

    }

    interface _ReturningSpec<I extends Item, Q extends Item>
            extends _StaticInsertReturningClause<_StaticReturningCommaSpec<Q>>,
            _DynamicReturningClause<_DqlInsertClause<Q>>,
            _DmlInsertClause<I> {

    }


    interface _DoUpdateWhereAndSpec<I extends Item, Q extends Item>
            extends UpdateStatement._UpdateWhereAndClause<_DoUpdateWhereAndSpec<I, Q>>,
            _ReturningSpec<I, Q> {

    }

    interface _DoUpdateWhereClause<I extends Item, Q extends Item>
            extends _WhereClause<_ReturningSpec<I, Q>, _DoUpdateWhereAndSpec<I, Q>>,
            _ReturningSpec<I, Q> {

    }


    interface _DoUpdateSetClause<T, I extends Item, Q extends Item>
            extends UpdateStatement._StaticRowSetClause<FieldMeta<T>, _DoUpdateWhereSpec<T, I, Q>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._RowPairs<FieldMeta<T>>, _DoUpdateWhereClause<I, Q>> {

    }

    interface _DoUpdateWhereSpec<T, I extends Item, Q extends Item>
            extends _DoUpdateWhereClause<I, Q>,
            _DoUpdateSetClause<T, I, Q> {

    }

    interface _ConflictDoNothingClause<I extends Item, Q extends Item> extends _DoNothingClause<_ReturningSpec<I, Q>> {


    }


    interface _ConflictActionClause<T, I extends Item, Q extends Item> extends _ConflictDoNothingClause<I, Q> {

        _DoUpdateSetClause<T, I, Q> doUpdate();

    }


    interface _ConflictTargetWhereAndSpec<T, I extends Item, Q extends Item>
            extends Statement._WhereAndClause<_ConflictTargetWhereAndSpec<T, I, Q>>,
            _ConflictActionClause<T, I, Q> {

    }

    interface _ConflictTargetWhereSpec<T, I extends Item, Q extends Item>
            extends _ConflictActionClause<T, I, Q>,
            Statement._WhereClause<_ConflictActionClause<T, I, Q>, _ConflictTargetWhereAndSpec<T, I, Q>> {

    }


    interface ConflictTargetCommaClause<T> {

        _ConflictCollateSpec<T> comma(IndexFieldMeta<T> indexColumn);

        _ConflictCollateSpec<T> comma(Expression indexExpression);
    }


    interface _ConflictOpClassSpec<T> extends ConflictTargetCommaClause<T> {

        /**
         * @param operatorClass operator class
         * @see <a href="https://www.postgresql.org/docs/current/brin-builtin-opclasses.html">Built-in BRIN Operator Classes</a>
         */
        ConflictTargetCommaClause<T> space(String operatorClass);

        /**
         * @param supplier provide operator class
         * @see <a href="https://www.postgresql.org/docs/current/brin-builtin-opclasses.html">Built-in BRIN Operator Classes</a>
         */
        ConflictTargetCommaClause<T> ifSpace(Supplier<String> supplier);
    }


    interface _ConflictCollateSpec<T> extends _ConflictOpClassSpec<T> {


        /**
         * @see <a href="https://www.postgresql.org/docs/16/collation.html">collation</a>
         */
        _ConflictOpClassSpec<T> collation(String collationName);

        /**
         *@see <a href="https://www.postgresql.org/docs/16/collation.html">collation</a>
         */
        _ConflictOpClassSpec<T> collation(Supplier<String> supplier);

        /**
         *@see <a href="https://www.postgresql.org/docs/16/collation.html">collation</a>
         */
        _ConflictOpClassSpec<T> ifCollation(Supplier<String> supplier);

    }


    interface _ConflictTargetOptionSpaceClause<T> {

        _ConflictCollateSpec<T> space(IndexFieldMeta<T> indexColumn);

        _ConflictCollateSpec<T> space(Expression indexExpression);

    }


    interface _ConflictTargetOptionSpec<T, I extends Item, Q extends Item>
            extends _ConflictDoNothingClause<I, Q> {

        _ConflictTargetWhereSpec<T, I, Q> parens(Consumer<_ConflictTargetOptionSpaceClause<T>> consumer);

        _ConflictActionClause<T, I, Q> onConstraint(String constraintName);

    }


    interface _OnConflictSpec<T, I extends Item, Q extends Item> extends _ReturningSpec<I, Q> {

        _ConflictTargetOptionSpec<T, I, Q> onConflict();


    }



    /*-------------------below insert syntax interfaces  -------------------*/

    interface _PostgreValuesStaticParensClause<T, I extends Item, Q extends Item>
            extends InsertStatement._ValuesParensClause<T, _PostgreValuesStaticParensCommaSpec<T, I, Q>> {

    }

    interface _PostgreValuesStaticParensCommaSpec<T, I extends Item, Q extends Item>
            extends _CommaClause<_PostgreValuesStaticParensClause<T, I, Q>>, _OnConflictSpec<T, I, Q> {

    }


    interface _ValuesDefaultSpec<T, I extends Item, Q extends Item>
            extends InsertStatement._FullColumnDefaultClause<T, _ValuesDefaultSpec<T, I, Q>>,
            InsertStatement._DomainValuesClause<T, _OnConflictSpec<T, I, Q>>,
            InsertStatement._DynamicValuesClause<T, _OnConflictSpec<T, I, Q>>,
            InsertStatement._StaticValuesClause<_PostgreValuesStaticParensClause<T, I, Q>> {

    }


    interface _ComplexColumnDefaultSpec<T, I extends Item, Q extends Item>
            extends _ValuesDefaultSpec<T, I, Q>,
            InsertStatement._QueryInsertSpaceClause<PostgreQuery.WithSpec<_OnConflictSpec<T, I, Q>>, _OnConflictSpec<T, I, Q>> {

    }

    interface _OverridingValueSpec<T, I extends Item, Q extends Item>
            extends _ComplexColumnDefaultSpec<T, I, Q>, _OverridingValueClause<_ComplexColumnDefaultSpec<T, I, Q>> {


    }


    interface _ColumnListSpec<T, I extends Item, Q extends Item>
            extends InsertStatement._ColumnListParensClause<T, _OverridingValueSpec<T, I, Q>>,
            _OverridingValueSpec<T, I, Q> {

    }

    interface _TableAliasSpec<T, I extends Item, Q extends Item>
            extends Statement._AsClause<_ColumnListSpec<T, I, Q>>,
            _ColumnListSpec<T, I, Q> {

    }

    interface _ChildInsertIntoClause<P> extends Item {

        <T> _TableAliasSpec<T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _ChildWithCteSpec<P>
            extends _PostgreDynamicWithClause<_ChildInsertIntoClause<P>>,
            PostgreQuery._PostgreStaticWithClause<_ChildInsertIntoClause<P>>,
            _ChildInsertIntoClause<P> {

    }


    interface _ParentInsert<P> extends Insert, InsertStatement._ChildPartClause<_ChildWithCteSpec<P>> {


    }

    interface _ParentReturnInsert<P> extends ReturningInsert, InsertStatement._ChildPartClause<_ChildWithCteSpec<P>> {


    }


    interface _PrimaryInsertIntoClause extends Item {


        <T> _TableAliasSpec<T, Insert, ReturningInsert> insertInto(TableMeta<T> table);

        <P> _TableAliasSpec<P, _ParentInsert<P>, _ParentReturnInsert<P>> insertInto(ParentTableMeta<P> table);

    }


    interface _PrimaryWithCteSpec
            extends _PostgreDynamicWithClause<_PrimaryInsertIntoClause>,
            PostgreQuery._PostgreStaticWithClause<_PrimaryInsertIntoClause>,
            _PrimaryInsertIntoClause {

    }


    interface _PrimaryPreferLiteralSpec extends InsertStatement._PreferLiteralClause<_PrimaryWithCteSpec>,
            _PrimaryWithCteSpec {

    }

    interface _PrimaryNullOptionSpec extends InsertStatement._NullOptionClause<_PrimaryPreferLiteralSpec>,
            _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec extends InsertStatement._MigrationOptionClause<_PrimaryNullOptionSpec>,
            InsertStatement._IgnoreReturnIdsOptionClause<_PrimaryNullOptionSpec>,
            _PrimaryNullOptionSpec {

    }


    /*-------------------below complex insert syntax -------------------*/

    /**
     * <p>
     * This interface is used by in multi-statement api.
     */
    interface _ComplexInsertIntoClause<I extends Item> extends Item {

        <T> _TableAliasSpec<T, I, I> insertInto(TableMeta<T> table);

    }


    interface _ComplexWithCteSpec<I extends Item>
            extends _PostgreDynamicWithClause<_ComplexInsertIntoClause<I>>,
            PostgreQuery._PostgreStaticWithClause<_ComplexInsertIntoClause<I>>,
            _ComplexInsertIntoClause<I> {

    }


    interface _ComplexPreferLiteralSpec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_ComplexWithCteSpec<I>>,
            _ComplexWithCteSpec<I> {

    }

    interface _ComplexNullOptionSpec<I extends Item>
            extends InsertStatement._NullOptionClause<_ComplexPreferLiteralSpec<I>>,
            _ComplexPreferLiteralSpec<I> {

    }

    interface _ComplexOptionSpec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_ComplexNullOptionSpec<I>>,
            InsertStatement._IgnoreReturnIdsOptionClause<_ComplexNullOptionSpec<I>>,
            _ComplexNullOptionSpec<I> {

    }

    /*-------------------below sub insert syntax -------------------*/

    interface _CteInsertIntoClause<I extends Item> extends Item {

        <T> _TableAliasSpec<T, I, I> insertInto(TableMeta<T> table);

    }

    interface _DynamicSubWithSpec<I extends Item> extends _PostgreDynamicWithClause<_CteInsertIntoClause<I>>,
            PostgreQuery._PostgreStaticWithClause<_CteInsertIntoClause<I>>,
            _CteInsertIntoClause<I> {

    }


    interface _DynamicSubPreferLiteralSpec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_DynamicSubWithSpec<I>>,
            _DynamicSubWithSpec<I> {

    }


    interface _DynamicSubNullOptionSpec<I extends Item>
            extends InsertStatement._NullOptionClause<_DynamicSubPreferLiteralSpec<I>>,
            _DynamicSubPreferLiteralSpec<I> {

    }


    interface _DynamicSubOptionSpec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_DynamicSubNullOptionSpec<I>>,
            _DynamicSubNullOptionSpec<I> {

    }


    interface _InsertDynamicCteAsClause extends _PostgreDynamicCteAsClause<_DynamicSubOptionSpec<_CommaClause<PostgreCtes>>,
            _CommaClause<PostgreCtes>> {

    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_InsertDynamicCteAsClause>, _InsertDynamicCteAsClause {

    }


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     */
    interface _StaticSubPreferLiteralSpec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_CteInsertIntoClause<I>>,
            _CteInsertIntoClause<I> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     */
    interface _StaticSubNullOptionSpec<I extends Item>
            extends InsertStatement._NullOptionClause<_StaticSubPreferLiteralSpec<I>>,
            _StaticSubPreferLiteralSpec<I> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     */
    interface _StaticSubOptionSpec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_StaticSubNullOptionSpec<I>>,
            _StaticSubNullOptionSpec<I> {

    }


}
