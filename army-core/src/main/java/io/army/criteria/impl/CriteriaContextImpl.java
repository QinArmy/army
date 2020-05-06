package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.util.CollectionUtils;

import java.util.*;

final class CriteriaContextImpl<C> implements CriteriaContext {

    private final C criteria;

    /*################################## blow cache prop ##################################*/

    private Map<String, Selection> composeSelectMap;

    private Map<String, SubQuery> subQueryMap = new HashMap<>();

    private Map<String, AliasField<?, ?>> aliasTableFieldCache = new HashMap<>();

    private Map<String, RefSelection<?>> refSelectionCache = new HashMap<>();

    private Map<String, Set<RefSelection<?>>> onceChangeRefCache = new HashMap<>();

    CriteriaContextImpl(C criteria) {
        this.criteria = criteria;
        this.composeSelectMap = Collections.emptyMap();
    }

    CriteriaContextImpl(C criteria, Map<String, Selection> composeSelectMap) {
        this.criteria = criteria;
        this.composeSelectMap = composeSelectMap;
    }

    /*################################## blow CriteriaContext method ##################################*/

    @SuppressWarnings("unchecked")
    @Override
    public final <T extends IDomain, F> AliasField<T, F> aliasField(
            String tableAlias, FieldMeta<T, F> fieldMeta) {
        AliasField<T, F> aliasField = (AliasField<T, F>) aliasTableFieldCache.computeIfAbsent(
                tableAlias + fieldMeta.fieldName()
                , k -> new AliasFieldExpImpl<>(fieldMeta, tableAlias)
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

    @Override
    public void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        if (subQueryMap.putIfAbsent(subQueryAlias, subQuery) != subQuery) {
            throw new CriteriaException(ErrorCode.TABLE_ALIAS_DUPLICATION
                    , "SubQuery alias[%s] duplication.", subQueryAlias);
        }
        doOnceChangeRefSelection(subQuery, subQueryAlias);
    }

    @Override
    public <E> Expression<E> composeRef(String selectionAlias) {
        @SuppressWarnings("unchecked")
        Expression<E> exp = (Expression<E>) this.composeSelectMap.get(selectionAlias);
        if (exp == null) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "not found compose selection[%s]", selectionAlias);
        }
        return exp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final C criteria() {
        return this.criteria;
    }

    @Override
    public void clear() {
        if (!this.onceChangeRefCache.isEmpty()) {
            throw new CriteriaException(ErrorCode.REF_EXP_ERROR, createReferenceErrorMsg());
        }

        this.subQueryMap.clear();
        this.subQueryMap = null;
        this.aliasTableFieldCache.clear();
        this.aliasTableFieldCache = null;

        this.refSelectionCache.clear();
        this.refSelectionCache = null;
        this.onceChangeRefCache = null;

    }


    private <E> RefSelection<E> createRefSelection(String subQueryAlias, String derivedFieldName
            , @Nullable Class<E> selectionType) {
        // 1. try to get targetSelection
        Selection targetSelection = null;
        SubQuery subQuery = subQueryMap.get(subQueryAlias);
        if (subQuery != null) {
            targetSelection = subQuery.selection(derivedFieldName);
        }
        // 2. create RefSelection
        RefSelection<E> refSelection;
        if (targetSelection == null) {
            refSelection = RefSelectionImpl.buildOnceChange(subQueryAlias, derivedFieldName);
            // 2-1. get refSelectionSet by subQueryAlias
            Set<RefSelection<?>> refSelectionSet = this.onceChangeRefCache.computeIfAbsent(
                    subQueryAlias, key -> new HashSet<>());
            // 2-2. add RefSelection that only change once.
            refSelectionSet.add(refSelection);
        } else {
            refSelection = RefSelectionImpl.buildImmutable(subQueryAlias, targetSelection);
        }
        // 3. cache refSelection
        this.refSelectionCache.putIfAbsent(subQueryAlias + derivedFieldName, refSelection);
        return refSelection;
    }

    private void doOnceChangeRefSelection(SubQuery subQuery, String subQueryAlias) {
        Set<RefSelection<?>> refSet = this.onceChangeRefCache.get(subQueryAlias);
        if (CollectionUtils.isEmpty(refSet)) {
            return;
        }
        for (RefSelection<?> refSelection : refSet) {
            refSelection.selection(subQuery.selection(refSelection.derivedFieldName()));
        }
        refSet.clear();
        this.onceChangeRefCache.remove(subQueryAlias);
    }

    private String createReferenceErrorMsg() {
        StringBuilder builder = new StringBuilder();
        builder.append("Reference Expressions[\n");
        for (Set<RefSelection<?>> refSet : this.onceChangeRefCache.values()) {
            for (Iterator<RefSelection<?>> iterator = refSet.iterator(); iterator.hasNext(); ) {
                RefSelection<?> ref = iterator.next();

                builder.append(ref);
                if (iterator.hasNext()) {
                    builder.append("\n");
                }
            }

        }
        builder.append("] not found from select query.");
        return builder.toString();
    }

}
