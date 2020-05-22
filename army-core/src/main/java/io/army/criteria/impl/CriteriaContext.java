package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.LogicalField;
import io.army.criteria.SubQuery;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

interface CriteriaContext {

    <T extends IDomain, F> LogicalField<T, F> aliasField(String tableAlias, FieldMeta<T, F> fieldMeta);

    <E> Expression<E> ref(String subQueryAlias, String derivedFieldName);

    <E> Expression<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType);

    void onAddSubQuery(SubQuery subQuery, String subQueryAlias);

    void onAddTable(TableMeta<?> tableMeta, String tableAlias);

    <E> Expression<E> composeRef(String selectionAlias);

    <C> C criteria();

    void clear();

}
