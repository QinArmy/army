package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
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
 * This class is utils class for creating {@link CriteriaContext}
 * </p>
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

    static CriteriaContext primaryQueryContext(final @Nullable Object criteria) {
        return new SimpleQueryContext(null, criteria);
    }

    static CriteriaContext primaryQueryContextFrom(final Query query) {
        final AbstractContext leftContext;
        leftContext = (AbstractContext) ((CriteriaContextSpec) query).getCriteriaContext();

        final SimpleQueryContext context;
        context = new SimpleQueryContext(null, ((CriteriaSpec<?>) query).getCriteria());
        ((AbstractContext) context).varMap = leftContext.varMap;
        return context;
    }

    static CriteriaContext subQueryContext(final @Nullable Object criteria) {
        return new SimpleQueryContext(CriteriaContextStack.peek(), criteria);
    }

    static CriteriaContext subQueryContextFrom(final Query query) {
        final AbstractContext leftContext;
        leftContext = (AbstractContext) ((CriteriaContextSpec) query).getCriteriaContext();

        final SimpleQueryContext context;
        context = new SimpleQueryContext(CriteriaContextStack.peek(), ((CriteriaSpec<?>) query).getCriteria());
        ((AbstractContext) context).varMap = leftContext.varMap;
        return context;
    }



    static CriteriaContext bracketContext(final Query left) {
        final AbstractContext leftContext;
        leftContext = (AbstractContext) ((CriteriaContextSpec) left).getCriteriaContext();
        final List<? extends SelectItem> selectItemList = ((_Query) left).selectItemList();
        final CriteriaContext outerContext;
        if (left instanceof SubStatement) {
            outerContext = CriteriaContextStack.peek();
        } else {
            outerContext = null;
        }
        final BracketQueryContext context;
        context = new BracketQueryContext(outerContext, leftContext, selectItemList);
        ((AbstractContext) context).varMap = leftContext.varMap;
        return context;
    }

    static CriteriaContext unionContext(final Query left, final RowSet right) {
        final AbstractContext leftContext;
        leftContext = (AbstractContext) ((CriteriaContextSpec) left).getCriteriaContext();
        final List<? extends SelectItem> selectItemList = ((_Query) left).selectItemList();
        final CriteriaContext outerContext;
        if (left instanceof SubStatement) {
            outerContext = CriteriaContextStack.peek();
        } else {
            outerContext = null;
        }
        final UnionQueryContext context;
        context = new UnionQueryContext(outerContext, leftContext
                , ((CriteriaContextSpec) right).getCriteriaContext(), selectItemList);
        ((AbstractContext) context).varMap = leftContext.varMap;
        return context;
    }


    static CriteriaContext primaryInsertContext(@Nullable Object criteria) {
        return new InsertContext(null, criteria);
    }


    static CriteriaContext primaryMultiDmlContext(@Nullable Object criteria) {
        return new MultiDmlContext(null, criteria);
    }

    static CriteriaContext primarySingleDmlContext(@Nullable Object criteria) {
        return new SingleDmlContext(null, criteria);
    }


    /**
     * @see OperationExpression#as(String)
     */
    static Selection createDerivedSelection(final DerivedField field, final String alias) {
        final _Selection selection;
        if (field instanceof RefDerivedField) {
            final RefDerivedField ref = (RefDerivedField) field;
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


    private static CriteriaException invalidRef(String subQueryAlias, String fieldName) {
        String m = String.format("ref of %s.%s is invalid.", subQueryAlias, fieldName);
        return new CriteriaException(m);
    }

    private static CriteriaException nonJoinable(String operation) {
        String m = String.format("Non-joinable context don't support %s operation", operation);
        return new CriteriaException(m);
    }

    private static CriteriaException notFoundCte(String name) {
        String m = String.format("Not found the %s[%s]", Cte.class.getName(), name);
        return new CriteriaException(m);
    }

    private static CriteriaException nestedItemsAliasHasText(String alias) {
        String m = String.format("%s alias[%s] must be empty.", NestedItems.class.getName(), alias);
        return new CriteriaException(m);
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

    private static CriteriaException dontSupportDerivedGroup(String alias) {
        String m = String.format("current context don't support creating %s for alias[%s]."
                , DerivedGroup.class.getName(), alias);
        throw new CriteriaException(m);
    }

    private static CriteriaException notFoundDerivedGroup(List<DerivedGroup> groupList) {
        final StringBuilder builder = new StringBuilder()
                .append("Not found ")
                .append(DerivedGroup.class.getName())
                .append('[');
        int count = 0;
        for (DerivedGroup group : groupList) {
            if (count > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            builder.append(group.tableAlias());
            count++;
        }
        builder.append(']');
        return new CriteriaException(builder.toString());
    }

    private static CriteriaException unknownSelection(String selectionAlias) {
        String m = String.format("unknown %s[%s]", Selection.class.getName(), selectionAlias);
        return new CriteriaException(m);
    }

    private static CriteriaException notFoundDerivedField(Map<String, Map<String, RefDerivedField>> aliasToRefSelection) {
        final StringBuilder builder = new StringBuilder()
                .append("Not found derived field[");
        int count = 0;
        String alias;
        for (Map.Entry<String, Map<String, RefDerivedField>> e : aliasToRefSelection.entrySet()) {

            alias = e.getKey();
            for (RefDerivedField s : e.getValue().values()) {
                if (count > 0) {
                    builder.append(Constant.SPACE_COMMA);
                }
                builder.append(alias)
                        .append(Constant.POINT)
                        .append(s.fieldName);
                count++;
            }
        }
        builder.append(']');
        return new CriteriaException(builder.toString());
    }

    private static CriteriaException notFoundDerivedField(String derivedTable, String derivedFieldName) {
        String m = String.format("Not found derived field[%s.%s]", derivedTable, derivedFieldName);
        return new CriteriaException(m);
    }

    /**
     * @return a unmodified map
     */
    static Map<String, Selection> createSelection(final List<? extends SelectItem> selectItemList) {
        final Map<String, Selection> selectionMap = new HashMap<>();
        //if alias duplication then override
        for (SelectItem item : selectItemList) {
            if (item instanceof Selection) {
                selectionMap.put(((Selection) item).alias(), (Selection) item);
                continue;
            } else if (!(item instanceof SelectionGroup)) {
                throw _Exceptions.unknownSelectItem(item);
            }

            for (Selection selection : ((SelectionGroup) item).selectionList()) {
                selectionMap.put(selection.alias(), selection);
            }
        }
        return _CollectionUtils.unmodifiableMap(selectionMap);
    }


    private static abstract class AbstractContext implements CriteriaContext {

        final Object criteria;


        private Map<String, VarExpression> varMap;

        final CriteriaContext outerContext;

        private boolean recursive;

        private Map<String, SQLs.CteImpl> withClauseCteMap;

        private Map<String, RefCte> refCteMap;

        private boolean withClauseEnd;


        private AbstractContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            this.outerContext = outerContext;
            this.criteria = criteria;
        }

        @Override
        public void onAddDerivedGroup(DerivedGroup group) {
            throw new UnsupportedOperationException("current context don't support onAddDerivedGroup(group)");
        }

        @Override
        public void selectList(List<? extends SelectItem> selectItemList) {
            throw new UnsupportedOperationException("current context don't support selectList(selectItemList)");
        }

        @Override
        public boolean isExistWindow(String windowName) {
            throw new CriteriaException("current context don't support isExistWindow(windowName)");
        }

        @Override
        public boolean containTableAlias(String tableAlias) {
            throw new CriteriaException("current context don't support containTableAlias(tableAlias)");
        }

        @Override
        public DerivedField ref(String derivedTable, String derivedFieldName) {
            throw new CriteriaException("current context don't support ref(derivedTable,derivedFieldName)");
        }

        @Override
        public DerivedField outerRef(String derivedTable, String derivedFieldName) {
            throw new CriteriaException("current context don't support lateralRef(derivedTable,derivedFieldName)");
        }

        @Override
        public Expression ref(String selectionAlias) {
            throw new CriteriaException("current context don't support ref(selectionAlias)");
        }

        @Override
        public void onAddBlock(_TableBlock block) {
            throw new CriteriaException("current context don't support onAddBlock(block)");
        }

        @Override
        public _TableBlock lastTableBlockWithoutOnClause() {
            throw new CriteriaException("current context don't support lastTableBlockWithoutOnClause()");
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
            if ((refCte = refCteMap.remove(cteImpl.name)) != null && refCte.actualCte == null) {//refCte.actualCte != null,actualCte from outer context
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
                for (RefCte refCte : refCteMap.values()) {
                    if (refCte.actualCte != null) {
                        continue;
                    }
                    if (outerContext == null) {
                        throw notFoundCte(refCte.name);
                    }
                    refCte.actualCte = (Cte) outerContext.refCte(refCte.name);
                }
                refCteMap.clear();
            }
            this.refCteMap = null;
            this.withClauseCteMap = _CollectionUtils.unmodifiableMap(withClauseCteMap);
            this.withClauseEnd = true;
        }


    }//AbstractContext


    private static abstract class JoinableContext extends AbstractContext {


        private List<_TableBlock> tableBlockList = new ArrayList<>();

        private Map<String, _TableBlock> aliasToBlock = new HashMap<>();

        private Map<String, Map<String, RefDerivedField>> aliasToRefSelection;

        private Map<String, Map<String, DerivedField>> aliasToSelection;

        private JoinableContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }


        @Override
        public final void onAddBlock(final _TableBlock block) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                throw _Exceptions.castCriteriaApi();
            }

            final TableItem tableItem = block.tableItem();
            String alias = block.alias();

            if (tableItem instanceof CteItem) {
                if ("".equals(alias)) {
                    alias = ((CteItem) tableItem).name();//modify alias
                } else if (!_StringUtils.hasText(alias)) {
                    throw tableItemAliasNoText(tableItem);
                }
            }
            if (tableItem instanceof NestedItems) {
                if (_StringUtils.hasText(alias)) {
                    throw nestedItemsAliasHasText(alias);
                }
                this.addNestedItems((NestedItems) tableItem);
            } else if (!_StringUtils.hasText(alias)) {
                throw tableItemAliasNoText(tableItem);
            } else if (aliasToBlock.putIfAbsent(alias, block) != null) {
                throw _Exceptions.tableAliasDuplication(alias);
            } else if (tableItem instanceof DerivedTable) {
                this.doOnAddDerived((DerivedTable) tableItem, alias);
            }

            this.tableBlockList.add(block); //add to list

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


        @Override
        public final DerivedField ref(final String derivedTable, final String fieldName) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                throw _Exceptions.castCriteriaApi();
            }
            final _TableBlock block = aliasToBlock.get(derivedTable);
            final TableItem tableItem;
            final DerivedField field;
            if (block == null) {
                field = getRefField(derivedTable, fieldName, true);
                assert field != null;
            } else if (!((tableItem = block.tableItem()) instanceof DerivedTable)) {
                String m = String.format("%s isn't alias of %s ", derivedTable, SubQuery.class.getName());
                throw new CriteriaException(m);
            } else {
                final DerivedField temp;
                temp = getRefField(derivedTable, fieldName, false);
                if (temp == null) {
                    field = getDerivedField((DerivedTable) tableItem, derivedTable, fieldName);
                } else {
                    field = temp;
                }
            }
            return field;
        }


        @Override
        public final List<_TableBlock> clear() {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                throw new IllegalStateException("duplication clear");
            }
            //1. clear super
            super.clear();
            //2. validate aliasToBlock and clear
            final List<_TableBlock> blockList = _CollectionUtils.unmodifiableList(this.tableBlockList);
            this.tableBlockList = blockList;//store for recursive checking
            final int blockSize = blockList.size();
            if (blockSize == 0) {
                throw _Exceptions.castCriteriaApi();
            }
            if (aliasToBlock.size() < blockSize) {// probably NestedItems
                throw new IllegalStateException("block size  match.");
            }
            aliasToBlock.clear();
            this.aliasToBlock = null;


            //3. clear aliasToRefSelection
            final Map<String, Map<String, RefDerivedField>> aliasToRefSelection = this.aliasToRefSelection;
            if (aliasToRefSelection != null && aliasToRefSelection.size() > 0) {
                throw notFoundDerivedField(aliasToRefSelection);
            }
            this.aliasToRefSelection = null;

            //4. clear aliasToSelection
            final Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToSelection;
            if (aliasToSelection != null) {
                aliasToSelection.clear();
            }
            this.aliasToSelection = null;

            //5. clear SimpleQueryContext
            if (this instanceof SimpleQueryContext) {
                final SimpleQueryContext context = (SimpleQueryContext) this;
                context.selectItemList = null;
                context.selectionMap = null;
                final Map<String, RefSelection> refSelectionMap = context.refSelectionMap;
                if (refSelectionMap != null) {
                    refSelectionMap.clear();
                    context.refSelectionMap = null;
                }
                //validate DerivedGroup list
                final List<DerivedGroup> groupList;
                groupList = context.groupList;
                context.groupList = null;
                if (groupList != null && groupList.size() > 0) {
                    throw notFoundDerivedGroup(groupList);
                }

            }
            return blockList;
        }


        private void doOnAddDerived(final DerivedTable derivedTable, final String alias) {
            if (derivedTable instanceof SubQuery && !(derivedTable instanceof _LateralSubQuery)) {
                final CriteriaContext context = ((CriteriaContextSpec) derivedTable).getCriteriaContext();
                if (((SimpleQueryContext) context).refOuterField) {
                    String m = String.format("SubQuery %s isn't lateral,couldn't reference outer field.", alias);
                    throw new CriteriaException(m);
                }
            }
            final Map<String, Map<String, RefDerivedField>> aliasToRefSelection = this.aliasToRefSelection;
            if (aliasToRefSelection != null) {
                final Map<String, RefDerivedField> fieldMap;
                fieldMap = aliasToRefSelection.remove(alias);
                if (fieldMap != null) {
                    this.finishRefSelections(derivedTable, alias, fieldMap);
                    fieldMap.clear();
                }
                if (aliasToRefSelection.size() == 0) {
                    this.aliasToRefSelection = null;
                }
            }

            final List<DerivedGroup> groupList;
            if (this instanceof SimpleQueryContext
                    && (groupList = ((SimpleQueryContext) this).groupList) != null
                    && groupList.size() > 0) {
                final Iterator<DerivedGroup> iterator = groupList.listIterator();
                DerivedGroup group;
                while (iterator.hasNext()) {
                    group = iterator.next();
                    if (alias.equals(group.tableAlias())) {
                        group.finish(derivedTable, alias);
                        iterator.remove();
                    }
                }
            }

        }


        /**
         * @see #doOnAddDerived(DerivedTable, String)
         */
        private void finishRefSelections(DerivedTable derivedTable, String alias, Map<String, RefDerivedField> fieldMap) {
            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToSelection;
            if (aliasToSelection == null) {
                aliasToSelection = new HashMap<>();
                this.aliasToSelection = aliasToSelection;
            }
            final Map<String, DerivedField> derivedFieldMap;
            derivedFieldMap = aliasToSelection.computeIfAbsent(alias, k -> new HashMap<>());
            Selection selection;
            for (RefDerivedField field : fieldMap.values()) {
                selection = derivedTable.selection(field.fieldName);
                if (selection == null) {
                    throw invalidRef(alias, field.fieldName);
                }
                if (field.paramMeta.actual == null) {
                    field.paramMeta.actual = selection.paramMeta();
                    derivedFieldMap.putIfAbsent(field.fieldName, field);
                }
            }

        }

        @Nullable
        private RefDerivedField getRefField(final String subQueryAlias, final String fieldName, final boolean create) {
            Map<String, Map<String, RefDerivedField>> aliasToRefSelection = this.aliasToRefSelection;
            if (aliasToRefSelection == null && create) {
                aliasToRefSelection = new HashMap<>();
                this.aliasToRefSelection = aliasToRefSelection;
            }
            final Map<String, RefDerivedField> fieldMap;
            final RefDerivedField field;
            if (aliasToRefSelection == null) {
                field = null;
            } else if (create) {
                fieldMap = aliasToRefSelection.computeIfAbsent(subQueryAlias, k -> new HashMap<>());
                field = fieldMap.computeIfAbsent(fieldName, k -> new RefDerivedField(subQueryAlias, fieldName));
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

        private DerivedField getDerivedField(final DerivedTable derivedTable, final String tableAlias
                , final String fieldName) {
            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToSelection;
            if (aliasToSelection == null) {
                aliasToSelection = new HashMap<>();
                this.aliasToSelection = aliasToSelection;
            }
            final Map<String, DerivedField> fieldMap;
            fieldMap = aliasToSelection.computeIfAbsent(tableAlias, k -> new HashMap<>());

            final DerivedField field;
            field = fieldMap.computeIfAbsent(fieldName
                    , k -> {
                        final Selection selection;
                        selection = derivedTable.selection(fieldName);
                        if (selection == null) {
                            throw invalidRef(tableAlias, fieldName);
                        }
                        return new DerivedSelection(tableAlias, selection);
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

        /**
         * @see #onAddBlock(_TableBlock)
         */
        private void addNestedItems(final NestedItems nestedItems) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            TableItem tableItem;
            String alias;
            for (_TableBlock block : ((_NestedItems) nestedItems).tableBlockList()) {
                tableItem = block.tableItem();
                alias = block.alias();
                if (tableItem instanceof CteItem) {
                    if ("".equals(alias)) {
                        alias = ((CteItem) tableItem).name();//modify alias
                    } else if (!_StringUtils.hasText(alias)) {
                        throw tableItemAliasNoText(tableItem);
                    }
                }
                if (tableItem instanceof NestedItems) {
                    if (_StringUtils.hasText(alias)) {
                        throw nestedItemsAliasHasText(alias);
                    }
                    this.addNestedItems((NestedItems) tableItem);
                } else if (aliasToBlock.putIfAbsent(alias, block) != null) {
                    throw _Exceptions.tableAliasDuplication(alias);
                } else if (tableItem instanceof DerivedTable) {
                    // note ,no tableBlockList.
                    this.doOnAddDerived((DerivedTable) tableItem, alias);
                }

            }


        }


    }//JoinableContext


    /**
     * @see #primaryInsertContext(Object)
     */
    private static final class InsertContext extends AbstractContext {

        private InsertContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }

    }// InsertContext


    /**
     * @see #primarySingleDmlContext(Object)
     */
    private static final class SingleDmlContext extends AbstractContext {

        private SingleDmlContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }


    }//SingleDmlContext

    private static final class MultiDmlContext extends JoinableContext {

        private MultiDmlContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }


    }// MultiDmlContext


    private static class SimpleQueryContext extends JoinableContext {

        private List<? extends SelectItem> selectItemList;

        private List<DerivedGroup> groupList;

        private Map<String, Selection> selectionMap;

        private Map<String, RefSelection> refSelectionMap;

        private boolean refOuterField;

        private SimpleQueryContext(@Nullable CriteriaContext outerContext, @Nullable Object criteria) {
            super(outerContext, criteria);
        }

        @Override
        public final DerivedField outerRef(String derivedTable, String derivedFieldName) {
            final CriteriaContext outerContext = this.outerContext;
            if (outerContext == null) {
                throw new CriteriaException("No outer context for current context.");
            }
            this.refOuterField = true;
            return outerContext.ref(derivedTable, derivedFieldName);
        }

        @Override
        public final void onAddDerivedGroup(final DerivedGroup group) {
            List<DerivedGroup> groupList = this.groupList;
            if (groupList == null) {
                groupList = new LinkedList<>();
                this.groupList = groupList;
            }
            groupList.add(group);
        }

        @Override
        public final void selectList(List<? extends SelectItem> selectItemList) {
            if (this.selectItemList != null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.selectItemList = selectItemList;
        }

        @Override
        public final Expression ref(final String selectionAlias) {
            Map<String, RefSelection> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                refSelectionMap = new HashMap<>();
                this.refSelectionMap = refSelectionMap;
            }
            return refSelectionMap.computeIfAbsent(selectionAlias, this::createRefSelection);
        }

        private RefSelection createRefSelection(final String selectionAlias) {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                final List<? extends SelectItem> selectItemList = this.selectItemList;
                if (selectItemList == null) {
                    String m = String.format("currently,couldn't reference %s,please check your syntax."
                            , Selection.class.getName());
                    throw new CriteriaException(m);
                }
                selectionMap = createSelection(selectItemList);
                this.selectionMap = selectionMap;
            }
            final Selection selection;
            selection = selectionMap.get(selectionAlias);
            if (selection == null) {
                throw unknownSelection(selectionAlias);
            }
            return new RefSelection(selection);
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

        private Map<String, Selection> selectionMap;

        private Map<String, RefSelection> refSelectionMap;


        private UnionOperationContext(@Nullable CriteriaContext outerContext, CriteriaContext leftContext
                , List<? extends SelectItem> selectItemList) {
            super(outerContext, leftContext.criteria());
            this.leftContext = leftContext;
            this.selectItemList = selectItemList;
        }


        @Override
        public Expression ref(final String selectionAlias) {
            Map<String, RefSelection> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                refSelectionMap = new HashMap<>();
                this.refSelectionMap = refSelectionMap;
            }
            return refSelectionMap.computeIfAbsent(selectionAlias, this::createRefSelection);
        }

        private RefSelection createRefSelection(final String selectionAlias) {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = createSelection(this.selectItemList);
                this.selectionMap = selectionMap;
            }
            final Selection selection;
            selection = selectionMap.get(selectionAlias);
            if (selection == null) {
                throw unknownSelection(selectionAlias);
            }
            return new RefSelection(selection);
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
                match = ((UnionOperationContext) targetContext).isRecursive(cteName);
            }
            return match;
        }


    }//UnionOperationContext


    private static final class BracketQueryContext extends UnionOperationContext {

        private BracketQueryContext(@Nullable CriteriaContext outerContext, CriteriaContext leftContext
                , List<? extends SelectItem> selectItemList) {
            super(outerContext, leftContext, selectItemList);
        }


    }

    private static final class UnionQueryContext extends UnionOperationContext {

        private final CriteriaContext rightContext;

        private UnionQueryContext(@Nullable CriteriaContext outerContext, CriteriaContext leftContext
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


    private static final class RefDerivedField extends OperationExpression implements DerivedField, _Selection {

        final String tableName;

        final String fieldName;

        final DelayParamMeta paramMeta;

        private RefDerivedField(String tableName, String fieldName) {
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
    private static final class RefSelection extends OperationExpression {

        private final Selection selection;

        private RefSelection(Selection selection) {
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
