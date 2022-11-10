package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.meta.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
 */
public interface MySQLInsert extends MySQLStatement {


    interface _InsertClause<IR> extends Item {

        IR insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers);

    }


    interface _StaticOnDuplicateKeySetClause<I extends Item, F extends TableField>
            extends Update._StaticSetClause<F, _StaticOnDuplicateKeySetSpec<I, F>> {

    }

    interface _StaticOnDuplicateKeySetSpec<I extends Item, F extends TableField>
            extends _StaticOnDuplicateKeySetClause<I, F>
            , _DmlInsertClause<I> {

    }


    interface _OnDuplicateKeyUpdateSpec<I extends Item, F extends TableField> extends _DmlInsertClause<I> {

        _StaticOnDuplicateKeySetClause<I, F> onDuplicateKey();

        _DmlInsertClause<I> onDuplicateKey(Consumer<ItemPairs<F>> consumer);

        _DmlInsertClause<I> ifOnDuplicateKey(Consumer<ItemPairs<F>> consumer);
    }


    interface _OnAsRowAliasSpec<I extends Item, T> extends _OnDuplicateKeyUpdateSpec<I, FieldMeta<T>> {

        _OnDuplicateKeyUpdateSpec<I, TypeTableField<T>> as(String rowAlias);

    }



    /*-------------------below insert syntax interfaces  -------------------*/

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

    interface _ComplexColumnDefaultSpec<I extends Item, T> extends _ValuesColumnDefaultSpec<I, T>
            , _StaticSpaceClause<MySQLQuery._WithSpec<_OnAsRowAliasSpec<I, T>>> {

    }

    interface _MySQLStaticAssignmentClause<I extends Item, T>
            extends Insert._StaticAssignmentSetClause<T, _StaticAssignmentSpec<I, T>> {

    }

    interface _StaticAssignmentSpec<I extends Item, T> extends _MySQLStaticAssignmentClause<I, T>
            , _OnAsRowAliasSpec<I, T> {

    }

    interface _ColumnListSpec<I extends Item, T> extends Insert._ColumnListClause<T, _ComplexColumnDefaultSpec<I, T>>
            , _ValuesColumnDefaultSpec<I, T>
            , _MySQLStaticAssignmentClause<I, T>
            , Insert._DynamicAssignmentSetClause<T, _OnAsRowAliasSpec<I, T>> {

    }

    interface _PartitionSpec<I extends Item, T> extends MySQLQuery._PartitionClause<_ColumnListSpec<I, T>>
            , _ColumnListSpec<I, T> {

    }



    interface _ChildIntoClause<P> {

        <T> _PartitionSpec<Insert, T> into(ComplexTableMeta<P, T> table);

    }

    interface _ChildInsertIntoSpec<P> extends _InsertClause<_ChildIntoClause<P>> {

        <T> _PartitionSpec<Insert, T> insertInto(ComplexTableMeta<P, T> table);
    }



    interface _PrimaryIntoClause {

        <T> _PartitionSpec<Insert, T> into(SimpleTableMeta<T> table);


        <P> _PartitionSpec<Insert._ParentInsert<_ChildInsertIntoSpec<P>>, P> into(ParentTableMeta<P> table);

    }


    interface _PrimaryInsertIntoSpec extends _InsertClause<_PrimaryIntoClause>, Item {

        <T> _PartitionSpec<Insert, T> insertInto(SimpleTableMeta<T> table);


        <P> _PartitionSpec<Insert._ParentInsert<_ChildInsertIntoSpec<P>>, P> insertInto(ParentTableMeta<P> table);

    }


    interface _PrimaryPreferLiteralSpec extends Insert._PreferLiteralClause<_PrimaryInsertIntoSpec>
            , _PrimaryInsertIntoSpec {

    }

    interface _PrimaryNullOptionSpec extends Insert._NullOptionClause<_PrimaryPreferLiteralSpec>
            , _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec extends Insert._MigrationOptionClause<_PrimaryNullOptionSpec>
            , _PrimaryNullOptionSpec {

    }


    interface _PrimaryIntoSingleClause<I extends Item> {

        <T> _PartitionSpec<I, T> into(SingleTableMeta<T> table);

    }


    interface _PrimaryInsertIntoSingleSpec<I extends Item> extends _InsertClause<_PrimaryIntoSingleClause<I>>, Item {

        <T> _PartitionSpec<I, T> insertInto(SingleTableMeta<T> table);

    }


    interface _PrimaryPreferLiteralSingleSpec<I extends Item>
            extends Insert._PreferLiteralClause<_PrimaryInsertIntoSingleSpec<I>>
            , _PrimaryInsertIntoSingleSpec<I> {

    }

    interface _PrimaryNullOptionSingleSpec<I extends Item>
            extends Insert._NullOptionClause<_PrimaryPreferLiteralSingleSpec<I>>
            , _PrimaryPreferLiteralSingleSpec<I> {

    }

    interface _PrimaryOptionSingleSpec<I extends Item>
            extends Insert._MigrationOptionClause<_PrimaryNullOptionSingleSpec<I>>
            , _PrimaryNullOptionSingleSpec<I> {

    }


}
