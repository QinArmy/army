package io.army.criteria.postgre;

import io.army.criteria.*;
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
public interface PostgreInsert extends DialectStatement {


    interface _ReturningSpec<I extends Item, Q extends Item>
            extends DialectStatement._StaticReturningClause<I, Q>
            , DialectStatement._DynamicReturningClause<I, Q> {

    }

    interface _DoUpdateWhereAndSpec<I extends Item, Q extends Item>
            extends Statement._MinWhereAndClause<_DoUpdateWhereAndSpec<I, Q>>
            , _ReturningSpec<I, Q> {

    }

    interface _DoUpdateWhereSpec<T, I extends Item, Q extends Item>
            extends Statement._MinQueryWhereClause<_ReturningSpec<I, Q>, _DoUpdateWhereAndSpec<I, Q>>
            , _DoUpdateSetClause<T, I, Q>
            , _ReturningSpec<I, Q> {

    }


    interface _DoUpdateSetClause<T, I extends Item, Q extends Item>
            extends Update._StaticSetClause<FieldMeta<T>, _DoUpdateWhereSpec<T, I, Q>> {


    }


    interface _ConflictActionClause<T, I extends Item, Q extends Item> {

        _ReturningSpec<I, Q> doNothing();

        _DoUpdateSetClause<T, I, Q> doUpdate();

    }

    interface _ConflictTargetWhereAndSpec<T, I extends Item, Q extends Item>
            extends Statement._MinWhereAndClause<_ConflictTargetWhereAndSpec<T, I, Q>>
            , _ConflictActionClause<T, I, Q> {

    }

    interface _ConflictTargetWhereSpec<T, I extends Item, Q extends Item>
            extends _ConflictActionClause<T, I, Q>
            , Statement._MinQueryWhereClause<_ConflictActionClause<T, I, Q>, _ConflictTargetWhereAndSpec<T, I, Q>> {

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


    interface _SpaceSubQuerySpec<T, I extends Item, Q extends Item>
            extends Query._StaticSpaceClause<_OnConflictSpec<T, I, Q>> {

    }


    interface _ComplexColumnDefaultSpec<T, I extends Item, Q extends Item>
            extends _ValuesDefaultSpec<T, I, Q>, _SpaceSubQuerySpec<T, I, Q> {

    }

    interface _OverridingValueSpec<T, I extends Item, Q extends Item>
            extends _ValuesDefaultSpec<T, I, Q> {

        _ValuesDefaultSpec<T, I, Q> overridingSystemValue();

        _ValuesDefaultSpec<T, I, Q> overridingUserValue();

        _ValuesDefaultSpec<T, I, Q> ifOverridingSystemValue(BooleanSupplier supplier);

        _ValuesDefaultSpec<T, I, Q> ifOverridingUserValue(BooleanSupplier supplier);

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
            extends Query._StaticWithCommaClause<_StaticCteLeftParenSpec<_ChildCteComma<P>, _ChildCteComma<P>>>
            , Query._StaticSpaceClause<_ChildInsertIntoClause<P>>
            , Item {

    }


    interface _ChildWithCteSpec<P>
            extends PostgreQuery._PostgreDynamicWithClause<_ChildInsertIntoClause<P>>
            , Query._StaticWithCteClause<_StaticCteLeftParenSpec<_ChildCteComma<P>, _ChildCteComma<P>>>
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


    interface _SubInsertIntoClause<I extends Item, Q extends Item> {

        <T> _TableAliasSpec<T, I, Q> insertInto(TableMeta<T> table);

    }

    interface _ParentCteComma
            extends Query._StaticWithCommaClause<_StaticCteLeftParenSpec<_ParentCteComma, _ParentCteComma>>
            , Query._StaticSpaceClause<_PrimaryInsertIntoClause>, Item {

    }


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubPreferLiteralSpec<I extends Item, Q extends Item>
            extends Insert._PreferLiteralClause<_SubInsertIntoClause<I, Q>>
            , _SubInsertIntoClause<I, Q> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubNullOptionSpec<I extends Item, Q extends Item>
            extends Insert._NullOptionClause<_StaticSubPreferLiteralSpec<I, Q>>
            , _StaticSubPreferLiteralSpec<I, Q> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubOptionSpec<I extends Item, Q extends Item>
            extends Insert._MigrationOptionClause<_StaticSubNullOptionSpec<I, Q>>
            , _StaticSubNullOptionSpec<I, Q> {

    }


    interface _StaticSubComplexCommandSpec<I extends Item, Q extends Item>
            extends _StaticSubOptionSpec<I, Q> {

    }

    interface _StaticCteAsClause<I extends Item, Q extends Item>
            extends Statement._StaticAsClaus<_StaticSubComplexCommandSpec<I, Q>> {


    }

    interface _StaticCteLeftParenSpec<I extends Item, Q extends Item>
            extends Statement._LeftParenStringQuadraSpec<_StaticCteAsClause<I, Q>>
            , _StaticCteAsClause<I, Q> {

    }

    interface _PrimaryWithCteSpec
            extends PostgreQuery._PostgreDynamicWithClause<_PrimaryInsertIntoClause>
            , Query._StaticWithCteClause<_StaticCteLeftParenSpec<_ParentCteComma, _ParentCteComma>>
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

    /*-------------------below dynamic sub insert syntax -------------------*/


    /**
     * <p>
     * sub-statement syntax forbid static WITH syntax,because it destroy the simpleness of SQL.
     * </p>
     */
    interface _DynamicSubWithCteSpec<I extends Item, Q extends Item>
            extends PostgreQuery._PostgreDynamicWithClause<_SubInsertIntoClause<I, Q>>
            , _SubInsertIntoClause<I, Q> {

    }


    interface _DynamicSubPreferLiteralSpec<I extends Item, Q extends Item>
            extends Insert._PreferLiteralClause<_DynamicSubWithCteSpec<I, Q>>
            , _DynamicSubWithCteSpec<I, Q> {

    }

    interface _DynamicSubNullOptionSpec<I extends Item, Q extends Item>
            extends Insert._NullOptionClause<_DynamicSubPreferLiteralSpec<I, Q>>
            , _DynamicSubPreferLiteralSpec<I, Q> {

    }

    interface _DynamicSubOptionSpec<I extends Item, Q extends Item>
            extends Insert._MigrationOptionClause<_DynamicSubNullOptionSpec<I, Q>>
            , _DynamicSubNullOptionSpec<I, Q> {

    }

    interface _DynamicSubInsert<I extends Item>
            extends _DynamicSubOptionSpec<I, I>
            , Statement._LeftParenStringQuadraOptionalSpec<_DynamicSubOptionSpec<I, I>> {


    }


}
