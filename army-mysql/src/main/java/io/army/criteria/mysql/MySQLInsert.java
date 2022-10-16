package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
 */
public interface MySQLInsert extends DialectStatement {


    interface _InsertClause<IR> {

        IR insert(Supplier<List<Hint>> supplier, List<MySQLModifier> modifiers);

    }


    interface _StaticOnDuplicateKeySetClause<I extends Item, F extends TableField>
            extends Update._StaticSetClause<F, _StaticOnDuplicateKeySetSpec<I, F>> {

    }

    interface _StaticOnDuplicateKeySetSpec<I extends Item, F extends TableField>
            extends _StaticOnDuplicateKeySetClause<I, F>
            , _DmlInsertSpec<I> {

    }


    interface _OnDuplicateKeyUpdateSpec<I extends Item, F extends TableField> extends _DmlInsertSpec<I> {

        _StaticOnDuplicateKeySetClause<I, F> onDuplicateKey();

        _DmlInsertSpec<I> onDuplicateKey(Consumer<ItemPairs<F>> consumer);

        _DmlInsertSpec<I> ifOnDuplicateKey(Consumer<ItemPairs<F>> consumer);
    }


    interface _OnAsRowAliasSpec<I extends Item, T> extends _OnDuplicateKeyUpdateSpec<I, FieldMeta<T>> {

        _OnDuplicateKeyUpdateSpec<I, TypeTableField<T>> as(String rowAlias);

    }



    /*-------------------below domain insert syntax interfaces  -------------------*/

    interface _MySQLStaticValuesLeftParenClause<I extends Item, T>
            extends Insert._StaticValueLeftParenClause<T, _StaticValuesLeftParenSpec<I, T>> {

    }

    interface _StaticValuesLeftParenSpec<I extends Item, T> extends _MySQLStaticValuesLeftParenClause<I, T>
            , _OnAsRowAliasSpec<I, T> {

    }

    interface _ValuesColumnDefaultSpec<I extends Item, T>
            extends Insert._ColumnDefaultClause<T, _ValuesColumnDefaultSpec<I, T>>
            , Insert._DomainValueClause<T, _OnAsRowAliasSpec<I, T>>
            , Insert._StaticValuesClause<_MySQLStaticValuesLeftParenClause<I, T>>
            , Insert._DynamicValuesClause<T, _OnAsRowAliasSpec<I, T>> {

    }

    interface _ComplexColumnDefaultSpec<I extends Item, T> extends _ValuesColumnDefaultSpec<I, T> {


    }

    interface _MySQLStaticAssignmentClause<I extends Item, T>
            extends Insert._StaticAssignmentSetClause<T, _StaticAssignmentSpec<I, T>> {

    }

    interface _StaticAssignmentSpec<I extends Item, T> extends _MySQLStaticAssignmentClause<I, T>
            , _OnAsRowAliasSpec<I, T> {

    }

    interface _ColumnListSpec<I extends Item, T> extends Insert._ColumnListClause<T, _ComplexColumnDefaultSpec<I, T>>
            , _ValuesColumnDefaultSpec<I, T>
            , _StaticAssignmentSpec<I, T>
            , Insert._DynamicAssignmentSetClause<T, _OnAsRowAliasSpec<I, T>> {

    }

    interface _PartitionSpec<I extends Item, T> extends MySQLQuery._PartitionClause<_ColumnListSpec<I, T>>
            , _ColumnListSpec<I, T> {

    }


    interface _StaticCteAsClause<I extends Item> extends Statement._AsClause<MySQL80Query._Select80Clause<I>> {

    }

    interface _StaticCteLeftParenSpec<I extends Item>
            extends Statement._LeftParenStringQuadraOptionalSpec<_StaticCteAsClause<I>>
            , _StaticCteAsClause<I> {

    }

    interface _ChildIntoClause<P> {

        <T> _PartitionSpec<Insert, T> into(ComplexTableMeta<P, T> table);

    }

    interface _ChildInsertIntoSpec<P> extends _InsertClause<_ChildIntoClause<P>> {

        <T> _PartitionSpec<Insert, T> insertInto(ComplexTableMeta<P, T> table);
    }

    interface _ChildCteSpec<P> extends _CteSpec<_ChildCteComma<P>> {

    }


    interface _ChildCteComma<P>
            extends Query._StaticWithCommaClause<_StaticCteLeftParenSpec<_ChildCteSpec<P>>>
            , _ChildInsertIntoSpec<P> {

    }

    interface _ChildWithCteSpec<P> extends Query._DynamicWithCteClause<MySQLCteBuilder, _ChildInsertIntoSpec<P>>
            , Query._StaticWithCteClause<_StaticCteLeftParenSpec<_ChildCteComma<P>>>
            , _ChildInsertIntoSpec<P> {

    }


    interface _PrimaryIntoClause {

        <T> _PartitionSpec<Insert, T> into(SimpleTableMeta<T> table);


        <P> _PartitionSpec<Insert._ParentInsert<_ChildWithCteSpec<P>>, P> into(ParentTableMeta<P> table);

    }


    interface _PrimaryInsertIntoSpec extends _InsertClause<_PrimaryIntoClause>, Item {

        <T> _PartitionSpec<Insert, T> insertInto(SimpleTableMeta<T> table);


        <P> _PartitionSpec<Insert._ParentInsert<_ChildWithCteSpec<P>>, P> insertInto(ParentTableMeta<P> table);

    }


    interface _PrimaryCteSpec extends _CteSpec<_PrimaryCteComma> {

    }


    interface _PrimaryCteComma extends Query._StaticWithCommaClause<_StaticCteLeftParenSpec<_PrimaryCteSpec>>
            , _PrimaryInsertIntoSpec {

    }


    interface _PrimaryWithCteSpec extends Query._DynamicWithCteClause<MySQLCteBuilder, _PrimaryInsertIntoSpec>
            , Query._StaticWithCteClause<_StaticCteLeftParenSpec<_PrimaryCteSpec>>
            , _PrimaryInsertIntoSpec {

    }


    interface _PrimaryPreferLiteralSpec extends Insert._PreferLiteralClause<_PrimaryWithCteSpec>, _PrimaryWithCteSpec {

    }

    interface _PrimaryNullOptionSpec extends Insert._NullOptionClause<_PrimaryPreferLiteralSpec>
            , _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec extends Insert._MigrationOptionClause<_PrimaryNullOptionSpec>
            , _PrimaryNullOptionSpec {

    }


}
