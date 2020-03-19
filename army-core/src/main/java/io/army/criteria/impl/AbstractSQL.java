package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.*;

abstract class AbstractSQL extends AbstractSQLDebug implements QueryAble {

    private List<TableWrapper> tableWrapperList = new ArrayList<>(tableWrapperCount());

    /*################################## blow cache props ##################################*/

    private Map<TableMeta<?>, Integer> tableRefCountCache = new HashMap<>(tableWrapperCount() + 3);



    /*################################## blow InnerSQL method ##################################*/

    @Override
    public final List<TableWrapper> tableWrapperList() {
        Assert.state(prepared(), "sql no prepared.");
        return this.tableWrapperList;
    }




    /*################################## blow package method ##################################*/

    final void asSQL() {
        this.tableWrapperList = Collections.unmodifiableList(this.tableWrapperList);
    }

    final int tableWrapperListSize() {
        return this.tableWrapperList.size();
    }

    final List<TableWrapper> immutableTableWrapperList() {
        if (prepared()) {
            return this.tableWrapperList;
        }
        return Collections.unmodifiableList(this.tableWrapperList);
    }

    /**
     * @see #addTableAble(TableAble, String, JoinType)
     */
    final void processSelectFieldMeta(FieldMeta<?, ?> fieldMeta
            , Map<TableMeta<?>, List<Selection>> tableFieldListMap) {

        int refCount = this.tableRefCountCache.getOrDefault(fieldMeta.tableMeta(), 0);

        switch (refCount) {
            case 0:
                String msg = "not found the table of FieldMeta[%s] from criteria context,please check from clause.";
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, msg, fieldMeta);
            case 1:
                List<Selection> fieldMetaList = tableFieldListMap.computeIfAbsent(fieldMeta.tableMeta()
                        , key -> new ArrayList<>());
                fieldMetaList.add(fieldMeta);
                break;
            default:
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "FieldMeta[%s] ambiguity,please check select clause and from clause.", fieldMeta);
        }
    }


    final void doOn(List<IPredicate> predicateList) {
        Assert.notEmpty(predicateList, "predicateList required");
        Assert.state(!this.tableWrapperList.isEmpty(), "no form/join clause.");

        TableWrapperImpl tableWrapper = (TableWrapperImpl) this.tableWrapperList.get(this.tableWrapperList.size() - 1);

        Assert.state(tableWrapper.onPredicateList.isEmpty()
                , () -> String.format("on clause of table[%s] ended.", tableWrapper.alias()));

        tableWrapper.onPredicateList.addAll(predicateList);
    }


    /**
     *
     */
    final void addTableAble(TableAble tableAble
            , String tableAlias, JoinType joinType) {

        if (joinType == JoinType.NONE) {
            Assert.state(this.tableWrapperList.isEmpty(), "from clause ended.");
        } else {
            Assert.state(!this.tableWrapperList.isEmpty(), "no from clause.");
        }

        if (tableAble instanceof TableMeta) {
            int refCount = tableRefCountCache.getOrDefault(tableAble, 0);
            tableRefCountCache.put((TableMeta<?>) tableAble, ++refCount);

            onAddTable((TableMeta<?>) tableAble, tableAlias);
        } else if (tableAble instanceof OuterQueryAble) {
            ((OuterQueryAble) tableAble).outerQuery(this);

            onAddSubQuery((SubQuery) tableAble, tableAlias);
        } else {
            throw new IllegalArgumentException(String.format("tableAble[%s] isn't TableMeta or SubQuery.", tableAlias));
        }

        this.tableWrapperList.add(new TableWrapperImpl(tableAble, tableAlias, joinType));
    }

    final void beforeClear(String msg) {
        Assert.state(prepared(), msg);

        this.tableWrapperList = null;
        this.tableRefCountCache.clear();
        this.tableRefCountCache = null;
    }

    /*################################## blow package template method ##################################*/

    abstract boolean prepared();

    /**
     * @see #tableWrapperList
     */
    abstract int tableWrapperCount();

    abstract void onAddTable(TableMeta<?> table, String tableAlias);

    abstract void onAddSubQuery(SubQuery subQuery, String subQueryAlias);


    /*################################## blow static inner class ##################################*/

    static final class TableWrapperImpl implements TableWrapper {

        private final TableAble tableAble;

        private final String alias;

        private final JoinType jointType;

        final List<IPredicate> onPredicateList = new ArrayList<>();

        TableWrapperImpl(TableAble tableAble, String alias, JoinType jointType) {
            this.tableAble = tableAble;
            this.alias = alias;
            this.jointType = jointType;
        }

        public TableAble tableAble() {
            return tableAble;
        }

        public String alias() {
            return alias;
        }

        public JoinType jointType() {
            return jointType;
        }

        public List<IPredicate> onPredicateList() {
            return onPredicateList;
        }
    }

    static final class CriteriaContextImpl<C> implements CriteriaContext {

        private final C criteria;

        /*################################## blow cache prop ##################################*/

        private Map<String, SubQuery> subQueryMap = new HashMap<>();

        private Map<String, AliasFieldExp<?, ?>> aliasTableFieldCache = new HashMap<>();

        private Map<String, RefSelection<?>> refSelectionCache = new HashMap<>();

        private Map<String, Set<RefSelection<?>>> onceChangeRefCache = new HashMap<>();

        CriteriaContextImpl(C criteria) {
            this.criteria = criteria;
        }

        /*################################## blow CriteriaContext method ##################################*/

        @SuppressWarnings("unchecked")
        @Override
        public final <T extends IDomain, F> AliasFieldExp<T, F> aliasField(
                String tableAlias, FieldMeta<T, F> fieldMeta) {
            AliasFieldExp<T, F> aliasField = (AliasFieldExp<T, F>) aliasTableFieldCache.computeIfAbsent(
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


}
