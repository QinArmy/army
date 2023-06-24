package io.army.criteria.standard;

import io.army.criteria.InsertStatement;
import io.army.criteria.Item;
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
public interface StandardInsert extends StandardStatement, InsertStatement {


    interface _StandardValueStaticLeftParenClause<T, I extends Item>
            extends InsertStatement._StaticValueLeftParenClause<T, _ValueStaticLeftParenSpec<T, I>> {

    }

    interface _ValueStaticLeftParenSpec<T, I extends Item>
            extends _StandardValueStaticLeftParenClause<T, I>, _DmlInsertClause<I> {

    }

    interface _ValuesColumnDefaultSpec<T, I extends Item>
            extends InsertStatement._ColumnDefaultClause<T, _ValuesColumnDefaultSpec<T, I>>,
            InsertStatement._DomainValueClause<T, _DmlInsertClause<I>>,
            InsertStatement._DynamicValuesClause<T, _DmlInsertClause<I>>,
            InsertStatement._StaticValuesClause<_StandardValueStaticLeftParenClause<T, I>> {

    }


    interface _ComplexColumnDefaultSpec<T, I extends Item> extends _ValuesColumnDefaultSpec<T, I>,
            InsertStatement._QueryInsertSpaceClause<StandardQuery._SelectSpec<_DmlInsertClause<I>>, _DmlInsertClause<I>> {

    }

    interface _ColumnListSpec<T, I extends Item>
            extends InsertStatement._ColumnListParensClause<T, _ComplexColumnDefaultSpec<T, I>>,
            _ValuesColumnDefaultSpec<T, I> {

    }

    interface _ChildInsertIntoClause<I extends Item, P> extends Item {

        <T> _ColumnListSpec<T, I> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _PrimaryInsertIntoClause<I extends Item> extends Item {

        <T> _ColumnListSpec<T, I> insertInto(SimpleTableMeta<T> table);

        <P> _ColumnListSpec<P, _ParentInsert20<I, _ChildInsertIntoClause<I, P>>> insertInto(ParentTableMeta<P> table);
    }

    interface _PrimaryPreferLiteralSpec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_PrimaryInsertIntoClause<I>>,
            _PrimaryInsertIntoClause<I> {

    }

    interface _PrimaryNullOptionSpec<I extends Item>
            extends InsertStatement._NullOptionClause<_PrimaryPreferLiteralSpec<I>>,
            _PrimaryPreferLiteralSpec<I> {

    }

    interface _PrimaryOptionSpec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_PrimaryNullOptionSpec<I>>,
            _PrimaryNullOptionSpec<I> {

    }


    interface _WithSpec<I extends Item> extends _StandardDynamicWithClause<_PrimaryInsertIntoClause<I>>,
            _StandardStaticWithClause<_PrimaryInsertIntoClause<I>>,
            _PrimaryInsertIntoClause<I> {

    }

    interface _PrimaryPreferLiteral20Spec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_WithSpec<I>>, _WithSpec<I> {

    }

    interface _PrimaryNullOption20Spec<I extends Item>
            extends InsertStatement._NullOptionClause<_PrimaryPreferLiteral20Spec<I>>,
            _PrimaryPreferLiteral20Spec<I> {

    }

    interface _PrimaryOption20Spec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_PrimaryNullOption20Spec<I>>,
            _PrimaryNullOption20Spec<I> {

    }


}
