package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util._ClassUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class CriteriaContexts {

    private CriteriaContexts() {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext primaryQueryContext(final @Nullable Object criteriaOrLeftQuery) {
        final Object criteria;
        if (criteriaOrLeftQuery instanceof RowSet) {
            criteria = ((CriteriaSpec<?>) criteriaOrLeftQuery).getCriteria();
        } else {
            criteria = criteriaOrLeftQuery;
        }
        return new SimpleQueryContext(null, criteria);
    }

    static CriteriaContext subQueryContext(final @Nullable Object criteriaOrLeftQuery) {
        final Object criteria;
        if (criteriaOrLeftQuery instanceof RowSet) {
            criteria = ((CriteriaSpec<?>) criteriaOrLeftQuery).getCriteria();
        } else {
            criteria = criteriaOrLeftQuery;
        }
        return new SimpleQueryContext(CriteriaContextStack.peek(), criteria);
    }


    static CriteriaContext insertContext(@Nullable Object criteria) {
        return new InsertContext(criteria);
    }


    static <C> CriteriaContext multiDmlContext(@Nullable C criteria) {
        return new MultiDmlContext(criteria);
    }

    static CriteriaContext singleDmlContext(@Nullable Object criteria) {
        return new SingleDmlContext(criteria);
    }


    static CriteriaContext unionContext(final RowSet leftQuery) {
        final AbstractContext leftContext = (AbstractContext) ((CriteriaContextSpec) leftQuery).getCriteriaContext();
        final CriteriaContext criteriaContext;
        if (leftQuery instanceof SimpleQuery) {
            criteriaContext = new UnionQueryContext(leftContext, ((_Query) leftQuery).selectItemList());
        } else if (leftQuery instanceof _UnionRowSet) {
            criteriaContext = leftContext;
        } else {
            throw _Exceptions.unknownRowSetType(leftQuery);
        }
        return criteriaContext;
    }

    static CriteriaContext unionAndContext(final Query leftQuery) {
        final AbstractContext leftContext = (AbstractContext) ((CriteriaContextSpec) leftQuery).getCriteriaContext();
        return new SimpleQueryContext(leftContext);
    }

    /**
     * @see OperationExpression#as(String)
     */
    static Selection createDerivedSelection(final DerivedField field, final String alias) {
        final _Selection selection;
        if (field instanceof RefSelection) {
            final RefSelection ref = (RefSelection) field;
            if (ref.fieldName.equals(alias)) {
                selection = ref;
            } else {
                selection = new DerivedAliasSelection(field, alias);
            }
        } else if (field instanceof DerivedSelection) {
            final DerivedSelection ref = (DerivedSelection) field;
            if (ref.selection.alias().equals(alias)) {
                selection = ref;
            } else {
                selection = new DerivedAliasSelection(field, alias);
            }
        } else {
            String m = String.format("unknown %s type[%s]", DerivedField.class.getName(), field.getClass().getName());
            throw new IllegalArgumentException(m);
        }
        return selection;
    }


    private static CriteriaException dontSupportRefSelection() {
        return new CriteriaException("Non union query couldn't reference selection of query.");
    }

    private static CriteriaException invalidRef(String subQueryAlias, String fieldName) {
        String m = String.format("ref of %s.%s is invalid.", subQueryAlias, fieldName);
        return new CriteriaException(m);
    }

    private static CriteriaException nonJoinable(String operation) {
        String m = String.format("Non-joinable context don't support %s operation", operation);
        return new CriteriaException(m);
    }

    private static CriteriaException notFoundWithCte(String name) {
        String m = String.format("Not found the %s[%s] that present in with clause.", Cte.class.getName(), name);
        return new CriteriaException(m);
    }

    private static CriteriaException notFoundCte(String name) {
        String m = String.format("Not found the %s[%s]", Cte.class.getName(), name);
        return new CriteriaException(m);
    }

    private static CriteriaException nestedItemsAliasHasText() {
        return new CriteriaException(String.format("%s alias must be empty.", NestedItems.class.getName()));
    }

    private static CriteriaException tableItemAliasNoText(TableItem tableItem) {
        String m = String.format("%s[%s] alias must be not empty."
                , TableItem.class.getName(), _ClassUtils.safeClassName(tableItem));
        return new CriteriaException(m);
    }

    private static CriteriaException nonRecursiveWithClause(String cteName) {
        String m = String.format("%s[%s] is not recursive,couldn't reference itself."
                , Cte.class.getName(), cteName);
        return new CriteriaException(m);
    }

    private static CriteriaException nonUnionRecursiveWithClause(String cteName) {
        String m = String.format("%s[%s] is not not union,couldn't reference itself."
                , Cte.class.getName(), cteName);
        return new CriteriaException(m);
    }


    private static CriteriaException referenceCteSyntaxError(String cteName) {
        String m = String.format("reference cte[%s] syntax error.", cteName);
        return new CriteriaException(m);
    }


    private static abstract class AbstractContext implements CriteriaContext {

        final Object criteria;


        Map<String, VarExpression> varMap;

        private final CriteriaContext outerContext;

        private boolean recursive;

        private Map<String, SQLs.CteImpl> withClauseCteMap;

        private Map<String, RefCte> refCteMap;

        private boolean withClauseEnd;


        private AbstractContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            this.outerContext = outerContext;
            this.criteria = criteria;
        }


        @Override
        public final CteConsumer onBeforeWithClause(final boolean recursive) {
            if (this.withClauseCteMap != null || this.withClauseEnd) {
                throw _Exceptions.castCriteriaApi();
            }
            this.recursive = recursive;
            this.withClauseCteMap = new HashMap<>();
            return new CteConsumerImpl(this::addCte, this::withClauseEnd);
        }

        @Override
        public final CteItem refCte(final String cteName) {
            final Map<String, SQLs.CteImpl> withClauseCteMap = this.withClauseCteMap;
            final CriteriaContext outerContext = this.outerContext;
            CteItem tempItem;
            final CteItem cteItem;
            if (withClauseCteMap == null) {
                if (outerContext == null) {
                    throw notFoundCte(cteName);
                }
                cteItem = outerContext.refCte(cteName);// get CteItem fro outer context
            } else if ((tempItem = withClauseCteMap.get(cteName)) != null) {
                cteItem = tempItem;
            } else if (this.withClauseEnd) {
                if (outerContext == null) {
                    throw notFoundCte(cteName);
                }
                cteItem = outerContext.refCte(cteName);
            } else {
                Map<String, RefCte> refCteMap = this.refCteMap;
                if (refCteMap == null) {
                    refCteMap = new HashMap<>();
                    this.refCteMap = refCteMap;
                }
                tempItem = refCteMap.get(cteName);
                if (tempItem == null) {
                    final RefCte refCte;
                    refCte = new RefCte(cteName);
                    refCteMap.put(cteName, refCte);
                    tempItem = refCte;
                }
                cteItem = tempItem;
            }
            return cteItem;
        }


        @Override
        public final VarExpression createVar(String name, ParamMeta paramMeta) throws CriteriaException {
            throw new UnsupportedOperationException();
        }

        @Override
        public final VarExpression var(String name) throws CriteriaException {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <C> C criteria() {
            try {
                return (C) this.criteria;
            } catch (ClassCastException e) {
                String m = String.format("Criteria cast failure,please check %s method parameter type."
                        , Function.class.getName());
                throw new CriteriaException(m, e);
            }
        }

        @Override
        public List<_TableBlock> clear() {
            final Map<String, SQLs.CteImpl> withClauseCteMap = this.withClauseCteMap;
            if (withClauseCteMap != null) {
                withClauseCteMap.clear();
                this.withClauseCteMap = null;
            }
            final Map<String, RefCte> refCteMap = this.refCteMap;
            if (refCteMap != null) {
                refCteMap.clear();
                this.refCteMap = null;
            }
            return Collections.emptyList();
        }

        private void addCte(final Cte cte) {
            final Map<String, SQLs.CteImpl> withClauseCteMap = this.withClauseCteMap;
            if (withClauseCteMap == null || this.withClauseEnd) {
                throw _Exceptions.castCriteriaApi();
            }
            if (!(cte instanceof SQLs.CteImpl)) {
                String m = String.format("Illegal implementation of %s", Cte.class.getName());
                throw new CriteriaException(m);
            }
            final SQLs.CteImpl cteImpl = (SQLs.CteImpl) cte;
            if (withClauseCteMap.putIfAbsent(cteImpl.name, cteImpl) != null) {
                String m = String.format("%s %s duplication.", Cte.class.getName(), cteImpl.name);
                throw new CriteriaException(m);
            }
            final Map<String, RefCte> refCteMap = this.refCteMap;
            if (refCteMap == null || refCteMap.size() == 0) {
                return;
            }
            final CriteriaContext outerContext = this.outerContext;
            final RefCte refCte;
            if ((refCte = refCteMap.get(cteImpl.name)) != null && refCte.actualCte == null) {//refCte.actualCte != null,actualCte from outer context
                final CriteriaContext context;
                context = ((CriteriaContextSpec) cteImpl.subStatement).getCriteriaContext();
                if (this.recursive
                        && context instanceof UnionOperationContext
                        && ((UnionOperationContext) context).isRecursive(cteImpl.name)) {
                    refCte.actualCte = cteImpl;
                    refCteMap.remove(cteImpl.name);
                } else {
                    throw referenceCteSyntaxError(cteImpl.name);
                }

            }

            if (refCteMap.size() == 0) {
                return;
            }
            for (RefCte c : refCteMap.values()) {
                if (outerContext == null) {
                    throw notFoundCte(c.name);
                }
                if (c.actualCte != null) {
                    //here,actualCte is from outer context.
                    continue;
                }
                c.actualCte = (Cte) outerContext.refCte(c.name);
            }

        }

        private void withClauseEnd() {
            final Map<String, SQLs.CteImpl> withClauseCteMap = this.withClauseCteMap;
            if (withClauseCteMap == null || this.withClauseEnd) {
                throw _Exceptions.castCriteriaApi();
            }
            final Map<String, RefCte> refCteMap = this.refCteMap;
            if (refCteMap != null && refCteMap.size() > 0) {
                final CriteriaContext outerContext = this.outerContext;
                SQLs.CteImpl cte;
                for (RefCte refCte : refCteMap.values()) {
                    if (refCte.actualCte != null) {
                        continue;
                    }
                    cte = withClauseCteMap.get(refCte.name);
                    if (cte != null) {
                        refCte.actualCte = cte;
                    } else if (outerContext != null) {
                        refCte.actualCte = (Cte) outerContext.refCte(refCte.name);
                    } else {
                        throw notFoundCte(refCte.name);
                    }
                }
            }
            this.withClauseCteMap = _CollectionUtils.unmodifiableMap(withClauseCteMap);
            this.withClauseEnd = true;
        }


    }//AbstractContext

    private static abstract class JoinableContext extends AbstractContext {

        private List<_TableBlock> tableBlockList = new ArrayList<>();

        Map<String, _TableBlock> aliasToBlock = new HashMap<>();

        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToField;

        Map<String, Map<String, RefSelection>> aliasToRefSelection;

        Map<String, Map<String, DerivedField>> aliasToSelection;

        Deque<_LeftBracketBlock> bracketStack;


        private JoinableContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }

        @Override
        public final boolean containsTable(String tableAlias) {
            return this.aliasToBlock.containsKey(tableAlias);
        }

        @Override
        public final void onAddBlock(final _TableBlock block) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            switch (block.jointType()) {
                case NONE: {
                    if (aliasToBlock.size() != 0 || block.predicateList().size() > 0) {
                        throw _Exceptions.castCriteriaApi();
                    }
                }
                break;
                case CROSS_JOIN: {
                    if (aliasToBlock.size() == 0 && block.predicateList().size() > 0) {
                        throw _Exceptions.castCriteriaApi();
                    }
                }
                break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN: {
                    if (aliasToBlock.size() == 0 && block.predicateList().size() == 0) {
                        throw _Exceptions.castCriteriaApi();
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(block.jointType());
            }

            final String alias = block.alias();
            final TableItem tableItem = block.tableItem();
            if (tableItem instanceof NestedItems) {
                if (_StringUtils.hasText(alias)) {
                    throw nestedItemsAliasHasText();
                }
            } else if (!(tableItem instanceof CteItem) && !_StringUtils.hasText(alias)) {
                throw tableItemAliasNoText(tableItem);
            }

            if (aliasToBlock.putIfAbsent(alias, block) != null) {
                throw _Exceptions.tableAliasDuplication(alias);
            }
            this.tableBlockList.add(block);
            this.doOnAddBlock(block);
        }

        @Override
        public final _TableBlock lastTableBlockWithoutOnClause() {
            final List<_TableBlock> tableBlockList = this.tableBlockList;
            if (tableBlockList.size() == 0) {
                throw _Exceptions.castCriteriaApi();
            }
            final _TableBlock block;
            block = tableBlockList.get(tableBlockList.size() - 1);
            if (block instanceof _NoTableBlock) {
                throw _Exceptions.castCriteriaApi();
            }
            switch (block.jointType()) {
                case NONE:
                case CROSS_JOIN:
                    break;
                default:
                    throw _Exceptions.castCriteriaApi();
            }
            return block;
        }

        @Override
        public final boolean containTableAlias(final String tableAlias) {
            final _TableBlock block;
            block = this.aliasToBlock.get(tableAlias);
            return block != null && block.tableItem() instanceof TableMeta;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T extends IDomain> QualifiedField<T> qualifiedField(String tableAlias, FieldMeta<T> field) {
            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToField = this.aliasToField;
            if (aliasToField == null) {
                aliasToField = new HashMap<>();
                this.aliasToField = aliasToField;
            }
            final Map<FieldMeta<?>, QualifiedField<?>> fieldMap;
            fieldMap = aliasToField.computeIfAbsent(tableAlias, k -> new HashMap<>());

            final QualifiedField<?> qualifiedField;
            qualifiedField = fieldMap.computeIfAbsent(field, k -> new QualifiedFieldImpl<>(tableAlias, k));
            return (QualifiedField<T>) qualifiedField;
        }


        @Override
        public DerivedField ref(final String subQueryAlias, final String fieldName) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            final _TableBlock block = aliasToBlock.get(subQueryAlias);
            final TableItem subQuery;
            final DerivedField field;
            if (block == null) {
                field = getField(subQueryAlias, fieldName, true);
                assert field != null;
            } else if (!((subQuery = block.tableItem()) instanceof SubQuery)) {
                //TODO handle MySQL TablePartGroup
                String m = String.format("%s isn't alias of %s ", subQueryAlias, SubQuery.class.getName());
                throw new CriteriaException(m);
            } else {
                final DerivedField temp;
                temp = getField(subQueryAlias, fieldName, false);
                if (temp == null) {
                    field = getSelection((SubQuery) subQuery, subQueryAlias, fieldName);
                } else {
                    field = temp;
                }
            }
            return field;
        }

        @Override
        public final Expression ref(String selectionAlias) {
            throw dontSupportRefSelection();
        }


        @Override
        public final List<_TableBlock> clear() {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                throw new IllegalStateException("duplication clear");
            }
            super.clear();
            final List<_TableBlock> blockList = _CollectionUtils.unmodifiableList(this.tableBlockList);
            this.tableBlockList = blockList;//store for recursive checking
            final int blockSize = blockList.size();
            if (blockSize == 0) {
                throw _Exceptions.castCriteriaApi();
            }
            if (blockSize != aliasToBlock.size()) {
                throw new IllegalStateException("block size not match.");
            }
            this.aliasToBlock = null;
            aliasToBlock.clear();

            final Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToField = this.aliasToField;
            if (aliasToField != null) {
                for (Map<FieldMeta<?>, QualifiedField<?>> map : aliasToField.values()) {
                    map.clear();
                }
                this.aliasToField = null;
                aliasToField.clear();
            }
            this.onClear();

            return blockList;
        }

        abstract void doOnAddBlock(_TableBlock block);

        abstract void onClear();


        @Nullable
        private RefSelection getField(final String subQueryAlias, final String fieldName, final boolean create) {
            Map<String, Map<String, RefSelection>> aliasToRefSelection = this.aliasToRefSelection;
            if (aliasToRefSelection == null) {
                aliasToRefSelection = new HashMap<>();
                this.aliasToRefSelection = aliasToRefSelection;
            }
            final Map<String, RefSelection> fieldMap;
            final RefSelection field;
            if (create) {
                fieldMap = aliasToRefSelection.computeIfAbsent(subQueryAlias, k -> new HashMap<>());
                field = fieldMap.computeIfAbsent(fieldName, k -> new RefSelection(subQueryAlias, fieldName));
            } else {
                fieldMap = aliasToRefSelection.get(subQueryAlias);
                if (fieldMap == null) {
                    field = null;
                } else {
                    field = fieldMap.get(fieldName);
                }
            }
            return field;
        }

        private DerivedField getSelection(final SubQuery subQuery, final String subQueryAlias
                , final String fieldName) {
            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToSelection;
            if (aliasToSelection == null) {
                aliasToSelection = new HashMap<>();
                this.aliasToSelection = aliasToSelection;
            }
            final Map<String, DerivedField> fieldMap;
            fieldMap = aliasToSelection.computeIfAbsent(subQueryAlias, k -> new HashMap<>());

            final DerivedField field;
            field = fieldMap.computeIfAbsent(fieldName
                    , k -> {
                        final Selection selection;
                        selection = subQuery.selection(fieldName);
                        if (selection == null) {
                            throw invalidRef(subQueryAlias, fieldName);
                        }
                        return new DerivedSelection(subQueryAlias, selection);
                    });
            return field;
        }

        private boolean containCte(final String cteName) {
            return this.doContainCte(cteName, this.tableBlockList);
        }

        private boolean doContainCte(final String cteName, final List<? extends _TableBlock> blockList) {
            boolean match = false;
            TableItem item;
            for (_TableBlock block : blockList) {
                item = block.tableItem();
                if (item instanceof CteItem && cteName.equals(((CteItem) item).name())) {
                    match = true;
                    break;
                }
                if (!(item instanceof NestedItems)) {
                    continue;
                }
                if (this.doContainCte(cteName, ((_NestedItems) item).tableBlockList())) {
                    match = true;
                    break;
                }
            }
            return match;
        }

    }//JoinableContext


    private static abstract class NonJoinableContext extends AbstractContext {

        private NonJoinableContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }


        @Override
        public final void selectList(List<? extends SelectItem> selectPartList) {
            throw nonJoinable("selectList");
        }

        @Override
        public final boolean containsTable(String tableAlias) {
            throw nonJoinable("containsTable");
        }

        @Override
        public final <T extends IDomain> QualifiedField<T> qualifiedField(String tableAlias, FieldMeta<T> field) {
            throw nonJoinable("field");
        }

        @Override
        public final DerivedField ref(String subQueryAlias, String derivedFieldName) {
            throw nonJoinable("ref(derivedTable,derivedFieldName)");
        }

        @Override
        public final Expression ref(String selectionAlias) {
            throw nonJoinable("ref(selectionAlias)");
        }

        @Override
        public final void onAddBlock(_TableBlock block) {
            throw nonJoinable("onAddBlock");
        }

        @Override
        public final _TableBlock lastTableBlockWithoutOnClause() {
            throw nonJoinable("firstBlock");
        }

        @Override
        public final List<_TableBlock> clear() {
            return Collections.emptyList();
        }

    }//NonJoinableContext

    /**
     * @see #insertContext(Object)
     */
    private static final class InsertContext extends NonJoinableContext {

        private InsertContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }

    }// InsertContext


    /**
     * @see #singleDmlContext(Object)
     */
    private static final class SingleDmlContext extends NonJoinableContext {

        private SingleDmlContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }


    }//SingleDmlContext

    private static final class MultiDmlContext extends JoinableContext {

        private MultiDmlContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }

        @Override
        public void selectList(List<? extends SelectItem> selectPartList) {
            throw new CriteriaException("Multi-table DML don't support selectList method");
        }

        @Override
        void doOnAddBlock(_TableBlock block) {
            //no-op
        }

        @Override
        void onClear() {
            //no-op
        }

    }// MultiDmlContext


    private static final class SimpleQueryContext extends JoinableContext {


        private List<DerivedGroup> groupList;


        private SimpleQueryContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }

        @Override
        public void selectList(List<? extends SelectItem> selectPartList) {
            if (this.groupList != null || this.aliasToBlock.size() != 0) {
                throw _Exceptions.castCriteriaApi();
            }
            List<DerivedGroup> selectionGroupList = null;
            for (SelectItem selectItem : selectPartList) {
                if (!(selectItem instanceof DerivedGroup)) {
                    continue;
                }
                if (selectionGroupList == null) {
                    selectionGroupList = new LinkedList<>();
                }
                selectionGroupList.add((DerivedGroup) selectItem);
            }
            if (selectionGroupList == null) {
                this.groupList = Collections.emptyList();
            } else {
                this.groupList = selectionGroupList;
            }
        }

        @Override
        void doOnAddBlock(final _TableBlock block) {
            //TODO handle MySQL TablePartGroup
            final TableItem tableItem;
            tableItem = block.tableItem();
            if (!(tableItem instanceof DerivedTable)) {
                return;
            }
            final DerivedTable derivedTable = (DerivedTable) tableItem;
            final String queryAlias = block.alias();

            final Map<String, Map<String, RefSelection>> aliasToDerivedField = this.aliasToRefSelection;
            if (aliasToDerivedField != null) {
                final Map<String, RefSelection> fieldMap;
                fieldMap = aliasToDerivedField.remove(queryAlias);
                if (fieldMap != null) {
                    finishRefSelections(derivedTable, queryAlias, fieldMap);
                }
                if (aliasToDerivedField.size() == 0) {
                    this.aliasToRefSelection = null;
                }
            }

            final List<DerivedGroup> groupList = this.groupList;
            assert groupList != null;
            if (groupList.size() == 0) {
                return;
            }
            final Iterator<DerivedGroup> iterator = groupList.listIterator();
            while (iterator.hasNext()) {
                final DerivedGroup group = iterator.next();
                if (queryAlias.equals(group.tableAlias())) {
                    group.finish(derivedTable, queryAlias);
                    iterator.remove();
                }
            }

        }

        /**
         * @see #doOnAddBlock(_TableBlock)
         */
        private void finishRefSelections(DerivedTable derivedTable, String queryAlias, Map<String, RefSelection> fieldMap) {
            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToSelection;
            if (aliasToSelection == null) {
                aliasToSelection = new HashMap<>();
                this.aliasToSelection = aliasToSelection;
            }
            final Map<String, DerivedField> derivedFieldMap;
            derivedFieldMap = aliasToSelection.computeIfAbsent(queryAlias, k -> new HashMap<>());
            for (RefSelection field : fieldMap.values()) {
                Selection selection;
                selection = derivedTable.selection(field.fieldName);
                if (selection == null) {
                    throw invalidRef(queryAlias, field.fieldName);
                }
                field.paramMeta.actual = selection.paramMeta();
                derivedFieldMap.putIfAbsent(field.fieldName, field);
            }
            fieldMap.clear();
        }

        @Override
        void onClear() {
            final List<DerivedGroup> groupList = this.groupList;
            if (!_CollectionUtils.isEmpty(groupList)) {
                for (DerivedGroup group : groupList) {
                    String m = String.format("DerivedGroup[%s] is invalid.", group.tableAlias());
                    throw new CriteriaException(m);
                }
            }
            final Map<String, Map<String, RefSelection>> aliasToRefSelection = this.aliasToRefSelection;
            if (!_CollectionUtils.isEmpty(aliasToRefSelection)) {
                String m = String.format("Derived tables%s is invalid.", aliasToRefSelection.keySet());
                throw new CriteriaException(m);
            }
        }

    }//SimpleQueryContext


    /**
     * <p>
     * This class is base class of below :
     *     <ul>
     *         <li>{@link BracketQueryContext}</li>
     *         <li>{@link UnionQueryContext}</li>
     *     </ul>
     * </p>
     */
    private static abstract class UnionOperationContext extends AbstractContext {

        private final CriteriaContext leftContext;

        private final List<? extends SelectItem> selectItemList;

        private Map<String, SelectionExpression> aliasToSelection;


        private UnionOperationContext(@Nullable CriteriaContext outerContext, AbstractContext leftContext
                , List<? extends SelectItem> selectItemList) {
            super(outerContext, leftContext);
            this.leftContext = leftContext;
            this.selectItemList = selectItemList;
        }

        @Override
        public final boolean containsTable(String tableAlias) {
            throw nonJoinable("containsTable");
        }

        @Override
        public final void selectList(List<? extends SelectItem> selectPartList) {
            throw nonJoinable("selectList");
        }

        @Override
        public final void onAddBlock(_TableBlock block) {
            throw nonJoinable("onAddBlock");
        }


        @Override
        public final _TableBlock lastTableBlockWithoutOnClause() {
            throw nonJoinable("firstBlock");
        }

        @Override
        public final <T extends IDomain> QualifiedField<T> qualifiedField(String tableAlias, FieldMeta<T> field) {
            throw nonJoinable("field");
        }

        @Override
        public final DerivedField ref(String subQueryAlias, String derivedFieldName) {
            throw nonJoinable("ref(derivedTable,derivedFieldName)");
        }

        @Override
        public final Expression ref(final String selectionAlias) {
            Map<String, SelectionExpression> aliasToSelection = this.aliasToSelection;
            if (aliasToSelection == null) {
                aliasToSelection = new HashMap<>();
                this.aliasToSelection = aliasToSelection;
            }
            final Expression expression;
            expression = aliasToSelection.computeIfAbsent(selectionAlias, this::createSelectionExpression);
            return expression;
        }

        @Override
        public final boolean containTableAlias(String tableAlias) {
            throw nonJoinable("containTableAlias(tableAlias)");
        }


        @Override
        public final boolean isExistWindow(String windowName) {
            throw new UnsupportedOperationException("error operation");
        }

        private SelectionExpression createSelectionExpression(final String selectionAlias) {
            Selection selection = null;
            outFor:
            for (SelectItem selectItem : this.selectItemList) {
                if (selectItem instanceof Selection && selectionAlias.equals(((Selection) selectItem).alias())) {
                    selection = (Selection) selectItem;
                    break;
                } else if (!(selectItem instanceof SelectionGroup)) {
                    throw _Exceptions.unknownSelectItem(selectItem);
                }

                for (Selection s : ((SelectionGroup) selectItem).selectionList()) {
                    if (selectionAlias.equals(s.alias())) {
                        selection = s;
                        break outFor;
                    }
                }

            }

            if (selection == null) {
                String m = String.format("Unknown selection alias[%s]", selectionAlias);
                throw new CriteriaException(m);
            }
            return new SelectionExpression(selection);
        }


        private boolean isRecursive(final String cteName) {
            final boolean match;
            final CriteriaContext targetContext;
            if (this instanceof BracketQueryContext) {
                targetContext = this.leftContext;
            } else if (this instanceof UnionQueryContext) {
                targetContext = ((UnionQueryContext) this).rightContext;
            } else {
                throw new IllegalStateException("Unknown type context");
            }
            if (targetContext instanceof JoinableContext) {
                match = ((JoinableContext) targetContext).containCte(cteName);
            } else {
                match = ((UnionOperationContext) rightContext).isRecursive(cteName);
            }
            return match;
        }


    }//UnionOperationContext


    private static final class BracketQueryContext extends UnionOperationContext {

        private BracketQueryContext(@Nullable CriteriaContext outerContext, AbstractContext leftContext
                , List<? extends SelectItem> selectItemList) {
            super(outerContext, leftContext, selectItemList);
        }


    }

    private static final class UnionQueryContext extends UnionOperationContext {

        private final CriteriaContext rightContext;

        private UnionQueryContext(@Nullable CriteriaContext outerContext, AbstractContext leftContext
                , CriteriaContext rightContext, List<? extends SelectItem> selectItemList) {
            super(outerContext, leftContext, selectItemList);
            this.rightContext = rightContext;
        }

        @Override
        public List<_TableBlock> clear() {
            super.clear();
            return Collections.emptyList();
        }


    }//UnionQueryContext


    private static final class DerivedSelection extends OperationExpression
            implements DerivedField, _Selection {

        private final String tableName;

        private final Selection selection;

        private DerivedSelection(String tableName, Selection selection) {
            this.tableName = tableName;
            this.selection = selection;
        }

        @Override
        public String tableAlias() {
            return this.tableName;
        }

        @Override
        public String alias() {
            return this.selection.alias();
        }

        @Override
        public ParamMeta paramMeta() {
            return this.selection.paramMeta();
        }

        @Override
        public void appendSelection(final _SqlContext context) {
            final _Dialect dialect = context.dialect();

            final String safeFieldName = dialect.quoteIfNeed(this.selection.alias());

            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(Constant.SPACE);

            dialect.quoteIfNeed(this.tableName, builder)
                    .append(Constant.POINT)
                    .append(safeFieldName)
                    .append(Constant.SPACE_AS_SPACE)
                    .append(safeFieldName);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final _Dialect dialect = context.dialect();
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(Constant.SPACE);

            dialect.quoteIfNeed(this.tableName, builder)
                    .append(Constant.POINT);
            dialect.quoteIfNeed(this.selection.alias(), builder);

        }

        @Override
        public int hashCode() {
            return Objects.hash(this.tableName, this.selection);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof DerivedSelection) {
                final DerivedSelection selection = (DerivedSelection) obj;
                match = selection.tableName.equals(this.tableName)
                        && selection.selection.equals(this.selection);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return String.format(" %s.%s", this.tableName, this.selection.alias());
        }

    }//DerivedSelection


    private static final class RefSelection extends OperationExpression implements DerivedField, _Selection {

        final String tableName;

        final String fieldName;

        final DelayParamMeta paramMeta;

        private RefSelection(String tableName, String fieldName) {
            this.tableName = tableName;
            this.fieldName = fieldName;
            this.paramMeta = new DelayParamMeta();
        }

        @Override
        public String tableAlias() {
            return this.tableName;
        }

        @Override
        public String alias() {
            return this.fieldName;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.paramMeta;
        }


        @Override
        public void appendSelection(final _SqlContext context) {
            final _Dialect dialect = context.dialect();

            final String safeFieldName = dialect.quoteIfNeed(this.fieldName);
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(Constant.SPACE);

            dialect.quoteIfNeed(this.tableName, builder)
                    .append(Constant.POINT)
                    .append(safeFieldName)
                    .append(Constant.SPACE_AS_SPACE)
                    .append(safeFieldName);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final _Dialect dialect = context.dialect();
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(Constant.SPACE);

            dialect.quoteIfNeed(this.tableName, builder)
                    .append(Constant.POINT);
            dialect.quoteIfNeed(this.fieldName, builder);
        }


        @Override
        public String toString() {
            return String.format(" %s.%s", this.tableName, this.fieldName);
        }


    }//DerivedFieldImpl


    private static final class DelayParamMeta implements ParamMeta {

        private ParamMeta actual;

        @Override
        public MappingType mappingType() {
            final ParamMeta actual = this.actual;
            if (actual == null) {
                throw new IllegalStateException(String.format("No actual %s", ParamMeta.class.getName()));
            }
            return actual.mappingType();
        }

    }

    /**
     * @see UnionQueryContext#ref(String)
     */
    private static final class SelectionExpression extends OperationExpression {

        private final Selection selection;

        private SelectionExpression(Selection selection) {
            this.selection = selection;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.selection.paramMeta();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(Constant.SPACE);

            context.dialect()
                    .quoteIfNeed(this.selection.alias(), builder);
        }

    }// SelectionExpression


    /**
     * @see #createDerivedSelection(DerivedField, String)
     */
    private static final class DerivedAliasSelection implements _Selection {

        private final DerivedField field;

        private final String alias;

        private DerivedAliasSelection(DerivedField field, String alias) {
            this.field = field;
            this.alias = alias;
        }

        @Override
        public String alias() {
            return this.alias;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.field.paramMeta();
        }

        @Override
        public void appendSelection(final _SqlContext context) {
            ((_SelfDescribed) this.field).appendSql(context);
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(Constant.SPACE_AS_SPACE);

            context.dialect()
                    .quoteIfNeed(this.alias, builder);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.field, this.alias);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof DerivedAliasSelection) {
                final DerivedAliasSelection selection = (DerivedAliasSelection) obj;
                match = selection.field.equals(this.field) && selection.alias.equals(this.alias);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return String.format("%s AS %s", this.field, this.alias);
        }
    }//DerivedAliasSelection


    private static final class RefCte implements Cte {

        private final String name;

        private Cte actualCte;

        private RefCte(String name) {
            this.name = name;
        }

        @Override
        public List<String> columnNameList() {
            final Cte actualCte = this.actualCte;
            if (actualCte == null) {
                throw new IllegalStateException("No actual cte");
            }
            return actualCte.columnNameList();
        }

        @Override
        public SubStatement subStatement() {
            final Cte actualCte = this.actualCte;
            if (actualCte == null) {
                throw new IllegalStateException("No actual cte");
            }
            return actualCte.subStatement();
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public List<? extends SelectItem> selectItemList() {
            final Cte actualCte = this.actualCte;
            if (actualCte == null) {
                throw new IllegalStateException("No actual cte");
            }
            return actualCte.selectItemList();
        }

        @Override
        public Selection selection(String derivedFieldName) {
            final Cte actualCte = this.actualCte;
            if (actualCte == null) {
                throw new IllegalStateException("No actual cte");
            }
            return actualCte.selection(derivedFieldName);
        }

        @Override
        public String toString() {
            return String.format("reference  %s[name:%s,hash:%s]", CteItem.class.getName()
                    , this.name, System.identityHashCode(this));
        }

    }// RefCte

    private static final class CteConsumerImpl implements CriteriaContext.CteConsumer {


        private final Consumer<Cte> cteConsumer;

        private final Runnable endCallback;

        private final List<Cte> cteList = new ArrayList<>();

        private CteConsumerImpl(Consumer<Cte> cteConsumer, Runnable endCallback) {
            this.cteConsumer = cteConsumer;
            this.endCallback = endCallback;
        }

        @Override
        public void addCte(Cte cte) {
            this.cteConsumer.accept(cte);
            this.cteList.add(cte);
        }

        @Override
        public List<Cte> end() {
            this.endCallback.run();
            return _CollectionUtils.unmodifiableList(this.cteList);
        }

    }//CteConsumerImpl


}
