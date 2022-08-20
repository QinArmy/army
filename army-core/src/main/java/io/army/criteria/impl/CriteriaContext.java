package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;

import java.util.List;

interface CriteriaContext {

    CteConsumer onBeforeWithClause(boolean recursive);

    CteItem refCte(String cteName);

    void contextEnd();

    void addEndEventListener(Runnable listener);

    void onAddDerivedGroup(DerivedGroup group);

    void selectList(List<? extends SelectItem> selectItemList);

    DerivedField ref(String derivedTable, String derivedFieldName);

    <T> QualifiedField<T> qualifiedField(String tableAlias, FieldMeta<T> field);

    DerivedField outerRef(String derivedTable, String derivedFieldName);

    Expression ref(String selectionAlias);

    /**
     * @throws CriteriaException when var exists.
     */
    VarExpression createVar(String name, TypeMeta paramMeta) throws CriteriaException;

    /**
     * @throws CriteriaException when var not exists.
     */
    VarExpression var(String name) throws CriteriaException;


    void onAddBlock(_TableBlock block);


    _TableBlock lastTableBlockWithoutOnClause();

    void onAddWindow(String windowName);

    boolean isExistWindow(String windowName);

    void onRefWindow(String windowName);

    @Nullable
    TableMeta<?> getTable(String tableAlias);


    @Nullable
    <C> C criteria();

    List<_TableBlock> clear();


    interface CteConsumer {

        void addCte(Cte cte);

        /**
         * @return a unmodified list.
         */
        List<Cte> end();

    }

    interface OuterContextSpec {

        @Nullable
        CriteriaContext getOuterContext();
    }

}
