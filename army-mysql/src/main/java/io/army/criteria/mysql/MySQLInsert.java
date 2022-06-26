package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

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

        IR insert(Function<C, List<Hint>> hints, List<MySQLWords> modifiers);

        IR insert(Supplier<List<Hint>> hints, List<MySQLWords> modifiers);
    }


    interface _ColumnAliasListClause<C, CR> {

        _RightParenClause<CR> leftParen(Consumer<Consumer<String>> consumer);

        _RightParenClause<CR> leftParen(BiConsumer<C, Consumer<String>> consumer);

        _ColumnAliasClause<CR> leftParen(String columnAlias);

    }


    interface _ColumnAliasClause<CR> extends _RightParenClause<CR> {

        CR comma(String columnAlias);

    }

    interface _RowConstructorClause<C, RR> {
        RR row(Expression value);

        RR row(Supplier<? extends Expression> supplier);

        RR row(Function<C, ? extends Expression> function);
    }

    interface _RowValueClause<C, VR> {

        VR comma(Expression value);

        VR comma(Supplier<? extends Expression> supplier);

        VR comma(Function<C, ? extends Expression> function);

    }


    interface _OnDuplicateKeyFieldSetSpec<C, T extends IDomain>
            extends _OnDuplicateKeySetClause<C, FieldMeta<? super T>, _OnDuplicateKeyFieldSetSpec<C, T>>
            , Insert._InsertSpec {

    }

    interface _OnDuplicateKeySetSpec<C>
            extends _OnDuplicateKeySetClause<C, TableField, _OnDuplicateKeySetSpec<C>>
            , _OnDuplicateKeyAliasSetClause<C, _OnDuplicateKeySetSpec<C>>, Insert._InsertSpec {

    }

    interface _OnDuplicateKeyFieldUpdateSpec<C, T extends IDomain>
            extends Insert._OnDuplicateKeyUpdateClause<_OnDuplicateKeyFieldSetSpec<C, T>>, Insert._InsertSpec {

    }


    interface _OnDuplicateKeyUpdateSpec<C>
            extends Insert._OnDuplicateKeyUpdateClause<_OnDuplicateKeySetSpec<C>>, Insert._InsertSpec {

    }

    interface _ColumnAliasAsSpec<C, T extends IDomain> extends Statement._AsClause<_ColumnAliasListClause<C, _OnDuplicateKeyUpdateSpec<C>>>
            , _OnDuplicateKeyFieldUpdateSpec<C, T> {

    }

    /*################################## blow value insert clause interface ##################################*/

    interface _LiteralOptionSpec<C> extends _PreferLiteralClause<_ValueInsertOptionSpec<C>>, _ValueInsertOptionSpec<C> {

    }

    interface _ValueInsertOptionSpec<C> extends Insert._OptionClause<_ValueInsertSpec<C>>, _ValueInsertSpec<C> {

    }

    interface _ValueInsertSpec<C> extends MySQLInsert._InsertClause<C, _ValueIntoClause<C>> {

        <T extends IDomain> _ValuePartitionSpec<C, T> insertInto(TableMeta<T> table);

    }


    interface _ValueIntoClause<C> {

        <T extends IDomain> _ValuePartitionSpec<C, T> into(TableMeta<T> table);

    }


    interface _ValuePartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _ComplexColumnListSpec<C, T>>
            , _ComplexColumnListSpec<C, T> {

    }

    interface _ComplexColumnListSpec<C, T extends IDomain> extends _ComplexColumnListClause<C, T, _ValuesSpec<C, T>>
            , _ValuesSpec<C, T> {

    }

    interface _ValuesSpec<C, T extends IDomain> extends Insert._ValueClause<C, T, _ColumnAliasAsSpec<C, T>> {

    }


    /*################################## blow assignment insert clause interface ##################################*/

    interface _AssignmentInsertOptionSpec<C> extends Insert._MigrationOptionClause<_AssignmentInsertSpec<C>>
            , _AssignmentInsertSpec<C> {

    }

    interface _AssignmentInsertSpec<C> extends MySQLInsert._InsertClause<C, _AssignmentIntoClause<C>> {

        <T extends IDomain> _AssignmentPartitionSpec<C, T> insertInto(TableMeta<T> table);

    }

    interface _AssignmentIntoClause<C> {

        <T extends IDomain> _AssignmentPartitionSpec<C, T> into(TableMeta<T> table);

    }

    interface _AssignmentPartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _AssignmentSetClause<C, T>>, _AssignmentSetClause<C, T> {

    }

    interface _AssignmentSetClause<C, T extends IDomain>
            extends Update._SimpleSetClause<C, FieldMeta<? super T>, _AssignmentSetSpec<C, T>> {

    }

    interface _AssignmentSetSpec<C, T extends IDomain> extends _AssignmentSetClause<C, T>, _ColumnAliasAsSpec<C, T> {

    }


    /*################################## blow SubQuery insert clause interface ##################################*/

    interface _SubQueryInsertSpec<C> extends MySQLInsert._InsertClause<C, _SubQueryIntoClause<C>> {
        <T extends IDomain> _RowSetPartitionSpec<C, T> insertInto(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _SubQueryParentPartitionSpec<C, P, T> insertInto(ComplexTableMeta<P, T> table);

    }

    interface _SubQueryIntoClause<C> {

        <T extends IDomain> _RowSetPartitionSpec<C, T> into(SingleTableMeta<T> table);

        <P extends IDomain, T extends IDomain> _SubQueryParentPartitionSpec<C, P, T> into(ComplexTableMeta<P, T> table);

    }

    interface _RowSetPartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _SubQuerySingleColumnListSpec<C, T>>
            , _SubQuerySingleColumnListSpec<C, T> {

    }

    interface _SubQuerySingleColumnListSpec<C, T extends IDomain>
            extends _SingleColumnListClause<C, T, _SingleRowSetClause<C, T>>
            , _SingleRowSetClause<C, T> {

    }

    interface _SingleRowSetClause<C, T extends IDomain>
            extends Insert._SubQueryClause<C, _OnDuplicateKeyFieldUpdateSpec<C, T>> {

        void values(Supplier<List<Expression>> supplier);

        void values(Function<C, List<Expression>> supplier);

        void values();
    }


    interface _SubQueryParentPartitionSpec<C, P extends IDomain, T extends IDomain> {

    }

    interface _ParentRowSetClause<C, T extends IDomain>
            extends Insert._SubQueryClause<C, _RowSetPartitionSpec<C, T>> {

    }

    interface _ParentOnDuplicateKeyUpdateSpec<C, P extends IDomain, T extends IDomain>
            extends Insert._OnDuplicateKeyUpdateClause<_ParentOnDuplicateKeySetClause<C, P, T>>
            , _RowSetPartitionSpec<C, T> {

    }

    interface _ParentOnDuplicateKeySetClause<C, P extends IDomain, T extends IDomain>
            extends Insert._OnDuplicateKeySetClause<C, FieldMeta<P>, _ParentOnDuplicateKeySetSpec<C, P, T>> {

    }

    interface _ParentOnDuplicateKeySetSpec<C, P extends IDomain, T extends IDomain>
            extends _ParentOnDuplicateKeySetClause<C, P, T>, _RowSetPartitionSpec<C, T> {

    }


}
