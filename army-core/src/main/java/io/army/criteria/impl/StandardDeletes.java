package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.Item;
import io.army.criteria.StandardDelete;
import io.army.criteria.impl.inner._BatchDml;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard domain delete statement.
 * </p>
 *
 * @since 1.0
 */
abstract class StandardDeletes<I extends Item, DR, WR, WA>
        extends SingleDelete<I, WR, WA, Object, Object, Object, Object>
        implements StandardDelete, Delete
        , StandardDelete._DeleteFromClause<DR> {

    static <I extends Item> _StandardDeleteClause<I> singleDelete(Function<Delete, I> function) {
        return new SimpleSingleDelete<>(function);
    }

    static <I extends Item> _BatchDeleteClause<I> batchSingleDelete(Function<Delete, I> function) {
        return new BatchSingleDelete<>(function);
    }


    private TableMeta<?> deleteTable;

    private String tableAlias;

    StandardDeletes() {
        super(CriteriaContexts.primarySingleDmlContext());
    }


    @SuppressWarnings("unchecked")
    @Override
    public final DR deleteFrom(final @Nullable TableMeta<?> table, final @Nullable String tableAlias) {
        if (this.deleteTable != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (table == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (tableAlias == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.deleteTable = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    public final TableMeta<?> table() {
        final TableMeta<?> deleteTable = this.deleteTable;
        if (deleteTable == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return deleteTable;
    }

    @Override
    public final String tableAlias() {
        final String tableAlias = this.tableAlias;
        if (tableAlias == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return tableAlias;
    }


    @Override
    final void onClear() {
        if (this instanceof BatchSingleDelete) {
            ((BatchSingleDelete<?>) this).paramList = null;
        }
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL57;
    }

    /*################################## blow static inner class ##################################*/


    private static final class SimpleSingleDelete<I extends Item> extends StandardDeletes<
            I,
            _WhereSpec<I>,
            _DmlDeleteSpec<I>,
            _WhereAndSpec<I>>
            implements _StandardDeleteClause<I>
            , _WhereSpec<I>
            , _WhereAndSpec<I> {

        private final Function<Delete, I> function;

        private SimpleSingleDelete(Function<Delete, I> function) {
            this.function = function;
        }


        @Override
        I onAsDelete() {
            return this.function.apply(this);
        }


    }//SimpleSingleDelete


    private static final class BatchSingleDelete<I extends Item> extends StandardDeletes<
            I,
            _BatchWhereSpec<I>,
            _BatchParamClause<_DmlDeleteSpec<I>>,
            _BatchWhereAndSpec<I>> implements
            _BatchDeleteClause<I>
            , _BatchWhereSpec<I>
            , _BatchWhereAndSpec<I>, _BatchDml {

        private final Function<Delete, I> function;


        private List<?> paramList;


        private BatchSingleDelete(Function<Delete, I> function) {
            this.function = function;
        }

        @Override
        public <P> _DmlDeleteSpec<I> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<I> paramList(Supplier<List<P>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public _DmlDeleteSpec<I> paramList(Function<String, ?> function, String keyName) {
            return this.paramList((List<?>) function.apply(keyName));
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
        I onAsDelete() {
            if (this.paramList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this.function.apply(this);
        }


    }//BatchSingleDelete




}
