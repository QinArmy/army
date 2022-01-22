package io.army.criteria.impl;

import io.army.criteria.DerivedField;
import io.army.criteria.Expression;
import io.army.criteria.QualifiedField;
import io.army.criteria.SortPart;
import io.army.criteria.impl.inner._TableBlock;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

import java.util.Collections;
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
    public <E> Expression<E> ref(String selectionAlias) {
        return null;
    }

    @Override
    public <C> C criteria() {
        return null;
    }

    @Override
    public List<_TableBlock> clear() {
        return Collections.emptyList();
    }


}
