package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLReplace;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.*;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementations of {@link MySQLReplace}.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class MySQLReplaces extends InsertSupports {

    private MySQLReplaces() {
        throw new UnsupportedOperationException();
    }


    /**
     * <p>
     * create single-table REPLACE statement that is primary statement and support {@link io.army.meta.ChildTableMeta}.
     * </p>
     */
    static MySQLReplace._PrimaryOptionSpec singleReplace() {
        return new PrimaryReplaceIntoClause();
    }


    /**
     * <p>
     * create single-table REPLACE statement that is primary statement for multi-statement and support only {@link SingleTableMeta}.
     * </p>
     */
    static <I extends Item> MySQLReplace._PrimarySingleOptionSpec<I> singleReplace(ArmyStmtSpec spec,
                                                                                   Function<? super Insert, I> function) {
        return new PrimarySingleReplaceIntoClause<>(spec, function);
    }



    /*-------------------below private methods -------------------*/

    private static Insert singleReplaceEnd(MySQLComplexValuesClause<?, ?> clause) {
        final InsertMode mode;
        mode = clause.getInsertMode();
        final Statement._DmlInsertClause<Insert> spec;
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

    private static <P> MySQLReplace._ParentReplace<P> parentReplaceEnd(MySQLComplexValuesClause<?, ?> clause) {
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


    private static final class PrimaryReplaceIntoClause extends InsertSupports.NonQueryInsertOptionsImpl<
            MySQLReplace._PrimaryNullOptionSpec>
            implements MySQLReplace._PrimaryOptionSpec,
            MySQLReplace._PrimaryIntoClause {


        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private PrimaryReplaceIntoClause() {
            super(CriteriaContexts.primaryInsertContext(MySQLUtils.DIALECT, null));
            ContextStack.push(this.context);
        }

        @Override
        public MySQLReplace._PrimaryIntoClause replace(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<Insert, T> into(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLReplaces::singleReplaceEnd);
        }

        @Override
        public <P> MySQLReplace._PartitionSpec<MySQLReplace._ParentReplace<P>, P> into(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLReplaces::parentReplaceEnd);
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<Insert, T> replaceInto(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLReplaces::singleReplaceEnd);
        }

        @Override
        public <P> MySQLReplace._PartitionSpec<MySQLReplace._ParentReplace<P>, P> replaceInto(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLReplaces::parentReplaceEnd);
        }


    }//PrimaryReplaceIntoClause


    private static final class ChildReplaceIntoClause<P> extends ChildOptionClause
            implements MySQLReplace._ChildReplaceIntoSpec<P>
            , MySQLReplace._ChildIntoClause<P> {

        private final Function<MySQLComplexValuesClause<?, ?>, Insert> dmlFunction;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private ChildReplaceIntoClause(ValueSyntaxOptions options
                , Function<MySQLComplexValuesClause<?, ?>, Insert> dmlFunction) {
            super(options, CriteriaContexts.primaryInsertContext(MySQLUtils.DIALECT, null));
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
        public <T> MySQLReplace._PartitionSpec<Insert, T> into(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<Insert, T> replaceInto(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }


    }//ChildReplaceIntoClause


    private static final class PrimarySingleReplaceIntoClause<I extends Item>
            extends InsertSupports.NonQueryInsertOptionsImpl<
            MySQLReplace._PrimarySingleNullOptionSpec<I>>
            implements MySQLReplace._PrimarySingleOptionSpec<I>,
            MySQLReplace._PrimarySingleIntoClause<I> {

        private final Function<? super Insert, I> function;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private PrimarySingleReplaceIntoClause(ArmyStmtSpec spec, Function<? super Insert, I> function) {
            super(CriteriaContexts.primaryInsertContext(MySQLUtils.DIALECT, spec));
            this.function = function;
            ContextStack.push(this.context);
        }

        @Override
        public MySQLReplace._PrimarySingleIntoClause<I> replace(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::replaceModifier);
            return this;
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<I, T> into(SingleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.function.compose(MySQLReplaces::singleReplaceEnd));
        }

        @Override
        public <T> MySQLReplace._PartitionSpec<I, T> replaceInto(SingleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.function.compose(MySQLReplaces::singleReplaceEnd));
        }

    }//PrimarySingleReplaceIntoClause


    private static final class MySQLStaticValuesClause<I extends Item, T>
            extends InsertSupports.StaticColumnValuePairClause<
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
            implements MySQLReplace._PartitionSpec<I, T>,
            MySQLReplace._ComplexColumnDefaultSpec<I, T>,
            Statement._DmlInsertClause<I> {

        private final Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction;

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private List<String> partitionList;

        private MySQLComplexValuesClause(PrimaryReplaceIntoClause options, SingleTableMeta<T> table,
                                         Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _Collections.safeList(options.hintList);
            this.modifierList = _Collections.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(ChildReplaceIntoClause<?> options, ComplexTableMeta<?, T> table,
                                         Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _Collections.safeList(options.hintList);
            this.modifierList = _Collections.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(PrimarySingleReplaceIntoClause<?> options, SingleTableMeta<T> table,
                                         Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _Collections.safeList(options.hintList);
            this.modifierList = _Collections.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        @Override
        public MySQLReplace._ColumnListSpec<I, T> partition(String first, String... rest) {
            this.partitionList = ArrayUtils.unmodifiableListOf(first, rest);
            return this;
        }

        @Override
        public MySQLReplace._ColumnListSpec<I, T> partition(Consumer<Consumer<String>> consumer) {
            this.partitionList = CriteriaUtils.stringList(this.context, true, consumer);
            return this;
        }

        @Override
        public MySQLReplace._ColumnListSpec<I, T> ifPartition(Consumer<Consumer<String>> consumer) {
            this.partitionList = CriteriaUtils.stringList(this.context, false, consumer);
            return this;
        }

        @Override
        public MySQLReplace._MySQLStaticValuesLeftParenClause<I, T> values() {
            return new MySQLStaticValuesClause<>(this);
        }

        @Override
        public MySQLQuery._WithSpec<Statement._DmlInsertClause<I>> space() {
            return MySQLQueries.subQuery(this.context, this::spaceQueryEnd);
        }

        @Override
        public Statement._DmlInsertClause<I> space(Supplier<SubQuery> supplier) {
            return this.spaceQueryEnd(supplier.get());
        }

        @Override
        public Statement._DmlInsertClause<I> space(Function<MySQLQuery._WithSpec<Statement._DmlInsertClause<I>>, Statement._DmlInsertClause<I>> function) {
            return function.apply(MySQLQueries.subQuery(this.context, this::spaceQueryEnd));
        }


        @Override
        public I asInsert() {
            this.endStaticAssignmentClauseIfNeed();
            return this.dmlFunction.apply(this);
        }


        @Override
        public String tableAlias() {
            // MySQL don't support
            return null;
        }


    }//MySQLComplexValuesClause


    static abstract class MySQLValueSyntaxStatement<I extends Statement> extends ValueSyntaxInsertStatement<I>
            implements MySQLReplace, _MySQLInsert, Insert {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;


        private MySQLValueSyntaxStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _Collections.safeList(clause.partitionList);
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
        public final boolean isIgnorableConflict() {
            //false ,MySQL REPLACE don't support do nothing clause
            return false;
        }

        @Override
        public final boolean isDoNothing() {
            //false,MySQL don't support
            return false;
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLValueSyntaxStatement


    static abstract class DomainReplaceStatement<I extends Statement> extends MySQLValueSyntaxStatement<I>
            implements _MySQLInsert._MySQLDomainInsert {

        private DomainReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);

        }


    }//DomainReplaceStatement

    private static final class PrimarySimpleDomainReplaceStatement
            extends DomainReplaceStatement<Insert> {

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


    private static final class PrimaryChildDomainReplaceStatement extends DomainReplaceStatement<Insert>
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
            this.domainList = _Collections.unmodifiableList(this.originalDomainList);
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

        private Insert childReplaceEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            return new PrimaryChildDomainReplaceStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentDomainReplaceStatement


    static abstract class ValueReplaceStatement<I extends Insert> extends MySQLValueSyntaxStatement<I>
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


    private static final class PrimarySimpleValueReplaceStatement extends ValueReplaceStatement<Insert> {

        private PrimarySimpleValueReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }

    }//PrimarySimpleValueReplaceStatement


    private static final class PrimaryChildValueReplaceStatement extends ValueReplaceStatement<Insert>
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

        private Insert childReplaceEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            if (childClause.rowPairList().size() != this.valuePairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueReplaceStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentValueReplaceStatement


    static abstract class AssignmentReplaceStatement<I extends Insert>
            extends InsertSupports.AssignmentInsertStatement<I>
            implements MySQLReplace, _MySQLInsert._MySQLAssignmentInsert, InsertStatement {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private AssignmentReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _Collections.safeList(clause.partitionList);
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
        public final boolean isIgnorableConflict() {
            //false ,MySQL REPLACE always replace when conflict
            return false;
        }

        @Override
        public final boolean isDoNothing() {
            //false,MySQL don't support
            return false;
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }

    }//PrimaryAssignmentReplaceStatement


    private static final class PrimarySimpleAssignmentReplaceStatement
            extends AssignmentReplaceStatement<Insert> {

        private PrimarySimpleAssignmentReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }

    }//PrimarySimpleAssignmentReplaceStatement

    private static final class PrimaryChildAssignmentReplaceStatement extends AssignmentReplaceStatement<Insert>
            implements _MySQLInsert._MySQLChildAssignmentInsert {

        private final PrimaryParentAssignmentReplaceStatement<?> parentStatement;

        private PrimaryChildAssignmentReplaceStatement(PrimaryParentAssignmentReplaceStatement<?> parentStatement,
                                                       MySQLComplexValuesClause<?, ?> childClause) {
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

        private Insert childReplaceEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildAssignmentReplaceStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentAssignmentReplaceStatement


    static abstract class QueryReplaceStatement<I extends Insert>
            extends InsertSupports.QuerySyntaxInsertStatement<I>
            implements MySQLReplace, _MySQLInsert._MySQLQueryInsert, InsertStatement {


        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;


        private QueryReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _Collections.safeList(clause.partitionList);
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
        public final boolean isIgnorableConflict() {
            //false ,MySQL REPLACE always replace when conflict
            return false;
        }

        @Override
        public final boolean isDoNothing() {
            //false,MySQL don't support
            return false;
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//PrimaryQueryReplaceStatement

    private static final class PrimarySimpleQueryReplaceStatement extends QueryReplaceStatement<Insert> {

        private PrimarySimpleQueryReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }


    }//PrimarySimpleQueryReplaceStatement


    private static final class PrimaryChildQueryReplaceStatement extends QueryReplaceStatement<Insert>
            implements _MySQLInsert._MySQLChildQueryInsert {

        private final PrimaryParentQueryReplaceStatement<?> parentStatement;

        private PrimaryChildQueryReplaceStatement(PrimaryParentQueryReplaceStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLParentQueryInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildQueryReplaceStatement


    private static final class PrimaryParentQueryReplaceStatement<P>
            extends QueryReplaceStatement<MySQLReplace._ParentReplace<P>>
            implements MySQLReplace._ParentReplace<P>,
            ParentQueryInsert,
            _MySQLInsert._MySQLParentQueryInsert {

        private CodeEnum discriminatorValue;

        private PrimaryParentQueryReplaceStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public void onValidateEnd(final CodeEnum discriminatorValue) {
            assert this.discriminatorValue == null;
            this.discriminatorValue = discriminatorValue;
        }

        @Override
        public CodeEnum discriminatorEnum() {
            final CodeEnum value = this.discriminatorValue;
            assert value != null;
            return value;
        }

        @Override
        public _ChildReplaceIntoSpec<P> child() {
            this.prepared();
            return new ChildReplaceIntoClause<>(this, this::childReplaceEnd);
        }

        private Insert childReplaceEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildQueryReplaceStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentQueryReplaceStatement


}
