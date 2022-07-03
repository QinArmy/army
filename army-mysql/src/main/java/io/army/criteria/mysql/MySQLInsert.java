package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;

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


    interface _ColumnAliasClause<F extends TableField, CR> extends _RightParenClause<CR> {

        _ColumnAliasClause<F, CR> comma(F field, String columnAlias);

    }

    interface _RowColumnAliasListClause<C, F extends TableField, CR> {

        _RightParenClause<CR> leftParen(Consumer<BiConsumer<F, String>> consumer);

        _RightParenClause<CR> leftParen(BiConsumer<C, BiConsumer<F, String>> consumer);

        _ColumnAliasClause<F, CR> leftParen(F field, String columnAlias);

    }

    interface _StaticOnDuplicateKeyFieldUpdateClause<C, F extends TableField, UR> {

        UR update(F field, @Nullable Object value);

        UR updateLiteral(F field, @Nullable Object value);

        UR updateExp(F field, Supplier<? extends Expression> supplier);

        UR updateExp(F field, Function<C, ? extends Expression> function);

    }

    interface _StaticOnDuplicateKeyAliasUpdateClause<C, F extends TableField, UR>
            extends _StaticOnDuplicateKeyFieldUpdateClause<C, F, UR> {
        UR update(String columnAlias, @Nullable Object value);

        UR updateLiteral(String columnAlias, @Nullable Object value);

        UR updateExp(String columnAlias, Supplier<? extends Expression> supplier);

        UR updateExp(String columnAlias, Function<C, ? extends Expression> supplier);

    }


    interface _StaticOnDuplicateKeyFieldClause<C, F extends TableField, KR> {

        _StaticOnDuplicateKeyFieldUpdateClause<C, F, KR> onDuplicateKey();

    }

    interface _StaticOnDuplicateKeyAliasClause<C, F extends TableField, KR> {

        _StaticOnDuplicateKeyAliasUpdateClause<C, F, KR> onDuplicateKey();

    }


    interface _DynamicOnDuplicateKeyUpdateClause<C, F extends TableField, CC extends ColumnConsumer<F>> {

        Insert._InsertSpec onDuplicateKeyUpdate(Consumer<CC> consumer);

        Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

        Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<CC> consumer);

        Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

    }

    interface _StaticValueRowClause<C, F extends TableField, RR> {

        _StaticValueRowCommaSpec<C, F, RR> row(F field, @Nullable Object value);

        _StaticValueRowCommaSpec<C, F, RR> rowLiteral(F field, @Nullable Object value);

        _StaticValueRowCommaSpec<C, F, RR> rowExp(F field, Supplier<? extends Expression> supplier);

        _StaticValueRowCommaSpec<C, F, RR> rowExp(F field, Function<C, ? extends Expression> supplier);

    }

    interface _StaticValueRowCommaSpec<C, F extends TableField, CR>
            extends Insert._CommaFieldValuePairClause<C, F, _StaticValueRowCommaSpec<C, F, CR>>
            , Statement._RightParenClause<CR> {

    }


    interface _StaticAssignmentCommaFieldSpec<C, F extends TableField>
            extends Insert._CommaFieldValuePairClause<C, F, _StaticAssignmentCommaFieldSpec<C, F>>
            , Insert._InsertSpec {

    }

    interface _OnDuplicateKeyUpdateFieldSpec<C, F extends TableField>
            extends _StaticOnDuplicateKeyFieldClause<C, F, _StaticAssignmentCommaFieldSpec<C, F>>
            , _DynamicOnDuplicateKeyUpdateClause<C, F, ColumnConsumer<F>>, Insert._InsertSpec {

    }


    interface _StaticCommaAliasValuePairSpec<C, F extends TableField>
            extends Insert._CommaFieldValuePairClause<C, F, _StaticCommaAliasValuePairSpec<C, F>>
            , _CommaAliasValuePairClause<C, _StaticCommaAliasValuePairSpec<C, F>>, Insert._InsertSpec {

    }


    interface _OnDuplicateKeyUpdateAliasSpec<C, F extends TableField>
            extends _StaticOnDuplicateKeyAliasClause<C, F, _StaticCommaAliasValuePairSpec<C, F>>
            , _DynamicOnDuplicateKeyUpdateClause<C, F, AliasColumnConsumer<F>>, Insert._InsertSpec {

    }


    interface _OnDuplicateKeyRowAliasClause<C, F extends TableField>
            extends _RowColumnAliasListClause<C, F, _OnDuplicateKeyUpdateAliasSpec<C, F>> {

    }


    interface _AsRowAliasSpec<C, F extends TableField>
            extends Statement._AsClause<_OnDuplicateKeyRowAliasClause<C, F>>, _OnDuplicateKeyUpdateFieldSpec<C, F> {

    }



    /*-------------------below domain insert syntax interfaces  -------------------*/


    interface _DomainInsertValueClause<C, T extends IDomain, F extends TableField>
            extends Insert._DomainValueClause<C, T, _AsRowAliasSpec<C, F>> {

    }

    interface _DomainCommonExpSpec<C, T extends IDomain, F extends TableField>
            extends Insert._CommonExpClause<C, F, _DomainCommonExpSpec<C, T, F>>, _DomainInsertValueClause<C, T, F> {

    }

    interface _DomainColumnListSpec<C, T extends IDomain, F extends TableField>
            extends Insert._ColumnListClause<C, F, _DomainCommonExpSpec<C, T, F>>, _DomainCommonExpSpec<C, T, F> {

    }

    interface _DomainPartitionSpec<C, T extends IDomain, F extends TableField>
            extends MySQLQuery._PartitionClause<C, _DomainColumnListSpec<C, T, F>>
            , _DomainColumnListSpec<C, T, F> {

    }


    interface _DomainChildPartitionSpec<C, T extends IDomain, F extends TableField>
            extends _ChildPartitionClause<C, _DomainColumnListSpec<C, T, F>>
            , _DomainColumnListSpec<C, T, F> {

    }

    interface _DomainParentPartitionSpec<C, T extends IDomain, F extends TableField>
            extends _ParentPartitionClause<C, _DomainChildPartitionSpec<C, T, F>>, _DomainChildPartitionSpec<C, T, F> {

    }


    interface _DomainIntoClause<C> {

        <T extends IDomain> _DomainPartitionSpec<C, T, FieldMeta<T>> into(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T, FieldMeta<? super T>> into(ChildTableMeta<T> table);

    }


    interface _DomainInsertIntoSpec<C> extends _InsertClause<C, _DomainIntoClause<C>> {

        <T extends IDomain> _DomainPartitionSpec<C, T, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table);

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

    interface _ValueStaticValuesLeftParenClause<C, F extends TableField>
            extends Insert._StaticValueLeftParenClause<C, F, _ValueStaticValuesLeftParenSpec<C, F>> {

    }

    interface _ValueStaticValuesLeftParenSpec<C, F extends TableField> extends _ValueStaticValuesLeftParenClause<C, F>
            , _AsRowAliasSpec<C, F> {

    }


    interface _ValueValueSpec<C, F extends TableField>
            extends Insert._StaticValueClause<Insert._StaticValueLeftParenClause<C, F, _AsRowAliasSpec<C, F>>>
            , Insert._DynamicValueClause<C, F, _AsRowAliasSpec<C, F>>
            , Insert._StaticValuesClause<_ValueStaticValuesLeftParenClause<C, F>>
            , Insert._DynamicValuesClause<C, F, _AsRowAliasSpec<C, F>> {

    }

    interface _ValueCommonExpSpec<C, F extends TableField>
            extends Insert._CommonExpClause<C, F, _ValueCommonExpSpec<C, F>>, _ValueValueSpec<C, F> {

    }

    interface _ValueColumnListSpec<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, _ValueCommonExpSpec<C, F>>
            , _ValueCommonExpSpec<C, F> {

    }


    interface _ValuePartitionSpec<C, F extends TableField>
            extends MySQLQuery._PartitionClause<C, _ValueColumnListSpec<C, F>>, _ValueColumnListSpec<C, F> {

    }

    interface _ValueChildPartitionSpec<C, F extends TableField>
            extends _ChildPartitionClause<C, _ValueColumnListSpec<C, F>>, _ValueColumnListSpec<C, F> {

    }

    interface _ValueParentPartitionSpec<C, F extends TableField>
            extends _ParentPartitionClause<C, _ValueChildPartitionSpec<C, F>>, _ValueChildPartitionSpec<C, F> {

    }


    interface _ValueIntoClause<C> {

        <T extends IDomain> _ValuePartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table);

        <T extends IDomain> _ValueParentPartitionSpec<C, FieldMeta<? super T>> into(ChildTableMeta<T> table);
    }

    interface _ValueInsertIntoSpec<C> extends _InsertClause<C, _ValueIntoClause<C>> {

        <T extends IDomain> _ValuePartitionSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        <T extends IDomain> _ValueParentPartitionSpec<C, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table);

    }


    interface _ValueNullOptionSpec<C> extends Insert._NullOptionClause<_ValueInsertIntoSpec<C>>
            , _ValueInsertIntoSpec<C> {

    }

    interface _ValueOptionSpec<C> extends Insert._MigrationOptionClause<_ValueNullOptionSpec<C>>
            , _ValueNullOptionSpec<C> {

    }


    /*-------------------below assignment insert syntax interfaces-------------------*/


    interface _MySQLAssignmentSetClause<C, F extends TableField>
            extends Insert._AssignmentSetClause<C, F, _MySQLAssignmentSetSpec<C, F>> {
    }

    interface _MySQLAssignmentSetSpec<C, F extends TableField> extends _MySQLAssignmentSetClause<C, F>
            , _AsRowAliasSpec<C, F> {

    }

    interface _AssignmentPartitionSpec<C, F extends TableField>
            extends MySQLQuery._PartitionClause<C, _MySQLAssignmentSetClause<C, F>>, _MySQLAssignmentSetClause<C, F> {

    }

    interface _AssignmentChildPartitionSpec<C, F extends TableField>
            extends _ChildPartitionClause<C, _MySQLAssignmentSetClause<C, F>>, _MySQLAssignmentSetClause<C, F> {

    }

    interface _AssignmentParentPartitionSpec<C, F extends TableField>
            extends _ParentPartitionClause<C, _AssignmentChildPartitionSpec<C, F>>, _AssignmentChildPartitionSpec<C, F> {

    }

    interface _AssignmentIntoClause<C> {

        <T extends IDomain> _AssignmentPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table);

        <T extends IDomain> _AssignmentParentPartitionSpec<C, FieldMeta<? super T>> into(ChildTableMeta<T> table);

    }

    interface _AssignmentInsertIntoSpec<C> extends _InsertClause<C, _AssignmentIntoClause<C>> {

        <T extends IDomain> _AssignmentPartitionSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        <T extends IDomain> _AssignmentParentPartitionSpec<C, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table);

    }

    interface _AssignmentNullOptionSpec<C> extends Insert._NullOptionClause<_AssignmentInsertIntoSpec<C>>
            , _AssignmentInsertIntoSpec<C> {

    }

    interface _AssignmentOptionSpec<C> extends Insert._MigrationOptionClause<_AssignmentNullOptionSpec<C>>
            , _AssignmentNullOptionSpec<C> {

    }

    /*-------------------below row set insert syntax interfaces-------------------*/


    interface _StaticRowSetMultiRowClause<C, F extends TableField>
            extends _StaticValueRowClause<C, F, _StaticRowSetMultiRowSpec<C, F>> {

    }

    interface _StaticRowSetMultiRowSpec<C, F extends TableField> extends _StaticRowSetMultiRowClause<C, F>
            , _OnDuplicateKeyUpdateFieldSpec<C, F> {

    }

    interface _RowSetValuesSpec<C, F extends TableField>
            extends Insert._StaticValuesClause<_StaticRowSetMultiRowClause<C, F>>
            , Insert._DynamicValuesClause<C, F, _OnDuplicateKeyUpdateFieldSpec<C, F>> {

    }

    interface _RowSetSpaceSubQueryClause<C, F extends TableField>
            extends Insert._SpaceSubQueryClause<C, _OnDuplicateKeyUpdateFieldSpec<C, F>> {

    }

    interface _RowSetSpec<C, F extends TableField> extends _RowSetSpaceSubQueryClause<C, F>, _RowSetValuesSpec<C, F> {

    }


    interface _RowSetColumnListClause<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, _RowSetSpec<C, F>> {

    }

    interface _RowSetPartitionSpec<C, F extends TableField>
            extends MySQLQuery._PartitionClause<C, _RowSetColumnListClause<C, F>>, _RowSetColumnListClause<C, F> {

    }


    interface _RowSetChildSubQueryColumnListClause<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, _RowSetSpaceSubQueryClause<C, F>> {

    }

    interface _RowSetChildSubQueryPartitionSpec<C, F extends TableField>
            extends _ChildPartitionClause<C, _RowSetChildSubQueryColumnListClause<C, F>>
            , _RowSetChildSubQueryColumnListClause<C, F> {

    }


    interface _RowSetChildValuesRowColumnsClause<C, F extends TableField>
            extends Insert._ColumnListClause<C, F, _RowSetValuesSpec<C, F>> {

    }

    interface _RowSetChildValuesPartitionSpec<C, F extends TableField>
            extends _ChildPartitionClause<C, _RowSetChildValuesRowColumnsClause<C, F>>
            , _RowSetChildValuesRowColumnsClause<C, F> {

    }

    interface _RowSetStaticParentMultiRowClause<C, PF extends TableField, TF extends TableField>
            extends _StaticValueRowClause<C, PF, _RowSetStaticParentMultiRowSpec<C, PF, TF>> {

    }

    interface _RowSetStaticParentMultiRowSpec<C, PF extends TableField, TF extends TableField>
            extends _RowSetStaticParentMultiRowClause<C, PF, TF>, _RowSetChildValuesPartitionSpec<C, TF> {

    }

    interface _RowSetParentRowSetSpec<C, PF extends TableField, TF extends TableField>
            extends Insert._SpaceSubQueryClause<C, _RowSetChildSubQueryPartitionSpec<C, TF>>
            , Insert._StaticValuesClause<_RowSetStaticParentMultiRowClause<C, PF, TF>>
            , Insert._DynamicValuesClause<C, PF, _RowSetChildValuesPartitionSpec<C, TF>> {

    }

    interface _RowSetParentColumnListClause<C, PF extends TableField, TF extends TableField>
            extends Insert._ColumnListClause<C, PF, _RowSetParentRowSetSpec<C, PF, TF>> {

    }

    interface _RowSetParentPartitionSpec<C, P extends IDomain, T extends IDomain>
            extends _ParentPartitionClause<C, _RowSetParentColumnListClause<C, FieldMeta<P>, FieldMeta<T>>>
            , _RowSetParentColumnListClause<C, FieldMeta<P>, FieldMeta<T>> {

    }


    interface _RowSetIntoClause<C> {

        <T extends IDomain> _RowSetPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _RowSetParentPartitionSpec<C, P, T> into(ComplexTableMeta<P, T> table);

    }

    interface _RowSetInsertIntoSpec<C> extends _InsertClause<C, _RowSetIntoClause<C>> {

        <T extends IDomain> _RowSetPartitionSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _RowSetParentPartitionSpec<C, P, T> insertInto(ComplexTableMeta<P, T> table);

    }


}
