package io.army.criteria.standard;

import io.army.criteria.DialectStatement;
import io.army.criteria.Insert;
import io.army.criteria.Item;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SingleTableMeta;

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
            , DialectStatement._StaticSpaceClause<StandardQuery._StandardSelectClause<_DmlInsertClause<I>>> {

    }

    interface _ColumnListSpec<T, I extends Item>
            extends Insert._ColumnListClause<T, _ComplexColumnDefaultSpec<T, I>>
            , _ValuesColumnDefaultSpec<T, I> {

    }

    interface _ChildInsertIntoClause<I extends Item, P> extends Item {

        <T> _ColumnListSpec<T, I> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _PrimaryInsertIntoClause<I extends Item> {

        <T> _ColumnListSpec<T, I> insertInto(SingleTableMeta<T> table);

        <P> _ColumnListSpec<P, Insert._ParentInsert<_ChildInsertIntoClause<I, P>>> insertInto(ParentTableMeta<P> table);
    }

    interface _PrimaryPreferLiteralSpec<I extends Item>
            extends Insert._PreferLiteralClause<_PrimaryInsertIntoClause<I>>, _PrimaryInsertIntoClause<I> {

    }

    interface _PrimaryNullOptionSpec<I extends Item> extends Insert._NullOptionClause<_PrimaryPreferLiteralSpec<I>>,
            _PrimaryPreferLiteralSpec<I> {

    }

    interface _PrimaryOptionSpec<I extends Item> extends Insert._MigrationOptionClause<_PrimaryNullOptionSpec<I>>,
            _PrimaryNullOptionSpec<I> {

    }


}
