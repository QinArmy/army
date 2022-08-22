package io.army.criteria.impl;

import io.army.criteria.Hint;
import io.army.criteria.ReplaceInsert;
import io.army.criteria.Statement;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLModifier;
import io.army.criteria.mysql.MySQLReplace;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._CollectionUtils;

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

    static <C> MySQLReplace._QueryReplaceIntoSpec<C> queryReplace(@Nullable C criteria) {
        return new QueryReplaceIntoClause<>(criteria);
    }


    private static abstract class MySQLReplaceClause<C, MR, NR, PR, RR>
            extends InsertSupport.NonQueryInsertOptionsImpl<MR, NR, PR>
            implements MySQLReplace._ReplaceClause<C, RR> {

        private List<Hint> hintList;

        private List<MySQLModifier> modifierList;

        private MySQLReplaceClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR replace(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::replaceModifier);
            return (RR) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR replace(Function<C, List<Hint>> hints, List<MySQLModifier> modifiers) {
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

        final List<MySQLModifier> modifierList() {
            List<MySQLModifier> list = this.modifierList;
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
            MySQLReplace._DomainReplaceIntoSpec<C>,
            MySQLReplace._DomainIntoClause<C>>
            implements MySQLReplace._DomainOptionSpec<C>, MySQLReplace._DomainIntoClause<C> {

        private DomainReplaceOptionClause(@Nullable C criteria) {
            super(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T> MySQLReplace._DomainPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new DomainPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._DomainParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new DomainParentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._DomainPartitionSpec<C, T> replaceInto(SimpleTableMeta<T> table) {
            return new DomainPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._DomainParentPartitionSpec<C, T> replaceInto(ParentTableMeta<T> table) {
            return new DomainParentPartitionClause<>(this, table);
        }


    }//DomainReplaceOptionClause


    private static final class DomainPartitionClause<C, T>
            extends InsertSupport.DomainValueClause<
            C,
            T,
            MySQLReplace._DomainDefaultSpec<C, T>,
            ReplaceInsert._ReplaceSpec>
            implements MySQLReplace._DomainPartitionSpec<C, T> {

        private final DomainParentPartitionClause<C, ?> parentClause;

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;


        private DomainPartitionClause(DomainReplaceOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentClause = null;
        }

        private DomainPartitionClause(DomainParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentClause = clause;
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLReplace._DomainColumnListSpec<C, T>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        MySQLReplace._DomainDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        ReplaceInsert._ReplaceSpec valuesEnd() {
            final ReplaceInsert._ReplaceSpec spec;
            if (this.parentClause == null) {
                spec = new DomainReplaceStatement(this);
            } else {
                final _Insert._DomainInsert parentStatement;
                parentStatement = this.parentClause.createParentStmt(this::domainList);
                spec = new DomainChildReplaceStatement(this, parentStatement);
            }
            return spec;
        }

        private MySQLReplace._DomainColumnListSpec<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


    }//DomainPartitionClause


    private static final class DomainParentPartitionClause<C, P>
            extends InsertSupport.DomainValueClause<
            C,
            P,
            MySQLReplace._DomainParentDefaultSpec<C, P>,
            ReplaceInsert._ReplaceSpec>
            implements MySQLReplace._DomainParentPartitionSpec<C, P>
            , MySQLReplace._DomainChildReplaceIntoSpec<C, P>
            , MySQLReplace._DomainChildIntoClause<C, P>
            , ValueSyntaxOptions {


        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<Hint> childHintList;

        private List<MySQLModifier> childModifierList;


        private DomainParentPartitionClause(DomainReplaceOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLReplace._DomainParentColumnsSpec<C, P>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLReplace._DomainChildReplaceIntoSpec<C, P> child() {
            return this;
        }

        @Override
        public MySQLReplace._DomainChildIntoClause<C, P> replace(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public MySQLReplace._DomainChildIntoClause<C, P> replace(Function<C, List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, hints.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._DomainPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.replaceInto(table);
        }

        @Override
        public <T> MySQLReplace._DomainPartitionSpec<C, T> replaceInto(ComplexTableMeta<P, T> table) {
            final DomainPartitionClause<C, T> childClause;
            childClause = new DomainPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        MySQLReplace._DomainParentDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        @Override
        ReplaceInsert._ReplaceSpec valuesEnd() {
            return this.createParentStmt(this::domainList);
        }

        private MySQLReplace._DomainParentColumnsSpec<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private DomainReplaceStatement createParentStmt(Supplier<List<?>> supplier) {
            return new DomainReplaceStatement(this, supplier);
        }


    }//DomainParentPartitionClause


    private static abstract class MySQLValuesSyntaxReplaceStatement
            extends InsertSupport.ValueSyntaxStatement<ReplaceInsert>
            implements MySQLReplace, ReplaceInsert._ReplaceSpec, _Insert._DuplicateKeyClause {

        private MySQLValuesSyntaxReplaceStatement(_ValuesSyntaxInsert clause) {
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


    }//MySQLValuesSyntaxReplaceStatement


    static class DomainReplaceStatement extends MySQLValuesSyntaxReplaceStatement
            implements _MySQLInsert._MySQLDomainInsert {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private final List<String> partitionList;

        private final List<?> domainList;


        private DomainReplaceStatement(DomainPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.domainList = clause.domainList();
        }

        private DomainReplaceStatement(DomainParentPartitionClause<?, ?> clause
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


    }//DomainReplaceStatement


    private static final class DomainChildReplaceStatement extends DomainReplaceStatement
            implements _Insert._ChildDomainInsert {

        private final _Insert._DomainInsert parentStmt;

        private DomainChildReplaceStatement(DomainPartitionClause<?, ?> clause, _Insert._DomainInsert parentStmt) {
            super(clause);
            this.parentStmt = parentStmt;
        }

        @Override
        public _DomainInsert parentStmt() {
            return this.parentStmt;
        }


    }//DomainChildReplaceStatement


    private static final class ValueReplaceOptionClause<C>
            extends MySQLReplaceClause<
            C,
            MySQLReplace._ValueNullOptionSpec<C>,
            MySQLReplace._ValuePreferLiteralSpec<C>,
            MySQLReplace._ValueReplaceIntoSpec<C>,
            MySQLReplace._ValueIntoClause<C>>
            implements MySQLReplace._ValueReplaceOptionSpec<C>
            , MySQLReplace._ValueIntoClause<C> {

        private ValueReplaceOptionClause(@Nullable C criteria) {
            super(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T> MySQLReplace._ValuePartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new ValuePartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._ValueParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new ValueParentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._ValuePartitionSpec<C, T> replaceInto(SimpleTableMeta<T> table) {
            return new ValuePartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._ValueParentPartitionSpec<C, T> replaceInto(ParentTableMeta<T> table) {
            return new ValueParentPartitionClause<>(this, table);
        }


    }//ValueReplaceOptionClause


    private static final class ValuePartitionClause<C, T>
            extends InsertSupport.DynamicValueInsertValueClause<
            C,
            T,
            MySQLReplace._ValueDefaultSpec<C, T>,
            MySQLReplace._ReplaceSpec>
            implements MySQLReplace._ValuePartitionSpec<C, T> {

        private final _Insert._ValuesInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private ValuePartitionClause(ValueReplaceOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;
        }

        private ValuePartitionClause(ValueParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt();//couldn't invoke asInsert
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLReplace._ValueColumnListSpec<C, T>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLReplace._ValueStaticValuesLeftParenClause<C, T> values() {
            return new MySQLStaticValuesSpec<>(this);
        }

        @Override
        MySQLReplace._ValueDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        MySQLReplace._ReplaceSpec valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowList) {
            final MySQLReplace._ReplaceSpec spec;
            if (this.parentStmt == null) {
                spec = new ValuesReplaceStatement(this, rowList);
            } else if (rowList.size() == this.parentStmt.rowList().size()) {
                spec = new ValuesChildReplaceStatement(this, rowList);
            } else {
                throw childAndParentRowsNotMatch(this.criteriaContext, (ChildTableMeta<?>) this.insertTable
                        , this.parentStmt.rowList().size(), rowList.size());
            }
            return spec;
        }

        private MySQLReplace._ValueColumnListSpec<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


    }//ValuePartitionClause


    private static final class ValueParentPartitionClause<C, P>
            extends InsertSupport.DynamicValueInsertValueClause<
            C,
            P,
            MySQLReplace._ValueParentDefaultSpec<C, P>,
            MySQLReplace._ValueChildSpec<C, P>>
            implements MySQLReplace._ValueParentPartitionSpec<C, P>
            , MySQLReplace._ValueChildSpec<C, P>
            , MySQLReplace._ValueChildReplaceIntoSpec<C, P>
            , MySQLReplace._ValueChildIntoClause<C, P>
            , ValueSyntaxOptions {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private List<Hint> childHintList;

        private List<MySQLModifier> childModifierList;


        private ValueParentPartitionClause(ValueReplaceOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLReplace._ValueParentColumnsSpec<C, P>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLReplace._ValueParentStaticValuesLeftParenClause<C, P> values() {
            return new MySQLParentStaticValuesSpec<>(this);
        }

        @Override
        public ReplaceInsert asInsert() {
            return this.createParentStmt().asInsert();
        }

        @Override
        public MySQLReplace._ValueChildReplaceIntoSpec<C, P> child() {
            return this;
        }

        @Override
        public MySQLReplace._ValueChildIntoClause<C, P> replace(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public MySQLReplace._ValueChildIntoClause<C, P> replace(Function<C, List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, hints.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._ValuePartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.replaceInto(table);
        }

        @Override
        public <T> MySQLReplace._ValuePartitionSpec<C, T> replaceInto(ComplexTableMeta<P, T> table) {
            final ValuePartitionClause<C, T> childClause;
            childClause = new ValuePartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        MySQLReplace._ValueParentDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        @Override
        MySQLReplace._ValueChildSpec<C, P> valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowList;
            return this;
        }

        private MySQLReplace._ValueParentColumnsSpec<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private ValuesReplaceStatement createParentStmt() {
            final List<Map<FieldMeta<?>, _Expression>> rowValuesList = this.rowValuesList;
            if (rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new ValuesReplaceStatement(this, rowValuesList);
        }


    }//ValueParentPartitionClause


    private static final class MySQLStaticValuesSpec<C, T>
            extends InsertSupport.StaticColumnValuePairClause<C, T, MySQLReplace._ValueStaticValuesLeftParenSpec<C, T>>
            implements MySQLReplace._ValueStaticValuesLeftParenSpec<C, T> {

        private final ValuePartitionClause<C, T> clause;

        private MySQLStaticValuesSpec(ValuePartitionClause<C, T> clause) {
            super(clause.criteriaContext, clause::validateField);
            this.clause = clause;
        }

        @Override
        public MySQLReplace._ValueStaticValuesLeftParenSpec<C, T> rightParen() {
            this.endCurrentRow();
            return this;
        }

        @Override
        public ReplaceInsert asInsert() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .asInsert();
        }


    }//MySQLStaticValuesSpec


    private static final class MySQLParentStaticValuesSpec<C, P>
            extends InsertSupport.StaticColumnValuePairClause<C, P, MySQLReplace._ValueParentStaticValuesLeftParenSpec<C, P>>
            implements MySQLReplace._ValueParentStaticValuesLeftParenSpec<C, P> {

        private final ValueParentPartitionClause<C, P> clause;

        private MySQLParentStaticValuesSpec(ValueParentPartitionClause<C, P> clause) {
            super(clause.criteriaContext, clause::validateField);
            this.clause = clause;
        }

        @Override
        public MySQLReplace._ValueParentStaticValuesLeftParenSpec<C, P> rightParen() {
            this.endCurrentRow();
            return this;
        }

        @Override
        public ReplaceInsert asInsert() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .asInsert();
        }

        @Override
        public MySQLReplace._ValueChildReplaceIntoSpec<C, P> child() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .child();
        }


    }//MySQLParentStaticValuesSpec


    static class ValuesReplaceStatement extends MySQLValuesSyntaxReplaceStatement
            implements _MySQLInsert._MySQLValueInsert {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private final List<String> partitionList;

        private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private ValuesReplaceStatement(ValuePartitionClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowValuesList = rowValuesList;
        }

        private ValuesReplaceStatement(ValueParentPartitionClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowValuesList = rowValuesList;
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
        public final List<Map<FieldMeta<?>, _Expression>> rowList() {
            return this.rowValuesList;
        }


    }//ValuesReplaceStatement


    private static final class ValuesChildReplaceStatement extends ValuesReplaceStatement
            implements _Insert._ChildValuesInsert {

        private final _Insert._ValuesInsert parentStmt;

        private ValuesChildReplaceStatement(ValuePartitionClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause, rowValuesList);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStmt;
        }


    }//ValuesChildReplaceStatement


    /*-----------------------below assignment replace class -----------------------*/


    private static final class AssignmentReplaceOptionClause<C> extends MySQLReplaceClause<
            C,
            MySQLReplace._AssignmentNullOptionSpec<C>,
            MySQLReplace._AssignmentPreferLiteralSpec<C>,
            MySQLReplace._AssignmentReplaceIntoSpec<C>,
            MySQLReplace._AssignmentIntoClause<C>>
            implements MySQLReplace._AssignmentOptionSpec<C>
            , MySQLReplace._AssignmentIntoClause<C> {

        private AssignmentReplaceOptionClause(@Nullable C criteria) {
            super(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T> MySQLReplace._AssignmentPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new AssignmentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._AssignmentParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new AssignmentParentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._AssignmentPartitionSpec<C, T> replaceInto(SimpleTableMeta<T> table) {
            return new AssignmentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._AssignmentParentPartitionSpec<C, T> replaceInto(ParentTableMeta<T> table) {
            return new AssignmentParentPartitionClause<>(this, table);
        }


    }//AssignmentReplaceOptionClause


    static final class AssignmentPartitionClause<C, T>
            extends InsertSupport.AssignmentInsertClause<
            C,
            T,
            MySQLReplace._AssignmentReplaceSetSpec<C, T>>
            implements MySQLReplace._AssignmentPartitionSpec<C, T>
            , MySQLReplace._AssignmentReplaceSetSpec<C, T> {

        private final _Insert._AssignmentInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private AssignmentPartitionClause(AssignmentReplaceOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;
        }

        private AssignmentPartitionClause(AssignmentParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt(); //couldn't invoke asInsert
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLReplace._AssignmentReplaceSetClause<C, T>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public ReplaceInsert asInsert() {
            this.endAssignmentSetClause();
            final ReplaceInsert._ReplaceSpec spec;
            if (this.parentStmt == null) {
                spec = new AssignmentsReplaceStatement(this);
            } else {
                spec = new AssignmentsChildReplaceStatement(this);
            }
            return spec.asInsert();
        }

        private MySQLReplace._AssignmentReplaceSetClause<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


    }//AssignmentPartitionClause

    private static final class AssignmentParentPartitionClause<C, P>
            extends InsertSupport.AssignmentInsertClause<
            C,
            P,
            MySQLReplace._AssignmentParentReplaceSetSpec<C, P>>
            implements MySQLReplace._AssignmentParentPartitionSpec<C, P>
            , MySQLReplace._AssignmentParentReplaceSetSpec<C, P>
            , MySQLReplace._AssignmentChildReplaceIntoSpec<C, P>
            , MySQLReplace._AssignmentChildIntoClause<C, P>
            , InsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<Hint> childHintList;

        private List<MySQLModifier> childModifierList;

        private AssignmentParentPartitionClause(AssignmentReplaceOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLReplace._AssignmentParentReplaceSetClause<C, P>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public ReplaceInsert asInsert() {
            this.endAssignmentSetClause();
            return this.createParentStmt().asInsert();
        }

        @Override
        public MySQLReplace._AssignmentChildReplaceIntoSpec<C, P> child() {
            this.endAssignmentSetClause();
            return this;
        }

        @Override
        public MySQLReplace._AssignmentChildIntoClause<C, P> replace(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public MySQLReplace._AssignmentChildIntoClause<C, P> replace(Function<C, List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, hints.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._AssignmentPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.replaceInto(table);
        }

        @Override
        public <T> MySQLReplace._AssignmentPartitionSpec<C, T> replaceInto(ComplexTableMeta<P, T> table) {
            final AssignmentPartitionClause<C, T> childClause;
            childClause = new AssignmentPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }


        private MySQLReplace._AssignmentParentReplaceSetClause<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private AssignmentsReplaceStatement createParentStmt() {
            return new AssignmentsReplaceStatement(this);
        }


    }//AssignmentParentPartitionClause


    static class AssignmentsReplaceStatement extends InsertSupport.AssignmentInsertStatement<ReplaceInsert>
            implements MySQLReplace
            , ReplaceInsert._ReplaceSpec
            , _MySQLInsert._MySQLAssignmentInsert
            , _Insert._DuplicateKeyClause {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private final List<String> partitionList;

        private AssignmentsReplaceStatement(AssignmentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        private AssignmentsReplaceStatement(AssignmentParentPartitionClause<?, ?> clause) {
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


    }//AssignmentsReplaceStatement


    private static final class AssignmentsChildReplaceStatement extends AssignmentsReplaceStatement
            implements _Insert._ChildAssignmentInsert {

        private final _Insert._AssignmentInsert parentStmt;

        private AssignmentsChildReplaceStatement(AssignmentPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _AssignmentInsert parentStmt() {
            return this.parentStmt;
        }

    }//AssignmentsChildReplaceStatement


    private static final class QueryReplaceIntoClause<C>
            implements MySQLReplace._QueryReplaceIntoSpec<C>
            , MySQLReplace._QueryIntoClause<C> {

        private final CriteriaContext criteriaContext;

        private List<Hint> hintList;

        private List<MySQLModifier> modifierList;

        private QueryReplaceIntoClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLReplace._QueryIntoClause<C> replace(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public MySQLReplace._QueryIntoClause<C> replace(Function<C, List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.apply(this.criteriaContext.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._QueryPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new QueryPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._QueryParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new QueryParentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._QueryPartitionSpec<C, T> replaceInto(SimpleTableMeta<T> table) {
            return new QueryPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLReplace._QueryParentPartitionSpec<C, T> replaceInto(ParentTableMeta<T> table) {
            return new QueryParentPartitionClause<>(this, table);
        }


    }//QueryReplaceOptionClause


    private static final class QueryPartitionClause<C, T>
            extends InsertSupport.QueryInsertSpaceClause<
            C,
            T,
            MySQLReplace._QuerySubQueryClause<C>,
            ReplaceInsert._ReplaceSpec>
            implements MySQLReplace._QueryPartitionSpec<C, T>
            , MySQLReplace._QuerySubQueryClause<C> {

        private final _Insert._QueryInsert parentStmt;
        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private QueryPartitionClause(QueryReplaceIntoClause<C> clause, SimpleTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.hintList);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);
            this.parentStmt = null;
        }

        private QueryPartitionClause(QueryParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt();//couldn't invoke asInsert method
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLReplace._QueryColumnListClause<C, T>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        MySQLReplace._QuerySubQueryClause<C> columnListEnd() {
            return this;
        }

        @Override
        ReplaceInsert._ReplaceSpec spaceEnd() {
            final ReplaceInsert._ReplaceSpec spec;
            if (this.parentStmt == null) {
                spec = new QueryReplaceStatement(this);
            } else {
                spec = new QueryChildReplaceStatement(this);
            }
            return spec;
        }

        private MySQLReplace._QueryColumnListClause<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


    }//QueryReplaceSubQueryClause

    private static final class QueryParentPartitionClause<C, P>
            extends InsertSupport.QueryInsertSpaceClause<
            C,
            P,
            MySQLReplace._QueryParentSubQueryClause<C, P>,
            MySQLReplace._QueryChildSpec<C, P>>
            implements MySQLReplace._QueryParentPartitionSpec<C, P>
            , MySQLReplace._QueryParentSubQueryClause<C, P>
            , MySQLReplace._QueryChildSpec<C, P>
            , MySQLReplace._QueryChildReplaceIntoSpec<C, P>
            , MySQLReplace._QueryChildIntoClause<C, P> {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private List<String> partitionList;

        private List<Hint> childHintList;

        private List<MySQLModifier> childModifierList;

        private QueryParentPartitionClause(QueryReplaceIntoClause<C> clause, ParentTableMeta<P> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.hintList);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<C, MySQLReplace._QueryParentColumnsClause<C, P>> partition() {
            return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public ReplaceInsert asInsert() {
            return this.createParentStmt()
                    .asInsert();
        }

        @Override
        public MySQLReplace._QueryChildReplaceIntoSpec<C, P> child() {
            return this;
        }

        @Override
        public MySQLReplace._QueryChildIntoClause<C, P> replace(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public MySQLReplace._QueryChildIntoClause<C, P> replace(Function<C, List<Hint>> hints, List<MySQLModifier> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, hints.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._QueryPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.replaceInto(table);
        }

        @Override
        public <T> MySQLReplace._QueryPartitionSpec<C, T> replaceInto(ComplexTableMeta<P, T> table) {
            final QueryPartitionClause<C, T> childClause;
            childClause = new QueryPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        MySQLReplace._QueryParentSubQueryClause<C, P> columnListEnd() {
            return this;
        }

        @Override
        MySQLReplace._QueryChildSpec<C, P> spaceEnd() {
            return this;
        }

        private MySQLReplace._QueryParentColumnsClause<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private QueryReplaceStatement createParentStmt() {
            return new QueryReplaceStatement(this);
        }


    }//QueryParentPartitionClause

    static class QueryReplaceStatement extends InsertSupport.QueryInsertStatement<ReplaceInsert>
            implements MySQLReplace
            , ReplaceInsert._ReplaceSpec
            , _MySQLInsert._MySQQueryInsert
            , _Insert._DuplicateKeyClause {

        private final List<Hint> hintList;

        private final List<MySQLModifier> modifierList;

        private final List<String> partitionList;


        private QueryReplaceStatement(QueryPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        private QueryReplaceStatement(QueryParentPartitionClause<?, ?> clause) {
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


    }//QueryReplaceStatement


    private static final class QueryChildReplaceStatement extends QueryReplaceStatement
            implements _Insert._ChildQueryInsert {

        private final _Insert._QueryInsert parentStmt;

        private QueryChildReplaceStatement(QueryPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _QueryInsert parentStmt() {
            return this.parentStmt;
        }


    }//QueryChildReplaceStatement


}
