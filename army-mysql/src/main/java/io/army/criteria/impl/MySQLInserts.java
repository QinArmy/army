package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.dialect._DialectUtils;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is the container of  MySQL insert syntax api implementation class.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class MySQLInserts extends InsertSupport {

    private MySQLInserts() {
        throw new UnsupportedOperationException();
    }


    static <C> MySQLInsert._DomainOptionSpec<C> domainInsert(@Nullable C criteria) {
        return new DomainInsertOptionClause<>(criteria);
    }

    static <C> MySQLInsert._ValueOptionSpec<C> valueInsert(@Nullable C criteria) {
        return new ValueInsertOptionClause<>(criteria);
    }

    static <C> MySQLInsert._AssignmentOptionSpec<C> assignmentInsert(@Nullable C criteria) {
        return new AssignmentInsertOptionClause<>(criteria);
    }

    static <C> MySQLInsert._QueryInsertIntoSpec<C> queryInsert(@Nullable C criteria) {
        return new QueryInsertIntoClause<>(criteria);
    }


    interface ClauseBeforeRowAlias extends ColumnListClause {


        /**
         * @param pairList an unmodified list,empty is allowed.
         */
        Insert endInsert(List<_Pair<FieldMeta<?>, _Expression>> pairList);

        TableMeta<?> table();

    }


    interface ParentClauseBeforeRowAlias<CT> extends ClauseBeforeRowAlias {

        CT parentStmtEnd(List<_Pair<FieldMeta<?>, _Expression>> pairList);
    }


    @SuppressWarnings("unchecked")
    private static abstract class MySQLInsertClause<C, MR, NR, PR, IR> extends NonQueryInsertOptionsImpl<MR, NR, PR>
            implements MySQLInsert._InsertClause<C, IR> {

        private List<Hint> hintList;

        private List<MySQLWords> modifierList;

        private MySQLInsertClause(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }


        @Override
        public final IR insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::insertModifier);
            return (IR) this;
        }

        @Override
        public final IR insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteriaContext.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::insertModifier);
            return (IR) this;
        }


        final List<Hint> hintList() {
            List<Hint> list = this.hintList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        final List<MySQLWords> modifierList() {
            List<MySQLWords> list = this.modifierList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//InsertClause


    @SuppressWarnings("unchecked")
    private static abstract class DuplicateKeyUpdateClause<C, T extends IDomain, UR, DR>
            implements Insert._CommaFieldValuePairClause<C, T, UR>
            , MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, UR>
            , MySQLInsert._StaticOnDuplicateKeyFieldClause<C, T, UR>
            , MySQLInsert._DynamicOnDuplicateKeyUpdateClause<C, PairConsumer<FieldMeta<T>>, DR>
            , PairConsumer<FieldMeta<T>> {

        final CriteriaContext criteriaContext;

        final C criteria;

        final ClauseBeforeRowAlias clause;

        final TableMeta<?> table;

        private boolean optionalOnDuplicateKey = true;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private DuplicateKeyUpdateClause(ClauseBeforeRowAlias clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.criteria = this.criteriaContext.criteria();
            this.clause = clause;
            this.table = clause.table();
        }


        @Override
        public final UR update(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return (UR) this;
        }

        @Override
        public final UR updateLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return (UR) this;
        }

        @Override
        public final UR updateExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return (UR) this;
        }

        @Override
        public final UR updateExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return (UR) this;
        }

        @Override
        public final UR comma(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return (UR) this;
        }

        @Override
        public final UR commaLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return (UR) this;
        }

        @Override
        public final UR commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return (UR) this;
        }

        @Override
        public final UR commaExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return (UR) this;
        }

        @Override
        public final MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, UR> onDuplicateKey() {
            this.optionalOnDuplicateKey = false;
            return this;
        }

        @Override
        public final DR onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKey = false;
            consumer.accept(this);
            return (DR) this;
        }

        @Override
        public final DR onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKey = false;
            consumer.accept(this.criteria, this);
            return (DR) this;
        }

        @Override
        public final DR ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this);
            return (DR) this;
        }

        @Override
        public final DR ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this.criteria, this);
            return (DR) this;
        }

        @Override
        public final PairConsumer<FieldMeta<T>> accept(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final PairConsumer<FieldMeta<T>> acceptLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final PairConsumer<FieldMeta<T>> acceptExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }


        final List<_Pair<FieldMeta<?>, _Expression>> endDuplicateKeyClause() {
            List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                if (!this.optionalOnDuplicateKey) {
                    String m = "You don't add any field and value pair";
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                }
                pairList = Collections.emptyList();
            } else if (pairList instanceof ArrayList) {
                pairList = Collections.unmodifiableList(pairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return pairList;
        }

        private void addValuePair(final FieldMeta<T> field, final @Nullable Expression value) {

            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }

            if (field.tableMeta() != this.table) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::unknownColumn, field);
            }
            if (field.updateMode() != UpdateMode.UPDATABLE) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonUpdatableField, field);
            }

            switch (field.fieldName()) {
                case _MetaBridge.UPDATE_TIME:
                case _MetaBridge.VERSION:
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::armyManageField, field);
            }

            Map<FieldMeta<?>, Boolean> filedMap = this.fieldMap;
            if (filedMap == null) {
                filedMap = new HashMap<>();
                this.fieldMap = filedMap;
            }
            if (filedMap.putIfAbsent(field, Boolean.TRUE) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }

            List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                pairList = new ArrayList<>();
                this.duplicatePairList = pairList;
            }
            pairList.add(_Pair.create(field, (ArmyExpression) value));
        }


    }//DuplicateKeyUpdateClause


    private static final class NonParentDuplicateKeyUpdateSpec<C, T extends IDomain>
            extends DuplicateKeyUpdateClause<
            C,
            T,
            MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>,
            Insert._InsertSpec>
            implements MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T>
            , MySQLInsert._StaticAssignmentCommaFieldSpec<C, T> {


        private NonParentDuplicateKeyUpdateSpec(ClauseBeforeRowAlias clause) {
            super(clause);
        }


        @Override
        public Insert asInsert() {
            return this.clause.endInsert(this.endDuplicateKeyClause());
        }


    }//NonParentDuplicateKeyUpdateSpec


    private static final class ParentDuplicateKeyUpdateSpec<C, P extends IDomain, CT>
            extends DuplicateKeyUpdateClause<
            C,
            P,
            MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, P, CT>,
            MySQLInsert._MySQLChildSpec<CT>>
            implements MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, CT>
            , MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, P, CT> {

        private ParentDuplicateKeyUpdateSpec(ParentClauseBeforeRowAlias<CT> clause) {
            super(clause);
        }

        @SuppressWarnings("unchecked")
        @Override
        public CT child() {
            return ((ParentClauseBeforeRowAlias<CT>) this.clause).parentStmtEnd(this.endDuplicateKeyClause());
        }

        @Override
        public Insert asInsert() {
            return this.clause.endInsert(this.endDuplicateKeyClause());
        }

    }//ParentDuplicateKeyUpdateSpec


    private static final class DomainDuplicateKeyUpdateSpec<C, P extends IDomain>
            extends DuplicateKeyUpdateClause<
            C,
            P,
            MySQLInsert._DomainParentStaticAssignmentCommaFieldSpec<C, P>,
            MySQLInsert._DomainChildClause<C, P>>
            implements MySQLInsert._DomainParentOnDuplicateKeyUpdateFieldSpec<C, P>
            , MySQLInsert._DomainParentStaticAssignmentCommaFieldSpec<C, P> {

        private DomainDuplicateKeyUpdateSpec(DomainParentPartitionClause<C, P> clause) {
            super(clause);
        }

        @SuppressWarnings("unchecked")
        @Override
        public MySQLInsert._DomainChildInsertIntoSpec<C, P> child() {
            return ((DomainParentPartitionClause<C, P>) this.clause).parentStmtEnd(this.endDuplicateKeyClause());
        }


    }//DomainDuplicateKeyUpdateSpec


    /*-------------------below domain insert syntax classes-------------------*/

    private static final class DomainInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._DomainNullOptionSpec<C>,
            MySQLInsert._DomainPreferLiteralSpec<C>,
            MySQLInsert._DomainInsertIntoSpec<C>,
            MySQLInsert._DomainIntoClause<C>>
            implements MySQLInsert._DomainOptionSpec<C>, MySQLInsert._DomainIntoClause<C> {


        private DomainInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new DomainPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new DomainParentPartitionClause<>(this, table);
        }


        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new DomainPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new DomainParentPartitionClause<>(this, table);
        }


    }//DomainInsertOptionClause


    private static final class DomainPartitionClause<C, T extends IDomain>
            extends InsertSupport.DomainValueClause<
            C,
            T,
            MySQLInsert._DomainDefaultSpec<C, T>,
            MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T>>
            implements MySQLInsert._DomainPartitionSpec<C, T>
            , MySQLInserts.ClauseBeforeRowAlias {

        private final _Insert._DomainInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private DomainPartitionClause(DomainInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;

        }

        private DomainPartitionClause(DomainParentPartitionClause<C, ?> parentClause, ChildTableMeta<T> table) {
            super(parentClause, table);
            this.hintList = _CollectionUtils.safeList(parentClause.childHintList);
            this.modifierList = _CollectionUtils.safeList(parentClause.childModifierList);
            this.parentStmt = parentClause.createParentStmt(this::domainList); //couldn't invoke asInsert method
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._DomainColumnListSpec<C, T>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }


        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new DomainInsertStatement(this);
                } else {
                    spec = new DomainChildInsertStatement(this);
                }
            } else if (this.parentStmt == null) {
                spec = new DomainInsertWithDuplicateKey(this, pairList);
            } else {
                spec = new DomainChildInsertWithDuplicateKey(this, pairList);
            }
            return spec.asInsert();
        }


        @Override
        MySQLInsert._DomainDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T> valuesEnd() {
            return new NonParentDuplicateKeyUpdateSpec<>(this);
        }

        private MySQLInsert._DomainColumnListSpec<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

    }//DomainPartitionClause


    private static final class DomainParentPartitionClause<C, P extends IDomain>
            extends InsertSupport.DomainValueClause<
            C,
            P,
            MySQLInsert._DomainParentDefaultSpec<C, P>,
            MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, P>>
            implements MySQLInsert._DomainParentPartitionSpec<C, P>
            , MySQLInserts.ParentClauseBeforeRowAlias<MySQLInsert._DomainChildInsertIntoSpec<C, P>>
            , MySQLInsert._DomainChildClause<C, P>
            , MySQLInsert._DomainChildInsertIntoSpec<C, P>
            , MySQLInsert._DomainChildIntoClause<C, P>
            , InsertSupport.NonQueryInsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLWords> childModifierList;

        private DomainParentPartitionClause(DomainInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }


        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._DomainParentColumnsSpec<C, P>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, P, MySQLInsert._DomainParentStaticAssignmentCommaFieldSpec<C, P>> onDuplicateKey() {
            return new DomainDuplicateKeyUpdateSpec<>(this)
                    .onDuplicateKey();
        }

        @Override
        public MySQLInsert._DomainChildClause<C, P> onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<P>>> consumer) {
            return new DomainDuplicateKeyUpdateSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._DomainChildClause<C, P> onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<P>>> consumer) {
            return new DomainDuplicateKeyUpdateSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._DomainChildClause<C, P> ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<P>>> consumer) {
            return new DomainDuplicateKeyUpdateSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._DomainChildClause<C, P> ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<P>>> consumer) {
            return new DomainDuplicateKeyUpdateSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._DomainChildInsertIntoSpec<C, P> child() {
            return this.parentStmtEnd(Collections.emptyList());
        }

        @Override
        public MySQLInsert._DomainChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._DomainChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final DomainPartitionClause<C, T> childClause;
            childClause = new DomainPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        MySQLInsert._DomainParentDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        @Override
        MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, P> valuesEnd() {
            return new NonParentDuplicateKeyUpdateSpec<>(this);
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt(null).asInsert();
        }

        public MySQLInsert._DomainChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.endColumnDefaultClause();
            this.duplicatePairList = pairList;
            return this;
        }


        private MySQLInsert._DomainParentColumnsSpec<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


        private DomainInsertStatement createParentStmt(@Nullable Supplier<List<IDomain>> supplier) {
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final DomainInsertStatement statement;
            if (pairList.size() == 0) {
                statement = new DomainInsertStatement(this, supplier);
            } else {
                statement = new DomainInsertWithDuplicateKey(this, pairList, supplier);
            }
            return statement;
        }


    }//DomainParentPartitionClause


    static abstract class MySQLValueSyntaxStatement extends InsertSupport.ValueSyntaxStatement<Insert>
            implements MySQLInsert, _MySQLInsert, Insert._InsertSpec {


        private MySQLValueSyntaxStatement(_ValuesSyntaxInsert clause) {
            super(clause);

        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//MySQLValueSyntaxStatement


    private static class DomainInsertStatement extends MySQLValueSyntaxStatement
            implements _MySQLInsert._MySQLDomainInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;
        private final List<IDomain> domainList;

        private final Supplier<List<IDomain>> supplier;

        private DomainInsertStatement(DomainPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.domainList = clause.domainList();
            this.supplier = null;
        }

        private DomainInsertStatement(DomainParentPartitionClause<?, ?> clause
                , @Nullable Supplier<List<IDomain>> supplier) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            if (supplier == null) {
                this.domainList = clause.domainList();
                this.supplier = null;
            } else {
                this.domainList = Collections.emptyList();
                this.supplier = supplier;
            }
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }


        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<IDomain> domainList() {
            return this.supplier == null ? this.domainList : this.supplier.get();
        }

    }//DomainInsertStatement


    private static class DomainInsertWithDuplicateKey extends DomainInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {


        private final List<_Pair<FieldMeta<?>, _Expression>> pairList;

        private DomainInsertWithDuplicateKey(DomainPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        private DomainInsertWithDuplicateKey(DomainParentPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList, @Nullable Supplier<List<IDomain>> supplier) {
            super(clause, supplier);
            this.pairList = pairList;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList() {
            return this.pairList;
        }


    }//DomainInsertWithDuplicateKey


    private static class DomainChildInsertStatement extends DomainInsertStatement
            implements _Insert._ChildDomainInsert {

        private final _Insert._DomainInsert parentStmt;

        private DomainChildInsertStatement(DomainPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public final _DomainInsert parentStmt() {
            return this.parentStmt;
        }

    }//DomainChildInsertStatement


    private static class DomainChildInsertWithDuplicateKey extends DomainChildInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<FieldMeta<?>, _Expression>> pairList;

        private DomainChildInsertWithDuplicateKey(DomainPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList() {
            return this.pairList;
        }

    }//DomainChildInsertWithDuplicateKey




    /*-------------------below value insert syntax classes  -------------------*/


    private static final class ValueInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._ValueNullOptionSpec<C>,
            MySQLInsert._ValuePreferLiteralSpec<C>,
            MySQLInsert._ValueInsertIntoSpec<C>,
            MySQLInsert._ValueIntoClause<C>>
            implements MySQLInsert._ValueOptionSpec<C>, MySQLInsert._ValueIntoClause<C>, InsertOptions {

        private ValueInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new ValuePartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValueParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new ValueParentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new ValuePartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValueParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new ValueParentPartitionClause<>(this, table);
        }

    }//ValueOptionClause


    private static final class StaticValuesLeftParenClause<C, T extends IDomain>
            extends InsertSupport.StaticColumnValuePairClause<C, T, MySQLInsert._ValueStaticValuesLeftParenSpec<C, T>>
            implements MySQLInsert._ValueStaticValuesLeftParenSpec<C, T> {

        private final ValuePartitionClause<C, T> clause;

        private StaticValuesLeftParenClause(ValuePartitionClause<C, T> clause) {
            super(clause.criteriaContext, clause::validateField);
            this.clause = clause;
        }

        @Override
        public MySQLInsert._ValueStaticValuesLeftParenSpec<C, T> rightParen() {
            this.endCurrentRow();
            return this;
        }


        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>> onDuplicateKey() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }


        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEndBeforeAs(this.endValuesClause());
        }

    }//StaticValuesLeftParenClause

    private static final class ParentStaticValuesLeftParenClause<C, T extends IDomain>
            extends InsertSupport.StaticColumnValuePairClause<C, T, MySQLInsert._ValueParentStaticValueLeftParenSpec<C, T>>
            implements MySQLInsert._ValueParentStaticValueLeftParenSpec<C, T> {

        private final ValueParentPartitionClause<C, T> clause;

        private ParentStaticValuesLeftParenClause(ValueParentPartitionClause<C, T> clause) {
            super(clause.getCriteriaContext(), clause::validateField);
            this.clause = clause;
        }

        @Override
        public MySQLInsert._ValueParentStaticValueLeftParenSpec<C, T> rightParen() {
            this.endCurrentRow();
            return this;
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, T, MySQLInsert._ValueChildInsertIntoSpec<C, T>>> onDuplicateKey() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }


        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEndBeforeAs(this.endValuesClause());
        }

        @Override
        public MySQLInsert._ValueChildInsertIntoSpec<C, T> child() {
            return this.clause.childBeforeAs(this.endValuesClause());
        }


    }//ParentStaticValuesLeftParenClause


    private static final class ValuePartitionClause<C, T extends IDomain>
            extends DynamicValueInsertValueClause<
            C,
            T,
            MySQLInsert._ValueDefaultSpec<C, T>,
            MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T>>
            implements MySQLInsert._ValuePartitionSpec<C, T>
            , ClauseBeforeRowAlias {

        private final _Insert._ValuesInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;


        private ValuePartitionClause(ValueInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;
        }

        private ValuePartitionClause(ValueParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt();//couldn't invoke asInsert method
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._ValueColumnListSpec<C, T>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._ValueStaticValuesLeftParenClause<C, T> values() {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new StaticValuesLeftParenClause<>(this);
        }


        @Override
        MySQLInsert._ValueDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new ValuesInsertStatement(this);
                } else {
                    spec = new ValueChildInsertStatement(this);
                }
            } else if (this.parentStmt == null) {
                spec = new ValuesInsertWithDuplicateKey(this, pairList);
            } else {
                spec = new ValueChildInsertWithDuplicateKey(this, pairList);
            }
            return spec.asInsert();
        }


        @Override
        MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T> valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return new NonParentDuplicateKeyUpdateSpec<>(this);
        }

        Insert valueClauseEndBeforeAs(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return this.endInsert(Collections.emptyList());
        }

        private MySQLInsert._ValueColumnListSpec<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


    }//ValuePartitionClause


    private static final class ValueParentPartitionClause<C, P extends IDomain>
            extends DynamicValueInsertValueClause<
            C,
            P,
            MySQLInsert._ValueParentDefaultSpec<C, P>,
            MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._ValueChildInsertIntoSpec<C, P>>>
            implements MySQLInsert._ValueParentPartitionSpec<C, P>
            , ParentClauseBeforeRowAlias<MySQLInsert._ValueChildInsertIntoSpec<C, P>>
            , MySQLInsert._ValueChildInsertIntoSpec<C, P>
            , MySQLInsert._ValueChildIntoClause<C, P>
            , InsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLWords> childModifierList;

        private ValueParentPartitionClause(ValueInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._ValueParentColumnsSpec<C, P>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._ValueParentStaticValueLeftParenClause<C, P> values() {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new ParentStaticValuesLeftParenClause<>(this);
        }

        @Override
        public MySQLInsert._ValueChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._ValueChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final ValuePartitionClause<C, T> childClause;
            childClause = new ValuePartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }


        @Override
        public MySQLInsert._ValueChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt().asInsert();
        }


        MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._ValueChildInsertIntoSpec<C, P>> valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return new ParentDuplicateKeyUpdateSpec<>(this);
        }

        private Insert valueClauseEndBeforeAs(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return this.endInsert(Collections.emptyList());
        }

        private MySQLInsert._ValueChildInsertIntoSpec<C, P> childBeforeAs(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return this;
        }


        @Override
        MySQLInsert._ValueParentDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        private MySQLInsert._ValueParentColumnsSpec<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


        private ValuesInsertStatement createParentStmt() {
            if (this.rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final ValuesInsertStatement statement;
            if (pairList.size() == 0) {
                statement = new ValuesInsertStatement(this);
            } else {
                statement = new ValuesInsertWithDuplicateKey(this, pairList);
            }
            return statement;
        }


    }//ValueParentPartitionClause


    private static class ValuesInsertStatement extends MySQLValueSyntaxStatement
            implements _MySQLInsert._MySQLValueInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;

        private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private ValuesInsertStatement(ValuePartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowValuesList = clause.rowValuesList;

            assert this.rowValuesList != null;
        }

        private ValuesInsertStatement(ValueParentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowValuesList = clause.rowValuesList;

            assert this.rowValuesList != null;
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            return this.rowValuesList;
        }


    }//MySQLValueInsertStatement

    private static class ValuesInsertWithDuplicateKey extends ValuesInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<FieldMeta<?>, _Expression>> pairList;

        private ValuesInsertWithDuplicateKey(ValuePartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        private ValuesInsertWithDuplicateKey(ValueParentPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList() {
            return this.pairList;
        }

    }//ValuesInsertWithDuplicateKey


    private static class ValueChildInsertStatement extends ValuesInsertStatement
            implements _Insert._ChildValuesInsert {


        private final _Insert._ValuesInsert parentStmt;

        private ValueChildInsertStatement(ValuePartitionClause<?, ?> clause) {
            super(clause);

            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public final _ValuesInsert parentStmt() {
            return this.parentStmt;
        }


    }//ValueChildInsertStatement

    private final static class ValueChildInsertWithDuplicateKey extends ValuesInsertWithDuplicateKey
            implements _Insert._ChildValuesInsert {

        private final _Insert._ValuesInsert parentStmt;

        private ValueChildInsertWithDuplicateKey(ValuePartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause, pairList);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStmt;
        }

    }//ValueChildInsertWithDuplicateKey




    /*-------------------below assignment insert syntax classes-------------------*/

    private static final class AssignmentInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._AssignmentNullOptionSpec<C>,
            MySQLInsert._AssignmentPreferLiteralSpec<C>,
            MySQLInsert._AssignmentInsertIntoSpec<C>,
            MySQLInsert._AssignmentIntoClause<C>>
            implements MySQLInsert._AssignmentOptionSpec<C>, MySQLInsert._AssignmentIntoClause<C> {


        private AssignmentInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new AssignmentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new AssignmentParentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new AssignmentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new AssignmentParentPartitionClause<>(this, table);
        }

    }//AssignmentInsertOptionClause


    private static final class AssignmentPartitionClause<C, T extends IDomain>
            extends InsertSupport.AssignmentInsertClause<C, T, MySQLInsert._MySQLAssignmentSetSpec<C, T>>
            implements MySQLInsert._AssignmentPartitionSpec<C, T>
            , MySQLInsert._MySQLAssignmentSetSpec<C, T>
            , ClauseBeforeRowAlias {


        private final _Insert._AssignmentInsert parentStmt;
        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;


        private AssignmentPartitionClause(AssignmentInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, false, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;
        }

        private AssignmentPartitionClause(AssignmentParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, false, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt(); //couldn't invoke asInsert method
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._MySQLAssignmentSetClause<C, T>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }


        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>> onDuplicateKey() {
            this.endAssignmentSetClause();
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .onDuplicateKey();
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            this.endAssignmentSetClause();
            return this.endInsert(Collections.emptyList());
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new AssignmentsInsertStatement(this);
                } else {
                    spec = new AssignmentsChildInsertStatement(this);
                }
            } else if (this.parentStmt == null) {
                spec = new AssignmentsInsertWithDuplicateKey(this, pairList);
            } else {
                spec = new AssignmentsChildInsertWithDuplicateKey(this, pairList);
            }
            return spec.asInsert();
        }

        private MySQLInsert._MySQLAssignmentSetClause<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        @Override
        public void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            if (!this.migration) {
                _DialectUtils.checkInsertField(this.table, field, this::forbidField);
                if (value != null && !field.nullable() && value.isNullValue()) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
                }
            }

        }

        private CriteriaException forbidField(FieldMeta<?> field, Function<FieldMeta<?>, CriteriaException> function) {
            return CriteriaContextStack.criteriaError(this.criteriaContext, function, field);
        }


    }//AssignmentPartitionClause


    private static final class AssignmentParentPartitionClause<C, P extends IDomain>
            extends InsertSupport.AssignmentInsertClause<C, P, MySQLInsert._AssignmentParentSetSpec<C, P>>
            implements MySQLInsert._AssignmentParentPartitionSpec<C, P>
            , MySQLInsert._AssignmentParentSetSpec<C, P>
            , ParentClauseBeforeRowAlias<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>
            , MySQLInsert._AssignmentChildInsertIntoSpec<C, P>
            , MySQLInsert._AssignmentChildIntoClause<C, P>
            , NonQueryInsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLWords> childModifierList;

        private AssignmentParentPartitionClause(AssignmentInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, false, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._AssignmentParentSetClause<C, P>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, P, MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>> onDuplicateKey() {
            this.endAssignmentSetClause();
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .onDuplicateKey();
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<P>>> consumer) {
            this.endAssignmentSetClause();
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<P>>> consumer) {
            this.endAssignmentSetClause();
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<P>>> consumer) {
            this.endAssignmentSetClause();
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<P>>> consumer) {
            this.endAssignmentSetClause();
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            this.endAssignmentSetClause();
            return this.endInsert(Collections.emptyList());
        }

        @Override
        public MySQLInsert._AssignmentChildInsertIntoSpec<C, P> child() {
            this.endAssignmentSetClause();
            return this;
        }

        @Override
        public MySQLInsert._AssignmentChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._AssignmentChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final AssignmentPartitionClause<C, T> childClause;
            childClause = new AssignmentPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        public MySQLInsert._AssignmentChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt().asInsert();
        }


        @Override
        public void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            if (!this.migration) {
                _DialectUtils.checkInsertField(this.table, field, this::forbidField);
                if (value != null && !field.nullable() && value.isNullValue()) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
                }
            }

        }

        private CriteriaException forbidField(FieldMeta<?> field, Function<FieldMeta<?>, CriteriaException> function) {
            return CriteriaContextStack.criteriaError(this.criteriaContext, function, field);
        }

        private MySQLInsert._AssignmentParentSetClause<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private AssignmentsInsertStatement createParentStmt() {
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final AssignmentsInsertStatement statement;
            if (pairList.size() == 0) {
                statement = new AssignmentsInsertStatement(this);
            } else {
                statement = new AssignmentsInsertWithDuplicateKey(this, pairList);
            }
            return statement;
        }


    }//AssignmentParentPartitionClause


    static class AssignmentsInsertStatement extends InsertSupport.AssignmentInsertStatement<Insert>
            implements MySQLInsert, _MySQLInsert._MySQLAssignmentInsert, Insert._InsertSpec {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;

        private AssignmentsInsertStatement(AssignmentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        private AssignmentsInsertStatement(AssignmentParentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

    }//MySQLAssignmentInsertStatement

    private static class AssignmentsInsertWithDuplicateKey extends AssignmentsInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<FieldMeta<?>, _Expression>> pairList;

        private AssignmentsInsertWithDuplicateKey(AssignmentPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        private AssignmentsInsertWithDuplicateKey(AssignmentParentPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }


        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList() {
            return this.pairList;
        }


    }//AssignmentInsertWithDuplicateKey


    private static class AssignmentsChildInsertStatement extends AssignmentsInsertStatement
            implements _Insert._ChildAssignmentInsert {

        private final _AssignmentInsert parentStmt;

        private AssignmentsChildInsertStatement(AssignmentPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public final _AssignmentInsert parentStmt() {
            return this.parentStmt;
        }

    }//AssignmentChildInsertStatement

    private static class AssignmentsChildInsertWithDuplicateKey extends AssignmentsInsertWithDuplicateKey
            implements _Insert._ChildAssignmentInsert {

        private final _AssignmentInsert parentStmt;

        private AssignmentsChildInsertWithDuplicateKey(AssignmentPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause, pairList);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public final _AssignmentInsert parentStmt() {
            return this.parentStmt;
        }


    }//AssignmentsChildInsertWithDuplicateKey



    /*-----------------------below query insert classes-----------------------*/


    private static final class QueryInsertIntoClause<C> implements MySQLInsert._QueryInsertIntoSpec<C>
            , MySQLInsert._QueryIntoClause<C> {

        private final CriteriaContext criteriaContext;

        private List<Hint> hintList;

        private List<MySQLWords> modifierList;

        private QueryInsertIntoClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLInsert._QueryIntoClause<C> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::queryInsertModifier);
            return this;
        }

        @Override
        public MySQLInsert._QueryIntoClause<C> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteriaContext.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::queryInsertModifier);
            return this;
        }


        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new QueryPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new QueryParentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new QueryPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new QueryParentPartitionClause<>(this, table);
        }


    }//QueryInsertIntoClause


    /**
     * <p>
     * This class is a the implementation of MySQL RowSet insert syntax after INTO clause,for {@link  SingleTableMeta}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
     */
    private static class QueryPartitionClause<C, T extends IDomain>
            extends InsertSupport.QueryInsertSpaceClause<
            C,
            T,
            MySQLInsert._QuerySpaceSubQueryClause<C, T>,
            MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T>>
            implements MySQLInsert._QueryPartitionSpec<C, T>
            , MySQLInsert._QuerySpaceSubQueryClause<C, T>
            , ClauseBeforeRowAlias {

        private final _Insert._QueryInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private QueryPartitionClause(QueryInsertIntoClause<C> clause, SimpleTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.hintList);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);
            this.parentStmt = null;
        }

        private QueryPartitionClause(QueryParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt(); //couldn't invoke asInsert method
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._QueryColumnListClause<C, T>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }


        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new MySQLQueryInsertStatement(this);
                } else {
                    spec = new QueryChildInsertStatement(this);
                }
            } else if (this.parentStmt == null) {
                spec = new QueryInsertWithDuplicate(this, pairList);
            } else {
                spec = new QueryChildInsertWithDuplicate(this, pairList);
            }
            return spec.asInsert();
        }


        @Override
        MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T> spaceEnd() {
            return new NonParentDuplicateKeyUpdateSpec<>(this);
        }

        @Override
        MySQLInsert._QuerySpaceSubQueryClause<C, T> columnListEnd() {
            return this;
        }

        private MySQLInsert._QueryColumnListClause<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

    }//QueryPartitionClause


    private static final class QueryParentPartitionClause<C, P extends IDomain>
            extends InsertSupport.QueryInsertSpaceClause<
            C,
            P,
            MySQLInsert._QueryParentQueryClause<C, P>,
            MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._QueryChildInsertIntoSpec<C, P>>>
            implements MySQLInsert._QueryParentPartitionSpec<C, P>
            , MySQLInsert._QueryParentQueryClause<C, P>
            , Insert._ChildPartClause<MySQLInsert._QueryChildInsertIntoSpec<C, P>>
            , ParentClauseBeforeRowAlias<MySQLInsert._QueryChildInsertIntoSpec<C, P>>
            , MySQLInsert._QueryChildInsertIntoSpec<C, P>
            , MySQLInsert._QueryChildIntoClause<C, P> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLWords> childModifierList;

        private QueryParentPartitionClause(QueryInsertIntoClause<C> clause, ParentTableMeta<P> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.hintList);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);

        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._QueryParentColumnListClause<C, P>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._QueryChildInsertIntoSpec<C, P> child() {
            return this;
        }

        @Override
        public MySQLInsert._QueryChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::queryInsertModifier);
            return this;
        }

        @Override
        public MySQLInsert._QueryChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::queryInsertModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final QueryPartitionClause<C, T> childClause;
            childClause = new QueryPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        public MySQLInsert._QueryChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt().asInsert();
        }

        @Override
        MySQLInsert._QueryParentQueryClause<C, P> columnListEnd() {
            return this;
        }

        @Override
        MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._QueryChildInsertIntoSpec<C, P>> spaceEnd() {
            return new ParentDuplicateKeyUpdateSpec<>(this);
        }


        private MySQLInsert._QueryParentColumnListClause<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLQueryInsertStatement createParentStmt() {
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final MySQLQueryInsertStatement statement;
            if (pairList.size() == 0) {
                statement = new MySQLQueryInsertStatement(this);
            } else {
                statement = new QueryInsertWithDuplicate(this, pairList);
            }
            return statement;
        }


    }//QueryParentPartitionClause


    static class MySQLQueryInsertStatement extends InsertSupport.QueryInsertStatement<Insert>
            implements MySQLInsert, Insert._InsertSpec, _MySQLInsert._MySQQueryInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;


        private MySQLQueryInsertStatement(QueryPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        private MySQLQueryInsertStatement(QueryParentPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//MySQLQueryInsertStatement

    private static final class QueryInsertWithDuplicate extends MySQLQueryInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<FieldMeta<?>, _Expression>> pairList;

        private QueryInsertWithDuplicate(QueryPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        private QueryInsertWithDuplicate(QueryParentPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }


        @Override
        public List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList() {
            return this.pairList;
        }

    }//QueryInsertWithDuplicate


    private static class QueryChildInsertStatement extends MySQLQueryInsertStatement
            implements _Insert._ChildQueryInsert {

        private final _QueryInsert parentStmt;

        private QueryChildInsertStatement(QueryPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _QueryInsert parentStmt() {
            return this.parentStmt;
        }


    }//QueryChildInsertStatement

    private static final class QueryChildInsertWithDuplicate extends QueryChildInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<FieldMeta<?>, _Expression>> pairList;

        private QueryChildInsertWithDuplicate(QueryPartitionClause<?, ?> clause
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList() {
            return this.pairList;
        }


    }//QueryChildInsertWithDuplicate


}
