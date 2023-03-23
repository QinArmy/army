package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.VarExpression;
import io.army.criteria.impl.inner._AliasDerivedBlock;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._SelectItem;
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

    @Nullable
    CriteriaContext getLeftContext();

    CriteriaContext getNonNullLeftContext();

    CriteriaContext onBeforeWithClause(boolean recursive);

    void onStartCte(String name);

    void onCteColumnAlias(String name, List<String> columnAliasList);


    void onAddCte(_Cte cte);


    List<_Cte> getCteList();

    boolean isWithRecursive();

    /**
     * @return a unmodified list
     */
    List<_Cte> endWithClause(boolean recursive, boolean required);

    _Cte refCte(String cteName);

    CriteriaContext onAddSelectItem(SelectItem selectItem);


    /**
     * <p>
     * This method flat {@link #selectItemList()} as list of {@link  Selection}.
     * </p>
     *
     * @return a unmodified list
     * @throws CriteriaException throw when context not end.
     */
    List<Selection> flatSelectItems();

    Selection selection(String alias);

    void contextEndEvent();

    void addEndEventListener(Runnable listener);


    DerivedField refThis(String derivedAlias, String selectionAlias);


    <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field);


    DerivedField refOuter(String derivedAlias, String fieldName);

    @Deprecated
    void onOrderByStart();

    Expression refSelection(String selectionAlias);

    void onSetInnerContext(CriteriaContext innerContext);

    /**
     * @throws CriteriaException when var exists.
     */
    VarExpression createVar(String name, TypeMeta paramMeta) throws CriteriaException;

    /**
     * @throws CriteriaException when var not exists.
     */
    VarExpression var(String name) throws CriteriaException;

    void bufferNestedDerived(_AliasDerivedBlock block);


    void onAddBlock(_TableBlock block);


    _TableBlock lastBlock();


    void onAddWindow(String windowName);

    void onRefWindow(String windowName);

    @Nullable
    TableMeta<?> getTable(String tableAlias);


    void onInsertRowAlias(String rowAlias);


    /**
     * <p>
     * should be invoked before {@link ContextStack#pop(CriteriaContext)}
     * </p>
     */
    List<_TableBlock> endContext();

    void endContextBeforeCommand();

    /**
     * @return a unmodified list
     * @throws CriteriaException throw when context not end
     */
    List<? extends _SelectItem> selectItemList();



}
