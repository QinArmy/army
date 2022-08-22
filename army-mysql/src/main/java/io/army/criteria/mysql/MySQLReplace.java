package io.army.criteria.mysql;

import io.army.criteria.DmlStatement;
import io.army.criteria.Hint;
import io.army.criteria.Insert;
import io.army.criteria.ReplaceInsert;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MySQLReplace extends ReplaceInsert, DmlStatement.DmlInsert {


    interface _ReplaceClause<C, RR> {

        RR replace(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers);

        RR replace(Function<C, List<Hint>> hints, List<MySQLModifier> modifiers);

    }


    /*-------------------below domain replace api interfaces -------------------*/

    interface _DomainDefaultSpec<C, T>
            extends Insert._ColumnDefaultClause<C, T, _DomainDefaultSpec<C, T>>
            , Insert._DomainValueClause<C, T, _ReplaceSpec> {

    }

    interface _DomainColumnListSpec<C, T>
            extends Insert._ColumnListClause<C, T, _DomainDefaultSpec<C, T>>
            , _DomainDefaultSpec<C, T> {

    }

    interface _DomainPartitionSpec<C, T>
            extends MySQLQuery._PartitionClause<C, _DomainColumnListSpec<C, T>>
            , _DomainColumnListSpec<C, T> {

    }

    interface _DomainChildIntoClause<C, P> {

        <T> _DomainPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }


    interface _DomainChildReplaceIntoSpec<C, P>
            extends _ReplaceClause<C, _DomainChildIntoClause<C, P>> {

        <T> _DomainPartitionSpec<C, T> replaceInto(ComplexTableMeta<P, T> table);
    }

    interface _DomainParentDefaultSpec<C, P>
            extends Insert._ColumnDefaultClause<C, P, _DomainParentDefaultSpec<C, P>>
            , Insert._DomainValueClause<C, P, _ReplaceSpec>
            , Insert._ChildPartClause<_DomainChildReplaceIntoSpec<C, P>> {

    }

    interface _DomainParentColumnsSpec<C, P>
            extends Insert._ColumnListClause<C, P, _DomainParentDefaultSpec<C, P>>
            , _DomainParentDefaultSpec<C, P> {

    }

    interface _DomainParentPartitionSpec<C, P>
            extends MySQLQuery._PartitionClause<C, _DomainParentColumnsSpec<C, P>>
            , _DomainParentColumnsSpec<C, P> {

    }

    interface _DomainIntoClause<C> {

        <T> _DomainPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T> _DomainParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }

    interface _DomainReplaceIntoSpec<C> extends _ReplaceClause<C, _DomainIntoClause<C>> {

        <T> _DomainPartitionSpec<C, T> replaceInto(SimpleTableMeta<T> table);

        <T> _DomainParentPartitionSpec<C, T> replaceInto(ParentTableMeta<T> table);

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

    interface _ValueStaticValuesLeftParenClause<C, T>
            extends Insert._StaticValueLeftParenClause<C, T, _ValueStaticValuesLeftParenSpec<C, T>> {

    }

    interface _ValueStaticValuesLeftParenSpec<C, T>
            extends _ValueStaticValuesLeftParenClause<C, T>, _ReplaceSpec {

    }

    interface _ValueDefaultSpec<C, T>
            extends Insert._ColumnDefaultClause<C, T, _ValueDefaultSpec<C, T>>
            , Insert._StaticValuesClause<_ValueStaticValuesLeftParenClause<C, T>>
            , Insert._DynamicValuesClause<C, T, _ReplaceSpec> {

    }

    interface _ValueColumnListSpec<C, T>
            extends Insert._ColumnListClause<C, T, _ValueDefaultSpec<C, T>>, _ValueDefaultSpec<C, T> {

    }

    interface _ValuePartitionSpec<C, T>
            extends MySQLQuery._PartitionClause<C, _ValueColumnListSpec<C, T>>, _ValueColumnListSpec<C, T> {

    }


    interface _ValueChildIntoClause<C, P> {

        <T> _ValuePartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }


    interface _ValueChildReplaceIntoSpec<C, P>
            extends _ReplaceClause<C, _ValueChildIntoClause<C, P>> {

        <T> _ValuePartitionSpec<C, T> replaceInto(ComplexTableMeta<P, T> table);
    }


    interface _ValueChildSpec<C, P> extends Insert._ChildPartClause<_ValueChildReplaceIntoSpec<C, P>>
            , _ReplaceSpec {

    }


    interface _ValueParentStaticValuesLeftParenClause<C, P>
            extends Insert._StaticValueLeftParenClause<C, P, _ValueParentStaticValuesLeftParenSpec<C, P>> {

    }

    interface _ValueParentStaticValuesLeftParenSpec<C, P>
            extends _ValueParentStaticValuesLeftParenClause<C, P>
            , _ValueChildSpec<C, P> {

    }

    interface _ValueParentDefaultSpec<C, P>
            extends Insert._ColumnDefaultClause<C, P, _ValueParentDefaultSpec<C, P>>
            , Insert._StaticValuesClause<_ValueParentStaticValuesLeftParenClause<C, P>>
            , Insert._DynamicValuesClause<C, P, _ValueChildSpec<C, P>> {

    }


    interface _ValueParentColumnsSpec<C, P>
            extends Insert._ColumnListClause<C, P, _ValueParentDefaultSpec<C, P>>
            , _ValueParentDefaultSpec<C, P> {

    }

    interface _ValueParentPartitionSpec<C, P>
            extends MySQLQuery._PartitionClause<C, _ValueParentColumnsSpec<C, P>>
            , _ValueParentColumnsSpec<C, P> {

    }

    interface _ValueIntoClause<C> {

        <T> _ValuePartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T> _ValueParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }

    interface _ValueReplaceIntoSpec<C> extends _ReplaceClause<C, _ValueIntoClause<C>> {

        <T> _ValuePartitionSpec<C, T> replaceInto(SimpleTableMeta<T> table);

        <T> _ValueParentPartitionSpec<C, T> replaceInto(ParentTableMeta<T> table);

    }


    interface _ValuePreferLiteralSpec<C> extends Insert._PreferLiteralClause<_ValueReplaceIntoSpec<C>>
            , _ValueReplaceIntoSpec<C> {

    }

    interface _ValueNullOptionSpec<C> extends Insert._NullOptionClause<_ValuePreferLiteralSpec<C>>
            , _ValuePreferLiteralSpec<C> {

    }

    interface _ValueReplaceOptionSpec<C> extends Insert._MigrationOptionClause<_ValueNullOptionSpec<C>>
            , _ValueNullOptionSpec<C> {

    }

    /*-------------------below assignment replace syntax api interfaces -------------------*/

    interface _AssignmentReplaceSetClause<C, T>
            extends Insert._AssignmentSetClause<C, T, _AssignmentReplaceSetSpec<C, T>> {

    }


    interface _AssignmentReplaceSetSpec<C, T>
            extends _AssignmentReplaceSetClause<C, T>, _ReplaceSpec {

    }

    interface _AssignmentPartitionSpec<C, T>
            extends MySQLQuery._PartitionClause<C, _AssignmentReplaceSetClause<C, T>>
            , _AssignmentReplaceSetClause<C, T> {

    }

    interface _AssignmentChildIntoClause<C, P> {

        <T> _AssignmentPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }


    interface _AssignmentChildReplaceIntoSpec<C, P>
            extends _ReplaceClause<C, _AssignmentChildIntoClause<C, P>> {

        <T> _AssignmentPartitionSpec<C, T> replaceInto(ComplexTableMeta<P, T> table);
    }


    interface _AssignmentParentReplaceSetClause<C, P>
            extends Insert._AssignmentSetClause<C, P, _AssignmentParentReplaceSetSpec<C, P>> {

    }


    interface _AssignmentParentReplaceSetSpec<C, P>
            extends _AssignmentParentReplaceSetClause<C, P>
            , Insert._ChildPartClause<_AssignmentChildReplaceIntoSpec<C, P>>
            , _ReplaceSpec {

    }


    interface _AssignmentParentPartitionSpec<C, P>
            extends MySQLQuery._PartitionClause<C, _AssignmentParentReplaceSetClause<C, P>>
            , _AssignmentParentReplaceSetClause<C, P> {

    }

    interface _AssignmentIntoClause<C> {

        <T> _AssignmentPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T> _AssignmentParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }

    interface _AssignmentReplaceIntoSpec<C> extends _ReplaceClause<C, _AssignmentIntoClause<C>> {

        <T> _AssignmentPartitionSpec<C, T> replaceInto(SimpleTableMeta<T> table);

        <T> _AssignmentParentPartitionSpec<C, T> replaceInto(ParentTableMeta<T> table);

    }

    interface _AssignmentPreferLiteralSpec<C> extends Insert._PreferLiteralClause<_AssignmentReplaceIntoSpec<C>>
            , _AssignmentReplaceIntoSpec<C> {

    }

    interface _AssignmentNullOptionSpec<C> extends Insert._NullOptionClause<_AssignmentPreferLiteralSpec<C>>
            , _AssignmentPreferLiteralSpec<C> {

    }

    interface _AssignmentOptionSpec<C> extends Insert._MigrationOptionClause<_AssignmentNullOptionSpec<C>>
            , _AssignmentNullOptionSpec<C> {

    }


    /*-------------------below query replace syntax api interfaces -------------------*/


    interface _QuerySubQueryClause<C> extends Insert._SpaceSubQueryClause<C, _ReplaceSpec> {

    }


    interface _QueryColumnListClause<C, T>
            extends Insert._ColumnListClause<C, T, _QuerySubQueryClause<C>> {

    }

    interface _QueryPartitionSpec<C, T>
            extends MySQLQuery._PartitionClause<C, _QueryColumnListClause<C, T>>
            , _QueryColumnListClause<C, T> {

    }

    interface _QueryChildIntoClause<C, P> {

        <T> _QueryPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }


    interface _QueryChildReplaceIntoSpec<C, P>
            extends _ReplaceClause<C, _QueryChildIntoClause<C, P>> {

        <T> _QueryPartitionSpec<C, T> replaceInto(ComplexTableMeta<P, T> table);
    }


    interface _QueryChildSpec<C, P> extends Insert._ChildPartClause<_QueryChildReplaceIntoSpec<C, P>>
            , _ReplaceSpec {

    }

    interface _QueryParentSubQueryClause<C, P>
            extends Insert._SpaceSubQueryClause<C, _QueryChildSpec<C, P>> {

    }

    interface _QueryParentColumnsClause<C, P>
            extends Insert._ColumnListClause<C, P, _QueryParentSubQueryClause<C, P>> {

    }


    interface _QueryParentPartitionSpec<C, P>
            extends MySQLQuery._PartitionClause<C, _QueryParentColumnsClause<C, P>>
            , _QueryParentColumnsClause<C, P> {

    }

    interface _QueryIntoClause<C> {

        <T> _QueryPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T> _QueryParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }

    interface _QueryReplaceIntoSpec<C> extends _ReplaceClause<C, _QueryIntoClause<C>> {

        <T> _QueryPartitionSpec<C, T> replaceInto(SimpleTableMeta<T> table);

        <T> _QueryParentPartitionSpec<C, T> replaceInto(ParentTableMeta<T> table);

    }


}
