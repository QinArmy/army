package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

final class UnionQueryContext implements CriteriaContext {

    final List<? extends SortPart> sortPartList;

    UnionQueryContext(List<? extends SortPart> sortPartList) {
        this.sortPartList = sortPartList;
    }

    @Override
    public <T extends IDomain, F> QualifiedField<T, F> qualifiedField(String tableAlias, FieldMeta<T, F> field) {
        return null;
    }

    @Override
    public <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName) {
        return null;
    }

    @Override
    public <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType) {
        return null;
    }

    @Override
    public void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {

    }

    @Override
    public void onAddTable(TableMeta<?> tableMeta, String tableAlias) {

    }

    @Override
    public <E> Expression<E> composeRef(String selectionAlias) {
        return null;
    }

    @Override
    public <C> C criteria() {
        return null;
    }

    @Override
    public void clear() {

    }


}
