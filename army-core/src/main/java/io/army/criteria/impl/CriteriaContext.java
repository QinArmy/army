package io.army.criteria.impl;

import io.army.criteria.DerivedField;
import io.army.criteria.Expression;
import io.army.criteria.QualifiedField;
import io.army.criteria.SubQuery;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

interface CriteriaContext {

    <T extends IDomain, F> QualifiedField<T, F> qualifiedField(String tableAlias, FieldMeta<T, F> field);

    <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName);

    <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType);

    void onAddSubQuery(SubQuery subQuery, String subQueryAlias);

    void onAddTable(TableMeta<?> tableMeta, String tableAlias);

    <E> Expression<E> composeRef(String selectionAlias);

    @Nullable
    <C> C criteria();

    void clear();

}
