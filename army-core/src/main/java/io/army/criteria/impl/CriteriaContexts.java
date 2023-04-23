package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.VarExpression;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
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


    /**
     * @param spec                if non-null,then outerBracketContext and leftContext both must be null.
     * @param outerBracketContext if non-null,then spec and leftContext both must be null.
     * @param leftContext         if non-null,then spec and outerBracketContext both must be null.
     */
    static CriteriaContext primaryQueryContext(final @Nullable ArmyStmtSpec spec,
                                               final @Nullable CriteriaContext outerBracketContext,
                                               final @Nullable CriteriaContext leftContext) {
        final PrimaryQueryContext context;
        if (spec == null) {
            assert !(outerBracketContext != null && leftContext != null);
            assert outerBracketContext == null || outerBracketContext instanceof BracketContext;
            context = new PrimaryQueryContext(outerBracketContext, leftContext);
        } else {
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();

            context = new PrimaryQueryContext(dispatcherContext.outerContext, dispatcherContext.leftContext);
            migrateContext(context, dispatcherContext);
            migrateToQueryContext(context, dispatcherContext);
        }
        return context;
    }

    /**
     * @param spec         if non-null,then outerBracketContext and leftContext both must be null.
     * @param outerContext if non-null,then spec must be null.
     * @param leftContext  if non-null,then outerContext must be non-null spec must be null.
     */
    static CriteriaContext subQueryContext(final @Nullable ArmyStmtSpec spec,
                                           final @Nullable CriteriaContext outerContext,
                                           final @Nullable CriteriaContext leftContext) {
        final SubQueryContext context;
        if (spec == null) {
            assert outerContext != null && (leftContext == null || leftContext.getOuterContext() == outerContext);
            context = new SubQueryContext(outerContext, leftContext);
        } else {
            assert outerContext == null && leftContext == null;
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();
            assert dispatcherContext.outerContext != null;
            context = new SubQueryContext(dispatcherContext.outerContext, dispatcherContext.leftContext);

            migrateContext(context, dispatcherContext);
            migrateToQueryContext(context, dispatcherContext);
        }
        return context;
    }


    static CriteriaContext bracketContext(final ArmyStmtSpec spec) {
        final StatementContext migratedContext;
        migratedContext = (StatementContext) spec.getContext();

        final BracketContext context;
        if (migratedContext instanceof PrimaryContext) {
            context = new PrimaryBracketContext(migratedContext.outerContext, migratedContext.getLeftContext());
        } else {
            context = new SubBracketContext(migratedContext.outerContext, migratedContext.getLeftContext());
        }
        migrateContext(context, migratedContext);
        return context;
    }


    static CriteriaContext primaryInsertContext(@Nullable ArmyStmtSpec spec) {
        final PrimaryDispatcherContext multiStmtContext;
        if (spec == null) {
            multiStmtContext = null;
        } else {
            multiStmtContext = (PrimaryDispatcherContext) spec.getContext();
        }

        final PrimaryInsertContext context;
        context = new PrimaryInsertContext();

        if (multiStmtContext != null) {
            migrateContext(context, multiStmtContext);
            assertNonQueryContext(multiStmtContext);
        }
        return context;
    }

    /**
     * @param spec         if non-null,then outerContext must be null.
     * @param outerContext if non-null,then spec must be null.
     */
    static CriteriaContext subInsertContext(final @Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext) {
        final StatementContext context;
        if (spec == null) {
            assert outerContext != null;
            context = new SubInsertContext(outerContext);
        } else {
            assert outerContext == null;
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();

            context = new SubInsertContext(dispatcherContext.getNonNullOuterContext());

            migrateContext(context, dispatcherContext);
            assertNonQueryContext(dispatcherContext);
        }
        return context;
    }

    static CriteriaContext primarySingleDmlContext(@Nullable ArmyStmtSpec spec) {
        final PrimaryDispatcherContext multiStmtContext;
        if (spec == null) {
            multiStmtContext = null;
        } else {
            multiStmtContext = (PrimaryDispatcherContext) spec.getContext();
        }
        final StatementContext context;
        context = new PrimarySingleDmlContext(multiStmtContext);

        if (multiStmtContext != null) {
            migrateContext(context, multiStmtContext);
            assertNonQueryContext(multiStmtContext);
        }
        return context;
    }


    static CriteriaContext primaryMultiDmlContext(final @Nullable ArmyStmtSpec spec) {
        final PrimaryMultiDmlContext context;
        context = new PrimaryMultiDmlContext();

        if (spec != null) {
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();
            migrateContext(context, dispatcherContext);

            assertNonQueryContext(dispatcherContext);
        }
        return context;
    }


    /**
     * <p>
     * For Example , Postgre update/delete criteria context
     * </p>
     */
    static CriteriaContext primaryJoinableSingleDmlContext(final @Nullable ArmyStmtSpec spec) {
        final PrimaryJoinableSingleDmlContext context;
        context = new PrimaryJoinableSingleDmlContext();
        if (spec != null) {
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();
            migrateContext(context, dispatcherContext);
            assertNonQueryContext(dispatcherContext);
        }
        return context;
    }

    /**
     * <p>
     * For Example ,Postgre update/delete criteria context
     * </p>
     */
    static CriteriaContext subJoinableSingleDmlContext(final CriteriaContext outerContext) {
        final SubJoinableSingleDmlContext context;
        context = new SubJoinableSingleDmlContext(outerContext);
        return context;
    }


    /**
     * @param spec probably is below:
     *             <ul>
     *               <li>{@link ArmyStmtSpec}</li>
     *               <li>{@link SimpleQueries},complex statement,need to migration with clause and inherit outer context</li>
     *             </ul>,
     *              if non-nul,then outerBracketContext and leftContext both are null.
     */
    static CriteriaContext primaryValuesContext(@Nullable ArmyStmtSpec spec,
                                                @Nullable CriteriaContext outerBracketContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param spec         <ul>
     *                     <li>If null,then outerContext non-null.</li>
     *                     <li>If non-null,then outerContext null and leftContext null.</li>
     *                     </ul>
     * @param outerContext <ul>
     *                     <li>If null,then spec non-null and leftContext null.</li>
     *                     <li>If non-null,then spec null.</li>
     *                     </ul>
     */
    static CriteriaContext subValuesContext(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext) {
        throw new UnsupportedOperationException();
    }

    static CriteriaContext otherPrimaryContext() {
        return new OtherPrimaryContext();
    }

    static CriteriaContext deriveTableFunctionContext() {
        return new DerivedTableFunctionContext(ContextStack.peek());
    }


    static CriteriaContext dispatcherContext(final @Nullable CriteriaContext outerContext,
                                             final @Nullable CriteriaContext leftContext) {
        final CriteriaContext dispatcherContext;
        if (outerContext == null) {
            dispatcherContext = new PrimaryDispatcherContext(leftContext);
        } else {
            dispatcherContext = new SubDispatcherContext(outerContext, leftContext);
        }
        return dispatcherContext;
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
            if (ref.selection.selectionName().equals(alias)) {
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

    /**
     * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
     */
    private static void migrateContext(final StatementContext context, final StatementContext migrated) {
        context.withCteContext = migrated.withCteContext;
        context.varMap = migrated.varMap;
        context.refCteMap = migrated.refCteMap;
        context.endListenerList = migrated.endListenerList;

        migrated.withCteContext = null;
        migrated.varMap = null;
        migrated.refCteMap = null;
        migrated.endListenerList = null;

        if (migrated instanceof DispatcherContext) {
            ((DispatcherContext) migrated).migrated = true;
        }

    }

    /**
     * @see #primaryQueryContext(ArmyStmtSpec, CriteriaContext, CriteriaContext)
     * @see #subQueryContext(ArmyStmtSpec, CriteriaContext, CriteriaContext)
     * @see #migrateContext(StatementContext, StatementContext)
     */
    private static void migrateToQueryContext(final SimpleQueryContext queryContext, final DispatcherContext migrated) {
        ((JoinableContext) queryContext).aliasFieldMap = migrated.aliasFieldMap;
        ((JoinableContext) queryContext).aliasToRefDerivedField = migrated.aliasToRefDerivedField;
        ((JoinableContext) queryContext).refOuter = migrated.refOuter;
        ((JoinableContext) queryContext).fieldsFromSubContext = migrated.fieldsFromSubContext;

        queryContext.refWindowNameMap = migrated.refWindowNameMap;
        queryContext.refSelectionMap = migrated.refSelectionMap;


        migrated.aliasFieldMap = null;
        migrated.aliasToRefDerivedField = null;
        migrated.refWindowNameMap = null;
        migrated.fieldsFromSubContext = null;

        migrated.refSelectionMap = null;
        migrated.migrated = true;

    }


    private static void assertNonQueryContext(final DispatcherContext dispatcherContext) {
        if (dispatcherContext.aliasFieldMap != null) {
            String m = String.format("error,couldn't create %s before command.", QualifiedField.class.getName());
            throw ContextStack.criteriaError(dispatcherContext, m);
        } else if (dispatcherContext.aliasToRefDerivedField != null) {
            throw ContextStack.criteriaError(dispatcherContext,
                    createNotFoundAllDerivedFieldMessage(dispatcherContext.aliasToRefDerivedField)
            );
        } else if (dispatcherContext.refWindowNameMap != null) {
            throw unknownWindows(dispatcherContext, dispatcherContext.refWindowNameMap);
        } else if (dispatcherContext.fieldsFromSubContext != null) {
            throw unknownQualifiedFields(dispatcherContext, dispatcherContext.fieldsFromSubContext);
        }

    }

    private static CriteriaException notFoundOuterContext(CriteriaContext context) {
        String m = String.format("current %s no outer context", context);
        throw ContextStack.criteriaError(context, m);
    }

    /**
     * @param spec the stmt that is migrated.
     * @return get outer context from the dispatcher statement that is migrated for new statement.
     */
    @Nullable
    private static CriteriaContext outerContextFromArmyStmt(@Nullable ArmyStmtSpec spec) {
        final CriteriaContext outerContext;
        if (spec == null) {
            outerContext = null;
        } else {
            outerContext = spec.getContext().getOuterContext();
        }
        return outerContext;
    }


    private static CriteriaException invalidRef(CriteriaContext context, String derivedAlias, String fieldName) {
        String m = String.format("ref of %s.%s is invalid.", derivedAlias, fieldName);
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

    /**
     * @see JoinableContext#validateQualifiedFieldMap()
     * @see StatementContext#validateFieldFromSubContext(QualifiedField)
     */
    private static UnknownQualifiedFieldException unknownQualifiedFields(final @Nullable CriteriaContext currentContext,
                                                                         final Collection<QualifiedField<?>> fields) {
        final StringBuilder builder = new StringBuilder();
        builder.append("unknown ")
                .append(QualifiedField.class.getName())
                .append(" :");

        int count = 0;
        for (QualifiedField<?> field : fields) {
            if (count > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append('\n');
            builder.append(field);

            count++;
        }

        final UnknownQualifiedFieldException e;
        if (currentContext == null) {
            e = ContextStack.clearStackAnd(UnknownQualifiedFieldException::new, builder.toString());
        } else {
            e = ContextStack.criteriaError(currentContext, UnknownQualifiedFieldException::new, builder.toString());
        }
        return e;
    }

    /**
     * @see JoinableContext#validateQualifiedFieldMap()
     * @see StatementContext#validateFieldFromSubContext(QualifiedField)
     */
    private static UnknownQualifiedFieldException unknownQualifiedField(@Nullable CriteriaContext currentContext,
                                                                        QualifiedField<?> field) {
        final UnknownQualifiedFieldException e;
        final String m = String.format("unknown %s", field);
        if (currentContext == null) {
            e = ContextStack.clearStackAnd(UnknownQualifiedFieldException::new, m);
        } else {
            e = ContextStack.criteriaError(currentContext, UnknownQualifiedFieldException::new, m);
        }
        return e;
    }


    private static CriteriaException unknownCte(CriteriaContext currentContext, @Nullable String cteName) {
        String m = String.format("unknown cte[%s]", cteName);
        return ContextStack.criteriaError(currentContext, m);
    }

    private static UnknownDerivedFieldException unknownDerivedField(String derivedAlias, String selectionAlias) {
        String m = String.format("unknown outer derived field[%s.%s].", derivedAlias, selectionAlias);
        throw ContextStack.clearStackAnd(UnknownDerivedFieldException::new, m);
    }


    private static UnknownDerivedFieldException unknownRestDerivedField(CriteriaContext context
            , Map<String, Map<String, RefDerivedField>> aliasToRefSelection) {
        return ContextStack.criteriaError(context, UnknownDerivedFieldException::new,
                createNotFoundAllDerivedFieldMessage(aliasToRefSelection));
    }

    private static UnknownDerivedFieldException unknownDerivedFields(CriteriaContext context, Map<String, RefDerivedField> map) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Not found derived field[");
        appendFoundDerivedFieldsMessage(map, builder, 0);
        builder.append(']');
        return ContextStack.criteriaError(context, UnknownDerivedFieldException::new,
                builder.toString());
    }

    private static UnknownFieldGroupException unknownFieldDerivedGroups(final CriteriaContext currentContext,
                                                                        final Collection<? extends _SelectionGroup> groupList) {
        final StringBuilder builder = new StringBuilder()
                .append("Not found ")
                .append('[');
        int count = 0;
        for (_SelectionGroup group : groupList) {
            if (count > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(group.tableAlias());
            count++;
        }
        builder.append(']');
        return ContextStack.criteriaError(currentContext, UnknownFieldGroupException::new, builder.toString());
    }

    private static String createNotFoundAllDerivedFieldMessage(Map<String, Map<String, RefDerivedField>> aliasToRefSelection) {
        final StringBuilder builder = new StringBuilder()
                .append("Not found derived field[");
        int count = 0;
        for (Map<String, RefDerivedField> map : aliasToRefSelection.values()) {
            count = appendFoundDerivedFieldsMessage(map, builder, count);
        }
        builder.append(']');
        return builder.toString();
    }

    private static int appendFoundDerivedFieldsMessage(final Map<String, RefDerivedField> map,
                                                       final StringBuilder builder, int count) {
        for (RefDerivedField s : map.values()) {
            if (count > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(s.tableName)
                    .append(_Constant.POINT)
                    .append(s.fieldName);
            count++;
        }
        return count;
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

    private interface PrimaryContext {

    }

    private interface SubContext {

    }


    private static final class WithCteContext {
        private final boolean recursive;

        private String currentName;

        private List<String> currentAliasList;

        private List<_Cte> cteList;

        private Map<String, _Cte> cteMap;

        private Map<String, RecursiveCte> recursiveCteMap;

        private WithCteContext(boolean recursive) {
            this.recursive = recursive;
        }

    }//WithClauseContext


    private static abstract class StatementContext implements CriteriaContext {

        final CriteriaContext outerContext;

        private Map<String, VarExpression> varMap;

        private Map<String, RecursiveCte> refCteMap;

        private List<Runnable> endListenerList;

        private WithCteContext withCteContext;


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
        public final CriteriaContext onBeforeWithClause(final boolean recursive) {
            if (this.withCteContext != null) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.withCteContext = new WithCteContext(recursive);
            return this;
        }

        @Override
        public final void onStartCte(final @Nullable String name) {
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;
            if (withContext.currentName != null) {
                String m = String.format("Cte[%s] don't end,couldn't start new Cte[%s]", withContext.currentName, name);
                throw ContextStack.criteriaError(this, m);
            } else if (name == null) {
                throw ContextStack.nullPointer(this);
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
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;
            final String currentName = withContext.currentName;
            assert currentName != null && currentName.equals(name);
            withContext.currentAliasList = columnAliasList;
        }


        @Override
        public final void onAddCte(final _Cte cte) {
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;

            final String currentName = withContext.currentName;
            assert currentName != null;
            final List<String> columnAliasList = withContext.currentAliasList;

            if (!currentName.equals(cte.name())) {
                throw ContextStack.castCriteriaApi(this);
            } else if (columnAliasList == null) {
                assert cte.columnAliasList().size() == 0;
            } else {
                assert columnAliasList == cte.columnAliasList();//same instance
            }

            Map<String, _Cte> cteMap = withContext.cteMap;
            List<_Cte> cteList = withContext.cteList;
            if (cteMap == null) {
                cteMap = new HashMap<>();
                withContext.cteMap = cteMap;
                cteList = new ArrayList<>();
                withContext.cteList = cteList;
            } else if (!(cteMap instanceof HashMap)) {
                // with clause end
                throw ContextStack.castCriteriaApi(this);
            }

            if (cteMap.putIfAbsent(currentName, cte) != null) {
                String m = String.format("Cte[%s] duplication", currentName);
                throw ContextStack.criteriaError(this, m);
            }
            cteList.add(cte);

            final Map<String, RecursiveCte> recursiveCteMap = withContext.recursiveCteMap;
            final RecursiveCte recursiveCte;
            final String errorMsg;
            if (recursiveCteMap != null
                    && (recursiveCte = recursiveCteMap.remove(currentName)) != null
                    && (errorMsg = recursiveCte.onRecursiveCteEnd(cte)) != null) {
                throw ContextStack.criteriaError(this, UnknownDerivedFieldException::new, errorMsg);
            }

            // clear current for next cte
            withContext.currentName = null;
            withContext.currentAliasList = null;

        }

        @Override
        public final List<_Cte> getCteList() {
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;
            final List<_Cte> cteList = withContext.cteList;
            assert !(cteList == null || cteList instanceof ArrayList);
            return cteList;
        }

        @Override
        public final boolean isWithRecursive() {
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;
            return withContext.recursive;
        }

        @Override
        public final List<_Cte> endWithClause(final boolean recursive, final boolean required) {
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;

            final String currentName = withContext.currentName;
            if (currentName != null) {
                String m = String.format("Cte[%s] don't end,couldn't end WITH clause.", currentName);
                throw ContextStack.criteriaError(this, m);
            }

            final Map<String, RecursiveCte> recursiveCteMap = withContext.recursiveCteMap;
            if (recursiveCteMap != null && recursiveCteMap.size() > 0) {
                final StringBuilder builder = new StringBuilder()
                        .append("recursive ctes[");
                int count = 0;
                for (String name : recursiveCteMap.keySet()) {
                    if (count > 0) {
                        builder.append(_Constant.SPACE_COMMA_SPACE);
                    }
                    builder.append(name);
                    count++;
                }
                builder.append(']');
                throw ContextStack.criteriaError(this, builder.toString());
            }

            final Map<String, _Cte> cteMap = withContext.cteMap;
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
                cteList = _Collections.unmodifiableList(cteList);
                withContext.cteMap = Collections.unmodifiableMap(cteMap);
            } else {
                throw ContextStack.castCriteriaApi(this);
            }
            withContext.cteList = cteList;
            return cteList;
        }


        @Override
        public final _Cte refCte(final @Nullable String cteName) {
            if (cteName == null) {
                throw ContextStack.nullPointer(this);
            }
            final WithCteContext withContext = this.withCteContext;
            final CriteriaContext outerContext = this.outerContext;
            if (withContext == null) {
                if (outerContext == null) {
                    throw unknownCte(this, cteName);
                }
                return outerContext.refCte(cteName);
            }

            final _Cte thisLevelCte;
            final String currentName = withContext.currentName;
            if (currentName != null && currentName.equals(cteName)) {
                if (!withContext.recursive) {
                    String m;
                    m = String.format("Non-recursive with clause,cte[%s] couldn't recursive-referencing", cteName);
                    throw ContextStack.criteriaError(this, m);
                }
                Map<String, RecursiveCte> recursiveCteMap = withContext.recursiveCteMap;
                if (recursiveCteMap == null) {
                    recursiveCteMap = new HashMap<>();
                    withContext.recursiveCteMap = recursiveCteMap;
                }
                thisLevelCte = recursiveCteMap.computeIfAbsent(cteName, RecursiveCte::new);
            } else {
                final Map<String, _Cte> cteMap = withContext.cteMap;
                if (cteMap == null) {
                    throw unknownCte(this, cteName);
                }
                thisLevelCte = cteMap.get(cteName);
            }
            final _Cte cte;
            if (thisLevelCte != null) {
                cte = thisLevelCte;
            } else if (outerContext == null) {
                throw unknownCte(this, cteName);
            } else {
                cte = outerContext.refCte(cteName);
            }
            return cte;
        }

        @Override
        public final VarExpression createVar(String name, TypeMeta paramMeta) throws CriteriaException {
            //TODO
            throw new UnsupportedOperationException();
        }

        @Override
        public final VarExpression var(String name) throws CriteriaException {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<_TabularBlock> endContext() {
            final Map<String, RecursiveCte> refCteMap = this.refCteMap;
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
        public List<Selection> flatSelectItems() {
            throw ContextStack.criteriaError(this, "current context don't support refAllSelection()");
        }

        @Override
        public Selection selection(String alias) {
            String m = String.format("current context don't support selection(alias[%s])", alias);
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public CriteriaContext getLeftContext() {
            throw ContextStack.criteriaError(this, "current context don't support getLeftContext()");
        }

        @Override
        public CriteriaContext getNonNullLeftContext() {
            throw ContextStack.criteriaError(this, "current context don't support getNonNullLeftContext()");
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
        public boolean isSelectionMap(String derivedAlias) {
            String m = "current context don't support isDerivedTable(derivedAlias)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public DerivedField refThis(String derivedAlias, String selectionAlias) {
            String m = "current context don't support refThis(derivedAlias,selectionAlias)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
            String m = "current context don't support field(tableAlias,field)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public DerivedField refOuter(String derivedAlias, String fieldName) {
            String m = "current context don't support refOuter(derivedAlias,fieldName)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public void onOrderByStart() {
            //no-op
        }

        @Override
        public SimpleExpression refSelection(String selectionAlias) {
            String m = "current context don't support refSelection(selectionAlias)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public SimpleExpression refSelection(int selectionOrdinal) {
            String m = "current context don't support refSelection(int selectionOrdinal)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public void onSetInnerContext(CriteriaContext innerContext) {
            String m = "current context don't support onSetInnerContext(innerContext)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public void bufferNestedDerived(_AliasDerivedBlock block) {
            String m = "current context don't support bufferNestedDerived(ArmyDerivedBlock,block)";
            throw ContextStack.criteriaError(this, m);
        }


        @Override
        public void onAddBlock(_TabularBlock block) {
            String m = "current context don't support onAddBlock(block)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public _TabularBlock lastBlock() {
            String m = "current context don't support lastBlock()";
            throw ContextStack.criteriaError(this, m);
        }


        @Override
        public void endContextBeforeCommand() {
            String m = "current context don't support endContextBeforeSelect()";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public List<? extends _SelectItem> selectItemList() {
            String m = "current context don't support selectionList()";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public void validateFieldFromSubContext(QualifiedField<?> field) {
            throw unknownQualifiedField(this, field);
        }

        @Override
        public void singleDmlTable(TableMeta<?> table, String tableAlias) {
            String m = "current context don't support singleDmlTable(TableMeta<?> table, String tableAlias)";
            throw ContextStack.criteriaError(this, m);
        }


        @Override
        public void insertRowAlias(TableMeta<?> table, String rowAlias) {
            String m = "current context don't support insertRowAlias(TableMeta<?> table, String rowAlias)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public Expression insertValueField(FieldMeta<?> field, Function<FieldMeta<?>, Expression> function) {
            String m = "current context don't support insertValueField(FieldMeta<?> field, Function<FieldMeta<?>, Expression> function)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public void insertColumnList(List<FieldMeta<?>> columnlist) {
            String m = "current context don't support insertColumnList(List<FieldMeta<?>> columnlist)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public final String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(this.getClass().getSimpleName())
                    .append("[hash:")
                    .append(System.identityHashCode(this))
                    .append(",outerContext:");
            final CriteriaContext outerContext = this.outerContext;
            if (outerContext == null) {
                builder.append("null");
            } else {
                builder.append(outerContext.getClass().getSimpleName());
            }
            return builder.append(']')
                    .toString();
        }

        List<_TabularBlock> onEndContext() {
            return Collections.emptyList();
        }

        final boolean isInWithClause() {
            final WithCteContext withContext = this.withCteContext;
            return withContext != null && (withContext.cteList == null || withContext.cteList instanceof ArrayList);
        }


    }//StatementContext


    private static abstract class JoinableContext extends StatementContext {

        /**
         * buffer for column alias clause
         */
        private Map<String, _AliasDerivedBlock> nestedDerivedBufferMap;

        /**
         * buffer for column alias clause
         */
        private _AliasDerivedBlock bufferDerivedBlock;

        private List<_TabularBlock> tableBlockList;

        private Map<String, _TabularBlock> aliasToBlock;

        private Map<String, Map<String, RefDerivedField>> aliasToRefDerivedField;

        private Map<String, Map<String, DerivedField>> aliasToDerivedField;

        /**
         * can't validate field,because field possibly from outer context,{@link _SqlContext} validate this.
         */
        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap;

        private List<QualifiedField<?>> fieldsFromSubContext;

        private boolean refOuter;

        private JoinableContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


        @Override
        public final void bufferNestedDerived(final _AliasDerivedBlock block) {
            if (this.isEndContext()) {
                throw ContextStack.castCriteriaApi(this);
            }
            Map<String, _AliasDerivedBlock> nestedDerivedBufferMap = this.nestedDerivedBufferMap;
            if (nestedDerivedBufferMap == null) {
                nestedDerivedBufferMap = _Collections.hashMap();
                this.nestedDerivedBufferMap = nestedDerivedBufferMap;
            }
            if (nestedDerivedBufferMap.putIfAbsent(block.alias(), block) != null) {
                throw ContextStack.criteriaError(this, _Exceptions::tableAliasDuplication, block.alias());
            }
        }


        @Override
        public final void onAddBlock(final _TabularBlock block) {
            //1. flush bufferDerivedBlock
            this.flushBufferDerivedBlock();

            final TabularItem tableItem;
            if (block instanceof _AliasDerivedBlock) {
                // buffer for column alias clause
                this.bufferDerivedBlock = (_AliasDerivedBlock) block;
                if (this.aliasToBlock == null && this instanceof SimpleQueryContext) {
                    ((SimpleQueryContext) this).endSelectClauseIfNeed();
                }
            } else if ((tableItem = block.tableItem()) instanceof _NestedItems) {
                removeNestedDerivedBuffer((_NestedItems) tableItem);
                this.addTableBlock(block);
            } else {
                this.addTableBlock(block);
            }

        }


        @Override
        public final TableMeta<?> getTable(final String tableAlias) {
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                return null;
            }
            final _TabularBlock block;
            block = aliasToBlock.get(tableAlias);
            final TabularItem tableItem;
            final TableMeta<?> table;
            if (block != null && ((tableItem = block.tableItem()) instanceof TableMeta)) {
                table = (TableMeta<?>) tableItem;
            } else {
                table = null;
            }
            return table;
        }

        @Override
        public final boolean isSelectionMap(final String derivedAlias) {
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                return false;
            }
            final _TabularBlock block;
            block = aliasToBlock.get(derivedAlias);
            return block != null && block.tableItem() instanceof _SelectionGroup;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> QualifiedField<T> field(final String tableAlias, final FieldMeta<T> field) {
            if (this.isEndContext()) {
                throw ContextStack.castCriteriaApi(this);
            }

            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap = this.aliasFieldMap;
            if (aliasFieldMap == null) {
                aliasFieldMap = _Collections.hashMap();
                this.aliasFieldMap = aliasFieldMap;
            }
            return (QualifiedField<T>) aliasFieldMap.computeIfAbsent(tableAlias, _Collections::hashMapIgnoreKey)
                    .computeIfAbsent(field,
                            alias -> QualifiedFieldImpl.create(tableAlias, field));
        }

        @Override
        public final DerivedField refThis(final String derivedAlias, final String selectionAlias) {
            if (this.isInWithClause()) {
                throw unknownDerivedField(derivedAlias, selectionAlias);
            }

            //1. flush buffer _DerivedBlock
            this.flushBufferDerivedBlock();

            final Map<String, _AliasDerivedBlock> nestedDerivedBufferMap = this.nestedDerivedBufferMap;
            final Map<String, _TabularBlock> aliasToBlock;
            //2. get SelectionMap of last block
            final _SelectionMap selectionMap;
            final TabularItem tabularItem;
            _TabularBlock block;
            if (nestedDerivedBufferMap != null
                    && (block = nestedDerivedBufferMap.get(derivedAlias)) != null) {
                selectionMap = (_AliasDerivedBlock) block;
            } else if ((aliasToBlock = this.aliasToBlock) == null) {
                selectionMap = null;
            } else if (!(aliasToBlock instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this);
            } else if ((block = aliasToBlock.get(derivedAlias)) == null) {
                selectionMap = null;
            } else if (block instanceof _AliasDerivedBlock) {
                selectionMap = (_AliasDerivedBlock) block;
            } else if ((tabularItem = block.tableItem()) instanceof _SelectionMap) {
                selectionMap = (_SelectionMap) tabularItem;
            } else {
                throw invalidRef(this, derivedAlias, selectionAlias);
            }

            //3. get DerivedField
            final DerivedField field;
            if (selectionMap == null) {
                field = getRefField(derivedAlias, selectionAlias, true);
                assert field != null;
            } else {
                final DerivedField temp;
                temp = getRefField(derivedAlias, selectionAlias, false);
                if (temp == null) {
                    field = getDerivedField(selectionMap, derivedAlias, selectionAlias);
                } else {
                    field = temp;
                }
            }
            return field;
        }

        @Override
        public final DerivedField refOuter(final String derivedAlias, final String fieldName) {
            final CriteriaContext outerContext = this.outerContext;
            if (outerContext == null || this instanceof PrimaryContext) {
                throw notFoundOuterContext(this);
            }
            this.refOuter = true;
            return outerContext.refThis(derivedAlias, fieldName);
        }


        @Override
        public final _TabularBlock lastBlock() {
            _TabularBlock block = this.bufferDerivedBlock;
            if (block != null) {
                return block;
            }
            final List<_TabularBlock> blockList = this.tableBlockList;
            final int size;
            if (blockList == null || (size = blockList.size()) == 0) {
                throw ContextStack.castCriteriaApi(this);
            }
            return blockList.get(size - 1);
        }

        @Override
        public final void endContextBeforeCommand() {
            assert this instanceof SimpleQueryContext;
            if (this.aliasToRefDerivedField != null) {
                String m = String.format("You couldn't reference %s before SELECT clause.", DerivedField.class.getName());
                throw ContextStack.criteriaError(this, m);
            } else if (this.aliasToBlock != null
                    || this.bufferDerivedBlock != null
                    || this.tableBlockList != null
                    || this.aliasFieldMap != null
                    || ((SimpleQueryContext) this).selectItemList != null) {
                throw ContextStack.castCriteriaApi(this);
            } else if (((SimpleQueryContext) this).refWindowNameMap != null) {
                String m = String.format("You couldn't reference %s before SELECT clause.", Window.class.getName());
                throw ContextStack.criteriaError(this, m);
            }

            ((SimpleQueryContext) this).selectItemList = Collections.emptyList();
        }

        @Override
        public final void validateFieldFromSubContext(final QualifiedField<?> field) {
            if (this.isInWithClause()) {
                throw unknownQualifiedField(null, field);
            }
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            final _TabularBlock block;
            final boolean notExists;

            final String tableAlias;
            tableAlias = field.tableAlias();
            if (aliasToBlock != null && (block = aliasToBlock.get(tableAlias)) != null) {
                notExists = block.tableItem() != field.tableMeta();
            } else if (this instanceof JoinableSingleDmlContext) {
                boolean aliasSame;
                aliasSame = tableAlias.equals(((JoinableSingleDmlContext) this).tableAlias);
                notExists = aliasSame && field.tableMeta() != ((JoinableSingleDmlContext) this).table;
                if (aliasSame && notExists) {
                    throw unknownQualifiedField(null, field);
                }
            } else {
                notExists = true;
            }

            if (notExists) {
                List<QualifiedField<?>> list = this.fieldsFromSubContext;
                if (list == null) {
                    list = _Collections.arrayList();
                    this.fieldsFromSubContext = list;
                }
                list.add(field);
            }

        }

        @Override
        final List<_TabularBlock> onEndContext() {

            //1. assert not duplication
            if (this.isEndContext()) {
                //no bug,never here
                throw ContextStack.castCriteriaApi(this);
            }

            //2. flush bufferDerivedBlock
            this.flushBufferDerivedBlock();

            final Map<String, _TabularBlock> aliasToBlock;
            aliasToBlock = _Collections.safeUnmodifiableMap(this.aliasToBlock);
            this.aliasToBlock = aliasToBlock;

            final List<_TabularBlock> blockList;
            blockList = _Collections.safeUnmodifiableList(this.tableBlockList);
            this.tableBlockList = blockList;//store for recursive checking


            //3. assert nestedDerivedBufferMap
            final Map<String, _AliasDerivedBlock> nestedDerivedBufferMap = this.nestedDerivedBufferMap;
            if (nestedDerivedBufferMap != null && nestedDerivedBufferMap.size() > 0) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.nestedDerivedBufferMap = null; //clear


            //4. validate aliasToBlock
            final int blockSize = blockList.size();
            if (blockSize == 0 && !(this instanceof SimpleQueryContext || this instanceof JoinableSingleDmlContext)) {
                throw ContextStack.castCriteriaApi(this);
            }
            assert aliasToBlock.size() >= blockSize;// nested items


            //5. validate aliasToRefSelection
            if (this.aliasToRefDerivedField != null) {
                this.validateDerivedFieldMap();
            }

            //6. validate QualifiedField map
            if (this.aliasFieldMap != null) {
                this.validateQualifiedFieldMap();
            }

            //7. validate field from sub context
            if (this.fieldsFromSubContext != null) {
                this.validateQualifiedFieldFromSub();
            }

            //8. clear this.aliasToDerivedField;
            final Map<String, Map<String, DerivedField>> aliasToDerivedField = this.aliasToDerivedField;
            if (aliasToDerivedField != null) {
                aliasToDerivedField.clear();
                this.aliasToDerivedField = null;//clear
            }

            // end SimpleQueryContext
            if (this instanceof SimpleQueryContext) {
                ((SimpleQueryContext) this).endQueryContext();
            }
            return blockList;
        }


        /**
         * @see #onEndContext()
         */
        private void validateDerivedFieldMap() {
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            final Map<String, Map<String, RefDerivedField>> aliasToRefDerivedField = this.aliasToRefDerivedField;
            assert aliasToRefDerivedField != null;
            if (aliasToBlock == null) {
                throw unknownRestDerivedField(this, aliasToRefDerivedField);
            }

            _TabularBlock block;
            TabularItem tabularItem;
            Map<String, RefDerivedField> refFieldMap;
            for (String itemAlias : aliasToRefDerivedField.keySet()) {
                block = aliasToBlock.get(itemAlias);
                if (block == null) {
                    throw unknownDerivedFields(this, aliasToRefDerivedField.get(itemAlias));
                }
                tabularItem = block.tableItem();
                if (!(tabularItem instanceof RecursiveCte)) {
                    continue;
                }
                refFieldMap = aliasToRefDerivedField.remove(itemAlias);
                if (refFieldMap != null && refFieldMap.size() > 0) {
                    ((RecursiveCte) tabularItem).addRefFields(refFieldMap.values());
                }
            }

            if (aliasToRefDerivedField.size() > 0) {
                throw unknownRestDerivedField(this, aliasToRefDerivedField);
            }
            this.aliasToRefDerivedField = null;//clear
        }


        /**
         * @see #onEndContext()
         */
        private void validateQualifiedFieldMap() {
            final Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap = this.aliasFieldMap;
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            assert aliasFieldMap != null;

            final CriteriaContext outerContext = this.outerContext;

            String tableAlias;
            TableMeta<?> firstFieldTable;
            _TabularBlock block;
            for (Map.Entry<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasEntry : aliasFieldMap.entrySet()) {

                firstFieldTable = null;
                for (QualifiedField<?> field : aliasEntry.getValue().values()) {
                    if (firstFieldTable != null) {
                        if (field.tableMeta() != firstFieldTable) {
                            throw unknownQualifiedField(this, field);
                        }
                        continue;
                    }

                    firstFieldTable = field.tableMeta();

                    tableAlias = aliasEntry.getKey();
                    if (aliasToBlock != null && (block = aliasToBlock.get(tableAlias)) != null) {
                        if (block.tableItem() != firstFieldTable) {
                            throw unknownQualifiedField(this, field);
                        }
                    } else if (outerContext != null) {
                        this.refOuter = true;
                        outerContext.validateFieldFromSubContext(field);
                    }

                }// inner for


            }// outer for

            aliasFieldMap.clear();
            this.aliasFieldMap = null;

        }

        /**
         * @see #onEndContext()
         */
        private void validateQualifiedFieldFromSub() {
            final List<QualifiedField<?>> fieldList = this.fieldsFromSubContext;
            assert fieldList != null;
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock == null) {
                throw unknownQualifiedFields(this, fieldList);
            }
            _TabularBlock block;
            for (QualifiedField<?> field : fieldList) {
                block = aliasToBlock.get(field.tableAlias());
                if (block == null || block.tableItem() != field.tableMeta()) {
                    throw unknownQualifiedField(this, field);
                }
            }

            fieldList.clear();
            this.fieldsFromSubContext = null;

        }


        final boolean isEndContext() {
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            return !(aliasToBlock == null || aliasToBlock instanceof HashMap);
        }


        /**
         * @see #onAddBlock(_TabularBlock)
         */
        private void addTableBlock(final _TabularBlock block) {
            Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            List<_TabularBlock> tableBlockList = this.tableBlockList;
            if (aliasToBlock == null) {
                assert tableBlockList == null;
                aliasToBlock = _Collections.hashMap();
                this.aliasToBlock = aliasToBlock;
                tableBlockList = _Collections.arrayList();
                this.tableBlockList = tableBlockList;

                if (this instanceof SimpleQueryContext) {
                    ((SimpleQueryContext) this).endSelectClauseIfNeed();
                }
            } else if (!(aliasToBlock instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this);
            }

            final int blockSize;
            blockSize = tableBlockList.size();

            final TabularItem tableItem = block.tableItem();
            String alias = block.alias();

            if (tableItem instanceof _Cte) {
                if ("".equals(alias)) {
                    alias = ((_Cte) tableItem).name();//modify alias
                } else if (!_StringUtils.hasText(alias)) {
                    throw ContextStack.criteriaError(this, _Exceptions::tableItemAliasNoText, tableItem);
                }
            }
            if (tableItem instanceof _NestedItems) {
                this.addNestedItems((_NestedItems) tableItem);
            } else if (!_StringUtils.hasText(alias)) {
                throw ContextStack.criteriaError(this, _Exceptions::tableItemAliasNoText, tableItem);
            } else if (aliasToBlock.putIfAbsent(alias, block) != null) {
                throw ContextStack.criteriaError(this, _Exceptions::tableAliasDuplication, alias);
            } else if (tableItem instanceof RecursiveCte) {
                this.onAddRecursiveCte((RecursiveCte) tableItem, alias);
            } else if (tableItem instanceof DerivedTable || tableItem instanceof _Cte) {
                this.onAddDerived(block, (_SelectionMap) tableItem, alias);
            } else if (tableItem instanceof TableMeta) {
                if (this instanceof SimpleQueryContext) {
                    ((SimpleQueryContext) this).onAddTabularItem(tableItem, alias);
                }
            }

            assert tableBlockList.size() == blockSize; // avoid bug
            tableBlockList.add(block); //add to list

        }


        /**
         * @see #onAddBlock(_TabularBlock)
         */
        private void removeNestedDerivedBuffer(final _NestedItems nestedItems) {
            final Map<String, _AliasDerivedBlock> nestedDerivedBufferMap = this.nestedDerivedBufferMap;
            TabularItem tabularItem;
            for (_TabularBlock block : nestedItems.tableBlockList()) {

                if (block instanceof _AliasDerivedBlock) {
                    if (nestedDerivedBufferMap == null
                            || nestedDerivedBufferMap.remove(block.alias()) != block) {
                        String m = String.format("%s[%s] no buffer", DerivedTable.class.getName(), block.alias());
                        throw ContextStack.criteriaError(this, m);
                    }

                    continue;
                }

                tabularItem = block.tableItem();
                if (tabularItem instanceof _NestedItems) {
                    this.removeNestedDerivedBuffer((_NestedItems) tabularItem);
                }

            }

        }

        /**
         * @see #onAddBlock(_TabularBlock)
         * @see #refThis(String, String)
         * @see #onEndContext()
         */
        private void flushBufferDerivedBlock() {
            final _AliasDerivedBlock bufferDerivedBlock = this.bufferDerivedBlock;
            if (bufferDerivedBlock != null) {
                this.bufferDerivedBlock = null;
                this.addTableBlock(bufferDerivedBlock);
            }
        }

        /**
         * @see #addTableBlock(_TabularBlock)
         * @see #addNestedItems(_NestedItems)
         */
        private void onAddDerived(final _TabularBlock block, final _SelectionMap derivedTable, final String alias) {
            if (derivedTable instanceof DerivedTable && derivedTable instanceof CriteriaContextSpec) {
                final CriteriaContext context = ((CriteriaContextSpec) derivedTable).getContext();
                if (context.getOuterContext() != this) {
                    String m = String.format("%s[%s] context not match.", DerivedTable.class.getSimpleName(), alias);
                    throw ContextStack.criteriaError(this, CriteriaException::new, m);
                } else if (context instanceof JoinableContext
                        && ((JoinableContext) context).refOuter
                        && (!(block instanceof _ModifierTabularBlock) || ((_ModifierTabularBlock) block).modifier() != SQLs.LATERAL)) {
                    String m = String.format("DerivedTable[%s] isn't LATERAL,couldn't reference outer field.", alias);
                    throw ContextStack.criteriaError(this, NonLateralException::new, m);
                }
            }
            final Map<String, Map<String, RefDerivedField>> aliasToRefSelection = this.aliasToRefDerivedField;
            if (aliasToRefSelection != null) {
                final Map<String, RefDerivedField> fieldMap;
                fieldMap = aliasToRefSelection.remove(alias);
                if (fieldMap != null) {
                    this.finishRefSelections(derivedTable, alias, fieldMap);
                    fieldMap.clear();
                }
                if (aliasToRefSelection.size() == 0) {
                    this.aliasToRefDerivedField = null;
                }
            }

            if (this instanceof SimpleQueryContext) {
                ((SimpleQueryContext) this).onAddTabularItem((TabularItem) derivedTable, alias);
            }

        }

        /**
         * @see #addTableBlock(_TabularBlock)
         * @see #addNestedItems(_NestedItems)
         */
        private void onAddRecursiveCte(final RecursiveCte cte, final String cteAlias) {
            final Map<String, Map<String, RefDerivedField>> aliasToRefSelection = this.aliasToRefDerivedField;
            final Map<String, RefDerivedField> fieldMap;
            if (aliasToRefSelection != null && (fieldMap = aliasToRefSelection.remove(cteAlias)) != null) {
                cte.addRefFields(fieldMap.values());
            }

            if (this instanceof SimpleQueryContext) {
                ((SimpleQueryContext) this).onAddTabularItem(cte, cteAlias);
            }

        }


        /**
         * @param derivedTable {@link _DerivedTable} or {@link _Cte},but not {@link RecursiveCte}
         * @see #onAddDerived(_TabularBlock, _SelectionMap, String)
         */
        private void finishRefSelections(final _SelectionMap derivedTable, final String tableAlias,
                                         final Map<String, RefDerivedField> fieldMap) {
            assert !(derivedTable instanceof RecursiveCte)
                    && (derivedTable instanceof _DerivedTable || derivedTable instanceof _Cte);

            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            if (aliasToSelection == null) {
                aliasToSelection = _Collections.hashMap();
                this.aliasToDerivedField = aliasToSelection;
            }
            final Map<String, DerivedField> derivedFieldMap;
            derivedFieldMap = aliasToSelection.computeIfAbsent(tableAlias, _Collections::hashMapIgnoreKey);

            Selection selection;
            for (RefDerivedField field : fieldMap.values()) {
                selection = derivedTable.refSelection(field.fieldName);
                if (selection == null) {
                    throw invalidRef(this, tableAlias, field.fieldName);
                }
                assert field.expType.selection == null;
                field.expType.selection = selection;
                derivedFieldMap.putIfAbsent(field.fieldName, field);
            }

        }


        @Nullable
        private RefDerivedField getRefField(final String derivedTableAlias, final String fieldName,
                                            final boolean create) {
            Map<String, Map<String, RefDerivedField>> aliasToRefDerivedField = this.aliasToRefDerivedField;
            if (aliasToRefDerivedField == null && create) {
                aliasToRefDerivedField = _Collections.hashMap();
                this.aliasToRefDerivedField = aliasToRefDerivedField;
            }

            final Map<String, RefDerivedField> fieldMap;
            final RefDerivedField field;
            if (aliasToRefDerivedField == null) {
                field = null;
            } else if (create) {
                fieldMap = aliasToRefDerivedField.computeIfAbsent(derivedTableAlias, _Collections::hashMapIgnoreKey);
                field = fieldMap.computeIfAbsent(fieldName, k -> new RefDerivedField(derivedTableAlias, k));
            } else {
                fieldMap = aliasToRefDerivedField.get(derivedTableAlias);
                if (fieldMap == null) {
                    field = null;
                } else {
                    field = fieldMap.get(fieldName);
                }
            }
            return field;
        }

        private DerivedField getDerivedField(final _SelectionMap selectionMap, final String tableAlias,
                                             final String fieldName) {
            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            if (aliasToSelection == null) {
                aliasToSelection = _Collections.hashMap();
                this.aliasToDerivedField = aliasToSelection;
            }

            return aliasToSelection.computeIfAbsent(tableAlias, _Collections::hashMapIgnoreKey)
                    .computeIfAbsent(fieldName, fieldNameKey -> {
                        final Selection selection;
                        selection = selectionMap.refSelection(fieldNameKey);
                        if (selection == null) {
                            throw invalidRef(this, tableAlias, fieldNameKey);
                        }
                        return new DerivedSelection(tableAlias, selection);
                    });
        }


        /**
         * <p>
         * add nested {@link TabularItem} to {@link #aliasToBlock}
         * </p>
         *
         * @see #onAddBlock(_TabularBlock)
         * @see #addTableBlock(_TabularBlock)
         */
        private void addNestedItems(final _NestedItems nestedItems) {
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            assert aliasToBlock != null;

            TabularItem tableItem;
            String alias;
            for (_TabularBlock block : nestedItems.tableBlockList()) {
                tableItem = block.tableItem();
                alias = block.alias();
                if (tableItem instanceof _Cte) {
                    if ("".equals(alias)) {
                        alias = ((_Cte) tableItem).name();//modify alias
                    } else if (!_StringUtils.hasText(alias)) {
                        throw ContextStack.criteriaError(this, _Exceptions::tableItemAliasNoText, tableItem);
                    }
                }
                if (tableItem instanceof _NestedItems) {
                    if (_StringUtils.hasText(alias)) {
                        throw ContextStack.criteriaError(this, _Exceptions::nestedItemsAliasHasText, alias);
                    }
                    this.addNestedItems((_NestedItems) tableItem);
                } else if (aliasToBlock.putIfAbsent(alias, block) != null) {
                    throw ContextStack.criteriaError(this, _Exceptions::tableAliasDuplication, alias);
                } else if (tableItem instanceof RecursiveCte) {
                    this.onAddRecursiveCte((RecursiveCte) tableItem, alias);
                } else if (tableItem instanceof DerivedTable || tableItem instanceof _Cte) {
                    // note ,no tableBlockList.
                    this.onAddDerived(block, (_SelectionMap) tableItem, alias);
                } else if (tableItem instanceof TableMeta) {
                    if (this instanceof SimpleQueryContext) {
                        ((SimpleQueryContext) this).onAddTabularItem(tableItem, alias);
                    }
                }

            }


        }


        /**
         * @see #validateQualifiedFieldMap()
         */
        private CriteriaException qualifiedFieldNotMatch(QualifiedField<?> first, QualifiedField<?> field) {
            String m = String.format("%s table and %s table not match.", first, field);
            return ContextStack.criteriaError(this, m);
        }


    }//JoinableContext


    static abstract class InsertContext extends StatementContext {

        private TableMeta<?> insertTable;

        private String tableAlias;

        private String rowAlias;


        private Map<FieldMeta<?>, QualifiedField<?>> qualifiedFieldMap;

        private Map<FieldMeta<?>, Expression> insertValueFieldMap;

        private List<FieldMeta<?>> columnlist;

        private Map<String, FieldMeta<?>> columnMap;

        private InsertContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


        @Override
        public final void singleDmlTable(final TableMeta<?> table, final @Nullable String tableAlias) {
            final TableMeta<?> insertTable = this.insertTable;
            if (insertTable == null) {
                this.insertTable = table;
            } else if (table != insertTable) {
                throw ContextStack.castCriteriaApi(this);
            }

            if (tableAlias == null) {
                throw ContextStack.nullPointer(this);
            } else if (this.tableAlias != null) {
                throw ContextStack.castCriteriaApi(this);
            }

            this.tableAlias = tableAlias;
        }


        @Override
        public final void insertRowAlias(final TableMeta<?> table, final @Nullable String rowAlias) {
            final TableMeta<?> insertTable = this.insertTable;
            if (insertTable == null) {
                this.insertTable = table;
            } else if (table != insertTable) {
                throw ContextStack.castCriteriaApi(this);
            }
            if (rowAlias == null) {
                throw ContextStack.nullPointer(this);
            } else if (this.rowAlias != null) {
                throw ContextStack.castCriteriaApi(this);
            } else if (rowAlias.equals(this.tableAlias)) {
                String m = String.format("INSERT statement row alias[%s] couldn't be equals to table alias[%s]",
                        rowAlias, this.tableAlias);
                throw ContextStack.criteriaError(this, m);
            }
            this.rowAlias = rowAlias;
        }

        @Override
        public final void insertColumnList(final List<FieldMeta<?>> columnlist) {
            if (this.columnlist != null) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.columnlist = columnlist;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> QualifiedField<T> field(final @Nullable String tableAlias, final FieldMeta<T> field) {
            if (tableAlias == null) {
                throw ContextStack.nullPointer(this);
            } else if (!tableAlias.equals(this.rowAlias) && !tableAlias.equals(this.tableAlias)) {
                String m = String.format("unknown %s[%s.%s]", QualifiedField.class.getName(), tableAlias, field);
                throw ContextStack.criteriaError(this, m);
            }
            Map<FieldMeta<?>, QualifiedField<?>> qualifiedFieldMap = this.qualifiedFieldMap;
            if (qualifiedFieldMap == null) {
                qualifiedFieldMap = new HashMap<>();
                this.qualifiedFieldMap = qualifiedFieldMap;
            } else if (!(qualifiedFieldMap instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this);
            }
            QualifiedField<?> qualifiedField;
            qualifiedField = qualifiedFieldMap.get(field);
            if (qualifiedField == null) {
                qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                qualifiedFieldMap.put(field, qualifiedField);
            }
            return (QualifiedField<T>) qualifiedField;
        }

        @Override
        public final Expression insertValueField(final FieldMeta<?> field, final Function<FieldMeta<?>, Expression> function) {
            Map<FieldMeta<?>, Expression> insertValueFieldMap = this.insertValueFieldMap;
            if (insertValueFieldMap == null) {
                insertValueFieldMap = new HashMap<>();
                this.insertValueFieldMap = insertValueFieldMap;
            }
            return insertValueFieldMap.computeIfAbsent(field, function);
        }

        @Override
        public final void validateFieldFromSubContext(final QualifiedField<?> field) {
            final String fieldTableAlias = field.tableAlias();
            if (field.tableMeta() != this.insertTable
                    || !(fieldTableAlias.equals(this.rowAlias) || fieldTableAlias.equals(this.tableAlias))) {
                throw unknownQualifiedField(this, field);
            } else if (this.columnlist != null &&
                    this.getOrCreateColumnMap().get(field.fieldName()) != field.fieldMeta()) {
                throw unknownQualifiedField(this, field);
            }
        }

        @Override
        final List<_TabularBlock> onEndContext() {
            // can't validate field,because field possibly from outer context,{@link _SqlContext} validate this.
            final Map<FieldMeta<?>, QualifiedField<?>> aliasFieldMap = this.qualifiedFieldMap;
            if (aliasFieldMap != null) {
                aliasFieldMap.clear();
                this.qualifiedFieldMap = null;
            }
            final Map<FieldMeta<?>, Expression> insertValueFieldMap = this.insertValueFieldMap;
            if (insertValueFieldMap != null) {
                insertValueFieldMap.clear();
                this.insertValueFieldMap = null;
            }
            return Collections.emptyList();
        }


        private Map<String, FieldMeta<?>> getOrCreateColumnMap() {
            Map<String, FieldMeta<?>> columnMap = this.columnMap;
            if (columnMap != null) {
                return columnMap;
            }
            final List<FieldMeta<?>> columnList = this.columnlist;
            if (columnList == null) {
                columnMap = Collections.emptyMap();
            } else {
                final int columnSize;
                columnSize = columnList.size();

                columnMap = new HashMap<>((int) (columnSize / 0.75F));
                FieldMeta<?> field;
                for (int i = 0; i < columnSize; i++) {
                    field = columnList.get(i);
                    columnMap.put(field.fieldName(), field);
                }
                assert columnMap.size() == columnSize;
                columnMap = Collections.unmodifiableMap(columnMap);

            }
            this.columnMap = columnMap;
            return columnMap;
        }


    }//InsertContext


    private static final class PrimaryInsertContext extends InsertContext implements PrimaryContext {

        /**
         * @see #primaryInsertContext(ArmyStmtSpec)
         */
        private PrimaryInsertContext() {
            super(null);
        }


    }// PrimaryInsertContext


    /**
     * @see #subInsertContext(ArmyStmtSpec, CriteriaContext)
     */
    private static final class SubInsertContext extends InsertContext implements SubContext {

        private SubInsertContext(CriteriaContext outerContext) {
            super(outerContext);
        }

    }//SubInsertContext


    private static abstract class SingleDmlContext extends StatementContext {

        private TableMeta<?> table;

        private String tableAlias;

        private SingleDmlContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }

        @Override
        public final void singleDmlTable(final @Nullable TableMeta<?> table, @Nullable final String tableAlias) {
            if (this.table != null) {
                throw ContextStack.castCriteriaApi(this);
            } else if (table == null) {
                throw ContextStack.nullPointer(this);
            } else if (tableAlias == null) {
                throw ContextStack.nullPointer(this);
            }
            this.table = table;
            this.tableAlias = tableAlias;
        }

        @Override
        public final void validateFieldFromSubContext(final QualifiedField<?> field) {
            if (this.isInWithClause()
                    || field.tableMeta() != this.table
                    || !field.tableAlias().equals(this.tableAlias)) {
                throw unknownQualifiedField(this, field);
            }
        }


    }//SingleDmlContext

    /**
     * @see #primarySingleDmlContext(ArmyStmtSpec)
     */
    private static final class PrimarySingleDmlContext extends SingleDmlContext implements PrimaryContext {

        private PrimarySingleDmlContext(@Nullable PrimaryDispatcherContext outerContext) {
            super(outerContext);
        }

    }//PrimarySingleDmlContext

    /**
     * <p>
     * This class representing multi-table dml context.
     * </p>
     *
     * @since 1.0
     */
    private static abstract class MultiDmlContext extends JoinableContext {

        private MultiDmlContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


    }// MultiDmlContext

    private static final class PrimaryMultiDmlContext extends MultiDmlContext implements PrimaryContext {

        private PrimaryMultiDmlContext() {
            super(null);
        }

    }//PrimaryMultiDmlContext


    private static abstract class JoinableSingleDmlContext extends JoinableContext {

        private TableMeta<?> table;

        private String tableAlias;

        private JoinableSingleDmlContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }

        @Override
        public final void singleDmlTable(final @Nullable TableMeta<?> table, @Nullable final String tableAlias) {
            if (this.table != null) {
                throw ContextStack.castCriteriaApi(this);
            } else if (tableAlias == null) {
                throw ContextStack.nullPointer(this);
            } else if (table == null) {
                throw ContextStack.nullPointer(this);
            }
            this.tableAlias = tableAlias;
            this.table = table;
        }


    }//JoinableSingleDmlContext


    private static final class PrimaryJoinableSingleDmlContext extends JoinableSingleDmlContext
            implements PrimaryContext {

        private PrimaryJoinableSingleDmlContext() {
            super(null);
        }

    }//PrimaryJoinableSingleDmlContext

    private static final class SubJoinableSingleDmlContext extends JoinableSingleDmlContext
            implements SubContext {

        private SubJoinableSingleDmlContext(CriteriaContext outerContext) {
            super(outerContext);
        }

    }//SubJoinableSingleDmlContext


    private static abstract class SimpleQueryContext extends JoinableContext {

        private final CriteriaContext leftContext;


        private List<_SelectItem> selectItemList;

        /**
         * couldn't clear this field
         */
        private List<Selection> flatSelectionList;

        private Map<String, _SelectionGroup> selectionGroupMap;


        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<Object, SimpleExpression> refSelectionMap;

        private Map<String, Boolean> windowNameMap;

        private Map<String, Boolean> refWindowNameMap;

        private boolean orderByStart;

        private SimpleQueryContext(final @Nullable CriteriaContext outerContext,
                                   final @Nullable CriteriaContext leftContext) {
            super(outerContext);
            assert leftContext == null || leftContext.getOuterContext() == outerContext;
            this.leftContext = leftContext;
        }


        @Override
        public final CriteriaContext getLeftContext() {
            return this.leftContext;
        }

        @Override
        public final CriteriaContext getNonNullLeftContext() {
            final CriteriaContext leftContext = this.leftContext;
            assert leftContext != null;
            return leftContext;
        }

        @Override
        public final List<? extends _SelectItem> selectItemList() {
            final List<? extends _SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null || selectItemList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this);
            }
            return selectItemList;
        }


        @Override
        public final CriteriaContext onAddSelectItem(final @Nullable SelectItem selectItem) {
            if (selectItem == null) {
                throw ContextStack.nullPointer(this);
            }

            List<_SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                selectItemList = new ArrayList<>();
                this.selectItemList = selectItemList;
            } else if (!(selectItemList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this);
            }

            selectItemList.add((_SelectItem) selectItem);


            if (selectItem instanceof _SelectionGroup) {
                Map<String, _SelectionGroup> map = this.selectionGroupMap;
                if (map == null) {
                    map = new HashMap<>();
                    this.selectionGroupMap = map;
                }
                final _SelectionGroup group = (_SelectionGroup) selectItem;
                if (map.putIfAbsent(group.tableAlias(), group) != null) {
                    String m = String.format("%s group[%s] duplication", Selection.class.getName(),
                            group.tableAlias());
                    throw ContextStack.criteriaError(this, m);
                }

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
        public final SimpleExpression refSelection(final @Nullable String selectionAlias) {
            if (selectionAlias == null) {
                throw ContextStack.nullPointer(this);
            }
            final CriteriaContext leftContext = this.leftContext;
            if (leftContext != null) {
                return leftContext.refSelection(selectionAlias);
            }
            SimpleExpression refSelection;
            Map<Object, SimpleExpression> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                refSelectionMap = new HashMap<>();
                this.refSelectionMap = refSelectionMap;
            }
            refSelection = refSelectionMap.get(selectionAlias);
            if (refSelection != null) {
                return refSelection;
            }
            if (this.isSelectClauseNotEnd()) {
                refSelection = new DelayNameRefSelection(selectionAlias);
            } else {
                final Selection selection;
                selection = this.getSelectionMap().get(selectionAlias);
                if (selection == null) {
                    throw CriteriaUtils.unknownSelection(this, selectionAlias);
                }
                refSelection = new NameRefSelection(selection);
            }
            refSelectionMap.put(selectionAlias, refSelection);
            return refSelection;
        }

        @Override
        public final SimpleExpression refSelection(final int selectionOrdinal) {
            if (selectionOrdinal < 1) {
                throw CriteriaUtils.unknownSelection(this, selectionOrdinal);
            }
            final CriteriaContext leftContext = this.leftContext;
            if (leftContext != null) {
                return leftContext.refSelection(selectionOrdinal);
            }
            SimpleExpression refSelection;
            Map<Object, SimpleExpression> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                refSelectionMap = new HashMap<>();
                this.refSelectionMap = refSelectionMap;
            }
            refSelection = refSelectionMap.get(selectionOrdinal);
            if (refSelection != null) {
                return refSelection;
            }
            final int selectionIndex = selectionOrdinal - 1;
            if (this.isSelectClauseNotEnd()) {
                refSelection = new DelayIndexRefSelection(selectionIndex);
            } else {
                final List<Selection> selectionList = this.flatSelectionList;
                assert selectionList != null;
                if (selectionOrdinal > selectionList.size()) {
                    throw CriteriaUtils.unknownSelection(this, selectionOrdinal);
                }
                refSelection = new IndexRefSelection(selectionIndex, selectionList.get(selectionIndex));
            }
            refSelectionMap.put(selectionIndex, refSelection);
            return refSelection;
        }

        @Override
        public final Selection selection(final String alias) {
            if (!(this instanceof SubQueryContext)) {
                throw ContextStack.castCriteriaApi(this);
            }
            return this.getSelectionMap().get(alias);
        }

        @Override
        public final List<Selection> flatSelectItems() {
            List<Selection> selectionList = this.flatSelectionList;
            if (selectionList != null) {
                return selectionList;
            }

            final List<_SelectItem> selectItemList = this.selectItemList;
            final int selectItemSize;
            if (selectItemList == null
                    || selectItemList instanceof ArrayList
                    || (selectItemSize = selectItemList.size()) == 0) {
                throw ContextStack.castCriteriaApi(this);
            }

            _SelectItem selectItem;
            if (selectItemSize == 1 && (selectItem = selectItemList.get(0)) instanceof Selection) {
                selectionList = Collections.singletonList((Selection) selectItem);
            } else {
                selectionList = new ArrayList<>(selectItemSize);

                for (int i = 0; i < selectItemSize; i++) {
                    selectItem = selectItemList.get(i);
                    if (selectItem instanceof Selection) {
                        selectionList.add((Selection) selectItem);
                    } else {
                        selectionList.addAll(((_SelectionGroup) selectItem).selectionList());
                    }
                }//for

                selectionList = Collections.unmodifiableList(selectionList);

            }// else
            this.flatSelectionList = selectionList;
            return selectionList;
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
            final Map<String, Boolean> windowNameMap = this.windowNameMap;
            if (windowNameMap != null && windowNameMap.containsKey(windowName)) {
                return;
            }
            Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap == null) {
                refWindowNameMap = new HashMap<>();
                this.refWindowNameMap = refWindowNameMap;
            }
            refWindowNameMap.putIfAbsent(windowName, Boolean.TRUE);
        }

        private void onAddTabularItem(final TabularItem table, final String alias) {
            final Map<String, _SelectionGroup> groupMap = this.selectionGroupMap;

            final _SelectionGroup group;
            if (groupMap == null || (group = groupMap.remove(alias)) == null) {
                return;
            }

            if (group instanceof _SelectionGroup._TableFieldGroup) {
                if (!(table instanceof TableMeta)) {
                    throw CriteriaUtils.unknownFieldDerivedGroup(this, alias);
                } else if (!((_SelectionGroup._TableFieldGroup) group).isLegalGroup((TableMeta<?>) table)) {
                    throw CriteriaUtils.unknownTableFieldGroup(this, (_SelectionGroup._TableFieldGroup) group);
                }
            } else if (!(group instanceof DerivedFieldGroup)) {
                throw CriteriaUtils.unknownFieldDerivedGroup(this, alias);
            } else if (table instanceof RecursiveCte) {
                ((RecursiveCte) table).addFieldGroup((DerivedFieldGroup) group);
            } else if (table instanceof DerivedTable || table instanceof _Cte) {
                ((DerivedFieldGroup) group).finish((_SelectionMap) table, alias);
            } else {
                throw CriteriaUtils.unknownFieldDerivedGroup(this, alias);
            }

        }


        private void endQueryContext() {
            //validate DerivedGroup list
            final Map<String, _SelectionGroup> groupMap = this.selectionGroupMap;

            if (groupMap != null && groupMap.size() > 0) {
                throw unknownFieldDerivedGroups(this, groupMap.values());
            }

            final Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap != null && refWindowNameMap.size() > 0) {
                throw unknownWindows(this, refWindowNameMap);
            }

            this.endSelectClauseIfNeed();

            this.selectionGroupMap = null;
            this.refWindowNameMap = null;
            this.windowNameMap = null;
        }

        /**
         * @see #endQueryContext()
         * @see #refSelection(String)
         * @see #onAddBlock(_TabularBlock)
         * @see #addTableBlock(_TabularBlock)
         */
        @SuppressWarnings("unchecked")
        private void endSelectClauseIfNeed() {
            final List<_SelectItem> selectItemList = this.selectItemList;
            if (selectItemList != null && !(selectItemList instanceof ArrayList)) {
                return;
            }
            final int selectItemSize;
            if (!(selectItemList != null && (selectItemSize = selectItemList.size()) > 0)) {
                throw ContextStack.castCriteriaApi(this);
            }
            final _SelectItem selectItem;
            if (selectItemSize > 1) {
                this.selectItemList = Collections.unmodifiableList(selectItemList);
            } else if ((selectItem = selectItemList.get(0)) instanceof Selection) {
                final List<? extends SelectItem> list;
                list = Collections.singletonList((_Selection) selectItem);
                this.selectItemList = (List<_SelectItem>) list;
                this.flatSelectionList = (List<Selection>) list;
            } else {
                this.selectItemList = Collections.singletonList(selectItem);
            }

            if (this.refSelectionMap != null) {
                this.validateDelayRefSelection();
            }
        }

        /**
         * @see #endSelectClauseIfNeed()
         */
        private void validateDelayRefSelection() {
            final Map<Object, SimpleExpression> refSelectionMap = this.refSelectionMap;
            assert refSelectionMap != null;
            Map<String, Selection> selectionMap = null;
            List<Selection> selectionList = null;

            Selection selection;
            int selectionIndex;
            for (Expression refSelection : refSelectionMap.values()) {
                if (!(refSelection instanceof DelayRefSelection)) {
                    continue;
                }
                if (refSelection instanceof DelayNameRefSelection) {
                    if (selectionMap == null) {
                        selectionMap = this.getSelectionMap();
                    }
                    selection = selectionMap.get(((DelayNameRefSelection) refSelection).selectionName);
                } else {
                    if (selectionList == null) {
                        selectionList = this.flatSelectItems();
                    }
                    selectionIndex = ((DelayIndexRefSelection) refSelection).selectionIndex;
                    if (selectionIndex >= 0 && selectionIndex < selectionList.size()) {
                        selection = selectionList.get(selectionIndex);
                    } else {
                        selection = null;
                    }
                }

                if (selection != null) {
                    assert ((DelayRefSelection) refSelection).delaySelection.selection == null;
                    ((DelayRefSelection) refSelection).delaySelection.selection = selection;
                } else if (refSelection instanceof DelayNameRefSelection) {
                    throw CriteriaUtils.unknownSelection(this, ((DelayNameRefSelection) refSelection).selectionName);
                } else {
                    throw CriteriaUtils.unknownSelection(this, ((DelayIndexRefSelection) refSelection).selectionIndex + 1);
                }


            }// for

        }


        private boolean isSelectClauseNotEnd() {
            final List<_SelectItem> selectItemList = this.selectItemList;
            return selectItemList == null || selectItemList instanceof ArrayList;
        }


        private Map<String, Selection> getSelectionMap() {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap != null) {
                return selectionMap;
            }

            final List<Selection> selectionList;
            selectionList = this.flatSelectItems();

            final int selectionSize;
            selectionSize = selectionList.size();
            switch (selectionSize) {
                case 0:
                    throw ContextStack.castCriteriaApi(this);
                case 1: {
                    final Selection selection;
                    selection = selectionList.get(0);
                    selectionMap = Collections.singletonMap(selection.selectionName(), selection);
                }
                break;
                default: {
                    selectionMap = new HashMap<>((int) (selectionSize / 0.75f));
                    for (Selection selection : selectionList) {
                        selectionMap.put(selection.selectionName(), selection);// override,if duplication
                    }
                    selectionMap = Collections.unmodifiableMap(selectionMap);
                }
            }
            this.selectionMap = selectionMap;
            return selectionMap;
        }


    }//SimpleQueryContext

    private static final class PrimaryQueryContext extends SimpleQueryContext
            implements PrimaryContext {


        /**
         * @see #primaryQueryContext(ArmyStmtSpec, CriteriaContext, CriteriaContext)
         */
        private PrimaryQueryContext(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }


    }//PrimaryQueryContext


    private static final class SubQueryContext extends SimpleQueryContext
            implements SubContext {


        /**
         * @see #subQueryContext(ArmyStmtSpec, CriteriaContext, CriteriaContext)
         */
        SubQueryContext(CriteriaContext outerContext, final @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
            Objects.requireNonNull(outerContext);
        }


    }//SubQueryContext


    private static abstract class BracketContext extends StatementContext {

        private final CriteriaContext leftContext;

        private CriteriaContext innerContext;

        private boolean orderByStart;


        private BracketContext(final @Nullable CriteriaContext outerContext,
                               final @Nullable CriteriaContext leftContext) {
            super(outerContext);
            this.leftContext = leftContext;
        }

        @Override
        public final CriteriaContext getLeftContext() {
            return this.leftContext;
        }

        @Override
        public final CriteriaContext getNonNullLeftContext() {
            final CriteriaContext leftContext = this.leftContext;
            assert leftContext != null;
            return leftContext;
        }

        @Override
        public final void onOrderByStart() {
            if (this.innerContext == null || this.orderByStart) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.orderByStart = true;
        }

        @Override
        public final SimpleExpression refSelection(final String selectionAlias) {
            final CriteriaContext leftContext = this.leftContext, innerContext = this.innerContext;
            final SimpleExpression selection;
            if (innerContext == null) {
                throw ContextStack.castCriteriaApi(this);
            } else if (leftContext == null) {
                selection = innerContext.refSelection(selectionAlias);
            } else {
                selection = leftContext.refSelection(selectionAlias);
            }
            return selection;
        }

        @Override
        public final void onSetInnerContext(final @Nullable CriteriaContext innerContext) {
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
        public final void validateFieldFromSubContext(final QualifiedField<?> field) {
            final CriteriaContext outerContext = this.outerContext;
            if (outerContext == null) {
                throw unknownQualifiedField(null, field);
            }
            outerContext.validateFieldFromSubContext(field);
        }

        @Override
        final List<_TabularBlock> onEndContext() {
            if (innerContext == null) {
                throw ContextStack.castCriteriaApi(this);
            }
            return Collections.emptyList();
        }


    }//BracketContext


    private static final class PrimaryBracketContext extends BracketContext
            implements PrimaryContext {

        /**
         * @see #bracketContext(ArmyStmtSpec)
         */
        private PrimaryBracketContext(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }

    }//PrimaryBracketContext

    private static final class SubBracketContext extends BracketContext
            implements SubContext {

        /**
         * @see #bracketContext(ArmyStmtSpec)
         */
        private SubBracketContext(CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }

    }//SubBracketContext

    private static final class ValuesContext extends StatementContext {

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketContext#refSelection(String)}
         */
        private List<? extends SelectItem> selectItemList;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#ref(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<String, NameRefSelection> refSelectionMap;

        private ValuesContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


        @Override
        public SimpleExpression refSelection(final String selectionAlias) {
            Map<String, NameRefSelection> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                this.refSelectionMap = refSelectionMap = new HashMap<>();
            }
            return refSelectionMap.computeIfAbsent(selectionAlias, this::createRefSelection);
        }

        private NameRefSelection createRefSelection(final String selectionAlias) {
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
            return new NameRefSelection(selection);
        }


    }//ValuesContext


    private static final class OtherPrimaryContext extends StatementContext {

        private OtherPrimaryContext() {
            super(null);
        }


    }//OtherPrimaryContext


    private static final class DerivedTableFunctionContext extends StatementContext implements SubContext {

        /**
         * @see #deriveTableFunctionContext()
         */
        private DerivedTableFunctionContext(CriteriaContext outerContext) {
            super(outerContext);
        }


    }//DerivedTableFunctionContext


    private static abstract class DispatcherContext extends StatementContext {

        private final CriteriaContext leftContext;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private Map<String, Map<String, RefDerivedField>> aliasToRefDerivedField;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private Map<String, Boolean> refWindowNameMap;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private List<QualifiedField<?>> fieldsFromSubContext;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private Map<Object, SimpleExpression> refSelectionMap;

        private boolean refOuter;

        private boolean migrated;

        /**
         * @see #dispatcherContext(CriteriaContext, CriteriaContext)
         */
        private DispatcherContext(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(outerContext);
            this.leftContext = leftContext;
        }


        @Override
        public final CriteriaContext getLeftContext() {
            return this.leftContext;
        }

        @Override
        public final CriteriaContext getNonNullLeftContext() {
            final CriteriaContext leftContext = this.leftContext;
            assert leftContext != null;
            return leftContext;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> QualifiedField<T> field(final String tableAlias, final FieldMeta<T> field) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> fieldMap = this.aliasFieldMap;
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                this.aliasFieldMap = fieldMap;
            }
            return (QualifiedField<T>) fieldMap.computeIfAbsent(tableAlias, k -> new HashMap<>())
                    .computeIfAbsent(field, k -> QualifiedFieldImpl.create(tableAlias, k));
        }

        @Override
        public final DerivedField refThis(final String derivedAlias, final String selectionAlias) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (this.isInWithClause()) {
                throw unknownDerivedField(derivedAlias, selectionAlias);
            }
            Map<String, Map<String, RefDerivedField>> derivedMap = this.aliasToRefDerivedField;
            if (derivedMap == null) {
                derivedMap = new HashMap<>();
                this.aliasToRefDerivedField = derivedMap;
            }
            return derivedMap.computeIfAbsent(derivedAlias, k -> new HashMap<>())
                    .computeIfAbsent(selectionAlias, k -> new RefDerivedField(derivedAlias, k));
        }

        @Override
        public final DerivedField refOuter(final String derivedAlias, final String fieldName) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            final CriteriaContext outerContext = this.outerContext;
            if (outerContext == null) {
                throw notFoundOuterContext(this);
            }
            this.refOuter = true;
            return outerContext.refThis(derivedAlias, fieldName);
        }

        @Override
        public final void onRefWindow(final @Nullable String windowName) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            if (windowName == null) {
                throw ContextStack.nullPointer(this);
            }
            Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap == null) {
                refWindowNameMap = new HashMap<>();
                this.refWindowNameMap = refWindowNameMap;
            }
            refWindowNameMap.putIfAbsent(windowName, Boolean.TRUE);
        }

        @Override
        public final void onAddWindow(final String windowName) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            throw ContextStack.castCriteriaApi(this);
        }

        @Override
        public final SimpleExpression refSelection(final @Nullable String selectionAlias) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (selectionAlias == null) {
                throw ContextStack.nullPointer(this);
            }
            Map<Object, SimpleExpression> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                refSelectionMap = new HashMap<>();
                this.refSelectionMap = refSelectionMap;
            }
            SimpleExpression refSelection;
            refSelection = refSelectionMap.get(selectionAlias);
            if (refSelection == null) {
                refSelection = new DelayNameRefSelection(selectionAlias);
                refSelectionMap.put(selectionAlias, refSelection);
            }
            return refSelection;
        }

        /**
         * @param selectionOrdinal based 1 .
         */
        @Override
        public final SimpleExpression refSelection(final int selectionOrdinal) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (selectionOrdinal < 1) {
                throw CriteriaUtils.unknownSelection(this, selectionOrdinal);
            }
            Map<Object, SimpleExpression> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                refSelectionMap = new HashMap<>();
                this.refSelectionMap = refSelectionMap;
            }
            final int selectionIndex = selectionOrdinal - 1;
            SimpleExpression refSelection;
            refSelection = refSelectionMap.get(selectionIndex);
            if (refSelection == null) {
                refSelection = new DelayIndexRefSelection(selectionIndex);
                refSelectionMap.put(selectionIndex, refSelection);
            }
            return refSelection;
        }

        @Override
        public final void validateFieldFromSubContext(final QualifiedField<?> field) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (this.isInWithClause()) {
                throw unknownQualifiedField(this, field);
            }
            List<QualifiedField<?>> list = this.fieldsFromSubContext;
            if (list == null) {
                list = new ArrayList<>();
                this.fieldsFromSubContext = list;
            }
            list.add(field);
        }


    }//DispatcherContext


    private static final class SubDispatcherContext extends DispatcherContext
            implements SubContext {

        private SubDispatcherContext(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }


    }//SubDispatcherContext


    private static final class PrimaryDispatcherContext extends DispatcherContext
            implements PrimaryContext {

        /**
         * @see #dispatcherContext(CriteriaContext, CriteriaContext)
         */
        private PrimaryDispatcherContext(@Nullable CriteriaContext leftContext) {
            super(null, leftContext);
        }


    }//MultiStmtContext


    static final class DerivedSelection extends OperationDataField implements _Selection, DerivedField {

        private final String tableName;

        private final Selection selection;

        private DerivedSelection(String tableName, Selection selection) {
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
        public Expression underlyingExp() {
            return this;
        }

        @Override
        public String fieldName() {
            return this.selection.selectionName();
        }

        @Override
        public String tableAlias() {
            return this.tableName;
        }

        @Override
        public String selectionName() {
            return this.selection.selectionName();
        }


        @Override
        public void appendSelectItem(final _SqlContext context) {
            final DialectParser dialect = context.parser();

            final String safeFieldName = dialect.identifier(this.selection.selectionName());

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
            dialect.identifier(this.selection.selectionName(), builder);

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
            return String.format(" %s.%s", this.tableName, this.selection.selectionName());
        }

    }//DerivedSelection


    static final class RefDerivedField extends OperationDataField implements DerivedField, _Selection {

        final String tableName;

        final String fieldName;

        private final DelaySelection expType;

        private RefDerivedField(String tableName, String fieldName) {
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
        public Expression underlyingExp() {
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
        public String selectionName() {
            return this.fieldName;
        }

        @Override
        public void appendSelectItem(final _SqlContext context) {
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
        public boolean isDelay() {
            return this.selection != null;
        }

    }//DelaySelection


    private static abstract class RefSelection extends OperationExpression.OperationSimpleExpression {

        private final Selection selection;

        private RefSelection(Selection selection) {
            this.selection = selection;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.selection.typeMeta();
        }


    }//RefSelection

    static final class NameRefSelection extends RefSelection {

        private NameRefSelection(Selection selection) {
            super(selection);
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            context.parser()
                    .identifier(((RefSelection) this).selection.selectionName(), builder);
        }

    }// NameRefSelection

    private static final class IndexRefSelection extends RefSelection {

        private final int selectionIndex;

        /**
         * @param selectionIndex based 0
         * @see SimpleQueryContext#refSelection(int)
         * @see DispatcherContext#refSelection(int)
         */
        private IndexRefSelection(int selectionIndex, Selection selection) {
            super(selection);
            this.selectionIndex = selectionIndex;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.selectionIndex); //TODO consider support ?
        }


    }//IndexRefSelection


    private static abstract class DelayRefSelection extends OperationExpression.OperationSimpleExpression {

        private final DelaySelection delaySelection;

        private DelayRefSelection() {
            this.delaySelection = new DelaySelection();
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.delaySelection;
        }


    }//DelayRefSelection

    static final class DelayNameRefSelection extends DelayRefSelection {

        private final String selectionName;

        /**
         * @see SimpleQueryContext#refSelection(String)
         * @see DispatcherContext#refSelection(String)
         */
        private DelayNameRefSelection(String selectionName) {
            this.selectionName = selectionName;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            context.parser()
                    .identifier(this.selectionName, builder);
        }


    }//DelayNameRefSelection

    static final class DelayIndexRefSelection extends DelayRefSelection {

        private final int selectionIndex;

        /**
         * @param selectionIndex based 0
         * @see SimpleQueryContext#refSelection(int)
         * @see DispatcherContext#refSelection(int)
         */
        private DelayIndexRefSelection(int selectionIndex) {
            this.selectionIndex = selectionIndex;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.selectionIndex); //TODO consider support ?

        }


    }//DelayIndexRefSelection


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
        public String selectionName() {
            return this.alias;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.field.typeMeta();
        }


        @Override
        public void appendSelectItem(final _SqlContext context) {
            ((_SelfDescribed) this.field).appendSql(context);
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser()
                    .identifier(this.alias, builder);
        }

        @Override
        public Expression underlyingExp() {
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


    static final class RecursiveCte implements _Cte {

        private final String name;

        /**
         * don't use {@link Map},because field possibly from different level,possibly duplication.
         */
        private List<RefDerivedField> refFieldList;

        private _Cte actualCte;

        private List<DerivedFieldGroup> fieldGroupList;

        /**
         * @see StatementContext#refCte(String)
         */
        private RecursiveCte(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public SubStatement subStatement() {
            final _Cte actual = this.actualCte;
            assert actual != null;
            return actual.subStatement();
        }

        @Override
        public List<String> columnAliasList() {
            final _Cte actual = this.actualCte;
            assert actual != null;
            return actual.columnAliasList();
        }

        @Override
        public Selection refSelection(String name) {
            final _Cte actual = this.actualCte;
            assert actual != null;
            return actual.refSelection(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            final _Cte actual = this.actualCte;
            assert actual != null;
            return actual.refAllSelection();
        }

        /**
         * @see JoinableContext#validateDerivedFieldMap()
         * @see JoinableContext#onAddRecursiveCte(RecursiveCte, String)
         */
        private void addRefFields(final Collection<RefDerivedField> refFields) {

            List<RefDerivedField> refFieldList = this.refFieldList;
            if (refFieldList == null) {
                refFieldList = new ArrayList<>(refFields.size());
                this.refFieldList = refFieldList;
            }
            refFieldList.addAll(refFields);

        }

        /**
         * @see SimpleQueryContext#onAddTabularItem(TabularItem, String)
         */
        private void addFieldGroup(final DerivedFieldGroup group) {
            List<DerivedFieldGroup> groupList = this.fieldGroupList;
            if (groupList == null) {
                groupList = new ArrayList<>(1);
                this.fieldGroupList = groupList;
            }
            groupList.add(group);
        }

        /**
         * @return not found derived field message
         * @see StatementContext#onAddCte(_Cte)
         */
        @Nullable
        private String onRecursiveCteEnd(final _Cte cte) {
            assert this.actualCte == null;
            assert cte.name().equals(this.name);
            this.actualCte = cte;

            final List<DerivedFieldGroup> groupList = this.fieldGroupList;

            if (groupList != null) {
                for (DerivedFieldGroup group : groupList) {
                    group.finish(cte, group.tableAlias());
                }

                this.fieldGroupList = null;
            }

            final List<RefDerivedField> refFieldList = this.refFieldList;
            if (refFieldList == null) {
                return null;
            }

            Selection selection;
            StringBuilder builder = null;
            for (RefDerivedField field : refFieldList) {
                selection = cte.refSelection(field.fieldName);
                if (selection != null) {
                    assert field.expType.selection == null;
                    field.expType.selection = selection;
                    continue;
                }

                if (builder == null) {
                    builder = new StringBuilder();
                    builder.append("unknown derived fields[").append(field.fieldName);
                } else {
                    builder.append(_Constant.SPACE_COMMA_SPACE)
                            .append(field.fieldName);
                }
            }

            this.refFieldList = null;

            final String errorMsg;
            if (builder == null) {
                errorMsg = null;
            } else {
                errorMsg = builder.append(']').toString();
            }
            return errorMsg;
        }


        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append("reference recursive cte[")
                    .append(this.name)
                    .append(",hash:")
                    .append(System.identityHashCode(this))
                    .append(']')
                    .toString();
        }

    }// RefCte


}
