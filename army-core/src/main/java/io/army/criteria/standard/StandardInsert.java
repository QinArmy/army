package io.army.criteria.standard;

import io.army.criteria.DialectStatement;
import io.army.criteria.Insert;
import io.army.criteria.InsertStatement;
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
            DialectStatement._StaticSpaceClause<StandardQuery._StandardSelectClause<_DmlInsertClause<I>>> {

    }

    interface _ColumnListSpec<T, I extends Item>
            extends InsertStatement._ColumnListClause<T, _ComplexColumnDefaultSpec<T, I>>,
            _ValuesColumnDefaultSpec<T, I> {

    }

    interface _ChildInsertIntoClause<I extends Item, P> extends Item {

        <T> _ColumnListSpec<T, I> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _PrimaryInsertIntoClause {

        <T> _ColumnListSpec<T, Insert> insertInto(SingleTableMeta<T> table);

        <P> _ColumnListSpec<P, InsertStatement._ParentInsert<_ChildInsertIntoClause<Insert, P>>> insertInto(ParentTableMeta<P> table);
    }

    interface _PrimaryPreferLiteralSpec
            extends InsertStatement._PreferLiteralClause<_PrimaryInsertIntoClause>, _PrimaryInsertIntoClause {

    }

    interface _PrimaryNullOptionSpec extends InsertStatement._NullOptionClause<_PrimaryPreferLiteralSpec>,
            _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec extends InsertStatement._MigrationOptionClause<_PrimaryNullOptionSpec>,
            _PrimaryNullOptionSpec {

    }


}
