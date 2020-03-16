package io.army.criteria.impl;

import io.army.criteria.AliasTableFieldMeta;
import io.army.criteria.Expression;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

interface CriteriaContext {

    <T extends IDomain, F> AliasTableFieldMeta<T, F> aliasField(String tableAlias, FieldMeta<T, F> fieldMeta);

    <E> Expression<E> ref(String subQueryAlias, String derivedFieldName);

    <E> Expression<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType);

   <C> C criteria();

}
