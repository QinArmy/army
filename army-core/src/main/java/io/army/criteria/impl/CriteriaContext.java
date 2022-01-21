package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

interface CriteriaContext {

    <T extends IDomain, F> QualifiedField<T, F> qualifiedField(String tableAlias, FieldMeta<T, F> field);

    <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName);

    <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType);

    @Deprecated
    void onAddSubQuery(SubQuery subQuery, String subQueryAlias);

    @Deprecated
    void onAddTable(TableMeta<?> tableMeta, String tableAlias);

    default void onAddTablePart(TablePart tablePart, String alias) {
        throw new UnsupportedOperationException();
    }

    default void onAddBlock(_TableBlock block) {
        throw new UnsupportedOperationException();
    }

    default void onFirstBlock(_TableBlock block) {
        throw new UnsupportedOperationException();
    }

    default TableBlock firstBlock() {
        throw new UnsupportedOperationException();
    }


    <E> Expression<E> composeRef(String selectionAlias);

    @Nullable
    <C> C criteria();

    List<_TableBlock> clear();

}
