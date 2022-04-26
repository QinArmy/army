package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;

import java.util.List;

interface CriteriaContext {

    void selectList(List<? extends SelectItem> selectPartList);

    boolean containsTable(String tableAlias);

    <T extends IDomain> QualifiedField<T> qualifiedField(String tableAlias, FieldMeta<T> field);

    DerivedField ref(String subQueryAlias, String derivedFieldName);

    Expression ref(String selectionAlias);

    /**
     * @throws CriteriaException when var exists.
     */
    VarExpression createVar(String name, ParamMeta paramMeta) throws CriteriaException;

    /**
     * @throws CriteriaException when var not exists.
     */
    VarExpression var(String name) throws CriteriaException;


    void onAddBlock(_TableBlock block);

    void onBlockWithoutOnClause(_TableBlock block);

    void onBracketBlock(_TableBlock block);

    void onJoinType(_JoinType joinType);

    _TableBlock lastTableBlockWithoutOnClause();

    @Nullable
    <C> C criteria();

    List<_TableBlock> clear();

}
