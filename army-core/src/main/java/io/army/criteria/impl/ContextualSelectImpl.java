package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

final class ContextualSelectImpl<C> extends AbstractSelectImpl<C> implements CriteriaContext
        , Select.SelectionGroupAble<C> {

    /*################################## blow cache prop ##################################*/

    private Map<String, AliasFieldMeta<?, ?>> aliasTableFieldCache = new HashMap<>();

    private Map<String, RefSelection<?>> refSelectionCache = new HashMap<>();

    private Map<String, Collection<RefSelection<?>>> noSelectionRefCache = new HashMap<>();

    ContextualSelectImpl(C criteria) {
        super(criteria);
    }



    /*################################## blow CriteriaContext method ##################################*/

    @SuppressWarnings("unchecked")
    @Override
    public final <T extends IDomain, F> AliasFieldMeta<T, F> aliasField(
            String tableAlias, FieldMeta<T, F> fieldMeta) {
        AliasFieldMeta<T, F> aliasField = (AliasFieldMeta<T, F>) aliasTableFieldCache.computeIfAbsent(
                tableAlias + fieldMeta.fieldName()
                , k -> new AliasFieldMetaImpl<>(fieldMeta, tableAlias)
        );
        if (aliasField.fieldMeta() != fieldMeta) {
            throw new CriteriaException(ErrorCode.TABLE_ALIAS_DUPLICATION, "table alias[%s] duplication", tableAlias);
        }
        return aliasField;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <E> Expression<E> ref(String subQueryAlias, String derivedFieldName) {
        return (Expression<E>) refSelectionCache.computeIfAbsent(
                subQueryAlias + derivedFieldName
                , key -> createRefSelection(subQueryAlias, derivedFieldName, null)
        );

    }

    @SuppressWarnings("unchecked")
    @Override
    public final <E> Expression<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType) {
        return (Expression<E>) refSelectionCache.computeIfAbsent(
                subQueryAlias + derivedFieldName
                , key -> createRefSelection(subQueryAlias, derivedFieldName, selectionType)
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public final C criteria() {
        return this.criteria;
    }


    /*################################## blow protected template method ##################################*/

    @Override
    protected void doTable(TableMeta<?> table, String tableAlias) {

    }

    @Override
    protected void doSubQuery(SubQuery subQuery, String subQueryAlias) {

    }

    protected final void doPrepare() {
        CriteriaContextHolder.clearContext(this);

        this.aliasTableFieldCache.clear();
        this.aliasTableFieldCache = null;
        this.refSelectionCache.clear();
        this.refSelectionCache = null;

        this.noSelectionRefCache.clear();
        this.noSelectionRefCache = null;
    }


    /*################################## blow private method ##################################*/


    private <E> RefSelection<E> createRefSelection(String subQueryAlias, String derivedFieldName
            , @Nullable Class<E> selectionType) {
        RefSelection<E> refSelection;
        if (selectionType == null) {
            refSelection = new RefSelectionImpl<>(subQueryAlias, derivedFieldName);
        } else {
            refSelection = new RefSelectionImpl<>(subQueryAlias, derivedFieldName, selectionType);
        }
        Collection<RefSelection<?>> collection = this.noSelectionRefCache.computeIfAbsent(
                subQueryAlias, key -> new HashSet<>());

        collection.add(refSelection);

        return refSelection;
    }

   /* private void setSelection() {
        for (TableWrapper tableWrapper : tableWrapperList) {
            Collection<RefSelection<?>> collection = this.noSelectionRefCache.get(tableWrapper.getAlias());
            if(CollectionUtils.isEmpty(collection)){
                continue;
            }

            for (RefSelection<?> refSelection : collection) {
                refSelection.selection();
            }
        }
    }*/


}
