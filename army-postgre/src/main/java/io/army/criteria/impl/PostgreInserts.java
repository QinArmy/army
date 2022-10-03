package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreCteBuilder;
import io.army.criteria.postgre.PostgreInsert;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.*;

/**
 * <p>
 * This class hold the implementation of postgre insert syntax interfaces.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreInserts extends InsertSupport {

    private PostgreInserts() {
    }


    static <C> PostgreInsert._PrimaryOptionSpec<C> primaryInsert(@Nullable C criteria) {
        return new PrimaryInsertIntoClause<>(criteria);
    }

    static <C> PostgreInsert._DynamicSubInsert<C> dynamicSubInsert(final String name, CriteriaContext outContext, @Nullable C criteria) {
        return new DynamicSubInsertIntoClause<>(name, outContext, criteria);
    }

    static <C, I extends DmlInsert, Q extends DqlInsert> PostgreInsert._StaticSubOptionSpec<C, I, Q> staticSubInsert(CriteriaContext outContext, @Nullable C criteria
            , Function<SubInsert, I> dmlFunc, Function<SubReturningInsert, Q> dqlFunc) {
        return new StaticSubInsertIntoClause<>(outContext, criteria, dmlFunc, dqlFunc);
    }


    /**
     * <p>
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link NonParentTargetWhereClauseSpec}</li>
     *         <li>{@link ParentTargetWhereClauseSpec}</li>
     *     </ul>
     * </p>
     */
    private interface TargetWhereClauseSpec extends CriteriaContextSpec {

        void addConflictTargetItem(_ConflictTargetItem item);

    }

    private interface NonParentTargetWhereClauseSpec<C, T, I extends DmlInsert, Q extends DqlInsert>
            extends TargetWhereClauseSpec {

        PostgreInsert._ReturningSpec<C, I, Q> _doNothing(List<_Predicate> predicateList);

        PostgreInsert._DoUpdateSetClause<C, T, I, Q> _doUpdate(List<_Predicate> predicateList);

    }


    private static final class ConflictActionClause1<C, T, I extends DmlInsert, Q extends DqlInsert>
            extends InsertSupport.MinWhereClause<
            C,
            PostgreInsert._ConflictActionClause<C, T, I, Q>,
            PostgreInsert._ConflictTargetWhereAndSpec<C, T, I, Q>
            > implements PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q>
            , PostgreInsert._ConflictTargetWhereAndSpec<C, T, I, Q> {

        private final NonParentTargetWhereClauseSpec<C, T, I, Q> clause;


        private ConflictActionClause1(NonParentTargetWhereClauseSpec<C, T, I, Q> clause) {
            super(clause.getContext());
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ReturningSpec<C, I, Q> doNothing() {
            return this.clause._doNothing(this.endWhereClause());
        }

        @Override
        public PostgreInsert._DoUpdateSetClause<C, T, I, Q> doUpdate() {
            return this.clause._doUpdate(this.endWhereClause());
        }


    }//ConflictActionClause


    private static final class ConflictTargetItem<C, T, I extends DmlInsert, Q extends DqlInsert>
            implements PostgreInsert._ConflictCollateSpec<C, T, I, Q>
            , _ConflictTargetItem {

        private final OnConflictClause<C, T, I, Q> clause;

        private final IndexFieldMeta<T> indexColumn;

        private String collationName;

        private Boolean opclass;

        private ConflictTargetItem(OnConflictClause<C, T, I, Q> clause, IndexFieldMeta<T> indexColumn) {
            this.clause = clause;
            this.indexColumn = indexColumn;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendField(this.indexColumn);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            final String collationName = this.collationName;
            if (collationName != null) {
                sqlBuilder.append(" COLLATE ");
                context.parser().identifier(collationName, sqlBuilder);
            }
            final Boolean opclass = this.opclass;
            if (opclass != null && opclass) {
                sqlBuilder.append(" opclass");
            }
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<C, T, I, Q> collation(final @Nullable String collationName) {
            if (collationName == null) {
                throw CriteriaContextStack.nullPointer(this.clause.valuesClause.context);
            } else if (this.collationName != null || this.opclass != null) {
                throw CriteriaContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            this.collationName = collationName;
            return this;
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<C, T, I, Q> collation(Supplier<String> supplier) {
            return this.collation(supplier.get());
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<C, T, I, Q> ifCollation(Supplier<String> supplier) {
            final String collation;
            collation = supplier.get();
            if (collation != null) {
                this.collation(collationName);
            }
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetCommaSpec<C, T, I, Q> opClass() {
            if (this.opclass != null) {
                throw CriteriaContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            this.opclass = Boolean.TRUE;
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetCommaSpec<C, T, I, Q> ifOpClass(BooleanSupplier supplier) {
            if (this.opclass != null) {
                throw CriteriaContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            if (supplier.getAsBoolean()) {
                this.opclass = Boolean.TRUE;
            } else {
                this.opclass = Boolean.FALSE;
            }
            return this;
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<C, T, I, Q> comma(IndexFieldMeta<T> indexColumn) {
            if (this.opclass == null) {
                this.opclass = Boolean.FALSE;
            }
            return this.clause.leftParen(indexColumn); // create and add
        }

        @Override
        public PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q> rightParen() {
            if (this.opclass == null) {
                this.opclass = Boolean.FALSE;
            }
            return this.clause.targetItemClauseEnd();
        }


    }//ConflictTargetItem


    private static final class ConflictDoUpdateActionClause<C, T, I extends DmlInsert, Q extends DqlInsert>
            extends InsertSupport.ConflictUpdateWhereClause<
            C,
            T,
            PostgreInsert._DoUpdateWhereSpec<C, T, I, Q>,
            PostgreInsert._ReturningSpec<C, I, Q>,
            PostgreInsert._DoUpdateWhereAndSpec<C, I, Q>>
            implements PostgreInsert._DoUpdateWhereSpec<C, T, I, Q>
            , PostgreInsert._DoUpdateWhereAndSpec<C, I, Q> {

        private final OnConflictClause<C, T, I, Q> onConflictClause;

        private ConflictDoUpdateActionClause(OnConflictClause<C, T, I, Q> onConflictClause) {
            super(onConflictClause.valuesClause.context, onConflictClause.valuesClause.insertTable);
            this.onConflictClause = onConflictClause;
        }


        @Override
        public DialectStatement._StaticReturningCommaUnaryClause<Q> returning(SelectItem selectItem) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(selectItem);
        }

        @Override
        public DialectStatement._StaticReturningCommaDualClause<Q> returning(SelectItem selectItem1, SelectItem selectItem2) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(selectItem1, selectItem2);
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returningAll() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returningAll();
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returning(Consumer<Consumer<SelectItem>> consumer) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(consumer);
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(consumer);
        }

        @Override
        public I asInsert() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .asInsert();
        }

        @Override
        Dialect syntaxDialect() {
            return PostgreDialect.POSTGRE14;
        }


    }//ConflictDoUpdateActionClause


    private static final class OnConflictClause<C, T, I extends DmlInsert, Q extends DqlInsert>
            extends InsertSupport.MinWhereClause<C, PostgreInsert._ConflictActionClause<C, T, I, Q>, PostgreInsert._ConflictTargetWhereAndSpec<C, T, I, Q>>
            implements PostgreInsert._ConflictTargetOptionSpec<C, T, I, Q>
            , PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q>
            , PostgreInsert._ConflictTargetWhereAndSpec<C, T, I, Q> {

        private final PostgreComplexInsertValuesClause<C, T, I, Q> valuesClause;

        private List<_ConflictTargetItem> targetItemList;

        private String constraintName;

        private boolean doNothing;

        private OnConflictClause(PostgreComplexInsertValuesClause<C, T, I, Q> valuesClause) {
            super(valuesClause.context);
            this.valuesClause = valuesClause;
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<C, T, I, Q> leftParen(IndexFieldMeta<T> indexColumn) {
            List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList == null) {
                targetItemList = new ArrayList<>();
                this.targetItemList = targetItemList;
            } else if (!(targetItemList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.valuesClause.context);
            }
            final ConflictTargetItem<C, T, I, Q> item = new ConflictTargetItem<>(this, indexColumn);
            targetItemList.add(item);
            return item;
        }

        @Override
        public PostgreInsert._ConflictActionClause<C, T, I, Q> onConstraint(final @Nullable String constraintName) {
            if (this.constraintName != null) {
                throw CriteriaContextStack.castCriteriaApi(this.valuesClause.context);
            } else if (constraintName == null) {
                throw CriteriaContextStack.nullPointer(this.valuesClause.context);
            }
            this.constraintName = constraintName;
            return this;
        }

        @Override
        public PostgreInsert._ReturningSpec<C, I, Q> doNothing() {
            this.endWhereClause();
            this.doNothing = true;
            return this.valuesClause.conflictClauseEnd(new ConflictActionClauseResult(this));
        }

        @Override
        public PostgreInsert._DoUpdateSetClause<C, T, I, Q> doUpdate() {
            this.endWhereClause();
            return new ConflictDoUpdateActionClause<>(this);
        }

        private PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q> targetItemClauseEnd() {
            final List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList instanceof ArrayList) {
                this.targetItemList = Collections.unmodifiableList(targetItemList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.valuesClause.context);
            }
            return this;
        }

        private PostgreInsert._ReturningSpec<C, I, Q> updateActionClauseEnd(List<ItemPair> itemPairList
                , List<_Predicate> predicateList) {
            return this.valuesClause
                    .conflictClauseEnd(new ConflictActionClauseResult(this, itemPairList, predicateList));
        }


    }//OnConflictClause


    private static final class ConflictActionClauseResult implements _PostgreInsert._ConflictActionClauseResult {

        private final List<_ConflictTargetItem> targetItemList;

        private final List<_Predicate> indexPredicateList;

        private final String constraintName;

        private final boolean doNothing;

        private final List<ItemPair> itemPairList;

        private final List<_Predicate> updatePredicateList;

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?, ?> clause) {
            this.targetItemList = _CollectionUtils.safeList(clause.targetItemList);
            if (this.targetItemList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(clause.valuesClause.context);
            }
            this.indexPredicateList = clause.predicateList();
            this.constraintName = clause.constraintName;
            this.doNothing = clause.doNothing;

            this.itemPairList = Collections.emptyList();
            this.updatePredicateList = Collections.emptyList();
        }

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?, ?> clause, List<ItemPair> itemPairList
                , List<_Predicate> updatePredicateList) {
            this.targetItemList = _CollectionUtils.safeList(clause.targetItemList);
            if (this.targetItemList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(clause.valuesClause.context);
            }
            this.indexPredicateList = clause.predicateList();
            this.constraintName = clause.constraintName;
            this.doNothing = clause.doNothing;

            this.itemPairList = itemPairList;
            this.updatePredicateList = updatePredicateList;
        }

        @Override
        public List<ItemPair> updateSetClauseList() {
            return this.itemPairList;
        }

        @Override
        public List<_Predicate> updateSetPredicateList() {
            return this.updatePredicateList;
        }

        @Override
        public String constraintName() {
            return this.constraintName;
        }

        @Override
        public List<_ConflictTargetItem> conflictTargetItemList() {
            return this.targetItemList;
        }

        @Override
        public List<_Predicate> indexPredicateList() {
            return this.indexPredicateList;
        }

        @Override
        public boolean isDoNothing() {
            return this.doNothing;
        }


    }//ConflictActionClauseResult




    /*-------------------below insert after values syntax class-------------------*/


    private static final class StaticParentCteCommaClause<C>
            implements PostgreInsert._CteComma<C>
            , PostgreInsert._CteQueryComma<C> {

        private final boolean recursive;

        private final PrimaryInsertIntoClause<C> primaryClause;

        private final StaticCteComplexCommandClause<C, PostgreInsert._CteComma<C>, PostgreInsert._CteQueryComma<C>> commandClause;

        private StaticParentCteCommaClause(boolean recursive, String name, PrimaryInsertIntoClause<C> primaryClause) {
            this.recursive = recursive;
            this.primaryClause = primaryClause;
            this.commandClause = new StaticCteComplexCommandClause<>(name, primaryClause.context, this, this);
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._CteComma<C>, PostgreInsert._CteQueryComma<C>> comma(final @Nullable String name) {
            final StaticCteComplexCommandClause<C, PostgreInsert._CteComma<C>, PostgreInsert._CteQueryComma<C>> commandClause;
            commandClause = this.commandClause;
            if (name == null) {
                throw CriteriaContextStack.nullPointer(commandClause.context);
            }
            if (commandClause.name != null) {
                throw CriteriaContextStack.castCriteriaApi(commandClause.context);
            }
            commandClause.context.onStartCte(name);
            commandClause.name = name;
            commandClause.columnAliasList = null;
            return commandClause;
        }

        @Override
        public PostgreInsert._PrimaryInsertIntoClause<C> space() {
            final PrimaryInsertIntoClause<C> primaryClause = this.primaryClause;
            primaryClause.endStaticWithClause(this.recursive);
            return primaryClause;
        }

    }//StaticParentCteCommaClause


    private static final class StaticChildCteCommaClause<C, P> implements PostgreInsert._CteChildComma<C, P>
            , PostgreInsert._CteChildQueryComma<C, P> {

        private final boolean recursive;
        private final ChildInsertIntoClause<C, P> primaryClause;

        private final StaticCteComplexCommandClause<C, PostgreInsert._CteChildComma<C, P>, PostgreInsert._CteChildQueryComma<C, P>> commandClause;

        private StaticChildCteCommaClause(boolean recursive, String name, ChildInsertIntoClause<C, P> primaryClause) {
            this.recursive = recursive;
            this.primaryClause = primaryClause;
            this.commandClause = new StaticCteComplexCommandClause<>(name, primaryClause.context, this, this);
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._CteChildComma<C, P>, PostgreInsert._CteChildQueryComma<C, P>> comma(final @Nullable String name) {
            final StaticCteComplexCommandClause<C, PostgreInsert._CteChildComma<C, P>, PostgreInsert._CteChildQueryComma<C, P>> complexClause;
            complexClause = this.commandClause;
            if (complexClause.name != null) {
                //last cte don't end
                throw CriteriaContextStack.castCriteriaApi(complexClause.context);
            }
            if (name == null) {
                throw CriteriaContextStack.nullPointer(complexClause.context);
            }
            complexClause.context.onStartCte(name);
            complexClause.name = name;
            complexClause.columnAliasList = null;
            return complexClause;
        }

        @Override
        public PostgreInsert._ChildInsertIntoClause<C, P> space() {
            final ChildInsertIntoClause<C, P> primaryClause = this.primaryClause;
            primaryClause.endStaticWithClause(this.recursive);
            return primaryClause;
        }


    }//StaticParentCteCommaClause


    private static final class StaticCteComplexCommandClause<C, I extends DmlInsert, Q extends DqlInsert>
            extends CriteriaSupports.ParenStringConsumerClause<C, PostgreInsert._StaticCteAsClause<C, I, Q>>
            implements PostgreInsert._StaticCteLeftParenSpec<C, I, Q>
            , PostgreInsert._StaticCteComplexCommandSpec<C, I, Q> {

        private final I commaClause;

        private final Q commaQueryClause;

        private String name;

        private List<String> columnAliasList;


        private StaticCteComplexCommandClause(final String name, CriteriaContext primaryContext
                , I commaClause, Q commaQueryClause) {
            super(primaryContext);
            this.commaClause = commaClause;
            this.commaQueryClause = commaQueryClause;
            this.name = name;

        }

        @Override
        public PostgreInsert._StaticCteComplexCommandSpec<C, I, Q> as() {
            return this;
        }

        @Override
        public PostgreInsert._CteInsertIntoClause<C, I, Q> literalMode(LiteralMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::cteInsertEnd, this::cteReturnInsertEnd)
                    .literalMode(mode);
        }

        @Override
        public PostgreInsert._StaticSubNullOptionSpec<C, I, Q> migration(boolean migration) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::cteInsertEnd, this::cteReturnInsertEnd)
                    .migration(migration);
        }

        @Override
        public PostgreInsert._StaticSubPreferLiteralSpec<C, I, Q> nullHandle(NullHandleMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::cteInsertEnd, this::cteReturnInsertEnd)
                    .nullHandle(mode);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, I, Q> insertInto(TableMeta<T> table) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::cteInsertEnd, this::cteReturnInsertEnd)
                    .insertInto(table);
        }


        @Override
        PostgreInsert._StaticCteAsClause<C, I, Q> stringConsumerEnd(final List<String> stringList) {
            if (this.columnAliasList != null || stringList.size() == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = stringList;
            this.context.onCteColumnAlias(this.name, stringList);
            return this;
        }


        private I cteInsertEnd(final SubInsert insert) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, insert);
            //clear for next cte
            this.name = null;
            this.columnAliasList = null;
            return this.commaClause;
        }

        private Q cteReturnInsertEnd(final SubReturningInsert insert) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, insert);
            //clear for next cte
            this.name = null;
            this.columnAliasList = null;
            return this.commaQueryClause;
        }


    }//StaticCteComplexCommandClause

    private static final class PrimaryInsertIntoClause<C> extends NonQueryWithCteOption<
            C,
            PostgreInsert._PrimaryNullOptionSpec<C>,
            PostgreInsert._PrimaryPreferLiteralSpec<C>,
            PostgreInsert._PrimaryWithCteSpec<C>,
            PostgreCteBuilder,
            PostgreInsert._PrimaryInsertIntoClause<C>>
            implements PostgreInsert._PrimaryOptionSpec<C> {

        private PrimaryInsertIntoClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.context);
        }


        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._CteComma<C>, PostgreInsert._CteQueryComma<C>> with(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw CriteriaContextStack.nullPointer(context);
            }
            final boolean recursive = false;
            context.onBeforeWithClause(recursive);
            context.onStartCte(name);
            return new StaticParentCteCommaClause<>(recursive, name, this).commandClause;
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._CteComma<C>, PostgreInsert._CteQueryComma<C>> withRecursive(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw CriteriaContextStack.nullPointer(context);
            }
            final boolean recursive = true;
            context.onBeforeWithClause(recursive);
            context.onStartCte(name);
            return new StaticParentCteCommaClause<>(recursive, name, this).commandClause;
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new PrimaryComplexValuesClause<>(this, table);
        }

        @Override
        public <P> PostgreInsert._ParentTableAliasSpec<C, P> insertInto(ParentTableMeta<P> table) {
            return new ParentComplexValuesClause<>(this, table);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table) {
            return new PrimaryComplexValuesClause<>(this, table);
        }

        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.cteBuilder(recursive, this.context);
        }

    }//PrimaryInsertIntoClause

    private static final class ChildInsertIntoClause<C, P> extends ChildDynamicWithClause<
            C,
            PostgreCteBuilder,
            PostgreInsert._ChildInsertIntoClause<C, P>>
            implements PostgreInsert._ChildWithCteSpec<C, P> {

        private final ParentComplexValuesClause<C, P> parentClause;

        private ChildInsertIntoClause(ParentComplexValuesClause<C, P> parentClause) {
            super(parentClause);
            this.parentClause = parentClause;
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._CteChildComma<C, P>, PostgreInsert._CteChildQueryComma<C, P>> with(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw CriteriaContextStack.nullPointer(context);
            }
            final boolean recursive = false;
            context.onBeforeWithClause(recursive);
            context.onStartCte(name);
            return new StaticChildCteCommaClause<>(recursive, name, this).commandClause;
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._CteChildComma<C, P>, PostgreInsert._CteChildQueryComma<C, P>> withRecursive(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw CriteriaContextStack.nullPointer(context);
            }
            final boolean recursive = true;
            context.onBeforeWithClause(recursive);
            context.onStartCte(name);
            return new StaticChildCteCommaClause<>(recursive, name, this).commandClause;
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table) {
            return new PrimaryComplexValuesClause<>(this.parentClause, this, table);
        }

        @Override
        PostgreCteBuilder createCteBuilder(boolean recursive) {
            return PostgreSupports.cteBuilder(recursive, this.context);
        }


    }//ChildInsertIntoClause


    private static final class StaticSubInsertIntoClause<C, I extends DmlInsert, Q extends DqlInsert>
            extends NonQueryInsertOptionsImpl<
            PostgreInsert._StaticSubNullOptionSpec<C, I, Q>,
            PostgreInsert._StaticSubPreferLiteralSpec<C, I, Q>,
            PostgreInsert._CteInsertIntoClause<C, I, Q>>
            implements PostgreInsert._StaticSubOptionSpec<C, I, Q>
            , WithValueSyntaxOptions {


        private final Function<SubInsert, I> dmlFunction;

        private final Function<SubReturningInsert, Q> dqlFunction;

        private StaticSubInsertIntoClause(CriteriaContext outerContext, @Nullable C criteria
                , Function<SubInsert, I> dmlFunction, Function<SubReturningInsert, Q> dqlFunction) {
            super(CriteriaContexts.cteInsertContext(outerContext, criteria));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
            //just push cte,here don't need to start cte
            CriteriaContextStack.push(this.context);

        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, I, Q> insertInto(TableMeta<T> table) {
            return new SubComplexValuesClause<>(this, table, dmlFunction, dqlFunction);
        }

        @Override
        public boolean isRecursive() {
            return false;
        }

        @Override
        public List<_Cte> cteList() {
            //static with cte don't support WITH clause
            return Collections.emptyList();
        }

    }//StaticSubInsertIntoClause


    private static final class DynamicSubInsertIntoClause<C>
            extends NonQueryWithCteOption<
            C,
            PostgreInsert._DynamicSubNullOptionSpec<C, DmlInsert, DqlInsert>,
            PostgreInsert._DynamicSubPreferLiteralSpec<C, DmlInsert, DqlInsert>,
            PostgreInsert._DynamicSubWithCteSpec<C, DmlInsert, DqlInsert>,
            PostgreCteBuilder,
            PostgreInsert._CteInsertIntoClause<C, DmlInsert, DqlInsert>>
            implements PostgreInsert._DynamicSubInsert<C>
            , Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert>>
            , DmlInsert, DqlInsert {

        private final C criteria;

        final String name;

        private List<String> columnAliasList;

        private DynamicSubInsertIntoClause(final String name, final CriteriaContext outContext, final @Nullable C criteria) {
            super(CriteriaContexts.cteInsertContext(outContext, criteria));
            this.criteria = criteria;
            this.name = name;
            //firstly,onStartCte
            outContext.onStartCte(name);
            //secondly,push context
            CriteriaContextStack.push(this.context);

        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert>> leftParen(String string) {
            if (this.columnAliasList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = Collections.singletonList(string);
            return this;
        }

        @Override
        public Statement._CommaStringDualSpec<PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert>> leftParen(String string1, String string2) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(string1, string2);
        }

        @Override
        public Statement._CommaStringQuadraSpec<PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert>> leftParen(String string1, String string2, String string3, String string4) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(string1, string2, string3, string4);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert>> leftParen(Consumer<Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert>> leftParen(BiConsumer<C, Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert>> leftParenIf(Consumer<Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParenIf(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert>> leftParenIf(BiConsumer<C, Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParenIf(consumer);
        }

        @Override
        public PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert> rightParen() {
            return this;
        }


        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, DmlInsert, DqlInsert> insertInto(TableMeta<T> table) {
            return new SubComplexValuesClause<>(this, table, this::subInsertEnd, this::subReturnInsertEnd);
        }


        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.cteBuilder(recursive, this.context);
        }


        private PostgreInsert._DynamicSubOptionSpec<C, DmlInsert, DqlInsert> columnAliasClauseEnd(final List<String> aliasList) {
            if (this.columnAliasList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final CriteriaContext outerContext;
            outerContext = this.context.getOuterContext();
            assert outerContext != null;
            outerContext.onCteColumnAlias(this.name, aliasList);
            this.columnAliasList = aliasList;
            return this;
        }

        private DmlInsert subInsertEnd(final SubInsert insert) {
            final CriteriaContext outerContext = this.context.getOuterContext();
            assert outerContext != null;
            CriteriaUtils.createAndAddCte(outerContext, this.name, this.columnAliasList, insert);
            //couldn't return actual instance 'insert'
            return this;
        }

        private DqlInsert subReturnInsertEnd(final SubReturningInsert insert) {
            final CriteriaContext outerContext = this.context.getOuterContext();
            assert outerContext != null;
            CriteriaUtils.createAndAddCte(outerContext, this.name, this.columnAliasList, insert);
            //couldn't return actual instance 'insert'
            return this;
        }


    }//DynamicSubInsertIntoClause


    private enum OverridingMode implements SQLWords {

        OVERRIDING_SYSTEM_VALUE,
        OVERRIDING_USER_VALUE;


        @Override
        public final String render() {
            final String words;
            switch (this) {
                case OVERRIDING_USER_VALUE:
                    words = "OVERRIDING USER VALUE";
                    break;
                case OVERRIDING_SYSTEM_VALUE:
                    words = "OVERRIDING SYSTEM VALUE";
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(this);
            }
            return words;
        }


        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(OverridingMode.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }


    }//OverridingMode


    private static abstract class PostgreComplexInsertValuesClause<C, T, I extends DmlInsert, Q extends DqlInsert>
            extends ComplexInsertValuesClause<
            C,
            T,
            PostgreInsert._ComplexOverridingValueSpec<C, T, I, Q>,
            PostgreInsert._ValuesDefaultSpec<C, T, I, Q>,
            PostgreInsert._OnConflictSpec<C, T, I, Q>>
            implements PostgreInsert._TableAliasSpec<C, T, I, Q>
            , PostgreInsert._OnConflictSpec<C, T, I, Q> {

        private _PostgreInsert._ConflictActionClauseResult actionResult;


        private PostgreComplexInsertValuesClause(InsertOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public final PostgreInsert._ColumnListSpec<C, T, I, Q> as(String alias) {
            return null;
        }

        @Override
        public final PostgreInsert._ValuesDefaultSpec<C, T, I, Q> overridingSystemValue() {
            return null;
        }

        @Override
        public final PostgreInsert._ValuesDefaultSpec<C, T, I, Q> overridingUserValue() {
            return null;
        }

        @Override
        public final PostgreInsert._ValuesDefaultSpec<C, T, I, Q> ifOverridingSystemValue(BooleanSupplier supplier) {
            return null;
        }

        @Override
        public final PostgreInsert._ValuesDefaultSpec<C, T, I, Q> ifOverridingUserValue(BooleanSupplier supplier) {
            return null;
        }

        @Override
        public final PostgreInsert._ValuesLeftParenClause<C, T, I, Q> values() {
            return new StaticValuesLeftParenClause<>(this);
        }

        @Override
        public final PostgreInsert._ConflictTargetOptionSpec<C, T, I, Q> onConflict() {
            return new OnConflictClause<>(this);
        }

        @Override
        public final DialectStatement._StaticReturningCommaUnaryClause<Q> returning(SelectItem selectItem) {
            return null;
        }

        @Override
        public final DialectStatement._StaticReturningCommaDualClause<Q> returning(SelectItem selectItem1, SelectItem selectItem2) {
            return null;
        }

        @Override
        public final DqlStatement._DqlInsertSpec<Q> returningAll() {
            return null;
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returning(Consumer<Consumer<SelectItem>> consumer) {
            return null;
        }

        @Override
        public final DqlStatement._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            return null;
        }


        private PostgreInsert._ReturningSpec<C, I, Q> conflictClauseEnd(_PostgreInsert._ConflictActionClauseResult result) {
            if (this.actionResult != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.actionResult = result;
            return this;
        }


    }//PostgreComplexInsertValuesClause


    private static abstract class ComplexValuesClause<C, T, CT, I extends DmlInsert, Q extends DqlInsert, AR, CR, DR, VR>
            extends ComplexInsertValuesClause<C, T, CR, DR, VR>
            implements Statement._AsClause<AR>
            , PostgreInsert._ParentReturningClause<C, CT, I, Q>
            , PostgreInsert._ParentReturningCommaUnaryClause<CT, Q>
            , PostgreInsert._ParentReturningCommaDualClause<CT, Q>
            , WithValueSyntaxOptions {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private String tableAlias;

        private List<SelectItem> selectItemList;

        OverridingMode overridingMode;

        _PostgreInsert._ConflictActionClauseResult conflictAction;

        private ComplexValuesClause(WithValueSyntaxOptions options, TableMeta<T> table) {
            super(options, table);
            this.cteList = options.cteList();
            this.recursive = options.isRecursive();
        }

        @SuppressWarnings("unchecked")
        @Override
        public final AR as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.tableAlias = alias;
            return (AR) this;
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returningAll() {
            if (this.selectItemList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.selectItemList = Collections.emptyList();
            return this;
        }

        @Override
        public final PostgreInsert._ParentReturningCommaUnaryClause<CT, Q> returning(SelectItem selectItem1) {
            return this.comma(selectItem1);
        }

        @Override
        public final PostgreInsert._ParentReturningCommaDualClause<CT, Q> returning(SelectItem selectItem1, SelectItem selectItem2) {
            return this.comma(selectItem1, selectItem2);
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returning(Consumer<Consumer<SelectItem>> consumer) {
            consumer.accept(this::comma);
            return this;
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            consumer.accept(this.criteria, this::comma);
            return this;
        }

        @Override
        public final PostgreInsert._ParentReturningCommaUnaryClause<CT, Q> comma(final SelectItem selectItem) {
            List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                this.selectItemList = selectItemList = new ArrayList<>();
            } else if (!(selectItemList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            selectItemList.add(selectItem);
            return this;
        }

        @Override
        public final PostgreInsert._ParentReturningCommaDualClause<CT, Q> comma(final SelectItem selectItem1, final SelectItem selectItem2) {
            List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                selectItemList = new ArrayList<>();
                this.selectItemList = selectItemList;
            } else if (!(selectItemList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            selectItemList.add(selectItem1);
            selectItemList.add(selectItem2);
            return this;
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        private List<? extends SelectItem> returningList() {
            final List<SelectItem> selectItemList = this.selectItemList;
            assert selectItemList != null;
            final List<? extends SelectItem> effectiveReturningList;
            if (selectItemList.size() == 0) {
                effectiveReturningList = this.effectiveFieldList();
            } else {
                effectiveReturningList = _CollectionUtils.unmodifiableList(selectItemList);
            }
            return effectiveReturningList;
        }

        final boolean hasReturningClause() {
            return this.selectItemList != null;
        }

    }//ComplexValuesClause


    private static final class StaticValuesLeftParenClause<C, T, I extends DmlInsert, Q extends DqlInsert>
            extends InsertSupport.StaticColumnValuePairClause<
            C,
            T,
            PostgreInsert._ValuesLeftParenSpec<C, T, I, Q>>
            implements PostgreInsert._ValuesLeftParenSpec<C, T, I, Q> {

        private final PostgreComplexInsertValuesClause<C, T, I, Q> clause;

        private StaticValuesLeftParenClause(PostgreComplexInsertValuesClause<C, T, I, Q> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ConflictTargetOptionSpec<C, T, I, Q> onConflict() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.onConflict();
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returningAll() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returningAll();
        }

        @Override
        public PostgreInsert._StaticReturningCommaUnaryClause<Q> returning(SelectItem selectItem) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(selectItem);
        }

        @Override
        public PostgreInsert._StaticReturningCommaDualClause<Q> returning(SelectItem selectItem1, SelectItem selectItem2) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(selectItem1, selectItem2);
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returning(Consumer<Consumer<SelectItem>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public I asInsert() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.asInsert();
        }


    }//NonParentStaticValuesLeftParenClause


    private static final class PrimaryComplexValuesClause<C, T>
            extends NonParentComplexValuesClause<C, T, Insert, ReturningInsert> {

        private final ParentComplexValuesClause<C, ?> parentClause;

        private PrimaryComplexValuesClause(PrimaryInsertIntoClause<C> options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentClause = null;
        }

        private PrimaryComplexValuesClause(PrimaryInsertIntoClause<C> options, ChildTableMeta<T> table) {
            super(options, table);
            this.parentClause = null;
        }

        private PrimaryComplexValuesClause(ParentComplexValuesClause<C, ?> parentClause, ChildInsertIntoClause<C, ?> childIntoClause, ChildTableMeta<T> table) {
            super(childIntoClause, table);
            this.parentClause = parentClause;
        }

        @Override
        public Insert asInsert() {
            if (this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final ParentComplexValuesClause<C, ?> parentClause = this.parentClause;
            final InsertMode mode;
            mode = this.assertInsertMode(parentClause);
            final Insert._InsertSpec spec;
            switch (mode) {
                case DOMAIN: {
                    if (parentClause == null) {
                        spec = new PrimaryDomainInsertStatement(this);
                    } else {
                        spec = new ChildDomainInsertStatement(parentClause, this);
                    }
                }
                break;
                case VALUES: {
                    if (parentClause == null) {
                        spec = new PrimaryValueInsertStatement(this);
                    } else {
                        spec = new ChildValueInsertStatement(parentClause, this);
                    }
                }
                break;
                case QUERY: {
                    if (parentClause == null) {
                        spec = new PrimaryQueryInsertStatement(this);
                    } else {
                        spec = new ChildQueryInsertStatement(parentClause, this);
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asInsert();
        }

        @Override
        public ReturningInsert asReturningInsert() {
            if (!this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final ParentComplexValuesClause<C, ?> parentClause = this.parentClause;
            final InsertMode mode;
            mode = this.assertInsertMode(parentClause);
            final ReturningInsert._ReturningInsertSpec spec;
            switch (mode) {
                case DOMAIN: {
                    if (parentClause == null) {
                        spec = new PrimaryDomainReturningInsertStatement(this);
                    } else {
                        spec = new ChildDomainReturningInsertStatement(parentClause, this);
                    }
                }
                break;
                case VALUES: {
                    if (parentClause == null) {
                        spec = new PrimaryValueReturningInsertStatement(this);
                    } else {
                        spec = new ChildValueReturningInsertStatement(parentClause, this);
                    }
                }
                break;
                case QUERY: {
                    if (parentClause == null) {
                        spec = new PrimaryQueryReturningInsertStatement(this);
                    } else {
                        spec = new ChildQueryReturningInsertStatement(parentClause, this);
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asReturningInsert();
        }


    }//PrimaryNonParentComplexValuesClause


    private static final class SubComplexValuesClause<C, T, I extends DmlInsert, Q extends DqlInsert>
            extends NonParentComplexValuesClause<C, T, I, Q>
            implements _Insert._SupportWithClauseInsert {

        private final Function<SubInsert, I> dmlFunction;

        private final Function<SubReturningInsert, Q> dqlFunction;


        private SubComplexValuesClause(DynamicSubInsertIntoClause<C> options, TableMeta<T> table
                , Function<SubInsert, I> dmlFunction, Function<SubReturningInsert, Q> dqlFunction) {
            super(options, table);
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        private SubComplexValuesClause(StaticSubInsertIntoClause<C, I, Q> options, TableMeta<T> table
                , Function<SubInsert, I> dmlFunction, Function<SubReturningInsert, Q> dqlFunction) {
            super(options, table);
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }


        @Override
        public I asInsert() {
            if (this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final InsertMode mode;
            mode = this.getInsertMode();
            final SubInsert._SubInsertSpec spec;
            switch (mode) {
                case DOMAIN:
                    spec = new SubDomainInsertStatement(this);
                    break;
                case VALUES:
                    spec = new SubValueInsertStatement(this);
                    break;
                case QUERY:
                    spec = new SubQueryInsertStatement(this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return this.dmlFunction.apply(spec.asInsert());
        }

        @Override
        public Q asReturningInsert() {
            if (!this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final InsertMode mode;
            mode = this.getInsertMode();
            final SubReturningInsert._SubReturningInsertSpec spec;
            switch (mode) {
                case DOMAIN:
                    spec = new SubDomainReturningInsertStatement(this);
                    break;
                case VALUES:
                    spec = new SubValueReturningInsertStatement(this);
                    break;
                case QUERY:
                    spec = new SubQueryReturningInsertStatement(this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return this.dqlFunction.apply(spec.asReturningInsert());
        }


    }//SubComplexValuesClause


    private static abstract class PostgreValueSyntaxInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends AbstractValueSyntaxStatement<I, Q>
            implements PostgreInsert, _PostgreInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        private final List<? extends SelectItem> returningList;


        private PostgreValueSyntaxInsertStatement(final ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);

            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            if (this instanceof DqlInsert) {
                this.returningList = clause.returningList();
                assert this.returningList.size() > 0;
            } else {
                this.returningList = Collections.emptyList();
            }
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public final List<? extends SelectItem> returningList() {
            return this.returningList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictAction != null;
        }

        @Override
        public final SQLWords overridingValueWords() {
            return this.overridingMode;
        }

        @Override
        public final _ConflictActionClauseResult getConflictActionResult() {
            return this.conflictAction;
        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(PostgreDialect.POSTGRE14, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//PrimaryValueSyntaxInsertStatement


    static abstract class DomainInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends PostgreValueSyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreDomainInsert {

        final List<?> domainList;

        private DomainInsertStatement(final NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            if (this.insertTable instanceof ChildTableMeta) {
                this.domainList = clause.domainListForWithInsert();
            } else {
                this.domainList = clause.domainListForSingle();
            }
        }

        private DomainInsertStatement(final ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause);
            this.domainList = domainList;
        }

        private DomainInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForChild(parentClause);
        }

        @Override
        public final List<?> domainList() {
            return this.domainList;
        }


    }//DomainInsertStatement

    private static final class PrimaryDomainInsertStatement extends DomainInsertStatement<Insert, ReturningInsert>
            implements Insert, Insert._InsertSpec {

        private PrimaryDomainInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryDomainInsertStatement(ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause, domainList);
        }

    }//PrimaryDomainInsertStatement

    private static final class PrimaryDomainReturningInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private PrimaryDomainReturningInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryDomainReturningInsertStatement(ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause, domainList);
        }

    }//PrimaryDomainReturningInsertStatement


    private static abstract class ChildSyntaxDomainInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildDomainInsert {

        private final DomainInsertStatement<Insert, ReturningInsert> parentStmt;

        private ChildSyntaxDomainInsertStatement(final ParentComplexValuesClause<?, ?> parentClause
                , final PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
            if (parentClause.hasReturningClause()) {
                this.parentStmt = new PrimaryDomainReturningInsertStatement(parentClause, this.domainList);
            } else {
                this.parentStmt = new PrimaryDomainInsertStatement(parentClause, this.domainList);
            }

        }

        @Override
        public final _PostgreDomainInsert parentStmt() {
            return this.parentStmt;
        }

    }//ChildSyntaxDomainInsertStatement


    private static final class ChildDomainInsertStatement extends ChildSyntaxDomainInsertStatement
            implements Insert, Insert._InsertSpec {

        private ChildDomainInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
        }


    }//ChildDomainInsertStatement

    private static final class ChildDomainReturningInsertStatement extends ChildSyntaxDomainInsertStatement
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private ChildDomainReturningInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
        }

    }//ChildDomainReturningInsertStatement


    private static final class SubDomainInsertStatement extends DomainInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert
            , SubInsert._SubInsertSpec {


        private SubDomainInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

    }//SubDomainInsertStatement


    private static final class SubDomainReturningInsertStatement
            extends DomainInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert
            , SubReturningInsert._SubReturningInsertSpec {


        private SubDomainReturningInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

    }//SubDomainReturningInsertStatement


    static abstract class ValueInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends PostgreValueSyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreValueInsert {

        private final List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private ValueInsertStatement(ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }


    }//ValueInsertStatement


    private static final class PrimaryValueInsertStatement extends ValueInsertStatement<Insert, ReturningInsert>
            implements Insert, Insert._InsertSpec {

        private PrimaryValueInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryValueInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueInsertStatement

    private static final class PrimaryValueReturningInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private PrimaryValueReturningInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryValueReturningInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueReturningInsertStatement


    private static abstract class ChildSyntaxValueInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildValueInsert {

        private final ValueInsertStatement<Insert, ReturningInsert> parentStmt;

        private ChildSyntaxValueInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
            if (parentClause.hasReturningClause()) {
                this.parentStmt = new PrimaryValueReturningInsertStatement(parentClause);
            } else {
                this.parentStmt = new PrimaryValueInsertStatement(parentClause);
            }

        }

        @Override
        public final _PostgreValueInsert parentStmt() {
            return this.parentStmt;
        }


    }//ChildSyntaxValueInsertStatement

    private static final class ChildValueInsertStatement extends ChildSyntaxValueInsertStatement
            implements Insert, Insert._InsertSpec {

        private ChildValueInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
        }
    }//ChildValueInsertStatement


    private static final class ChildValueReturningInsertStatement extends ChildSyntaxValueInsertStatement
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private ChildValueReturningInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
        }

    }//ChildValueReturningInsertStatement


    private static final class SubValueInsertStatement
            extends ValueInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert
            , SubInsert._SubInsertSpec {

        private SubValueInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubValueInsertStatement


    private static final class SubValueReturningInsertStatement
            extends ValueInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert
            , SubReturningInsert._SubReturningInsertSpec {

        private SubValueReturningInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

    }//SubValueReturningInsertStatement


    static abstract class QueryInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends InsertSupport.AbstractQuerySyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreQueryInsert, PostgreInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        private final List<? extends SelectItem> returningList;

        private QueryInsertStatement(ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);
            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            if (this instanceof DqlInsert) {
                this.returningList = clause.returningList();
                assert this.returningList.size() > 0;
            } else {
                this.returningList = Collections.emptyList();
            }
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public final SQLWords overridingValueWords() {
            return this.overridingMode;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictAction != null;
        }

        @Override
        public final _ConflictActionClauseResult getConflictActionResult() {
            return this.conflictAction;
        }

        @Override
        public final List<? extends SelectItem> returningList() {
            return this.returningList;
        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(PostgreDialect.POSTGRE14, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//QueryInsertStatement


    private static final class PrimaryQueryInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements Insert, Insert._InsertSpec {

        private PrimaryQueryInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryQueryInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }

    }//PrimaryQueryInsertStatement

    private static final class PrimaryQueryReturningInsertStatement
            extends QueryInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private PrimaryQueryReturningInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryQueryReturningInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }


    }//PrimaryQueryReturningInsertStatement


    private static final class ChildQueryInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildQueryInsert, Insert, Insert._InsertSpec {

        private final QueryInsertStatement<Insert, ReturningInsert> parentStmt;


        private ChildQueryInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
            if (parentClause.hasReturningClause()) {
                this.parentStmt = new PrimaryQueryReturningInsertStatement(parentClause);
            } else {
                this.parentStmt = new PrimaryQueryInsertStatement(parentClause);
            }
        }

        @Override
        public _PostgreQueryInsert parentStmt() {
            return this.parentStmt;
        }

    } //ChildQueryInsertStatement

    private static final class ChildQueryReturningInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildQueryInsert
            , ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private final QueryInsertStatement<Insert, ReturningInsert> parentStmt;


        private ChildQueryReturningInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
            if (parentClause.hasReturningClause()) {
                this.parentStmt = new PrimaryQueryReturningInsertStatement(parentClause);
            } else {
                this.parentStmt = new PrimaryQueryInsertStatement(parentClause);
            }
        }

        @Override
        public _PostgreQueryInsert parentStmt() {
            return this.parentStmt;
        }

    } //ChildQueryReturningInsertStatement

    private static final class SubQueryInsertStatement extends QueryInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert
            , SubInsert._SubInsertSpec {

        private SubQueryInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement

    private static final class SubQueryReturningInsertStatement extends QueryInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert
            , SubReturningInsert._SubReturningInsertSpec {

        private SubQueryReturningInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement


}
