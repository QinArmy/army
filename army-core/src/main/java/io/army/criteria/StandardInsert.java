package io.army.criteria;

import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

public interface StandardInsert extends StandardStatement {


    interface _StandardValueStaticLeftParenClause<C, T>
            extends Insert._StaticValueLeftParenClause<C, T, _ValueStaticLeftParenSpec<C, T>> {

    }

    interface _ValueStaticLeftParenSpec<C, T>
            extends _StandardValueStaticLeftParenClause<C, T>, Insert._InsertSpec {

    }

    interface _ValuesColumnDefaultSpec<C, T>
            extends Insert._ColumnDefaultClause<C, T, _ValuesColumnDefaultSpec<C, T>>
            , Insert._DomainValueClause<C, T, Insert._InsertSpec>
            , Insert._DynamicValuesClause<C, T, Insert._InsertSpec>
            , Insert._StaticValuesClause<_StandardValueStaticLeftParenClause<C, T>> {

    }

    interface _InsertQuery extends StandardQuery, Insert._InsertSpec {

    }

    interface _ComplexColumnDefaultSpec<C, T> extends _ValuesColumnDefaultSpec<C, T>
            , Insert._SpaceSubQueryClause<C, Insert._InsertSpec> {

        StandardQuery._StandardSelectClause<C, _InsertQuery> space();
    }

    interface _ColumnListSpec<C, T>
            extends Insert._ColumnListClause<C, T, _ComplexColumnDefaultSpec<C, T>>
            , _ValuesColumnDefaultSpec<C, T> {

    }

    interface _ChildInsertIntoClause<C, P> {

        <T> _ColumnListSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _ChildPartSpec<CT> extends Insert._ChildPartClause<CT>
            , Insert._InsertSpec {

    }

    interface _ParentValueStaticLeftParenClause<C, P, CT>
            extends Insert._StaticValueLeftParenClause<C, P, _ParentValueStaticLeftParenSpec<C, P, CT>> {

    }

    interface _ParentValueStaticLeftParenSpec<C, P, CT>
            extends _ParentValueStaticLeftParenClause<C, P, CT>, _ChildPartSpec<CT> {

    }

    interface _ParentValuesColumnDefaultSpec<C, P, CT>
            extends Insert._ColumnDefaultClause<C, P, _ParentValuesColumnDefaultSpec<C, P, CT>>
            , Insert._DomainValueClause<C, P, _ChildPartSpec<CT>>
            , Insert._DynamicValuesClause<C, P, _ChildPartSpec<CT>>
            , Insert._StaticValuesClause<_ParentValueStaticLeftParenClause<C, P, CT>> {

    }

    interface _ParentInsertQuery<CT> extends StandardQuery, _ChildPartSpec<CT> {

    }

    interface _ParentComplexColumnDefaultSpec<C, P, CT> extends _ParentValuesColumnDefaultSpec<C, P, CT>
            , Insert._SpaceSubQueryClause<C, _ChildPartSpec<CT>> {

        StandardQuery._StandardSelectClause<C, _ParentInsertQuery<CT>> space();
    }

    interface _ParentColumnListSpec<C, P, CT>
            extends Insert._ColumnListClause<C, P, _ParentComplexColumnDefaultSpec<C, P, CT>>
            , _ParentValuesColumnDefaultSpec<C, P, CT> {

    }

    interface _PrimaryInsertIntoClause<C> {

        <T> _ColumnListSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <P> _ParentColumnListSpec<C, P, _ChildInsertIntoClause<C, P>> insertInto(ParentTableMeta<P> table);
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
