package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.dialect.ReturningInsert;
import io.army.meta.*;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing postgre insert statement.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-insert.html">Postgre INSERT syntax</a>
 * @since 1.0
 */
public interface PostgreInsert extends PostgreStatement {

    interface _OverridingValueClause<R> {

        R overridingSystemValue();

        R overridingUserValue();

        R ifOverridingSystemValue(BooleanSupplier predicate);

        R ifOverridingUserValue(BooleanSupplier predicate);
    }

    interface _StaticReturningCommaSpec<Q extends Item>
            extends _StaticReturningCommaClause<_StaticReturningCommaSpec<Q>>
            , _DqlInsertClause<Q> {

    }

    interface _ReturningSpec<I extends Item, Q extends Item>
            extends _StaticReturningClause<_StaticReturningCommaSpec<Q>>
            , _DynamicReturningClause<_DqlInsertClause<Q>>
            , _DmlInsertClause<I> {

    }


    interface _DoUpdateWhereAndSpec<I extends Item, Q extends Item>
            extends Update._UpdateWhereAndClause<_DoUpdateWhereAndSpec<I, Q>>
            , _ReturningSpec<I, Q> {

    }

    interface _DoUpdateWhereClause<I extends Item, Q extends Item>
            extends _WhereClause<_ReturningSpec<I, Q>, _DoUpdateWhereAndSpec<I, Q>>
            , _ReturningSpec<I, Q> {

    }


    interface _DoUpdateSetClause<T, I extends Item, Q extends Item>
            extends Update._StaticRowSetClause<FieldMeta<T>, _DoUpdateWhereSpec<T, I, Q>>
            , Update._DynamicSetClause<RowPairs<FieldMeta<T>>, _DoUpdateWhereClause<I, Q>> {

    }

    interface _DoUpdateWhereSpec<T, I extends Item, Q extends Item>
            extends _DoUpdateWhereClause<I, Q>
            , _DoUpdateSetClause<T, I, Q> {

    }


    interface _ConflictActionClause<T, I extends Item, Q extends Item> {

        _ReturningSpec<I, Q> doNothing();

        _DoUpdateSetClause<T, I, Q> doUpdate();

    }

    interface _ConflictTargetWhereAndSpec<T, I extends Item, Q extends Item>
            extends Statement._WhereAndClause<_ConflictTargetWhereAndSpec<T, I, Q>>
            , _ConflictActionClause<T, I, Q> {

    }

    interface _ConflictTargetWhereSpec<T, I extends Item, Q extends Item>
            extends _ConflictActionClause<T, I, Q>
            , Statement._WhereClause<_ConflictActionClause<T, I, Q>, _ConflictTargetWhereAndSpec<T, I, Q>> {

    }


    interface _ConflictTargetCommaSpec<T, I extends Item, Q extends Item>
            extends Statement._RightParenClause<_ConflictTargetWhereSpec<T, I, Q>> {

        _ConflictCollateSpec<T, I, Q> comma(IndexFieldMeta<T> indexColumn);


    }


    interface _ConflictOpClassSpec<T, I extends Item, Q extends Item>
            extends _ConflictTargetCommaSpec<T, I, Q> {

        _ConflictTargetCommaSpec<T, I, Q> opClass();

        _ConflictTargetCommaSpec<T, I, Q> ifOpClass(BooleanSupplier supplier);
    }


    interface _ConflictCollateSpec<T, I extends Item, Q extends Item>
            extends _ConflictOpClassSpec<T, I, Q> {


        _ConflictOpClassSpec<T, I, Q> collation(String collationName);

        _ConflictOpClassSpec<T, I, Q> collation(Supplier<String> supplier);

        _ConflictOpClassSpec<T, I, Q> ifCollation(Supplier<String> supplier);

    }


    interface _ConflictTargetOptionSpec<T, I extends Item, Q extends Item>
            extends _ConflictActionClause<T, I, Q> {

        _ConflictCollateSpec<T, I, Q> leftParen(IndexFieldMeta<T> indexColumn);

        _ConflictActionClause<T, I, Q> onConstraint(String constraintName);

    }


    interface _OnConflictSpec<T, I extends Item, Q extends Item> extends _ReturningSpec<I, Q> {

        _ConflictTargetOptionSpec<T, I, Q> onConflict();


    }



    /*-------------------below insert syntax interfaces  -------------------*/

    interface _ValuesLeftParenClause<T, I extends Item, Q extends Item>
            extends Insert._StaticValueLeftParenClause<T, _ValuesLeftParenSpec<T, I, Q>> {

    }

    interface _ValuesLeftParenSpec<T, I extends Item, Q extends Item>
            extends _ValuesLeftParenClause<T, I, Q>, _OnConflictSpec<T, I, Q> {

    }


    interface _ValuesDefaultSpec<T, I extends Item, Q extends Item>
            extends Insert._ColumnDefaultClause<T, _ValuesDefaultSpec<T, I, Q>>
            , Insert._DomainValueClause<T, _OnConflictSpec<T, I, Q>>
            , Insert._DynamicValuesClause<T, _OnConflictSpec<T, I, Q>>
            , Insert._StaticValuesClause<_ValuesLeftParenClause<T, I, Q>> {

    }


    interface _ComplexColumnDefaultSpec<T, I extends Item, Q extends Item>
            extends _ValuesDefaultSpec<T, I, Q>
            , _StaticSpaceClause<PostgreQuery._WithSpec<_OnConflictSpec<T, I, Q>>> {

    }

    interface _OverridingValueSpec<T, I extends Item, Q extends Item>
            extends _ValuesDefaultSpec<T, I, Q>, _OverridingValueClause<_ValuesDefaultSpec<T, I, Q>> {


    }

    interface _ComplexOverridingValueSpec<T, I extends Item, Q extends Item>
            extends _OverridingValueSpec<T, I, Q> {

        @Override
        _ComplexColumnDefaultSpec<T, I, Q> overridingSystemValue();

        @Override
        _ComplexColumnDefaultSpec<T, I, Q> overridingUserValue();

        @Override
        _ComplexColumnDefaultSpec<T, I, Q> ifOverridingSystemValue(BooleanSupplier supplier);

        @Override
        _ComplexColumnDefaultSpec<T, I, Q> ifOverridingUserValue(BooleanSupplier supplier);

    }


    interface _ColumnListSpec<T, I extends Item, Q extends Item>
            extends Insert._ColumnListClause<T, _ComplexOverridingValueSpec<T, I, Q>>
            , _OverridingValueSpec<T, I, Q> {

    }

    interface _TableAliasSpec<T, I extends Item, Q extends Item>
            extends Statement._AsClause<_ColumnListSpec<T, I, Q>>
            , _ColumnListSpec<T, I, Q> {


    }

    interface _ChildInsertIntoClause<P> {

        <T> _TableAliasSpec<T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _ChildCteComma<P>
            extends _StaticWithCommaClause<_StaticCteParensSpec<_ChildCteComma<P>>>
            , _ChildInsertIntoClause<P> {

    }


    interface _ChildWithCteSpec<P>
            extends _PostgreDynamicWithClause<_ChildInsertIntoClause<P>>
            , _StaticWithClause<_StaticCteParensSpec<_ChildCteComma<P>>>
            , _ChildInsertIntoClause<P> {

    }


    interface _ParentInsert<P> extends Insert, Insert._ChildPartClause<_ChildWithCteSpec<P>> {


    }

    interface _ParentReturnInsert<P> extends ReturningInsert, Insert._ChildPartClause<_ChildWithCteSpec<P>> {


    }


    interface _PrimaryInsertIntoClause {

        <T> _TableAliasSpec<T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table);

        <T> _TableAliasSpec<T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table);

        <P> _TableAliasSpec<P, _ParentInsert<P>, _ParentReturnInsert<P>> insertInto(ParentTableMeta<P> table);

    }

    interface _ComplexInsertIntoClause<I extends Item, Q extends Item> {

        <T> _TableAliasSpec<T, I, Q> insertInto(TableMeta<T> table);

    }


    interface _PrimaryCteComma
            extends _StaticWithCommaClause<_StaticCteParensSpec<_PrimaryCteComma>>
            , _PrimaryInsertIntoClause {

    }


    interface _PrimaryWithCteSpec
            extends _PostgreDynamicWithClause<_PrimaryInsertIntoClause>
            , _StaticWithClause<_StaticCteParensSpec<_PrimaryCteComma>>
            , _PrimaryInsertIntoClause {

    }


    interface _PrimaryPreferLiteralSpec
            extends Insert._PreferLiteralClause<_PrimaryWithCteSpec>
            , _PrimaryWithCteSpec {

    }

    interface _PrimaryNullOptionSpec
            extends Insert._NullOptionClause<_PrimaryPreferLiteralSpec>
            , _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec
            extends Insert._MigrationOptionClause<_PrimaryNullOptionSpec>
            , _PrimaryNullOptionSpec {

    }

    /*-------------------below sub insert syntax -------------------*/


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubPreferLiteralSpec<I extends Item>
            extends Insert._PreferLiteralClause<_ComplexInsertIntoClause<I, I>>
            , _ComplexInsertIntoClause<I, I> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubNullOptionSpec<I extends Item>
            extends Insert._NullOptionClause<_StaticSubPreferLiteralSpec<I>>
            , _StaticSubPreferLiteralSpec<I> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubOptionSpec<I extends Item>
            extends Insert._MigrationOptionClause<_StaticSubNullOptionSpec<I>>
            , _StaticSubNullOptionSpec<I> {

    }

    /*-------------------below complex insert -------------------*/


    interface _ComplexComma<I extends Item>
            extends _StaticWithCommaClause<_StaticCteParensSpec<_ComplexComma<I>>>
            , _ComplexInsertIntoClause<I, I> {

    }


    /**
     * <p>
     * sub-statement syntax forbid static WITH syntax,because it destroy the simpleness of SQL.
     * </p>
     */
    interface _ComplexWithSpec<I extends Item>
            extends _PostgreDynamicWithClause<_ComplexInsertIntoClause<I, I>>
            , _StaticWithClause<_StaticCteParensSpec<_ComplexComma<I>>>
            , _ComplexInsertIntoClause<I, I> {

    }


    interface _ComplexPreferLiteralSpec<I extends Item>
            extends Insert._PreferLiteralClause<_ComplexWithSpec<I>>
            , _ComplexWithSpec<I> {

    }

    interface _ComplexNullOptionSpec<I extends Item>
            extends Insert._NullOptionClause<_ComplexPreferLiteralSpec<I>>
            , _ComplexPreferLiteralSpec<I> {
    }

    interface _ComplexOptionSpec<I extends Item>
            extends Insert._MigrationOptionClause<_ComplexNullOptionSpec<I>>
            , _ComplexNullOptionSpec<I> {

    }

    /*-------------------below dynamic sub insert -------------------*/

    /**
     * <p>
     * sub-statement syntax forbid static WITH syntax,because it destroy the simpleness of SQL.
     * </p>
     */
    interface _DynamicSubWithCteSpec<I extends Item>
            extends _PostgreDynamicWithClause<_ComplexInsertIntoClause<I, I>>
            , _ComplexInsertIntoClause<I, I> {

    }


    interface _DynamicSubPreferLiteralSpec<I extends Item>
            extends Insert._PreferLiteralClause<_DynamicSubWithCteSpec<I>>
            , _DynamicSubWithCteSpec<I> {

    }

    interface _DynamicSubNullOptionSpec<I extends Item>
            extends Insert._NullOptionClause<_DynamicSubPreferLiteralSpec<I>>
            , _DynamicSubPreferLiteralSpec<I> {

    }

    interface _DynamicSubOptionSpec<I extends Item>
            extends Insert._MigrationOptionClause<_DynamicSubNullOptionSpec<I>>
            , _DynamicSubNullOptionSpec<I> {

    }

    interface _DynamicSubMaterializedSpec<I extends Item> extends _CteMaterializedClause<_DynamicSubOptionSpec<I>>
            , _DynamicSubOptionSpec<I> {

    }

    interface _DynamicSubInsertSpec
            extends _SimpleCteLeftParenSpec<_DynamicSubMaterializedSpec<_AsCteClause<PostgreCtes>>> {

    }


}
