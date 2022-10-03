package io.army.criteria;

import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

public interface StandardInsert extends StandardStatement {


    interface _StandardValueStaticLeftParenClause<C, T, I extends DmlInsert>
            extends Insert._StaticValueLeftParenClause<C, T, _ValueStaticLeftParenSpec<C, T, I>> {

    }

    interface _ValueStaticLeftParenSpec<C, T, I extends DmlInsert>
            extends _StandardValueStaticLeftParenClause<C, T, I>, DmlInsert._DmlInsertSpec<I> {

    }

    interface _ValuesColumnDefaultSpec<C, T, I extends DmlInsert>
            extends Insert._ColumnDefaultClause<C, T, _ValuesColumnDefaultSpec<C, T, I>>
            , Insert._DomainValueClause<C, T, DmlInsert._DmlInsertSpec<I>>
            , Insert._DynamicValuesClause<C, T, DmlInsert._DmlInsertSpec<I>>
            , Insert._StaticValuesClause<_StandardValueStaticLeftParenClause<C, T, I>> {

    }

    interface _InsertQuery<I extends DmlInsert> extends StandardQuery, DmlInsert._DmlInsertSpec<I> {

    }

    interface _ComplexColumnDefaultSpec<C, T, I extends DmlInsert> extends _ValuesColumnDefaultSpec<C, T, I>
            , Insert._SpaceSubQueryClause<C, DmlInsert._DmlInsertSpec<I>> {

        StandardQuery._SelectSpec<C, _InsertQuery<I>> space();
    }

    interface _ColumnListSpec<C, T, I extends DmlInsert>
            extends Insert._ColumnListClause<C, T, _ComplexColumnDefaultSpec<C, T, I>>
            , _ValuesColumnDefaultSpec<C, T, I> {

    }

    interface _ChildInsertIntoClause<C, P> {

        <T> _ColumnListSpec<C, T, Insert> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _ParentInsert<C, P> extends Insert, Insert._ChildPartClause<_ChildInsertIntoClause<C, P>> {

    }


    interface _PrimaryInsertIntoClause<C> {

        <T> _ColumnListSpec<C, T, Insert> insertInto(SimpleTableMeta<T> table);

        <P> _ColumnListSpec<C, P, _ParentInsert<C, P>> insertInto(ParentTableMeta<P> table);
    }

    interface _PrimaryPreferLiteralSpec<C> extends Insert._PreferLiteralClause<_PrimaryInsertIntoClause<C>>
            , _PrimaryInsertIntoClause<C> {

    }

    interface _PrimaryNullOptionSpec<C> extends Insert._NullOptionClause<_PrimaryPreferLiteralSpec<C>>
            , _PrimaryPreferLiteralSpec<C> {

    }

    interface _PrimaryOptionSpec<C> extends Insert._MigrationOptionClause<_PrimaryNullOptionSpec<C>>
            , _PrimaryNullOptionSpec<C> {

    }


}
