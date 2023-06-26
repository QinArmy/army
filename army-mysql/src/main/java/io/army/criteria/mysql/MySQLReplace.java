package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL REPLACE statement.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/replace.html">REPLACE Statement</a>
 * @since 1.0
 */
public interface MySQLReplace extends MySQLStatement {


    interface _ReplaceClause<RR> extends Item {

        RR replace(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

    }


    /*-------------------below  replace api interfaces -------------------*/

    interface _MySQLValuesStaticParensClause<I extends Item, T>
            extends InsertStatement._ValuesParensClause<T, _ValuesStaticParensCommaSpec<I, T>> {

    }

    interface _ValuesStaticParensCommaSpec<I extends Item, T> extends _CommaClause<_MySQLValuesStaticParensClause<I, T>>,
            _DmlInsertClause<I> {

    }

    interface _ValueColumnDefaultSpec<I extends Item, T>
            extends InsertStatement._FullColumnDefaultClause<T, _ValueColumnDefaultSpec<I, T>>,
            InsertStatement._DomainValueClause<T, _DmlInsertClause<I>>,
            InsertStatement._StaticValuesClause<_MySQLValuesStaticParensClause<I, T>>,
            InsertStatement._DynamicValuesClause<T, _DmlInsertClause<I>> {

    }

    interface _ComplexColumnDefaultSpec<I extends Item, T> extends _ValueColumnDefaultSpec<I, T>,
            InsertStatement._QueryInsertSpaceClause<MySQLQuery._WithSpec<_DmlInsertClause<I>>, _DmlInsertClause<I>> {

    }

    interface _ColumnListSpec<I extends Item, T>
            extends InsertStatement._ColumnListParensClause<T, _ComplexColumnDefaultSpec<I, T>>,
            _ValueColumnDefaultSpec<I, T> {

    }

    interface _PartitionSpec<I extends Item, T> extends MySQLStatement._PartitionClause<_ColumnListSpec<I, T>>,
            _ColumnListSpec<I, T> {

    }

    interface _ChildIntoClause<P> {

        <T> _PartitionSpec<Insert, T> into(ComplexTableMeta<P, T> table);
    }


    interface _ChildReplaceIntoSpec<P> extends _ReplaceClause<_ChildIntoClause<P>> {

        <T> _PartitionSpec<Insert, T> replaceInto(ComplexTableMeta<P, T> table);
    }


    interface _ParentReplace<P> extends Insert, InsertStatement._ChildPartClause<_ChildReplaceIntoSpec<P>> {

    }

    interface _PrimaryIntoClause {

        <T> _PartitionSpec<Insert, T> into(SimpleTableMeta<T> table);

        <P> _PartitionSpec<_ParentReplace<P>, P> into(ParentTableMeta<P> table);

    }

    interface _PrimaryReplaceIntoSpec extends _ReplaceClause<_PrimaryIntoClause> {

        <T> _PartitionSpec<Insert, T> replaceInto(SimpleTableMeta<T> table);

        <P> _PartitionSpec<_ParentReplace<P>, P> replaceInto(ParentTableMeta<P> table);

    }

    interface _PrimaryPreferLiteralSpec
            extends InsertStatement._PreferLiteralClause<_PrimaryReplaceIntoSpec>, _PrimaryReplaceIntoSpec {

    }

    interface _PrimaryNullOptionSpec extends InsertStatement._NullOptionClause<_PrimaryPreferLiteralSpec>,
            _PrimaryPreferLiteralSpec {

    }

    interface _PrimaryOptionSpec extends InsertStatement._MigrationOptionClause<_PrimaryNullOptionSpec>,
            _PrimaryNullOptionSpec {

    }


    /**
     * <p>
     * This interface representing INTO clause that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySingleIntoClause<I extends Item> {

        <T> _PartitionSpec<I, T> into(SingleTableMeta<T> table);

    }

    /**
     * <p>
     * This interface representing REPLACE INTO spec that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySingleReplaceIntoSpec<I extends Item> extends _ReplaceClause<_PrimarySingleIntoClause<I>> {

        <T> _PartitionSpec<I, T> replaceInto(SingleTableMeta<T> table);

    }

    /**
     * <p>
     * This interface representing {@link LiteralMode} spec that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySinglePreferLiteralSpec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_PrimarySingleReplaceIntoSpec<I>>,
            _PrimarySingleReplaceIntoSpec<I> {

    }

    /**
     * <p>
     * This interface representing {@link NullMode} spec that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySingleNullOptionSpec<I extends Item>
            extends InsertStatement._NullOptionClause<_PrimarySinglePreferLiteralSpec<I>>,
            _PrimarySinglePreferLiteralSpec<I> {

    }

    /**
     * <p>
     * This interface representing migration spec that support only {@link SingleTableMeta}.
     * </p>
     */
    interface _PrimarySingleOptionSpec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_PrimarySingleNullOptionSpec<I>>,
            _PrimarySingleNullOptionSpec<I> {

    }


}
