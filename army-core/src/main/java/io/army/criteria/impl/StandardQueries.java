package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is a implementation of {@link StandardQuery}.
 * </p>
 *
 * @see StandardUnionQueries
 * @since 1.0
 */
abstract class StandardQueries<C, Q extends Item> extends SimpleQuery<
        C,
        Q,
        SQLs.SelectModifier,
        StandardQuery._FromSpec<C, Q>, // SR
        StandardQuery._JoinSpec<C, Q>,// FT
        StandardQuery._JoinSpec<C, Q>,// FS
        Void,                          //FC
        Statement._OnClause<C, StandardQuery._JoinSpec<C, Q>>, // JT
        Statement._OnClause<C, StandardQuery._JoinSpec<C, Q>>, // JS
        Void,                               // JC
        StandardQuery._GroupBySpec<C, Q>, // WR
        StandardQuery._WhereAndSpec<C, Q>, // AR
        StandardQuery._HavingSpec<C, Q>, // GR
        StandardQuery._OrderBySpec<C, Q>, // HR
        StandardQuery._LimitSpec<C, Q>, // OR
        StandardQuery._SelectSpec<C, Q>> // SP

        implements StandardQuery, StandardQuery._SelectSpec<C, Q>, StandardQuery._FromSpec<C, Q>
        , StandardQuery._JoinSpec<C, Q>, StandardQuery._WhereAndSpec<C, Q>, StandardQuery._HavingSpec<C, Q>
        , _StandardQuery {


    static <C> _SelectSpec<C, Select> primaryQuery(@Nullable C criteria) {
        return new SimpleSelect<>(CriteriaContexts.primaryQueryContext(criteria));
    }


    static <C, Q extends Item> _SelectSpec<C, Q> subQuery(@Nullable C criteria, CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        return new SimpleSubQuery<>(CriteriaContexts.subQueryContext(criteria, outerContext), function);
    }

    @SuppressWarnings("unchecked")
    static <C, Q extends Query> _SelectSpec<C, Q> unionAndQuery(Q query, UnionType unionType) {
        final _SelectSpec<C, ?> spec;
        final CriteriaContext criteriaContext;
        if (query instanceof Select) {
            criteriaContext = CriteriaContexts.primaryQueryContextFrom(query);
            spec = new UnionAndSelect<>((Select) query, unionType, criteriaContext);
        } else if (query instanceof SubQuery) {
            criteriaContext = CriteriaContexts.subQueryContextFrom(query);
            spec = new UnionAndSubQuery<>((SubQuery) query, unionType, criteriaContext);
        } else {
            throw _Exceptions.unknownRowSetType(query);
        }
        return (_SelectSpec<C, Q>) spec;
    }


    private LockMode lockMode;

    private long offset;

    private long rowCount;

    private StandardQueries(CriteriaContext context) {
        super(context);

    }


    @Override
    public final _LockSpec<C, Q> limit(long rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final _LockSpec<C, Q> limit(Supplier<? extends Number> supplier) {
        this.rowCount = CriteriaUtils.asLimitParam(this.context, supplier.get());
        return this;
    }

    @Override
    public final _LockSpec<C, Q> limit(Function<C, ? extends Number> function) {
        this.rowCount = CriteriaUtils.asLimitParam(this.context, function.apply(this.criteria));
        return this;
    }

    @Override
    public final _LockSpec<C, Q> limit(Function<String, ?> function, String keyName) {
        this.rowCount = CriteriaUtils.asLimitParam(this.context, function.apply(keyName));
        return this;
    }

    @Override
    public final _LockSpec<C, Q> ifLimit(Supplier<? extends Number> supplier) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.context, supplier.get());
        return this;
    }

    @Override
    public final _LockSpec<C, Q> ifLimit(Function<C, ? extends Number> function) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.context, function.apply(this.criteria));
        return this;
    }

    @Override
    public final _LockSpec<C, Q> ifLimit(Function<String, ?> function, String keyName) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.context, function.apply(keyName));
        return this;
    }


    @Override
    public final _LockSpec<C, Q> limit(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final _LockSpec<C, Q> limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        CriteriaUtils.limitPair(this.context, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<C, Q> limit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        CriteriaUtils.limitPair(this.context, function.apply(offsetKey), function.apply(rowCountKey), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<C, Q> ifLimit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        CriteriaUtils.ifLimitPair(this.context, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<C, Q> ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        CriteriaUtils.ifLimitPair(this.context, function.apply(offsetKey), function.apply(rowCountKey), this::limit);
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> lock(@Nullable LockMode lockMode) {
        if (lockMode == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLock(Supplier<LockMode> supplier) {
        this.lockMode = supplier.get();
        return this;
    }

    @Override
    public final String toString() {
        final String s;
        if (this instanceof Select && this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }


    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        //standard statement don't hints
        throw ContextStack.castCriteriaApi(this.context);
    }

    @Override
    final List<SQLs.SelectModifier> asModifierList(final @Nullable List<SQLs.SelectModifier> modifiers) {
        if (modifiers == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return CriteriaUtils.asModifierList(this.context, modifiers, CriteriaUtils::standardModifier);
    }

    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, table, alias);
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, tableItem, alias);
    }

    @Override
    final _OnClause<C, _JoinSpec<C, Q>> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final _OnClause<C, _JoinSpec<C, Q>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }


    @Override
    final Void createCteBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    @Override
    final void onEndQuery() {
        //no-op
    }


    @Override
    public final long offset() {
        return this.offset;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }

    @Override
    final void onClear() {
        this.lockMode = null;
    }




    /*################################## blow private inter class method ##################################*/


    private static class SimpleSelect<C> extends StandardQueries<C, Select>
            implements Select {

        private SimpleSelect(CriteriaContext context) {
            super(context);
        }

        @Override
        final Select onAsQuery() {
            return this;
        }


        @Override
        final _SelectSpec<C, Select> createQueryUnion(UnionType unionType) {
            return new UnionAndSelect<>(this, unionType, CriteriaContexts.primaryQueryContext(this.criteria));
        }


    }//SimpleSelect

    private static final class UnionAndSelect<C> extends StandardQueries<C, Select>
            implements Select {

        private final Select left;

        private final UnionType unionType;

        private UnionAndSelect(Select left, UnionType unionType, CriteriaContext context) {
            super(context);
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        Select onAsQuery() {
            return StandardUnionQueries.unionQuery(this.left, this.unionType, this)
                    .asQuery();
        }

        @Override
        _SelectSpec<C, Select> createQueryUnion(final UnionType unionType) {
            final Select unionLeft;
            unionLeft = StandardUnionQueries.unionQuery(this.left, this.unionType, this)
                    .asQuery();
            return new UnionAndSelect<>(unionLeft, unionType, CriteriaContexts.primaryQueryContext(this.criteria));
        }

    }//UnionAndSelect


    private static class SimpleSubQuery<C, Q extends Item> extends StandardQueries<C, Q>
            implements SubQuery {

        private final Function<SubQuery, Q> function;

        private SimpleSubQuery(CriteriaContext context, Function<SubQuery, Q> function) {
            super(context);
            this.function = function;
        }

        @Override
        Q onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectSpec<C, Q> createQueryUnion(UnionType unionType) {
            return null;
        }


    } // SimpleSubQuery


}
