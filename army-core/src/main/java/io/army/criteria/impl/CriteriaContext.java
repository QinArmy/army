package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;

import java.util.List;

interface CriteriaContext {

    @Nullable
    CriteriaContext getOuterContext();

    CriteriaContext getNonNullOuterContext();

    CteConsumer onBeforeWithClause(boolean recursive);

    void onStartCte(String name);

    void onCteColumnAlias(String name, List<String> columnAliasList);

    void onAddCte(_Cte cte);

    /**
     * @return a unmodified list
     */
    @Deprecated
    List<_Cte> endWithClause(boolean required);

    boolean isWithRecursive();

    /**
     * @return a unmodified list
     */
    List<_Cte> endWithClause(boolean recursive, boolean required);

    CteItem refCte(String cteName);

    CriteriaContext onAddSelectItem(SelectItem selectItem);

    List<SelectItem> endSelectClause();

    int selectionSize();

    Selection selection(String alias);

    void contextEndEvent();

    void addEndEventListener(Runnable listener);

    @Deprecated
    void onAddDerivedGroup(DerivedGroup group);

    @Deprecated
    void selectList(List<? extends SelectItem> selectItemList);

    DerivedField ref(String derivedTable, String derivedFieldName);

    DerivedField refThis(String derivedTable, String derivedFieldName);

    <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field);

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


    _TableBlock lastBlock();

    void onAddWindow(String windowName);

    boolean isExistWindow(String windowName);

    void onRefWindow(String windowName);

    @Nullable
    TableMeta<?> getTable(String tableAlias);

    @Deprecated

    @Nullable
    <C> C criteria();

    List<_TableBlock> endContext();

    /**
     * @return this
     */
    CriteriaContext endContextBeforeSelect();


    @Deprecated
    interface CteConsumer {

        void addCte(_Cte cte);

        /**
         * @return a unmodified list.
         */
        List<_Cte> end();

    }

    @Deprecated
    interface OuterContextSpec {

        @Nullable
        CriteriaContext getOuterContext();
    }

}
