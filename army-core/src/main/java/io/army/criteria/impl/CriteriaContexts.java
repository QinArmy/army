/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.VarExpression;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.Function;

/**
 * <p>This class is utils class for creating {@link CriteriaContext}
 * <p>Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @since 0.6.0
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
    static CriteriaContext primaryQueryContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec,
                                               final @Nullable CriteriaContext outerBracketContext,
                                               final @Nullable CriteriaContext leftContext) {
        final PrimaryQueryContext context;
        if (spec == null) {
            assert !(outerBracketContext != null && leftContext != null);
            assert outerBracketContext == null || outerBracketContext instanceof BracketContext;
            context = new PrimaryQueryContext(dialect, outerBracketContext, leftContext);
        } else {
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();

            context = new PrimaryQueryContext(dialect, dispatcherContext.outerContext, dispatcherContext.leftContext);
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
    static CriteriaContext subQueryContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec,
                                           final @Nullable CriteriaContext outerContext,
                                           final @Nullable CriteriaContext leftContext) {
        final SubQueryContext context;
        if (spec == null) {
            assert outerContext != null && (leftContext == null || leftContext.getOuterContext() == outerContext);
            context = new SubQueryContext(dialect, outerContext, leftContext);
        } else {
            assert outerContext == null && leftContext == null;
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();
            assert dispatcherContext.outerContext != null;
            context = new SubQueryContext(dialect, dispatcherContext.outerContext, dispatcherContext.leftContext);

            migrateContext(context, dispatcherContext);
            migrateToQueryContext(context, dispatcherContext);
        }
        return context;
    }


    static CriteriaContext bracketContext(final ArmyStmtSpec spec) {
        final StatementContext ctx;
        ctx = (StatementContext) spec.getContext();

        final BracketContext context;
        if (ctx instanceof PrimaryContext) {
            context = new PrimaryBracketContext(ctx.dialect, ctx.outerContext, ctx.getLeftContext());
        } else {
            context = new SubBracketContext(ctx.dialect, ctx.outerContext, ctx.getLeftContext());
        }
        migrateContext(context, ctx);
        return context;
    }


    static CriteriaContext primaryInsertContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec) {
        final PrimaryDispatcherContext multiStmtContext;
        if (spec == null) {
            multiStmtContext = null;
        } else {
            multiStmtContext = (PrimaryDispatcherContext) spec.getContext();
        }

        final PrimaryInsertContext context;
        context = new PrimaryInsertContext(dialect);

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
    static CriteriaContext subInsertContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec,
                                            @Nullable CriteriaContext outerContext) {
        final StatementContext context;
        if (spec == null) {
            assert outerContext != null;
            context = new SubInsertContext(dialect, outerContext);
        } else {
            assert outerContext == null;
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();

            context = new SubInsertContext(dialect, dispatcherContext.getNonNullOuterContext());

            migrateContext(context, dispatcherContext);
            assertNonQueryContext(dispatcherContext);
        }
        return context;
    }


    static CriteriaContext primarySingleDmlContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec) {
        final PrimaryDispatcherContext ctx;
        if (spec == null) {
            ctx = null;
        } else {
            ctx = (PrimaryDispatcherContext) spec.getContext();
        }
        final StatementContext context;
        context = new PrimarySingleDmlContext(dialect, ctx);

        if (ctx != null) {
            migrateContext(context, ctx);
            assertNonQueryContext(ctx);
        }
        return context;
    }


    static CriteriaContext primaryMultiDmlContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec) {
        final PrimaryMultiDmlContext context;
        context = new PrimaryMultiDmlContext(dialect);

        if (spec != null) {
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();
            migrateContext(context, dispatcherContext);

            assertNonQueryContext(dispatcherContext);
        }
        return context;
    }


    /**
     * <p>For Example , Postgre update/delete criteria context
     */
    static CriteriaContext primaryJoinableSingleDeleteContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec) {
        final PrimaryJoinableSingleDeleteContext context;
        context = new PrimaryJoinableSingleDeleteContext(dialect);
        if (spec != null) {
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();
            migrateContext(context, dispatcherContext);
            assertNonQueryContext(dispatcherContext);
        }
        return context;
    }

    /**
     * <p>For Example , Postgre update criteria context
     */
    static CriteriaContext primaryJoinableSingleUpdateContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec) {
        final PrimaryJoinableSingleUpdateContext context;
        context = new PrimaryJoinableSingleUpdateContext(dialect);
        if (spec != null) {
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();
            migrateContext(context, dispatcherContext);
            assertNonQueryContext(dispatcherContext);
        }
        return context;
    }


    /**
     * <p>For Example ,Postgre update/delete criteria context
     */
    static CriteriaContext subJoinableSingleUpdateContext(final Dialect dialect, final CriteriaContext outerContext) {
        return new SubJoinableSingleUpdateContext(dialect, outerContext);
    }

    /**
     * <p>For Example ,Postgre update/delete criteria context
     */
    static CriteriaContext subJoinableSingleDeleteContext(final Dialect dialect, final CriteriaContext outerContext) {
        return new SubJoinableSingleDeleteContext(dialect, outerContext);
    }

    static CriteriaContext subSingleDmlContext(final Dialect dialect, final CriteriaContext outerContext) {
        return new SubSingleDmlContext(dialect, outerContext);
    }


    /**
     * @param spec probably is below:
     *             <ul>
     *               <li>{@link ArmyStmtSpec}</li>
     *               <li>{@link SimpleQueries},complex statement,need to migration with clause and inherit outer context</li>
     *             </ul>,
     *              if non-nul,then outerBracketContext and leftContext both are null.
     */
    static CriteriaContext primaryValuesContext(final Dialect dialect, final @Nullable ArmyStmtSpec spec,
                                                final @Nullable CriteriaContext outerBracketContext,
                                                final @Nullable CriteriaContext leftContext) {
        final PrimaryValuesContext context;
        if (spec == null) {
            assert !(outerBracketContext != null && leftContext != null);
            assert outerBracketContext == null || outerBracketContext instanceof BracketContext;
            context = new PrimaryValuesContext(dialect, outerBracketContext, leftContext);
        } else {
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();

            context = new PrimaryValuesContext(dialect, dispatcherContext.outerContext, dispatcherContext.leftContext);
            migrateContext(context, dispatcherContext);
        }
        return context;
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
    static CriteriaContext subValuesContext(final Dialect dialect, @Nullable ArmyStmtSpec spec,
                                            @Nullable CriteriaContext outerContext,
                                            @Nullable CriteriaContext leftContext) {
        final SubValuesContext context;
        if (spec == null) {
            assert outerContext != null && (leftContext == null || leftContext.getOuterContext() == outerContext);
            context = new SubValuesContext(dialect, outerContext, leftContext);
        } else {
            assert outerContext == null && leftContext == null;
            final DispatcherContext dispatcherContext;
            dispatcherContext = (DispatcherContext) spec.getContext();
            assert dispatcherContext.outerContext != null;
            context = new SubValuesContext(dialect, dispatcherContext.outerContext, dispatcherContext.leftContext);

            migrateContext(context, dispatcherContext);
        }
        return context;
    }

    static CriteriaContext otherPrimaryContext(Dialect dialect) {
        return new OtherPrimaryContext(dialect);
    }

    /**
     * currently ,just for postgre merge statement
     */
    static CriteriaContext primaryJoinableMergeContext(Dialect dialect) {
        return new PrimaryJoinableMergeContext(dialect);
    }

    static CriteriaContext primaryDispatcherContext(final Dialect dialect, @Nullable final CriteriaContext outerContext,
                                                    final @Nullable CriteriaContext leftContext) {
        assert outerContext != null || leftContext != null;
        return new PrimaryDispatcherContext(dialect, outerContext, leftContext);
    }

    static CriteriaContext subDispatcherContext(final CriteriaContext outerContext,
                                                final @Nullable CriteriaContext leftContext) {
        return new SubDispatcherContext(outerContext, leftContext);
    }

    static UnknownQualifiedFieldException unknownQualifiedField(String tableAlias, FieldMeta<?> field) {
        final String m = String.format("unknown %s tableAlias[%s] %s", QualifiedField.class.getSimpleName(), tableAlias, field);
        return ContextStack.clearStackAnd(UnknownQualifiedFieldException::new, m);
    }

    private static CriteriaException qualifiedFieldTableNotMatch(final String tableAlias, TableMeta<?> targetTable,
                                                                 final FieldMeta<?> field) {
        String m = String.format("the %s that is represented by alias[%s] and %s not match.", targetTable,
                tableAlias, field);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    /**
     * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
     */
    private static void migrateContext(final StatementContext context, final StatementContext migrated) {
        context.withCteContext = migrated.withCteContext;
        context.varMap = migrated.varMap;
        context.endListener = migrated.endListener;

        migrated.withCteContext = null;
        migrated.varMap = null;
        migrated.endListener = null;

        if (migrated instanceof DispatcherContext) {
            ((DispatcherContext) migrated).migrated = true;
        }

    }

    /**
     * @see #migrateContext(StatementContext, StatementContext)
     */
    private static void migrateToQueryContext(final SimpleQueryContext queryContext, final DispatcherContext migrated) {
        ((JoinableContext) queryContext).aliasFieldMap = migrated.aliasFieldMap;
        ((JoinableContext) queryContext).fieldsFromSubContext = migrated.fieldsFromSubContext;
        ((JoinableContext) queryContext).outerRefMap = migrated.outerRefMap;

        queryContext.refWindowNameMap = migrated.refWindowNameMap;


        migrated.aliasFieldMap = null;
        migrated.refWindowNameMap = null;
        migrated.fieldsFromSubContext = null;
        migrated.outerRefMap = null;

        migrated.migrated = true;

    }


    private static void assertNonQueryContext(final DispatcherContext dispatcherContext) {
        if (dispatcherContext.aliasFieldMap != null) {
            String m = String.format("error,couldn't create %s before command.", QualifiedField.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (dispatcherContext.refWindowNameMap != null) {
            throw unknownWindows(dispatcherContext.refWindowNameMap);
        } else if (dispatcherContext.fieldsFromSubContext != null) {
            throw unknownQualifiedFields(dispatcherContext, dispatcherContext.fieldsFromSubContext);
        }

    }

    @Nullable
    private static _SelectionMap obtainSelectionMapFromBlock(final _TabularBlock block) {
        final _SelectionMap selectionMap;
        final TabularItem tabularItem;

        if ((tabularItem = block.tableItem()) instanceof _Cte) {
            selectionMap = (_Cte) tabularItem;
        } else if (block instanceof _SelectionMap) {
            selectionMap = (_SelectionMap) block;
        } else if (tabularItem instanceof _SelectionMap) {
            selectionMap = (_SelectionMap) tabularItem;
        } else {
            selectionMap = null;
        }
        return selectionMap;
    }

    private static DerivedField createDerivedField(final _SelectionMap selectionMap, final String derivedAlias,
                                                   final String fieldName) {
        final Selection selection;
        selection = selectionMap.refSelection(fieldName);
        if (selection == null) {
            throw unknownDerivedField(derivedAlias, fieldName);
        }

        final DerivedField field;
        if (selection instanceof FieldSelection) {
            field = new FieldSelectionField(derivedAlias, (FieldSelection) selection);
        } else {
            field = new ImmutableDerivedField(derivedAlias, selection);
        }
        return field;
    }

    static UnknownDerivedFieldException invalidRef(CriteriaContext context, String derivedAlias, String fieldName) {
        String m = String.format("ref of %s.%s is invalid.", derivedAlias, fieldName);
        return ContextStack.criteriaError(context, UnknownDerivedFieldException::new, m);
    }


    private static CriteriaException currentlyCannotRefSelection(Object labelOrOrdinal) {
        final String identifier = labelOrOrdinal instanceof String ? "label" : "ordinal";
        String m = String.format("currently,couldn't reference %s[%s : %s],please check your syntax.",
                Selection.class.getName(), identifier, labelOrOrdinal);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    private static CriteriaException invokeRefSelectionInSelectionClause() {
        String m = String.format("You couldn't invoke %s.refSelection() in SELECT clause.", SQLs.class.getName());
        return ContextStack.clearStackAndCriteriaError(m);
    }


    private static NotExistsNonRecursiveException notExistsNonRecursivePart(String cteName) {
        String m = String.format("couldn't reference recursive cte[%s],because not exists non-recursive part.",
                cteName);
        return new NotExistsNonRecursiveException(m);
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
    private static UnknownQualifiedFieldException unknownQualifiedField(QualifiedField<?> field) {
        final String m = String.format("unknown %s", field);
        return ContextStack.clearStackAnd(UnknownQualifiedFieldException::new, m);
    }


    private static CriteriaException unknownCte(@Nullable String cteName) {
        String m = String.format("unknown cte[%s]", cteName);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static UnknownDerivedFieldException unknownDerivedField(String derivedAlias, String selectionAlias) {
        String m = String.format("unknown outer derived field[%s.%s].", derivedAlias, selectionAlias);
        return ContextStack.clearStackAnd(UnknownDerivedFieldException::new, m);
    }

    private static CriteriaException nonDeferCommandClause(final Dialect dialect, String commandName,
                                                           String derivedAlias, String selectionAlias) {
        String m;
        m = String.format("%s.refField(\"%s\",\"%s\") isn't allowed in static %s clause for %s",
                SQLs.class.getName(), derivedAlias, selectionAlias, commandName, dialect);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    private static CriteriaException unknownWindows(Map<String, Boolean> refWindowNameMap) {
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
        return ContextStack.clearStackAndCriteriaError(builder.toString());
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

        private List<_Cte> readOnlyCteList;

        private Map<String, _Cte> cteMap;

        private RecursiveCte recursiveCte;

        private WithCteContext(boolean recursive) {
            this.recursive = recursive;
        }

    }//WithClauseContext


    private static abstract class StatementContext implements CriteriaContext {

        final Dialect dialect;

        final StatementContext outerContext;

        private Map<String, VarExpression> varMap;

        private Runnable endListener;

        private WithCteContext withCteContext;

        private boolean endBeforeCommand;


        private StatementContext(Dialect dialect, @Nullable CriteriaContext outerContext) {
            this.dialect = dialect;
            this.outerContext = (StatementContext) outerContext;
        }

        @Override
        public final Dialect dialect() {
            return this.dialect;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> T dialect(Class<T> type) {
            return (T) this.dialect;
        }

        @Override
        public final void validateDialect(final CriteriaContext context) {
            final Dialect d;
            d = ((StatementContext) context).dialect;
            if (this.dialect instanceof StandardDialect) {
                if (!this.dialect.isFamily(d)) {
                    String m;
                    m = String.format("error,standard and domain api couldn't mixe %s api.", d.database().name());
                    throw ContextStack.clearStackAndCriteriaError(m);
                }
            } else if (!(d instanceof StandardDialect) && !this.dialect.isFamily(d)) {
                String m;
                m = String.format("error,%s api couldn't mixe %s api.", this.dialect.database(), d.database().name());
                throw ContextStack.clearStackAndCriteriaError(m);
            }

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
        public final void addEndEventListener(final Runnable listener) {
            final Runnable currentListener = this.endListener;
            if (currentListener == null) {
                this.endListener = listener;
            } else {
                this.endListener = () -> {
                    currentListener.run();
                    listener.run();
                };
            }
        }


        @Override
        public final void onBeforeWithClause(final boolean recursive) {
            if (this.withCteContext != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.withCteContext = new WithCteContext(recursive);
        }

        @Override
        public final void onStartCte(final @Nullable String name) {
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;
            if (withContext.currentName != null) {
                String m = String.format("Cte[%s] don't end,couldn't start new Cte[%s]", withContext.currentName, name);
                throw ContextStack.clearStackAndCriteriaError(m);
            } else if (name == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            final List<_Cte> cteList = withContext.cteList;
            if (cteList != null && !(cteList instanceof ArrayList)) {
                // with clause have ended
                throw ContextStack.clearStackAndCastCriteriaApi();
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
                throw ContextStack.clearStackAndCastCriteriaApi();
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
                throw ContextStack.clearStackAndCastCriteriaApi();
            }


            final RecursiveCte recursiveCte = withContext.recursiveCte;

            if (recursiveCte != null) {
                if (!recursiveCte.name.equals(currentName)) {
                    String m = String.format("recursive cte[%s] not end", recursiveCte.name);
                    throw ContextStack.clearStackAndCriteriaError(m);
                }
                recursiveCte.onRecursiveCteEnd(cte);
            }

            if (cteMap.putIfAbsent(currentName, cte) != null) {
                String m = String.format("Cte[%s] duplication", currentName);
                throw ContextStack.clearStackAndCriteriaError(m);
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
        public final List<_Cte> accessCteList() {
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;
            List<_Cte> readOnlyCteList = withContext.readOnlyCteList;
            if (readOnlyCteList != null) {
                return readOnlyCteList;
            }
            final List<_Cte> cteList = withContext.cteList;
            if (cteList == null) {
                readOnlyCteList = _Collections.emptyList();
            } else if (cteList instanceof ArrayList) {
                withContext.readOnlyCteList = readOnlyCteList = _Collections.unmodifiableList(cteList);
            } else {
                withContext.readOnlyCteList = readOnlyCteList = cteList;
            }
            return readOnlyCteList;
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
                throw ContextStack.clearStackAndCriteriaError(m);
            }

            if (withContext.recursiveCte != null) {
                String m = String.format("recursive cte[%s] not found.", withContext.recursiveCte.name);
                throw ContextStack.clearStackAndCriteriaError(m);
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
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            withContext.cteList = cteList;
            withContext.readOnlyCteList = cteList;
            return cteList;
        }

        @Override
        public final _Cte refCte(final @Nullable String cteName) {
            if (cteName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            final _Cte cte;
            if (this.withCteContext != null) {
                cte = this.doRefCte(this, cteName);
            } else if (this.outerContext == null) {
                throw unknownCte(cteName);
            } else {
                cte = this.outerContext.refCteForSub(this, cteName);
            }
            return cte;
        }


        @Override
        public final void registerVar(final VarExpression var) throws CriteriaException {
            Map<String, VarExpression> varMap = this.varMap;
            if (varMap == null) {
                this.varMap = varMap = _Collections.hashMap();
            }

            if (varMap.putIfAbsent(var.name(), var) != null) {
                String m = String.format("variable[%s] duplication register", var.name());
                throw ContextStack.clearStackAndCriteriaError(m);
            }

        }

        @Override
        public final VarExpression refVar(final @Nullable String name) throws CriteriaException {
            if (name == null) {
                throw ContextStack.clearStackAndNullPointer("variable must be non-null");
            }
            final Map<String, VarExpression> varMap = this.varMap;

            final VarExpression varExp;
            if (varMap == null) {
                varExp = null;
            } else {
                varExp = varMap.get(name);
            }
            if (varExp == null) {
                String m = String.format("variable[%s] don't exists", name);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            return varExp;

        }

        @Override
        public final List<_TabularBlock> endContext() {
            if (this.endBeforeCommand) {
                return Collections.emptyList();
            }

            final Runnable listener = this.endListener;
            if (listener != null) {
                try {
                    listener.run();
                } catch (Exception e) {
                    throw ContextStack.clearStackAndCause(e, "Context end listener invoking occur error");
                } catch (Error e) {
                    throw ContextStack.clearStackAndError(e);
                } finally {
                    this.endListener = null;
                }
            }

            final List<_TabularBlock> list;
            list = onEndContext();

            final Map<String, VarExpression> varMap = this.varMap;
            if (varMap != null) {
                varMap.clear();
                this.varMap = null;
            }

            // couldn't clear other fields , specially  this.withCteContext , before context migration
            return list;
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
        public List<? extends Selection> flatSelectItems() {
            throw ContextStack.criteriaError(this, "current context don't support refAllSelection()");
        }

        @Override
        public Selection selection(String selectionLabel) {
            String m = String.format("current context don't support selection(selectionLabel[%s])", selectionLabel);
            throw ContextStack.clearStackAndCriteriaError(m);
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
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public _SelectionMap getDerived(String derivedAlias) {
            String m = "current context don't support getDerived(derivedAlias)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public _SelectionMap getNonNullDerived(String derivedAlias) {
            String m = "current context don't support getNonNullDerived(derivedAlias)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public void addSelectClauseEndListener(Runnable listener) {
            // no bug,never here
            throw new UnsupportedOperationException();
        }

        @Override
        public void registerDeferCommandClause(Runnable deferCommandClause) {
            // no bug,never here
            throw new UnsupportedOperationException();
        }


        @Override
        public <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
            throw unknownQualifiedField(tableAlias, field);
        }


        @Override
        public DerivedField refField(String derivedAlias, String fieldName) {
            throw unknownDerivedField(derivedAlias, fieldName);
        }


        @Override
        public Expression refSelection(String selectionLabel) {
            String m = "current context don't support refSelection(selectionLabel)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public Expression refSelection(int selectionOrdinal) {
            String m = "current context don't support refSelection(int selectionOrdinal)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public void onSetInnerContext(CriteriaContext innerContext) {
            String m = "current context don't support onSetInnerContext(innerContext)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public void bufferNestedDerived(_AliasDerivedBlock block) {
            String m = "current context don't support bufferNestedDerived(ArmyDerivedBlock,block)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }


        @Override
        public void onAddBlock(_TabularBlock block) {
            String m = "current context don't support onAddBlock(block)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public _TabularBlock lastBlock() {
            String m = "current context don't support lastBlock()";
            throw ContextStack.clearStackAndCriteriaError(m);
        }


        @Override
        public final void endContextBeforeCommand() {
            this.endBeforeCommand = true;
            onEndContextBeforeCommand();
        }

        @Override
        public List<? extends _SelectItem> selectItemList() {
            String m = "current context don't support selectionList()";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public void registerValuesSelectionList(List<? extends _Selection> selectionList) {
            String m = "current context don't support registerValuesSelectionList(List<Selection> selectionList)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public void onValuesRowStart() {
            String m = "current context don't support onValuesRowStart()";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public void onValuesRowEnd() {
            String m = "current context don't support onValuesRowEnd()";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public void singleDmlTable(TableMeta<?> table, String tableAlias) {
            String m = "current context don't support singleDmlTable(TableMeta<?> table, String tableAlias)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }


        @Override
        public void insertRowAlias(TableMeta<?> table, String rowAlias) {
            String m = "current context don't support insertRowAlias(TableMeta<?> table, String rowAlias)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public Expression insertValueField(FieldMeta<?> field, Function<FieldMeta<?>, Expression> function) {
            String m = "current context don't support insertValueField(FieldMeta<?> field, Function<FieldMeta<?>, Expression> function)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public void insertColumnList(List<FieldMeta<?>> columnList) {
            String m = "current context don't support insertColumnList(List<FieldMeta<?>> columnList)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public RowElement row(String alias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            String m = "current context don't support row(String alias, SQLs.SymbolPeriod period, TableMeta<?> table)";
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        @Override
        public RowElement row(String alias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk) {
            String m = "current context don't support row(String alias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk";
            throw ContextStack.clearStackAndCriteriaError(m);
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
                    .append(",dialect:")
                    .append(this.dialect.name())
                    .append(",outerContext:");
            CriteriaContext context = this.outerContext;
            if (context == null) {
                builder.append("null");
            } else {
                builder.append(context.getClass().getSimpleName());
            }
            if (this instanceof SimpleQueryContext || this instanceof ValuesContext) {
                builder.append(",leftContext:");
                context = this.getLeftContext();
                if (context == null) {
                    builder.append("null");
                } else {
                    builder.append(context.getClass().getSimpleName());
                }
            }
            return builder.append(']')
                    .toString();
        }

        void onEndContextBeforeCommand() {
            // for sub class
        }

        /**
         * <p>This method is invoked by sub context
         */
        void validateFieldFromSubContext(QualifiedField<?> field) {
            throw unknownQualifiedField(field);
        }

        List<_TabularBlock> onEndContext() {
            return Collections.emptyList();
        }

        final boolean isInWithClause() {
            final WithCteContext withContext = this.withCteContext;
            return withContext != null && (withContext.cteList == null || withContext.cteList instanceof ArrayList);
        }


        /**
         * @param cteName   the name of recursive cte.
         * @param fieldName the field name of recursive cte.
         * @return <ul>
         * <li>non-negative : alias index</li>
         * <li>{@link Integer#MIN_VALUE} : no cte alias</li>
         * <li>else fieldName is unknown.</li>
         * </ul>
         */
        final int indexOfCteField(final String cteName, final String fieldName) {
            final WithCteContext withContext = this.withCteContext;
            if (withContext == null || !cteName.equals(withContext.currentName)) {
                // no bug,never here
                String m = String.format("unknown recursive field[%s]", fieldName);
                throw ContextStack.clearStackAndCriteriaError(m); // this isn't current context.
            }
            final List<String> aliasList = withContext.currentAliasList;
            if (aliasList == null) {
                return Integer.MIN_VALUE;
            }
            int index;
            final int size = aliasList.size();
            index = -1;
            for (int i = size - 1; i > -1; i--) {
                if (!fieldName.equals(aliasList.get(i))) {
                    continue;
                }
                index = i;
                break;
            }
            return index;
        }


        _SelectionMap refNonRecursivePart(RecursiveCte cte) {
            // no bug,never here
            throw new UnsupportedOperationException();
        }


        /**
         * <p>For validate derived table lateral outer reference.
         *
         * @param outerTabularItemAlias outer context table alias set.
         * @return true contain any one tabular item alias of outer context.
         * @see JoinableContext#refField(String, String)
         */
        boolean containsAnyTableAlias(final Set<String> outerTabularItemAlias) {
            return false;
        }

        @Nullable
        DerivedField refOuterOrMoreOuterField(String derivedAlias, String fieldName) {
            // default return null .
            return null;
        }

        @Nullable
        <T> QualifiedField<T> outerOrMoreOuterField(String tableAlias, FieldMeta<T> field) {
            // default return null .
            return null;
        }


        /**
         * @param sourceContext the context whose {@link CriteriaContext#refCte(String)} is invoked .
         */
        private _Cte refCteForSub(final CriteriaContext sourceContext, final String cteName) {
            final StatementContext outerContext = this.outerContext;
            final _Cte cte;
            if (this.withCteContext != null) {
                cte = this.doRefCte(sourceContext, cteName);
            } else if (outerContext == null) {
                throw unknownCte(cteName);
            } else {
                cte = outerContext.refCteForSub(this, cteName);
            }
            return cte;
        }

        /**
         * @see #refCte(String)
         * @see #refCteForSub(CriteriaContext, String)
         */
        private _Cte doRefCte(final CriteriaContext sourceContext, final String cteName) {
            final WithCteContext withContext = this.withCteContext;
            assert withContext != null;
            final _Cte thisLevelCte;
            final String currentName = withContext.currentName;
            if (currentName != null && currentName.equals(cteName)) {
                if (!withContext.recursive) {
                    String m;
                    m = String.format("Non-recursive with clause,cte[%s] couldn't recursive-referencing", cteName);
                    throw ContextStack.clearStackAndCriteriaError(m);
                }
                RecursiveCte recursiveCte = withContext.recursiveCte;
                if (recursiveCte == null) {
                    withContext.recursiveCte = recursiveCte = new RecursiveCte(this, cteName, sourceContext);
                } else {
                    assert recursiveCte.name.equals(cteName);
                }
                thisLevelCte = recursiveCte;
            } else {
                final Map<String, _Cte> cteMap = withContext.cteMap;
                if (cteMap == null) {
                    thisLevelCte = null;
                } else {
                    thisLevelCte = cteMap.get(cteName);
                }
            }

            final _Cte cte;
            if (thisLevelCte != null) {
                cte = thisLevelCte;
            } else if (this.outerContext == null) {
                throw unknownCte(cteName);
            } else {
                cte = this.outerContext.refCteForSub(sourceContext, cteName);
            }
            return cte;
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

        private Map<String, Map<String, DerivedField>> aliasToDerivedField;

        /**
         * can't validate field,because field possibly from outer context,{@link _SqlContext} validate this.
         */
        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap;

        private List<QualifiedField<?>> fieldsFromSubContext;

        private Map<String, Boolean> outerRefMap;

        private JoinableContext(Dialect dialect, @Nullable CriteriaContext outerContext) {
            super(dialect, outerContext);
        }


        @Override
        public final void bufferNestedDerived(final _AliasDerivedBlock block) {
            if (this.isEndContext()) {
                throw ContextStack.clearStackAndCastCriteriaApi();
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
        public final TableMeta<?> getTable(final @Nullable String tableAlias) {
            if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
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
        public final _SelectionMap getDerived(final @Nullable String derivedAlias) {
            if (derivedAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            // first flush buffer
            flushBufferDerivedBlock();

            final Map<String, _TabularBlock> blockMap = this.aliasToBlock;
            final _TabularBlock block;

            final _SelectionMap selectionMap;
            if (blockMap == null || (block = blockMap.get(derivedAlias)) == null) {
                selectionMap = null;
            } else {
                selectionMap = obtainSelectionMapFromBlock(block);
            }
            return selectionMap;
        }

        @Override
        public final _SelectionMap getNonNullDerived(String derivedAlias) {
            final _SelectionMap selectionMap;
            selectionMap = this.getDerived(derivedAlias);
            if (selectionMap == null) {
                throw CriteriaUtils.unknownFieldDerivedGroup(this, derivedAlias);
            }
            return selectionMap;
        }


        @SuppressWarnings("unchecked")
        @Override
        public final <T> QualifiedField<T> field(final @Nullable String tableAlias, final @Nullable FieldMeta<T> field) {
            if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (field == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            // here , perhaps from sub values context
            final Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap = this.aliasFieldMap;
            final Map<FieldMeta<?>, QualifiedField<?>> fieldMap;

            final TableMeta<?> targetTable;

            QualifiedField<?> qualifiedField;
            if (aliasFieldMap != null && (fieldMap = aliasFieldMap.get(tableAlias)) != null) {
                qualifiedField = fieldMap.get(field);
                if (qualifiedField == null) {
                    qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                    fieldMap.put(field, qualifiedField);
                }
            } else if ((targetTable = getTable(tableAlias)) != null) {
                if (targetTable != field.tableMeta()) {
                    throw qualifiedFieldTableNotMatch(tableAlias, targetTable, field);
                }
                qualifiedField = getOrCreateField(tableAlias, field);
            } else if (this instanceof JoinableSingleDmlContext
                    && tableAlias.equals(((JoinableSingleDmlContext) this).tableAlias)) {
                if (field.tableMeta() != ((JoinableSingleDmlContext) this).targetTable) {
                    throw qualifiedFieldTableNotMatch(tableAlias, ((JoinableSingleDmlContext) this).targetTable, field);
                }
                qualifiedField = getOrCreateField(tableAlias, field);
            } else if (isInWithClause()) {
                throw unknownQualifiedField(tableAlias, field);
            } else if (this instanceof PrimaryContext) {
                qualifiedField = null;
            } else {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                qualifiedField = outerContext.outerOrMoreOuterField(tableAlias, field);
                if (qualifiedField != null) {
                    addOuterRef(tableAlias);
                }
            }

            if (qualifiedField == null) {
                if (this.aliasToBlock != null && this instanceof PrimaryContext) {
                    throw unknownQualifiedField(tableAlias, field);
                }
                qualifiedField = getOrCreateField(tableAlias, field);
            }
            return (QualifiedField<T>) qualifiedField;
        }


        @Override
        public final DerivedField refField(final String derivedAlias, final String fieldName) {
            // here , perhaps from sub values context
            final Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            final boolean noFromClause;
            final Map<String, DerivedField> fieldMap;

            final DerivedField field;
            DerivedField tempField;
            if (aliasToSelection != null
                    && (fieldMap = aliasToSelection.get(derivedAlias)) != null
                    && (tempField = fieldMap.get(fieldName)) != null) {
                field = tempField;
            } else if (getDerived(derivedAlias) != null) { // here getDerived() trigger buffer derived table in current context
                field = createDerivedField(derivedAlias, fieldName);
            } else if (isInWithClause()) {
                throw unknownDerivedField(derivedAlias, fieldName);
            } else if ((noFromClause = this.aliasToBlock == null) && (this instanceof SimpleQueryContext
                    && ((SimpleQueryContext) this).deferCommandClause == null)) {
                throw nonDeferCommandClause(this.dialect, "SELECT", derivedAlias, fieldName);
            } else if (noFromClause && (this instanceof JoinableSingleDmlContext
                    && ((JoinableSingleDmlContext) this).deferCommandClause == null)) {
                throw nonDeferCommandClause(this.dialect, "SET", derivedAlias, fieldName);
            } else if (this instanceof PrimaryContext) {
                throw unknownDerivedField(derivedAlias, fieldName);
            } else {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
                if (field == null) {
                    throw unknownDerivedField(derivedAlias, fieldName);
                }
                addOuterRef(derivedAlias);
            }
            return field;
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
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return blockList.get(size - 1);
        }

        @Override
        final void onEndContextBeforeCommand() {
            assert this instanceof SimpleQueryContext;
            if (this.aliasToBlock != null
                    || this.bufferDerivedBlock != null
                    || this.tableBlockList != null
                    || this.aliasFieldMap != null
                    || ((SimpleQueryContext) this).selectItemList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (((SimpleQueryContext) this).refWindowNameMap != null) {
                String m = String.format("You couldn't reference %s before SELECT clause.", Window.class.getName());
                throw ContextStack.clearStackAndCriteriaError(m);
            }

            this.aliasToBlock = Collections.emptyMap();
            ((SimpleQueryContext) this).selectItemList = Collections.emptyList();
        }



        /*-------------------below package methods -------------------*/


        @Override
        final void validateFieldFromSubContext(final QualifiedField<?> field) {
            if (isInWithClause()) {
                throw unknownQualifiedField(field);
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
                notExists = aliasSame && field.tableMeta() != ((JoinableSingleDmlContext) this).targetTable;
                if (aliasSame && notExists) {
                    throw unknownQualifiedField(field);
                }
            } else {
                notExists = true;
            }

            if (notExists) {
                List<QualifiedField<?>> list = this.fieldsFromSubContext;
                if (list == null) {
                    this.fieldsFromSubContext = list = _Collections.arrayList();
                }
                list.add(field);
            }
        }


        /**
         * @see JoinableContext#field(String, FieldMeta)
         */
        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        final <T> QualifiedField<T> outerOrMoreOuterField(final String tableAlias, final FieldMeta<T> field) {

            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap = this.aliasFieldMap;
            final Map<FieldMeta<?>, QualifiedField<?>> fieldMap;


            final TableMeta<?> targetTable;

            final QualifiedField<?> qualifiedField, tempFiled;

            if (aliasFieldMap != null && (fieldMap = aliasFieldMap.get(tableAlias)) != null) {
                tempFiled = fieldMap.get(field);
                if (tempFiled == null) {
                    qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                    fieldMap.put(field, qualifiedField);
                } else {
                    qualifiedField = tempFiled;
                }
            } else if ((targetTable = getTable(tableAlias)) != null) {
                if (targetTable != field.tableMeta()) {
                    throw qualifiedFieldTableNotMatch(tableAlias, targetTable, field);
                }
                qualifiedField = getOrCreateField(tableAlias, field);
            } else if (isInWithClause()) {
                if (this instanceof SubContext) {
                    final StatementContext outerContext = this.outerContext;
                    assert outerContext != null;
                    qualifiedField = outerContext.outerOrMoreOuterField(tableAlias, field);
                    if (qualifiedField != null) {
                        addOuterRef(tableAlias);
                    }
                } else {
                    qualifiedField = null;
                }
            } else if (this instanceof PrimaryContext) {
                qualifiedField = null;
            } else {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                qualifiedField = outerContext.outerOrMoreOuterField(tableAlias, field);
                if (qualifiedField != null) {
                    addOuterRef(tableAlias);
                }
            }
            return (QualifiedField<T>) qualifiedField;
        }


        /**
         * @see JoinableContext#refField(String, String)
         */
        @Nullable
        @Override
        final DerivedField refOuterOrMoreOuterField(final String derivedAlias, final String fieldName) {

            final Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            final boolean noFromClause;
            final Map<String, DerivedField> fieldMap;

            final DerivedField field;
            DerivedField tempField;
            if (aliasToSelection != null
                    && (fieldMap = aliasToSelection.get(derivedAlias)) != null
                    && (tempField = fieldMap.get(fieldName)) != null) {
                field = tempField;
            } else if (getDerived(derivedAlias) != null) { // here getDerived() trigger buffer derived table in current context
                field = createDerivedField(derivedAlias, fieldName);
            } else if (isInWithClause()) {
                if (this instanceof SubContext) {
                    final StatementContext outerContext = this.outerContext;
                    assert outerContext != null;
                    field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
                    if (field != null) {
                        addOuterRef(derivedAlias);
                    }
                } else {
                    field = null;
                }
            } else if ((noFromClause = this.aliasToBlock == null) && (this instanceof SimpleQueryContext
                    && ((SimpleQueryContext) this).deferCommandClause == null)) {
                throw nonDeferCommandClause(this.dialect, "SELECT", derivedAlias, fieldName);
            } else if (noFromClause && (this instanceof JoinableSingleDmlContext
                    && ((JoinableSingleDmlContext) this).deferCommandClause == null)) {
                throw nonDeferCommandClause(this.dialect, "SET", derivedAlias, fieldName);
            } else if (this instanceof PrimaryContext) {
                field = null;
            } else {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
                if (field != null) {
                    addOuterRef(derivedAlias);
                }
            }
            return field;
        }


        @Override
        final boolean containsAnyTableAlias(final Set<String> outerTabularItemAlias) {
            final Map<String, Boolean> outerRefMap = this.outerRefMap;
            if (outerRefMap == null || outerRefMap instanceof HashMap) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            boolean contains = false;
            for (String alias : outerRefMap.keySet()) {
                if (outerTabularItemAlias.contains(alias)) {
                    contains = true;
                    break;
                }
            }
            return contains;
        }


        @Override
        final List<_TabularBlock> onEndContext() {

            if (this.isEndContext()) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            //2. flush bufferDerivedBlock
            this.flushBufferDerivedBlock();

            //3 end SimpleQueryContext ,possibly trigger defer SELECT clause.
            if (this instanceof JoinableDeferCommandContext) {
                ((JoinableDeferCommandContext) this).endDeferCommand();
            }

            final Map<String, _TabularBlock> aliasToBlock;
            aliasToBlock = _Collections.safeUnmodifiableMap(this.aliasToBlock);
            this.aliasToBlock = aliasToBlock;

            final List<_TabularBlock> blockList;
            blockList = _Collections.safeUnmodifiableList(this.tableBlockList);
            this.tableBlockList = blockList;//store for recursive checking


            //4. assert nestedDerivedBufferMap
            final Map<String, _AliasDerivedBlock> nestedDerivedBufferMap = this.nestedDerivedBufferMap;
            if (nestedDerivedBufferMap != null && nestedDerivedBufferMap.size() > 0) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.nestedDerivedBufferMap = null; //clear


            //5. validate aliasToBlock
            final int blockSize = blockList.size();
            if (blockSize == 0 && !(this instanceof SimpleQueryContext || this instanceof JoinableSingleDmlContext)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            assert aliasToBlock.size() >= blockSize;// nested items

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
                this.aliasToDerivedField = null; // clear
            }

            this.outerRefMap = _Collections.safeUnmodifiableMap(this.outerRefMap);
            return blockList;
        }


        /*-------------------below private methods -------------------*/

        @SuppressWarnings("unchecked")
        final <T> QualifiedField<T> getOrCreateField(final String tableAlias, final FieldMeta<T> field) {

            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap = this.aliasFieldMap;
            Map<FieldMeta<?>, QualifiedField<?>> fieldMap;

            if (aliasFieldMap == null) {
                this.aliasFieldMap = aliasFieldMap = _Collections.hashMap();
                fieldMap = null;
            } else {
                fieldMap = aliasFieldMap.get(tableAlias);
            }

            QualifiedField<?> qualifiedField;
            if (fieldMap == null) {
                fieldMap = _Collections.hashMap();
                aliasFieldMap.put(tableAlias, fieldMap);
                qualifiedField = null;
            } else {
                qualifiedField = fieldMap.get(field);
            }

            if (qualifiedField == null) {
                qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                fieldMap.put(field, qualifiedField);
            }
            return (QualifiedField<T>) qualifiedField;
        }


        /**
         * @see JoinableContext#refField(String, String)
         */
        private DerivedField createDerivedField(final String derivedAlias, final String fieldAlias) {
            //1. flush buffer _DerivedBlock
            flushBufferDerivedBlock();

            final Map<String, _AliasDerivedBlock> nestedDerivedBufferMap = this.nestedDerivedBufferMap;
            final Map<String, _TabularBlock> aliasToBlock;
            //2. get SelectionMap of  block
            final _SelectionMap selectionMap;
            final TabularItem tabularItem;
            _TabularBlock block;
            if (nestedDerivedBufferMap != null && (block = nestedDerivedBufferMap.get(derivedAlias)) != null) {
                selectionMap = (_AliasDerivedBlock) block;
            } else if ((aliasToBlock = this.aliasToBlock) == null) {
                selectionMap = null;
            } else if (!(aliasToBlock instanceof HashMap)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if ((block = aliasToBlock.get(derivedAlias)) == null) {
                selectionMap = null;
            } else if ((tabularItem = block.tableItem()) instanceof _Cte) {
                selectionMap = (_SelectionMap) tabularItem;
            } else if (block instanceof _SelectionMap) {
                selectionMap = (_SelectionMap) block;
            } else if (tabularItem instanceof _SelectionMap) {
                selectionMap = (_SelectionMap) tabularItem;
            } else {
                selectionMap = null;
            }

            if (selectionMap == null) {
                throw unknownDerivedField(derivedAlias, fieldAlias);
            }
            Map<String, Map<String, DerivedField>> aliasToSelection = this.aliasToDerivedField;
            if (aliasToSelection == null) {
                this.aliasToDerivedField = aliasToSelection = _Collections.hashMap();
            }
            return aliasToSelection.computeIfAbsent(derivedAlias, _Collections::hashMapIgnoreKey)
                    .computeIfAbsent(fieldAlias, fieldNameKey -> {
                        final Selection selection;
                        selection = selectionMap.refSelection(fieldNameKey);
                        if (selection == null) {
                            throw invalidRef(this, derivedAlias, fieldNameKey);
                        }
                        final DerivedField field;
                        if (selection instanceof FieldSelection) { // for codec field
                            field = new FieldSelectionField(derivedAlias, (FieldSelection) selection);
                        } else {
                            field = new ImmutableDerivedField(derivedAlias, selection);
                        }
                        return field;
                    });

        }


        /**
         * @see #onEndContext()
         */
        private void validateQualifiedFieldMap() {
            final Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap = this.aliasFieldMap;
            final Map<String, _TabularBlock> aliasToBlock = this.aliasToBlock;
            assert aliasFieldMap != null;

            final StatementContext outerContext = this.outerContext;

            final TableMeta<?> dmlTargetTable;
            final String dmlTargetTableAlias;
            if (this instanceof JoinableSingleDmlContext) {
                dmlTargetTable = ((JoinableSingleDmlContext) this).targetTable;
                dmlTargetTableAlias = ((JoinableSingleDmlContext) this).tableAlias;
            } else {
                dmlTargetTable = null;
                dmlTargetTableAlias = null;
            }

            String tableAlias;
            TableMeta<?> firstFieldTable;
            _TabularBlock block;
            boolean belongToThisLevel = false;
            for (Map.Entry<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasEntry : aliasFieldMap.entrySet()) {

                firstFieldTable = null;
                for (QualifiedField<?> field : aliasEntry.getValue().values()) {

                    if (firstFieldTable == null) {
                        firstFieldTable = field.tableMeta();
                        tableAlias = aliasEntry.getKey();
                        if (firstFieldTable == dmlTargetTable && tableAlias.equals(dmlTargetTableAlias)) {
                            belongToThisLevel = true;
                        } else if (aliasToBlock != null && (block = aliasToBlock.get(tableAlias)) != null) {
                            if (block.tableItem() != firstFieldTable) {
                                throw unknownQualifiedField(field);
                            }
                            belongToThisLevel = true;
                        } else {
                            belongToThisLevel = false;
                            addOuterRef(tableAlias);
                        }
                    } else if (field.tableMeta() != firstFieldTable) {
                        String m = String.format("the table of %s and target table not match.", field);
                        throw ContextStack.clearStackAnd(UnknownQualifiedFieldException::new, m);
                    }

                    if (belongToThisLevel) {
                        continue;
                    }

                    if (outerContext == null) {
                        throw unknownQualifiedField(field);
                    }

                    outerContext.validateFieldFromSubContext(field);

                } // inner for


            } // outer for

            aliasFieldMap.clear();
            this.aliasFieldMap = null;
        }

        /**
         * @see #field(String, FieldMeta)
         * @see #refField(String, String)
         * @see #validateQualifiedFieldMap()
         * @see #validateQualifiedFieldFromSub()
         */
        private void addOuterRef(final String tableAlias) {
            Map<String, Boolean> outerRefMap = this.outerRefMap;
            if (outerRefMap == null) {
                this.outerRefMap = outerRefMap = _Collections.hashMap();
            }
            outerRefMap.putIfAbsent(tableAlias, Boolean.TRUE);
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
            final StatementContext outerContext = this.outerContext;

            _TabularBlock block;
            for (QualifiedField<?> field : fieldList) {
                block = aliasToBlock.get(field.tableAlias());
                if (block != null) {
                    if (block.tableItem() != field.tableMeta()) {
                        throw unknownQualifiedField(field);
                    }
                } else if (outerContext == null) {
                    throw unknownQualifiedField(field);
                } else {
                    outerContext.validateFieldFromSubContext(field);
                    addOuterRef(field.tableAlias());
                }

            } // for loop

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
                this.aliasToBlock = aliasToBlock = _Collections.hashMap();
                this.tableBlockList = tableBlockList = _Collections.arrayList();

                if (this instanceof SimpleQueryContext) {
                    ((SimpleQueryContext) this).endSelectClauseIfNeed();
                }
            } else if (!(aliasToBlock instanceof HashMap)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            final int blockSize;
            blockSize = tableBlockList.size();

            final TabularItem tableItem = block.tableItem();
            final String alias = block.alias();

            assert !(tableItem instanceof RecursiveCte) || ((RecursiveCte) tableItem).sourceContext == this;

            if (tableItem instanceof _NestedItems) {
                this.addNestedItems((_NestedItems) tableItem);
            } else if (!_StringUtils.hasText(alias)) {
                throw ContextStack.criteriaError(this, _Exceptions::tableItemAliasNoText, tableItem);
            } else if (aliasToBlock.putIfAbsent(alias, block) != null) {
                throw ContextStack.criteriaError(this, _Exceptions::tableAliasDuplication, alias);
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
                        throw ContextStack.clearStackAndCriteriaError(m);
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
            if (!(derivedTable instanceof DerivedTable && derivedTable instanceof CriteriaContextSpec)) {
                return;
            }

            if (((CriteriaContextSpec) derivedTable).getContext().getOuterContext() != this) {
                String m = String.format("%s[%s] context not match.", DerivedTable.class.getSimpleName(), alias);
                throw ContextStack.clearStackAndCriteriaError(m);
            }

            if ((!(block instanceof _ModifierTabularBlock) || ((_ModifierTabularBlock) block).modifier() != SQLs.LATERAL)
                    && validateLateralRef((DerivedTable) derivedTable, this.aliasToBlock.keySet())) {
                String m = String.format("DerivedTable[%s] isn't LATERAL,couldn't reference outer field.", alias);
                throw ContextStack.clearStackAndCriteriaError(NonLateralException::new, m);
            }

        }


        /**
         * @see #onAddDerived(_TabularBlock, _SelectionMap, String)
         */
        private boolean validateLateralRef(final DerivedTable table, final Set<String> aliasSet) {
            final boolean contains;
            if (table instanceof OrderByClause.UnionSubRowSet) {
                RowSet rowSet;
                rowSet = ((OrderByClause.UnionSubRowSet) table).left;
                if (rowSet instanceof DerivedTable && validateLateralRef((DerivedTable) rowSet, aliasSet)) {
                    contains = true;
                } else {
                    contains = (rowSet = ((OrderByClause.UnionSubRowSet) table).right) instanceof DerivedTable
                            && validateLateralRef((DerivedTable) rowSet, aliasSet);
                }
            } else if (table instanceof CriteriaContextSpec) {
                contains = ((StatementContext) ((CriteriaContextSpec) table).getContext()).containsAnyTableAlias(aliasSet);
            } else {
                contains = false;
            }
            return contains;
        }


        /**
         * <p>
         * add nested {@link TabularItem} to {@link #aliasToBlock}
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
                if (tableItem instanceof _NestedItems) {
                    if (_StringUtils.hasText(alias)) {
                        throw ContextStack.criteriaError(this, _Exceptions::nestedItemsAliasHasText, alias);
                    }
                    this.addNestedItems((_NestedItems) tableItem);
                } else if (aliasToBlock.putIfAbsent(alias, block) != null) {
                    throw ContextStack.criteriaError(this, _Exceptions::tableAliasDuplication, alias);
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
                }

            }//for


        }


    } // JoinableContext

    private static abstract class JoinableDeferCommandContext extends JoinableContext {


        private JoinableDeferCommandContext(Dialect dialect, @Nullable CriteriaContext outerContext) {
            super(dialect, outerContext);
        }


        abstract void endDeferCommand();


    } // JoinableDeferCommandContext


    static abstract class InsertContext extends StatementContext {

        private TableMeta<?> insertTable;

        private String tableAlias;

        private String rowAlias;

        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToFieldMap;

        private Map<FieldMeta<?>, Expression> insertValueFieldMap;

        private List<FieldMeta<?>> columnlist;

        private Map<String, FieldMeta<?>> columnMap;

        private InsertContext(Dialect dialect, @Nullable CriteriaContext outerContext) {
            super(dialect, outerContext);
        }


        @Override
        public final void singleDmlTable(final @Nullable TableMeta<?> table, final @Nullable String tableAlias) {
            if (table == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.insertTable == null) {
                this.insertTable = table;
            } else if (this.insertTable != table) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.tableAlias == null) {
                this.tableAlias = tableAlias;
            } else if (!tableAlias.equals(this.tableAlias)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
        }


        @Override
        public final void insertRowAlias(final @Nullable TableMeta<?> table, final @Nullable String rowAlias) {

            if (table == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.insertTable == null) {
                this.insertTable = table;
            } else if (this.insertTable != table) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            if (rowAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (rowAlias.equals(this.tableAlias)) {
                String m = String.format("INSERT statement row alias[%s] couldn't be equals to table alias[%s]",
                        rowAlias, this.tableAlias);
                throw ContextStack.clearStackAndCriteriaError(m);
            } else if (this.rowAlias == null) {
                this.rowAlias = rowAlias;
            } else if (!rowAlias.equals(this.rowAlias)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

        }

        @Override
        public final void insertColumnList(final List<FieldMeta<?>> columnList) {
            if (this.columnlist != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.columnlist = columnList;
        }


        @SuppressWarnings("unchecked")
        @Override
        public final <T> QualifiedField<T> field(final @Nullable String tableAlias, final @Nullable FieldMeta<T> field) {

            if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (field == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToFieldMap = this.aliasToFieldMap;

            Map<FieldMeta<?>, QualifiedField<?>> fieldMap = null;

            final QualifiedField<T> qualifiedField;

            QualifiedField<?> tempField;
            if (aliasToFieldMap != null
                    && (fieldMap = aliasToFieldMap.get(tableAlias)) != null
                    && (tempField = fieldMap.get(field)) != null) {
                qualifiedField = (QualifiedField<T>) tempField;
            } else if (field.tableMeta() == this.insertTable
                    && (tableAlias.equals(this.tableAlias) || tableAlias.equals(this.rowAlias))) {
                qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                if (fieldMap == null) {
                    fieldMap = _Collections.hashMap();
                    if (aliasToFieldMap == null) {
                        this.aliasToFieldMap = aliasToFieldMap = _Collections.hashMap();
                    }
                    aliasToFieldMap.put(tableAlias, fieldMap);
                }
                fieldMap.put(field, qualifiedField);
            } else if (this instanceof SubContext) {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                qualifiedField = outerContext.outerOrMoreOuterField(tableAlias, field);
                if (qualifiedField == null) {
                    throw unknownQualifiedField(tableAlias, field);
                }
            } else {
                throw unknownQualifiedField(tableAlias, field);
            }
            return qualifiedField;
        }

        @Override
        public final DerivedField refField(final @Nullable String derivedAlias, final @Nullable String fieldName) {
            if (derivedAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (fieldName == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (!(this instanceof SubContext)) {
                throw unknownDerivedField(derivedAlias, fieldName);
            }

            final DerivedField field;
            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;
            field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
            if (field == null) {
                throw unknownDerivedField(derivedAlias, fieldName);
            }
            return field;
        }

        @Override
        public final Expression insertValueField(final FieldMeta<?> field, final Function<FieldMeta<?>, Expression> function) {
            Map<FieldMeta<?>, Expression> insertValueFieldMap = this.insertValueFieldMap;
            if (insertValueFieldMap == null) {
                this.insertValueFieldMap = insertValueFieldMap = _Collections.hashMap();
            }
            return insertValueFieldMap.computeIfAbsent(field, function);
        }

        /*-------------------below package methods -------------------*/


        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        final <T> QualifiedField<T> outerOrMoreOuterField(final String tableAlias, final FieldMeta<T> field) {
            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToFieldMap = this.aliasToFieldMap;

            Map<FieldMeta<?>, QualifiedField<?>> fieldMap = null;

            final QualifiedField<T> qualifiedField;

            QualifiedField<?> tempField;
            if (aliasToFieldMap != null
                    && (fieldMap = aliasToFieldMap.get(tableAlias)) != null
                    && (tempField = fieldMap.get(field)) != null) {
                qualifiedField = (QualifiedField<T>) tempField;
            } else if (field.tableMeta() == this.insertTable
                    && (tableAlias.equals(this.tableAlias) || tableAlias.equals(this.rowAlias))) {
                qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                if (fieldMap == null) {
                    fieldMap = _Collections.hashMap();
                    if (aliasToFieldMap == null) {
                        this.aliasToFieldMap = aliasToFieldMap = _Collections.hashMap();
                    }
                    aliasToFieldMap.put(tableAlias, fieldMap);
                }
                fieldMap.put(field, qualifiedField);
            } else if (this instanceof SubContext) {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                qualifiedField = outerContext.outerOrMoreOuterField(tableAlias, field);
            } else {
                qualifiedField = null;
            }
            return qualifiedField;
        }

        @Nullable
        @Override
        final DerivedField refOuterOrMoreOuterField(final String derivedAlias, final String fieldName) {

            final DerivedField field;
            if (this instanceof SubContext) {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
            } else {
                field = null;
            }
            return field;
        }


        @Override
        final void validateFieldFromSubContext(final QualifiedField<?> field) {
            final String fieldTableAlias = field.tableAlias();
            if (field.tableMeta() != this.insertTable
                    || !(fieldTableAlias.equals(this.rowAlias) || fieldTableAlias.equals(this.tableAlias))) {
                throw unknownQualifiedField(field);
            } else if (this.columnlist != null &&
                    this.getOrCreateColumnMap().get(field.fieldName()) != field.fieldMeta()) {
                throw unknownQualifiedField(field);
            }
        }

        @Override
        final List<_TabularBlock> onEndContext() {
            // can't validate field,because field possibly from outer context,{@link _SqlContext} validate this.
            final Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToFieldMap = this.aliasToFieldMap;
            if (aliasToFieldMap != null) {
                aliasToFieldMap.clear();
                this.aliasToFieldMap = null;
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

                columnMap = _Collections.hashMap((int) (columnSize / 0.75F));
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
         * @see #primaryInsertContext(Dialect, ArmyStmtSpec)
         */
        private PrimaryInsertContext(Dialect dialect) {
            super(dialect, null);
        }


    }// PrimaryInsertContext


    /**
     * @see #subInsertContext(Dialect, ArmyStmtSpec, CriteriaContext)
     */
    private static final class SubInsertContext extends InsertContext implements SubContext {

        private SubInsertContext(Dialect dialect, CriteriaContext outerContext) {
            super(dialect, outerContext);
        }


    }//SubInsertContext


    private static abstract class SingleDmlContext extends StatementContext {

        private TableMeta<?> targetTable;

        private String targetAlias;

        private Map<FieldMeta<?>, QualifiedField<?>> fieldMap;

        private SingleDmlContext(Dialect dialect, @Nullable CriteriaContext outerContext) {
            super(dialect, outerContext);
        }

        @Override
        public final void singleDmlTable(final @Nullable TableMeta<?> table, @Nullable final String tableAlias) {
            if (table == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.targetTable == null) {
                this.targetTable = table;
            } else if (this.targetTable != table) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.targetAlias == null) {
                this.targetAlias = tableAlias;
            } else if (!tableAlias.equals(this.targetAlias)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }


        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> QualifiedField<T> field(final @Nullable String tableAlias, final @Nullable FieldMeta<T> field) {
            if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (field == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            Map<FieldMeta<?>, QualifiedField<?>> fieldMap = this.fieldMap;

            final QualifiedField<T> qualifiedField;

            QualifiedField<?> tempField;
            if (fieldMap != null && (tempField = fieldMap.get(field)) != null) {
                qualifiedField = (QualifiedField<T>) tempField;
            } else if (this.targetTable == field.tableMeta()) {
                qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                if (fieldMap == null) {
                    this.fieldMap = fieldMap = _Collections.hashMap();
                }
                fieldMap.put(field, qualifiedField);
            } else if (this instanceof SubContext) {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                qualifiedField = outerContext.outerOrMoreOuterField(tableAlias, field);
            } else {
                qualifiedField = null;
            }

            if (qualifiedField == null) {
                throw unknownQualifiedField(tableAlias, field);
            }
            return qualifiedField;
        }

        @Override
        public final DerivedField refField(final @Nullable String derivedAlias, @Nullable String fieldName) {
            if (derivedAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (fieldName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            if (!(this instanceof SubContext)) {
                throw unknownDerivedField(derivedAlias, fieldName);
            }

            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final DerivedField field;
            field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
            if (field == null) {
                throw unknownDerivedField(derivedAlias, fieldName);
            }
            return field;
        }

        /*-------------------below package methods -------------------*/


        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        final <T> QualifiedField<T> outerOrMoreOuterField(final String tableAlias, final FieldMeta<T> field) {

            Map<FieldMeta<?>, QualifiedField<?>> fieldMap = this.fieldMap;

            final QualifiedField<T> qualifiedField;

            QualifiedField<?> tempField;
            if (fieldMap != null && (tempField = fieldMap.get(field)) != null) {
                qualifiedField = (QualifiedField<T>) tempField;
            } else if (this.targetTable == field.tableMeta()) {
                qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                if (fieldMap == null) {
                    this.fieldMap = fieldMap = _Collections.hashMap();
                }
                fieldMap.put(field, qualifiedField);
            } else if (this instanceof SubContext) {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                qualifiedField = outerContext.outerOrMoreOuterField(tableAlias, field);
            } else {
                qualifiedField = null;
            }
            return qualifiedField;
        }

        @Nullable
        @Override
        final DerivedField refOuterOrMoreOuterField(final String derivedAlias, final String fieldName) {
            final DerivedField field;
            if (this instanceof SubContext) {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;
                field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
            } else {
                field = null;
            }
            return field;
        }


        @Override
        final void validateFieldFromSubContext(final QualifiedField<?> field) {
            if (isInWithClause()
                    || field.tableMeta() != this.targetTable
                    || !field.tableAlias().equals(this.targetAlias)) {
                throw unknownQualifiedField(field);
            }
        }

        @Override
        final List<_TabularBlock> onEndContext() {
            final Map<FieldMeta<?>, QualifiedField<?>> fieldMap = this.fieldMap;
            if (fieldMap != null) {
                fieldMap.clear();
                this.fieldMap = null;
            }
            return Collections.emptyList();
        }

        /*-------------------below private methods-------------------*/


    } // SingleDmlContext

    /**
     * @see #primarySingleDmlContext(Dialect, ArmyStmtSpec)
     */
    private static final class PrimarySingleDmlContext extends SingleDmlContext implements PrimaryContext {

        private PrimarySingleDmlContext(Dialect dialect, @Nullable PrimaryDispatcherContext outerContext) {
            super(dialect, outerContext);
        }

    } // PrimarySingleDmlContext

    private static final class SubSingleDmlContext extends SingleDmlContext implements SubContext {

        /**
         * @see #subSingleDmlContext(Dialect, CriteriaContext)
         */
        private SubSingleDmlContext(Dialect dialect, CriteriaContext outerContext) {
            super(dialect, outerContext);
        }


    } // SubSingleDmlContext

    /**
     * <p>
     * This class representing multi-table dml context.
     *
     * @since 0.6.0
     */
    private static abstract class MultiDmlContext extends JoinableContext {

        private MultiDmlContext(Dialect dialect, @Nullable CriteriaContext outerContext) {
            super(dialect, outerContext);
        }


    }// MultiDmlContext

    private static final class PrimaryMultiDmlContext extends MultiDmlContext implements PrimaryContext {

        private PrimaryMultiDmlContext(Dialect dialect) {
            super(dialect, null);
        }

    }//PrimaryMultiDmlContext


    private static abstract class JoinableSingleDmlContext extends JoinableDeferCommandContext {

        private TableMeta<?> targetTable;

        private String tableAlias;

        private Runnable deferCommandClause;

        private JoinableSingleDmlContext(Dialect dialect, @Nullable CriteriaContext outerContext) {
            super(dialect, outerContext);
        }

        @Override
        public final void singleDmlTable(final @Nullable TableMeta<?> table, @Nullable final String tableAlias) {
            if (table == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.targetTable != null && table != this.targetTable) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (this.tableAlias != null && !tableAlias.equals(this.tableAlias)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.tableAlias = tableAlias;
            this.targetTable = table;
        }

        @Override
        public final void registerDeferCommandClause(final Runnable deferCommandClause) {
            final Runnable command = this.deferCommandClause;
            if (command == null) {
                this.deferCommandClause = deferCommandClause;
            } else {
                // for multi dynamic SET clause
                this.deferCommandClause = () -> {
                    command.run();
                    deferCommandClause.run();
                };
            }

        }


        @Override
        final void endDeferCommand() {
            final Runnable deferCommandClause = this.deferCommandClause;
            if (deferCommandClause != null) {
                this.deferCommandClause = null;
                deferCommandClause.run();
            }
        }


    } //JoinableSingleDmlContext


    private static abstract class JoinableSingleUpdateContext extends JoinableSingleDmlContext {

        private JoinableSingleUpdateContext(Dialect dialect, @Nullable CriteriaContext outerContext) {
            super(dialect, outerContext);
        }


    } // JoinableSingleUpdateContext


    private static final class PrimaryJoinableSingleUpdateContext extends JoinableSingleUpdateContext
            implements PrimaryContext {

        private PrimaryJoinableSingleUpdateContext(Dialect dialect) {
            super(dialect, null);
        }

    } // PrimaryJoinableSingleUpdateContext

    private static final class SubJoinableSingleUpdateContext extends JoinableSingleUpdateContext
            implements SubContext {

        private SubJoinableSingleUpdateContext(Dialect dialect, CriteriaContext outerContext) {
            super(dialect, outerContext);
            Objects.requireNonNull(outerContext);
        }

    } // SubJoinableSingleUpdateContext

    private static final class PrimaryJoinableSingleDeleteContext extends JoinableSingleDmlContext
            implements PrimaryContext {

        private PrimaryJoinableSingleDeleteContext(Dialect dialect) {
            super(dialect, null);
        }

    } // PrimaryJoinableSingleDmlContext

    private static final class SubJoinableSingleDeleteContext extends JoinableSingleDmlContext
            implements SubContext {

        private SubJoinableSingleDeleteContext(Dialect dialect, CriteriaContext outerContext) {
            super(dialect, outerContext);
            Objects.requireNonNull(outerContext);
        }

    } //SubJoinableSingleDmlContext


    private static abstract class SimpleQueryContext extends JoinableDeferCommandContext {

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

        private Runnable deferCommandClause;

        private Runnable selectClauseEndListener;

        /**
         * @see #callDeferCommandClauseIfNeed()
         */
        private boolean deferSelectClauseIng;

        private SimpleQueryContext(Dialect dialect, final @Nullable CriteriaContext outerContext,
                                   final @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext);
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
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return selectItemList;
        }

        @Override
        public final void addSelectClauseEndListener(final Runnable listener) {
            if (this.isSelectClauseEnd()) {
                throw ContextStack.clearStackAndCastCriteriaApi();
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
        public final void registerDeferCommandClause(final Runnable deferCommandClause) {
            if (this.deferCommandClause != null || this.selectItemList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.deferCommandClause = deferCommandClause;
        }


        @Override
        public final void onAddDerivedGroup(final @Nullable String derivedAlis) {
            if (derivedAlis == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            this.onAddSelectItem(this.createDerivedFieldGroup(derivedAlis));
        }


        @Override
        public final CriteriaContext onAddSelectItem(final @Nullable SelectItem selectItem) {
            if (selectItem == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            List<_SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                selectItemList = _Collections.arrayList();
                this.selectItemList = selectItemList;
            } else if (!(selectItemList instanceof ArrayList)) {
                final Map<Object, SelectionReference> selectionMap = this.refSelectionMap;
                if (selectionMap != null && selectionMap.size() > 0) {
                    throw invokeRefSelectionInSelectionClause();
                }
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            selectItemList.add((_SelectItem) selectItem);

            if (!(selectItem instanceof _SelectionGroup)) {
                return this;
            }

            if (!(selectItem instanceof _SelectionGroup._TableFieldGroup
                    || selectItem instanceof SelectionGroups.DerivedSelectionGroup)) {
                throw ContextStack.criteriaError(this, String.format("unknown %s", selectItem));
            }

            final _SelectionGroup group = (_SelectionGroup) selectItem;
            if (this.getOrCreateSelectionGroupMap().putIfAbsent(group.tableAlias(), group) != null) {
                String m = String.format("%s group[%s] duplication", Selection.class.getName(),
                        group.tableAlias());
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            return this;
        }

        @Override
        public final RowElement row(final @Nullable String alias, SQLs.SymbolPeriod period,
                                    final @Nullable TableMeta<?> table) {
            if (this.isSelectClauseEnd()) {
                throw ContextStack.clearStackAndCriteriaError("Error,SELECT clause have ended.");
            } else if (alias == null || table == null) {
                throw ContextStack.clearStackAndNullPointer();
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
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            return (RowElement) group;
        }


        @Override
        public final RowElement row(final @Nullable String alias, SQLs.SymbolPeriod period,
                                    SQLs.SymbolAsterisk asterisk) {
            if (this.isSelectClauseEnd()) {
                throw ContextStack.clearStackAndCriteriaError("Error,SELECT clause have ended.");
            } else if (alias == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            final Map<String, _SelectionGroup> map;
            map = this.getOrCreateSelectionGroupMap();
            _SelectionGroup group;
            group = map.get(alias);

            if (group == null) {
                group = this.createDerivedFieldGroup(alias);
                map.put(alias, group);
            } else if (!(group instanceof SelectionGroups.DerivedSelectionGroup)
                    || !alias.equals(group.tableAlias())) {
                String m = String.format("error,please check derived alias[%s] in statement.", alias);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            return (RowElement) group;
        }


        @Override
        public final Expression refSelection(final @Nullable String selectionLabel) {
            if (selectionLabel == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.deferSelectClauseIng) {
                throw currentlyCannotRefSelection(selectionLabel);
            }

            final CriteriaContext leftContext = this.leftContext;
            if (leftContext != null) {
                return leftContext.refSelection(selectionLabel);
            }
            Map<Object, SelectionReference> refSelectionMap = this.refSelectionMap;
            SelectionReference refSelection;
            if (refSelectionMap != null && (refSelection = refSelectionMap.get(selectionLabel)) != null) {
                return refSelection;
            }

            if (refSelectionMap == null) {

                this.callDeferCommandClauseIfNeed();
                this.endSelectClauseIfNeed();

                this.refSelectionMap = refSelectionMap = _Collections.hashMap();
            }

            final Selection selection;
            selection = this.getSelectionMap().get(selectionLabel);
            if (selection == null) {
                throw CriteriaUtils.unknownSelection(selectionLabel);
            }
            refSelection = new ImmutableNameRefSelection(selection);
            refSelectionMap.put(selectionLabel, refSelection);
            return refSelection;
        }

        /**
         * @param selectionOrdinal based 1 .
         */
        @Override
        public final Expression refSelection(final int selectionOrdinal) {
            if (selectionOrdinal < 1) {
                throw CriteriaUtils.unknownSelection(selectionOrdinal);
            } else if (this.deferSelectClauseIng) {
                throw currentlyCannotRefSelection(selectionOrdinal);
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

                this.callDeferCommandClauseIfNeed();
                this.endSelectClauseIfNeed();

                this.refSelectionMap = refSelectionMap = _Collections.hashMap();
            }

            final List<? extends Selection> selectionList = flatSelectItems();
            if (selectionOrdinal > selectionList.size()) {
                throw CriteriaUtils.unknownSelection(selectionOrdinal);
            }

            final Selection selection;
            selection = selectionList.get(selectionOrdinal - 1);
            assert selection != null;
            refSelection = new ImmutableOrdinalRefSelection(selectionOrdinal, selection);
            refSelectionMap.put(selectionOrdinal, refSelection);
            return refSelection;
        }

        @Override
        public final Selection selection(final String selectionLabel) {
            if (!(this instanceof SubQueryContext)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return this.getSelectionMap().get(selectionLabel);
        }

        @Override
        public final List<? extends Selection> flatSelectItems() {
            List<Selection> selectionList = this.flatSelectionList;
            if (selectionList != null) {
                return selectionList;
            }

            final List<_SelectItem> selectItemList = this.selectItemList;
            final int selectItemSize;
            if (selectItemList == null
                    || selectItemList instanceof ArrayList
                    || (selectItemSize = selectItemList.size()) == 0) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            _SelectItem selectItem;
            if (selectItemSize == 1 && (selectItem = selectItemList.get(0)) instanceof Selection) {
                selectionList = Collections.singletonList((Selection) selectItem);
            } else {
                selectionList = _Collections.arrayList(selectItemSize);

                for (int i = 0; i < selectItemSize; i++) {
                    selectItem = selectItemList.get(i);
                    if (selectItem instanceof Selection) {
                        selectionList.add((Selection) selectItem);
                    } else {
                        selectionList.addAll(((_SelectionGroup) selectItem).selectionList());
                    }
                }//for

                selectionList = _Collections.unmodifiableList(selectionList);

            }// else
            this.flatSelectionList = selectionList;
            return selectionList;
        }


        @Override
        public final void onAddWindow(final @Nullable String windowName) {
            if (windowName == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (!_StringUtils.hasText(windowName)) {
                throw ContextStack.criteriaError(this, "window name must be non-empty.");
            }
            Map<String, Boolean> windowNameMap = this.windowNameMap;
            if (windowNameMap == null) {
                windowNameMap = _Collections.hashMap();
                this.windowNameMap = windowNameMap;
            }
            if (windowNameMap.putIfAbsent(windowName, Boolean.TRUE) != null) {
                String m = String.format("window[%s] duplication.", windowName);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            final Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap != null) {
                refWindowNameMap.remove(windowName);
            }
        }

        @Override
        public final void onRefWindow(final @Nullable String windowName) {
            if (windowName == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (!_StringUtils.hasText(windowName)) {
                throw ContextStack.criteriaError(this, "couldn't reference empty window name");
            }
            final Map<String, Boolean> windowNameMap = this.windowNameMap;
            if (windowNameMap != null && windowNameMap.containsKey(windowName)) {
                return;
            }
            Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap == null) {
                refWindowNameMap = _Collections.hashMap();
                this.refWindowNameMap = refWindowNameMap;
            }
            refWindowNameMap.putIfAbsent(windowName, Boolean.TRUE);
        }


        /**
         * @see RecursiveCte#RecursiveCte(CriteriaContext, String, CriteriaContext)
         * @see BracketContext#refNonRecursivePart(RecursiveCte)
         */
        @Override
        final _SelectionMap refNonRecursivePart(final RecursiveCte cte) {
            final StatementContext outerContext = this.outerContext, leftContext = this.leftContext;
            assert outerContext != null; // fail ,bug
            final _SelectionMap selectionMap;
            if (leftContext != null) {
                selectionMap = leftContext.refNonRecursivePart(cte);
            } else if (cte.sourceContext == this) {
                if (outerContext == cte.topContextOfCte || !this.hasOuterLeftContext(cte.topContextOfCte)) {
                    throw notExistsNonRecursivePart(cte.name);
                }
                selectionMap = outerContext.refNonRecursivePart(cte);
            } else if (outerContext == cte.topContextOfCte) {
                selectionMap = this.createNonRecursiveSelectionMap();
            } else if (this.hasOuterLeftContext(cte.topContextOfCte)) {
                selectionMap = outerContext.refNonRecursivePart(cte);
            } else {
                selectionMap = this.createNonRecursiveSelectionMap();
            }
            return selectionMap;
        }


        /**
         * select statement end event handler.
         * This method is triggered by {@link #onEndContext()}
         */
        @Override
        final void endDeferCommand() {

            // firstly
            this.callDeferCommandClauseIfNeed();

            //validate DerivedGroup list
            final Map<String, _SelectionGroup> groupMap = this.selectionGroupMap;
            if (groupMap != null && groupMap.size() > 0) {
                this.validTableGroup();
            }

            final Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap != null && refWindowNameMap.size() > 0) {
                throw unknownWindows(refWindowNameMap);
            }

            this.endSelectClauseIfNeed();

            final List<_SelectItem> selectItemList = this.selectItemList;
            assert selectItemList != null && selectItemList.size() > 0;

            this.selectionGroupMap = null;
            this.refWindowNameMap = null;
            this.windowNameMap = null;
            this.deferCommandClause = null; // finally
        }

        /*-------------------below private methods -------------------*/

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
         * @see SimpleQueryContext#refNonRecursivePart(RecursiveCte)
         */
        private boolean hasOuterLeftContext(final CriteriaContext topContext) {
            StatementContext temp = this.outerContext;
            boolean enable = false;
            while (temp != null && temp != topContext) {
                if (temp.getLeftContext() != null) {
                    enable = true;
                    break;
                }
                temp = temp.outerContext;
            }
            return enable;
        }

        /**
         * @see SimpleQueryContext#refNonRecursivePart(RecursiveCte)
         */
        private _SelectionMap createNonRecursiveSelectionMap() {
            return new _SelectionMap() {
                @Override
                public Selection refSelection(String name) {
                    return SimpleQueryContext.this.selection(name);
                }

                @Override
                public List<? extends Selection> refAllSelection() {
                    return SimpleQueryContext.this.flatSelectItems();
                }
            };

        }


        /**
         * <p>
         * This method is triggered by :
         * <ul>
         *     <li>{@link #refSelection(int)}</li>
         *     <li>{@link #refSelection(String)}</li>
         *     <li>{@link #endDeferCommand()}</li>
         * </ul>
         */
        private void callDeferCommandClauseIfNeed() {
            final Runnable deferCommandClause = this.deferCommandClause;
            if (deferCommandClause != null && this.selectItemList == null) {
                assert !this.deferSelectClauseIng;
                this.deferSelectClauseIng = true;
                deferCommandClause.run();
                this.deferSelectClauseIng = false;
                if (this.selectItemList == null) {
                    throw ContextStack.criteriaError(this, _Exceptions::selectListIsEmpty);
                }
            }

        }

        /**
         * @see #endDeferCommand()
         * @see #refSelection(String)
         * @see #onAddBlock(_TabularBlock)
         * @see #addTableBlock(_TabularBlock)
         */
        @SuppressWarnings("unchecked")
        private void endSelectClauseIfNeed() {
            final List<_SelectItem> selectItemList = this.selectItemList;
            if ((selectItemList != null && !(selectItemList instanceof ArrayList))
                    || (selectItemList == null && this.deferCommandClause != null)
                    || this.deferSelectClauseIng) {
                // have ended or defer select clause not run
                return;
            }


            final int selectItemSize;
            if (!(selectItemList != null && (selectItemSize = selectItemList.size()) > 0)) {
                // not exists select item
                throw ContextStack.clearStackAndCastCriteriaApi();
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
         * @see #row(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)
         * @see #onAddDerivedGroup(String)
         */
        private SelectionGroups.DerivedSelectionGroup createDerivedFieldGroup(final String derivedAlias) {
            final _SelectionMap selectionMap;
            selectionMap = this.getDerived(derivedAlias);
            if (selectionMap == null) {
                throw CriteriaUtils.unknownFieldDerivedGroup(this, derivedAlias);
            } else if (selectionMap instanceof RecursiveCte) {
                String m = String.format("error , recursive cte %s.* will produce endless loop", derivedAlias);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            return SelectionGroups.derivedGroup(selectionMap, derivedAlias);
        }


        /**
         * @see #endDeferCommand()
         */
        private void validTableGroup() {
            final Map<String, _SelectionGroup> groupMap = this.selectionGroupMap;
            assert groupMap != null;
            for (_SelectionGroup group : groupMap.values()) {
                if (group instanceof _SelectionGroup._TableFieldGroup) {
                    if (!((_SelectionGroup._TableFieldGroup) group).isLegalGroup(this.getTable(group.tableAlias()))) {
                        throw CriteriaUtils.unknownTableFieldGroup(this, (_SelectionGroup._TableFieldGroup) group);
                    }
                } else if (!(group instanceof SelectionGroups.DerivedSelectionGroup)) {
                    //no bug,never here
                    String m = String.format("unknown selection group %s", ClassUtils.safeClassName(group));
                    throw ContextStack.clearStackAndCriteriaError(m);
                } else if (this.getDerived(group.tableAlias()) == null) {
                    throw CriteriaUtils.unknownFieldDerivedGroup(this, group.tableAlias());
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
            if (selectionMap == null) {
                this.selectionMap = selectionMap = CriteriaUtils.createSelectionMap(flatSelectItems());
            }
            return selectionMap;
        }


    } // SimpleQueryContext

    private static final class PrimaryQueryContext extends SimpleQueryContext
            implements PrimaryContext {


        /**
         * @see #primaryQueryContext(Dialect, ArmyStmtSpec, CriteriaContext, CriteriaContext)
         */
        private PrimaryQueryContext(Dialect dialect, @Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);
        }


    }//PrimaryQueryContext


    private static final class SubQueryContext extends SimpleQueryContext
            implements SubContext {


        /**
         * @see #subQueryContext(Dialect, ArmyStmtSpec, CriteriaContext, CriteriaContext)
         */
        SubQueryContext(Dialect dialect, CriteriaContext outerContext, final @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);
            Objects.requireNonNull(outerContext);
        }


    }//SubQueryContext


    private static abstract class BracketContext extends StatementContext {

        private final StatementContext leftContext;

        private StatementContext innerContext;

        private Map<String, Boolean> outerRefMap;


        private BracketContext(Dialect dialect, final @Nullable CriteriaContext outerContext,
                               final @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext);
            this.leftContext = (StatementContext) leftContext;
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
        public final <T> QualifiedField<T> field(final String tableAlias, final FieldMeta<T> field) {
            assert this.innerContext != null; // fail ,bug

            if (this instanceof PrimaryContext) {
                // here try ref outer field
                throw unknownQualifiedField(tableAlias, field);
            }

            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final QualifiedField<T> qualifiedField;
            qualifiedField = outerContext.outerOrMoreOuterField(tableAlias, field);
            if (qualifiedField == null) {
                throw unknownQualifiedField(tableAlias, field);
            }

            addOuterRef(tableAlias);
            return qualifiedField;
        }

        @Override
        public final DerivedField refField(final String derivedAlias, final String fieldName) {
            assert this.innerContext != null; // fail ,bug

            if (this instanceof PrimaryContext) {
                // here try ref outer field
                throw unknownDerivedField(derivedAlias, fieldName);
            }

            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final DerivedField field;
            field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
            if (field == null) {
                throw unknownDerivedField(derivedAlias, fieldName);
            }

            addOuterRef(derivedAlias);
            return field;
        }


        @Override
        public final Expression refSelection(final String selectionLabel) {
            final CriteriaContext leftContext = this.leftContext, innerContext = this.innerContext;
            final Expression selection;
            if (innerContext == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (leftContext == null) {
                selection = innerContext.refSelection(selectionLabel);
            } else {
                selection = leftContext.refSelection(selectionLabel);
            }
            return selection;
        }

        @Override
        public final Expression refSelection(final int selectionOrdinal) {
            final CriteriaContext leftContext = this.leftContext, innerContext = this.innerContext;
            final Expression selection;
            if (innerContext == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
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
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (innerContext == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (innerContext.getOuterContext() != this) {
                //no bug,never here
                throw ContextStack.clearStackAndCriteriaError("innerContext not match");
            }
            this.innerContext = (StatementContext) innerContext;
        }


        /*-------------------below package methods -------------------*/

        @Nullable
        @Override
        final DerivedField refOuterOrMoreOuterField(final String derivedAlias, final String fieldName) {
            if (this instanceof PrimaryContext) {
                return null;
            }
            // from inner sub context invoking.
            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final DerivedField outerField;
            outerField = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
            if (outerField != null) {
                addOuterRef(derivedAlias);
            }
            return outerField;
        }

        @Nullable
        @Override
        final <T> QualifiedField<T> outerOrMoreOuterField(final String tableAlias, final FieldMeta<T> field) {
            if (this instanceof PrimaryContext) {
                return null;
            }
            // from inner sub context invoking.
            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final QualifiedField<T> outerField;
            outerField = outerContext.outerOrMoreOuterField(tableAlias, field);
            if (outerField != null) {
                addOuterRef(tableAlias);
            }
            return outerField;
        }

        @Override
        final void validateFieldFromSubContext(final QualifiedField<?> field) {
            final StatementContext outerContext = this.outerContext;
            if (outerContext == null) {
                throw unknownQualifiedField(field);
            }

            outerContext.validateFieldFromSubContext(field);
        }


        /**
         * @see RecursiveCte#RecursiveCte(CriteriaContext, String, CriteriaContext)
         * @see SimpleQueryContext#refNonRecursivePart(RecursiveCte)
         */
        @Override
        final _SelectionMap refNonRecursivePart(final RecursiveCte cte) {
            final StatementContext outerContext = this.outerContext, leftContext = this.leftContext;
            final StatementContext innerContext = this.innerContext;
            assert outerContext != null; // fail ,bug
            final _SelectionMap selectionMap;
            if (leftContext != null) {
                selectionMap = leftContext.refNonRecursivePart(cte);
            } else if (outerContext != cte.topContextOfCte) {
                selectionMap = outerContext.refNonRecursivePart(cte);
            } else if (innerContext == null) {
                throw notExistsNonRecursivePart(cte.name);
            } else {
                selectionMap = innerContext.refNonRecursivePart(cte);
            }
            return selectionMap;
        }

        @Override
        final List<_TabularBlock> onEndContext() {
            if (this.innerContext == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return Collections.emptyList();
        }

        @Override
        final boolean containsAnyTableAlias(final Set<String> outerTabularItemAlias) {
            final StatementContext innerContext = this.innerContext;
            if (innerContext == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            final Map<String, Boolean> outerRefMap = this.outerRefMap;

            boolean contains = false;
            if (outerRefMap != null) {
                for (String tabularItemAlias : outerTabularItemAlias) {
                    if (outerRefMap.containsKey(tabularItemAlias)) {
                        contains = true;
                        break;
                    }
                }
            }
            return contains || innerContext.containsAnyTableAlias(outerTabularItemAlias);
        }

        private void addOuterRef(final String tabularAlias) {
            Map<String, Boolean> outerRefMap = this.outerRefMap;
            if (outerRefMap == null) {
                this.outerRefMap = outerRefMap = _Collections.hashMap();
            } else if (!(outerRefMap instanceof HashMap)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            outerRefMap.putIfAbsent(tabularAlias, Boolean.TRUE);
        }


    } // BracketContext


    private static final class PrimaryBracketContext extends BracketContext
            implements PrimaryContext {

        /**
         * @see #bracketContext(ArmyStmtSpec)
         */
        private PrimaryBracketContext(Dialect dialect, @Nullable CriteriaContext outerContext,
                                      @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);
        }

    }//PrimaryBracketContext

    private static final class SubBracketContext extends BracketContext implements SubContext {

        /**
         * @see #bracketContext(ArmyStmtSpec)
         */
        private SubBracketContext(Dialect dialect, CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);
        }


    }//SubBracketContext


    private static abstract class ValuesContext extends StatementContext {

        private final StatementContext leftContext;

        /**
         * couldn't clear this field,because {@link  SQLs#refSelection(String)} and {@link  BracketContext#refSelection(String)}
         */
        private List<? extends _Selection> selectionList;

        private boolean inRowClause;

        /**
         * couldn't clear this field,because {@link  SQLs#refSelection(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<String, Selection> selectionMap;

        /**
         * couldn't clear this field,because {@link  SQLs#refSelection(String)} and {@link  BracketContext#refSelection(String)}
         */
        private Map<Object, SelectionReference> refSelectionMap;

        private Map<String, Boolean> outerRefMap;


        private ValuesContext(Dialect dialect, @Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext);
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
            if (leftContext == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return leftContext;
        }

        @Override
        public final void registerValuesSelectionList(final List<? extends _Selection> selectionList) {
            assert selectionList.size() > 0 && !(selectionList instanceof ArrayList);
            if (this.selectionList != null) {
                // no bug ,never here
                throw new IllegalStateException();
            }
            this.selectionList = selectionList;
            this.inRowClause = false;
        }

        @Override
        public final void onValuesRowStart() {
            this.inRowClause = true;
        }

        @Override
        public final void onValuesRowEnd() {
            this.inRowClause = false;
        }


        @Override
        public final Expression refSelection(final int selectionOrdinal) {
            if (this.inRowClause) {
                throw currentlyCannotRefSelection(selectionOrdinal);
            } else if (selectionOrdinal < 1) {
                throw CriteriaUtils.unknownSelection(selectionOrdinal);
            }

            final CriteriaContext leftContext = this.leftContext;
            if (leftContext != null) {
                return leftContext.refSelection(selectionOrdinal);
            }

            Map<Object, SelectionReference> refSelectionMap = this.refSelectionMap;
            SelectionReference reference;
            if (refSelectionMap == null) {
                this.refSelectionMap = refSelectionMap = _Collections.hashMap();
                reference = null;
            } else {
                reference = refSelectionMap.get(selectionOrdinal);
            }

            if (reference != null) {
                return reference;
            }

            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                throw currentlyCannotRefSelection(selectionOrdinal);
            }

            if (selectionOrdinal > selectionList.size()) {
                throw CriteriaUtils.unknownSelection(selectionOrdinal);
            }

            reference = new ImmutableOrdinalRefSelection(selectionOrdinal, selectionList.get(selectionOrdinal - 1));
            refSelectionMap.put(selectionOrdinal, reference);
            return reference;
        }

        @Override
        public final Expression refSelection(final @Nullable String selectionLabel) {
            if (selectionLabel == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.inRowClause) {
                throw currentlyCannotRefSelection(selectionLabel);
            }

            final CriteriaContext leftContext = this.leftContext;
            if (leftContext != null) {
                return leftContext.refSelection(selectionLabel);
            }

            Map<Object, SelectionReference> refSelectionMap = this.refSelectionMap;
            SelectionReference reference;
            if (refSelectionMap == null) {
                this.refSelectionMap = refSelectionMap = _Collections.hashMap();
                reference = null;
            } else {
                reference = refSelectionMap.get(selectionLabel);
            }

            if (reference != null) {
                return reference;
            }

            final Selection selection;
            selection = selection(selectionLabel);
            if (selection == null) {
                throw CriteriaUtils.unknownSelection(selectionLabel);
            }

            reference = new ImmutableNameRefSelection(selection);
            refSelectionMap.put(selectionLabel, reference);
            return reference;
        }

        @Override
        public final Selection selection(final @Nullable String selectionLabel) {
            if (selectionLabel == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                final List<? extends Selection> selectionList = this.selectionList;
                if (selectionList == null) {
                    throw currentlyCannotRefSelection(selectionLabel);
                }
                this.selectionMap = selectionMap = CriteriaUtils.createSelectionMap(selectionList);
            }
            return selectionMap.get(selectionLabel);
        }


        @Override
        public final List<? extends _SelectItem> selectItemList() {
            final List<? extends _Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return selectionList;
        }


        @Override
        public final List<? extends Selection> flatSelectItems() {
            final List<? extends _Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return selectionList;
        }

        @Override
        public final <T> QualifiedField<T> field(final String tableAlias, final FieldMeta<T> field) {
            if (this instanceof PrimaryContext) {
                throw unknownQualifiedField(tableAlias, field);
            }
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;

            final QualifiedField<T> outerField;
            outerField = outerContext.field(tableAlias, field); // here, dont' use outerOrMoreOuterField() method, for example : SELECT clause
            addOuterRef(tableAlias);
            return outerField;
        }

        @Override
        public final DerivedField refField(final String derivedAlias, final String fieldName) {
            if (this instanceof PrimaryContext) {
                throw unknownDerivedField(derivedAlias, fieldName);
            }
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;

            final DerivedField outerField;
            outerField = outerContext.refField(derivedAlias, fieldName);  // here, dont' use refOuterOrMoreOuterField() method, for example : SELECT clause
            addOuterRef(derivedAlias);
            return outerField;
        }


        @Override
        final void validateFieldFromSubContext(final QualifiedField<?> field) {
            if (this instanceof PrimaryContext) {
                throw unknownQualifiedField(field);
            }
            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;
            outerContext.validateFieldFromSubContext(field);
        }

        @Override
        final void onEndContextBeforeCommand() {
            if (this.selectionList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.selectionList = Collections.emptyList();
        }

        /*-------------------below package methods -------------------*/

        @Nullable
        @Override
        final DerivedField refOuterOrMoreOuterField(final String derivedAlias, final String fieldName) {
            if (this instanceof PrimaryContext) {
                return null;
            }
            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final DerivedField field;
            field = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
            if (field != null) {
                addOuterRef(derivedAlias);
            }
            return field;
        }

        @Nullable
        @Override
        final <T> QualifiedField<T> outerOrMoreOuterField(final String tableAlias, final FieldMeta<T> field) {
            if (this instanceof PrimaryContext) {
                return null;
            }
            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final QualifiedField<T> outerField;
            outerField = outerContext.outerOrMoreOuterField(tableAlias, field);
            if (outerField != null) {
                addOuterRef(tableAlias);
            }
            return outerField;
        }

        @Override
        final List<_TabularBlock> onEndContext() {
            return Collections.emptyList();
        }

        @Override
        final boolean containsAnyTableAlias(final Set<String> outerTabularItemAlias) {
            if (this instanceof PrimaryContext) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            final Map<String, Boolean> outerRefMap = this.outerRefMap;

            if (outerRefMap == null) {
                return false;
            }
            boolean contain = false;
            for (String outerAlias : outerTabularItemAlias) {
                if (outerRefMap.containsKey(outerAlias)) {
                    contain = true;
                    break;
                }
            }
            return contain;
        }

        /**
         * @see #field(String, FieldMeta)
         * @see #refField(String, String)
         */
        private void addOuterRef(final String outerTableAlias) {
            Map<String, Boolean> outerRefMap = this.outerRefMap;
            if (outerRefMap == null) {
                this.outerRefMap = outerRefMap = _Collections.hashMap();
            } else if (!(outerRefMap instanceof HashMap)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            outerRefMap.putIfAbsent(outerTableAlias, Boolean.TRUE);
        }


    } // ValuesContext


    private static final class PrimaryValuesContext extends ValuesContext implements PrimaryContext {

        /**
         * @see #primaryValuesContext(Dialect, ArmyStmtSpec, CriteriaContext, CriteriaContext)
         */
        private PrimaryValuesContext(Dialect dialect, @Nullable CriteriaContext outerContext,
                                     @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);
        }


    } // PrimaryValuesContext


    private static final class SubValuesContext extends ValuesContext implements SubContext {

        /**
         * @see #subValuesContext(Dialect, ArmyStmtSpec, CriteriaContext, CriteriaContext)
         */
        private SubValuesContext(Dialect dialect, CriteriaContext outerContext,
                                 @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);
            Objects.requireNonNull(outerContext);
        }


    } // SubValuesContext


    private static final class OtherPrimaryContext extends StatementContext implements PrimaryContext {

        private OtherPrimaryContext(Dialect dialect) {
            super(dialect, null);
        }


    } // OtherPrimaryContext


    /**
     * currently ,just for postgre merge statement
     */
    private static final class PrimaryJoinableMergeContext extends StatementContext implements PrimaryContext {

        private TableMeta<?> targetTable;

        private String targetAlias;

        private _TabularBlock sourceBlock;

        private Map<String, DerivedField> derivedFieldMap;

        /**
         * can't validate field,because field possibly from outer context,{@link _SqlContext} validate this.
         */
        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap;

        /**
         * @see #primaryJoinableMergeContext(Dialect)
         */
        private PrimaryJoinableMergeContext(Dialect dialect) {
            super(dialect, null);
        }

        @Override
        public void singleDmlTable(final @Nullable TableMeta<?> table, final @Nullable String tableAlias) {

            if (table == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.targetTable == null) {
                this.targetTable = table;
            } else if (this.targetTable != table) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }


            if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.targetAlias == null) {
                this.targetAlias = tableAlias;
            } else if (!tableAlias.equals(this.targetAlias)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
        }

        @Override
        public void onAddBlock(final _TabularBlock block) {
            if (this.sourceBlock != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.sourceBlock = block;
        }

        @Override
        public TableMeta<?> getTable(final String tableAlias) {
            final TableMeta<?> table;

            final _TabularBlock block;
            final TabularItem tabularItem;
            if (tableAlias.equals(this.targetAlias)) {
                table = this.targetTable;
            } else if ((block = this.sourceBlock) == null) {
                table = null;
            } else if (!tableAlias.equals(block.alias())) {
                table = null;
            } else if ((tabularItem = block.tableItem()) instanceof TableMeta) {
                table = (TableMeta<?>) tabularItem;
            } else {
                table = null;
            }
            return table;
        }

        @Override
        public _SelectionMap getDerived(final String derivedAlias) {
            final _TabularBlock block = this.sourceBlock;
            final _SelectionMap selectionMap;
            if (block == null || !derivedAlias.equals(block.alias())) {
                selectionMap = null;
            } else {
                selectionMap = obtainSelectionMapFromBlock(block);
            }
            return selectionMap;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> QualifiedField<T> field(final @Nullable String tableAlias, final @Nullable FieldMeta<T> field) {
            if (tableAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (field == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToFieldMap = this.aliasFieldMap;

            Map<FieldMeta<?>, QualifiedField<?>> fieldMap = null;

            final QualifiedField<T> qualifiedField;

            QualifiedField<?> tempField;
            if (aliasToFieldMap != null
                    && (fieldMap = aliasToFieldMap.get(tableAlias)) != null
                    && (tempField = fieldMap.get(field)) != null) {
                qualifiedField = (QualifiedField<T>) tempField;
            } else if (getTable(tableAlias) == field.tableMeta()) {
                qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                if (fieldMap == null) {
                    fieldMap = _Collections.hashMap();
                    if (aliasToFieldMap == null) {
                        this.aliasFieldMap = aliasToFieldMap = _Collections.hashMap();
                    }
                    aliasToFieldMap.put(tableAlias, fieldMap);
                }
                fieldMap.put(field, qualifiedField);
            } else {
                throw unknownQualifiedField(tableAlias, field);
            }
            return qualifiedField;
        }


        @Override
        public DerivedField refField(final @Nullable String derivedAlias, final @Nullable String fieldName) {
            if (derivedAlias == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (fieldName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }

            Map<String, DerivedField> derivedFieldMap = this.derivedFieldMap;

            final _SelectionMap selectionMap;
            final DerivedField field;
            DerivedField tempField;
            if (derivedFieldMap != null && (tempField = derivedFieldMap.get(fieldName)) != null) {
                field = tempField;
            } else if ((selectionMap = getDerived(derivedAlias)) == null) {
                throw unknownDerivedField(derivedAlias, fieldName);
            } else {
                field = createDerivedField(selectionMap, derivedAlias, fieldName);
                if (derivedFieldMap == null) {
                    this.derivedFieldMap = derivedFieldMap = _Collections.hashMap();
                }
                derivedFieldMap.put(fieldName, field);
            }
            return field;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        <T> QualifiedField<T> outerOrMoreOuterField(final String tableAlias, final FieldMeta<T> field) {
            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasToFieldMap = this.aliasFieldMap;

            Map<FieldMeta<?>, QualifiedField<?>> fieldMap = null;

            final QualifiedField<T> qualifiedField;

            QualifiedField<?> tempField;
            if (aliasToFieldMap != null
                    && (fieldMap = aliasToFieldMap.get(tableAlias)) != null
                    && (tempField = fieldMap.get(field)) != null) {
                qualifiedField = (QualifiedField<T>) tempField;
            } else if (getTable(tableAlias) == field.tableMeta()) {
                qualifiedField = QualifiedFieldImpl.create(tableAlias, field);
                if (fieldMap == null) {
                    fieldMap = _Collections.hashMap();
                    if (aliasToFieldMap == null) {
                        this.aliasFieldMap = aliasToFieldMap = _Collections.hashMap();
                    }
                    aliasToFieldMap.put(tableAlias, fieldMap);
                }
                fieldMap.put(field, qualifiedField);
            } else {
                qualifiedField = null;
            }
            return qualifiedField;
        }


        @Nullable
        @Override
        DerivedField refOuterOrMoreOuterField(final String derivedAlias, final String fieldName) {
            Map<String, DerivedField> derivedFieldMap = this.derivedFieldMap;

            final _SelectionMap selectionMap;
            final DerivedField field;
            DerivedField tempField;
            if (derivedFieldMap != null && (tempField = derivedFieldMap.get(fieldName)) != null) {
                field = tempField;
            } else if ((selectionMap = getDerived(derivedAlias)) == null) {
                field = null;
            } else {
                field = createDerivedField(selectionMap, derivedAlias, fieldName);
                if (derivedFieldMap == null) {
                    this.derivedFieldMap = derivedFieldMap = _Collections.hashMap();
                }
                derivedFieldMap.put(fieldName, field);
            }
            return field;
        }


    } // PrimaryJoinableMergeContext


    private static abstract class DispatcherContext extends StatementContext {

        private final CriteriaContext leftContext;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private Map<String, Map<FieldMeta<?>, QualifiedField<?>>> aliasFieldMap;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private Map<String, Boolean> refWindowNameMap;

        /**
         * @see #migrateToQueryContext(SimpleQueryContext, DispatcherContext)
         */
        private List<QualifiedField<?>> fieldsFromSubContext;

        private Map<String, Boolean> outerRefMap;


        private boolean migrated;

        /**
         * @see #subDispatcherContext(CriteriaContext, CriteriaContext)
         */
        private DispatcherContext(Dialect dialect, @Nullable CriteriaContext outerContext,
                                  @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext);
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

        @SuppressWarnings("unchecked")
        @Override
        public final <T> QualifiedField<T> field(final String tableAlias, final FieldMeta<T> field) {
            if (this.migrated) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            if (this instanceof SubContext) {
                final StatementContext outerContext = this.outerContext;
                assert outerContext != null;

                final QualifiedField<T> outerField;
                outerField = outerContext.outerOrMoreOuterField(tableAlias, field);
                if (outerField != null) {
                    addOuterRef(tableAlias);
                    return outerField;
                }
            }

            Map<String, Map<FieldMeta<?>, QualifiedField<?>>> fieldMap = this.aliasFieldMap;
            if (fieldMap == null) {
                this.aliasFieldMap = fieldMap = _Collections.hashMap();
            }
            return (QualifiedField<T>) fieldMap.computeIfAbsent(tableAlias, _Collections::hashMapIgnoreKey)
                    .computeIfAbsent(field, k -> QualifiedFieldImpl.create(tableAlias, k));
        }


        @Override
        public final DerivedField refField(final String derivedAlias, final String fieldName) {
            if (this.migrated) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            // this is  DispatcherContext , so in static SELECT clause
            throw nonDeferCommandClause(this.dialect, "SELECT", derivedAlias, fieldName);
        }

        @Override
        public final void onRefWindow(final @Nullable String windowName) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            if (windowName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            Map<String, Boolean> refWindowNameMap = this.refWindowNameMap;
            if (refWindowNameMap == null) {
                this.refWindowNameMap = refWindowNameMap = _Collections.hashMap();
            }
            refWindowNameMap.putIfAbsent(windowName, Boolean.TRUE);
        }

        @Override
        public final void onAddWindow(final String windowName) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }

        @Override
        public final Expression refSelection(final @Nullable String selectionLabel) {
            if (this.migrated) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (selectionLabel == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            throw invokeRefSelectionInSelectionClause();
        }

        /**
         * @param selectionOrdinal based 1 .
         */
        @Override
        public final Expression refSelection(final int selectionOrdinal) {
            if (this.migrated) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (selectionOrdinal < 1) {
                throw CriteriaUtils.unknownSelection(selectionOrdinal);
            }
            throw invokeRefSelectionInSelectionClause();
        }

        /*-------------------below package methods -------------------*/

        @Nullable
        @Override
        final <T> QualifiedField<T> outerOrMoreOuterField(final String tableAlias, final FieldMeta<T> field) {
            if (this instanceof PrimaryContext) {
                return null;
            }
            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final QualifiedField<T> outerField;
            outerField = outerContext.outerOrMoreOuterField(tableAlias, field);
            if (outerField != null) {
                addOuterRef(tableAlias);
            }
            return outerField;
        }


        @Nullable
        @Override
        final DerivedField refOuterOrMoreOuterField(final String derivedAlias, final String fieldName) {
            if (this instanceof PrimaryContext) {
                return null;
            }
            final StatementContext outerContext = this.outerContext;
            assert outerContext != null;

            final DerivedField outerField;
            outerField = outerContext.refOuterOrMoreOuterField(derivedAlias, fieldName);
            if (outerField != null) {
                addOuterRef(derivedAlias);
            }
            return outerField;
        }

        @Override
        final void validateFieldFromSubContext(final QualifiedField<?> field) {
            if (this.migrated) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (this.isInWithClause()) {
                throw unknownQualifiedField(field);
            }
            List<QualifiedField<?>> list = this.fieldsFromSubContext;
            if (list == null) {
                this.fieldsFromSubContext = list = _Collections.arrayList();
            }
            list.add(field);
        }

        /*-------------------below private methods -------------------*/

        /**
         * @see #field(String, FieldMeta)
         * @see #refField(String, String)
         */
        private void addOuterRef(final String outerTableAlias) {
            Map<String, Boolean> outerRefMap = this.outerRefMap;
            if (outerRefMap == null) {
                this.outerRefMap = outerRefMap = _Collections.hashMap();
            } else if (!(outerRefMap instanceof HashMap)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            outerRefMap.putIfAbsent(outerTableAlias, Boolean.TRUE);
        }


    } // DispatcherContext


    private static final class SubDispatcherContext extends DispatcherContext
            implements SubContext {

        /**
         * @see #subDispatcherContext(CriteriaContext, CriteriaContext)
         */
        private SubDispatcherContext(CriteriaContext outerContext,
                                     @Nullable CriteriaContext leftContext) {
            super(outerContext.dialect(), outerContext, leftContext);
        }


    } // SubDispatcherContext


    private static final class PrimaryDispatcherContext extends DispatcherContext
            implements PrimaryContext {

        /**
         * @see #primaryDispatcherContext(Dialect, CriteriaContext, CriteriaContext)
         */
        private PrimaryDispatcherContext(Dialect dialect, @Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);

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
            return ((_Selection) this.selection).underlyingExp();
        }

        @Override
        public final String fieldName() {
            return this.selection.label();
        }

        @Override
        public final String tableAlias() {
            return this.tableName;
        }

        @Override
        public final String label() {
            return this.selection.label();
        }


        @Override
        public final void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
            final DialectParser parser = context.parser();

            sqlBuilder.append(_Constant.SPACE);

            parser.identifier(this.tableName, sqlBuilder)
                    .append(_Constant.PERIOD);
            parser.identifier(this.selection.label(), sqlBuilder);
        }

        @Override
        public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final DialectParser dialect = context.parser();

            sqlBuilder.append(_Constant.SPACE);

            dialect.identifier(this.tableName, sqlBuilder)
                    .append(_Constant.PERIOD);
            dialect.identifier(this.selection.label(), sqlBuilder);

        }

        @Override
        public final boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
            // false
            return false;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.tableName)
                    .append(_Constant.PERIOD)
                    .append(this.selection.label())
                    .toString();
        }

    } // ImmutableDerivedField


    /**
     * <p>
     * This class is designed for codec field.
     *
     * @since 0.6.0
     */
    private static final class FieldSelectionField extends ImmutableDerivedField implements FieldSelection {

        private FieldSelectionField(String tableName, FieldSelection selection) {
            super(tableName, selection);
        }

        @Override
        public FieldMeta<?> fieldMeta() {
            return ((FieldSelection) this.selection).fieldMeta();
        }


    }//FieldSelectionField


    /**
     * <p>
     * Package interface for {@link OperationExpression#as(String)}
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
        public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE);

            context.parser().identifier(this.selection.label(), sqlBuilder);
        }

        @Override
        public final boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
            // always false
            return false;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.selection.label())
                    .toString();
        }


    }// ImmutableNameRefSelection


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
        public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE)
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


    static final class RecursiveCte implements _Cte {

        private final StatementContext topContextOfCte;

        private final String name;

        /**
         * The context whose {@link CriteriaContext#refCte(String)} is invoked .
         */
        private final CriteriaContext sourceContext;

        /**
         * this instance must be anonymous class.
         * see {@link SimpleQueryContext#refNonRecursivePart(RecursiveCte)}
         */
        private final _SelectionMap nonRecursivePart;

        private _Cte actualCte;

        private Map<String, Selection> selectionMap;

        /**
         * @see StatementContext#refCte(String)
         * @see StatementContext#doRefCte(CriteriaContext, String)
         */
        private RecursiveCte(CriteriaContext topContextOfCte, String name, CriteriaContext sourceContext) {
            this.topContextOfCte = (StatementContext) topContextOfCte;
            this.name = name;
            this.sourceContext = sourceContext;
            this.nonRecursivePart = ((StatementContext) sourceContext).refNonRecursivePart(this);
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
        public Selection refSelection(final String name) {
            final _Cte actual = this.actualCte;
            final Selection selection, tempSelection;
            final int index;
            final List<? extends Selection> selectionList;

            Map<String, Selection> selectionMap;

            if (actual != null) {
                selection = actual.refSelection(name);
            } else if ((selectionMap = this.selectionMap) != null && (tempSelection = selectionMap.get(name)) != null) {
                selection = tempSelection;
            } else if ((index = this.topContextOfCte.indexOfCteField(this.name, name)) == Integer.MIN_VALUE) {
                selection = this.nonRecursivePart.refSelection(name);
            } else if (index < 0) {
                selection = null;
            } else if (index >= (selectionList = this.nonRecursivePart.refAllSelection()).size()) {
                String m = String.format("cte[%s] column alias count and query selection count not match.", this.name);
                throw ContextStack.criteriaError(this.sourceContext, m);
            } else {
                if (selectionMap == null) {
                    this.selectionMap = selectionMap = _Collections.hashMap();
                }
                selection = ArmySelections.renameSelection(selectionList.get(index), name);
                selectionMap.put(name, selection);
            }
            return selection;
        }


        @Override
        public List<? extends Selection> refAllSelection() {
            final _Cte actual = this.actualCte;
            final List<? extends Selection> list;
            if (actual == null) {
                list = this.nonRecursivePart.refAllSelection();
            } else {
                list = actual.refAllSelection();
            }
            return list;
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


        /**
         * @see StatementContext#onAddCte(_Cte)
         */
        private void onRecursiveCteEnd(final _Cte cte) {
            assert this.actualCte == null;
            assert cte.name().equals(this.name);
            this.actualCte = cte;
        }


    }// RefCte


}
