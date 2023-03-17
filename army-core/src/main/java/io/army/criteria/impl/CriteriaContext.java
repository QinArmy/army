package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.VarExpression;
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

    CteItem refCte(String cteName);

    CriteriaContext onAddSelectItem(SelectItem selectItem);


    int selectionSize();

    Selection selection(String alias);

    void contextEndEvent();

    void addEndEventListener(Runnable listener);


    DerivedField refThisOrOuter(String derivedAlias, String selectionAlias);

    DerivedField refThis(String derivedAlias, String selectionAlias);


    <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field);

    /**
     * @deprecated use {@link #refThisOrOuter(String, String)}
     */
    @Deprecated
    DerivedField refOuter(String derivedAlias, String fieldName);

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

    void bufferNestedDerived(String tableAlias, DerivedTable table);


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

    List<String> derivedColumnAliasList();

    List<Selection> selectionList();

    void onDerivedColumnAliasList(List<String> aliasList);

    boolean isBracketAndNotEnd();


}
