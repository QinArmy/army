package io.army.criteria.impl;

import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._StandardSelect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Objects;

abstract class StandardSelect<C> extends StandardQuery<Select, C>
        implements _StandardSelect, Select {

    static StandardSelect<Void> create() {
        return new SimpleSelect<>(null);
    }

    static <C> StandardSelect<C> create(final @Nullable C criteria) {
        Objects.requireNonNull(criteria);
        return new SimpleSelect<>(criteria);
    }

    static <C> StandardSelect<C> unionAndSelect(Select left, UnionType unionType, @Nullable C criteria) {
        return null;
    }

    private final CriteriaContext criteriaContext;

    private StandardSelect(@Nullable C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final UnionSpec<Select, C> bracketsQuery() {
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
    final UnionSpec<Select, C> createUnionQuery(Select left, UnionType unionType, Select right) {
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
    final SelectPartSpec<Select, C> asQueryAndSelect(UnionType unionType) {
        return StandardSelect.unionAndSelect(this.asQuery(), unionType, this.criteria);
    }

    private static final class SimpleSelect<C> extends StandardSelect<C> {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }

    }


}
