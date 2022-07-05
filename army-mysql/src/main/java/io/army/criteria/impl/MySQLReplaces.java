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
import io.army.meta.*;
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

    static <C> MySQLReplace._ValueReplaceOptionSpec<C> valueReplace(@Nullable C criteria) {
        return new ValueReplaceOptionClause<>(criteria);
    }

    static <C> MySQLReplace._AssignmentOptionSpec<C> assignmentReplace(@Nullable C criteria) {
        return new AssignmentReplaceOptionClause<>(criteria);
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
            return new MySQLValueReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._ValueParentPartitionSpec<C, FieldMeta<? super T>> into(ChildTableMeta<T> table) {
            return new MySQLValueReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._ValuePartitionSpec<C, FieldMeta<T>> replaceInto(SingleTableMeta<T> table) {
            return new MySQLValueReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._ValueParentPartitionSpec<C, FieldMeta<? super T>> replaceInto(ChildTableMeta<T> table) {
            return new MySQLValueReplaceStatement<>(this, table);
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


    private static final class AssignmentReplaceOptionClause<C> extends MySQLReplaceClause<
            C,
            MySQLReplace._AssignmentNullOptionSpec<C>,
            MySQLReplace._AssignmentReplaceIntoClause<C>,
            MySQLReplace._AssignmentIntoClause<C>>
            implements MySQLReplace._AssignmentOptionSpec<C>
            , MySQLReplace._AssignmentIntoClause<C> {

        private AssignmentReplaceOptionClause(@Nullable C criteria) {
            super(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLReplace._AssignmentPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table) {
            return new MySQLAssignmentReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._AssignmentParentPartitionSpec<C, FieldMeta<? super T>> into(ChildTableMeta<T> table) {
            return new MySQLAssignmentReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._AssignmentPartitionSpec<C, FieldMeta<T>> replaceInto(SingleTableMeta<T> table) {
            return new MySQLAssignmentReplaceStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLReplace._AssignmentParentPartitionSpec<C, FieldMeta<? super T>> replaceInto(ChildTableMeta<T> table) {
            return new MySQLAssignmentReplaceStatement<>(this, table);
        }


    }//AssignmentReplaceOptionClause


    static final class MySQLAssignmentReplaceStatement<C, F extends TableField>
            extends InsertSupport.AssignmentInsertClause<
            C,
            F,
            MySQLReplace._AssignmentReplaceSetSpec<C, F>>
            implements MySQLReplace._AssignmentReplaceSetSpec<C, F>
            , MySQLReplace._AssignmentChildPartitionSpec<C, F>
            , MySQLReplace._AssignmentParentPartitionSpec<C, F>
            , MySQLReplace._AssignmentPartitionSpec<C, F>
            , MySQLReplace, ReplaceInsert._ReplaceSpec
            , _MySQLInsert._MySQLAssignmentInsert, _MySQLInsert._MySQLReplace {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<String> childPartitionList;

        private Boolean prepared;

        private MySQLAssignmentReplaceStatement(AssignmentReplaceOptionClause<C> clause, TableMeta<?> table) {
            super(clause, false, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._AssignmentReplaceSetClause<C, F>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._AssignmentChildPartitionSpec<C, F>> parentPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::parentPartitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._AssignmentReplaceSetClause<C, F>> childPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::childPartitionEnd);
        }


        @Override
        public ReplaceInsert asInsert() {
            _Assert.prepared(this.prepared);
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.endAssignmentSetClause();
            this.prepared = Boolean.TRUE;
            return this;
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
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
            super.clear();
            this.partitionList = null;
            this.childPartitionList = null;
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
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


        private MySQLReplace._AssignmentReplaceSetClause<C, F> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLReplace._AssignmentChildPartitionSpec<C, F> parentPartitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLReplace._AssignmentReplaceSetClause<C, F> childPartitionEnd(List<String> partitionList) {
            this.childPartitionList = partitionList;
            return this;
        }


    }//MySQLAssignmentReplaceStatement


    private static final class QueryReplaceOptionClause<C>
            implements MySQLReplace._QueryReplaceIntoSpec<C>
            , MySQLReplace._QueryIntoClause<C> {

        private final CriteriaContext criteriaContext;

        private List<Hint> hintList;

        private List<MySQLWords> modifierList;

        private QueryReplaceOptionClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLReplace._QueryIntoClause<C> replace(Supplier<List<Hint>> hints, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public MySQLReplace._QueryIntoClause<C> replace(Function<C, List<Hint>> hints, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.apply(this.criteriaContext.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLReplace._QueryPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table) {
            return new QueryReplaceSubQueryClause<>(this, table);
        }

        @Override
        public <P extends IDomain, T extends IDomain> MySQLReplace._QueryParentPartitionSpec<C, P, T> into(ComplexTableMeta<P, T> table) {
            return null;
        }

        @Override
        public <T extends IDomain> MySQLReplace._QueryPartitionSpec<C, FieldMeta<T>> replaceInto(SingleTableMeta<T> table) {
            return new QueryReplaceSubQueryClause<>(this, table);
        }

        @Override
        public <P extends IDomain, T extends IDomain> MySQLReplace._QueryParentPartitionSpec<C, P, T> replaceInto(ComplexTableMeta<P, T> table) {
            return null;
        }

        private List<Hint> hintList() {
            List<Hint> list = this.hintList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private List<MySQLWords> modifierList() {
            List<MySQLWords> list = this.modifierList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

    }//QueryReplaceOptionClause


    private static final class QueryReplaceSubQueryClause<C, F extends TableField>
            extends InsertSupport.ColumnsClause<C, F, MySQLReplace._QuerySubQueryClause<C>>
            implements MySQLReplace._QueryPartitionSpec<C, F>
            , MySQLReplace._QuerySubQueryClause<C> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private SubQuery subQuery;

        private QueryReplaceSubQueryClause(QueryReplaceOptionClause<C> clause, SingleTableMeta<?> table) {
            super(clause.criteriaContext, true, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._QueryColumnListClause<C, F>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public ReplaceInsert._ReplaceSpec space(Supplier<? extends SubQuery> supplier) {
            final SubQuery subQuery;
            subQuery = supplier.get();
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new MySQLQueryReplaceStatement(this);
        }

        @Override
        public ReplaceInsert._ReplaceSpec space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new MySQLQueryReplaceStatement(this);
        }

        @Override
        MySQLReplace._QuerySubQueryClause<C> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize == 0) {
                throw noColumnList(this.criteriaContext, this.table);
            }
            if (childFieldSize > 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        private MySQLReplace._QueryColumnListClause<C, F> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//QueryReplaceSubQueryClause

    private static final class QueryParentReplaceSubQueryClause<C, P extends IDomain, T extends IDomain>
            extends InsertSupport.ColumnsClause<
            C,
            FieldMeta<P>,
            MySQLReplace._QueryParentSubQueryClause<C, FieldMeta<T>>>
            implements MySQLReplace._QueryParentPartitionSpec<C, P, T>
            , MySQLReplace._QueryParentSubQueryClause<C, FieldMeta<T>> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final ChildTableMeta<T> childTable;

        private List<String> partitionList;

        private SubQuery subQuery;

        private QueryParentReplaceSubQueryClause(QueryReplaceOptionClause<C> clause, ComplexTableMeta<P, T> table) {
            super(clause.criteriaContext, true, table.parentMeta());
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.childTable = table;
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._QueryParentColumnsClause<C, FieldMeta<P>, FieldMeta<T>>> parentPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::parentPartitionEnd);
        }

        @Override
        public MySQLReplace._QueryChildPartitionSpec<C, FieldMeta<T>> space(Supplier<? extends SubQuery> supplier) {
            final SubQuery subQuery;
            subQuery = supplier.get();
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return null;
        }

        @Override
        public MySQLReplace._QueryChildPartitionSpec<C, FieldMeta<T>> space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return null;
        }


        @Override
        MySQLReplace._QueryParentSubQueryClause<C, FieldMeta<T>> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize == 0) {
                throw noColumnList(this.criteriaContext, this.table);
            }
            if (childFieldSize > 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        private MySQLReplace._QueryParentColumnsClause<C, FieldMeta<P>, FieldMeta<T>> parentPartitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//QueryParentReplaceSubQueryClause

    private static final class QueryChildReplaceSubQueryClause<C, F extends TableField>
            extends InsertSupport.ColumnsClause<
            C,
            F,
            MySQLReplace._QuerySubQueryClause<C>>
            implements MySQLReplace._QueryChildPartitionSpec<C, F>
            , MySQLReplace._QuerySubQueryClause<C> {

        private final QueryParentReplaceSubQueryClause<C, ?, ?> parentClause;


        private List<String> partitionList;

        private SubQuery subQuery;


        private QueryChildReplaceSubQueryClause(QueryParentReplaceSubQueryClause<C, ?, ?> parentClause) {
            super(parentClause.criteriaContext, parentClause.migration, parentClause.childTable);
            this.parentClause = parentClause;
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLReplace._QueryColumnListClause<C, F>> childPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::childPartitionEnd);
        }

        @Override
        public ReplaceInsert._ReplaceSpec space(Supplier<? extends SubQuery> supplier) {
            final SubQuery subQuery;
            subQuery = supplier.get();
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new MySQLQueryReplaceStatement(this);
        }

        @Override
        public ReplaceInsert._ReplaceSpec space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new MySQLQueryReplaceStatement(this);
        }

        @Override
        MySQLReplace._QuerySubQueryClause<C> columnListEnd(int fieldSize, int childFieldSize) {
            if (childFieldSize == 0) {
                throw noColumnList(this.criteriaContext, this.table);
            }
            if (fieldSize > 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        private MySQLReplace._QueryColumnListClause<C, F> childPartitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

    }//QueryChildReplaceSubQueryClause


    static final class MySQLQueryReplaceStatement extends InsertSupport.QueryInsertStatement<ReplaceInsert>
            implements MySQLReplace, ReplaceInsert._ReplaceSpec
            , _MySQLInsert._MySQQueryInsert, _MySQLInsert._MySQLReplace {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;

        private final List<String> childPartitionList;


        private MySQLQueryReplaceStatement(QueryReplaceSubQueryClause<?, ?> clause) {
            super(clause, clause.subQuery);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = clause.partitionList();
            this.childPartitionList = Collections.emptyList();

        }

        private MySQLQueryReplaceStatement(QueryChildReplaceSubQueryClause<?, ?> clause) {
            super(clause.parentClause, clause.parentClause.subQuery, clause, clause.subQuery);

            this.hintList = clause.parentClause.hintList;
            this.modifierList = clause.parentClause.modifierList;
            this.partitionList = clause.parentClause.partitionList();
            this.childPartitionList = clause.partitionList();

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
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<String> childPartitionList() {
            return this.childPartitionList;
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


    }//MySQLQueryReplaceStatement


}
