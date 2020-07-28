package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.dialect.SQL;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.CollectionUtils;

import java.util.*;

final class CriteriaContextImpl<C> implements CriteriaContext {

    private final C criteria;

    /*################################## blow cache prop ##################################*/

    private Map<String, Selection> composeSelectMap;

    private Map<String, Expression<?>> composeRefSelectionMap;

    private Map<String, SubQuery> subQueryMap = new HashMap<>();

    private Map<String, TableMeta<?>> tableMetaMap = new HashMap<>();

    private Map<String, LogicalField<?, ?>> aliasTableFieldCache = new HashMap<>();

    private Map<String, RefSelection<?>> refSelectionCache = new HashMap<>();

    private Map<String, Set<RefSelection<?>>> onceChangeRefCache = new HashMap<>();

    private boolean clearFinished;

    CriteriaContextImpl(C criteria) {
        this.criteria = criteria;
        this.composeSelectMap = Collections.emptyMap();
        this.composeRefSelectionMap = Collections.emptyMap();
    }

    CriteriaContextImpl(C criteria, Map<String, Selection> composeSelectMap) {
        this.criteria = criteria;
        this.composeSelectMap = composeSelectMap;
        this.composeRefSelectionMap = new HashMap<>();
    }

    /*################################## blow CriteriaContext method ##################################*/

    @SuppressWarnings("unchecked")
    @Override
    public final <T extends IDomain, F> LogicalField<T, F> aliasField(
            String tableAlias, FieldMeta<T, F> fieldMeta) {
        LogicalField<T, F> aliasField = (LogicalField<T, F>) aliasTableFieldCache.computeIfAbsent(
                tableAlias + fieldMeta.fieldName()
                , k -> new LogicalFieldExpImpl<>(tableAlias, fieldMeta)
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
        if (this.subQueryMap.putIfAbsent(subQueryAlias, subQuery) != null) {
            throw new CriteriaException(ErrorCode.TABLE_ALIAS_DUPLICATION
                    , "SubQuery alias[%s] duplication.", subQueryAlias);
        }
        handleOnceChangeRefSelection(subQuery, subQueryAlias);
    }

    @Override
    public void onAddTable(TableMeta<?> tableMeta, String tableAlias) {
        if (this.tableMetaMap.putIfAbsent(tableAlias, tableMeta) != null) {
            throw new CriteriaException(ErrorCode.TABLE_ALIAS_DUPLICATION
                    , "Table alias[%s] duplication.", tableAlias);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Expression<E> composeRef(String selectionAlias) {
        Selection selection = this.composeSelectMap.get(selectionAlias);
        if (selection == null) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "not found compose selection[%s]", selectionAlias);
        }
        return (Expression<E>) this.composeRefSelectionMap.computeIfAbsent(
                selectionAlias, key -> new ComposeRefSelection<>(selection));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final C criteria() {
        return this.criteria;
    }

    @Override
    public void clear() {
        if (this.clearFinished) {
            return;
        }
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
        this.composeSelectMap = null;

        this.composeRefSelectionMap = null;
        this.tableMetaMap = null;
        this.clearFinished = true;
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
            refSelection = RefSelectionImpl.buildImmutable(subQueryAlias, derivedFieldName
                    , targetSelection.mappingMeta());
        }
        // 3. cache refSelection
        this.refSelectionCache.putIfAbsent(subQueryAlias + derivedFieldName, refSelection);
        return refSelection;
    }

    private void handleOnceChangeRefSelection(SubQuery subQuery, String subQueryAlias) {
        Set<RefSelection<?>> refSet = this.onceChangeRefCache.get(subQueryAlias);
        if (CollectionUtils.isEmpty(refSet)) {
            return;
        }
        for (RefSelection<?> refSelection : refSet) {
            refSelection.selection(subQueryAlias, subQuery.selection(refSelection.derivedFieldName()));
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


    private static final class ComposeRefSelection<E> extends AbstractExpression<E> {

        private final Selection selection;

        private ComposeRefSelection(Selection selection) {
            this.selection = selection;
        }

        @Override
        public Selection as(String alias) {
            return new DefaultSelection(this, this.selection.alias());
        }

        @Override
        protected void appendSQL(SQLContext context) {
            SQL sql = context.dql();
            context.sqlBuilder()
                    .append(sql.quoteIfNeed(this.selection.alias()));

        }

        @Override
        protected String toString() {
            return this.selection.alias();
        }

        @Override
        public MappingMeta mappingMeta() {
            return this.selection.mappingMeta();
        }
    }

}
