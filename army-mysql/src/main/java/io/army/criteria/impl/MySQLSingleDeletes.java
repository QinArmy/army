package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._MySQLSingleDelete;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.util.ArrayUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementations of MySQL single-table DELETE syntax
 * </p>
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLSimpleDelete}</li>
 *         <li>{@link MySQLBatchDelete}</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleDeletes<I extends Item, WE extends Item, DT, PR, WR, WA, OR, LR>
        extends SingleDeleteStatement.WithSingleDelete<I, MySQLCtes, WE, WR, WA, OR, LR, Object, Object>
        implements MySQLDelete,
        _MySQLSingleDelete,
        DeleteStatement,
        MySQLDelete._SingleDeleteClause<DT>,
        MySQLStatement._PartitionClause<PR>,
        DeleteStatement._SingleDeleteFromClause<DT> {


    static <I extends Item> _SingleWithSpec<I> simple(@Nullable ArmyStmtSpec spec, Function<? super Delete, I> function) {
        return new MySQLSimpleDelete<>(spec, function);
    }

    static _BatchSingleWithSpec<BatchDelete> batch() {
        return new MySQLBatchDelete();
    }

    private List<Hint> hintList;

    private List<MySQLSyntax.Modifier> modifierList;

    private SingleTableMeta<?> deleteTable;

    private String tableAlias;

    private List<String> partitionList;


    private MySQLSingleDeletes(@Nullable ArmyStmtSpec spec) {
        super(spec, CriteriaContexts.primarySingleDmlContext(spec));
    }

    @Override
    public final DT deleteFrom(final @Nullable SingleTableMeta<?> table, SQLs.WordAs wordAs,
                               final @Nullable String tableAlias) {
        if (this.deleteTable != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        if (table == null || tableAlias == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.deleteTable = table;
        this.tableAlias = tableAlias;
        return (DT) this;
    }

    @Override
    public final _SingleDeleteFromClause<DT> delete(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
        this.hintList = MySQLUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.context, modifierList, MySQLUtils::deleteModifier);
        return this;
    }

    @Override
    public final DT from(SingleTableMeta<?> table, SQLsSyntax.WordAs wordAs, String alias) {
        return this.deleteFrom(table, wordAs, alias);
    }

    @Override
    public final PR partition(String first, String... rest) {
        this.partitionList = ArrayUtils.unmodifiableListOf(first, rest);
        return (PR) this;
    }

    @Override
    public final PR partition(Consumer<Consumer<String>> consumer) {
        this.partitionList = CriteriaUtils.stringList(this.context, true, consumer);
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Consumer<Consumer<String>> consumer) {
        this.partitionList = CriteriaUtils.stringList(this.context, false, consumer);
        return (PR) this;
    }

    @Override
    public final List<Hint> hintList() {
        final List<Hint> list = this.hintList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<MySQLs.Modifier> modifierList() {
        final List<MySQLs.Modifier> list = this.modifierList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final SingleTableMeta<?> table() {
        final SingleTableMeta<?> deleteTable = this.deleteTable;
        if (deleteTable == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return deleteTable;
    }

    @Override
    public final String tableAlias() {
        final String alias = this.tableAlias;
        if (alias == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return alias;
    }


    @Override
    public final List<String> partitionList() {
        final List<String> list = this.partitionList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    abstract I asMySQLDelete();

    @Override
    final I onAsDelete() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (this.partitionList == null) {
            this.partitionList = Collections.emptyList();
        }
        if (this.deleteTable == null || this.tableAlias == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.asMySQLDelete();
    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.partitionList = null;
        if (this instanceof MySQLBatchDelete) {
            ((MySQLBatchDelete) this).paramList = null;
        }

    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }


    private static final class MySQLSimpleDelete<I extends Item> extends MySQLSingleDeletes<
            I,
            MySQLDelete._SimpleSingleDeleteClause<I>,
            MySQLDelete._SinglePartitionSpec<I>,
            MySQLDelete._SingleWhereClause<I>,
            MySQLDelete._OrderBySpec<I>,
            MySQLDelete._SingleWhereAndSpec<I>,
            MySQLDelete._OrderByCommaSpec<I>,
            Statement._DmlDeleteSpec<I>>
            implements MySQLDelete._SingleWithSpec<I>,
            MySQLDelete._SinglePartitionSpec<I>,
            MySQLDelete._SingleWhereAndSpec<I>,
            MySQLDelete._OrderByCommaSpec<I>,
            Delete {

        private final Function<? super Delete, I> function;

        private MySQLSimpleDelete(@Nullable ArmyStmtSpec spec, Function<? super Delete, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_SimpleSingleDeleteClause<I>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_SimpleSingleDeleteClause<I>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        I asMySQLDelete() {
            return this.function.apply(this);
        }

    }//MySQLSimpleDelete


    private static final class MySQLBatchDelete extends MySQLSingleDeletes<
            BatchDelete,
            MySQLDelete._BatchSingleDeleteClause<BatchDelete>,
            MySQLDelete._BatchSinglePartitionSpec<BatchDelete>,
            MySQLDelete._BatchSingleWhereClause<BatchDelete>,
            MySQLDelete._BatchOrderBySpec<BatchDelete>,
            MySQLDelete._BatchSingleWhereAndSpec<BatchDelete>,
            MySQLDelete._BatchOrderByCommaSpec<BatchDelete>,
            MySQLDelete._BatchParamClause<_DmlDeleteSpec<BatchDelete>>>
            implements MySQLDelete._BatchSingleWithSpec<BatchDelete>,
            MySQLDelete._BatchSinglePartitionSpec<BatchDelete>,
            MySQLDelete._BatchSingleWhereAndSpec<BatchDelete>,
            MySQLDelete._BatchOrderByCommaSpec<BatchDelete>,
            BatchDelete,
            _BatchDml {

        private List<?> paramList;

        private MySQLBatchDelete() {
            super(null);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_BatchSingleDeleteClause<BatchDelete>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_BatchSingleDeleteClause<BatchDelete>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <P> _DmlDeleteSpec<BatchDelete> namedParamList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<BatchDelete> namedParamList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlDeleteSpec<BatchDelete> namedParamList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList(this.context, (List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        BatchDelete asMySQLDelete() {
            if (this.paramList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }


    }//MySQLBatchDelete


}
