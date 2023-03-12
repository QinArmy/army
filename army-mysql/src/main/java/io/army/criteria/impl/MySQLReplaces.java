package io.army.criteria.impl;

import io.army.criteria.InsertStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLReplace;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLReplaces extends InsertSupport {

    private MySQLReplaces() {
        throw new UnsupportedOperationException();
    }


    static MySQLReplace._PrimaryOptionSpec primaryReplace() {
        return new PrimaryReplaceIntoClause();
    }


    private static final class PrimaryReplaceIntoClause extends InsertSupport.NonQueryInsertOptionsImpl<
            MySQLReplace._PrimaryNullOptionSpec,
            MySQLReplace._PrimaryPreferLiteralSpec,
            MySQLReplace._PrimaryReplaceIntoSpec>
            implements MySQLReplace._PrimaryOptionSpec
            , MySQLReplace._PrimaryIntoClause {


        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private PrimaryReplaceIntoClause() {
            super(CriteriaContexts.primaryInsertContext(null));
            ContextStack.push(this.context);
        }

        @Override
        public MySQLReplace._PrimaryIntoClause replace(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<InsertStatement, T> into(SingleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this::singleReplaceEnd);
        }

        @Override
        public <P> MySQLReplace._PartitionSpec<MySQLReplace._ParentReplace<P>, P> into(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, this::parentReplaceEnd);
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<InsertStatement, T> replaceInto(SingleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this::singleReplaceEnd);
        }

        @Override
        public <P> MySQLReplace._PartitionSpec<MySQLReplace._ParentReplace<P>, P> replaceInto(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, this::parentReplaceEnd);
        }


        private InsertStatement singleReplaceEnd(MySQLComplexValuesClause<?, ?> clause) {
            final InsertMode mode;
            mode = clause.getInsertMode();
            final Statement._DmlInsertClause<InsertStatement> spec;
            switch (mode) {
                case DOMAIN:
                    spec = new PrimarySimpleDomainReplaceStatement(clause);
                    break;
                case VALUES:
                    spec = new PrimarySimpleValueReplaceStatement(clause);
                    break;
                case ASSIGNMENT:
                    spec = new PrimarySimpleAssignmentReplaceStatement(clause);
                    break;
                case QUERY:
                    spec = new PrimarySimpleQueryReplaceStatement(clause);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asInsert();
        }

        private <P> MySQLReplace._ParentReplace<P> parentReplaceEnd(MySQLComplexValuesClause<?, ?> clause) {
            final InsertMode mode;
            mode = clause.getInsertMode();
            final Statement._DmlInsertClause<MySQLReplace._ParentReplace<P>> spec;
            switch (mode) {
                case DOMAIN:
                    spec = new PrimaryParentDomainReplaceStatement<>(clause);
                    break;
                case VALUES:
                    spec = new PrimaryParentValueReplaceStatement<>(clause);
                    break;
                case ASSIGNMENT:
                    spec = new PrimaryParentAssignmentReplaceStatement<>(clause);
                    break;
                case QUERY:
                    spec = new PrimaryParentQueryReplaceStatement<>(clause);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asInsert();
        }


    }//PrimaryReplaceIntoClause


    private static final class ChildReplaceIntoClause<P> extends ChildOptionClause
            implements MySQLReplace._ChildReplaceIntoSpec<P>
            , MySQLReplace._ChildIntoClause<P> {

        private final Function<MySQLComplexValuesClause<?, ?>, InsertStatement> dmlFunction;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private ChildReplaceIntoClause(ValueSyntaxOptions options
                , Function<MySQLComplexValuesClause<?, ?>, InsertStatement> dmlFunction) {
            super(options, CriteriaContexts.primaryInsertContext(null));
            this.dmlFunction = dmlFunction;
            ContextStack.push(this.context);
        }

        @Override
        public MySQLReplace._ChildIntoClause<P> replace(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<InsertStatement, T> into(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<InsertStatement, T> replaceInto(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }


    }//ChildReplaceIntoClause


    private static final class MySQLStaticValuesClause<I extends Item, T>
            extends InsertSupport.StaticColumnValuePairClause<
            T,
            MySQLReplace._StaticValuesLeftParenSpec<I, T>>
            implements MySQLReplace._StaticValuesLeftParenSpec<I, T> {

        private final MySQLComplexValuesClause<I, T> clause;

        private MySQLStaticValuesClause(MySQLComplexValuesClause<I, T> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public I asInsert() {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .asInsert();
        }


    }//MySQLStaticValuesClause


    private static final class MySQLComplexValuesClause<I extends Item, T> extends ComplexInsertValuesAssignmentClause<
            T,
            MySQLReplace._ComplexColumnDefaultSpec<I, T>,
            MySQLReplace._ValueColumnDefaultSpec<I, T>,
            Statement._DmlInsertClause<I>,
            Statement._DmlInsertClause<I>>
            implements MySQLReplace._PartitionSpec<I, T>
            , MySQLReplace._ComplexColumnDefaultSpec<I, T>
            , Statement._DmlInsertClause<I> {

        private final Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction;

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private List<String> partitionList;

        private MySQLComplexValuesClause(PrimaryReplaceIntoClause options, TableMeta<T> table
                , Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _CollectionUtils.safeList(options.hintList);
            this.modifierList = _CollectionUtils.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(ChildReplaceIntoClause<?> options, ComplexTableMeta<?, T> table
                , Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _CollectionUtils.safeList(options.hintList);
            this.modifierList = _CollectionUtils.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<MySQLReplace._ColumnListSpec<I, T>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }

        @Override
        public MySQLReplace._MySQLStaticValuesLeftParenClause<I, T> values() {
            return new MySQLStaticValuesClause<>(this);
        }

        @Override
        public MySQLQuery._WithSpec<Statement._DmlInsertClause<I>> space() {
            return MySQLQueries.subQuery(null,this.context, this::staticSpaceQueryEnd);
        }

        @Override
        public I asInsert() {
            this.endStaticAssignmentClauseIfNeed();
            return this.dmlFunction.apply(this);
        }

        private MySQLReplace._ColumnListSpec<I, T> partitionEnd(final List<String> list) {
            if (this.partitionList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.partitionList = list;
            return this;
        }


    }//MySQLComplexValuesClause


    static abstract class MySQLValueSyntaxStatement<I extends Statement.DmlInsert> extends ValueSyntaxInsertStatement<I>
            implements MySQLReplace, _MySQLInsert, InsertStatement {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;


        private MySQLValueSyntaxStatement(MySQLComplexValuesClause<?, ?> clause) {
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
        public final List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String rowAlias() {
            return null;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return Collections.emptyList();
        }

        @Override
        public final boolean hasConflictAction() {
            // replace always true
            return true;
        }

        @Override
       final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLValueSyntaxStatement


    static abstract class DomainReplaceStatement<I extends Statement.DmlInsert> extends MySQLValueSyntaxStatement<I>
            implements _MySQLInsert._MySQLDomainInsert {

        private DomainReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);

        }


    }//DomainReplaceStatement

    private static final class PrimarySimpleDomainReplaceStatement
            extends DomainReplaceStatement<InsertStatement> {

        private final List<?> domainList;

        private PrimarySimpleDomainReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
            this.domainList = clause.domainListForSingle();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//PrimarySimpleDomainReplaceStatement


    private static final class PrimaryChildDomainReplaceStatement extends DomainReplaceStatement<InsertStatement>
            implements _MySQLInsert._MySQLChildDomainInsert {

        private final PrimaryParentDomainReplaceStatement<?> parentStatement;

        private PrimaryChildDomainReplaceStatement(PrimaryParentDomainReplaceStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public List<?> domainList() {
            return this.parentStatement.domainList;
        }

        @Override
        public _MySQLDomainInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildDomainReplaceStatement


    private static final class PrimaryParentDomainReplaceStatement<P>
            extends DomainReplaceStatement<MySQLReplace._ParentReplace<P>>
            implements MySQLReplace._ParentReplace<P> {

        private final List<?> originalDomainList;

        private final List<?> domainList;

        private PrimaryParentDomainReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _CollectionUtils.unmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildReplaceIntoSpec<P> child() {
            this.prepared();
            return new ChildReplaceIntoClause<>(this, this::childReplaceEnd);
        }

        private InsertStatement childReplaceEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            return new PrimaryChildDomainReplaceStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentDomainReplaceStatement


    static abstract class ValueReplaceStatement<I extends Statement.DmlInsert> extends MySQLValueSyntaxStatement<I>
            implements _MySQLInsert._MySQLValueInsert {

        final List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private ValueReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.valuePairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.valuePairList;
        }


    }//ValueReplaceStatement


    private static final class PrimarySimpleValueReplaceStatement extends ValueReplaceStatement<InsertStatement> {

        private PrimarySimpleValueReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }

    }//PrimarySimpleValueReplaceStatement


    private static final class PrimaryChildValueReplaceStatement extends ValueReplaceStatement<InsertStatement>
            implements _MySQLInsert._MySQLChildValueInsert {

        private final PrimaryParentValueReplaceStatement<?> parentStatement;

        private PrimaryChildValueReplaceStatement(PrimaryParentValueReplaceStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLValueInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildValueReplaceStatement


    private static final class PrimaryParentValueReplaceStatement<P>
            extends ValueReplaceStatement<MySQLReplace._ParentReplace<P>>
            implements MySQLReplace._ParentReplace<P> {

        private PrimaryParentValueReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public _ChildReplaceIntoSpec<P> child() {
            this.prepared();
            return new ChildReplaceIntoClause<>(this, this::childReplaceEnd);
        }

        private InsertStatement childReplaceEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            if (childClause.rowPairList().size() != this.valuePairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueReplaceStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentValueReplaceStatement


    static abstract class AssignmentReplaceStatement<I extends Statement.DmlInsert>
            extends InsertSupport.AssignmentInsertStatement<I>
            implements MySQLReplace, _MySQLInsert._MySQLAssignmentInsert, InsertStatement {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private AssignmentReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
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
        public final List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String rowAlias() {
            return null;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return Collections.emptyList();
        }

        @Override
        public final boolean hasConflictAction() {
            // replace always true
            return true;
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }

    }//PrimaryAssignmentReplaceStatement


    private static final class PrimarySimpleAssignmentReplaceStatement
            extends AssignmentReplaceStatement<InsertStatement> {

        private PrimarySimpleAssignmentReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }

    }//PrimarySimpleAssignmentReplaceStatement

    private static final class PrimaryChildAssignmentReplaceStatement extends AssignmentReplaceStatement<InsertStatement>
            implements _MySQLInsert._MySQLChildAssignmentInsert {

        private final PrimaryParentAssignmentReplaceStatement<?> parentStatement;

        private PrimaryChildAssignmentReplaceStatement(PrimaryParentAssignmentReplaceStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLAssignmentInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildAssignmentReplaceStatement

    private static final class PrimaryParentAssignmentReplaceStatement<P>
            extends AssignmentReplaceStatement<MySQLReplace._ParentReplace<P>>
            implements MySQLReplace._ParentReplace<P> {

        private PrimaryParentAssignmentReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }


        @Override
        public _ChildReplaceIntoSpec<P> child() {
            this.prepared();
            return new ChildReplaceIntoClause<>(this, this::childReplaceEnd);
        }

        private InsertStatement childReplaceEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildAssignmentReplaceStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentAssignmentReplaceStatement


    static abstract class QueryReplaceStatement<I extends Statement.DmlInsert>
            extends InsertSupport.QuerySyntaxInsertStatement<I>
            implements MySQLReplace, _MySQLInsert._MySQLQueryInsert, InsertStatement {


        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;


        private QueryReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
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
        public final List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String rowAlias() {
            return null;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return Collections.emptyList();
        }

        @Override
        public final boolean hasConflictAction() {
            //replace always true
            return true;
        }

        @Override
       final   Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//PrimaryQueryReplaceStatement

    private static final class PrimarySimpleQueryReplaceStatement extends QueryReplaceStatement<InsertStatement> {

        private PrimarySimpleQueryReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }


    }//PrimarySimpleQueryReplaceStatement


    private static final class PrimaryChildQueryReplaceStatement extends QueryReplaceStatement<InsertStatement>
            implements _MySQLInsert._MySQLChildQueryInsert {

        private final PrimaryParentQueryReplaceStatement<?> parentStatement;

        private PrimaryChildQueryReplaceStatement(PrimaryParentQueryReplaceStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLQueryInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildQueryReplaceStatement


    private static final class PrimaryParentQueryReplaceStatement<P>
            extends QueryReplaceStatement<MySQLReplace._ParentReplace<P>>
            implements MySQLReplace._ParentReplace<P> {

        private PrimaryParentQueryReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public _ChildReplaceIntoSpec<P> child() {
            this.prepared();
            return new ChildReplaceIntoClause<>(this, this::childReplaceEnd);
        }

        private InsertStatement childReplaceEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildQueryReplaceStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentQueryReplaceStatement


}
