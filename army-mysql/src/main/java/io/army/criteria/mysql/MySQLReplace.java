package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MySQLReplace extends DmlStatement, DialectStatement {


    interface _ReplaceSpec {

        MySQLReplace asReplace();

    }


    interface _ReplaceClause<C, RR> {

        RR replace(Supplier<List<Hint>> hints, List<MySQLWords> modifiers);

        RR replace(Function<C, List<Hint>> hints, List<MySQLWords> modifiers);

    }


    /*-------------------below domain replace api interfaces -------------------*/

    interface _DomainReplaceValueClause<C, T extends IDomain> extends Insert._DomainValueClause<C, T, _ReplaceSpec> {

    }

    interface _DomainCommonExpSpec<C, T extends IDomain, F extends TableField>
            extends Insert._CommonExpClause<C, F, _DomainCommonExpSpec<C, T, F>>, _DomainReplaceValueClause<C, T> {

    }

    interface _DomainColumnListSpec<C, T extends IDomain, F extends TableField>
            extends Insert._ColumnListClause<C, F, _DomainCommonExpSpec<C, T, F>>
            , _DomainCommonExpSpec<C, T, F> {

    }

    interface _DomainPartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _DomainColumnListSpec<C, T, FieldMeta<T>>>
            , _DomainColumnListSpec<C, T, FieldMeta<T>> {

    }


    interface _DomainChildPartitionSpec<C, T extends IDomain>
            extends MySQLInsert._ChildPartitionClause<C, _DomainColumnListSpec<C, T, FieldMeta<? super T>>>
            , _DomainColumnListSpec<C, T, FieldMeta<? super T>> {

    }

    interface _DomainParentPartitionSpec<C, T extends IDomain>
            extends MySQLInsert._ParentPartitionClause<C, _DomainChildPartitionSpec<C, T>>
            , _DomainChildPartitionSpec<C, T> {

    }

    interface _DomainIntoClause<C> {

        <T extends IDomain> _DomainPartitionSpec<C, T> into(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T> into(ChildTableMeta<T> table);

    }

    interface _DomainReplaceIntoSpec<C> extends _ReplaceClause<C, _DomainIntoClause<C>> {

        <T extends IDomain> _DomainPartitionSpec<C, T> replaceInto(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T> replaceInto(ChildTableMeta<T> table);

    }

    interface _PreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_DomainReplaceIntoSpec<C>>, _DomainReplaceIntoSpec<C> {

    }

    interface _DomainOptionSpec<C> extends Insert._NullOptionClause<MySQLReplace._PreferLiteralSpec<C>>
            , Insert._MigrationOptionClause<MySQLReplace._PreferLiteralSpec<C>>, MySQLReplace._PreferLiteralSpec<C> {

    }


    /*-------------------below value replace syntax api interfaces -------------------*/

    interface _ValueStaticValuesLeftParenClause<C, F extends TableField>
            extends Insert._StaticValueLeftParenClause<C, F, _ValueStaticValuesLeftParenSpec<C, F>> {

    }

    interface _ValueStaticValuesLeftParenSpec<C, F extends TableField>
            extends _ValueStaticValuesLeftParenClause<C, F>, _ReplaceSpec {

    }

    interface _ValueReplaceValueClauseSpec<C, F extends TableField>
            extends Insert._StaticValueClause<Insert._StaticValueLeftParenClause<C, F, _ReplaceSpec>>
            , Insert._DynamicValueClause<C, F, _ReplaceSpec>
            , Insert._StaticValuesClause<_ValueStaticValuesLeftParenClause<C, F>>
            , Insert._DynamicValuesClause<C, F, _ReplaceSpec> {

    }

    interface _ValueCommonExpSpec<C, F extends TableField>
            extends Insert._CommonExpClause<C, F, _ValueCommonExpSpec<C, F>>, _ValueReplaceValueClauseSpec<C, F> {

    }

    interface _ValueColumnListSpec<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, _ValueCommonExpSpec<C, F>>, _ValueCommonExpSpec<C, F> {

    }

    interface _ValuePartitionSpec<C, F extends TableField>
            extends MySQLQuery._PartitionClause<C, _ValueColumnListSpec<C, F>>, _ValueColumnListSpec<C, F> {

    }


    interface _ValueChildPartitionSpec<C, F extends TableField>
            extends MySQLInsert._ChildPartitionClause<C, _ValueColumnListSpec<C, F>>
            , _ValueColumnListSpec<C, F> {

    }

    interface _ValueParentPartitionSpec<C, F extends TableField>
            extends MySQLInsert._ParentPartitionClause<C, _ValueChildPartitionSpec<C, F>>
            , _ValueChildPartitionSpec<C, F> {

    }

    interface _ValueIntoClause<C> {

        <T extends IDomain> _ValuePartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table);

        <T extends IDomain> _ValueParentPartitionSpec<C, FieldMeta<? super T>> into(ChildTableMeta<T> table);

    }

    interface _ValueReplaceIntoSpec<C> extends _ReplaceClause<C, _ValueIntoClause<C>> {

        <T extends IDomain> _ValuePartitionSpec<C, FieldMeta<T>> replaceInto(SingleTableMeta<T> table);

        <T extends IDomain> _ValueParentPartitionSpec<C, FieldMeta<? super T>> replaceInto(ChildTableMeta<T> table);

    }

    interface _ValueReplaceOptionSpec<C> extends Insert._NullOptionClause<MySQLReplace._ValueReplaceIntoSpec<C>>
            , Insert._MigrationOptionClause<MySQLReplace._ValueReplaceIntoSpec<C>> {

    }


}
