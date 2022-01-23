package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;

import java.util.List;

interface CriteriaContext {

    void selectList(List<? extends SelectPart> selectPartList);

    <T extends IDomain, F> QualifiedField<T, F> qualifiedField(String tableAlias, FieldMeta<T, F> field);

    <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName);

    <E> Expression<E> ref(String selectionAlias);

    /**
     * @throws CriteriaException when var exists.
     */
    <E> VarExpression<E> createVar(String name, ParamMeta paramMeta) throws CriteriaException;

    /**
     * @throws CriteriaException when var not exists.
     */
    <E> VarExpression<E> var(String name) throws CriteriaException;


    default void onAddBlock(_TableBlock block) {
        throw new UnsupportedOperationException();
    }

    default void onFirstBlock(_TableBlock block) {
        throw new UnsupportedOperationException();
    }

    default _TableBlock firstBlock() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    <C> C criteria();

    List<_TableBlock> clear();

}
