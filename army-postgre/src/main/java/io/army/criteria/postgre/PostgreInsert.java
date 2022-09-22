package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.meta.*;

import java.util.function.*;

public interface PostgreInsert extends Insert, DialectStatement {


    interface _StaticReturningCommaClause<R> {

        R comma(SelectItem selectItem);

        _StaticReturningCommaClause<R> comma(SelectItem selectItem1, SelectItem selectItem2);

    }

    interface _StaticReturningClause<R> {

        R returning(SelectItem selectItem1, SelectItem selectItem2);
    }


    interface _DynamicReturningClause<C, R> {

        R returning();

        R returning(SelectItem selectItem);

        R returning(Consumer<Consumer<SelectItem>> consumer);

        R returning(BiConsumer<C, Consumer<SelectItem>> consumer);
    }


    interface _ConflictActionClause<NR, UR> {

        NR doNothing();

        UR doUpdate();
    }

    interface _ConflictOpClassClause<C, R> {

        R opClass();

        R ifOpClass(BooleanSupplier supplier);

        R ifOpClass(Predicate<C> predicate);

    }

    interface _ConflictTargetCommaClause<T, R> {

        R comma(IndexFieldMeta<T> indexColumn);
    }

    interface _ConflictCollateClause<R> {

        R collation(String collationName);

        R collation(Supplier<String> supplier);
    }

    interface _OnConflictClause<R> {

        R onConflict();
    }

    interface _ConflictItemClause<T, LR, OR> {

        LR leftParen(IndexFieldMeta<T> indexColumn);

        OR onConstraint(String constraintName);
    }

    interface _DefaultValuesClause<DR> {


        DR defaultValues();

    }

    interface _OverridingValueClause<OR> {

        OR overridingSystemValue();

        OR overridingUserValue();
    }

    interface _ReturningCommaSpec<Q extends DqlStatement.DqlInsert> extends DqlStatement.DqlInsertSpec<Q>
            , _StaticReturningCommaClause<DqlStatement.DqlInsertSpec<Q>> {

    }


    interface _ReturningSpec<C, I extends DmlInsert, Q extends DqlStatement.DqlInsert> extends _DmlInsertSpec<I>
            , _DynamicReturningClause<C, DqlStatement.DqlInsertSpec<Q>>
            , _StaticReturningClause<_ReturningCommaSpec<Q>> {

    }

    interface _DoUpdateWhereAndSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._MinWhereAndClause<C, _DoUpdateWhereAndSpec<C, T, I, Q>>
            , _ReturningSpec<C, I, Q> {

    }

    interface _DoUpdateWhereSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._MinQueryWhereClause<C, _ReturningSpec<C, I, Q>, _DoUpdateWhereAndSpec<C, T, I, Q>>
            , _DoUpdateSetClause<C, T, I, Q>
            , _ReturningSpec<C, I, Q> {

    }


    interface _DoUpdateSetClause<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Update._SimpleSetClause<C, FieldMeta<T>, _DoUpdateWhereSpec<C, T, I, Q>> {


    }


    interface _NonParentConflictActionClause<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ConflictActionClause<_ReturningSpec<C, I, Q>, _DoUpdateSetClause<C, T, I, Q>> {

    }

    interface _ConflictTargetWhereAndSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._MinWhereAndClause<C, _ConflictTargetWhereAndSpec<C, T, I, Q>>
            , _NonParentConflictActionClause<C, T, I, Q> {

    }

    interface _ConflictTargetWhereSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _NonParentConflictActionClause<C, T, I, Q>
            , Statement._MinQueryWhereClause<C, _NonParentConflictActionClause<C, T, I, Q>, _ConflictTargetWhereAndSpec<C, T, I, Q>> {

    }


    interface _ConflictTargetCommaSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._RightParenClause<_ConflictTargetWhereSpec<C, T, I, Q>>
            , _ConflictTargetCommaClause<T, _ConflictCollateSpec<C, T, I, Q>> {

    }


    interface _ConflictOpClassSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ConflictTargetCommaSpec<C, T, I, Q>
            , _ConflictOpClassClause<C, _ConflictTargetCommaSpec<C, T, I, Q>> {

    }


    interface _ConflictCollateSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ConflictOpClassSpec<C, T, I, Q>
            , _ConflictCollateClause<_ConflictOpClassSpec<C, T, I, Q>> {


    }


    interface _NonParentConflictItemClause<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ConflictItemClause<T, _ConflictCollateSpec<C, T, I, Q>, _NonParentConflictActionClause<C, T, I, Q>> {

    }


    interface _OnConflictSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ReturningSpec<C, I, Q>
            , _OnConflictClause<_NonParentConflictItemClause<C, T, I, Q>> {

    }


    interface _PostgreChildSpec<CT> extends Insert._ChildPartClause<CT>, Insert._InsertSpec {

    }

    interface _PostgreChildReturnSpec<CT> extends Insert._ChildPartClause<CT>
            , DqlStatement.DqlInsertSpec<ReturningInsert> {

    }

    interface _ParentReturningCommaSpec<CT> extends _PostgreChildReturnSpec<CT>
            , _StaticReturningCommaClause<_PostgreChildReturnSpec<CT>> {

    }


    interface _ParentReturningSpec<C, CT> extends _PostgreChildSpec<CT>
            , _DynamicReturningClause<C, _PostgreChildReturnSpec<CT>>
            , _StaticReturningClause<_ParentReturningCommaSpec<CT>> {

    }

    interface _ParentDoUpdateWhereAndSpec<C, CT>
            extends Statement._MinWhereAndClause<C, _ParentDoUpdateWhereAndSpec<C, CT>>
            , _ParentReturningSpec<C, CT> {

    }

    interface _ParentDoUpdateWhereSpec<C, P, CT>
            extends Statement._MinQueryWhereClause<C, _ParentReturningSpec<C, CT>, _ParentDoUpdateWhereAndSpec<C, CT>>
            , _ParentDoUpdateSetClause<C, P, CT>
            , _ParentReturningSpec<C, CT> {

    }

    interface _ParentDoUpdateSetClause<C, P, CT>
            extends Update._SimpleSetClause<C, FieldMeta<P>, _ParentDoUpdateWhereSpec<C, P, CT>> {


    }

    interface _ParentConflictActionClause<C, P, CT>
            extends _ConflictActionClause<_ParentReturningSpec<C, CT>, _ParentDoUpdateSetClause<C, P, CT>> {

    }

    interface _ParentConflictTargetWhereAndSpec<C, P, CT>
            extends Statement._MinWhereAndClause<C, _ParentConflictTargetWhereAndSpec<C, P, CT>>
            , _ParentConflictActionClause<C, P, CT> {

    }


    interface _ParentConflictTargetWhereSpec<C, P, CT> extends _ParentConflictActionClause<C, P, CT>
            , Statement._MinQueryWhereClause<C, _ParentConflictActionClause<C, P, CT>, _ParentConflictTargetWhereAndSpec<C, P, CT>> {

    }

    interface _ParentConflictTargetCommaSpec<C, P, CT>
            extends Statement._RightParenClause<_ParentConflictTargetWhereSpec<C, P, CT>>
            , _ConflictTargetCommaClause<P, _ParentConflictCollateSpec<C, P, CT>> {

    }


    interface _ParentConflictOpClassSpec<C, P, CT> extends _ParentConflictTargetCommaSpec<C, P, CT>
            , _ConflictOpClassClause<C, _ParentConflictTargetCommaSpec<C, P, CT>> {

    }


    interface _ParentConflictCollateSpec<C, P, CT> extends _ParentConflictOpClassSpec<C, P, CT>
            , _ConflictCollateClause<_ParentConflictOpClassSpec<C, P, CT>> {


    }

    interface _ParentConflictItemClause<C, P, CT>
            extends _ConflictItemClause<P, _ParentConflictCollateSpec<C, P, CT>, _ParentConflictActionClause<C, P, CT>> {

    }


    interface _ParentOnConflictSpec<C, P, CT> extends _ParentReturningSpec<C, CT>
            , _OnConflictClause<_ParentConflictItemClause<C, P, CT>> {

    }



    /*-------------------below domain insert syntax interfaces  -------------------*/

    interface _DomainColumnDefaultSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._ColumnDefaultClause<C, T, _DomainColumnDefaultSpec<C, T, I, Q>>
            , _DomainValueClause<C, T, _OnConflictSpec<C, T, I, Q>>
            , _DefaultValuesClause<_OnConflictSpec<C, T, I, Q>> {

    }

    interface _DomainOverridingValueSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _DomainColumnDefaultSpec<C, T, I, Q>
            , _OverridingValueClause<_DomainColumnDefaultSpec<C, T, I, Q>> {

    }

    interface _DomainColumnListSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._ColumnListClause<C, T, _DomainOverridingValueSpec<C, T, I, Q>>
            , _DomainOverridingValueSpec<C, T, I, Q> {

    }

    interface _DomainTableAliasSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._AsClause<_DomainColumnListSpec<C, T, I, Q>>
            , _DomainColumnListSpec<C, T, I, Q> {


    }

    interface _DomainChildInsertIntoClause<C, P> {

        <T> _DomainTableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _DomainChildWithSpec<C, P>
            extends DialectStatement._WithCteClause<C, SubStatement, _DomainChildInsertIntoClause<C, P>>
            , _DomainChildInsertIntoClause<C, P> {

    }

    interface _DomainParentColumnDefaultSpec<C, P>
            extends Insert._ColumnDefaultClause<C, P, _DomainParentColumnDefaultSpec<C, P>>
            , _DomainValueClause<C, P, _ParentOnConflictSpec<C, P, _DomainChildWithSpec<C, P>>>
            , _DefaultValuesClause<_ParentOnConflictSpec<C, P, _DomainChildWithSpec<C, P>>> {

    }


    interface _DomainParentOverridingValueSpec<C, P>
            extends _OverridingValueClause<_DomainParentColumnDefaultSpec<C, P>>
            , _DomainParentColumnDefaultSpec<C, P> {

    }

    interface _DomainParentColumnListSpec<C, P>
            extends Insert._ColumnListClause<C, P, _DomainParentOverridingValueSpec<C, P>>
            , _DomainParentOverridingValueSpec<C, P> {

    }

    interface _DomainParentAliasSpec<C, P>
            extends Statement._AsClause<_DomainParentColumnListSpec<C, P>>
            , _DomainParentColumnListSpec<C, P> {

    }


    interface _DomainInsertIntoClause<C> {

        <T> _DomainTableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table);

        <P> _DomainParentAliasSpec<C, P> insertInto(ParentTableMeta<P> table);
    }

    interface _DomainWithCteSpec<C>
            extends DialectStatement._WithCteClause<C, SubStatement, _DomainInsertIntoClause<C>>
            , _DomainInsertIntoClause<C> {

    }


    interface _DomainPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_DomainWithCteSpec<C>>
            , _DomainWithCteSpec<C> {

    }

    interface _DomainNullOptionSpec<C>
            extends Insert._NullOptionClause<_DomainPreferLiteralSpec<C>>
            , _DomainPreferLiteralSpec<C> {

    }

    interface _DomainOptionSpec<C>
            extends Insert._MigrationOptionClause<_DomainNullOptionSpec<C>>
            , _DomainNullOptionSpec<C> {

    }

    /*-------------------below domain syntax sub insert -------------------*/


    interface _DomainSubInsertIntoClause<C> {

        <T> _DomainTableAliasSpec<C, T, SubInsert, SubReturningInsert> insertInto(SingleTableMeta<T> table);

    }

    interface _DomainSubWithCteSpec<C>
            extends DialectStatement._WithCteClause<C, SubStatement, _DomainSubInsertIntoClause<C>>
            , _DomainSubInsertIntoClause<C> {

    }


    interface _DomainSubPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_DomainSubWithCteSpec<C>>
            , _DomainSubWithCteSpec<C> {

    }

    interface _DomainSubNullOptionSpec<C>
            extends Insert._NullOptionClause<_DomainSubPreferLiteralSpec<C>>
            , _DomainSubPreferLiteralSpec<C> {

    }

    interface _DomainSubOptionSpec<C>
            extends Insert._MigrationOptionClause<_DomainSubNullOptionSpec<C>>
            , _DomainSubNullOptionSpec<C> {

    }



    /*-------------------below values insert syntax interfaces  -------------------*/

    interface _ValueStaticValuesLeftParenClause<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._StaticValueLeftParenClause<C, T, _ValueStaticValuesLeftParenSpec<C, T, I, Q>>
            , _DefaultValuesClause<_ValueStaticValuesLeftParenSpec<C, T, I, Q>> {

    }

    interface _ValueStaticValuesLeftParenSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ValueStaticValuesLeftParenClause<C, T, I, Q>
            , _OnConflictSpec<C, T, I, Q> {

    }

    interface _ValueColumnDefaultSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._ColumnDefaultClause<C, T, _ValueColumnDefaultSpec<C, T, I, Q>>
            , Insert._StaticValuesClause<_ValueStaticValuesLeftParenClause<C, T, I, Q>>
            , Insert._DynamicValuesClause<C, T, _OnConflictSpec<C, T, I, Q>> {

    }

    interface _ValueOverridingValueSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _OverridingValueClause<_ValueColumnDefaultSpec<C, T, I, Q>>
            , _ValueColumnDefaultSpec<C, T, I, Q> {

    }


    interface _ValueColumnListSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._ColumnListClause<C, T, _ValueOverridingValueSpec<C, T, I, Q>>
            , _ValueOverridingValueSpec<C, T, I, Q> {

    }


    interface _ValueAliasSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._AsClause<_ValueColumnListSpec<C, T, I, Q>>
            , _ValueColumnListSpec<C, T, I, Q> {

    }

    interface _ValueChildInsertIntoClause<C, P> {

        <T> _ValueAliasSpec<C, T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table);
    }


    interface _ValueChildWithCteSpec<C, P>
            extends DialectStatement._WithCteClause<C, SubStatement, _ValueChildInsertIntoClause<C, P>>
            , _ValueChildInsertIntoClause<C, P> {

    }

    interface _ValueParentStaticValuesLeftParenClause<C, P>
            extends Insert._StaticValueLeftParenClause<C, P, _ValueParentStaticValuesLeftParenSpec<C, P>>
            , _DefaultValuesClause<_ValueParentStaticValuesLeftParenSpec<C, P>> {

    }

    interface _ValueParentStaticValuesLeftParenSpec<C, P>
            extends _ValueParentStaticValuesLeftParenClause<C, P>
            , _ParentOnConflictSpec<C, P, _ValueChildWithCteSpec<C, P>> {

    }

    interface _ValueParentColumnDefaultSpec<C, P>
            extends Insert._ColumnDefaultClause<C, P, _ValueParentColumnDefaultSpec<C, P>>
            , _ValueParentStaticValuesLeftParenClause<C, P> {

    }

    interface _ValueParentOverridingValueSpec<C, P>
            extends _OverridingValueClause<_ValueParentColumnDefaultSpec<C, P>>
            , _ValueParentColumnDefaultSpec<C, P> {

    }


    interface _ValueParentColumnListSpec<C, P>
            extends Insert._ColumnListClause<C, P, _ValueParentOverridingValueSpec<C, P>>
            , _ValueParentOverridingValueSpec<C, P> {

    }

    interface _ValueParentAliasSpec<C, P> extends Statement._AsClause<_ValueParentColumnListSpec<C, P>>
            , _ValueParentColumnListSpec<C, P> {

    }

    interface _ValueInsertIntoClause<C> {

        <T> _ValueAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table);

        <P> _ValueParentAliasSpec<C, P> insertInto(ParentTableMeta<P> table);
    }


    interface _ValueWithCteSpec<C>
            extends DialectStatement._WithCteClause<C, SubStatement, _ValueInsertIntoClause<C>>
            , _ValueInsertIntoClause<C> {

    }


    interface _ValuePreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_ValueWithCteSpec<C>>
            , _ValueWithCteSpec<C> {

    }

    interface _ValueNullOptionSpec<C>
            extends Insert._NullOptionClause<_ValuePreferLiteralSpec<C>>
            , _ValuePreferLiteralSpec<C> {

    }

    interface _ValueOptionSpec<C>
            extends Insert._MigrationOptionClause<_ValueNullOptionSpec<C>>
            , _ValueNullOptionSpec<C> {

    }

    /*-------------------below value sub insert syntax interfaces-------------------*/


    interface _ValueSubInsertIntoClause<C> {

        <T> _ValueAliasSpec<C, T, SubInsert, SubReturningInsert> insertInto(SingleTableMeta<T> table);

    }


    interface _ValueSubWithCteSpec<C>
            extends DialectStatement._WithCteClause<C, SubStatement, _ValueSubInsertIntoClause<C>>
            , _ValueSubInsertIntoClause<C> {

    }


    interface _ValueSubPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_ValueSubWithCteSpec<C>>
            , _ValueSubWithCteSpec<C> {

    }

    interface _ValueSubNullOptionSpec<C>
            extends Insert._NullOptionClause<_ValueSubPreferLiteralSpec<C>>
            , _ValueSubPreferLiteralSpec<C> {

    }

    interface _ValueSubOptionSpec<C>
            extends Insert._MigrationOptionClause<_ValueSubNullOptionSpec<C>>
            , _ValueSubNullOptionSpec<C> {

    }

    /*-------------------below query insert syntax interfaces-------------------*/

    interface _QuerySpaceClause<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._SpaceSubQueryClause<C, _OnConflictSpec<C, T, I, Q>> {

    }

    interface _QueryOverridingValueSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _OverridingValueClause<_QuerySpaceClause<C, T, I, Q>>
            , _QuerySpaceClause<C, T, I, Q> {

    }

    interface _QueryColumnListClause<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._ColumnListClause<C, T, _QueryOverridingValueSpec<C, T, I, Q>> {

    }

    interface _QueryAliasSpec<C, T, I extends DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._AsClause<_QueryColumnListClause<C, T, I, Q>>
            , _QueryColumnListClause<C, T, I, Q> {

    }


    interface _QueryChildInsertIntoClause<C, P> {

        <T> _QueryAliasSpec<C, T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _QueryChildWithCteSpec<C, P>
            extends DialectStatement._WithCteClause<C, SubStatement, _QueryChildInsertIntoClause<C, P>>
            , _QueryChildInsertIntoClause<C, P> {

    }

    interface _QueryParentSpaceClause<C, P>
            extends Insert._SpaceSubQueryClause<C, _ParentOnConflictSpec<C, P, _QueryChildWithCteSpec<C, P>>> {

    }

    interface _QueryParentOverridingValueSpec<C, P>
            extends _OverridingValueClause<_QueryParentSpaceClause<C, P>>
            , _QueryParentSpaceClause<C, P> {

    }

    interface _QueryParentColumnListClause<C, P>
            extends Insert._ColumnListClause<C, P, _QueryParentOverridingValueSpec<C, P>> {

    }

    interface _QueryParentAliasSpec<C, P> extends Statement._AsClause<_QueryParentColumnListClause<C, P>>
            , _QueryParentColumnListClause<C, P> {

    }


    interface _QueryInsertIntoClause<C> {

        <T> _QueryAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table);

        <P> _QueryParentAliasSpec<C, P> insertInto(ParentTableMeta<P> table);

    }

    interface _QueryWithCteSpec<C>
            extends DialectStatement._WithCteClause<C, SubStatement, _QueryInsertIntoClause<C>>
            , _QueryInsertIntoClause<C> {

    }


    interface _QueryPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_QueryWithCteSpec<C>>
            , _QueryWithCteSpec<C> {

    }

    interface _QueryNullOptionSpec<C>
            extends Insert._NullOptionClause<_QueryPreferLiteralSpec<C>>
            , _QueryPreferLiteralSpec<C> {

    }

    interface _QueryOptionSpec<C>
            extends Insert._MigrationOptionClause<_QueryNullOptionSpec<C>>
            , _QueryNullOptionSpec<C> {

    }


    interface _QuerySubInsertIntoClause<C> {

        <T> _QueryAliasSpec<C, T, SubInsert, SubReturningInsert> insertInto(SingleTableMeta<T> table);

    }

    interface _QuerySubWithCteSpec<C>
            extends DialectStatement._WithCteClause<C, SubStatement, _QuerySubInsertIntoClause<C>>
            , _QuerySubInsertIntoClause<C> {

    }


    interface _QuerySubPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_QuerySubWithCteSpec<C>>
            , _QuerySubWithCteSpec<C> {

    }

    interface _QuerySubNullOptionSpec<C>
            extends Insert._NullOptionClause<_QuerySubPreferLiteralSpec<C>>
            , _QuerySubPreferLiteralSpec<C> {

    }

    interface _QuerySubOptionSpec<C>
            extends Insert._MigrationOptionClause<_QuerySubNullOptionSpec<C>>
            , _QuerySubNullOptionSpec<C> {

    }


}
