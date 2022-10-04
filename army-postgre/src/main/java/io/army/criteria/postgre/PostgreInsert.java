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
 * @see <a href="https://www.postgresql.org/docs/current/sql-insert.html">Postgre insert syntax</a>
 * @since 1.0
 */
public interface PostgreInsert extends DialectStatement {


    interface _ReturningSpec<C, I extends Item, Q extends Item>
            extends DialectStatement._StaticReturningClause<I, Q>
            , DialectStatement._DynamicReturningClause<C, I, Q> {

    }

    interface _DoUpdateWhereAndSpec<C, I extends Item, Q extends Item>
            extends Statement._MinWhereAndClause<C, _DoUpdateWhereAndSpec<C, I, Q>>
            , _ReturningSpec<C, I, Q> {

    }

    interface _DoUpdateWhereSpec<C, T, I extends Item, Q extends Item>
            extends Statement._MinQueryWhereClause<C, _ReturningSpec<C, I, Q>, _DoUpdateWhereAndSpec<C, I, Q>>
            , _DoUpdateSetClause<C, T, I, Q>
            , _ReturningSpec<C, I, Q> {

    }


    interface _DoUpdateSetClause<C, T, I extends Item, Q extends Item>
            extends Update._SetClause<C, FieldMeta<T>, _DoUpdateWhereSpec<C, T, I, Q>> {


    }


    interface _ConflictActionClause<C, T, I extends Item, Q extends Item> {

        _ReturningSpec<C, I, Q> doNothing();

        _DoUpdateSetClause<C, T, I, Q> doUpdate();

    }

    interface _ConflictTargetWhereAndSpec<C, T, I extends Item, Q extends Item>
            extends Statement._MinWhereAndClause<C, _ConflictTargetWhereAndSpec<C, T, I, Q>>
            , _ConflictActionClause<C, T, I, Q> {

    }

    interface _ConflictTargetWhereSpec<C, T, I extends Item, Q extends Item>
            extends _ConflictActionClause<C, T, I, Q>
            , Statement._MinQueryWhereClause<C, _ConflictActionClause<C, T, I, Q>, _ConflictTargetWhereAndSpec<C, T, I, Q>> {

    }


    interface _ConflictTargetCommaSpec<C, T, I extends Item, Q extends Item>
            extends Statement._RightParenClause<_ConflictTargetWhereSpec<C, T, I, Q>> {

        _ConflictCollateSpec<C, T, I, Q> comma(IndexFieldMeta<T> indexColumn);


    }


    interface _ConflictOpClassSpec<C, T, I extends Item, Q extends Item>
            extends _ConflictTargetCommaSpec<C, T, I, Q> {

        _ConflictTargetCommaSpec<C, T, I, Q> opClass();

        _ConflictTargetCommaSpec<C, T, I, Q> ifOpClass(BooleanSupplier supplier);
    }


    interface _ConflictCollateSpec<C, T, I extends Item, Q extends Item>
            extends _ConflictOpClassSpec<C, T, I, Q> {


        _ConflictOpClassSpec<C, T, I, Q> collation(String collationName);

        _ConflictOpClassSpec<C, T, I, Q> collation(Supplier<String> supplier);

        _ConflictOpClassSpec<C, T, I, Q> ifCollation(Supplier<String> supplier);

    }


    interface _ConflictTargetOptionSpec<C, T, I extends Item, Q extends Item>
            extends _ConflictActionClause<C, T, I, Q> {

        _ConflictCollateSpec<C, T, I, Q> leftParen(IndexFieldMeta<T> indexColumn);

        _ConflictActionClause<C, T, I, Q> onConstraint(String constraintName);

    }


    interface _OnConflictSpec<C, T, I extends Item, Q extends Item> extends _ReturningSpec<C, I, Q> {

        _ConflictTargetOptionSpec<C, T, I, Q> onConflict();


    }



    /*-------------------below insert syntax interfaces  -------------------*/

    interface _ValuesLeftParenClause<C, T, I extends Item, Q extends Item>
            extends Insert._StaticValueLeftParenClause<C, T, _ValuesLeftParenSpec<C, T, I, Q>> {

    }

    interface _ValuesLeftParenSpec<C, T, I extends Item, Q extends Item>
            extends _ValuesLeftParenClause<C, T, I, Q>, _OnConflictSpec<C, T, I, Q> {

    }


    interface _ValuesDefaultSpec<C, T, I extends Item, Q extends Item>
            extends Insert._ColumnDefaultClause<C, T, _ValuesDefaultSpec<C, T, I, Q>>
            , Insert._DomainValueClause<C, T, _OnConflictSpec<C, T, I, Q>>
            , Insert._DynamicValuesClause<C, T, _OnConflictSpec<C, T, I, Q>>
            , Insert._StaticValuesClause<_ValuesLeftParenClause<C, T, I, Q>> {

    }


    interface _SpaceSubQuerySpec<C, T, I extends Item, Q extends Item>
            extends Insert._SpaceSubQueryClause<C, _OnConflictSpec<C, T, I, Q>> {

    }


    interface _ComplexColumnDefaultSpec<C, T, I extends Item, Q extends Item>
            extends _ValuesDefaultSpec<C, T, I, Q>, _SpaceSubQuerySpec<C, T, I, Q> {

    }

    interface _OverridingValueSpec<C, T, I extends Item, Q extends Item>
            extends _ValuesDefaultSpec<C, T, I, Q> {

        _ValuesDefaultSpec<C, T, I, Q> overridingSystemValue();

        _ValuesDefaultSpec<C, T, I, Q> overridingUserValue();

        _ValuesDefaultSpec<C, T, I, Q> ifOverridingSystemValue(BooleanSupplier supplier);

        _ValuesDefaultSpec<C, T, I, Q> ifOverridingUserValue(BooleanSupplier supplier);

    }

    interface _ComplexOverridingValueSpec<C, T, I extends Item, Q extends Item>
            extends _OverridingValueSpec<C, T, I, Q> {

        @Override
        _ComplexColumnDefaultSpec<C, T, I, Q> overridingSystemValue();

        @Override
        _ComplexColumnDefaultSpec<C, T, I, Q> overridingUserValue();

        @Override
        _ComplexColumnDefaultSpec<C, T, I, Q> ifOverridingSystemValue(BooleanSupplier supplier);

        @Override
        _ComplexColumnDefaultSpec<C, T, I, Q> ifOverridingUserValue(BooleanSupplier supplier);

    }


    interface _ColumnListSpec<C, T, I extends Item, Q extends Item>
            extends Insert._ColumnListClause<C, T, _ComplexOverridingValueSpec<C, T, I, Q>>
            , _OverridingValueSpec<C, T, I, Q> {

    }

    interface _TableAliasSpec<C, T, I extends Item, Q extends Item>
            extends Statement._AsClause<_ColumnListSpec<C, T, I, Q>>
            , _ColumnListSpec<C, T, I, Q> {


    }

    interface _ChildInsertIntoClause<C, P> {

        <T> _TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _ChildCteComma<C, P>
            extends _StaticWithCommaClause<_StaticCteLeftParenSpec<C, _ChildCteComma<C, P>, _ChildCteComma<C, P>>>
            , DialectStatement._StaticSpaceClause<_ChildInsertIntoClause<C, P>>
            , Item {

    }


    interface _ChildWithCteSpec<C, P>
            extends PostgreQuery._PostgreDynamicWithSpec<C, _ChildInsertIntoClause<C, P>>
            , DialectStatement._StaticWithCteClause<_StaticCteLeftParenSpec<C, _ChildCteComma<C, P>, _ChildCteComma<C, P>>>
            , _ChildInsertIntoClause<C, P> {

    }


    interface _ParentInsert<C, P> extends Insert, Insert._ChildPartClause<_ChildWithCteSpec<C, P>> {


    }

    interface _ParentReturnInsert<C, P> extends ReturningInsert, Insert._ChildPartClause<_ChildWithCteSpec<C, P>> {


    }


    interface _PrimaryInsertIntoClause<C> {

        <T> _TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table);

        <T> _TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table);

        <P> _TableAliasSpec<C, P, _ParentInsert<C, P>, _ParentReturnInsert<C, P>> insertInto(ParentTableMeta<P> table);

    }


    interface _SubInsertIntoClause<C, I extends Item, Q extends Item> {

        <T> _TableAliasSpec<C, T, I, Q> insertInto(TableMeta<T> table);

    }

    interface _ParentCteComma<C>
            extends _StaticWithCommaClause<_StaticCteLeftParenSpec<C, _ParentCteComma<C>, _ParentCteComma<C>>>
            , DialectStatement._StaticSpaceClause<_PrimaryInsertIntoClause<C>>, Item {

    }


    /**
     * <p>
     * static sub-statement syntax forbid the with clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubPreferLiteralSpec<C, I extends Item, Q extends Item>
            extends Insert._PreferLiteralClause<_SubInsertIntoClause<C, I, Q>>
            , _SubInsertIntoClause<C, I, Q> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the with clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubNullOptionSpec<C, I extends Item, Q extends Item>
            extends Insert._NullOptionClause<_StaticSubPreferLiteralSpec<C, I, Q>>
            , _StaticSubPreferLiteralSpec<C, I, Q> {

    }

    /**
     * <p>
     * static sub-statement syntax forbid the with clause of cte insert,because it destroy the Readability of code.
     * </p>
     */
    interface _StaticSubOptionSpec<C, I extends Item, Q extends Item>
            extends Insert._MigrationOptionClause<_StaticSubNullOptionSpec<C, I, Q>>
            , _StaticSubNullOptionSpec<C, I, Q> {

    }


    interface _StaticSubComplexCommandSpec<C, I extends Item, Q extends Item>
            extends _StaticSubOptionSpec<C, I, Q> {

    }

    interface _StaticCteAsClause<C, I extends Item, Q extends Item>
            extends Statement._StaticAsClaus<_StaticSubComplexCommandSpec<C, I, Q>> {


    }

    interface _StaticCteLeftParenSpec<C, I extends Item, Q extends Item>
            extends Statement._LeftParenStringQuadraSpec<C, _StaticCteAsClause<C, I, Q>>
            , _StaticCteAsClause<C, I, Q> {

    }

    interface _PrimaryWithCteSpec<C>
            extends PostgreQuery._PostgreDynamicWithSpec<C, _PrimaryInsertIntoClause<C>>
            , DialectStatement._StaticWithCteClause<_StaticCteLeftParenSpec<C, _ParentCteComma<C>, _ParentCteComma<C>>>
            , _PrimaryInsertIntoClause<C> {

    }


    interface _PrimaryPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_PrimaryWithCteSpec<C>>
            , _PrimaryWithCteSpec<C> {

    }

    interface _PrimaryNullOptionSpec<C>
            extends Insert._NullOptionClause<_PrimaryPreferLiteralSpec<C>>
            , _PrimaryPreferLiteralSpec<C> {

    }

    interface _PrimaryOptionSpec<C>
            extends Insert._MigrationOptionClause<_PrimaryNullOptionSpec<C>>
            , _PrimaryNullOptionSpec<C> {

    }

    /*-------------------below dynamic sub insert syntax -------------------*/


    /**
     * <p>
     * Dynamic sub-statement syntax forbid static WITH syntax,because it destroy the simpleness of SQL.
     * </p>
     */
    interface _DynamicSubWithCteSpec<C, I extends Item, Q extends Item>
            extends PostgreQuery._PostgreDynamicWithSpec<C, _SubInsertIntoClause<C, I, Q>>
            , _SubInsertIntoClause<C, I, Q> {

    }


    interface _DynamicSubPreferLiteralSpec<C, I extends Item, Q extends Item>
            extends Insert._PreferLiteralClause<_DynamicSubWithCteSpec<C, I, Q>>
            , _DynamicSubWithCteSpec<C, I, Q> {

    }

    interface _DynamicSubNullOptionSpec<C, I extends Item, Q extends Item>
            extends Insert._NullOptionClause<_DynamicSubPreferLiteralSpec<C, I, Q>>
            , _DynamicSubPreferLiteralSpec<C, I, Q> {

    }

    interface _DynamicSubOptionSpec<C, I extends Item, Q extends Item>
            extends Insert._MigrationOptionClause<_DynamicSubNullOptionSpec<C, I, Q>>
            , _DynamicSubNullOptionSpec<C, I, Q> {

    }

    interface _DynamicSubInsert<C, I extends Item>
            extends _DynamicSubOptionSpec<C, I, I>
            , Statement._LeftParenStringQuadraOptionalSpec<C, _DynamicSubOptionSpec<C, I, I>> {


    }


}
