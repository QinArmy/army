package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

interface CriteriaContext {

    default Cte refCte(String cteName) {
        throw new UnsupportedOperationException();
    }

    default boolean cteList(boolean recursive, List<Cte> cteList, boolean subStatement) {
        final Map<String, Cte> cteMap = new HashMap<>((int) (cteList.size() / 0.75F));

        for (Cte cte : cteList) {
            ((CriteriaContextSpec) ((SQLs.CteImpl) cte).subQuery)
                    .getCriteriaContext().finishCteRefs(cteMap::get, subStatement);
        }
        throw new UnsupportedOperationException();
    }

    default boolean finishCteRefs(Function<String, Cte> function, boolean subStatement) {
        throw new UnsupportedOperationException();
    }

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

    void onAddNoOnBlock(_TableBlock block);

    void onBracketBlock(_TableBlock block);

    void onJoinType(_JoinType joinType);

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
