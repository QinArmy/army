package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLReplace;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLReplaces extends InsertSupport {

    private MySQLReplaces() {
    }


    static <C> MySQLReplace._DomainOptionSpec<C> domainReplace(@Nullable C criteria) {
        return new DomainReplaceOptionClause<>(criteria);
    }


    private static abstract class MySQLReplaceClause<C, MR, NR, RR> extends InsertSupport.InsertOptionsImpl<MR, NR>
            implements MySQLReplace._ReplaceClause<C, RR> {

        private List<Hint> hintList;

        private List<MySQLWords> modifierList;

        private MySQLReplaceClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR replace(Supplier<List<Hint>> hints, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::replaceModifier);
            return (RR) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR replace(Function<C, List<Hint>> hints, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.apply(this.criteriaContext.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::replaceModifier);
            return (RR) this;
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


    }//MySQLReplaceClause


    private static final class DomainReplaceOptionClause<C> extends MySQLReplaceClause<
            C,
            MySQLReplace._DomainNullOptionSpec<C>,
            MySQLReplace._DomainPreferLiteralSpec<C>,
            MySQLReplace._DomainIntoClause<C>>
            implements MySQLReplace._DomainOptionSpec<C>, MySQLReplace._DomainIntoClause<C>, DomainInsertOptions {

        private boolean preferLiteral;

        private DomainReplaceOptionClause(@Nullable C criteria) {
            super(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLReplace._DomainReplaceIntoSpec<C> preferLiteral(boolean prefer) {
            this.preferLiteral = prefer;
            return this;
        }

        @Override
        public <T extends IDomain> MySQLReplace._DomainPartitionSpec<C, T, FieldMeta<T>> into(SingleTableMeta<T> table) {
            return new DomainReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._DomainParentPartitionSpec<C, T, FieldMeta<? super T>> into(ChildTableMeta<T> table) {
            return new DomainReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._DomainPartitionSpec<C, T, FieldMeta<T>> replaceInto(SingleTableMeta<T> table) {
            return new DomainReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._DomainParentPartitionSpec<C, T, FieldMeta<? super T>> replaceInto(ChildTableMeta<T> table) {
            return new DomainReplaceStatement<>(this, table);
        }

        @Override
        public boolean isPreferLiteral() {
            return this.preferLiteral;
        }


    }//DomainReplaceOptionClause


    static final class DomainReplaceStatement<C, T extends IDomain, F extends TableField>
            extends InsertSupport.DomainValueClause<
            C,
            T,
            F,
            MySQLReplace._DomainCommonExpSpec<C, T, F>,
            ReplaceInsert._ReplaceSpec>
            implements MySQLReplace._DomainParentPartitionSpec<C, T, F>
            , MySQLReplace._DomainChildPartitionSpec<C, T, F>
            , MySQLReplace._DomainPartitionSpec<C, T, F>
            , MySQLReplace, ReplaceInsert._ReplaceSpec
            , _MySQLInsert._MySQLDomainInsert, _MySQLInsert._MySQLReplace {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<String> childPartitionList;


        private Boolean prepared;


        private DomainReplaceStatement(DomainReplaceOptionClause<C> clause, TableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._DomainColumnListSpec<C, T, F>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._DomainChildPartitionSpec<C, T, F>> parentPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::parentPartitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._DomainColumnListSpec<C, T, F>> childPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::childPartitionEnd);
        }


        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public ReplaceInsert asInsert() {
            _Assert.nonPrepared(this.prepared);
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
            super.clear();
            this.partitionList = null;
            this.childPartitionList = null;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


        @Override
        public List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public List<String> childPartitionList() {
            List<String> list = this.childPartitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public List<MySQLWords> modifierList() {
            return this.modifierList;
        }


        @Override
        MySQLReplace._DomainCommonExpSpec<C, T, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }

        @Override
        ReplaceInsert._ReplaceSpec valuesEnd() {
            return this;
        }

        private MySQLReplace._DomainColumnListSpec<C, T, F> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLReplace._DomainChildPartitionSpec<C, T, F> parentPartitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLReplace._DomainColumnListSpec<C, T, F> childPartitionEnd(List<String> partitionList) {
            this.childPartitionList = partitionList;
            return this;
        }


    }//DomainReplaceStatement


    private static final class ValueReplaceOptionClause<C>
            extends MySQLReplaceClause<
            C,
            MySQLReplace._ValueNullOptionSpec<C>,
            MySQLReplace._ValueReplaceIntoSpec<C>,
            MySQLReplace._ValueIntoClause<C>>
            implements MySQLReplace._ValueReplaceOptionSpec<C>
            , MySQLReplace._ValueIntoClause<C> {

        private ValueReplaceOptionClause(@Nullable C criteria) {
            super(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLReplace._ValuePartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table) {
            return null;
        }

        @Override
        public <T extends IDomain> MySQLReplace._ValueParentPartitionSpec<C, FieldMeta<? super T>> into(ChildTableMeta<T> table) {
            return null;
        }

        @Override
        public <T extends IDomain> MySQLReplace._ValuePartitionSpec<C, FieldMeta<T>> replaceInto(SingleTableMeta<T> table) {
            return null;
        }

        @Override
        public <T extends IDomain> MySQLReplace._ValueParentPartitionSpec<C, FieldMeta<? super T>> replaceInto(ChildTableMeta<T> table) {
            return null;
        }


    }//ValueReplaceOptionClause


    static final class MySQLValueReplaceStatement<C, F extends TableField>
            extends InsertSupport.DynamicValueInsertValueClause<
            C,
            F,
            MySQLReplace._ValueCommonExpSpec<C, F>,
            MySQLReplace._ReplaceSpec>
            implements MySQLReplace._ValueParentPartitionSpec<C, F>
            , MySQLReplace._ValueChildPartitionSpec<C, F>
            , MySQLReplace._ValuePartitionSpec<C, F>
            , MySQLReplace, ReplaceInsert._ReplaceSpec
            , _MySQLInsert._MySQLValueInsert, _MySQLInsert._MySQLReplace {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<String> childPartitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;


        private Boolean prepared;


        private MySQLValueReplaceStatement(ValueReplaceOptionClause<C> clause, TableMeta<?> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }


        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._ValueColumnListSpec<C, F>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._ValueChildPartitionSpec<C, F>> parentPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::parentPartitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._ValueColumnListSpec<C, F>> childPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::childPartitionEnd);
        }

        @Override
        public Insert._StaticValueLeftParenClause<C, F, _ReplaceSpec> value() {
            return new MySQLStaticValueClause<>(this);
        }

        @Override
        public _ValueStaticValuesLeftParenClause<C, F> values() {
            return new MySQLStaticValuesClause<>(this);
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public ReplaceInsert asInsert() {
            _Assert.nonPrepared(this.prepared);
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
            super.clear();
            this.partitionList = null;
            this.childPartitionList = null;
            this.rowValuesList = null;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


        @Override
        public List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public List<String> childPartitionList() {
            List<String> list = this.childPartitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            _Assert.prepared(this.prepared);
            return this.rowValuesList;
        }

        @Override
        MySQLReplace._ValueCommonExpSpec<C, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }

        @Override
        _ReplaceSpec valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return this;
        }

        private MySQLReplace._ValueColumnListSpec<C, F> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLReplace._ValueChildPartitionSpec<C, F> parentPartitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLReplace._ValueColumnListSpec<C, F> childPartitionEnd(List<String> partitionList) {
            this.childPartitionList = partitionList;
            return this;
        }


    }//MySQLValueReplaceStatement


    private static final class MySQLStaticValueClause<C, F extends TableField>
            extends InsertSupport.StaticColumnValuePairClause<C, F, ReplaceInsert._ReplaceSpec> {

        private final MySQLValueReplaceStatement<C, F> clause;

        private MySQLStaticValueClause(MySQLValueReplaceStatement<C, F> clause) {
            super(clause.criteriaContext, clause::containField);
            this.clause = clause;
        }

        @Override
        public ReplaceInsert._ReplaceSpec rightParen() {
            return this.clause.valueClauseEnd(this.endValuesClause());
        }


    }//MySQLStaticValueClause

    private static final class MySQLStaticValuesClause<C, F extends TableField>
            extends InsertSupport.StaticColumnValuePairClause<C, F, MySQLReplace._ValueStaticValuesLeftParenSpec<C, F>>
            implements MySQLReplace._ValueStaticValuesLeftParenSpec<C, F> {

        private final MySQLValueReplaceStatement<C, F> clause;

        private MySQLStaticValuesClause(MySQLValueReplaceStatement<C, F> clause) {
            super(clause.criteriaContext, clause::containField);
            this.clause = clause;
        }

        @Override
        public MySQLReplace._ValueStaticValuesLeftParenSpec<C, F> rightParen() {
            this.endCurrentRow();
            return this;
        }

        @Override
        public ReplaceInsert asInsert() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .asInsert();
        }


    }//MySQLStaticValuesClause


}
