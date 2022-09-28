package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.meta.*;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface PostgreInsert extends DialectStatement {

    interface _StaticReturningCommaUnaryClause<Q extends DqlStatement.DqlInsert>
            extends DqlStatement._DqlInsertSpec<Q> {

        _StaticReturningCommaUnaryClause<Q> comma(SelectItem selectItem);
    }


    interface _StaticReturningCommaDualClause<Q extends DqlStatement.DqlInsert>
            extends DqlStatement._DqlInsertSpec<Q> {

        DqlStatement._DqlInsertSpec<Q> comma(SelectItem selectItem);

        _StaticReturningCommaDualClause<Q> comma(SelectItem selectItem1, SelectItem selectItem2);

    }

    interface _ReturningClause<C, Q extends DqlStatement.DqlInsert> {

        DqlStatement._DqlInsertSpec<Q> returning();

        _StaticReturningCommaUnaryClause<Q> returning(SelectItem selectItem);

        _StaticReturningCommaDualClause<Q> returning(SelectItem selectItem1, SelectItem selectItem2);

        DqlStatement._DqlInsertSpec<Q> returning(Consumer<Consumer<SelectItem>> consumer);

        DqlStatement._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer);
    }


    interface _ConflictActionClause<NR, UR> {

        NR doNothing();

        UR doUpdate();
    }

    interface _ConflictOpClassClause<R> {

        R opClass();

        R ifOpClass(BooleanSupplier supplier);

    }

    interface _ConflictTargetCommaClause<T, R> {

        R comma(IndexFieldMeta<T> indexColumn);
    }

    interface _ConflictCollateClause<R> {

        R collation(String collationName);

        R collation(Supplier<String> supplier);

        R ifCollation(Supplier<String> supplier);
    }

    interface _OnConflictClause<R> {

        R onConflict();
    }

    interface _ConflictItemClause<T, LR, OR> {

        LR leftParen(IndexFieldMeta<T> indexColumn);

        OR onConstraint(String constraintName);
    }


    interface _OverridingValueClause<OR> {

        OR overridingSystemValue();

        OR overridingUserValue();

        OR ifOverridingSystemValue(BooleanSupplier supplier);

        OR ifOverridingUserValue(BooleanSupplier supplier);

    }


    interface _ReturningSpec<C, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert> extends DmlStatement._DmlInsertSpec<I>
            , _ReturningClause<C, Q> {

    }

    interface _DoUpdateWhereAndSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._MinWhereAndClause<C, _DoUpdateWhereAndSpec<C, T, I, Q>>
            , _ReturningSpec<C, I, Q> {

    }

    interface _DoUpdateWhereSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._MinQueryWhereClause<C, _ReturningSpec<C, I, Q>, _DoUpdateWhereAndSpec<C, T, I, Q>>
            , _DoUpdateSetClause<C, T, I, Q>
            , _ReturningSpec<C, I, Q> {

    }


    interface _DoUpdateSetClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Update._SetClause<C, FieldMeta<T>, _DoUpdateWhereSpec<C, T, I, Q>> {


    }


    interface _NonParentConflictActionClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ConflictActionClause<_ReturningSpec<C, I, Q>, _DoUpdateSetClause<C, T, I, Q>> {

    }

    interface _ConflictTargetWhereAndSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._MinWhereAndClause<C, _ConflictTargetWhereAndSpec<C, T, I, Q>>
            , _NonParentConflictActionClause<C, T, I, Q> {

    }

    interface _ConflictTargetWhereSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _NonParentConflictActionClause<C, T, I, Q>
            , Statement._MinQueryWhereClause<C, _NonParentConflictActionClause<C, T, I, Q>, _ConflictTargetWhereAndSpec<C, T, I, Q>> {

    }


    interface _ConflictTargetCommaSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._RightParenClause<_ConflictTargetWhereSpec<C, T, I, Q>>
            , _ConflictTargetCommaClause<T, _ConflictCollateSpec<C, T, I, Q>> {

    }


    interface _ConflictOpClassSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ConflictTargetCommaSpec<C, T, I, Q>
            , _ConflictOpClassClause<_ConflictTargetCommaSpec<C, T, I, Q>> {

    }


    interface _ConflictCollateSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ConflictOpClassSpec<C, T, I, Q>
            , _ConflictCollateClause<_ConflictOpClassSpec<C, T, I, Q>> {


    }


    interface _NonParentConflictItemClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ConflictItemClause<T, _ConflictCollateSpec<C, T, I, Q>, _NonParentConflictActionClause<C, T, I, Q>> {

    }


    interface _OnConflictSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ReturningSpec<C, I, Q>
            , _OnConflictClause<_NonParentConflictItemClause<C, T, I, Q>> {

    }


    interface _PostgreChildSpec<CT, I extends DmlStatement.DmlInsert>
            extends Insert._ChildPartClause<CT>, DmlStatement._DmlInsertSpec<I> {

    }

    interface _PostgreChildReturnSpec<CT, Q extends DqlStatement.DqlInsert> extends Insert._ChildPartClause<CT>
            , DqlStatement._DqlInsertSpec<Q> {

    }

    interface _ParentReturningCommaUnaryClause<CT, Q extends DqlStatement.DqlInsert>
            extends _StaticReturningCommaUnaryClause<Q>, _PostgreChildReturnSpec<CT, Q> {

        _ParentReturningCommaUnaryClause<CT, Q> comma(SelectItem selectItem);

    }


    interface _ParentReturningCommaDualClause<CT, Q extends DqlStatement.DqlInsert>
            extends _StaticReturningCommaDualClause<Q>, _PostgreChildReturnSpec<CT, Q> {

        _PostgreChildReturnSpec<CT, Q> comma(SelectItem selectItem);

        _ParentReturningCommaDualClause<CT, Q> comma(SelectItem selectItem1, SelectItem selectItem2);

    }

    interface _ParentReturningClause<C, CT, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ReturningClause<C, Q>, _PostgreChildSpec<CT, I> {

        @Override
        _PostgreChildReturnSpec<CT, Q> returning();

        @Override
        _ParentReturningCommaUnaryClause<CT, Q> returning(SelectItem selectItem);

        @Override
        _ParentReturningCommaDualClause<CT, Q> returning(SelectItem selectItem1, SelectItem selectItem2);

        @Override
        _PostgreChildReturnSpec<CT, Q> returning(Consumer<Consumer<SelectItem>> consumer);

        @Override
        _PostgreChildReturnSpec<CT, Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer);

    }


    interface _ParentReturningSpec<C, P> extends _PostgreChildSpec<_ChildWithCteSpec<C, P>, Insert>
            , _ParentReturningClause<C, _ChildWithCteSpec<C, P>, Insert, ReturningInsert> {
    }

    interface _ParentDoUpdateWhereAndSpec<C, P>
            extends Statement._MinWhereAndClause<C, _ParentDoUpdateWhereAndSpec<C, P>>
            , _ParentReturningSpec<C, P> {

    }

    interface _ParentDoUpdateWhereSpec<C, P>
            extends Statement._MinQueryWhereClause<C, _ParentReturningSpec<C, P>, _ParentDoUpdateWhereAndSpec<C, P>>
            , _ParentDoUpdateSetClause<C, P>
            , _ParentReturningSpec<C, P> {

    }

    interface _ParentDoUpdateSetClause<C, P>
            extends Update._SetClause<C, FieldMeta<P>, _ParentDoUpdateWhereSpec<C, P>> {


    }

    interface _ParentConflictActionClause<C, P>
            extends _ConflictActionClause<_ParentReturningSpec<C, P>, _ParentDoUpdateSetClause<C, P>> {

    }

    interface _ParentConflictTargetWhereAndSpec<C, P>
            extends Statement._MinWhereAndClause<C, _ParentConflictTargetWhereAndSpec<C, P>>
            , _ParentConflictActionClause<C, P> {

    }


    interface _ParentConflictTargetWhereSpec<C, P> extends _ParentConflictActionClause<C, P>
            , Statement._MinQueryWhereClause<C, _ParentConflictActionClause<C, P>, _ParentConflictTargetWhereAndSpec<C, P>> {

    }

    interface _ParentConflictTargetCommaSpec<C, P>
            extends Statement._RightParenClause<_ParentConflictTargetWhereSpec<C, P>>
            , _ConflictTargetCommaClause<P, _ParentConflictCollateSpec<C, P>> {

    }


    interface _ParentConflictOpClassSpec<C, P> extends _ParentConflictTargetCommaSpec<C, P>
            , _ConflictOpClassClause<_ParentConflictTargetCommaSpec<C, P>> {

    }


    interface _ParentConflictCollateSpec<C, P> extends _ParentConflictOpClassSpec<C, P>
            , _ConflictCollateClause<_ParentConflictOpClassSpec<C, P>> {


    }

    interface _ParentConflictItemClause<C, P>
            extends _ConflictItemClause<P, _ParentConflictCollateSpec<C, P>, _ParentConflictActionClause<C, P>> {

    }


    interface _ParentOnConflictSpec<C, P> extends _ParentReturningSpec<C, P>
            , _OnConflictClause<_ParentConflictItemClause<C, P>> {

    }



    /*-------------------below insert syntax interfaces  -------------------*/

    interface _ValuesLeftParenClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._StaticValueLeftParenClause<C, T, _ValuesLeftParenSpec<C, T, I, Q>> {

    }

    interface _ValuesLeftParenSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ValuesLeftParenClause<C, T, I, Q>, _OnConflictSpec<C, T, I, Q> {

    }


    interface _ValuesDefaultSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._ColumnDefaultClause<C, T, _ValuesDefaultSpec<C, T, I, Q>>
            , Insert._DomainValueClause<C, T, _OnConflictSpec<C, T, I, Q>>
            , Insert._DynamicValuesClause<C, T, _OnConflictSpec<C, T, I, Q>>
            , Insert._StaticValuesClause<_ValuesLeftParenClause<C, T, I, Q>> {

    }


    interface _SpaceSubQuerySpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._SpaceSubQueryClause<C, _OnConflictSpec<C, T, I, Q>> {

    }


    interface _ComplexColumnDefaultSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ValuesDefaultSpec<C, T, I, Q>, _SpaceSubQuerySpec<C, T, I, Q> {

    }

    interface _OverridingValueSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _ValuesDefaultSpec<C, T, I, Q>
            , _OverridingValueClause<_ValuesDefaultSpec<C, T, I, Q>> {

    }

    interface _ComplexOverridingValueSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends _OverridingValueSpec<C, T, I, Q> {

        @Override
        _ComplexColumnDefaultSpec<C, T, I, Q> overridingSystemValue();

        @Override
        _ComplexColumnDefaultSpec<C, T, I, Q> overridingUserValue();

    }


    interface _ColumnListSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Insert._ColumnListClause<C, T, _ComplexOverridingValueSpec<C, T, I, Q>>
            , _OverridingValueSpec<C, T, I, Q> {

    }

    interface _TableAliasSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends Statement._AsClause<_ColumnListSpec<C, T, I, Q>>
            , _ColumnListSpec<C, T, I, Q> {


    }

    interface _ChildInsertIntoClause<C, P> {

        <T> _TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _ChildWithCteSpec<C, P>
            extends _WithCteClause2<C, SubStatement, _ChildInsertIntoClause<C, P>>
            , _ChildInsertIntoClause<C, P> {

    }


    interface _ParentValuesLeftParenClause<C, P>
            extends Insert._StaticValueLeftParenClause<C, P, _ParentValuesLeftParenSpec<C, P>> {

    }

    interface _ParentValuesLeftParenSpec<C, P>
            extends _ParentValuesLeftParenClause<C, P>, _ParentOnConflictSpec<C, P> {

    }


    interface _ParentValuesDefaultSpec<C, P>
            extends Insert._ColumnDefaultClause<C, P, _ParentValuesDefaultSpec<C, P>>
            , Insert._DomainValueClause<C, P, _ParentOnConflictSpec<C, P>>
            , Insert._DynamicValuesClause<C, P, _ParentOnConflictSpec<C, P>>
            , Insert._StaticValuesClause<_ParentValuesLeftParenClause<C, P>> {

    }


    interface _ParentSpaceSubQuerySpec<C, P>
            extends Insert._SpaceSubQueryClause<C, _ParentOnConflictSpec<C, P>> {

    }


    interface _ParentComplexColumnDefaultSpec<C, P>
            extends _ParentValuesDefaultSpec<C, P>, _ParentSpaceSubQuerySpec<C, P> {

    }

    interface _ParentOverridingValueSpec<C, P>
            extends _ParentValuesDefaultSpec<C, P>
            , _OverridingValueClause<_ParentValuesDefaultSpec<C, P>> {

    }

    interface _ParentComplexOverridingValueSpec<C, P>
            extends _ParentOverridingValueSpec<C, P> {

        @Override
        _ParentComplexColumnDefaultSpec<C, P> overridingSystemValue();

        @Override
        _ParentComplexColumnDefaultSpec<C, P> overridingUserValue();

    }


    interface _ParentColumnListSpec<C, P>
            extends Insert._ColumnListClause<C, P, _ParentComplexOverridingValueSpec<C, P>>
            , _ParentOverridingValueSpec<C, P> {

    }

    interface _ParentTableAliasSpec<C, P>
            extends Statement._AsClause<_ParentColumnListSpec<C, P>>
            , _ParentColumnListSpec<C, P> {

    }


    interface _PrimaryInsertIntoClause<C> {

        <T> _TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table);

        <T> _TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table);

        <P> _ParentTableAliasSpec<C, P> insertInto(ParentTableMeta<P> table);
    }

    interface _PrimaryWithCteSpec<C>
            extends PostgreQuery._PostgreDynamicWithSpec<C>
            , DialectStatement._StaticWithCteClause<PostgreQuery._PostgreComplexCommandSpec<C>>
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

    /*-------------------below sub insert syntax -------------------*/


    interface _SubInsertIntoClause<C> {

        <T> _TableAliasSpec<C, T, SubInsert, SubReturningInsert> insertInto(SimpleTableMeta<T> table);

        <T> _TableAliasSpec<C, T, SubInsert, SubReturningInsert> insertInto(ChildTableMeta<T> table);

        <T> _TableAliasSpec<C, T, SubInsert, SubReturningInsert> insertInto(ParentTableMeta<T> table, Enum<?> discriminator);

    }

    interface _SubWithCteSpec<C>
            extends PostgreQuery._PostgreDynamicWithSpec<C>
            , _SubInsertIntoClause<C> {

    }


    interface _SubPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_SubWithCteSpec<C>>
            , _SubWithCteSpec<C> {

    }

    interface _SubNullOptionSpec<C>
            extends Insert._NullOptionClause<_SubPreferLiteralSpec<C>>
            , _SubPreferLiteralSpec<C> {

    }

    interface _SubOptionSpec<C>
            extends Insert._MigrationOptionClause<_SubNullOptionSpec<C>>
            , _SubNullOptionSpec<C> {

    }


}
