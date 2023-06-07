package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.VarExpression;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
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
     * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
     */
    private static void migrateContext(final StatementContext context, final StatementContext migrated) {
        context.withCteContext = migrated.withCteContext;
        context.varMap = migrated.varMap;
        context.endListenerList = migrated.endListenerList;

        migrated.withCteContext = null;
        migrated.varMap = null;
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
        return ContextStack.criteriaError(context, m);
    }


    private static UnknownDerivedFieldException invalidRef(CriteriaContext context, String derivedAlias, String fieldName) {
        String m = String.format("ref of %s.%s is invalid.", derivedAlias, fieldName);
        return ContextStack.criteriaError(context, UnknownDerivedFieldException::new, m);
    }


    private static CriteriaException currentlyCannotRefSelection(CriteriaContext context, Object selectionAlias) {
        String m = String.format("currently,couldn't reference %s[%s],please check your syntax."
                , Selection.class.getName(), selectionAlias);
        return ContextStack.criteriaError(context, m);
    }

    private static CriteriaException invokeRefSelectionInSelectionClause(CriteriaContext context) {
        String m = String.format("You couldn't invoke %s.refSelection() in SELECT clause.", SQLs.class.getName());
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

    private static CriteriaException unknownTableSelectionGroup(CriteriaContext context,
                                                                _SelectionGroup._TableFieldGroup group) {
        String m = String.format("unknown table selection group,please table alias[%s] and table",
                group.tableAlias());
        return ContextStack.criteriaError(context, m);
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
        return ContextStack.clearStackAnd(UnknownDerivedFieldException::new, m);
    }


    private static UnknownDerivedFieldException unknownRestDerivedField(CriteriaContext context
            , Map<String, Map<String, MutableDerivedField>> aliasToRefSelection) {
        return ContextStack.criteriaError(context, UnknownDerivedFieldException::new,
                createNotFoundAllDerivedFieldMessage(aliasToRefSelection));
    }

    private static UnknownDerivedFieldException unknownDerivedFields(CriteriaContext context, Map<String, MutableDerivedField> map) {
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
                .append("Not found derived table")
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

    private static String createNotFoundAllDerivedFieldMessage(Map<String, Map<String, MutableDerivedField>> aliasToRefSelection) {
        final StringBuilder builder = new StringBuilder()
                .append("Not found derived field[");
        int count = 0;
        for (Map<String, MutableDerivedField> map : aliasToRefSelection.values()) {
            count = appendFoundDerivedFieldsMessage(map, builder, count);
        }
        builder.append(']');
        return builder.toString();
    }

    private static int appendFoundDerivedFieldsMessage(final Map<String, MutableDerivedField> map,
                                                       final StringBuilder builder, int count) {
        for (MutableDerivedField s : map.values()) {
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

    private static CriteriaException recursiveNoNonRecursivePart(CriteriaContext context, RecursiveCte cte) {
        String m = String.format("recursive cte[%s] no non-recursive part.", cte.name);
        throw ContextStack.criteriaError(context, m);
    }

    private static NoColumnFuncFieldAliasException noSpecifiedColumnFuncFieldAlias(_TabularBlock block) {
        final String m, funcName;
        funcName = ((ArmyTabularFunction) block.tableItem()).name();
        m = String.format("You should specified function field alias for column function[%s ; alias(%s)] in column alias clause,Because the function[%s] no explicit field name.",
                funcName,
                block.alias(),
                funcName
        );
        return new NoColumnFuncFieldAliasException(m);
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

        private RecursiveCte recursiveCte;

        private WithCteContext(boolean recursive) {
            this.recursive = recursive;
        }

    }//WithClauseContext


    private static abstract class StatementContext implements CriteriaContext {

        final StatementContext outerContext;

        private Map<String, VarExpression> varMap;

        private List<Runnable> endListenerList;

        private WithCteContext withCteContext;


        private StatementContext(@Nullable CriteriaContext outerContext) {
            this.outerContext = (StatementContext) outerContext;
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
                this.endListenerList = endListenerList = _Collections.arrayList();
            }
            endListenerList.add(listener);
        }


        @Override
        public final void onBeforeWithClause(final boolean recursive) {
            if (this.withCteContext != null) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.withCteContext = new WithCteContext(recursive);
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
            final List<String> columnAliasList = withContext.currentAliasList;

            if (currentName == null || !currentName.equals(cte.name())) {
                throw ContextStack.castCriteriaApi(this);
            } else if (columnAliasList == null) {
                assert cte.columnAliasList().size() == 0;
            } else {
                assert columnAliasList == cte.columnAliasList();//same instance
            }

            Map<String, _Cte> cteMap = withContext.cteMap;
            List<_Cte> cteList = withContext.cteList;
            if (cteMap == null) {
                cteMap = _Collections.hashMap();
                withContext.cteMap = cteMap;
                cteList = _Collections.arrayList();
                withContext.cteList = cteList;
            } else if (!(cteMap instanceof HashMap)) {
                // with clause end
                throw ContextStack.castCriteriaApi(this);
            }


            final RecursiveCte recursiveCte = withContext.recursiveCte;

            if (recursiveCte != null) {
                if (!recursiveCte.name.equals(currentName)) {
                    String m = String.format("recursive cte[%s] not end", recursiveCte.name);
                    throw ContextStack.criteriaError(this, m);
                }
                final String errorMsg;
                if ((errorMsg = recursiveCte.onRecursiveCteEnd(cte)) != null) {
                    throw ContextStack.criteriaError(this, UnknownDerivedFieldException::new, errorMsg);
                }
            }

            if (cteMap.putIfAbsent(currentName, cte) != null) {
                String m = String.format("Cte[%s] duplication", currentName);
                throw ContextStack.criteriaError(this, m);
            }
            cteList.add(cte);

            // clear current for next cte
            withContext.recursiveCte = null;
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

            if (withContext.recursiveCte != null) {
                String m = String.format("recursive cte[%s] not found.", withContext.recursiveCte.name);
                throw ContextStack.criteriaError(this, m);
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
                withContext.cteMap = _Collections.unmodifiableMap(cteMap);
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
                RecursiveCte recursiveCte = withContext.recursiveCte;
                if (recursiveCte == null) {
                    withContext.recursiveCte = recursiveCte = new RecursiveCte(cteName);
                } else {
                    assert recursiveCte.name.equals(cteName);
                }
                thisLevelCte = recursiveCte;
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
        public final List<_TabularBlock> endContext() {
            return this.onEndContext();
        }


        @Override
        public void onAddDerivedGroup(String derivedAlis) {
            //no bug ,never here
            throw new UnsupportedOperationException();
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
        public void addSelectClauseEndListener(Runnable listener) {
            // no bug,never here
            throw new UnsupportedOperationException();
        }

        @Override
        public void registerDeferSelectClause(Runnable deferSelectClause) {
            // no bug,never here
            throw new UnsupportedOperationException();
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
        public Expression refSelection(String selectionAlias) {
            String m = "current context don't support refSelection(selectionAlias)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public Expression refSelection(int selectionOrdinal) {
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
        public void insertColumnList(List<FieldMeta<?>> columnList) {
            String m = "current context don't support insertColumnList(List<FieldMeta<?>> columnList)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public RowElement row(String alias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            String m = "current context don't support row(String alias, SQLs.SymbolPeriod period, TableMeta<?> table)";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public RowElement row(String alias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk) {
            String m = "current context don't support row(String alias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk";
            throw ContextStack.criteriaError(this, m);
        }

        @Override
        public final int hashCode() {
            return System.identityHashCode(this);
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

        private Map<String, Map<String, MutableDerivedField>> aliasToRefDerivedField;

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
                assert this.bufferDerivedBlock == null;
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
                    .computeIfAbsent(field, f -> QualifiedFieldImpl.create(tableAlias, f));
        }

        @Override
        public final DerivedField refThis(final String derivedAlias, final String selectionAlias) {

            final Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;

            final Map<String, DerivedField> fieldMap;
            if (aliasToSelection != null) {
                fieldMap = aliasToSelection.get(derivedAlias);
            } else if (this.isInWithClause()) {
                throw unknownDerivedField(derivedAlias, selectionAlias);
            } else {
                fieldMap = null;
            }
            DerivedField field;
            if (fieldMap == null || (field = fieldMap.get(selectionAlias)) == null) {
                field = createDerivedField(derivedAlias, selectionAlias);
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

        @Nullable
        final _SelectionMap derivedSelectionMap(final @Nullable String derivedAlis) {
            if (derivedAlis == null) {
                throw ContextStack.nullPointer(this);
            }
            this.flushBufferDerivedBlock();

            final Map<String, _TabularBlock> blockMap = this.aliasToBlock;
            if (blockMap == null) {
                throw ContextStack.nullPointer(this);
            }
            final _TabularBlock block;
            block = blockMap.get(derivedAlis);

            final _SelectionMap selectionMap;
            final TabularItem item;
            if (block == null) {
                selectionMap = null;
            } else if (block instanceof _SelectionMap) {
                selectionMap = (_SelectionMap) block;
            } else if ((item = block.tableItem()) instanceof _SelectionMap) {
                selectionMap = (_SelectionMap) item;
            } else {
                String m = String.format("error,'%s' isn't derived table alias or cte alias", derivedAlis);
                throw ContextStack.criteriaError(this, m);
            }
            return selectionMap;
        }


        /**
         * @see #refThis(String, String)
         */
        private DerivedField createDerivedField(final String derivedAlias, final String selectionAlias) {
            //1. flush buffer _DerivedBlock
            this.flushBufferDerivedBlock();

            final Map<String, _AliasDerivedBlock> nestedDerivedBufferMap = this.nestedDerivedBufferMap;
            final Map<String, _TabularBlock> aliasToBlock;
            //2. get SelectionMap of last block
            final _SelectionMap selectionMap;
            final TabularItem tabularItem;
            _TabularBlock block;
            if (nestedDerivedBufferMap != null && (block = nestedDerivedBufferMap.get(derivedAlias)) != null) {
                selectionMap = (_AliasDerivedBlock) block;
            } else if ((aliasToBlock = this.aliasToBlock) == null) {
                throw invalidRef(this, derivedAlias, selectionAlias);
            } else if (!(aliasToBlock instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this);
            } else if ((block = aliasToBlock.get(derivedAlias)) == null) {
                throw invalidRef(this, derivedAlias, selectionAlias);
            } else if ((tabularItem = block.tableItem()) instanceof RecursiveCte) {
                selectionMap = (_SelectionMap) tabularItem;
            } else if (block instanceof _SelectionMap) {
                selectionMap = (_SelectionMap) block;
            } else if (tabularItem instanceof _SelectionMap) {
                selectionMap = (_SelectionMap) tabularItem;
            } else {
                throw invalidRef(this, derivedAlias, selectionAlias);
            }

            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            if (aliasToSelection == null) {
                aliasToSelection = _Collections.hashMap();
                this.aliasToDerivedField = aliasToSelection;
            }
            return aliasToSelection.computeIfAbsent(derivedAlias, _Collections::hashMapIgnoreKey)
                    .computeIfAbsent(selectionAlias, fieldNameKey -> {
                        final Selection selection;
                        selection = selectionMap.refSelection(fieldNameKey);
                        if (selection == null) {
                            throw invalidRef(this, derivedAlias, fieldNameKey);
                        }
                        return new ImmutableDerivedField(derivedAlias, selection);
                    });

        }

        /**
         * @see #onEndContext()
         */
        private void validateDerivedFieldMap() {
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            final Map<String, Map<String, MutableDerivedField>> aliasToRefDerivedField = this.aliasToRefDerivedField;
            assert aliasToRefDerivedField != null;
            if (aliasToBlock == null) {
                throw unknownRestDerivedField(this, aliasToRefDerivedField);
            }

            _TabularBlock block;
            TabularItem tabularItem;
            Map<String, MutableDerivedField> refFieldMap;
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
                if (this instanceof SimpleQueryContext) {
                    ((SimpleQueryContext) this).onAddSelectionMap((RecursiveCte) tableItem, alias);
                }
            } else if (tableItem instanceof DerivedTable) {
                if (tableItem instanceof ArmyTabularFunction
                        && ((ArmyTabularFunction) tableItem).hasAnonymousField()
                        && ((_AliasDerivedBlock) block).columnAliasList().size() == 0) {
                    throw noSpecifiedColumnFuncFieldAlias(block);
                }
                this.onAddDerived(block, (_SelectionMap) tableItem, alias);
            } else if (tableItem instanceof _Cte) {
                this.onAddDerived(block, (_SelectionMap) tableItem, alias);
            } else if (tableItem instanceof TableMeta) {
                if (this instanceof SimpleQueryContext) {
                    ((SimpleQueryContext) this).onAddTable((TableMeta<?>) tableItem, alias);
                }
            } else if (tableItem instanceof UndoneFunction) {
                if (this instanceof SimpleQueryContext) {
                    ((SimpleQueryContext) this).onAddSelectionMap((_DoneFuncBlock) block, alias);
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
                this.addTableBlock(bufferDerivedBlock);
                this.bufferDerivedBlock = null;
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
            final Map<String, Map<String, MutableDerivedField>> aliasToRefSelection = this.aliasToRefDerivedField;
            if (aliasToRefSelection != null) {
                final Map<String, MutableDerivedField> fieldMap;
                fieldMap = aliasToRefSelection.remove(alias);
                if (fieldMap != null) {
                    if (block instanceof _SelectionMap) {
                        this.finishRefSelections((_SelectionMap) block, alias, fieldMap);
                    } else {
                        this.finishRefSelections(derivedTable, alias, fieldMap);
                    }
                    fieldMap.clear();
                }
            }

            if (this instanceof SimpleQueryContext) {
                if (block instanceof _SelectionMap) {
                    ((SimpleQueryContext) this).onAddSelectionMap((_SelectionMap) block, alias);
                } else {
                    ((SimpleQueryContext) this).onAddSelectionMap(derivedTable, alias);
                }

            }

        }


        /**
         * @param derivedTable {@link _DerivedTable} or {@link _Cte},but not {@link RecursiveCte}
         * @see #onAddDerived(_TabularBlock, _SelectionMap, String)
         */
        private void finishRefSelections(final _SelectionMap derivedTable, final String tableAlias,
                                         final Map<String, MutableDerivedField> fieldMap) {
            assert !(derivedTable instanceof RecursiveCte);

            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            if (aliasToSelection == null) {
                aliasToSelection = _Collections.hashMap();
                this.aliasToDerivedField = aliasToSelection;
            }
            final Map<String, DerivedField> derivedFieldMap;
            derivedFieldMap = aliasToSelection.computeIfAbsent(tableAlias, _Collections::hashMapIgnoreKey);

            Selection selection;
            for (MutableDerivedField field : fieldMap.values()) {
                selection = derivedTable.refSelection(field.fieldName);
                if (selection == null) {
                    throw invalidRef(this, tableAlias, field.fieldName);
                }
                assert field.targetSelection == null;
                field.targetSelection = selection;
                derivedFieldMap.putIfAbsent(field.fieldName, field);
            }

        }


        @Nullable
        private MutableDerivedField getDelayRefField(final String derivedTableAlias, final String fieldName,
                                                     final boolean create) {
            Map<String, Map<String, MutableDerivedField>> aliasToRefDerivedField = this.aliasToRefDerivedField;
            if (aliasToRefDerivedField == null && create) {
                aliasToRefDerivedField = _Collections.hashMap();
                this.aliasToRefDerivedField = aliasToRefDerivedField;
            }

            final Map<String, MutableDerivedField> fieldMap;
            final MutableDerivedField field;
            if (aliasToRefDerivedField == null) {
                field = null;
            } else if (create) {
                fieldMap = aliasToRefDerivedField.computeIfAbsent(derivedTableAlias, _Collections::hashMapIgnoreKey);
                field = fieldMap.computeIfAbsent(fieldName, k -> new MutableDerivedField(derivedTableAlias, k));
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
                        final ImmutableDerivedField field;
                        if (selection instanceof TypeInfer.DelayTypeInfer
                                && ((TypeInfer.DelayTypeInfer) selection).isDelay()) {
                            field = new DelayImmutableDerivedField(tableAlias, selection);
                        } else {
                            field = new ImmutableDerivedField(tableAlias, selection);
                        }
                        return field;
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
                    if (this instanceof SimpleQueryContext) {
                        ((SimpleQueryContext) this).onAddSelectionMap((RecursiveCte) tableItem, alias);
                    }
                } else if (tableItem instanceof DerivedTable) {
                    if (tableItem instanceof ArmyTabularFunction
                            && ((ArmyTabularFunction) tableItem).hasAnonymousField()
                            && ((_AliasDerivedBlock) block).columnAliasList().size() == 0) {
                        throw noSpecifiedColumnFuncFieldAlias(block);
                    }
                    this.onAddDerived(block, (_SelectionMap) tableItem, alias);
                } else if (tableItem instanceof _Cte) {
                    this.onAddDerived(block, (_SelectionMap) tableItem, alias);
                } else if (tableItem instanceof TableMeta) {
                    if (this instanceof SimpleQueryContext) {
                        ((SimpleQueryContext) this).onAddTable((TableMeta<?>) tableItem, alias);
                    }
                } else if (tableItem instanceof UndoneFunction) {
                    if (this instanceof SimpleQueryContext) {
                        ((SimpleQueryContext) this).onAddSelectionMap((_DoneFuncBlock) block, alias);
                    }
                }

            }


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
        public final void insertColumnList(final List<FieldMeta<?>> columnList) {
            if (this.columnlist != null) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.columnlist = columnList;
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

        private final StatementContext leftContext;


        private List<_SelectItem> selectItemList;

        /**
         * couldn't clear this field
         */
        private List<Selection> flatSelectionList;

        private Map<String, _SelectionGroup> selectionGroupMap;


        /**
         * couldn't clear this field,because {@link  SQLs#refSelection(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#refSelection(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<Object, SelectionReference> refSelectionMap;

        private Map<String, Boolean> windowNameMap;

        private Map<String, Boolean> refWindowNameMap;

        private Runnable deferSelectClause;

        private Runnable selectClauseEndListener;

        private boolean orderByStart;

        /**
         * @see #callDeferSelectClauseIfNeed()
         */
        private boolean deferSelectClauseIng;

        private SimpleQueryContext(final @Nullable CriteriaContext outerContext,
                                   final @Nullable CriteriaContext leftContext) {
            super(outerContext);
            this.leftContext = (StatementContext) leftContext;
            assert leftContext == null || this.leftContext.outerContext == outerContext;
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
        public void addSelectClauseEndListener(final Runnable listener) {
            if (this.isSelectClauseEnd()) {
                throw ContextStack.castCriteriaApi(this);
            }
            final Runnable prevListener = this.selectClauseEndListener;
            if (prevListener == null) {
                this.selectClauseEndListener = listener;
            } else {
                this.selectClauseEndListener = () -> {
                    prevListener.run();
                    listener.run();
                };
            }
        }

        @Override
        public final void registerDeferSelectClause(final Runnable deferSelectClause) {
            if (this.deferSelectClause != null || this.selectItemList != null) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.deferSelectClause = deferSelectClause;
        }


        @Override
        public final void onAddDerivedGroup(final @Nullable String derivedAlis) {
            if (derivedAlis == null) {
                throw ContextStack.nullPointer(this);
            }
            final _SelectionMap selectionMap;
            selectionMap = this.derivedSelectionMap(derivedAlis);
            if (selectionMap == null) {
                String m = String.format("unknown derived group[%s],please check derived alias.", derivedAlis);
                throw ContextStack.criteriaError(this, m);
            } else if (selectionMap instanceof RecursiveCte) {
                String m = String.format("error , recursive %s.* will produce endless loop", derivedAlis);
                throw ContextStack.criteriaError(this, m);
            }

            this.onAddSelectItem(SelectionGroups.derivedGroup(selectionMap, derivedAlis));
        }


        @Override
        public final CriteriaContext onAddSelectItem(final @Nullable SelectItem selectItem) {
            if (selectItem == null) {
                throw ContextStack.nullPointer(this);
            }

            List<_SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                selectItemList = _Collections.arrayList();
                this.selectItemList = selectItemList;
            } else if (!(selectItemList instanceof ArrayList)) {
                final Map<Object, SelectionReference> selectionMap = this.refSelectionMap;
                if (selectionMap != null && selectionMap.size() > 0) {
                    throw invokeRefSelectionInSelectionClause(this);
                }
                throw ContextStack.castCriteriaApi(this);
            }

            selectItemList.add((_SelectItem) selectItem);

            if (!(selectItem instanceof _SelectionGroup)) {
                return this;
            }

            if (selectItem instanceof _SelectionGroup._TableFieldGroup) {
                Map<String, _SelectionGroup> map = this.selectionGroupMap;
                if (map == null) {
                    map = _Collections.hashMap();
                    this.selectionGroupMap = map;
                }
                final _SelectionGroup group = (_SelectionGroup) selectItem;
                if (map.putIfAbsent(group.tableAlias(), group) != null) {
                    String m = String.format("%s group[%s] duplication", Selection.class.getName(),
                            group.tableAlias());
                    throw ContextStack.criteriaError(this, m);
                }
            } else if (!(selectItem instanceof SelectionGroups.DerivedSelectionGroup)) {
                throw ContextStack.castCriteriaApi(this);
            }
            return this;
        }

        @Override
        public final RowElement row(final @Nullable String alias, SQLs.SymbolPeriod period,
                                    final @Nullable TableMeta<?> table) {
            if (this.isSelectClauseEnd()) {
                throw ContextStack.criteriaError(this, "Error,SELECT clause have ended.");
            } else if (alias == null || table == null) {
                throw ContextStack.nullPointer(this);
            }
            final Map<String, _SelectionGroup> map;
            map = this.getOrCreateSelectionGroupMap();
            _SelectionGroup group;
            group = map.get(alias);
            if (group == null) {
                group = SelectionGroups.singleGroup(table, alias);
                map.put(alias, group);
            } else if (!(group instanceof _SelectionGroup._TableFieldGroup)
                    || !((_SelectionGroup._TableFieldGroup) group).isLegalGroup(table)) {
                String m = String.format("error,please check table alias[%s] in statement.", alias);
                throw ContextStack.criteriaError(this, m);
            }
            return (RowElement) group;
        }


        @Override
        public final RowElement row(final @Nullable String alias, SQLs.SymbolPeriod period,
                                    SQLs.SymbolAsterisk asterisk) {
            if (this.isSelectClauseEnd()) {
                throw ContextStack.criteriaError(this, "Error,SELECT clause have ended.");
            } else if (alias == null) {
                throw ContextStack.nullPointer(this);
            }

            final Map<String, _SelectionGroup> map;
            map = this.getOrCreateSelectionGroupMap();
            _SelectionGroup group;
            group = map.get(alias);
            if (group == null) {
                group = SelectionGroups.derivedGroup(alias);
                map.put(alias, group);
            } else if (!(group instanceof DerivedFieldGroup)
                    || !alias.equals(group.tableAlias())) {
                String m = String.format("error,please check derived alias[%s] in statement.", alias);
                throw ContextStack.criteriaError(this, m);
            }
            return (RowElement) group;
        }

        @Override
        public final void onOrderByStart() {
            if (this.orderByStart) {
                throw ContextStack.castCriteriaApi(this);
            }
            this.orderByStart = true;
        }


        @Override
        public final Expression refSelection(final @Nullable String selectionAlias) {
            if (selectionAlias == null) {
                throw ContextStack.nullPointer(this);
            } else if (this.deferSelectClauseIng) {
                throw currentlyCannotRefSelection(this, selectionAlias);
            }

            final CriteriaContext leftContext = this.leftContext;
            if (leftContext != null) {
                return leftContext.refSelection(selectionAlias);
            }
            Map<Object, SelectionReference> refSelectionMap = this.refSelectionMap;
            SelectionReference refSelection;
            if (refSelectionMap != null && (refSelection = refSelectionMap.get(selectionAlias)) != null) {
                return refSelection;
            }

            if (refSelectionMap == null) {

                this.callDeferSelectClauseIfNeed();
                this.endSelectClauseIfNeed();

                refSelectionMap = _Collections.hashMap();
                this.refSelectionMap = refSelectionMap;
            }

            final Selection selection;
            selection = this.getSelectionMap().get(selectionAlias);
            if (selection == null) {
                throw CriteriaUtils.unknownSelection(this, selectionAlias);
            }
            refSelection = new ImmutableNameRefSelection(selection);
            refSelectionMap.put(selectionAlias, refSelection);
            return refSelection;
        }

        /**
         * @param selectionOrdinal based 1 .
         */
        @Override
        public final Expression refSelection(final int selectionOrdinal) {
            if (selectionOrdinal < 1) {
                throw CriteriaUtils.unknownSelection(this, selectionOrdinal);
            } else if (this.deferSelectClauseIng) {
                throw currentlyCannotRefSelection(this, selectionOrdinal);
            }

            final CriteriaContext leftContext = this.leftContext;
            if (leftContext != null) {
                return leftContext.refSelection(selectionOrdinal);
            }

            Map<Object, SelectionReference> refSelectionMap = this.refSelectionMap;
            SelectionReference refSelection;
            if (refSelectionMap != null && (refSelection = refSelectionMap.get(selectionOrdinal)) != null) {
                return refSelection;
            }

            if (refSelectionMap == null) {

                this.callDeferSelectClauseIfNeed();
                this.endSelectClauseIfNeed();

                refSelectionMap = _Collections.hashMap();
                this.refSelectionMap = refSelectionMap;
            }

            final List<Selection> selectionList = this.flatSelectionList;
            assert selectionList != null;
            if (selectionOrdinal > selectionList.size()) {
                throw CriteriaUtils.unknownSelection(this, selectionOrdinal);
            }

            final Selection selection;
            selection = selectionList.get(selectionOrdinal - 1);
            assert selection != null;
            refSelection = new ImmutableOrdinalRefSelection(selectionOrdinal, selection);
            refSelectionMap.put(selectionOrdinal, refSelection);
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
        public final void onRefWindow(final @Nullable String windowName) {
            if (windowName == null) {
                throw ContextStack.nullPointer(this);
            } else if (!_StringUtils.hasText(windowName)) {
                throw ContextStack.criteriaError(this, "couldn't reference empty window name");
            }
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

        private void onAddSelectionMap(final _SelectionMap selectionMap, final String alias) {
            final Map<String, _SelectionGroup> groupMap = this.selectionGroupMap;

            final _SelectionGroup group;
            if (groupMap == null || (group = groupMap.remove(alias)) == null) {
                return;
            }

            if (!(group instanceof DerivedFieldGroup)) {
                throw CriteriaUtils.unknownFieldDerivedGroup(this, alias);
            } else if (selectionMap instanceof RecursiveCte) {
                ((RecursiveCte) selectionMap).addFieldGroup((DerivedFieldGroup) group);
            } else {
                ((DerivedFieldGroup) group).finish(selectionMap, alias);
            }

        }

        private void onAddTable(final TableMeta<?> table, final String alias) {
            final Map<String, _SelectionGroup> groupMap = this.selectionGroupMap;

            final _SelectionGroup group;
            if (groupMap == null || (group = groupMap.remove(alias)) == null) {
                return;
            }

            if (!(group instanceof _SelectionGroup._TableFieldGroup)) {
                throw CriteriaUtils.unknownFieldDerivedGroup(this, alias);
            } else if (!((_SelectionGroup._TableFieldGroup) group).isLegalGroup(table)) {
                throw CriteriaUtils.unknownTableFieldGroup(this, (_SelectionGroup._TableFieldGroup) group);
            }

        }


        /**
         * select statement end event handler.
         * This method is triggered by {@link #onEndContext()}
         */
        private void endQueryContext() {

            this.callDeferSelectClauseIfNeed();

            //validate DerivedGroup list
            final Map<String, _SelectionGroup> groupMap = this.selectionGroupMap;
            if (groupMap != null && groupMap.size() > 0) {
                this.validTableGroup();
            }

            final Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap != null && refWindowNameMap.size() > 0) {
                throw unknownWindows(this, refWindowNameMap);
            }

            this.endSelectClauseIfNeed();

            if (this.refSelectionMap != null) {

            }
            this.deferSelectClause = null;
            this.selectionGroupMap = null;
            this.refWindowNameMap = null;
            this.windowNameMap = null;
        }


        /**
         * <p>
         * This method is triggered by :
         *     <ul>
         *         <li>{@link #refSelection(int)}</li>
         *         <li>{@link #refSelection(String)}</li>
         *         <li>{@link #endQueryContext()}</li>
         *     </ul>
         * </p>
         */
        private void callDeferSelectClauseIfNeed() {
            final Runnable deferSelectClause = this.deferSelectClause;
            if (deferSelectClause != null && this.selectItemList == null) {
                this.deferSelectClauseIng = true;
                deferSelectClause.run();
                this.deferSelectClauseIng = false;
                if (this.selectItemList == null) {
                    throw ContextStack.criteriaError(this, _Exceptions::selectListIsEmpty);
                }
            }

        }

        /**
         * @see #endQueryContext()
         * @see #refSelection(String)
         * @see #onAddBlock(_TabularBlock)
         * @see #addTableBlock(_TabularBlock)
         * @see #callDeferSelectClauseIfNeed()
         */
        @SuppressWarnings("unchecked")
        private void endSelectClauseIfNeed() {
            final List<_SelectItem> selectItemList = this.selectItemList;
            if ((selectItemList != null && !(selectItemList instanceof ArrayList))
                    || (selectItemList == null && this.deferSelectClause != null)) {
                // have ended or defer select clause not run
                return;
            }

            assert !this.deferSelectClauseIng;

            final int selectItemSize;
            if (!(selectItemList != null && (selectItemSize = selectItemList.size()) > 0)) {
                // not exists select item
                throw ContextStack.castCriteriaApi(this);
            }
            final _SelectItem selectItem;
            if (selectItemSize > 1) {
                this.selectItemList = _Collections.unmodifiableList(selectItemList);
            } else if ((selectItem = selectItemList.get(0)) instanceof Selection) {
                final List<? extends SelectItem> list;
                list = _Collections.singletonList((_Selection) selectItem);
                this.selectItemList = (List<_SelectItem>) list;
                this.flatSelectionList = (List<Selection>) list;
            } else {
                this.selectItemList = _Collections.singletonList(selectItem);
            }

            // call listener
            final Runnable listener = this.selectClauseEndListener;
            if (listener != null) {
                this.selectClauseEndListener = null;
                listener.run();
            }

        }


        /**
         * @see #endQueryContext()
         */
        private void validTableGroup() {
            final Map<String, _SelectionGroup> groupMap = this.selectionGroupMap;
            assert groupMap != null;
            for (_SelectionGroup group : groupMap.values()) {
                if (group instanceof _SelectionGroup._TableFieldGroup) {
                    if (!((_SelectionGroup._TableFieldGroup) group).isLegalGroup(this.getTable(group.tableAlias()))) {
                        throw unknownTableSelectionGroup(this, (_SelectionGroup._TableFieldGroup) group);
                    }
                } else if (!(group instanceof SelectionGroups.DerivedSelectionGroup)) {
                    throw ContextStack.castCriteriaApi(this);
                }
            }

            this.selectionMap = null;
            groupMap.clear();
        }


        private Map<String, _SelectionGroup> getOrCreateSelectionGroupMap() {
            Map<String, _SelectionGroup> map = this.selectionGroupMap;
            if (map == null) {
                map = _Collections.hashMap();
                this.selectionGroupMap = map;
            }
            return map;
        }


        private boolean isSelectClauseEnd() {
            final List<_SelectItem> selectItemList = this.selectItemList;
            return selectItemList != null && !(selectItemList instanceof ArrayList);
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
                    selectionMap = _Collections.singletonMap(selection.alias(), selection);
                }
                break;
                default: {
                    selectionMap = _Collections.hashMap((int) (selectionSize / 0.75f));
                    for (Selection selection : selectionList) {
                        selectionMap.put(selection.alias(), selection);// override,if duplication
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
        public final Expression refSelection(final String selectionAlias) {
            final CriteriaContext leftContext = this.leftContext, innerContext = this.innerContext;
            final Expression selection;
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
        public final Expression refSelection(int selectionOrdinal) {
            final CriteriaContext leftContext = this.leftContext, innerContext = this.innerContext;
            final Expression selection;
            if (innerContext == null) {
                throw ContextStack.castCriteriaApi(this);
            } else if (leftContext == null) {
                selection = innerContext.refSelection(selectionOrdinal);
            } else {
                selection = leftContext.refSelection(selectionOrdinal);
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
         * couldn't clear this field,because {@link  SQLs#refSelection(String)} and {@link  BracketContext#refSelection(String)}
         */
        private List<? extends SelectItem> selectItemList;

        /**
         * couldn't clear this field,because {@link  SQLs#refSelection(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#refSelection(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<String, ImmutableNameRefSelection> refSelectionMap;

        private ValuesContext(@Nullable CriteriaContext outerContext) {
            super(outerContext);
        }


        @Override
        public SimpleExpression refSelection(final String selectionAlias) {
            Map<String, ImmutableNameRefSelection> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                this.refSelectionMap = refSelectionMap = new HashMap<>();
            }
            return refSelectionMap.computeIfAbsent(selectionAlias, this::createRefSelection);
        }

        private ImmutableNameRefSelection createRefSelection(final String selectionAlias) {
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
            return new ImmutableNameRefSelection(selection);
        }


    }//ValuesContext


    private static final class OtherPrimaryContext extends StatementContext {

        private OtherPrimaryContext() {
            super(null);
        }


    }//OtherPrimaryContext


    private static abstract class DispatcherContext extends StatementContext {

        private final CriteriaContext leftContext;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private Map<String, Map<String, MutableDerivedField>> aliasToRefDerivedField;

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
        private Map<Object, SelectionReference> refSelectionMap;

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
            Map<String, Map<String, MutableDerivedField>> derivedMap = this.aliasToRefDerivedField;
            if (derivedMap == null) {
                derivedMap = new HashMap<>();
                this.aliasToRefDerivedField = derivedMap;
            }
            return derivedMap.computeIfAbsent(derivedAlias, k -> new HashMap<>())
                    .computeIfAbsent(selectionAlias, k -> new MutableDerivedField(derivedAlias, k));
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
                refWindowNameMap = _Collections.hashMap();
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
        public final Expression refSelection(final @Nullable String selectionAlias) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (selectionAlias == null) {
                throw ContextStack.nullPointer(this);
            }
            Map<Object, SelectionReference> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                refSelectionMap = _Collections.hashMap();
                this.refSelectionMap = refSelectionMap;
            }
            SelectionReference refSelection;
            refSelection = refSelectionMap.get(selectionAlias);
            if (refSelection == null) {
                refSelection = new MutableNameRefSelection(selectionAlias);
                refSelectionMap.put(selectionAlias, refSelection);
            }
            return refSelection;
        }

        /**
         * @param selectionOrdinal based 1 .
         */
        @Override
        public final Expression refSelection(final int selectionOrdinal) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (selectionOrdinal < 1) {
                throw CriteriaUtils.unknownSelection(this, selectionOrdinal);
            }
            Map<Object, SelectionReference> refSelectionMap = this.refSelectionMap;
            if (refSelectionMap == null) {
                refSelectionMap = _Collections.hashMap();
                this.refSelectionMap = refSelectionMap;
            }
            SelectionReference refSelection;
            refSelection = refSelectionMap.get(selectionOrdinal);
            if (refSelection == null) {
                refSelection = new MutableOrdinalRefSelection(selectionOrdinal);
                refSelectionMap.put(selectionOrdinal, refSelection);
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


    static class ImmutableDerivedField extends OperationDataField implements DerivedField {

        private final String tableName;

        final Selection selection;

        private ImmutableDerivedField(String tableName, Selection selection) {
            this.tableName = tableName;
            this.selection = selection;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.selection.typeMeta();
        }

        @Override
        public final TableField tableField() {
            return ((_Selection) this.selection).tableField();
        }

        @Override
        public final Expression underlyingExp() {
            return this;
        }

        @Override
        public final String fieldName() {
            return this.selection.alias();
        }

        @Override
        public final String tableAlias() {
            return this.tableName;
        }

        @Override
        public final String alias() {
            return this.selection.alias();
        }


        @Override
        public final void appendSelectItem(final _SqlContext context) {
            final DialectParser dialect = context.parser();

            final String safeFieldName;
            safeFieldName = dialect.identifier(this.selection.alias());

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
        public final void appendSql(final _SqlContext context) {
            final DialectParser dialect = context.parser();
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            dialect.identifier(this.tableName, builder)
                    .append(_Constant.POINT);
            dialect.identifier(this.selection.alias(), builder);

        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.tableName, this.selection);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ImmutableDerivedField) {
                final ImmutableDerivedField o = (ImmutableDerivedField) obj;
                match = o.tableName.equals(this.tableName)
                        && o.selection.equals(this.selection);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.tableName)
                    .append(_Constant.POINT)
                    .append(this.selection.alias())
                    .toString();
        }

    }//ImmutableDerivedField


    private static final class DelayImmutableDerivedField extends ImmutableDerivedField
            implements TypeInfer.DelayTypeInfer {

        private DelayImmutableDerivedField(String tableName, Selection selection) {
            super(tableName, selection);
        }

        @Override
        public boolean isDelay() {
            final Selection selection = this.selection;
            return selection instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) selection).isDelay();
        }


    }//DelayImmutableDerivedField


    private static final class MutableDerivedField extends OperationDataField
            implements DerivedField, TypeInfer.DelayTypeInfer {

        final String tableName;

        final String fieldName;

        private Selection targetSelection;

        private MutableDerivedField(String tableName, String fieldName) {
            this.tableName = tableName;
            this.fieldName = fieldName;
        }

        @Override
        public boolean isDelay() {
            final Selection selection = this.targetSelection;
            return selection == null
                    || (selection instanceof DelayTypeInfer && ((DelayTypeInfer) selection).isDelay());
        }

        @Override
        public TypeMeta typeMeta() {
            return this.getTargetSelection().typeMeta();
        }

        @Override
        public TableField tableField() {
            return this.getTargetSelection().tableField();
        }

        @Override
        public Expression underlyingExp() {
            return this.getTargetSelection().underlyingExp();
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
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.tableName)
                    .append(_Constant.POINT)
                    .append(this.fieldName)
                    .toString();
        }

        private _Selection getTargetSelection() {
            final Selection selection = this.targetSelection;
            if (selection == null) {
                String m = String.format("%s is delay", this);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            return (_Selection) selection;
        }


    }//MutableDerivedField

    /**
     * <p>
     * Package interface for {@link OperationExpression#as(String)}
     * </p>
     */
    interface SelectionReference extends Expression {


    }//SelectionReference


    private static class ImmutableNameRefSelection extends OperationExpression.OperationSimpleExpression
            implements SelectionReference {

        final Selection selection;

        private ImmutableNameRefSelection(Selection selection) {
            this.selection = selection;
        }

        @Override
        public final MappingType typeMeta() {
            return this.selection.typeMeta().mappingType();
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            context.parser().identifier(this.selection.alias(), builder);
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.selection.alias())
                    .toString();
        }


    }// ImmutableNameRefSelection

    private static final class DelayImmutableNameRefSelection extends ImmutableNameRefSelection
            implements TypeInfer.DelayTypeInfer {

        private DelayImmutableNameRefSelection(Selection selection) {
            super(selection);
        }

        @Override
        public boolean isDelay() {
            final Selection selection = this.selection;
            return selection instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) selection).isDelay();
        }


    }//DelayImmutableNameRefSelection

    private static class ImmutableOrdinalRefSelection extends NonOperationExpression implements SelectionReference {

        final Selection selection;

        private final int selectionOrdinal;

        /**
         * @param selectionOrdinal based 1
         * @see SimpleQueryContext#refSelection(int)
         * @see DispatcherContext#refSelection(int)
         */
        private ImmutableOrdinalRefSelection(int selectionOrdinal, Selection selection) {
            assert selectionOrdinal > 0;
            this.selectionOrdinal = selectionOrdinal;
            this.selection = selection;
        }

        @Override
        public final MappingType typeMeta() {
            // always return IntegerType.INSTANCE not this.targetSelection type.
            return IntegerType.INSTANCE;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.selectionOrdinal); //TODO consider support ?
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.selectionOrdinal)
                    .toString();
        }

        @Override
        final String operationErrorMessage() {
            return String.format("%s is the ordinal number of Selection[%s],don't support operator and function.",
                    this, this.selection);
        }


    }//ImmutableOrdinalRefSelection

    private static final class DelayImmutableOrdinalRefSelection extends ImmutableOrdinalRefSelection
            implements TypeInfer.DelayTypeInfer {

        /**
         * @param selectionOrdinal based 1
         * @see SimpleQueryContext#refSelection(int)
         * @see DispatcherContext#refSelection(int)
         */
        private DelayImmutableOrdinalRefSelection(int selectionOrdinal, Selection selection) {
            super(selectionOrdinal, selection);
        }

        @Override
        public boolean isDelay() {
            final Selection selection = this.selection;
            return selection instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) selection).isDelay();
        }


    }//DelayImmutableOrdinalRefSelection


    private interface MutableRefSelection extends SelectionReference {

        void setTargetSelection(Selection selection);
    }


    private static final class MutableNameRefSelection extends OperationExpression.OperationSimpleExpression
            implements MutableRefSelection, TypeInfer.DelayTypeInfer {

        private final String selectionName;

        private Selection targetSelection;

        /**
         * @see SimpleQueryContext#refSelection(String)
         * @see DispatcherContext#refSelection(String)
         */
        private MutableNameRefSelection(String selectionName) {
            this.selectionName = selectionName;
        }

        @Override
        public MappingType typeMeta() {
            final Selection selection = this.targetSelection;
            if (selection == null) {
                throw CriteriaUtils.delayTypeInfer(this);
            }
            return selection.typeMeta().mappingType();
        }

        @Override
        public boolean isDelay() {
            final Selection selection = this.targetSelection;
            return selection == null
                    || (selection instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) selection).isDelay());
        }

        @Override
        public void appendSql(final _SqlContext context) {
            if (this.targetSelection == null) {
                throw CriteriaUtils.delayTypeInfer(this);
            }
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            context.parser().identifier(this.selectionName, builder);
        }

        @Override
        public void setTargetSelection(final @Nullable Selection selection) {
            assert this.targetSelection == null;
            assert selection != null;
            assert this.selectionName.equals(selection.alias());
            this.targetSelection = selection;
        }

    }//MutableNameRefSelection

    static final class MutableOrdinalRefSelection extends NonOperationExpression
            implements MutableRefSelection {

        private final int selectionOrdinal;

        private Selection targetSelection;

        /**
         * @param selectionOrdinal based 1
         * @see SimpleQueryContext#refSelection(int)
         * @see DispatcherContext#refSelection(int)
         */
        private MutableOrdinalRefSelection(int selectionOrdinal) {
            assert selectionOrdinal > 0;
            this.selectionOrdinal = selectionOrdinal;
        }

        @Override
        public MappingType typeMeta() {
            // always return IntegerType.INSTANCE not this.targetSelection type.
            return IntegerType.INSTANCE;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            if (this.targetSelection == null) {
                throw new CriteriaException("targetSelection is null");
            }
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.selectionOrdinal); //TODO consider support ?

        }

        @Override
        public void setTargetSelection(final @Nullable Selection selection) {
            assert this.targetSelection == null;
            assert selection != null;
            this.targetSelection = selection;
        }

        @Override
        String operationErrorMessage() {
            final String m;
            if (this.targetSelection == null) {
                m = String.format("%s is the ordinal number of %s,don't support operator and function.",
                        this, Selection.class.getName());
            } else {
                m = String.format("%s is the ordinal number of Selection[%s],don't support operator and function.",
                        this, this.targetSelection);
            }
            return m;
        }


    }//MutableOrdinalRefSelection


    static final class RecursiveCte implements _Cte {

        private final String name;

        /**
         * don't use {@link Map},because field possibly from different level,possibly duplication.
         */
        private List<MutableDerivedField> refFieldList;

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
         */
        private void addRefFields(final Collection<MutableDerivedField> refFields) {

            List<MutableDerivedField> refFieldList = this.refFieldList;
            if (refFieldList == null) {
                refFieldList = _Collections.arrayList(refFields.size());
                this.refFieldList = refFieldList;
            }
            refFieldList.addAll(refFields);

        }

        /**
         * @see SimpleQueryContext#onAddSelectionMap(_SelectionMap, String)
         */
        private void addFieldGroup(final DerivedFieldGroup group) {
            List<DerivedFieldGroup> groupList = this.fieldGroupList;
            if (groupList == null) {
                groupList = _Collections.arrayList();
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

            final List<MutableDerivedField> refFieldList = this.refFieldList;
            if (refFieldList == null) {
                return null;
            }

            Selection selection;
            StringBuilder builder = null;
            for (MutableDerivedField field : refFieldList) {
                selection = cte.refSelection(field.fieldName);
                if (selection != null) {
                    assert field.targetSelection == null;
                    field.targetSelection = selection;
                    continue;
                }

                if (builder == null) {
                    builder = new StringBuilder();
                    builder.append("unknown derived fields[").append(field);
                } else {
                    builder.append(_Constant.SPACE_COMMA_SPACE)
                            .append(field);
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
