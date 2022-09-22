package io.army.criteria.mysql;

import io.army.criteria.*;
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

        IR insert(Supplier<List<Hint>> supplier, List<MySQLModifier> modifiers);

        IR insert(Function<C, List<Hint>> function, List<MySQLModifier> modifiers);

    }



    interface _StaticOnDuplicateKeyFieldUpdateClause<C, T, UR> {

        UR update(FieldMeta<T> field, @Nullable Object value);

        UR updateLiteral(FieldMeta<T> field, @Nullable Object value);

        UR updateExp(FieldMeta<T> field, Supplier<? extends Expression> supplier);

        UR updateExp(FieldMeta<T> field, Function<C, ? extends Expression> function);

    }

    interface _StaticOnDuplicateKeyAliasUpdateClause<C, T, UR>
            extends _StaticOnDuplicateKeyFieldUpdateClause<C, T, UR> {
        UR update(String columnAlias, @Nullable Object value);

        UR updateLiteral(String columnAlias, @Nullable Object value);

        UR updateExp(String columnAlias, Supplier<? extends Expression> supplier);

        UR updateExp(String columnAlias, Function<C, ? extends Expression> function);

    }


    interface _StaticOnDuplicateKeyFieldClause<C, T, KR> {

        _StaticOnDuplicateKeyFieldUpdateClause<C, T, KR> onDuplicateKey();

    }



    interface _DynamicOnDuplicateKeyUpdateClause<C, CC extends PairConsumer<?>, UR> {

        UR onDuplicateKeyUpdate(Consumer<CC> consumer);

        UR onDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

        UR ifOnDuplicateKeyUpdate(Consumer<CC> consumer);

        UR ifOnDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

    }


    interface _StaticAssignmentCommaFieldSpec<C, T>
            extends Insert._CommaFieldValuePairClause<C, T, _StaticAssignmentCommaFieldSpec<C, T>>
            , Insert._InsertSpec {

    }

    interface _OnDuplicateKeyUpdateFieldSpec<C, T>
            extends _StaticOnDuplicateKeyFieldClause<C, T, _StaticAssignmentCommaFieldSpec<C, T>>
            , _DynamicOnDuplicateKeyUpdateClause<C, PairConsumer<T>, Insert._InsertSpec>
            , Insert._InsertSpec {

    }


    interface _MySQLChildSpec<CT> extends Insert._ChildPartClause<CT>, Insert._InsertSpec {

    }

    interface _ParentStaticAssignmentCommaFieldSpec<C, T, CT>
            extends Insert._CommaFieldValuePairClause<C, T, _ParentStaticAssignmentCommaFieldSpec<C, T, CT>>
            , _MySQLChildSpec<CT> {

    }

    interface _ParentOnDuplicateKeyUpdateFieldSpec<C, T, CT>
            extends _StaticOnDuplicateKeyFieldClause<C, T, _ParentStaticAssignmentCommaFieldSpec<C, T, CT>>
            , _DynamicOnDuplicateKeyUpdateClause<C, PairConsumer<T>, _MySQLChildSpec<CT>>
            , _MySQLChildSpec<CT> {

    }





    /*-------------------below domain insert syntax interfaces  -------------------*/

    interface _DomainValuesSpec<C, T>
            extends _DomainValueClause<C, T, _OnDuplicateKeyUpdateFieldSpec<C, T>> {

    }

    interface _DomainDefaultSpec<C, T>
            extends _ColumnDefaultClause<C, T, _DomainDefaultSpec<C, T>>, _DomainValuesSpec<C, T> {

    }

    interface _DomainColumnListSpec<C, T>
            extends Insert._ColumnListClause<C, T, _DomainDefaultSpec<C, T>>, _DomainDefaultSpec<C, T> {

    }

    interface _DomainPartitionSpec<C, T>
            extends MySQLQuery._PartitionClause<C, _DomainColumnListSpec<C, T>>
            , _DomainColumnListSpec<C, T> {

    }

    interface _DomainChildIntoClause<C, P> {

        <T> _DomainPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }

    interface _DomainChildInsertIntoSpec<C, P> extends _InsertClause<C, _DomainChildIntoClause<C, P>> {

        <T> _DomainPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _DomainParentColumnDefaultSpec<C, P>
            extends Insert._ColumnDefaultClause<C, P, _DomainParentColumnDefaultSpec<C, P>>
            , _DomainValueClause<C, P, _ParentOnDuplicateKeyUpdateFieldSpec<C, P, _DomainChildInsertIntoSpec<C, P>>> {

    }


    interface _DomainParentColumnsSpec<C, P>
            extends Insert._ColumnListClause<C, P, _DomainParentColumnDefaultSpec<C, P>>
            , _DomainParentColumnDefaultSpec<C, P> {

    }

    interface _DomainParentPartitionSpec<C, P>
            extends MySQLQuery._PartitionClause<C, _DomainParentColumnsSpec<C, P>>, _DomainParentColumnsSpec<C, P> {

    }


    interface _DomainIntoClause<C> {

        <T> _DomainPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T> _DomainParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }


    interface _DomainInsertIntoSpec<C> extends _InsertClause<C, _DomainIntoClause<C>> {

        <T> _DomainPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T> _DomainParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table);

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

    interface _ValueStaticValuesLeftParenClause<C, T>
            extends Insert._StaticValueLeftParenClause<C, T, _ValueStaticValuesLeftParenSpec<C, T>> {

    }

    interface _ValueStaticValuesLeftParenSpec<C, T> extends _ValueStaticValuesLeftParenClause<C, T>
            , _OnDuplicateKeyUpdateFieldSpec<C, T> {

    }

    interface _ValueDefaultSpec<C, T>
            extends Insert._ColumnDefaultClause<C, T, _ValueDefaultSpec<C, T>>
            , Insert._StaticValuesClause<_ValueStaticValuesLeftParenClause<C, T>>
            , Insert._DynamicValuesClause<C, T, _OnDuplicateKeyUpdateFieldSpec<C, T>> {

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

    interface _ValueChildInsertIntoSpec<C, P> extends _InsertClause<C, _ValueChildIntoClause<C, P>> {

        <T> _ValuePartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _ValueParentStaticValueLeftParenClause<C, P>
            extends Insert._StaticValueLeftParenClause<C, P, _ValueParentStaticValueLeftParenSpec<C, P>> {

    }

    interface _ValueParentStaticValueLeftParenSpec<C, P>
            extends _ValueParentStaticValueLeftParenClause<C, P>
            , _ParentOnDuplicateKeyUpdateFieldSpec<C, P, _ValueChildInsertIntoSpec<C, P>> {

    }


    interface _ValueParentDefaultSpec<C, P>
            extends _ColumnDefaultClause<C, P, _ValueParentDefaultSpec<C, P>>
            , Insert._StaticValuesClause<_ValueParentStaticValueLeftParenClause<C, P>>
            , Insert._DynamicValuesClause<C, P, _ParentOnDuplicateKeyUpdateFieldSpec<C, P, _ValueChildInsertIntoSpec<C, P>>> {

    }

    interface _ValueParentColumnsSpec<C, P>
            extends Insert._ColumnListClause<C, P, _ValueParentDefaultSpec<C, P>>
            , _ValueParentDefaultSpec<C, P> {

    }

    interface _ValueParentPartitionSpec<C, P>
            extends MySQLQuery._PartitionClause<C, _ValueParentColumnsSpec<C, P>>, _ValueParentColumnsSpec<C, P> {

    }


    interface _ValueIntoClause<C> {

        <T> _ValuePartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T> _ValueParentPartitionSpec<C, T> into(ParentTableMeta<T> table);
    }

    interface _ValueInsertIntoSpec<C> extends _InsertClause<C, _ValueIntoClause<C>> {

        <T> _ValuePartitionSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T> _ValueParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table);

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


    interface _MySQLAssignmentSetClause<C, T>
            extends Insert._AssignmentSetClause<C, T, _MySQLAssignmentSetSpec<C, T>> {
    }

    interface _MySQLAssignmentSetSpec<C, T> extends _MySQLAssignmentSetClause<C, T>
            , _OnDuplicateKeyUpdateFieldSpec<C, T> {

    }

    interface _AssignmentPartitionSpec<C, T>
            extends MySQLQuery._PartitionClause<C, _MySQLAssignmentSetClause<C, T>>, _MySQLAssignmentSetClause<C, T> {

    }

    interface _AssignmentChildIntoClause<C, P> {

        <T> _AssignmentPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);

    }

    interface _AssignmentChildInsertIntoSpec<C, P>
            extends _InsertClause<C, _AssignmentChildIntoClause<C, P>> {

        <T> _AssignmentPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _AssignmentParentSetClause<C, P>
            extends Insert._AssignmentSetClause<C, P, _AssignmentParentSetSpec<C, P>> {

    }

    interface _AssignmentParentSetSpec<C, P> extends _AssignmentParentSetClause<C, P>
            , _ParentOnDuplicateKeyUpdateFieldSpec<C, P, _AssignmentChildInsertIntoSpec<C, P>> {

    }


    interface _AssignmentParentPartitionSpec<C, P>
            extends MySQLQuery._PartitionClause<C, _AssignmentParentSetClause<C, P>>, _AssignmentParentSetClause<C, P> {

    }

    interface _AssignmentIntoClause<C> {

        <T> _AssignmentPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T> _AssignmentParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }

    interface _AssignmentInsertIntoSpec<C> extends _InsertClause<C, _AssignmentIntoClause<C>> {

        <T> _AssignmentPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T> _AssignmentParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table);

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


    interface _QuerySpaceSubQueryClause<C, T>
            extends Insert._SpaceSubQueryClause<C, _OnDuplicateKeyUpdateFieldSpec<C, T>> {

    }


    interface _QueryColumnListClause<C, T>
            extends Insert._ColumnListClause<C, T, _QuerySpaceSubQueryClause<C, T>> {

    }

    interface _QueryPartitionSpec<C, T>
            extends MySQLQuery._PartitionClause<C, _QueryColumnListClause<C, T>>, _QueryColumnListClause<C, T> {

    }


    interface _QueryChildIntoClause<C, P> {

        <T> _QueryPartitionSpec<C, T> into(ComplexTableMeta<P, T> table);
    }

    interface _QueryChildInsertIntoSpec<C, P> extends _InsertClause<C, _QueryChildIntoClause<C, P>> {

        <T> _QueryPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table);
    }


    interface _QueryParentQueryClause<C, P>
            extends Insert._SpaceSubQueryClause<C, _ParentOnDuplicateKeyUpdateFieldSpec<C, P, _QueryChildInsertIntoSpec<C, P>>> {

    }

    interface _QueryParentColumnListClause<C, P>
            extends Insert._ColumnListClause<C, P, _QueryParentQueryClause<C, P>> {

    }

    interface _QueryParentPartitionSpec<C, P>
            extends MySQLQuery._PartitionClause<C, _QueryParentColumnListClause<C, P>>
            , _QueryParentColumnListClause<C, P> {

    }


    interface _QueryIntoClause<C> {

        <T> _QueryPartitionSpec<C, T> into(SimpleTableMeta<T> table);

        <T> _QueryParentPartitionSpec<C, T> into(ParentTableMeta<T> table);

    }

    interface _QueryInsertIntoSpec<C> extends _InsertClause<C, _QueryIntoClause<C>> {

        <T> _QueryPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table);

        <T> _QueryParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table);

    }


}
