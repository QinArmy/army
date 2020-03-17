package io.army.criteria.impl;

import io.army.criteria.AliasFieldMeta;
import io.army.criteria.Expression;
import io.army.criteria.SubQuery;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

interface CriteriaContext {

    <T extends IDomain, F> AliasFieldMeta<T, F> aliasField(String tableAlias, FieldMeta<T, F> fieldMeta);

    <E> Expression<E> ref(String subQueryAlias, String derivedFieldName);

    <E> Expression<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType);

    void onAddSubQuery(SubQuery subQuery, String subQueryAlias);

    <C> C criteria();

}
