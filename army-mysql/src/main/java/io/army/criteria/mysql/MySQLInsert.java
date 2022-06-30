package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
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

        IR insert(Supplier<List<Hint>> hints, List<MySQLWords> modifiers);

        IR insert(Function<C, List<Hint>> hints, List<MySQLWords> modifiers);

    }


    interface _ParentPartitionClause<C, PR> {

        PR parentPartition(String partitionName);

        PR parentPartition(String partitionName1, String partitionNam2);

        PR parentPartition(String partitionName1, String partitionNam2, String partitionNam3);

        PR parentPartition(Supplier<List<String>> supplier);

        PR parentPartition(Function<C, List<String>> function);

        PR parentPartition(Consumer<List<String>> consumer);

        PR ifParentPartition(Supplier<List<String>> supplier);

        PR ifParentPartition(Function<C, List<String>> function);

    }


    interface _ChildPartitionClause<C, PR> {

        PR childPartition(String partitionName);

        PR childPartition(String partitionName1, String partitionNam2);

        PR childPartition(String partitionName1, String partitionNam2, String partitionNam3);

        PR childPartition(Supplier<List<String>> supplier);

        PR childPartition(Function<C, List<String>> function);

        PR childPartition(Consumer<List<String>> consumer);

        PR ifChildPartition(Supplier<List<String>> supplier);

        PR ifChildPartition(Function<C, List<String>> function);

    }


    interface _ColumnAliasClause<CR> extends _RightParenClause<CR> {

        _ColumnAliasClause<CR> comma(String columnAlias);

    }

    interface _ColumnAliasListClause<C, CR> {

        _RightParenClause<CR> leftParen(Consumer<Consumer<String>> consumer);

        _RightParenClause<CR> leftParen(BiConsumer<C, Consumer<String>> consumer);

        _ColumnAliasClause<CR> leftParen(String columnAlias);

    }


    interface _StaticOnDuplicateKeyUpdateClause<UR> {

        UR onDuplicateKeyUpdate();
    }


    interface _DynamicOnDuplicateKeyUpdateClause<C, F extends TableField, CC extends ColumnConsumer<F>> {

        Insert._InsertSpec onDuplicateKeyUpdate(Consumer<CC> consumer);

        Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, CC> consumer);

    }

    interface _AssignmentListSpaceClause<C, F extends TableField, SR> {

        SR space(F field, @Nullable Object value);

        SR spaceLiteral(F field, @Nullable Object value);

        SR spaceExp(F field, Supplier<? extends Expression> supplier);

        SR spaceExp(F field, Function<C, ? extends Expression> supplier);

    }

    interface _AssignmentCommaFieldClause<C, F extends TableField, SR> {

        SR comma(F field, @Nullable Object value);

        SR commaLiteral(F field, @Nullable Object value);

        SR commaExp(F field, Supplier<? extends Expression> supplier);

        SR commaExp(F field, Function<C, ? extends Expression> supplier);

    }

    interface _AssignmentListSpaceAliasClause<C, SR> {

        SR space(String columnAlias, @Nullable Object value);

        SR spaceLiteral(String columnAlias, @Nullable Object value);

        SR spaceExp(String columnAlias, Supplier<? extends Expression> supplier);

        SR spaceExp(String columnAlias, Function<C, ? extends Expression> supplier);

    }

    interface _AssignmentCommaAliasClause<C, SR> {

        SR comma(String columnAlias, @Nullable Object value);

        SR commaLiteral(String columnAlias, @Nullable Object value);

        SR commaExp(String columnAlias, Supplier<? extends Expression> supplier);

        SR commaExp(String columnAlias, Function<C, ? extends Expression> supplier);

    }


    interface _AssignmentCommaFieldSpec<C, F extends TableField>
            extends _AssignmentCommaFieldClause<C, F, _AssignmentCommaFieldSpec<C, F>>, Insert._InsertSpec {

    }

    interface _AssignmentCommaAliasSpec<C, F extends TableField>
            extends _AssignmentCommaFieldClause<C, F, _AssignmentCommaAliasSpec<C, F>>
            , _AssignmentCommaAliasClause<C, _AssignmentCommaAliasSpec<C, F>>, Insert._InsertSpec {

    }


    interface _OnDuplicateKeyUpdateFieldSpec<C, F extends TableField>
            extends _StaticOnDuplicateKeyUpdateClause<_AssignmentCommaFieldSpec<C, F>>
            , _DynamicOnDuplicateKeyUpdateClause<C, F, ColumnConsumer<F>>, Insert._InsertSpec {

    }


    interface _OnDuplicateKeyUpdateAliasSpec<C, F extends TableField>
            extends _StaticOnDuplicateKeyUpdateClause<_AssignmentCommaAliasSpec<C, F>>
            , _DynamicOnDuplicateKeyUpdateClause<C, F, AliasColumnConsumer<F>>, Insert._InsertSpec {

    }


    interface _ColumnAliasListAliasClause<C, F extends TableField>
            extends _ColumnAliasListClause<C, _OnDuplicateKeyUpdateAliasSpec<C, F>> {

    }


    interface _AsRowAliasSpec<C, F extends TableField>
            extends Statement._AsClause<_ColumnAliasListAliasClause<C, F>>, _OnDuplicateKeyUpdateFieldSpec<C, F> {

    }


    /*-------------------below domain insert syntax interfaces  -------------------*/


    interface _DomainInsertValueClause<C, T extends IDomain, F extends TableField>
            extends Insert._DomainValueClause<C, T, _AsRowAliasSpec<C, F>> {

    }

    interface _DomainColumnListSpec<C, T extends IDomain, F extends TableField>
            extends _ColumnListClause<C, F, _DomainInsertValueClause<C, T, F>>, _DomainInsertValueClause<C, T, F> {

    }

    interface _DomainSinglePartitionSpec<C, T extends IDomain, F extends TableField>
            extends MySQLQuery._PartitionClause<C, _DomainColumnListSpec<C, T, F>>, _DomainColumnListSpec<C, T, F> {

    }


    interface _DomainChildPartitionSpec<C, T extends IDomain, F extends TableField>
            extends _ChildPartitionClause<C, _DomainColumnListSpec<C, T, F>>, _DomainColumnListSpec<C, T, F> {

    }

    interface _DomainParentPartitionSpec<C, T extends IDomain, F extends TableField>
            extends _ParentPartitionClause<C, _DomainChildPartitionSpec<C, T, F>>, _DomainChildPartitionSpec<C, T, F> {

    }


    interface _DomainIntoClause<C> {

        <T extends IDomain> _DomainSinglePartitionSpec<C, T, FieldMeta<T>> into(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T, FieldMeta<? super T>> into(ChildTableMeta<T> table);

    }


    interface _DomainInsertIntoSpec<C> extends _InsertClause<C, _DomainIntoClause<C>> {

        <T extends IDomain> _DomainSinglePartitionSpec<C, T, FieldMeta<T>> insertInto(SingleTableMeta<T> table);

        <T extends IDomain> _DomainParentPartitionSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table);

    }

    interface _PreferLiteralSpec<C>
            extends Insert._PreferLiteralClause<_DomainInsertIntoSpec<C>>, _DomainInsertIntoSpec<C> {

    }

    interface _DomainOptionSpec<C> extends Insert._NullOptionClause<_PreferLiteralSpec<C>>
            , Insert._MigrationOptionClause<_PreferLiteralSpec<C>>, _PreferLiteralSpec<C> {

    }

    /*-------------------below value insert syntax interfaces-------------------*/


}
