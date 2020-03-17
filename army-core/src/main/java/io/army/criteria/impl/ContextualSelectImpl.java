package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;

import java.util.*;

final class ContextualSelectImpl<C> extends AbstractSelectImpl<C> implements CriteriaContext {

    /*################################## blow cache prop ##################################*/

    private Map<String, SubQuery> subQueryMap = new HashMap<>();

    private Map<String, AliasFieldMeta<?, ?>> aliasTableFieldCache = new HashMap<>();

    private Map<String, RefSelection<?>> refSelectionCache = new HashMap<>();

    private Map<String, Set<RefSelection<?>>> onceChangeRefCache = new HashMap<>();

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

    @Override
    public void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        if (subQueryMap.putIfAbsent(subQueryAlias, subQuery) != subQuery) {
            throw new CriteriaException(ErrorCode.TABLE_ALIAS_DUPLICATION
                    , "SubQuery alias[%s] duplication.", subQueryAlias);
        }
        doOnceChangeRefSelection(subQuery, subQueryAlias);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final C criteria() {
        return this.criteria;
    }

    @Override
    public void clear() {
        super.clear();

        this.subQueryMap.clear();
        this.subQueryMap = null;
        this.aliasTableFieldCache.clear();
        this.aliasTableFieldCache = null;

        this.refSelectionCache.clear();
        this.refSelectionCache = null;
        this.onceChangeRefCache.clear();
        this.onceChangeRefCache = null;

    }

    /*################################## blow protected template method ##################################*/

    @Override
    protected void doTable(TableMeta<?> table, String tableAlias) {

    }

    @Override
    protected void doSubQuery(SubQuery subQuery, String subQueryAlias) {
        onAddSubQuery(subQuery, subQueryAlias);
    }

    protected final void doPrepare() {
        CriteriaContextHolder.clearContext(this);

        if (!this.onceChangeRefCache.isEmpty()) {
            throw new CriteriaException(ErrorCode.REF_EXP_ERROR, createReferenceErrorMsg());
        }
    }


    /*################################## blow private method ##################################*/

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

    private <E> RefSelection<E> createRefSelection(String subQueryAlias, String derivedFieldName
            , @Nullable Class<E> selectionType) {
        // 1. try to get targetSelection
        Selection targetSelection = null;
        SubQuery subQuery = subQueryMap.get(subQueryAlias);
        if (subQuery != null) {
            targetSelection = subQuery.getSelection(derivedFieldName);
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
        Set<RefSelection<?>> set = this.onceChangeRefCache.get(subQueryAlias);
        if (CollectionUtils.isEmpty(set)) {
            return;
        }

        RefSelection<?> ref;
        for (Iterator<RefSelection<?>> iterator = set.iterator(); iterator.hasNext(); ) {
            ref = iterator.next();
            ref.selection(subQuery.getSelection(ref.derivedFieldName()));
            iterator.remove();
        }
        this.onceChangeRefCache.remove(subQueryAlias);
    }


}
