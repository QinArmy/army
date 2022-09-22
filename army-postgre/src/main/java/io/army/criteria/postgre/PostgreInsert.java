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

    interface _ReturningCommaSpec<I extends DmlInsert> extends DmlStatement._DmlInsertSpec<I>
            , _StaticReturningCommaClause<DmlStatement._DmlInsertSpec<I>> {

    }


    interface _ReturningSpec<C, I extends DmlInsert> extends DmlStatement._DmlInsertSpec<I>
            , _DynamicReturningClause<C, DmlStatement._DmlInsertSpec<I>>
            , _StaticReturningClause<_ReturningCommaSpec<I>> {

    }


    interface _DoUpdateWhereAndSpec<C, T, I extends DmlInsert>
            extends Statement._MinWhereAndClause<C, _DoUpdateWhereAndSpec<C, T, I>>
            , _ReturningSpec<C, I> {

    }

    interface _DoUpdateWhereSpec<C, T, I extends DmlInsert>
            extends Statement._MinQueryWhereClause<C, _ReturningSpec<C, I>, _DoUpdateWhereAndSpec<C, T, I>>
            , _DoUpdateSetClause<C, T, I>
            , _ReturningSpec<C, I> {

    }


    interface _DoUpdateSetClause<C, T, I extends DmlInsert>
            extends Update._SimpleSetClause<C, FieldMeta<T>, _DoUpdateWhereSpec<C, T, I>> {


    }


    interface _NonParentConflictActionClause<C, T, I extends DmlInsert>
            extends _ConflictActionClause<_ReturningSpec<C, I>, _DoUpdateSetClause<C, T, I>> {

    }

    interface _ConflictTargetWhereAndSpec<C, T, I extends DmlInsert>
            extends Statement._MinWhereAndClause<C, _ConflictTargetWhereAndSpec<C, T, I>>
            , _NonParentConflictActionClause<C, T, I> {

    }

    interface _ConflictTargetWhereSpec<C, T, I extends DmlInsert> extends _NonParentConflictActionClause<C, T, I>
            , Statement._MinQueryWhereClause<C, _NonParentConflictActionClause<C, T, I>, _ConflictTargetWhereAndSpec<C, T, I>> {

    }


    interface _ConflictTargetCommaSpec<C, T, I extends DmlInsert>
            extends Statement._RightParenClause<_ConflictTargetWhereSpec<C, T, I>>
            , _ConflictTargetCommaClause<T, _ConflictCollateSpec<C, T, I>> {

    }


    interface _ConflictOpClassSpec<C, T, I extends DmlInsert> extends _ConflictTargetCommaSpec<C, T, I>
            , _ConflictOpClassClause<C, _ConflictTargetCommaSpec<C, T, I>> {

    }


    interface _ConflictCollateSpec<C, T, I extends DmlInsert> extends _ConflictOpClassSpec<C, T, I>
            , _ConflictCollateClause<_ConflictOpClassSpec<C, T, I>> {


    }


    interface _NonParentConflictItemClause<C, T, I extends DmlInsert>
            extends _ConflictItemClause<T, _ConflictCollateSpec<C, T, I>, _NonParentConflictActionClause<C, T, I>> {

    }


    interface _OnConflictSpec<C, T, I extends DmlInsert> extends _ReturningSpec<C, I>
            , _OnConflictClause<_NonParentConflictItemClause<C, T, I>> {

    }


    interface _PostgreChildSpec<CT> extends Insert._ChildPartClause<CT>, Insert._InsertSpec {

    }

    interface _ParentReturningCommaSpec<CT> extends _PostgreChildSpec<CT>
            , _StaticReturningCommaClause<_PostgreChildSpec<CT>> {

    }

    interface _ParentReturningSpec<C, CT> extends _PostgreChildSpec<CT>
            , _DynamicReturningClause<C, _PostgreChildSpec<CT>>
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

    interface _DomainValueSpec<C, T, I extends DmlInsert>
            extends Insert._DomainValueClause<C, T, _OnConflictSpec<C, T, I>>
            , _DefaultValuesClause<_OnConflictSpec<C, T, I>> {

    }

    interface _DomainColumnDefaultSpec<C, T, I extends DmlInsert>
            extends Insert._ColumnDefaultClause<C, T, _DomainColumnDefaultSpec<C, T, I>>
            , _DomainValueSpec<C, T, I> {

    }

    interface _DomainOveridingValueSpec<C, T, I extends DmlInsert> extends _DomainColumnDefaultSpec<C, T, I>
            , _OverridingValueClause<_DomainColumnDefaultSpec<C, T, I>> {

    }

    interface _DomainColumnListSpec<C, T, I extends DmlInsert>
            extends Insert._ColumnListClause<C, T, _DomainOveridingValueSpec<C, T, I>>
            , _DomainOveridingValueSpec<C, T, I> {

    }

    interface _DomainTableAliasSpec<C, T, I extends DmlInsert>
            extends Statement._AsClause<_DomainColumnListSpec<C, T, I>>
            , _DomainColumnListSpec<C, T, I> {


    }

    interface _DomainChildInsertIntoClause<C, P> {

        <T> _DomainTableAliasSpec<C, T, Insert> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _DomainChildWithSpec<C, P>
            extends DialectStatement._WithCteClause<C, SubStatement, _DomainChildInsertIntoClause<C, P>> {

    }

    interface _DomainChildClause<C, P> extends Insert._ChildPartClause<_DomainChildWithSpec<C, P>> {

    }


    interface _DomainParentReturningCommaSpec<C, P> extends _DomainChildClause<C, P>
            , _StaticReturningCommaClause<_DomainChildClause<C, P>> {

    }

    interface _DomainParentReturningSpec<C, P> extends _DomainChildClause<C, P>
            , _DynamicReturningClause<C, _DomainChildClause<C, P>>
            , _StaticReturningClause<_DomainParentReturningCommaSpec<C, P>> {

    }

    interface _DomainParentDoUpdateWhereAndSpec<C, P>
            extends Statement._MinWhereAndClause<C, _DomainParentDoUpdateWhereAndSpec<C, P>>
            , _DomainParentReturningSpec<C, P> {


    }

    interface _DomainParentDoUpdateWhereSpec<C, P>
            extends Statement._MinQueryWhereClause<C, _DomainParentReturningSpec<C, P>, _DomainParentDoUpdateWhereAndSpec<C, P>>
            , _DomainParentDoUpdateSetClause<C, P>
            , _DomainParentReturningSpec<C, P> {

    }


    interface _DomainParentDoUpdateSetClause<C, P>
            extends Update._SimpleSetClause<C, FieldMeta<P>, _DomainParentDoUpdateWhereSpec<C, P>> {


    }

    interface _DomainParentConflictActionSpec<C, P>
            extends _ConflictActionClause<_DomainParentReturningSpec<C, P>, _DomainParentDoUpdateSetClause<C, P>> {

    }

    interface _DomainParentConflictTargetWhereAndSpec<C, P>
            extends Statement._MinWhereAndClause<C, _DomainParentConflictTargetWhereAndSpec<C, P>>
            , _DomainParentConflictActionSpec<C, P> {

    }


    interface _DomainParentConflictTargetWhereSpec<C, P> extends _DomainParentConflictActionSpec<C, P>
            , Statement._MinQueryWhereClause<C, _DomainParentConflictActionSpec<C, P>, _DomainParentConflictTargetWhereAndSpec<C, P>> {

    }


    interface _DomainParentConflictTargetCommaSpec<C, P>
            extends Statement._RightParenClause<_DomainParentConflictTargetWhereSpec<C, P>>
            , _ConflictTargetCommaClause<P, _DomainParentConflictCollateSpec<C, P>> {

    }

    interface _DomainParentConflictOpClassSpec<C, P> extends _DomainParentConflictTargetCommaSpec<C, P>
            , _ConflictOpClassClause<C, _DomainParentConflictTargetCommaSpec<C, P>> {

    }


    interface _DomainParentConflictCollateSpec<C, P> extends _DomainParentConflictOpClassSpec<C, P>
            , _ConflictCollateClause<_DomainParentConflictOpClassSpec<C, P>> {

    }

    interface _DomainParentConflictItemClause<C, P>
            extends _ConflictItemClause<P, _DomainParentConflictCollateSpec<C, P>, _DomainParentConflictActionSpec<C, P>> {

    }


    interface _DomainParentOnConflictSpec<C, P> extends _DomainParentReturningSpec<C, P>
            , _OnConflictClause<_DomainParentConflictItemClause<C, P>> {

    }


    interface _DomainParentColumnDefaultSpec<C, P>
            extends Insert._ColumnDefaultClause<C, P, _DomainParentColumnDefaultSpec<C, P>>
            , _DomainParentOnConflictSpec<C, P>
            , _DomainValueSpec<C, P, Insert> {

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

        <T> _DomainTableAliasSpec<C, T, Insert> insertInto(SimpleTableMeta<T> table);

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

        <T> _DomainTableAliasSpec<C, T, SubInsert> insertInto(SingleTableMeta<T> table);

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

    interface _ValueStaticValuesLeftParenClause<C, T, I extends DmlInsert>
            extends Insert._StaticValueLeftParenClause<C, T, _ValueStaticValuesLeftParenSpec<C, T, I>> {

    }

    interface _ValueStaticValuesLeftParenSpec<C, T, I extends DmlInsert>
            extends _ValueStaticValuesLeftParenClause<C, T, I>
            , _OnConflictSpec<C, T, I> {

    }

    interface _ValueColumnDefaultSpec<C, T, I extends DmlInsert>
            extends Insert._ColumnDefaultClause<C, T, _ValueColumnDefaultSpec<C, T, I>>
            , Insert._StaticValuesClause<_ValueStaticValuesLeftParenClause<C, T, I>>
            , Insert._DynamicValuesClause<C, T, _OnConflictSpec<C, T, I>> {

    }

    interface _ValueOverridingValueSpec<C, T, I extends DmlInsert>
            extends _OverridingValueClause<_ValueColumnDefaultSpec<C, T, I>>
            , _ValueColumnDefaultSpec<C, T, I> {

    }


    interface _ValueColumnListSpec<C, T, I extends DmlInsert>
            extends Insert._ColumnListClause<C, T, _ValueOverridingValueSpec<C, T, I>>
            , _ValueOverridingValueSpec<C, T, I> {

    }


    interface _ValueAliasSpec<C, T, I extends DmlInsert> extends Statement._AsClause<_ValueColumnListSpec<C, T, I>>
            , _ValueColumnListSpec<C, T, I> {

    }

    interface _ValueChildInsertIntoClause<C, P, I extends DmlInsert> {

        <T> _ValueAliasSpec<C, T, I> insertInto(ComplexTableMeta<P, T> table);
    }


    interface _ValueChildWithCteSpec<C, P, I extends DmlInsert>
            extends DialectStatement._WithCteClause<C, SubStatement, _ValueChildInsertIntoClause<C, P, I>>
            , _ValueChildInsertIntoClause<C, P, I> {

    }

    interface _ValueChildClause<C, P, I extends DmlInsert>
            extends Insert._ChildPartClause<_ValueChildWithCteSpec<C, P, I>> {

    }


}
