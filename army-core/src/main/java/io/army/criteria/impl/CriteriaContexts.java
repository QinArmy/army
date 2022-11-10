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
    static CriteriaContext primaryQuery(@Nullable CriteriaContext outerContext) {
        return new SimpleQueryContext(outerContext);
    }

    static CriteriaContext primaryQuery(@Nullable _Statement._WithClauseSpec spec
            , @Nullable CriteriaContext outerContext) {
        return new SimpleQueryContext(outerContext);
    }

    static CriteriaContext unionSelectContext(final CriteriaContext leftContext) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext primaryQueryContextFrom(final Query query) {
        final StatementContext leftContext;
        leftContext = (StatementContext) ((CriteriaContextSpec) query).getContext();

        final SimpleQueryContext context;
        context = new SimpleQueryContext(null);
        ((StatementContext) context).varMap = leftContext.varMap;
        return context;
    }

    /**
     * @see #unionSubQueryContext(CriteriaContext)
     */
    static CriteriaContext subQueryContext(final CriteriaContext outerContext) {
        assert outerContext == ContextStack.peek();
        final StatementContext context;
        context = new SimpleQueryContext(outerContext);
        context.varMap = ((StatementContext) outerContext).varMap;
        return context;
    }

    static CriteriaContext subQueryContext(@Nullable _Statement._WithClauseSpec withSpec
            , final CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #subQueryContext(CriteriaContext)
     */
    static CriteriaContext unionSubQueryContext(final CriteriaContext leftContext) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext unionBracketContext(final CriteriaContext leftContext) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static CriteriaContext bracketContext(@Nullable final CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext bracketContext(@Nullable _Statement._WithClauseSpec withSpec
            , @Nullable final CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static CriteriaContext withClauseContext(final @Nullable CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static CriteriaContext bracketContext(@Nullable _Statement._WithClauseSpec withSpec) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext bracketContext(@Nullable CriteriaContext outerContext
            , @Nullable _Statement._WithClauseSpec withSpec) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext subQueryContextFrom(final Query query) {
        final StatementContext leftContext;
        leftContext = (StatementContext) ((CriteriaContextSpec) query).getContext();

        final SimpleQueryContext context;
        context = new SimpleQueryContext(ContextStack.peek());
        ((StatementContext) context).varMap = leftContext.varMap;
        return context;
    }


    static CriteriaContext noActionContext(final RowSet rowSet) {
        final StatementContext leftContext;
        leftContext = (StatementContext) ((CriteriaContextSpec) rowSet).getContext();
        final CriteriaContext outerContext;
        if (rowSet instanceof SubStatement) {
            outerContext = ContextStack.peek();
        } else {
            outerContext = null;
        }
        final NoActionQueryContext context;
        context = new NoActionQueryContext(outerContext, leftContext);
        ((StatementContext) context).varMap = leftContext.varMap;
        return context;
    }

    static CriteriaContext unionContext(final RowSet left, final RowSet right) {
        final StatementContext leftContext;
        leftContext = (StatementContext) ((CriteriaContextSpec) left).getContext();
        final CriteriaContext outerContext;
        if (left instanceof SubStatement) {
            outerContext = ContextStack.peek();
        } else {
            outerContext = null;
        }
        final UnionQueryContext context;
        context = new UnionQueryContext(outerContext, leftContext
                , ((CriteriaContextSpec) right).getContext());
        ((StatementContext) context).varMap = leftContext.varMap;
        return context;
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

    static CriteriaContext primarySingleDmlContext(@Nullable _Statement._WithClauseSpec spec) {
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


    private static abstract class StatementContext implements CriteriaContext, CriteriaContext.OuterContextSpec {

        Object criteria;


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
        public void onAddDerivedGroup(DerivedGroup group) {
            throw new UnsupportedOperationException("current context don't support onAddDerivedGroup(group)");
        }

        @Override
        public void selectList(List<? extends SelectItem> selectItemList) {
            //no bug,never here
            throw new UnsupportedOperationException("current context don't support selectList(selectItemList)");
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

        @SuppressWarnings("unchecked")
        @Override
        public final <C> C criteria() {
            try {
                return (C) this.criteria;
            } catch (ClassCastException e) {
                String m = String.format("Criteria cast failure,please check %s method parameter type."
                        , Function.class.getName());
                throw ContextStack.criteriaError(this, m);
            }
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
            return Collections.emptyList();
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
            throw ContextStack.criteriaError(this, "current context don't support selection(alias)");
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
        public <I extends Item> ItemDerivedField<I> ref(String derivedTable, String fieldName
                , Function<Selection, I> function) {
            String m = "current context don't support ref(derivedTable,fieldName)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public DerivedField refThis(String derivedTable, String fieldName) {
            String m = "current context don't support refThis(derivedTable,fieldName)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public <I extends Item> ItemDerivedField<I> refThis(String derivedTable, String fieldName
                , Function<Selection, I> function) {
            String m = "current context don't support refThis(derivedTable,fieldName,function)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
            String m = "current context don't support field(tableAlias,field)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public <T, I extends Item> ItemField<T, I> field(String tableAlias, FieldMeta<T> field
                , Function<Selection, I> function) {
            String m = "current context don't support field(tableAlias,field)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public DerivedField outerRef(String derivedTable, String derivedFieldName) {
            String m = "current context don't support lateralRef(derivedTable,derivedFieldName)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public Expression ref(String selectionAlias) {
            String m = "current context don't support ref(selectionAlias)";
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
                        && context instanceof UnionOperationContext
                        && ((UnionOperationContext) context).isRecursive(cteImpl.name)) {
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


    }//AbstractContext


    private static abstract class JoinableContext extends StatementContext {


        private List<_TableBlock> tableBlockList = new ArrayList<>();

        private Map<String, _TableBlock> aliasToBlock = new HashMap<>();

        private Map<String, Map<String, RefDerivedField<?>>> aliasToRefDerivedField;

        private Map<String, Map<String, DerivedField>> aliasToDerivedField;

        private Map<String, Map<FieldMeta<?>, ItemField<?, ?>>> aliasFieldMap;

        private JoinableContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


        @Override
        public final void onAddBlock(final _TableBlock block) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                throw ContextStack.castCriteriaApi(this);
            }
            final List<_TableBlock> tableBlockList = this.tableBlockList;
            final int oldBlockSize = tableBlockList.size();

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
                this.doOnAddDerived((DerivedTable) tableItem, alias);
            }

            if (tableBlockList.size() != oldBlockSize) {
                // no bug,never here
                throw new IllegalStateException("addNestedItems error");
            }
            tableBlockList.add(block); //add to list

        }


        @Override
        public final _TableBlock lastBlock() {
            final List<_TableBlock> tableBlockList = this.tableBlockList;
            if (tableBlockList.size() == 0) {
                throw _Exceptions.castCriteriaApi();
            }
            final _TableBlock block;
            block = tableBlockList.get(tableBlockList.size() - 1);
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
        public final <T, I extends Item> ItemField<T, I> field(final String tableAlias, final FieldMeta<T> field
                , final Function<Selection, I> function) {
            if (!(this.aliasToBlock instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this);
            }
            Map<String, Map<FieldMeta<?>, ItemField<?, ?>>> aliasFieldMap = this.aliasFieldMap;
            if (aliasFieldMap == null) {
                aliasFieldMap = new HashMap<>();
                this.aliasFieldMap = aliasFieldMap;
            }
            return (ItemField<T, I>) aliasFieldMap.computeIfAbsent(tableAlias, k -> new HashMap<>())
                    .computeIfAbsent(field, k -> QualifiedFieldImpl.create(tableAlias, field, function));
        }

        @Override
        public final DerivedField ref(final String derivedTable, final String fieldName) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (!(aliasToBlock instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this);
            }
            final _TableBlock block = aliasToBlock.get(derivedTable);
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
        public final <I extends Item> ItemDerivedField<I> ref(String derivedTable, String fieldName
                , Function<Selection, I> function) {
            return super.ref(derivedTable, fieldName, function);
        }

        @Override
        public final DerivedField refThis(String derivedTable, String fieldName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <I extends Item> ItemDerivedField<I> refThis(String derivedTable, String fieldName
                , Function<Selection, I> function) {
            return super.refThis(derivedTable, fieldName, function);
        }

        @Override
        public final List<_TableBlock> endContext() {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (!(aliasToBlock instanceof HashMap)) {
                //no bug,never here
                throw new IllegalStateException("duplication clear");
            }
            //1. clear super
            super.endContext();
            //2. validate aliasToBlock
            final List<_TableBlock> blockList = _CollectionUtils.unmodifiableList(this.tableBlockList);
            this.tableBlockList = blockList;//store for recursive checking
            final int blockSize = blockList.size();
            if (blockSize == 0 && !(this instanceof SimpleQueryContext)) {
                throw ContextStack.castCriteriaApi(this);
            }
            if (aliasToBlock.size() < blockSize) {// probably NestedItems
                //no bug,never here
                throw new IllegalStateException("block size  match.");
            }
            this.aliasToBlock = Collections.unmodifiableMap(aliasToBlock);// unmodifiable


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
                groupList = context.groupList;
                context.groupList = null;
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


        private void doOnAddDerived(final DerivedTable derivedTable, final String alias) {
            if (derivedTable instanceof SubQuery) {
                final CriteriaContext context = ((CriteriaContextSpec) derivedTable).getContext();
                if (((SimpleQueryContext) context).refOuterField) {
                    String m = String.format("SubQuery %s isn't lateral,couldn't reference outer field.", alias);
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
        private void finishRefSelections(DerivedTable derivedTable, String alias, Map<String, RefDerivedField<?>> fieldMap) {
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
                if (field.paramMeta.selection == null) {
                    field.paramMeta.selection = selection;
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
                fieldMap = aliasToRefDerivedField.computeIfAbsent(derivedTableAlias, k -> new HashMap<>());
                field = fieldMap.computeIfAbsent(fieldName, k -> new RefDerivedField<>(derivedTableAlias, fieldName, null));
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
                        return new DerivedSelection<>(tableAlias, selection, null);//TODO
                    });
            return field;
        }

        private boolean containCte(final String cteName) {
            return this.doContainCte(cteName, this.tableBlockList);
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
                    this.doOnAddDerived((DerivedTable) tableItem, alias);
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


    private static class SimpleQueryContext extends JoinableContext {

        final List<SelectItem> SELECT_START = Collections.singletonList((SelectItem) SQLs.START);


        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  UnionOperationContext#ref(String)}
         */
        private List<? extends SelectItem> selectItemList;

        private List<DerivedGroup> groupList;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  UnionOperationContext#ref(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  UnionOperationContext#ref(String)}
         */
        private Map<String, RefSelection> refSelectionMap;

        private Map<String, Boolean> windowNameMap;

        private Map<String, Boolean> refWindowNameMap;

        private boolean refOuterField;

        private SimpleQueryContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }

        @Override
        public final DerivedField outerRef(String derivedTable, String derivedFieldName) {
            final CriteriaContext outerContext = this.outerContext;
            if (outerContext == null) {
                throw ContextStack.criteriaError(this, "No outer context for current context.");
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

        @Override
        public final boolean isExistWindow(final String windowName) {
            final Map<String, Boolean> windowNameMap = this.windowNameMap;
            return windowNameMap != null && windowNameMap.containsKey(windowName);
        }

        @Override
        public final void onAddWindow(final String windowName) {
            Map<String, Boolean> windowNameMap = this.windowNameMap;
            if (windowNameMap == null) {
                this.windowNameMap = windowNameMap = new HashMap<>();
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
                this.refWindowNameMap = refWindowNameMap = new HashMap<>();
            }
            refWindowNameMap.putIfAbsent(windowName, Boolean.TRUE);
        }

        private <I extends Item> RefSelection<I> createRefSelection(final String selectionAlias) {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                final List<? extends SelectItem> selectItemList = this.selectItemList;
                if (selectItemList == null) {
                    throw currentlyCannotRefSelection(this, selectionAlias);
                }
                selectionMap = CriteriaUtils.createSelectionMap(selectItemList);
                this.selectionMap = selectionMap;
            }
            final Selection selection;
            selection = selectionMap.get(selectionAlias);
            if (selection == null) {
                throw CriteriaUtils.unknownSelection(this, selectionAlias);
            }
            return new RefSelection<>(selection, null);//TODO
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
    private static abstract class UnionOperationContext extends StatementContext {

        private final CriteriaContext leftContext;

        private UnionOperationContext(@Nullable CriteriaContext outerContext, CriteriaContext leftContext) {
            super(outerContext);
            this.leftContext = leftContext;
        }


        @Override
        public final Expression ref(final String selectionAlias) {
            return this.leftContext.ref(selectionAlias);
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

        private BracketQueryContext(@Nullable CriteriaContext outerContext, CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }

    }//BracketQueryContext


    private static final class NoActionQueryContext extends UnionOperationContext {

        private NoActionQueryContext(@Nullable CriteriaContext outerContext, CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }

    }//NoActionQueryContext


    private static final class UnionQueryContext extends UnionOperationContext {

        private final CriteriaContext rightContext;

        private UnionQueryContext(@Nullable CriteriaContext outerContext, CriteriaContext leftContext
                , CriteriaContext rightContext) {
            super(outerContext, leftContext);
            this.rightContext = rightContext;
        }

        @Override
        public List<_TableBlock> endContext() {
            super.endContext();
            return Collections.emptyList();
        }


    }//UnionQueryContext

    private static final class ValuesContext extends StatementContext {

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  UnionOperationContext#ref(String)}
         */
        private List<? extends SelectItem> selectItemList;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  UnionOperationContext#ref(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  UnionOperationContext#ref(String)}
         */
        private Map<String, RefSelection<?>> refSelectionMap;

        private ValuesContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }

        @Override
        public void selectList(List<? extends SelectItem> selectItemList) {
            if (this.selectItemList != null) {
                //no bug,never here
                throw new IllegalStateException("duplication");
            }
            this.selectItemList = selectItemList;
        }


        @Override
        public Expression ref(final String selectionAlias) {
            Map<String, RefSelection<?>> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                this.refSelectionMap = refSelectionMap = new HashMap<>();
            }
            return refSelectionMap.computeIfAbsent(selectionAlias, this::createRefSelection);
        }

        private <I extends Item> RefSelection<I> createRefSelection(final String selectionAlias) {
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
            return new RefSelection<>(selection, null);
        }


    }//ValuesContext


    private static final class OtherPrimaryContext extends StatementContext {

        private OtherPrimaryContext() {
            super(null);
        }


    }//OtherPrimaryContext


    static final class DerivedSelection<I extends Item> extends OperationDataField<I>
            implements ItemDerivedField<I>, _Selection {

        private final String tableName;

        private final Selection selection;

        private DerivedSelection(String tableName, Selection selection, Function<Selection, I> function) {
            super(function);
            this.tableName = tableName;
            this.selection = selection;
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
        public TypeMeta typeMeta() {
            return this.selection.typeMeta();
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
            implements ItemDerivedField<I>, _Selection {

        final String tableName;

        final String fieldName;

        final DelaySelection paramMeta;

        private RefDerivedField(String tableName, String fieldName, Function<Selection, I> function) {
            super(function);
            this.tableName = tableName;
            this.fieldName = fieldName;
            this.paramMeta = new DelaySelection();
        }

        @Override
        public TableField tableField() {
            final Selection selection = this.paramMeta.selection;
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
        public TypeMeta typeMeta() {
            return this.paramMeta;
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

    /**
     * @see UnionQueryContext#ref(String)
     */
    static final class RefSelection<I extends Item> extends OperationExpression<I> {

        private final Selection selection;

        private RefSelection(Selection selection, Function<Selection, I> function) {
            super(function);
            this.selection = selection;
        }


        @Override
        public _ItemExpression<I> bracket() {
            //return this ,don't create new instance
            return this;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.selection.typeMeta();
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
