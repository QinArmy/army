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

    static CriteriaContext queryContext(final @Nullable Object criteria) {

        return new SimpleQueryContext(criteria);
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

    private static CriteriaException noFoundWithCte(String name) {
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


    private static abstract class AbstractContext implements CriteriaContext {

        final Object criteria;

        Map<String, VarExpression> varMap;

        private boolean withClauseRecursive;

        private Map<String, SQLs.CteImpl> withClauseCteMap;

        private boolean withClauseCteFinish;

        private Map<String, RefCte> refCteMap;

        private boolean refCteFinish;


        private AbstractContext(@Nullable Object criteria) {
            this.criteria = criteria;
        }

        private AbstractContext(AbstractContext leftContext) {
            this.criteria = leftContext.criteria;
            this.varMap = leftContext.varMap;
        }

        @Override
        public final CteItem refCte(final String cteName) {
            final Map<String, SQLs.CteImpl> withClauseCteMap = this.withClauseCteMap;
            if ((this.withClauseCteFinish && withClauseCteMap == null)
                    || (this.refCteFinish && this.refCteMap == null)) {
                throw _Exceptions.castCriteriaApi();
            }

            CteItem cteItem = null;
            if (withClauseCteMap != null) {
                cteItem = withClauseCteMap.get(cteName);
            }
            if (cteItem == null) {
                Map<String, RefCte> refCteMap = this.refCteMap;
                if (refCteMap == null) {
                    refCteMap = new HashMap<>();
                    this.refCteMap = refCteMap;
                    this.refCteFinish = false;
                }
                cteItem = refCteMap.get(cteName);
                if (cteItem == null) {
                    final RefCte refCte;
                    refCte = new RefCte(cteName);
                    this.refCteFinish = false;
                    refCteMap.put(cteName, refCte);
                    cteItem = refCte;
                }
            }
            return cteItem;
        }

        @Override
        public final boolean cteList(final boolean recursive, final List<Cte> cteList, final boolean subStatement) {
            if (this.withClauseCteMap != null) {
                throw _Exceptions.castCriteriaApi();
            }
            final int cteListSize = cteList.size();
            final Map<String, SQLs.CteImpl> cteMap = new HashMap<>((int) (cteListSize / 0.75F));
            SQLs.CteImpl cteImpl;
            for (Cte cte : cteList) {
                cteImpl = (SQLs.CteImpl) cte;
                if (cteMap.putIfAbsent(cteImpl.name, cteImpl) != null) {
                    String m = String.format("%s %s duplication.", Cte.class.getName(), cteImpl.name);
                    throw new CriteriaException(m);
                }
            }
            final Function<String, Cte> function = cteMap::get;
            CriteriaContext context;
            int finishCount = 0;
            for (SQLs.CteImpl cte : cteMap.values()) {
                context = ((CriteriaContextSpec) cte.subStatement).getCriteriaContext();
                if (context.finishCteRefs(recursive, cte.name, function, subStatement)) {
                    finishCount++;
                }
            }
            this.withClauseRecursive = recursive;
            this.withClauseCteMap = Collections.unmodifiableMap(cteMap);
            final boolean cteFinish = finishCount == cteListSize;
            this.withClauseCteFinish = cteFinish;
            return cteFinish;
        }

        @Override
        public final boolean finishCteRefs(final boolean recursive, final String thisCteName
                , final Function<String, Cte> function, final boolean subStatement) {
            if (this.withClauseCteFinish && this.refCteFinish) {
                return true;
            }
            final Map<String, SQLs.CteImpl> withClauseCteMap = this.withClauseCteMap;
            if (withClauseCteMap != null && !this.withClauseCteFinish) {
                final boolean withClauseRecursive = this.withClauseRecursive;
                boolean finish;
                int finishCount = 0;
                for (SQLs.CteImpl cte : withClauseCteMap.values()) {
                    finish = ((CriteriaContextSpec) cte.subStatement).getCriteriaContext()
                            .finishCteRefs(withClauseRecursive, cte.name, function, subStatement);
                    if (finish) {
                        finishCount++;
                    } else if (!subStatement) {
                        throw noFoundWithCte(cte.name);
                    }
                }
                if (finishCount == withClauseCteMap.size()) {
                    this.withClauseCteMap = null;
                    this.withClauseCteFinish = true;
                }
            }

            final Map<String, RefCte> refCteMap = this.refCteMap;
            if (refCteMap == null) {
                return this.withClauseCteFinish;
            }
            int finishCount = 0;
            Cte cte;
            for (RefCte refCte : refCteMap.values()) {
                if (refCte.name.equals(thisCteName)) {
                    if (!this.withClauseRecursive) {
                        throw nonRecursiveWithClause(refCte.name);
                    } else if (!(this instanceof UnionQueryContext)) {
                        throw nonUnionRecursiveWithClause(refCte.name);
                    }
                }
                if (refCte.actualCte != null) {
                    finishCount++;
                } else if ((cte = function.apply(refCte.name)) != null) {
                    refCte.actualCte = cte;
                    finishCount++;
                } else if (!subStatement) {
                    throw notFoundCte(refCte.name);
                }
            }
            if (finishCount == refCteMap.size()) {
                refCteMap.clear();
                this.refCteMap = null;
                this.refCteFinish = true;
            }
            return this.withClauseCteFinish && this.refCteFinish;
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
            if (this.withClauseCteFinish) {
                this.withClauseCteMap = null;
            }
            if (this.refCteFinish) {
                this.refCteMap = null;
            }
            return Collections.emptyList();
        }

    }//AbstractContext

    private static abstract class JoinableContext extends AbstractContext {

        private List<_TableBlock> tableBlockList = new ArrayList<>();

        Map<String, _TableBlock> aliasToBlock = new HashMap<>();

        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToField;

        Map<String, Map<String, RefSelection>> aliasToRefSelection;

        Map<String, Map<String, DerivedField>> aliasToSelection;

        Deque<_LeftBracketBlock> bracketStack;


        private JoinableContext(@Nullable Object criteria) {
            super(criteria);
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
            super.clear();
            final List<_TableBlock> tableBlockList = this.tableBlockList;
            if (tableBlockList == null) {
                throw new IllegalStateException("duplication clear");
            }
            this.tableBlockList = null;
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
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

            final Deque<_LeftBracketBlock> bracketStack = this.bracketStack;
            if (bracketStack != null && bracketStack.size() > 0) {
                String m = String.format("%s bracket not close in join expression clause.", bracketStack.size());
                throw new CriteriaException(m);
            }
            this.bracketStack = null;
            return _CollectionUtils.unmodifiableList(tableBlockList);
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


    }


    private static abstract class NonJoinableContext extends AbstractContext {

        private NonJoinableContext(@Nullable Object criteria) {
            super(criteria);
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
        public final void onAddNoOnBlock(_TableBlock block) {
            throw nonJoinable("onFirstBlock");
        }

        @Override
        public final void onBracketBlock(_TableBlock block) {
            throw nonJoinable("onBracketBlock");
        }

        @Override
        public final void onJoinType(_JoinType joinType) {
            throw nonJoinable("onJoinType");
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

        private InsertContext(@Nullable Object criteria) {
            super(criteria);
        }

    }// InsertContext


    /**
     * @see #singleDmlContext(Object)
     */
    private static final class SingleDmlContext extends NonJoinableContext {

        private SingleDmlContext(@Nullable Object criteria) {
            super(criteria);
        }


    }//SingleDmlContext

    private static final class MultiDmlContext extends JoinableContext {

        private MultiDmlContext(@Nullable Object criteria) {
            super(criteria);
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


        private SimpleQueryContext(@Nullable Object criteria) {
            super(criteria);
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

    private static final class UnionQueryContext extends AbstractContext {

        private final List<? extends SelectItem> selectItemList;

        private Map<String, SelectionExpression> aliasToSelection;

        private UnionQueryContext(AbstractContext leftContext, List<? extends SelectItem> selectItemList) {
            super(leftContext);
            this.selectItemList = selectItemList;
        }

        @Override
        public boolean containsTable(String tableAlias) {
            throw nonJoinable("containsTable");
        }

        @Override
        public void selectList(List<? extends SelectItem> selectPartList) {
            throw nonJoinable("selectList");
        }

        @Override
        public void onAddBlock(_TableBlock block) {
            throw nonJoinable("onAddBlock");
        }

        @Override
        public void onAddNoOnBlock(_TableBlock block) {
            throw nonJoinable("onFirstBlock");
        }

        @Override
        public _TableBlock lastTableBlockWithoutOnClause() {
            throw nonJoinable("firstBlock");
        }

        @Override
        public void onBracketBlock(_TableBlock block) {
            throw nonJoinable("onBracketBlock");
        }

        @Override
        public void onJoinType(_JoinType joinType) {
            throw nonJoinable("onJoinType");
        }

        @Override
        public <T extends IDomain> QualifiedField<T> qualifiedField(String tableAlias, FieldMeta<T> field) {
            throw nonJoinable("field");
        }

        @Override
        public DerivedField ref(String subQueryAlias, String derivedFieldName) {
            throw nonJoinable("ref(derivedTable,derivedFieldName)");
        }

        @Override
        public Expression ref(final String selectionAlias) {
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
        public List<_TableBlock> clear() {
            return Collections.emptyList();
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


}
