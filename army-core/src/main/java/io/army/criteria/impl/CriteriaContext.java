package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;

import java.util.List;
import java.util.function.Function;

interface CriteriaContext {

    CteItem refCte(String cteName);

    boolean cteList(boolean recursive, List<Cte> cteList, boolean subStatement);


    boolean finishCteRefs(boolean recursive, String thisCteName, Function<String, Cte> function
            , boolean subStatement);

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

    @Deprecated
    default void onAddNoOnBlock(_TableBlock block) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default void onBracketBlock(_TableBlock block) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default void onJoinType(_JoinType joinType) {
        throw new UnsupportedOperationException();
    }

    _TableBlock lastTableBlockWithoutOnClause();

    default boolean isExistWindow(String windowName) {
        throw new UnsupportedOperationException();
    }

    default boolean containTableAlias(String tableAlias) {
        throw new UnsupportedOperationException();
    }


    @Nullable
    <C> C criteria();

    List<_TableBlock> clear();

}
