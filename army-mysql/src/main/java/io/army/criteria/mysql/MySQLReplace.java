package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MySQLReplace extends ReplaceInsert, DialectStatement, DmlStatement.DmlInsert {


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

    interface _DomainPartitionSpec<C, T extends IDomain, F extends TableField>
            extends MySQLQuery._PartitionClause<C, _DomainColumnListSpec<C, T, F>>
            , _DomainColumnListSpec<C, T, F> {

    }


    interface _DomainChildPartitionSpec<C, T extends IDomain, F extends TableField>
            extends MySQLInsert._ChildPartitionClause<C, _DomainColumnListSpec<C, T, F>>
            , _DomainColumnListSpec<C, T, F> {

    }

    interface _DomainParentPartitionSpec<C, T extends IDomain, F extends TableField>
            extends MySQLInsert._ParentPartitionClause<C, _DomainChildPartitionSpec<C, T, F>>
            , _DomainChildPartitionSpec<C, T, F> {

    }

    interface _DomainIntoClause<C> {

        <T extends IDomain> _DomainPartitionSpec<C, T, FieldMeta<T>> into(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T, FieldMeta<? super T>> into(ChildTableMeta<T> table);

    }

    interface _DomainReplaceIntoSpec<C> extends _ReplaceClause<C, _DomainIntoClause<C>> {

        <T extends IDomain> _DomainPartitionSpec<C, T, FieldMeta<T>> replaceInto(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T, FieldMeta<? super T>> replaceInto(ChildTableMeta<T> table);

    }

    interface _DomainPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_DomainReplaceIntoSpec<C>>, _DomainReplaceIntoSpec<C> {

    }

    interface _DomainNullOptionSpec<C> extends Insert._NullOptionClause<_DomainPreferLiteralSpec<C>>
            , _DomainPreferLiteralSpec<C> {

    }

    interface _DomainOptionSpec<C> extends Insert._MigrationOptionClause<_DomainNullOptionSpec<C>>
            , _DomainNullOptionSpec<C> {

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


    interface _ValueNullOptionSpec<C> extends Insert._NullOptionClause<MySQLReplace._ValueReplaceIntoSpec<C>>
            , MySQLReplace._ValueReplaceIntoSpec<C> {

    }

    interface _ValueReplaceOptionSpec<C> extends Insert._MigrationOptionClause<_ValueNullOptionSpec<C>>
            , _ValueNullOptionSpec<C> {

    }

    /*-------------------below assignment replace syntax api interfaces -------------------*/

    interface _AssignmentReplaceSetClause<C, F extends TableField>
            extends Insert._AssignmentSetClause<C, F, _AssignmentReplaceSetSpec<C, F>> {

    }


    interface _AssignmentReplaceSetSpec<C, F extends TableField>
            extends _AssignmentReplaceSetClause<C, F>, _ReplaceSpec {

    }

    interface _AssignmentPartitionSpec<C, F extends TableField>
            extends MySQLQuery._PartitionClause2<C, _AssignmentReplaceSetClause<C, F>>
            , _AssignmentReplaceSetClause<C, F> {

    }

    interface _AssignmentChildPartitionSpec<C, F extends TableField>
            extends MySQLInsert._ChildPartitionClause<C, _AssignmentReplaceSetClause<C, F>>
            , _AssignmentReplaceSetClause<C, F> {

    }

    interface _AssignmentParentPartitionSpec<C, F extends TableField>
            extends MySQLInsert._ParentPartitionClause<C, _AssignmentChildPartitionSpec<C, F>>
            , _AssignmentChildPartitionSpec<C, F> {

    }

    interface _AssignmentIntoClause<C> {

        <T extends IDomain> _AssignmentPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table);

        <T extends IDomain> _AssignmentParentPartitionSpec<C, FieldMeta<? super T>> into(ChildTableMeta<T> table);

    }

    interface _AssignmentReplaceIntoClause<C> extends _ReplaceClause<C, _AssignmentIntoClause<C>> {

        <T extends IDomain> _AssignmentPartitionSpec<C, FieldMeta<T>> replaceInto(SingleTableMeta<T> table);

        <T extends IDomain> _AssignmentParentPartitionSpec<C, FieldMeta<? super T>> replaceInto(ChildTableMeta<T> table);

    }

    interface _AssignmentNullOptionSpec<C> extends Insert._NullOptionClause<_AssignmentReplaceIntoClause<C>>
            , _AssignmentReplaceIntoClause<C> {

    }

    interface _AssignmentOptionSpec<C> extends Insert._MigrationOptionClause<_AssignmentNullOptionSpec<C>>
            , _AssignmentNullOptionSpec<C> {

    }


    /*-------------------below row set replace syntax api interfaces -------------------*/

    interface _RowSetStaticRowClause<C, F extends TableField>
            extends MySQLInsert._StaticValueRowClause<C, F, _RowSetStaticRowSpec<C, F>> {

    }

    interface _RowSetStaticRowSpec<C, F extends TableField> extends _RowSetStaticRowClause<C, F>
            , _ReplaceSpec {

    }

    interface _RowSetValuesClause<C, F extends TableField>
            extends Insert._StaticValuesClause<_RowSetStaticRowClause<C, F>>
            , Insert._DynamicValuesClause<C, F, _ReplaceSpec> {

    }


    interface _RowSetSubQueryClause<C> extends Insert._SpaceSubQueryClause<C, _ReplaceSpec> {

    }

    interface _RowSetSpec<C, F extends TableField> extends _RowSetSubQueryClause<C>
            , _RowSetValuesClause<C, F> {

    }


    interface _RowSetColumnListClause<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, MySQLReplace._RowSetSpec<C, F>> {

    }

    interface _RowSetPartitionSpec<C, F extends TableField>
            extends MySQLQuery._PartitionClause2<C, _RowSetColumnListClause<C, F>>
            , _RowSetColumnListClause<C, F> {

    }


    interface _RowSetChildValuesColumnsClause<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, _RowSetValuesClause<C, F>> {

    }

    interface _RowSetChildValuesPartitionSpec<C, F extends TableField>
            extends MySQLInsert._ChildPartitionClause<C, _RowSetChildValuesColumnsClause<C, F>>
            , _RowSetChildValuesColumnsClause<C, F> {

    }

    interface _RowSetParentStaticRowClause<C, PF extends TableField, TF extends TableField>
            extends MySQLInsert._StaticValueRowClause<C, PF, _RowSetParentStaticRowSpec<C, PF, TF>> {

    }

    interface _RowSetParentStaticRowSpec<C, PF extends TableField, TF extends TableField>
            extends _RowSetParentStaticRowClause<C, PF, TF>, _RowSetChildValuesPartitionSpec<C, TF> {

    }


    interface _RowChildSubQueryColumnsClause<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, _RowSetSubQueryClause<C>> {

    }

    interface _RowSetChildSubQueryPartitionSpec<C, F extends TableField>
            extends MySQLInsert._ChildPartitionClause<C, _RowChildSubQueryColumnsClause<C, F>>
            , _RowChildSubQueryColumnsClause<C, F> {

    }

    interface _RowSetParentSpec<C, PF extends TableField, TF extends TableField>
            extends Insert._SpaceSubQueryClause<C, _RowSetChildSubQueryPartitionSpec<C, TF>>
            , Insert._StaticValuesClause<_RowSetParentStaticRowClause<C, PF, TF>>
            , Insert._DynamicValuesClause<C, PF, _RowSetChildValuesPartitionSpec<C, TF>> {

    }

    interface _RowSetParentValueColumnsClause<C, PF extends TableField, TF extends TableField>
            extends Insert._ColumnListClause<C, PF, MySQLReplace._RowSetParentSpec<C, PF, TF>> {

    }


    interface _RowSetParentPartitionSpec<C, P extends IDomain, T extends IDomain>
            extends MySQLInsert._ParentPartitionClause<C, _RowSetParentValueColumnsClause<C, FieldMeta<P>, FieldMeta<T>>>
            , _RowSetParentValueColumnsClause<C, FieldMeta<P>, FieldMeta<T>> {

    }

    interface _RowSetIntoClause<C> {

        <T extends IDomain> _RowSetPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _RowSetParentPartitionSpec<C, P, T> into(ComplexTableMeta<P, T> table);

    }

    interface _RowSetReplaceIntoSpec<C> extends _ReplaceClause<C, _RowSetIntoClause<C>> {

        <T extends IDomain> _RowSetPartitionSpec<C, FieldMeta<T>> replaceInto(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _RowSetParentPartitionSpec<C, P, T> replaceInto(ComplexTableMeta<P, T> table);

    }


}
