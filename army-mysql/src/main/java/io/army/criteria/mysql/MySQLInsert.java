package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

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

        UR updateExp(String columnAlias, Function<C, ? extends Expression> function);

    }


    interface _StaticOnDuplicateKeyFieldClause<C, T extends IDomain, KR> {

        _StaticOnDuplicateKeyFieldUpdateClause<C, T, KR> onDuplicateKey();

    }

    interface _StaticOnDuplicateKeyAliasClause<C, T extends IDomain, KR> {

        _StaticOnDuplicateKeyAliasUpdateClause<C, T, KR> onDuplicateKey();

    }


    interface _DynamicOnDuplicateKeyUpdateClause<C, CC extends PairConsumer<?>, UR> {

        UR onDuplicateKeyUpdate(Consumer<CC> consumer);

        UR onDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

        UR ifOnDuplicateKeyUpdate(Consumer<CC> consumer);

        UR ifOnDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

    }


    interface _StaticAssignmentCommaFieldSpec<C, T extends IDomain>
            extends Insert._CommaFieldValuePairClause<C, T, _StaticAssignmentCommaFieldSpec<C, T>>
            , Insert._InsertSpec {

    }

    interface _OnDuplicateKeyUpdateFieldSpec<C, T extends IDomain>
            extends _StaticOnDuplicateKeyFieldClause<C, T, _StaticAssignmentCommaFieldSpec<C, T>>
            , _DynamicOnDuplicateKeyUpdateClause<C, PairConsumer<FieldMeta<T>>, Insert._InsertSpec>
            , Insert._InsertSpec {

    }


    interface _StaticCommaAliasValuePairSpec<C, T extends IDomain>
            extends Insert._CommaFieldValuePairClause<C, T, _StaticCommaAliasValuePairSpec<C, T>>
            , _CommaAliasValuePairClause<C, _StaticCommaAliasValuePairSpec<C, T>>, Insert._InsertSpec {

    }


    interface _OnDuplicateKeyUpdateAliasSpec<C, T extends IDomain>
            extends _StaticOnDuplicateKeyAliasClause<C, T, _StaticCommaAliasValuePairSpec<C, T>>
            , _DynamicOnDuplicateKeyUpdateClause<C, AliasColumnConsumer<FieldMeta<T>>, Insert._InsertSpec> {

    }


    interface _OnDuplicateKeyRowAliasClause<C, T extends IDomain>
            extends _RowColumnAliasListClause<C, T, _OnDuplicateKeyUpdateAliasSpec<C, T>> {

    }

    interface _AsRowAliasClause<C, T extends IDomain, RR> extends Statement._AsClause<_RowColumnAliasListClause<C, T, RR>> {

    }


    interface _AsRowAliasSpec<C, T extends IDomain>
            extends _AsRowAliasClause<C, T, _OnDuplicateKeyUpdateAliasSpec<C, T>>, _OnDuplicateKeyUpdateFieldSpec<C, T> {

    }


    interface _MySQLChildSpec<CT> extends Insert._ChildPartClause<CT>, Insert._InsertSpec {

    }

    interface _ParentStaticAssignmentCommaFieldSpec<C, T extends IDomain, CT>
            extends Insert._CommaFieldValuePairClause<C, T, _ParentStaticAssignmentCommaFieldSpec<C, T, CT>>
            , _MySQLChildSpec<CT> {

    }

    interface _ParentOnDuplicateKeyUpdateFieldSpec<C, T extends IDomain, CT>
            extends _StaticOnDuplicateKeyFieldClause<C, T, _ParentStaticAssignmentCommaFieldSpec<C, T, CT>>
            , _DynamicOnDuplicateKeyUpdateClause<C, PairConsumer<FieldMeta<T>>, _MySQLChildSpec<CT>>
            , _MySQLChildSpec<CT> {

    }


    interface _ParentStaticCommaAliasValuePairSpec<C, T extends IDomain, CT>
            extends Insert._CommaFieldValuePairClause<C, T, _ParentStaticCommaAliasValuePairSpec<C, T, CT>>
            , _CommaAliasValuePairClause<C, _ParentStaticCommaAliasValuePairSpec<C, T, CT>>, _MySQLChildSpec<CT> {

    }


    interface _ParentOnDuplicateKeyUpdateAliasSpec<C, T extends IDomain, CT>
            extends _StaticOnDuplicateKeyAliasClause<C, T, _ParentStaticCommaAliasValuePairSpec<C, T, CT>>
            , _DynamicOnDuplicateKeyUpdateClause<C, AliasColumnConsumer<FieldMeta<T>>, _MySQLChildSpec<CT>> {

    }



    interface _ParentAsRowAliasSpec<C, T extends IDomain, CT>
            extends _AsRowAliasClause<C, T, _ParentOnDuplicateKeyUpdateAliasSpec<C, T, CT>>
            , _ParentOnDuplicateKeyUpdateFieldSpec<C, T, CT> {

    }



    /*-------------------below domain insert syntax interfaces  -------------------*/

    interface _DomainValuesSpec<C, T extends IDomain> extends Insert._DomainValueClause<C, T, _AsRowAliasSpec<C, T>> {

    }


    interface _DomainDefaultSpec<C, T extends IDomain>
            extends _ColumnDefaultClause<C, T, _DomainDefaultSpec<C, T>>, _DomainValuesSpec<C, T> {

    }

    interface _DomainColumnListSpec<C, T extends IDomain>
            extends Insert._ColumnListClause<C, T, _DomainDefaultSpec<C, T>>, _DomainDefaultSpec<C, T> {

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


    interface _DomainChildClause<C, P extends IDomain>
            extends Insert._ChildPartClause<_DomainChildInsertIntoSpec<C, P>> {

    }


    interface _DomainParentStaticAssignmentCommaFieldSpec<C, P extends IDomain>
            extends Insert._CommaFieldValuePairClause<C, P, _DomainParentStaticAssignmentCommaFieldSpec<C, P>>
            , _DomainChildClause<C, P> {

    }

    interface _DomainParentOnDuplicateKeyUpdateFieldSpec<C, P extends IDomain>
            extends _StaticOnDuplicateKeyFieldClause<C, P, _DomainParentStaticAssignmentCommaFieldSpec<C, P>>
            , _DynamicOnDuplicateKeyUpdateClause<C, PairConsumer<FieldMeta<P>>, _DomainChildClause<C, P>>
            , _DomainChildClause<C, P> {

    }


    interface _DomainParentStaticCommaAliasValuePairSpec<C, P extends IDomain>
            extends Insert._CommaFieldValuePairClause<C, P, _DomainParentStaticCommaAliasValuePairSpec<C, P>>
            , _CommaAliasValuePairClause<C, _DomainParentStaticCommaAliasValuePairSpec<C, P>>
            , _DomainChildClause<C, P> {

    }


    interface _DomainParentOnDuplicateKeyUpdateAliasSpec<C, P extends IDomain>
            extends _StaticOnDuplicateKeyAliasClause<C, P, _DomainParentStaticCommaAliasValuePairSpec<C, P>>
            , _DynamicOnDuplicateKeyUpdateClause<C, AliasColumnConsumer<FieldMeta<P>>, _DomainChildClause<C, P>> {

    }


    interface _DomainParentAsRowAliasSpec<C, P extends IDomain>
            extends _AsRowAliasClause<C, P, _DomainParentOnDuplicateKeyUpdateAliasSpec<C, P>>
            , _DomainParentOnDuplicateKeyUpdateFieldSpec<C, P> {

    }


    interface _DomainParentDefaultSpec<C, P extends IDomain>
            extends Insert._ColumnDefaultClause<C, P, _DomainParentDefaultSpec<C, P>>, _DomainValuesSpec<C, P>
            , _DomainParentAsRowAliasSpec<C, P> {

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

        <T extends IDomain> _DomainPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table);

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

    interface _ValueDefaultSpec<C, T extends IDomain>
            extends _ColumnDefaultClause<C, T, _ValueDefaultSpec<C, T>>
            , Insert._StaticValuesClause<_ValueStaticValuesLeftParenClause<C, T>>
            , Insert._DynamicValuesClause<C, T, _AsRowAliasSpec<C, T>> {

    }

    interface _ValueColumnListSpec<C, T extends IDomain>
            extends Insert._ColumnListClause<C, T, _ValueDefaultSpec<C, T>>, _ValueDefaultSpec<C, T> {

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




    interface _ValueParentStaticValueLeftParenClause<C, P extends IDomain>
            extends Insert._StaticValueLeftParenClause<C, P, _ValueParentStaticValueLeftParenSpec<C, P>> {

    }

    interface _ValueParentStaticValueLeftParenSpec<C, P extends IDomain>
            extends _ValueParentStaticValueLeftParenClause<C, P>
            , _ParentAsRowAliasSpec<C, P, _ValueChildInsertIntoSpec<C, P>> {

    }


    interface _ValueParentDefaultSpec<C, P extends IDomain>
            extends _ColumnDefaultClause<C, P, _ValueParentDefaultSpec<C, P>>
            , Insert._StaticValuesClause<_ValueParentStaticValueLeftParenClause<C, P>>
            , Insert._DynamicValuesClause<C, P, _ParentAsRowAliasSpec<C, P, _ValueChildInsertIntoSpec<C, P>>> {

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
            , _ParentAsRowAliasSpec<C, P, _AssignmentChildInsertIntoSpec<C, P>> {

    }


    interface _AssignmentParentPartitionSpec<C, P extends IDomain>
            extends MySQLQuery._PartitionClause<C, _AssignmentParentSetClause<C, P>>, _AssignmentParentSetClause<C, P> {

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


    interface _QuerySpaceSubQueryClause<C, T extends IDomain>
            extends Insert._SpaceSubQueryClause<C, _OnDuplicateKeyUpdateFieldSpec<C, T>> {

    }


    interface _QueryColumnListClause<C, T extends IDomain>
            extends Insert._ColumnListClause<C, T, _QuerySpaceSubQueryClause<C, T>> {

    }

    interface _QueryPartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _QueryColumnListClause<C, T>>, _QueryColumnListClause<C, T> {

    }


    interface _QueryChildIntoClause<C, P extends IDomain> {

        <T extends IDomain> _QueryPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }

    interface _QueryChildInsertIntoSpec<C, P extends IDomain> extends _InsertClause<C, _QueryChildIntoClause<C, P>> {

        <T extends IDomain> _QueryPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table);
    }


    interface _QueryParentQueryClause<C, P extends IDomain>
            extends Insert._SpaceSubQueryClause<C, _ParentOnDuplicateKeyUpdateFieldSpec<C, P, _QueryChildInsertIntoSpec<C, P>>> {

    }

    interface _QueryParentColumnListClause<C, P extends IDomain>
            extends Insert._ColumnListClause<C, P, _QueryParentQueryClause<C, P>> {

    }

    interface _QueryParentPartitionSpec<C, P extends IDomain>
            extends MySQLQuery._PartitionClause<C, _QueryParentColumnListClause<C, P>>
            , _QueryParentColumnListClause<C, P> {

    }


    interface _QueryIntoClause<C> {

        <T extends IDomain> _QueryPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T extends IDomain> _QueryParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }

    interface _QueryInsertIntoSpec<C> extends _InsertClause<C, _QueryIntoClause<C>> {

        <T extends IDomain> _QueryPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T extends IDomain> _QueryParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table);

    }


}
