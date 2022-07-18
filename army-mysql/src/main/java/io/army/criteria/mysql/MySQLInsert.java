package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
 */
public interface MySQLInsert extends Insert, DialectStatement {


    interface _InsertClause<C, IR> {

        IR insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers);

        IR insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers);

    }


    interface _ParentPartitionClause<C, PR> {

        MySQLQuery._PartitionLeftParenClause<C, PR> parentPartition();

    }


    interface _ChildPartitionClause<C, PR> {

        MySQLQuery._PartitionLeftParenClause<C, PR> childPartition();

    }


    interface _ColumnAliasClause<T extends IDomain, CR> extends _RightParenClause<CR> {

        _ColumnAliasClause<T, CR> comma(FieldMeta<T> field, String columnAlias);

    }

    interface _RowColumnAliasListClause<C, T extends IDomain, CR> {

        _RightParenClause<CR> leftParen(Consumer<BiConsumer<FieldMeta<T>, String>> consumer);

        _RightParenClause<CR> leftParen(BiConsumer<C, BiConsumer<FieldMeta<T>, String>> consumer);

        _ColumnAliasClause<T, CR> leftParen(FieldMeta<T> field, String columnAlias);

    }

    interface _StaticOnDuplicateKeyFieldUpdateClause<C, T extends IDomain, UR> {

        UR update(FieldMeta<T> field, @Nullable Object value);

        UR updateLiteral(FieldMeta<T> field, @Nullable Object value);

        UR updateExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        UR updateExp(FieldMeta<T> field, Function<C, ? extends Expression> function);

    }

    interface _StaticOnDuplicateKeyAliasUpdateClause<C, T extends IDomain, UR>
            extends _StaticOnDuplicateKeyFieldUpdateClause<C, T, UR> {
        UR update(String columnAlias, @Nullable Object value);

        UR updateLiteral(String columnAlias, @Nullable Object value);

        UR updateExp(String columnAlias, Supplier<? extends Expression> supplier);

        UR updateExp(String columnAlias, Function<C, ? extends Expression> supplier);

    }


    interface _StaticOnDuplicateKeyFieldClause<C, T extends IDomain, KR> {

        _StaticOnDuplicateKeyFieldUpdateClause<C, T, KR> onDuplicateKey();

    }

    interface _StaticOnDuplicateKeyAliasClause<C, T extends IDomain, KR> {

        _StaticOnDuplicateKeyAliasUpdateClause<C, T, KR> onDuplicateKey();

    }


    interface _DynamicOnDuplicateKeyUpdateClause<C, T extends IDomain, CC extends PairConsumer<FieldMeta<T>>> {

        Insert._InsertSpec onDuplicateKeyUpdate(Consumer<CC> consumer);

        Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

        Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<CC> consumer);

        Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

    }


    interface _StaticValueRowCommaSpec<C, F extends TableField, CR>
            extends Insert._CommaFieldValuePairClause<C, F, _StaticValueRowCommaSpec<C, F, CR>>
            , Statement._RightParenClause<CR> {

    }


    interface _StaticAssignmentCommaFieldSpec<C, T extends IDomain>
            extends Insert._CommaFieldValuePairClause<C, T, _StaticAssignmentCommaFieldSpec<C, T>>
            , Insert._InsertSpec {

    }

    interface _OnDuplicateKeyUpdateFieldSpec<C, T extends IDomain>
            extends _StaticOnDuplicateKeyFieldClause<C, T, _StaticAssignmentCommaFieldSpec<C, T>>
            , _DynamicOnDuplicateKeyUpdateClause<C, T, PairConsumer<FieldMeta<T>>>, Insert._InsertSpec {

    }


    interface _StaticCommaAliasValuePairSpec<C, T extends IDomain>
            extends Insert._CommaFieldValuePairClause<C, T, _StaticCommaAliasValuePairSpec<C, T>>
            , _CommaAliasValuePairClause<C, _StaticCommaAliasValuePairSpec<C, T>>, Insert._InsertSpec {

    }


    interface _OnDuplicateKeyUpdateAliasSpec<C, T extends IDomain>
            extends _StaticOnDuplicateKeyAliasClause<C, T, _StaticCommaAliasValuePairSpec<C, T>>
            , _DynamicOnDuplicateKeyUpdateClause<C, T, AliasColumnConsumer<FieldMeta<T>>>, Insert._InsertSpec {

    }


    interface _OnDuplicateKeyRowAliasClause<C, T extends IDomain>
            extends _RowColumnAliasListClause<C, T, _OnDuplicateKeyUpdateAliasSpec<C, T>> {

    }


    interface _AsRowAliasSpec<C, T extends IDomain>
            extends Statement._AsClause<_OnDuplicateKeyRowAliasClause<C, T>>, _OnDuplicateKeyUpdateFieldSpec<C, T> {

    }



    /*-------------------below domain insert syntax interfaces  -------------------*/


    interface _DomainInsertValueClause<C, T extends IDomain>
            extends Insert._DomainValueClause<C, T, _AsRowAliasSpec<C, T>> {

    }

    interface _DomainCommonExpSpec<C, T extends IDomain>
            extends _ColumnDefaultClause<C, T, _DomainCommonExpSpec<C, T>>
            , _DomainInsertValueClause<C, T> {

    }

    interface _DomainColumnListSpec<C, T extends IDomain>
            extends Insert._ColumnListClause<C, T, _DomainCommonExpSpec<C, T>>, _DomainCommonExpSpec<C, T> {

    }

    interface _DomainPartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _DomainColumnListSpec<C, T>>
            , _DomainColumnListSpec<C, T> {

    }

    interface _DomainChildIntoClause<C, P extends IDomain> {

        <T extends IDomain> _DomainPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }

    interface _DomainChildInsertIntoSpec<C, P extends IDomain> extends _InsertClause<C, _DomainChildIntoClause<C, P>> {

        <T extends IDomain> _DomainPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _DomainParentDefaultSpec<C, P extends IDomain>
            extends Insert._ColumnDefaultClause<C, P, _DomainParentDefaultSpec<C, P>>
            , Insert._DomainValueClause<C, P, _AsRowAliasSpec<C, P>>
            , Insert._ChildPartClause<_DomainChildInsertIntoSpec<C, P>> {

    }


    interface _DomainParentColumnsSpec<C, P extends IDomain>
            extends Insert._ColumnListClause<C, P, _DomainParentDefaultSpec<C, P>>
            , _DomainParentDefaultSpec<C, P> {

    }

    interface _DomainParentPartitionSpec<C, P extends IDomain>
            extends MySQLQuery._PartitionClause<C, _DomainParentColumnsSpec<C, P>>, _DomainParentColumnsSpec<C, P> {

    }


    interface _DomainIntoClause<C> {

        <T extends IDomain> _DomainPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }


    interface _DomainInsertIntoSpec<C> extends _InsertClause<C, _DomainIntoClause<C>> {

        <T extends IDomain> _DomainPartitionSpec<C, T> insertInto(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T> insertInto(ChildTableMeta<T> table);

    }

    interface _DomainPreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_DomainInsertIntoSpec<C>>, _DomainInsertIntoSpec<C> {

    }

    interface _DomainNullOptionSpec<C> extends Insert._NullOptionClause<_DomainPreferLiteralSpec<C>>
            , _DomainPreferLiteralSpec<C> {

    }

    interface _DomainOptionSpec<C> extends Insert._MigrationOptionClause<_DomainNullOptionSpec<C>>
            , _DomainNullOptionSpec<C> {

    }

    /*-------------------below value insert syntax interfaces-------------------*/

    interface _ValueStaticValuesLeftParenClause<C, T extends IDomain>
            extends Insert._StaticValueLeftParenClause<C, T, _ValueStaticValuesLeftParenSpec<C, T>> {

    }

    interface _ValueStaticValuesLeftParenSpec<C, T extends IDomain> extends _ValueStaticValuesLeftParenClause<C, T>
            , _AsRowAliasSpec<C, T> {

    }

    interface _ValueCommonExpSpec<C, T extends IDomain>
            extends _ColumnDefaultClause<C, T, _ValueCommonExpSpec<C, T>>
            , Insert._StaticValuesClause<_ValueStaticValuesLeftParenClause<C, T>>
            , Insert._DynamicValuesClause<C, T, _AsRowAliasSpec<C, T>> {

    }

    interface _ValueColumnListSpec<C, T extends IDomain>
            extends Insert._ColumnListClause<C, T, _ValueCommonExpSpec<C, T>>
            , _ValueCommonExpSpec<C, T> {

    }


    interface _ValuePartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _ValueColumnListSpec<C, T>>, _ValueColumnListSpec<C, T> {

    }

    interface _ValueChildIntoClause<C, P extends IDomain> {

        <T extends IDomain> _ValuePartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }

    interface _ValueChildInsertIntoSpec<C, P extends IDomain> extends _InsertClause<C, _ValueChildIntoClause<C, P>> {

        <T extends IDomain> _ValuePartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _ValueChildSpec<C, P extends IDomain> extends Insert._ChildPartClause<_ValueChildInsertIntoSpec<C, P>>
            , _AsRowAliasSpec<C, P> {

    }


    interface _ValueParentStaticValueLeftParenClause<C, P extends IDomain>
            extends Insert._StaticValueLeftParenClause<C, P, _ValueParentStaticValueLeftParenSpec<C, P>> {

    }

    interface _ValueParentStaticValueLeftParenSpec<C, P extends IDomain>
            extends _ValueParentStaticValueLeftParenClause<C, P>
            , _ValueChildSpec<C, P> {

    }


    interface _ValueParentDefaultSpec<C, P extends IDomain>
            extends _ColumnDefaultClause<C, P, _ValueParentDefaultSpec<C, P>>
            , Insert._StaticValuesClause<_ValueParentStaticValueLeftParenClause<C, P>>
            , Insert._DynamicValuesClause<C, P, _ValueChildSpec<C, P>> {

    }

    interface _ValueParentColumnsSpec<C, P extends IDomain>
            extends Insert._ColumnListClause<C, P, _ValueParentDefaultSpec<C, P>>
            , _ValueParentDefaultSpec<C, P> {

    }

    interface _ValueParentPartitionSpec<C, P extends IDomain>
            extends MySQLQuery._PartitionClause<C, _ValueParentColumnsSpec<C, P>>, _ValueParentColumnsSpec<C, P> {

    }


    interface _ValueIntoClause<C> {

        <T extends IDomain> _ValuePartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T extends IDomain> _ValueParentPartitionSpec<C, T> into(ParentTableMeta<T> table);
    }

    interface _ValueInsertIntoSpec<C> extends _InsertClause<C, _ValueIntoClause<C>> {

        <T extends IDomain> _ValuePartitionSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _ValueParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table);

    }

    interface _ValuePreferLiteralSpec<C> extends Insert._PreferLiteralClause<_ValueInsertIntoSpec<C>>
            , _ValueInsertIntoSpec<C> {

    }

    interface _ValueNullOptionSpec<C> extends Insert._NullOptionClause<_ValuePreferLiteralSpec<C>>
            , _ValuePreferLiteralSpec<C> {

    }

    interface _ValueOptionSpec<C> extends Insert._MigrationOptionClause<_ValueNullOptionSpec<C>>
            , _ValueNullOptionSpec<C> {

    }


    /*-------------------below assignment insert syntax interfaces-------------------*/


    interface _MySQLAssignmentSetClause<C, T extends IDomain>
            extends Insert._AssignmentSetClause<C, T, _MySQLAssignmentSetSpec<C, T>> {
    }

    interface _MySQLAssignmentSetSpec<C, T extends IDomain> extends _MySQLAssignmentSetClause<C, T>
            , _AsRowAliasSpec<C, T> {

    }

    interface _AssignmentPartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _MySQLAssignmentSetClause<C, T>>, _MySQLAssignmentSetClause<C, T> {

    }

    interface _AssignmentChildIntoClause<C, P extends IDomain> {

        <T extends IDomain> _AssignmentPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);

    }

    interface _AssignmentChildInsertIntoSpec<C, P extends IDomain>
            extends _InsertClause<C, _AssignmentChildIntoClause<C, P>> {

        <T extends IDomain> _AssignmentPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _AssignmentParentSetClause<C, P extends IDomain>
            extends Insert._AssignmentSetClause<C, P, _AssignmentParentSetSpec<C, P>> {

    }

    interface _AssignmentParentSetSpec<C, P extends IDomain> extends _AssignmentParentSetClause<C, P>
            , _AsRowAliasSpec<C, P>, Insert._ChildPartClause<_AssignmentChildInsertIntoSpec<C, P>> {

    }


    interface _AssignmentParentPartitionSpec<C, P extends IDomain>
            extends MySQLQuery._PartitionClause<C, _AssignmentParentSetSpec<C, P>>, _AssignmentParentSetSpec<C, P> {

    }

    interface _AssignmentIntoClause<C> {

        <T extends IDomain> _AssignmentPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T extends IDomain> _AssignmentParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }

    interface _AssignmentInsertIntoSpec<C> extends _InsertClause<C, _AssignmentIntoClause<C>> {

        <T extends IDomain> _AssignmentPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _AssignmentParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table);

    }


    interface _AssignmentPreferLiteralSpec<C> extends Insert._PreferLiteralClause<_AssignmentInsertIntoSpec<C>>
            , _AssignmentInsertIntoSpec<C> {

    }

    interface _AssignmentNullOptionSpec<C> extends Insert._NullOptionClause<_AssignmentPreferLiteralSpec<C>>
            , _AssignmentPreferLiteralSpec<C> {

    }

    interface _AssignmentOptionSpec<C> extends Insert._MigrationOptionClause<_AssignmentNullOptionSpec<C>>
            , _AssignmentNullOptionSpec<C> {

    }

    /*-------------------below query insert syntax interfaces-------------------*/


    interface _QuerySpaceSubQueryClause<C, F extends TableField>
            extends Insert._SpaceSubQueryClause<C, _OnDuplicateKeyUpdateFieldSpec<C, F>> {

    }


    interface _QueryColumnListClause<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, _QuerySpaceSubQueryClause<C, F>> {

    }

    interface _QueryPartitionSpec<C, F extends TableField>
            extends MySQLQuery._PartitionClause<C, _QueryColumnListClause<C, F>>, _QueryColumnListClause<C, F> {

    }

    interface _QueryChildSubQueryPartitionSpec<C, F extends TableField>
            extends _ChildPartitionClause<C, _QueryColumnListClause<C, F>>, _QueryColumnListClause<C, F> {

    }

    interface _QueryParentQuerySpec<C, F extends TableField>
            extends Insert._SpaceSubQueryClause<C, _QueryChildSubQueryPartitionSpec<C, F>> {

    }

    interface _QueryParentColumnListClause<C, PF extends TableField, TF extends TableField>
            extends Insert._ColumnListClause<C, PF, _QueryParentQuerySpec<C, TF>> {

    }

    interface _QueryParentPartitionSpec<C, P extends IDomain, T extends IDomain>
            extends _ParentPartitionClause<C, _QueryParentColumnListClause<C, FieldMeta<P>, FieldMeta<T>>>
            , _QueryParentColumnListClause<C, FieldMeta<P>, FieldMeta<T>> {

    }


    interface _QueryIntoClause<C> {

        <T extends IDomain> _QueryPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _QueryParentPartitionSpec<C, P, T> into(ComplexTableMeta<P, T> table);

    }

    interface _QueryInsertIntoSpec<C> extends _InsertClause<C, _QueryIntoClause<C>> {

        <T extends IDomain> _QueryPartitionSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _QueryParentPartitionSpec<C, P, T> insertInto(ComplexTableMeta<P, T> table);

    }


}
