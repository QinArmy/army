/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.VarExpression;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

interface CriteriaContext {

    Dialect dialect();

    <T> T dialect(Class<T> type);

    void validateDialect(CriteriaContext context);

    @Nullable
    CriteriaContext getOuterContext();

    CriteriaContext getNonNullOuterContext();

    @Nullable
    CriteriaContext getLeftContext();

    CriteriaContext getNonNullLeftContext();

    void onBeforeWithClause(boolean recursive);

    void onStartCte(String name);

    void onCteColumnAlias(String name, List<String> columnAliasList);


    void onAddCte(_Cte cte);


    List<_Cte> getCteList();

    /**
     * <p>
     * This method is invoked by sub-dml,now WITH clause have not ended.
     * *
     *
     * @return unmodified list
     */
    List<_Cte> accessCteList();

    boolean isWithRecursive();

    /**
     * @return a unmodified list
     */
    List<_Cte> endWithClause(boolean recursive, boolean required);

    _Cte refCte(String cteName);

    void onAddDerivedGroup(String derivedAlis);

    CriteriaContext onAddSelectItem(SelectItem selectItem);


    /**
     * <p>
     * This method flat {@link #selectItemList()} as list of {@link  Selection}.
     * *
     *
     * @return a unmodified list
     * @throws CriteriaException throw when context not end.
     */
    List<Selection> flatSelectItems();

    @Nullable
    Selection selection(String alias);

    void contextEndEvent();

    void addEndEventListener(Runnable listener);

    void addSelectClauseEndListener(Runnable listener);

    void registerDeferSelectClause(Runnable deferSelectClause);


    /**
     * <p>This method is invoked by {@link SQLs#field(String, FieldMeta)}
     */
    @Nullable
    <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field);

    @Nullable
    DerivedField refField(String derivedAlias, String fieldName);

    Expression refSelection(String selectionAlias);

    /**
     * @param selectionOrdinal based 1 .
     */
    Expression refSelection(int selectionOrdinal);

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


    void onAddBlock(_TabularBlock block);


    _TabularBlock lastBlock();


    void onAddWindow(String windowName);

    /**
     * @param windowName non-null and non-empty
     * @throws CriteriaException throw when windowName is null or empty.
     */
    void onRefWindow(String windowName);

    @Nullable
    TableMeta<?> getTable(String tableAlias);

    @Nullable
    _SelectionMap getDerived(String derivedAlias);

    _SelectionMap getNonNullDerived(String derivedAlias);

    /**
     * <p>
     * should be invoked before {@link ContextStack#pop(CriteriaContext)}
     */
    List<_TabularBlock> endContext();

    void endContextBeforeCommand();

    /**
     * @return a unmodified list
     * @throws CriteriaException throw when context not end
     */
    List<? extends _SelectItem> selectItemList();


    /**
     * @param tableAlias table alias not insert row alias.
     */
    void singleDmlTable(TableMeta<?> table, String tableAlias);


    void insertRowAlias(TableMeta<?> table, String rowAlias);

    Expression insertValueField(FieldMeta<?> field, Function<FieldMeta<?>, Expression> function);

    void insertColumnList(List<FieldMeta<?>> columnList);


    RowElement row(String alias, SQLs.SymbolPeriod period, TableMeta<?> table);

    RowElement row(String alias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);

}
