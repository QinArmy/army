package io.army.criteria.impl;

import io.army.criteria.Select;
import io.army.criteria.StandardQuery;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._StandardSelect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Objects;

abstract class StandardSelect<C> extends StandardQueryImpl<Select, C>
        implements _StandardSelect, Select {

    static io.army.criteria.StandardSelect.StandardSelect<Void> create() {
        return new SimpleSelect<>(null);
    }

    static <C> io.army.criteria.StandardSelect.StandardSelect<C> create(final @Nullable C criteria) {
        Objects.requireNonNull(criteria);
        return new SimpleSelect<>(criteria);
    }

    static <C> io.army.criteria.StandardSelect.StandardSelect<C> unionAndSelect(Select left, UnionType unionType, @Nullable C criteria) {
        return null;
    }

    private final CriteriaContext criteriaContext;

    private StandardSelect(@Nullable C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final StandardUnionResultSpec<Select, C> bracketsQuery() {
        return ComposeQueries.bracketsSelect(this.criteria, this.asQuery());
    }



    /*################################## blow package method ##################################*/

    @Override
    final Select onAsQuery() {
        // must clear context
        CriteriaContextStack.clearContextStack(this.criteriaContext);
        return this;
    }

    @Override
    final StandardUnionResultSpec<Select, C> createUnionQuery(Select left, UnionType unionType, Select right) {
        right.prepared();
        return StandardUnionQuery.unionSelect(left, unionType, right, this.criteria);
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    final void onAddTable(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onAddTable(table, tableAlias);
    }

    @Override
    final StandardQuery.StandardSelectClauseSpec<Select, C> asQueryAndSelect(UnionType unionType) {
        return io.army.criteria.StandardSelect.StandardSelect.unionAndSelect(this.asQuery(), unionType, this.criteria);
    }

    private static final class SimpleSelect<C> extends io.army.criteria.StandardSelect.StandardSelect<C> {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }

    }


}
