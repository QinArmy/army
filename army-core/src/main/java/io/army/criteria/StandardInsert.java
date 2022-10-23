package io.army.criteria;

import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

/**
 * <p>
 * This interface representing standard insert statement.
 * </p>
 *
 * @since 1.0
 */
public interface StandardInsert extends StandardStatement {


    interface _StandardValueStaticLeftParenClause<T, I extends Item>
            extends Insert._StaticValueLeftParenClause<T, _ValueStaticLeftParenSpec<T, I>> {

    }

    interface _ValueStaticLeftParenSpec<T, I extends Item>
            extends _StandardValueStaticLeftParenClause<T, I>, _DmlInsertClause<I> {

    }

    interface _ValuesColumnDefaultSpec<T, I extends Item>
            extends Insert._ColumnDefaultClause<T, _ValuesColumnDefaultSpec<T, I>>
            , Insert._DomainValueClause<T, _DmlInsertClause<I>>
            , Insert._DynamicValuesClause<T, _DmlInsertClause<I>>
            , Insert._StaticValuesClause<_StandardValueStaticLeftParenClause<T, I>> {

    }


    interface _ComplexColumnDefaultSpec<T, I extends Item> extends _ValuesColumnDefaultSpec<T, I>
            , DialectStatement._StaticSpaceClause<StandardQuery._SelectSpec<_DmlInsertClause<I>>> {

    }

    interface _ColumnListSpec<T, I extends Item>
            extends Insert._ColumnListClause<T, _ComplexColumnDefaultSpec<T, I>>
            , _ValuesColumnDefaultSpec<T, I> {

    }

    interface _ChildInsertIntoClause<P> extends Item {

        <T> _ColumnListSpec<T, Insert> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _PrimaryInsertIntoClause {

        <T> _ColumnListSpec<T, Insert> insertInto(SimpleTableMeta<T> table);

        <P> _ColumnListSpec<P, Insert._ParentInsert<_ChildInsertIntoClause<P>>> insertInto(ParentTableMeta<P> table);
    }

    interface _PrimaryPreferLiteralSpec extends Insert._PreferLiteralClause<_PrimaryInsertIntoClause>
            , _PrimaryInsertIntoClause {

    }

    interface _PrimaryNullOptionSpec extends Insert._NullOptionClause<_PrimaryPreferLiteralSpec>
            , _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec extends Insert._MigrationOptionClause<_PrimaryNullOptionSpec>
            , _PrimaryNullOptionSpec {

    }


}
