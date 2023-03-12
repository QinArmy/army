package io.army.criteria.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.InsertStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.function.Supplier;

public interface MySQLReplace extends  DialectStatement, Statement.DmlInsert {


    interface _ReplaceClause<RR> extends Item {

        RR replace(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

    }


    /*-------------------below  replace api interfaces -------------------*/

    interface _MySQLStaticValuesLeftParenClause<I extends Item, T>
            extends InsertStatement._StaticValueLeftParenClause<T, _StaticValuesLeftParenSpec<I, T>> {

    }

    interface _StaticValuesLeftParenSpec<I extends Item, T> extends _MySQLStaticValuesLeftParenClause<I, T>
            , _DmlInsertClause<I> {

    }

    interface _ValueColumnDefaultSpec<I extends Item, T>
            extends InsertStatement._ColumnDefaultClause<T, _ValueColumnDefaultSpec<I, T>>
            , InsertStatement._DomainValueClause<T, _DmlInsertClause<I>>
            , InsertStatement._StaticValuesClause<_MySQLStaticValuesLeftParenClause<I, T>>
            , InsertStatement._DynamicValuesClause<T, _DmlInsertClause<I>> {

    }

    interface _ComplexColumnDefaultSpec<I extends Item, T> extends _ValueColumnDefaultSpec<I, T>
            , _StaticSpaceClause<MySQLQuery._WithSpec<_DmlInsertClause<I>>> {

    }

    interface _ColumnListSpec<I extends Item, T>
            extends InsertStatement._ColumnListClause<T, _ComplexColumnDefaultSpec<I, T>>
            , _ValueColumnDefaultSpec<I, T> {

    }

    interface _PartitionSpec<I extends Item, T> extends MySQLStatement._PartitionClause_0<_ColumnListSpec<I, T>>
            , _ColumnListSpec<I, T> {

    }

    interface _ChildIntoClause<P> {

        <T> _PartitionSpec<InsertStatement, T> into(ComplexTableMeta<P, T> table);
    }


    interface _ChildReplaceIntoSpec<P> extends _ReplaceClause<_ChildIntoClause<P>> {

        <T> _PartitionSpec<InsertStatement, T> replaceInto(ComplexTableMeta<P, T> table);
    }


    interface _ParentReplace<P> extends InsertStatement, InsertStatement._ChildPartClause<_ChildReplaceIntoSpec<P>> {

    }

    interface _PrimaryIntoClause {

        <T> _PartitionSpec<InsertStatement, T> into(SingleTableMeta<T> table);

        <P> _PartitionSpec<_ParentReplace<P>, P> into(ParentTableMeta<P> table);

    }

    interface _PrimaryReplaceIntoSpec extends _ReplaceClause<_PrimaryIntoClause> {

        <T> _PartitionSpec<InsertStatement, T> replaceInto(SingleTableMeta<T> table);

        <P> _PartitionSpec<_ParentReplace<P>, P> replaceInto(ParentTableMeta<P> table);

    }

    interface _PrimaryPreferLiteralSpec
            extends InsertStatement._PreferLiteralClause<_PrimaryReplaceIntoSpec>, _PrimaryReplaceIntoSpec {

    }

    interface _PrimaryNullOptionSpec extends InsertStatement._NullOptionClause<_PrimaryPreferLiteralSpec>
            , _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec extends InsertStatement._MigrationOptionClause<_PrimaryNullOptionSpec>
            , _PrimaryNullOptionSpec {

    }


}
