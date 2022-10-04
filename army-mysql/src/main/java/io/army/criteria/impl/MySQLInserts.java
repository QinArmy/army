package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLModifier;
import io.army.dialect.mysql.MySQLDialect;
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

        private List<MySQLModifier> modifierList;

        private MySQLInsertClause(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }


        @Override
        public final IR insert(Supplier<List<Hint>> supplier, List<MySQLModifier> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return (IR) this;
        }

        @Override
        public final IR insert(Function<C, List<Hint>> function, List<MySQLModifier> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.context, function.apply(this.context.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return (IR) this;
        }


        final List<Hint> hintList() {
            List<Hint> list = this.hintList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        final List<MySQLModifier> modifierList() {
            List<MySQLModifier> list = this.modifierList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//InsertClause


    @SuppressWarnings("unchecked")
    private static abstract class DuplicateKeyUpdateClause<C, T, UR, DR>
            implements Insert._CommaFieldValuePairClause<C, T, UR>
            , MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, UR>
            , MySQLInsert._StaticOnDuplicateKeyFieldClause<C, T, UR>
            , MySQLInsert._DynamicOnDuplicateKeyUpdateClause<C, PairConsumer<T>, DR> {

        final CriteriaContext context;

        final C criteria;

        final ClauseBeforeRowAlias clause;

        final TableMeta<?> table;

        private boolean optionalOnDuplicateKey = true;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private DuplicateKeyUpdateClause(ClauseBeforeRowAlias clause) {
            this.context = clause.getContext();
            this.criteria = this.context.criteria();
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
        public final DR onDuplicateKeyUpdate(Consumer<PairConsumer<T>> consumer) {
            this.optionalOnDuplicateKey = false;
            final DynamicPairsConstructor<T> constructor;
            constructor = new DynamicPairsConstructor<>(this.context, this.clause::validateField);
            constructor.row();
            consumer.accept(constructor);

            List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;//TODO
            constructor.endPairConsumer();
            return (DR) this;
        }

        @Override
        public final DR onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<T>> consumer) {
            this.optionalOnDuplicateKey = false;
            consumer.accept(this.criteria, this);
            return (DR) this;
        }

        @Override
        public final DR ifOnDuplicateKeyUpdate(Consumer<PairConsumer<T>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this);
            return (DR) this;
        }

        @Override
        public final DR ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<T>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this.criteria, this);
            return (DR) this;
        }



        final List<_Pair<FieldMeta<?>, _Expression>> endDuplicateKeyClause() {
            List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                if (!this.optionalOnDuplicateKey) {
                    String m = "You don't add any field and value pair";
                    throw ContextStack.criteriaError(this.context, m);
                }
                pairList = Collections.emptyList();
            } else if (pairList instanceof ArrayList) {
                pairList = Collections.unmodifiableList(pairList);
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.duplicatePairList = pairList;
            return pairList;
        }

        private void addValuePair(final FieldMeta<T> field, final @Nullable Expression value) {

            if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }

            if (field.tableMeta() != this.table) {
                throw ContextStack.criteriaError(this.context, _Exceptions::unknownColumn, field);
            }
            if (field.updateMode() != UpdateMode.UPDATABLE) {
                throw ContextStack.criteriaError(this.context, _Exceptions::nonUpdatableField, field);
            }

            switch (field.fieldName()) {
                case _MetaBridge.UPDATE_TIME:
                case _MetaBridge.VERSION:
                    throw ContextStack.criteriaError(this.context, _Exceptions::armyManageField, field);
            }

            Map<FieldMeta<?>, Boolean> filedMap = this.fieldMap;
            if (filedMap == null) {
                filedMap = new HashMap<>();
                this.fieldMap = filedMap;
            }
            if (filedMap.putIfAbsent(field, Boolean.TRUE) != null) {
                throw duplicationValuePair(this.context, field);
            }

            List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                pairList = new ArrayList<>();
                this.duplicatePairList = pairList;
            }
            pairList.add(_Pair.create(field, (ArmyExpression) value));
        }


    }//DuplicateKeyUpdateClause


    private static final class NonParentDuplicateKeyUpdateSpec<C, T>
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


    private static final class ParentDuplicateKeyUpdateSpec<C, P, CT>
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
            ContextStack.setContextStack(this.context);
        }

        @Override
        public <T> MySQLInsert._DomainPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new DomainPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._DomainParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new DomainParentPartitionClause<>(this, table);
        }


        @Override
        public <T> MySQLInsert._DomainPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new DomainPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._DomainParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new DomainParentPartitionClause<>(this, table);
        }


    }//DomainInsertOptionClause


    private static final class DomainPartitionClause<C, T>
            extends DomainValueShortClause<
            C,
            T,
            MySQLInsert._DomainDefaultSpec<C, T>,
            MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T>>
            implements MySQLInsert._DomainPartitionSpec<C, T>
            , MySQLInserts.ClauseBeforeRowAlias {

        private final DomainParentPartitionClause<C, ?> parentClause;

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private DomainPartitionClause(DomainInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentClause = null;

        }

        private DomainPartitionClause(DomainParentPartitionClause<C, ?> parentClause, ChildTableMeta<T> table) {
            super(parentClause, table);
            this.hintList = _CollectionUtils.safeList(parentClause.childHintList);
            this.modifierList = _CollectionUtils.safeList(parentClause.childModifierList);
            this.parentClause = parentClause;
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLInsert._DomainColumnListSpec<C, T>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }


        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentClause == null) {
                    spec = new DomainInsertStatement(this);
                } else {
                    final _Insert._DomainInsert parentStatement;
                    parentStatement = this.parentClause.createParentStmt(this::domainList);
                    spec = new DomainChildInsertStatement(this, parentStatement);
                }
            } else if (this.parentClause == null) {
                spec = new DomainInsertWithDuplicateKey(this, pairList);
            } else {
                final _Insert._DomainInsert parentStatement;
                parentStatement = this.parentClause.createParentStmt(this::domainList);
                spec = new DomainChildInsertWithDuplicateKey(this, parentStatement, pairList);
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


    private static final class DomainParentPartitionClause<C, P>
            extends DomainValueShortClause<
            C,
            P,
            MySQLInsert._DomainParentColumnDefaultSpec<C, P>,
            MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._DomainChildInsertIntoSpec<C, P>>>
            implements MySQLInsert._DomainParentPartitionSpec<C, P>
            , MySQLInserts.ParentClauseBeforeRowAlias<MySQLInsert._DomainChildInsertIntoSpec<C, P>>
            , MySQLInsert._DomainChildInsertIntoSpec<C, P>
            , MySQLInsert._DomainChildIntoClause<C, P>
            , ValueSyntaxOptions {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLModifier> childModifierList;

        private DomainParentPartitionClause(DomainInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }


        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLInsert._DomainParentColumnsSpec<C, P>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }

        @Override
        public MySQLInsert._DomainChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.context, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._DomainChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.context, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.context, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._DomainPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T> MySQLInsert._DomainPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final DomainPartitionClause<C, T> childClause;
            childClause = new DomainPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        MySQLInsert._DomainParentColumnDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        @Override
        MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._DomainChildInsertIntoSpec<C, P>> valuesEnd() {
            return new ParentDuplicateKeyUpdateSpec<>(this);
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt(this::domainList)
                    .asInsert();
        }

        public MySQLInsert._DomainChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();
            this.duplicatePairList = pairList;
            return this;
        }


        private MySQLInsert._DomainParentColumnsSpec<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


        private DomainInsertStatement createParentStmt(Supplier<List<?>> supplier) {
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw ContextStack.castCriteriaApi(this.context);
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


    static abstract class MySQLValueSyntaxStatement extends ValueSyntaxInsertStatement<Insert>
            implements MySQLInsert, _MySQLInsert, Insert._InsertSpec {


        private MySQLValueSyntaxStatement(_ValuesSyntaxInsert clause) {
            super(clause);

        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//MySQLValueSyntaxStatement


    private static class DomainInsertStatement extends MySQLValueSyntaxStatement
            implements _MySQLInsert._MySQLDomainInsert {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private final List<String> partitionList;
        private final List<?> domainList;

        private DomainInsertStatement(DomainPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.domainList = clause.domainList();
        }

        private DomainInsertStatement(DomainParentPartitionClause<?, ?> clause
                , Supplier<List<?>> supplier) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.domainList = supplier.get();
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLModifier> modifierList() {
            return this.modifierList;
        }


        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<?> domainList() {
            return this.domainList;
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
                , List<_Pair<FieldMeta<?>, _Expression>> pairList, Supplier<List<?>> supplier) {
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

        private DomainChildInsertStatement(DomainPartitionClause<?, ?> clause, _Insert._DomainInsert parentStmt) {
            super(clause);
            assert clause.parentClause != null;
            this.parentStmt = parentStmt;
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
                , _Insert._DomainInsert parentStmt
                , List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            super(clause, parentStmt);
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
            ContextStack.setContextStack(this.context);
        }

        @Override
        public <T> MySQLInsert._ValuePartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new ValuePartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._ValueParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new ValueParentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._ValuePartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new ValuePartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._ValueParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new ValueParentPartitionClause<>(this, table);
        }

    }//ValueOptionClause


    private static final class StaticValuesLeftParenClause<C, T>
            extends InsertSupport.StaticColumnValuePairClause<C, T, MySQLInsert._ValueStaticValuesLeftParenSpec<C, T>>
            implements MySQLInsert._ValueStaticValuesLeftParenSpec<C, T> {

        private final ValuePartitionClause<C, T> clause;

        private StaticValuesLeftParenClause(ValuePartitionClause<C, T> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>> onDuplicateKey() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }


        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<T>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<T>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<T>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<T>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEndBeforeAs(this.endValuesClause());
        }

    }//StaticValuesLeftParenClause

    private static final class ParentStaticValuesLeftParenClause<C, T>
            extends InsertSupport.StaticColumnValuePairClause<C, T, MySQLInsert._ValueParentStaticValueLeftParenSpec<C, T>>
            implements MySQLInsert._ValueParentStaticValueLeftParenSpec<C, T> {

        private final ValueParentPartitionClause<C, T> clause;

        private ParentStaticValuesLeftParenClause(ValueParentPartitionClause<C, T> clause) {
            super(clause.getContext(), clause::validateField);
            this.clause = clause;
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, T, MySQLInsert._ValueChildInsertIntoSpec<C, T>>> onDuplicateKey() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }


        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> onDuplicateKeyUpdate(Consumer<PairConsumer<T>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<T>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> ifOnDuplicateKeyUpdate(Consumer<PairConsumer<T>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<T>> consumer) {
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


    private static final class ValuePartitionClause<C, T>
            extends DynamicValueInsertValueClauseShort<
            C,
            T,
            MySQLInsert._ValueDefaultSpec<C, T>,
            MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T>>
            implements MySQLInsert._ValuePartitionSpec<C, T>
            , ClauseBeforeRowAlias {

        private final _Insert._ValuesInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowList;


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
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLInsert._ValueColumnListSpec<C, T>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }

        @Override
        public MySQLInsert._ValueStaticValuesLeftParenClause<C, T> values() {
            if (this.rowList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new StaticValuesLeftParenClause<>(this);
        }


        @Override
        MySQLInsert._ValueDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            final List<Map<FieldMeta<?>, _Expression>> rowList = this.rowList;
            if (rowList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new ValuesInsertStatement(this);
                } else if (rowList.size() == this.parentStmt.rowPairList().size()) {
                    spec = new ValueChildInsertStatement(this);
                } else {
                    throw childAndParentRowsNotMatch(this.context, (ChildTableMeta<?>) this.insertTable
                            , this.parentStmt.rowPairList().size(), rowList.size());
                }
            } else if (this.parentStmt == null) {
                spec = new ValuesInsertWithDuplicateKey(this, pairList);
            } else if (rowList.size() == this.parentStmt.rowPairList().size()) {
                spec = new ValueChildInsertWithDuplicateKey(this, pairList);
            } else {
                throw childAndParentRowsNotMatch(this.context, (ChildTableMeta<?>) this.insertTable
                        , this.parentStmt.rowPairList().size(), rowList.size());
            }
            return spec.asInsert();
        }


        @Override
        MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T> valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowList) {
            if (this.rowList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.rowList = rowList;
            return new NonParentDuplicateKeyUpdateSpec<>(this);
        }

        Insert valueClauseEndBeforeAs(final List<Map<FieldMeta<?>, _Expression>> rowList) {
            if (this.rowList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.rowList = rowList;
            return this.endInsert(Collections.emptyList());
        }

        private MySQLInsert._ValueColumnListSpec<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


    }//ValuePartitionClause


    private static final class ValueParentPartitionClause<C, P>
            extends DynamicValueInsertValueClauseShort<
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

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowList;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLModifier> childModifierList;

        private ValueParentPartitionClause(ValueInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLInsert._ValueParentColumnsSpec<C, P>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }

        @Override
        public MySQLInsert._ValueParentStaticValueLeftParenClause<C, P> values() {
            if (this.rowList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new ParentStaticValuesLeftParenClause<>(this);
        }

        @Override
        public MySQLInsert._ValueChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.context, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._ValueChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.context, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.context, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._ValuePartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T> MySQLInsert._ValuePartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final ValuePartitionClause<C, T> childClause;
            childClause = new ValuePartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }


        @Override
        public MySQLInsert._ValueChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<FieldMeta<?>, _Expression>> rowList) {
            if (this.duplicatePairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.duplicatePairList = rowList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> rowList) {
            if (this.duplicatePairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.duplicatePairList = rowList;
            return this.createParentStmt().asInsert();
        }


        MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._ValueChildInsertIntoSpec<C, P>> valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowList) {
            if (this.rowList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.rowList = rowList;
            return new ParentDuplicateKeyUpdateSpec<>(this);
        }

        private Insert valueClauseEndBeforeAs(final List<Map<FieldMeta<?>, _Expression>> rowList) {
            if (this.rowList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.rowList = rowList;
            return this.endInsert(Collections.emptyList());
        }

        private MySQLInsert._ValueChildInsertIntoSpec<C, P> childBeforeAs(final List<Map<FieldMeta<?>, _Expression>> rowList) {
            if (this.rowList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.rowList = rowList;
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
            if (this.rowList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw ContextStack.castCriteriaApi(this.context);
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

        private final List<MySQLModifier> modifierList;

        private final List<String> partitionList;

        private final List<Map<FieldMeta<?>, _Expression>> rowList;

        private ValuesInsertStatement(ValuePartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowList = clause.rowList;

            assert this.rowList != null;
        }

        private ValuesInsertStatement(ValueParentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowList = clause.rowList;

            assert this.rowList != null;
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLModifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowList;
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
            ContextStack.setContextStack(this.context);
        }

        @Override
        public <T> MySQLInsert._AssignmentPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new AssignmentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._AssignmentParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new AssignmentParentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._AssignmentPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new AssignmentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._AssignmentParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new AssignmentParentPartitionClause<>(this, table);
        }

    }//AssignmentInsertOptionClause


    private static final class AssignmentPartitionClause<C, T>
            extends InsertSupport.AssignmentInsertClause<C, T, MySQLInsert._MySQLAssignmentSetSpec<C, T>>
            implements MySQLInsert._AssignmentPartitionSpec<C, T>
            , MySQLInsert._MySQLAssignmentSetSpec<C, T>
            , ClauseBeforeRowAlias {

        private final _Insert._AssignmentInsert parentStmt;
        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;


        private AssignmentPartitionClause(AssignmentInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;
        }

        private AssignmentPartitionClause(AssignmentParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt(); //couldn't invoke asInsert method
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLInsert._MySQLAssignmentSetClause<C, T>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }


        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>> onDuplicateKey() {
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .onDuplicateKey();
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<T>> consumer) {
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<T>> consumer) {
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<T>> consumer) {
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<T>> consumer) {
            return new NonParentDuplicateKeyUpdateSpec<C, T>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            return this.endInsert(Collections.emptyList());
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            this.endAssignmentSetClause();
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


    }//AssignmentPartitionClause


    private static final class AssignmentParentPartitionClause<C, P>
            extends InsertSupport.AssignmentInsertClause<C, P, MySQLInsert._AssignmentParentSetSpec<C, P>>
            implements MySQLInsert._AssignmentParentPartitionSpec<C, P>
            , MySQLInsert._AssignmentParentSetSpec<C, P>
            , ParentClauseBeforeRowAlias<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>
            , MySQLInsert._AssignmentChildInsertIntoSpec<C, P>
            , MySQLInsert._AssignmentChildIntoClause<C, P>
            , InsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLModifier> childModifierList;

        private AssignmentParentPartitionClause(AssignmentInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLInsert._AssignmentParentSetClause<C, P>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, P, MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>> onDuplicateKey() {
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .onDuplicateKey();
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> onDuplicateKeyUpdate(Consumer<PairConsumer<P>> consumer) {
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<P>> consumer) {
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> ifOnDuplicateKeyUpdate(Consumer<PairConsumer<P>> consumer) {
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<P>> consumer) {
            return new ParentDuplicateKeyUpdateSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            return this.endInsert(Collections.emptyList());
        }

        @Override
        public MySQLInsert._AssignmentChildInsertIntoSpec<C, P> child() {
            return this.parentStmtEnd(Collections.emptyList());
        }

        @Override
        public MySQLInsert._AssignmentChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._AssignmentChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._AssignmentPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T> MySQLInsert._AssignmentPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final AssignmentPartitionClause<C, T> childClause;
            childClause = new AssignmentPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        public MySQLInsert._AssignmentChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList == null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.endAssignmentSetClause();
            this.duplicatePairList = pairList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.endAssignmentSetClause();
            this.duplicatePairList = pairList;
            return this.createParentStmt().asInsert();
        }

        private MySQLInsert._AssignmentParentSetClause<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private AssignmentsInsertStatement createParentStmt() {
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
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

        private final List<MySQLModifier> modifierList;

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
        public final List<MySQLModifier> modifierList() {
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
                s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
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

        private List<MySQLModifier> modifierList;

        private QueryInsertIntoClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            ContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLInsert._QueryIntoClause<C> insert(Supplier<List<Hint>> supplier, List<MySQLModifier> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::queryInsertModifier);
            return this;
        }

        @Override
        public MySQLInsert._QueryIntoClause<C> insert(Function<C, List<Hint>> function, List<MySQLModifier> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteriaContext.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::queryInsertModifier);
            return this;
        }


        @Override
        public <T> MySQLInsert._QueryPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new QueryPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._QueryParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new QueryParentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._QueryPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new QueryPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLInsert._QueryParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
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
    private static class QueryPartitionClause<C, T>
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

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private QueryPartitionClause(QueryInsertIntoClause<C> clause, SimpleTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.hintList);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);
            this.parentStmt = null;
        }

        private QueryPartitionClause(QueryParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause.context, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt(); //couldn't invoke asInsert method
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLInsert._QueryColumnListClause<C, T>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
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


    private static final class QueryParentPartitionClause<C, P>
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

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLModifier> childModifierList;

        private QueryParentPartitionClause(QueryInsertIntoClause<C> clause, ParentTableMeta<P> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.hintList);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);

        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLInsert._QueryParentColumnListClause<C, P>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }

        @Override
        public MySQLInsert._QueryChildInsertIntoSpec<C, P> child() {
            return this;
        }

        @Override
        public MySQLInsert._QueryChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.context, modifiers
                    , MySQLUtils::queryInsertModifier);
            return this;
        }

        @Override
        public MySQLInsert._QueryChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.context, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.context, modifiers
                    , MySQLUtils::queryInsertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._QueryPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T> MySQLInsert._QueryPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final QueryPartitionClause<C, T> childClause;
            childClause = new QueryPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        public MySQLInsert._QueryChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.duplicatePairList = pairList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<FieldMeta<?>, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
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
                throw ContextStack.castCriteriaApi(this.context);
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


    static class MySQLQueryInsertStatement extends QuerySyntaxInsertStatement<Insert>
            implements MySQLInsert, Insert._InsertSpec, _MySQLInsert._MySQQueryInsert {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

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
        public final List<MySQLModifier> modifierList() {
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
                s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
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
