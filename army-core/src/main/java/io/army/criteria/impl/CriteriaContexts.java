package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.dialect.VarExpression;
import io.army.criteria.impl.inner.*;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
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


    @Deprecated
    static CriteriaContext primaryQuery(final @Nullable _Statement._WithClauseSpec spec,
                                        final @Nullable CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext primaryQuery(final @Nullable _Statement._WithClauseSpec spec,
                                        final @Nullable CriteriaContext outerContext,
                                        final @Nullable CriteriaContext leftContext) {
        final StatementContext context;
        context = new SelectContext(outerContext, leftContext);
        if (spec != null) {
            final WithClauseContext withClauseContext;
            withClauseContext = ((StatementContext) ((CriteriaContextSpec) spec).getContext()).withClauseContext;
            assert withClauseContext != null;
            context.withClauseContext = withClauseContext;
        }
        return context;
    }

    static void setAliasEventFunction(CriteriaContext queryContext, Function<TypeInfer, ? extends Item> function) {
        assert queryContext instanceof SimpleQueryContext;
        assert ((JoinableContext) queryContext).function == SQLs._IDENTITY;
        ((JoinableContext) queryContext).function = function;
    }

    @Deprecated

    static CriteriaContext subQueryContext(final @Nullable _Statement._WithClauseSpec spec,
                                           final CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext subQueryContext(final @Nullable _Statement._WithClauseSpec spec,
                                           final CriteriaContext outerContext,
                                           final @Nullable CriteriaContext leftContext) {
        final StatementContext context;
        context = new SubQueryContext(outerContext, leftContext);
        if (spec != null) {
            final WithClauseContext withClauseContext;
            withClauseContext = ((StatementContext) ((CriteriaContextSpec) spec).getContext()).withClauseContext;
            assert withClauseContext != null;
            context.withClauseContext = withClauseContext;
        }
        return context;
    }


    @Deprecated
    static CriteriaContext bracketContext(@Nullable final CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static CriteriaContext bracketContext(@Nullable _Statement._WithClauseSpec withSpec
            , @Nullable final CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext bracketContext(final @Nullable _Statement._WithClauseSpec spec,
                                          final @Nullable CriteriaContext outerContext,
                                          final @Nullable CriteriaContext leftContext) {
        final StatementContext context;
        context = new BracketQueryContext(outerContext, leftContext);
        if (spec != null) {
            final WithClauseContext withClauseContext;
            withClauseContext = ((StatementContext) ((CriteriaContextSpec) spec).getContext()).withClauseContext;
            assert withClauseContext != null;
            context.withClauseContext = withClauseContext;
        }
        return context;
    }

    @Deprecated
    static CriteriaContext withClauseContext(final @Nullable CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static CriteriaContext bracketContext(@Nullable _Statement._WithClauseSpec withSpec) {
        throw new UnsupportedOperationException();
    }


    static CriteriaContext primaryInsertContext() {
        return new InsertContext();
    }

    static CriteriaContext cteInsertContext(CriteriaContext outContext) {
        assert ContextStack.peek() == outContext;
        final StatementContext subContext;
        subContext = new SubInsertContext(outContext);
        subContext.varMap = ((StatementContext) outContext).varMap;
        return subContext;
    }


    static CriteriaContext primaryMultiDmlContext() {
        return new MultiDmlContext(null);
    }

    @Deprecated
    static CriteriaContext primarySingleDmlContext() {
        return new SingleDmlContext(null);
    }

    @Deprecated
    static CriteriaContext primarySingleDmlContext(@Nullable _Statement._WithClauseSpec spec) {
        return new SingleDmlContext(null);
    }

    static CriteriaContext primarySingleDmlContext(@Nullable _Statement._WithClauseSpec spec,
                                                   @Nullable CriteriaContext outerContext) {
        return new SingleDmlContext(null);
    }

    static CriteriaContext joinableSingleDmlContext(@Nullable CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }


    static CriteriaContext primaryValuesContext(@Nullable _Statement._WithClauseSpec spec
            , @Nullable CriteriaContext outerContext) {
        return new ValuesContext(null);
    }

    static CriteriaContext subValuesContext(@Nullable _Statement._WithClauseSpec spec, CriteriaContext outerContext) {
        return new ValuesContext(ContextStack.peek());
    }

    static CriteriaContext otherPrimaryContext() {
        return new OtherPrimaryContext();
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


    private static CriteriaException invalidRef(CriteriaContext context, String subQueryAlias, String fieldName) {
        String m = String.format("ref of %s.%s is invalid.", subQueryAlias, fieldName);
        return ContextStack.criteriaError(context, m);
    }


    private static CriteriaException currentlyCannotRefSelection(CriteriaContext context, String selectionAlias) {
        String m = String.format("currently,couldn't reference %s[%s],please check your syntax."
                , Selection.class.getName(), selectionAlias);
        throw ContextStack.criteriaError(context, m);
    }

    private static CriteriaException notFoundCte(CriteriaContext context, String name) {
        String m = String.format("Not found the %s[%s]", _Cte.class.getName(), name);
        return ContextStack.criteriaError(context, m);
    }

    private static CriteriaException referenceCteSyntaxError(CriteriaContext context, String cteName) {
        String m = String.format("reference cte[%s] syntax error.", cteName);
        return ContextStack.criteriaError(context, m);
    }

    private static CriteriaException notFoundDerivedGroup(CriteriaContext context, List<DerivedGroup> groupList) {
        final StringBuilder builder = new StringBuilder()
                .append("Not found ")
                .append(DerivedGroup.class.getName())
                .append('[');
        int count = 0;
        for (DerivedGroup group : groupList) {
            if (count > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(group.tableAlias());
            count++;
        }
        builder.append(']');
        return ContextStack.criteriaError(context, builder.toString());
    }


    private static CriteriaException notFoundDerivedField(CriteriaContext context
            , Map<String, Map<String, RefDerivedField<?>>> aliasToRefSelection) {
        final StringBuilder builder = new StringBuilder()
                .append("Not found derived field[");
        int count = 0;
        String alias;
        for (Map.Entry<String, Map<String, RefDerivedField<?>>> e : aliasToRefSelection.entrySet()) {

            alias = e.getKey();
            for (RefDerivedField<?> s : e.getValue().values()) {
                if (count > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(alias)
                        .append(_Constant.POINT)
                        .append(s.fieldName);
                count++;
            }
        }
        builder.append(']');
        return ContextStack.criteriaError(context, builder.toString());
    }

    private static CriteriaException unknownWindows(CriteriaContext context, Map<String, Boolean> refWindowNameMap) {
        final StringBuilder builder = new StringBuilder()
                .append("unknown windows:");
        int index = 0;
        for (String windowName : refWindowNameMap.keySet()) {
            if (index > 0) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
            }
            builder.append(windowName);
            index++;
        }
        return ContextStack.criteriaError(context, builder.toString());
    }


    private static final class WithClauseContext {
        private final boolean recursive;

        private String currentName;

        private List<String> currentAliasList;

        private List<_Cte> cteList;

        private Map<String, SQLs.CteImpl> cteMap;

        private WithClauseContext(boolean recursive) {
            this.recursive = recursive;
        }

    }//WithClauseContext


    private static abstract class StatementContext implements CriteriaContext {

        private Map<String, VarExpression> varMap;

        final CriteriaContext outerContext;

        private boolean recursive;

        private Map<String, SQLs.CteImpl> withClauseCteMap;

        private Map<String, RefCte> refCteMap;

        private List<Runnable> endListenerList;

        private boolean withClauseEnd;

        private WithClauseContext withClauseContext;


        private StatementContext(@Nullable CriteriaContext outerContext) {
            this.outerContext = outerContext;
        }

        @Override
        public final CriteriaContext getOuterContext() {
            return this.outerContext;
        }

        @Override
        public final CriteriaContext getNonNullOuterContext() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return outerContext;
        }

        @Override
        public final void contextEndEvent() {
            final List<Runnable> endListenerList = this.endListenerList;
            if (endListenerList != null) {
                for (Runnable listener : endListenerList) {
                    listener.run();
                }
                endListenerList.clear();
                this.endListenerList = null;
            }
        }

        @Override
        public final void addEndEventListener(final Runnable listener) {
            List<Runnable> endListenerList = this.endListenerList;
            if (endListenerList == null) {
                this.endListenerList = endListenerList = new ArrayList<>();
            }
            endListenerList.add(listener);
        }


        @Override
        public final CteConsumer onBeforeWithClause(final boolean recursive) {
            if (this.withClauseCteMap != null || this.withClauseEnd) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.recursive = recursive;
            this.withClauseCteMap = new HashMap<>();
            return new CteConsumerImpl(this::addCte, this::withClauseEnd);
        }

        @Override
        public final void onStartCte(final String name) {
            final WithClauseContext withContext = this.withClauseContext;
            assert withContext != null;
            if (withContext.currentName != null) {
                String m = String.format("Cte[%s] don't end,couldn't start new Cte[%s]", withContext.currentName, name);
                throw ContextStack.criteriaError(this, m);
            }
            final List<_Cte> cteList = withContext.cteList;
            if (cteList != null && !(cteList instanceof ArrayList)) {
                // with clause have ended
                throw ContextStack.castCriteriaApi(this);
            }
            withContext.currentName = name;
        }

        @Override
        public final void onCteColumnAlias(final String name, final List<String> columnAliasList) {
            final WithClauseContext withContext = this.withClauseContext;
            assert withContext != null;
            final String currentName = withContext.currentName;
            assert currentName != null && currentName.equals(name);
            withContext.currentAliasList = columnAliasList;
        }

        @Override
        public final void onAddCte(final _Cte cte) {
            final WithClauseContext withContext = this.withClauseContext;
            assert withContext != null;

            final String currentName = withContext.currentName;
            assert currentName != null;
            final List<String> columnAliasList = withContext.currentAliasList;

            final SQLs.CteImpl cteImpl = (SQLs.CteImpl) cte;
            assert currentName.equals(cteImpl.name);
            assert columnAliasList == null || columnAliasList == cteImpl.columnNameList;//same instance

            Map<String, SQLs.CteImpl> cteMap = withContext.cteMap;
            List<_Cte> cteList = withContext.cteList;
            if (cteMap == null) {
                cteMap = new HashMap<>();
                withContext.cteMap = cteMap;
                cteList = new ArrayList<>();
                withContext.cteList = cteList;
            } else if (!(cteList instanceof ArrayList)) {
                // with clause end
                throw ContextStack.castCriteriaApi(this);
            }

            if (cteMap.putIfAbsent(currentName, cteImpl) != null) {
                String m = String.format("Cte[%s] duplication", currentName);
                throw ContextStack.criteriaError(this, m);
            }
            cteList.add(cteImpl);

            // clear current for next cte
            withContext.currentName = null;
            withContext.currentAliasList = null;

        }

        @Override
        public final boolean isWithRecursive() {
            final WithClauseContext withContext = this.withClauseContext;
            assert withContext != null;
            return withContext.recursive;
        }

        @Override
        public final List<_Cte> endWithClause(final boolean required) {
            final WithClauseContext withContext = this.withClauseContext;
            assert withContext != null;

            final String currentName = withContext.currentName;
            if (currentName != null) {
                String m = String.format("Cte[%s] don't end,couldn't end WITH clause.", currentName);
                throw ContextStack.criteriaError(this, m);
            }

            final Map<String, SQLs.CteImpl> cteMap = withContext.cteMap;
            List<_Cte> cteList = withContext.cteList;
            if (cteList == null) {
                assert cteMap == null;
                if (required) {
                    throw ContextStack.criteriaError(this, "You don't add any cte.");
                }
                cteList = Collections.emptyList();
                withContext.cteMap = Collections.emptyMap();
            } else if (cteList instanceof ArrayList) {
                assert cteMap instanceof HashMap;
                cteList = _CollectionUtils.unmodifiableList(cteList);
                withContext.cteMap = Collections.unmodifiableMap(cteMap);
            } else {
                throw ContextStack.castCriteriaApi(this);
            }
            withContext.cteList = cteList;
            return cteList;
        }

        @Override
        public List<_Cte> endWithClause(boolean recursive, boolean required) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final CteItem refCte(final String cteName) {
            final Map<String, SQLs.CteImpl> withClauseCteMap = this.withClauseCteMap;
            final CriteriaContext outerContext = this.outerContext;
            CteItem tempItem;
            final CteItem cteItem;
            if (withClauseCteMap == null) {
                if (outerContext == null) {
                    throw notFoundCte(this, cteName);
                }
                cteItem = outerContext.refCte(cteName);// get CteItem fro outer context
            } else if ((tempItem = withClauseCteMap.get(cteName)) != null) {
                cteItem = tempItem;
            } else if (this.withClauseEnd) {
                if (outerContext == null) {
                    throw notFoundCte(this, cteName);
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
        public final VarExpression createVar(String name, TypeMeta paramMeta) throws CriteriaException {
            throw new UnsupportedOperationException();
        }

        @Override
        public final VarExpression var(String name) throws CriteriaException {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<_TableBlock> endContext() {
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
            return this.onEndContext();
        }

        @Override
        public CriteriaContext onAddSelectItem(SelectItem selectItem) {
            throw ContextStack.criteriaError(this, "current context don't support onAddSelectItem(selectItem)");
        }

        @Override
        public List<SelectItem> endSelectClause() {
            throw ContextStack.criteriaError(this, "current context don't support endSelectClause()");
        }

        @Override
        public int selectionSize() {
            throw ContextStack.criteriaError(this, "current context don't support selectionSize()");
        }

        @Override
        public Selection selection(String alias) {
            String m = String.format("current context don't support selection(alias[%s])", alias);
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public boolean isExistWindow(String windowName) {
            throw ContextStack.criteriaError(this, "current context don't support isExistWindow(windowName)");
        }

        @Override
        public void onAddWindow(String windowName) {
            throw ContextStack.criteriaError(this, "current context don't support registerRefWindow(windowName)");
        }

        @Override
        public void onRefWindow(String windowName) {
            throw ContextStack.criteriaError(this, "current context don't support onRefWindow(windowName)");
        }

        @Override
        public TableMeta<?> getTable(String tableAlias) {
            String m = "current context don't support containTableAlias(tableAlias)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public DerivedField ref(String derivedTable, String derivedFieldName) {
            String m = "current context don't support ref(derivedTable,derivedFieldName)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public DerivedField refThis(String derivedTable, String fieldName) {
            String m = "current context don't support refThis(derivedTable,fieldName)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
            String m = "current context don't support field(tableAlias,field)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public DerivedField refOuter(String derivedTable, String fieldName) {
            String m = "current context don't support lateralRef(derivedTable,fieldName)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public void onOrderByStart() {
            //no-op
        }

        @Override
        public Expression ref(String selectionAlias) {
            String m = "current context don't support ref(selectionAlias)";
            throw ContextStack.criteriaError(this, m);
        }


        @Override
        public void onSetInnerContext(CriteriaContext innerContext) {
            String m = "current context don't support onSetInnerContext(innerContext)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public void onAddBlock(_TableBlock block) {
            String m = "current context don't support onAddBlock(block)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public _TableBlock lastBlock() {
            String m = "current context don't support lastBlock()";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public CriteriaContext endContextBeforeSelect() {
            String m = "current context don't support endContextBeforeSelect()";
            throw ContextStack.criteriaError(this, m);
        }


        List<_TableBlock> onEndContext() {
            return Collections.emptyList();
        }


        private void addCte(final _Cte cte) {
            final Map<String, SQLs.CteImpl> withClauseCteMap = this.withClauseCteMap;
            if (withClauseCteMap == null || this.withClauseEnd) {
                throw ContextStack.castCriteriaApi(this);
            }
            if (!(cte instanceof SQLs.CteImpl)) {
                String m = String.format("Illegal implementation of %s", _Cte.class.getName());
                throw ContextStack.criteriaError(this, m);
            }
            final SQLs.CteImpl cteImpl = (SQLs.CteImpl) cte;
            if (withClauseCteMap.putIfAbsent(cteImpl.name, cteImpl) != null) {
                String m = String.format("%s %s duplication.", _Cte.class.getName(), cteImpl.name);
                throw ContextStack.criteriaError(this, m);
            }
            final Map<String, RefCte> refCteMap = this.refCteMap;
            if (refCteMap == null || refCteMap.size() == 0) {
                return;
            }
            final CriteriaContext outerContext = this.outerContext;
            final RefCte refCte;
            if ((refCte = refCteMap.remove(cteImpl.name)) != null && refCte.actualCte == null) {//refCte.actualCte != null,actualCte from outer context
                final CriteriaContext context;
                context = ((CriteriaContextSpec) cteImpl.subStatement).getContext();
                if (this.recursive
                        && context instanceof BracketQueryContext) {
                    refCte.actualCte = cteImpl;
                    refCteMap.remove(cteImpl.name);
                } else {
                    throw referenceCteSyntaxError(this, cteImpl.name);
                }

            }

            if (refCteMap.size() == 0) {
                return;
            }
            for (RefCte c : refCteMap.values()) {
                if (outerContext == null) {
                    throw notFoundCte(this, c.name);
                }
                if (c.actualCte != null) {
                    //here,actualCte is from outer context.
                    continue;
                }
                c.actualCte = (_Cte) outerContext.refCte(c.name);
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
                        throw notFoundCte(this, refCte.name);
                    }
                    refCte.actualCte = (_Cte) outerContext.refCte(refCte.name);
                }
                refCteMap.clear();
            }
            this.refCteMap = null;
            this.withClauseCteMap = _CollectionUtils.unmodifiableMap(withClauseCteMap);
            this.withClauseEnd = true;
        }


    }//StatementContext


    private interface PrimaryContext {

    }


    private static abstract class JoinableContext extends StatementContext {

        private List<_TableBlock> tableBlockList;

        private Map<String, _TableBlock> aliasToBlock;

        private Map<String, Map<String, RefDerivedField<?>>> aliasToRefDerivedField;

        private Map<String, Map<String, DerivedField>> aliasToDerivedField;

        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap;

        private Function<TypeInfer, ? extends Item> function = SQLs._IDENTITY;

        private boolean refOuter;

        private JoinableContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


        @Override
        public final void onAddBlock(final _TableBlock block) {
            Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            List<_TableBlock> tableBlockList = this.tableBlockList;
            if (aliasToBlock == null) {
                assert tableBlockList == null;
                aliasToBlock = new HashMap<>();
                this.aliasToBlock = aliasToBlock;
                tableBlockList = new ArrayList<>();
                this.tableBlockList = tableBlockList;
            } else if (!(aliasToBlock instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this);
            }

            final TabularItem tableItem = block.tableItem();
            String alias = block.alias();

            if (tableItem instanceof CteItem) {
                if ("".equals(alias)) {
                    alias = ((CteItem) tableItem).name();//modify alias
                } else if (!_StringUtils.hasText(alias)) {
                    throw ContextStack.criteriaError(this, _Exceptions::tableItemAliasNoText, tableItem);
                }
            }
            if (tableItem instanceof NestedItems) {
                if (_StringUtils.hasText(alias)) {
                    throw ContextStack.criteriaError(this, _Exceptions::nestedItemsAliasHasText, alias);
                }
                this.addNestedItems((NestedItems) tableItem);
            } else if (!_StringUtils.hasText(alias)) {
                throw ContextStack.criteriaError(this, _Exceptions::tableItemAliasNoText, tableItem);
            } else if (aliasToBlock.putIfAbsent(alias, block) != null) {
                throw ContextStack.criteriaError(this, _Exceptions::tableAliasDuplication, alias);
            } else if (tableItem instanceof DerivedTable) {
                this.doOnAddDerived(block, (DerivedTable) tableItem, alias);
            }

            tableBlockList.add(block); //add to list

        }

        @Override
        public final _TableBlock lastBlock() {
            final List<_TableBlock> tableBlockList = this.tableBlockList;
            final int size;
            size = tableBlockList.size();
            if (size == 0) {
                throw ContextStack.castCriteriaApi(this);
            }
            return tableBlockList.get(size - 1);
        }

        @Override
        public final TableMeta<?> getTable(final String tableAlias) {
            final _TableBlock block;
            block = this.aliasToBlock.get(tableAlias);
            final TabularItem tableItem;
            final TableMeta<?> table;
            if (block != null && ((tableItem = block.tableItem()) instanceof TableMeta)) {
                table = (TableMeta<?>) tableItem;
            } else {
                table = null;
            }
            return table;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> QualifiedField<T> field(final String tableAlias, final FieldMeta<T> field) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock != null && !(aliasToBlock instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this);
            }
            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap = this.aliasFieldMap;
            if (aliasFieldMap == null) {
                aliasFieldMap = new HashMap<>();
                this.aliasFieldMap = aliasFieldMap;
            }
            final Function<TypeInfer, ? extends Item> function = this.function;
            assert function != null;
            return (QualifiedField<T>) aliasFieldMap.computeIfAbsent(tableAlias, k -> new HashMap<>())
                    .computeIfAbsent(field,
                            alias -> QualifiedFieldImpl.create(tableAlias, field, function));
        }

        @Override
        public final DerivedField refThis(final String derivedTable, final String fieldName) {
            final _TableBlock block;
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                block = null;
            } else if (aliasToBlock instanceof HashMap) {
                block = aliasToBlock.get(derivedTable);
            } else {
                throw ContextStack.castCriteriaApi(this);
            }
            final TabularItem tableItem;
            final DerivedField field;
            if (block == null) {
                field = getRefField(derivedTable, fieldName, true);
                assert field != null;
            } else if (!((tableItem = block.tableItem()) instanceof DerivedTable)) {
                String m = String.format("%s isn't alias of %s ", derivedTable, SubQuery.class.getName());
                throw ContextStack.criteriaError(this, m);
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
        public final DerivedField refOuter(final String derivedTable, final String fieldName) {
            final CriteriaContext outerContext = this.outerContext;
            if (outerContext == null || this instanceof PrimaryContext) {
                String m = String.format("current context[%s] no outer context", this.getClass().getSimpleName());
                throw ContextStack.criteriaError(this, m);
            }
            this.refOuter = true;
            return outerContext.refThis(derivedTable, fieldName);
        }

        @Override
        public final CriteriaContext endContextBeforeSelect() {
            assert this instanceof SimpleQueryContext;
            if (this.aliasToBlock != null
                    || this.tableBlockList != null
                    || this.aliasToRefDerivedField != null
                    || ((SimpleQueryContext) this).selectItemList != null) {
                throw ContextStack.castCriteriaApi(this);
            }
            ((SimpleQueryContext) this).selectItemList = Collections.emptyList();
            return this;
        }

        @Override
        final List<_TableBlock> onEndContext() {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock != null && !(aliasToBlock instanceof HashMap)) {
                //no bug,never here
                throw ContextStack.castCriteriaApi(this);
            }
            final List<_TableBlock> blockList;
            blockList = _CollectionUtils.safeUnmodifiableList(this.tableBlockList);
            //2. validate aliasToBlock
            final int blockSize = blockList.size();
            if (blockSize == 0 && !(this instanceof SimpleQueryContext)) {//TODO optimize for postgre
                throw ContextStack.castCriteriaApi(this);
            }
            assert aliasToBlock == null || aliasToBlock.size() >= blockSize;
            this.tableBlockList = blockList;//store for recursive checking
            this.aliasToBlock = _CollectionUtils.safeUnmodifiableMap(aliasToBlock);// unmodifiable

            //3. validate aliasToRefSelection
            final Map<String, Map<String, RefDerivedField<?>>> aliasToRefSelection = this.aliasToRefDerivedField;
            if (aliasToRefSelection != null && aliasToRefSelection.size() > 0) {
                throw notFoundDerivedField(this, aliasToRefSelection);
            }
            this.aliasToRefDerivedField = null;//clear

            final Map<String, Map<String, DerivedField>> aliasToDerivedField = this.aliasToDerivedField;
            if (aliasToDerivedField != null) {
                aliasToDerivedField.clear();
                this.aliasToDerivedField = null;//clear
            }

            //4. clear SimpleQueryContext
            if (this instanceof SimpleQueryContext) {
                final SimpleQueryContext context = (SimpleQueryContext) this;
                //validate DerivedGroup list
                final List<DerivedGroup> groupList;
                groupList = context.derivedGroupList;
                context.derivedGroupList = null;
                if (groupList != null && groupList.size() > 0) {
                    throw notFoundDerivedGroup(this, groupList);
                }
                final Map<String, Boolean> refWindowNameMap = context.refWindowNameMap;
                if (refWindowNameMap != null && refWindowNameMap.size() > 0) {
                    throw unknownWindows(this, refWindowNameMap);
                }
                context.refWindowNameMap = null;
                context.windowNameMap = null;
            }
            return blockList;
        }

        /**
         * @see #onAddBlock(_TableBlock)
         * @see #addNestedItems(NestedItems)
         */
        private void doOnAddDerived(final _TableBlock block, final DerivedTable derivedTable, final String alias) {
            if (derivedTable instanceof SubQuery) {
                final CriteriaContext context = ((CriteriaContextSpec) derivedTable).getContext();
                if (((JoinableContext) context).refOuter
                        && (!(block instanceof _DialectTableBlock)
                        || ((_DialectTableBlock) block).modifier() != SQLs.LATERAL)) {
                    String m = String.format("DerivedTable[%s] isn't lateral,couldn't reference outer field.", alias);
                    throw ContextStack.criteriaError(this, m);
                }
            }
            final Map<String, Map<String, RefDerivedField<?>>> aliasToRefSelection = this.aliasToRefDerivedField;
            if (aliasToRefSelection != null) {
                final Map<String, RefDerivedField<?>> fieldMap;
                fieldMap = aliasToRefSelection.remove(alias);
                if (fieldMap != null) {
                    this.finishRefSelections(derivedTable, alias, fieldMap);
                    fieldMap.clear();
                }
                if (aliasToRefSelection.size() == 0) {
                    this.aliasToRefDerivedField = null;
                }
            }

            final List<DerivedGroup> groupList;
            if (this instanceof SimpleQueryContext
                    && (groupList = ((SimpleQueryContext) this).derivedGroupList) != null
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
         * @see #doOnAddDerived(_TableBlock, DerivedTable, String)
         */
        private void finishRefSelections(final DerivedTable derivedTable, final String alias,
                                         final Map<String, RefDerivedField<?>> fieldMap) {
            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            if (aliasToSelection == null) {
                aliasToSelection = new HashMap<>();
                this.aliasToDerivedField = aliasToSelection;
            }
            final Map<String, DerivedField> derivedFieldMap;
            derivedFieldMap = aliasToSelection.computeIfAbsent(alias, k -> new HashMap<>());
            Selection selection;
            for (RefDerivedField<?> field : fieldMap.values()) {
                selection = derivedTable.selection(field.fieldName);
                if (selection == null) {
                    throw invalidRef(this, alias, field.fieldName);
                }

                if (field.expType.selection == null) {
                    field.expType.selection = selection;
                    derivedFieldMap.putIfAbsent(field.fieldName, field);
                }
            }

        }


        @SuppressWarnings("unchecked")
        @Nullable
        private <I extends Item> RefDerivedField<I> getRefField(final String derivedTableAlias, final String fieldName
                , final boolean create) {
            Map<String, Map<String, RefDerivedField<?>>> aliasToRefDerivedField = this.aliasToRefDerivedField;
            if (aliasToRefDerivedField == null && create) {
                aliasToRefDerivedField = new HashMap<>();
                this.aliasToRefDerivedField = aliasToRefDerivedField;
            }

            final Map<String, RefDerivedField<?>> fieldMap;
            final RefDerivedField<?> field;
            if (aliasToRefDerivedField == null) {
                field = null;
            } else if (create) {
                final Function<TypeInfer, ? extends Item> function = this.function;
                assert function != null;
                fieldMap = aliasToRefDerivedField.computeIfAbsent(derivedTableAlias, k -> new HashMap<>());
                field = fieldMap.computeIfAbsent(fieldName,
                        k -> new RefDerivedField<>(derivedTableAlias, fieldName, function));
            } else {
                fieldMap = aliasToRefDerivedField.get(derivedTableAlias);
                if (fieldMap == null) {
                    field = null;
                } else {
                    field = fieldMap.get(fieldName);
                }
            }
            return (RefDerivedField<I>) field;
        }

        private DerivedField getDerivedField(final DerivedTable derivedTable, final String tableAlias
                , final String fieldName) {
            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            if (aliasToSelection == null) {
                aliasToSelection = new HashMap<>();
                this.aliasToDerivedField = aliasToSelection;
            }
            final Map<String, DerivedField> fieldMap;
            fieldMap = aliasToSelection.computeIfAbsent(tableAlias, k -> new HashMap<>());

            final DerivedField field;
            field = fieldMap.computeIfAbsent(fieldName
                    , k -> {
                        final Selection selection;
                        selection = derivedTable.selection(fieldName);
                        if (selection == null) {
                            throw invalidRef(this, tableAlias, fieldName);
                        }
                        final Function<TypeInfer, ? extends Item> function = this.function;
                        assert function != null;
                        return new DerivedSelection<>(tableAlias, selection, function);//TODO
                    });
            return field;
        }

        private boolean doContainCte(final String cteName, final List<? extends _TableBlock> blockList) {
            boolean match = false;
            TabularItem item;
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
            TabularItem tableItem;
            String alias;
            for (_TableBlock block : ((_NestedItems) nestedItems).tableBlockList()) {
                tableItem = block.tableItem();
                alias = block.alias();
                if (tableItem instanceof CteItem) {
                    if ("".equals(alias)) {
                        alias = ((CteItem) tableItem).name();//modify alias
                    } else if (!_StringUtils.hasText(alias)) {
                        throw ContextStack.criteriaError(this, _Exceptions::tableItemAliasNoText, tableItem);
                    }
                }
                if (tableItem instanceof NestedItems) {
                    if (_StringUtils.hasText(alias)) {
                        throw ContextStack.criteriaError(this, _Exceptions::nestedItemsAliasHasText, alias);
                    }
                    this.addNestedItems((NestedItems) tableItem);
                } else if (aliasToBlock.putIfAbsent(alias, block) != null) {
                    throw ContextStack.criteriaError(this, _Exceptions::tableAliasDuplication, alias);
                } else if (tableItem instanceof DerivedTable) {
                    // note ,no tableBlockList.
                    this.doOnAddDerived(block, (DerivedTable) tableItem, alias);
                }

            }


        }


    }//JoinableContext


    /**
     * @see #primaryInsertContext()
     */
    private static final class InsertContext extends StatementContext {

        private InsertContext() {
            super(null);
        }

    }// InsertContext


    /**
     * @see #cteInsertContext(CriteriaContext)
     */
    private static final class SubInsertContext extends StatementContext {

        private SubInsertContext(CriteriaContext outerContext) {
            super(outerContext);
        }

    }//SubInsertContext


    /**
     * @see #primarySingleDmlContext()
     */
    private static final class SingleDmlContext extends StatementContext {

        private SingleDmlContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


    }//SingleDmlContext

    private static final class MultiDmlContext extends JoinableContext {

        private MultiDmlContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


    }// MultiDmlContext


    private static abstract class SimpleQueryContext extends JoinableContext {

        private final CriteriaContext leftContext;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketQueryContext#ref(String)}
         */
        private List<SelectItem> selectItemList;

        private List<DerivedGroup> derivedGroupList;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketQueryContext#ref(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketQueryContext#ref(String)}
         */
        private Map<String, RefSelection> refSelectionMap;

        private Map<String, Boolean> windowNameMap;

        private Map<String, Boolean> refWindowNameMap;

        private boolean orderByStart;

        private SimpleQueryContext(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(outerContext);
            assert leftContext == null || leftContext.getOuterContext() == outerContext;
            this.leftContext = leftContext;
        }


        @Override
        public final CriteriaContext onAddSelectItem(final @Nullable SelectItem selectItem) {
            if (selectItem == null) {
                throw ContextStack.nullPointer(this);
            }
            List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                selectItemList = new ArrayList<>();
                this.selectItemList = selectItemList;
            } else if (!(selectItemList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this);
            }

            selectItemList.add(selectItem);

            if (selectItem instanceof DerivedGroup) {
                List<DerivedGroup> derivedGroupList = this.derivedGroupList;
                if (derivedGroupList == null) {
                    derivedGroupList = new LinkedList<>();
                    this.derivedGroupList = derivedGroupList;
                }
                derivedGroupList.add((DerivedGroup) selectItem);
            }
            return this;
        }


        @Override
        public final void onOrderByStart() {
            if (this.orderByStart) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.orderByStart = true;
        }

        @Override
        public final Expression ref(final String selectionAlias) {
            final CriteriaContext leftContext = this.leftContext;
            final Expression selection;
            if (leftContext == null || !this.orderByStart) {
                Map<String, RefSelection> refSelectionMap = this.refSelectionMap;
                if (refSelectionMap == null) {
                    refSelectionMap = new HashMap<>();
                    this.refSelectionMap = refSelectionMap;
                }
                selection = refSelectionMap.computeIfAbsent(selectionAlias, this::createRefSelection);
            } else {
                selection = leftContext.ref(selectionAlias);
            }
            return selection;
        }

        @Override
        public final Selection selection(final String alias) {
            if (!(this instanceof SubQueryContext)) {
                throw ContextStack.castCriteriaApi(this);
            }
            return this.getSelectionMap().get(alias);
        }

        @Override
        public final int selectionSize() {
            return this.getSelectionMap().size();
        }

        @Override
        public final boolean isExistWindow(final String windowName) {
            final Map<String, Boolean> windowNameMap = this.windowNameMap;
            return windowNameMap != null && windowNameMap.containsKey(windowName);
        }

        @Override
        public final void onAddWindow(final String windowName) {
            Map<String, Boolean> windowNameMap = this.windowNameMap;
            if (windowNameMap == null) {
                windowNameMap = new HashMap<>();
                this.windowNameMap = windowNameMap;
            }
            if (windowNameMap.putIfAbsent(windowName, Boolean.TRUE) != null) {
                String m = String.format("window[%s] duplication.", windowName);
                throw ContextStack.criteriaError(this, m);
            }
            final Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap != null) {
                refWindowNameMap.remove(windowName);
            }
        }

        @Override
        public final void onRefWindow(final String windowName) {
            Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap == null) {
                refWindowNameMap = new HashMap<>();
                this.refWindowNameMap = refWindowNameMap;
            }
            refWindowNameMap.putIfAbsent(windowName, Boolean.TRUE);
        }


        @Override
        public final List<SelectItem> endSelectClause() {
            List<SelectItem> selectItemList = this.selectItemList;
            if (!(selectItemList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this);
            }
            selectItemList = _CollectionUtils.unmodifiableList(selectItemList);
            this.selectItemList = selectItemList;
            return selectItemList;
        }


        /**
         * @see #ref(String)
         */
        private RefSelection createRefSelection(final String selectionAlias) {
            final Selection selection;
            selection = this.getSelectionMap().get(selectionAlias);
            if (selection == null) {
                throw CriteriaUtils.unknownSelection(this, selectionAlias);
            }
            return new RefSelection(selection);//TODO
        }

        private Map<String, Selection> getSelectionMap() {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                final List<SelectItem> selectItemList = this.selectItemList;
                if (selectItemList == null) {
                    throw ContextStack.castCriteriaApi(this);
                }
                selectionMap = CriteriaUtils.createSelectionMap(selectItemList);
                this.selectionMap = selectionMap;
            }
            return selectionMap;
        }


    }//SimpleQueryContext

    private static final class SelectContext extends SimpleQueryContext implements PrimaryContext {


        /**
         * @see #primaryQuery(_Statement._WithClauseSpec, CriteriaContext, CriteriaContext)
         */
        private SelectContext(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }

    }//SelectContext


    private static final class SubQueryContext extends SimpleQueryContext {

        /**
         * @see #subQueryContext(_Statement._WithClauseSpec, CriteriaContext, CriteriaContext)
         */
        SubQueryContext(CriteriaContext outerContext, final @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
            Objects.requireNonNull(outerContext);
        }

    }//SubQueryContext


    private static final class BracketQueryContext extends StatementContext {

        private final CriteriaContext leftContext;

        private CriteriaContext innerContext;

        private boolean orderByStart;

        /**
         * @see #bracketContext(_Statement._WithClauseSpec, CriteriaContext, CriteriaContext)
         */
        private BracketQueryContext(final @Nullable CriteriaContext outerContext,
                                    final @Nullable CriteriaContext leftContext) {
            super(outerContext);
            this.leftContext = leftContext;
        }

        @Override
        public void onOrderByStart() {
            if (this.innerContext == null || this.orderByStart) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.orderByStart = true;
        }

        @Override
        public Expression ref(final String selectionAlias) {
            final CriteriaContext leftContext = this.leftContext, innerContext = this.innerContext;
            final Expression selection;
            if (innerContext == null || !this.orderByStart) {
                throw ContextStack.castCriteriaApi(this);
            } else if (leftContext == null) {
                selection = innerContext.ref(selectionAlias);
            } else {
                selection = leftContext.ref(selectionAlias);
            }
            return selection;
        }

        @Override
        public void onSetInnerContext(final @Nullable CriteriaContext innerContext) {
            if (this.innerContext != null) {
                throw ContextStack.castCriteriaApi(this);
            } else if (innerContext == null) {
                throw ContextStack.nullPointer(this);
            } else if (innerContext.getOuterContext() != this) {
                //no bug,never here
                throw ContextStack.criteriaError(this, "innerContext not match");
            }
            this.innerContext = innerContext;
        }


        @Override
        List<_TableBlock> onEndContext() {
            if (innerContext == null) {
                throw ContextStack.castCriteriaApi(this);
            }
            return Collections.emptyList();
        }


    }//BracketQueryContext

    private static final class ValuesContext extends StatementContext {

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketQueryContext#ref(String)}
         */
        private List<? extends SelectItem> selectItemList;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketQueryContext#ref(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketQueryContext#ref(String)}
         */
        private Map<String, RefSelection> refSelectionMap;

        private ValuesContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


        @Override
        public Expression ref(final String selectionAlias) {
            Map<String, RefSelection> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                this.refSelectionMap = refSelectionMap = new HashMap<>();
            }
            return refSelectionMap.computeIfAbsent(selectionAlias, this::createRefSelection);
        }

        private RefSelection createRefSelection(final String selectionAlias) {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                final List<? extends SelectItem> selectItemList = this.selectItemList;
                if (selectItemList == null) {
                    throw currentlyCannotRefSelection(this, selectionAlias);
                }
                this.selectionMap = selectionMap = CriteriaUtils.createSelectionMap(selectItemList);
            }
            final Selection selection;
            selection = selectionMap.get(selectionAlias);
            if (selection == null) {
                throw CriteriaUtils.unknownSelection(this, selectionAlias);
            }
            return new RefSelection(selection);
        }


    }//ValuesContext


    private static final class OtherPrimaryContext extends StatementContext {

        private OtherPrimaryContext() {
            super(null);
        }


    }//OtherPrimaryContext


    static final class DerivedSelection<I extends Item> extends OperationDataField<I>
            implements _Selection, DerivedField {

        private final String tableName;

        private final Selection selection;

        private DerivedSelection(String tableName, Selection selection, Function<TypeInfer, I> function) {
            super(function);
            this.tableName = tableName;
            this.selection = selection;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.selection.typeMeta();
        }

        @Override
        public TableField tableField() {
            return ((_Selection) this.selection).tableField();
        }

        @Override
        public Expression selectionExp() {
            return this;
        }

        @Override
        public String fieldName() {
            return this.selection.alias();
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
        public void appendSelection(final _SqlContext context) {
            final DialectParser dialect = context.parser();

            final String safeFieldName = dialect.identifier(this.selection.alias());

            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            dialect.identifier(this.tableName, builder)
                    .append(_Constant.POINT)
                    .append(safeFieldName)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(safeFieldName);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final DialectParser dialect = context.parser();
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            dialect.identifier(this.tableName, builder)
                    .append(_Constant.POINT);
            dialect.identifier(this.selection.alias(), builder);

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
                final DerivedSelection<?> selection = (DerivedSelection<?>) obj;
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


    static final class RefDerivedField<I extends Item> extends OperationDataField<I>
            implements DerivedField, _Selection {

        final String tableName;

        final String fieldName;

        private final DelaySelection expType;

        private RefDerivedField(String tableName, String fieldName, Function<TypeInfer, I> function) {
            super(function);
            this.tableName = tableName;
            this.fieldName = fieldName;
            this.expType = new DelaySelection();
        }

        @Override
        public TypeMeta typeMeta() {
            return this.expType;
        }

        @Override
        public TableField tableField() {
            final Selection selection = this.expType.selection;
            if (selection == null) {
                throw new IllegalStateException(String.format("No actual %s", Selection.class.getName()));
            }
            return ((_Selection) selection).tableField();
        }

        @Override
        public Expression selectionExp() {
            return this;
        }

        @Override
        public String fieldName() {
            return this.fieldName;
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
        public void appendSelection(final _SqlContext context) {
            final DialectParser dialect = context.parser();

            final String safeFieldName = dialect.identifier(this.fieldName);
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            dialect.identifier(this.tableName, builder)
                    .append(_Constant.POINT)
                    .append(safeFieldName)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(safeFieldName);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final DialectParser dialect = context.parser();
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            dialect.identifier(this.tableName, builder)
                    .append(_Constant.POINT);
            dialect.identifier(this.fieldName, builder);
        }


        @Override
        public String toString() {
            return String.format(" %s.%s", this.tableName, this.fieldName);
        }

        private void setSelection(final Selection selection) {
            final DelaySelection delaySelection = this.expType;
            assert delaySelection.selection == null;
            delaySelection.selection = selection;
        }

        private boolean isDelay() {
            return this.expType.selection == null;
        }


    }//DerivedFieldImpl


    private static final class DelaySelection implements TypeMeta.Delay {

        private Selection selection;

        @Override
        public MappingType mappingType() {
            final Selection selection = this.selection;
            if (selection == null) {
                throw new IllegalStateException(String.format("No actual %s", Selection.class.getName()));
            }
            return selection.typeMeta().mappingType();
        }

        @Override
        public boolean isPrepared() {
            return this.selection != null;
        }

    }//DelaySelection


    static final class RefSelection extends OperationExpression<TypeInfer> {

        private final Selection selection;

        private RefSelection(Selection selection) {
            super(SQLs._IDENTITY);
            this.selection = selection;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.selection.typeMeta();
        }

        @Override
        public RefSelection bracket() {
            //return this ,don't create new instance
            return this;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            context.parser()
                    .identifier(this.selection.alias(), builder);
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
        public TableField tableField() {
            return ((_Selection) this.field).tableField();
        }

        @Override
        public String alias() {
            return this.alias;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.field.typeMeta();
        }


        @Override
        public void appendSelection(final _SqlContext context) {
            ((_SelfDescribed) this.field).appendSql(context);
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser()
                    .identifier(this.alias, builder);
        }

        @Override
        public Expression selectionExp() {
            return this.field;
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


    static final class RefCte implements _Cte {

        private final String name;

        private _Cte actualCte;

        private RefCte(String name) {
            this.name = name;
        }

        @Override
        public List<String> columnNameList() {
            final _Cte actualCte = this.actualCte;
            if (actualCte == null) {
                throw new IllegalStateException("No actual cte");
            }
            return actualCte.columnNameList();
        }

        @Override
        public SubStatement subStatement() {
            final _Cte actualCte = this.actualCte;
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
            final _Cte actualCte = this.actualCte;
            if (actualCte == null) {
                throw new IllegalStateException("No actual cte");
            }
            return actualCte.selectItemList();
        }

        @Override
        public Selection selection(String derivedAlias) {
            final _Cte actualCte = this.actualCte;
            if (actualCte == null) {
                throw new IllegalStateException("No actual cte");
            }
            return actualCte.selection(derivedAlias);
        }

        @Override
        public String toString() {
            return String.format("reference  %s[name:%s,hash:%s]", CteItem.class.getName()
                    , this.name, System.identityHashCode(this));
        }

    }// RefCte

    private static final class CteConsumerImpl implements CriteriaContext.CteConsumer {


        private final Consumer<_Cte> cteConsumer;

        private final Runnable endCallback;

        private final List<_Cte> cteList = new ArrayList<>();

        private CteConsumerImpl(Consumer<_Cte> cteConsumer, Runnable endCallback) {
            this.cteConsumer = cteConsumer;
            this.endCallback = endCallback;
        }

        @Override
        public void addCte(_Cte cte) {
            this.cteConsumer.accept(cte);
            this.cteList.add(cte);
        }

        @Override
        public List<_Cte> end() {
            this.endCallback.run();
            return _CollectionUtils.unmodifiableList(this.cteList);
        }

    }//CteConsumerImpl


}
