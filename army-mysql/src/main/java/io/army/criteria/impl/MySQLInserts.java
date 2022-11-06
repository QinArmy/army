package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    static MySQLInsert._PrimaryOptionSpec primaryInsert() {
        return new PrimaryInsertIntoClause();
    }

    static <I extends Item> MySQLInsert._PrimaryIntoSingleClause<I> insertSingle(Function<Insert, I> function) {
        return new PrimaryInsertIntoSingleClause<>(function);
    }


    private static Insert createSimpleInsert(final MySQLComplexValuesClause<?, ?> clause) {
        final InsertMode mode;
        mode = clause.getInsertMode();
        final Statement._DmlInsertClause<Insert> spec;
        switch (mode) {
            case DOMAIN:
                spec = new PrimarySimpleDomainInsertStatement(clause);
                break;
            case VALUES:
                spec = new PrimarySimpleValueInsertStatement(clause);
                break;
            case ASSIGNMENT:
                spec = new PrimarySimpleAssignmentInsertStatement(clause);
                break;
            case QUERY:
                spec = new PrimarySimpleQueryInsertStatement(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }

    private static <P> Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> createParentInsert(
            final MySQLComplexValuesClause<?, ?> clause) {
        final InsertMode mode;
        mode = clause.getInsertMode();
        final Statement._DmlInsertClause<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>> spec;
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryParentDomainInsertStatement<>(clause);
                break;
            case VALUES:
                spec = new PrimaryParentValueInsertStatement<>(clause);
                break;
            case ASSIGNMENT:
                spec = new PrimaryParentAssignmentInsertStatement<>(clause);
                break;
            case QUERY:
                spec = new PrimaryParentQueryInsertStatement<>(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }

    private static final class PrimaryInsertIntoSingleClause<I extends Item>
            extends InsertSupport.NonQueryInsertOptionsImpl<
            MySQLInsert._PrimaryNullOptionSingleSpec<I>,
            MySQLInsert._PrimaryPreferLiteralSingleSpec<I>,
            MySQLInsert._PrimaryInsertIntoSingleSpec<I>>
            implements MySQLInsert._PrimaryOptionSingleSpec<I>
            , MySQLInsert._PrimaryIntoSingleClause<I> {

        private final Function<Insert, I> function;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private PrimaryInsertIntoSingleClause(Function<Insert, I> function) {
            super(CriteriaContexts.primaryInsertContext());
            ContextStack.push(this.context);
            this.function = function;
        }

        @Override
        public MySQLInsert._PrimaryIntoSingleClause<I> insert(Supplier<List<Hint>> supplier
                , List<MySQLSyntax.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<I, T> into(SingleTableMeta<T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<I, T> insertInto(SingleTableMeta<T> table) {
            final Function<MySQLComplexValuesClause<?, ?>, I> composeFunction;
            if (table instanceof ParentTableMeta) {
                composeFunction = this.function.compose(MySQLInserts::createParentInsert);
            } else {
                composeFunction = this.function.compose(MySQLInserts::createSimpleInsert);
            }
            return new MySQLComplexValuesClause<>(this, table, composeFunction);
        }

    }//PrimaryInsertIntoMultiClause


    private static final class PrimaryInsertIntoClause extends InsertSupport.NonQueryInsertOptionsImpl<
            MySQLInsert._PrimaryNullOptionSpec,
            MySQLInsert._PrimaryPreferLiteralSpec,
            MySQLInsert._PrimaryInsertIntoSpec>
            implements MySQLInsert._PrimaryOptionSpec
            , MySQLInsert._PrimaryIntoClause {

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private PrimaryInsertIntoClause() {
            super(CriteriaContexts.primaryInsertContext());
            ContextStack.push(this.context);
        }


        @Override
        public MySQLInsert._PrimaryIntoClause insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> into(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLInserts::createSimpleInsert);
        }


        @Override
        public <P> MySQLInsert._PartitionSpec<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>, P> into(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLInserts::createParentInsert);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> insertInto(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLInserts::createSimpleInsert);
        }

        @Override
        public <P> MySQLInsert._PartitionSpec<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>, P> insertInto(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLInserts::createParentInsert);
        }



    }//PrimaryInsertIntoClause


    private static final class ChildInsertIntoClause<P> extends ChildOptionClause
            implements MySQLInsert._ChildInsertIntoSpec<P>, MySQLInsert._ChildIntoClause<P> {

        private final Function<MySQLComplexValuesClause<?, ?>, Insert> dmlFunction;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private ChildInsertIntoClause(ValueSyntaxOptions options
                , Function<MySQLComplexValuesClause<?, ?>, Insert> dmlFunction) {
            super(options, CriteriaContexts.primaryInsertContext());
            this.dmlFunction = dmlFunction;
            ContextStack.push(this.context);
        }


        @Override
        public MySQLInsert._ChildIntoClause<P> insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> insertInto(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> into(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }


    }//ChildInsertIntoClause


    private static final class StaticOnDuplicateKeyClause<I extends Item, F extends TableField>
            extends SetWhereClause.SetWhereClauseClause<
            F,
            MySQLInsert._StaticOnDuplicateKeySetSpec<I, F>,
            Object,
            Object> implements MySQLInsert._StaticOnDuplicateKeySetSpec<I, F> {

        private final MySQLComplexValuesClause<I, ?> clause;

        private StaticOnDuplicateKeyClause(MySQLComplexValuesClause<I, ?> clause) {
            super(clause.context, clause.insertTable, "");
            this.clause = clause;
        }

        private StaticOnDuplicateKeyClause(MySQLComplexValuesClause<I, ?> clause, String rowAlias) {
            super(clause.context, clause.insertTable, rowAlias);
            this.clause = clause;
        }


        @Override
        public I asInsert() {
            return this.clause.onDuplicateKeyClauseEnd(this.endUpdateSetClause())
                    .asInsert();
        }



    }//StaticOnDuplicateKeyClause

    private static final class OnDuplicateKeyUpdateClause<I extends Item, F extends TableField>
            implements MySQLInsert._OnDuplicateKeyUpdateSpec<I, F> {


        private final MySQLComplexValuesClause<I, ?> valuesClause;

        private OnDuplicateKeyUpdateClause(MySQLComplexValuesClause<I, ?> valuesClause) {
            this.valuesClause = valuesClause;
        }


        @Override
        public MySQLInsert._StaticOnDuplicateKeySetClause<I, F> onDuplicateKey() {
            final String rowAlias = this.valuesClause.rowAlias;
            assert rowAlias != null;
            return new StaticOnDuplicateKeyClause<>(this.valuesClause, rowAlias);
        }

        @Override
        public Statement._DmlInsertClause<I> onDuplicateKey(Consumer<ItemPairs<F>> consumer) {
            final MySQLComplexValuesClause<I, ?> valuesClause = this.valuesClause;
            if (valuesClause.conflictPairList != null) {
                throw ContextStack.castCriteriaApi(valuesClause.context);
            }

            final List<_ItemPair> list = new ArrayList<>();
            consumer.accept(CriteriaSupports.simpleFieldItemPairs(valuesClause.context, valuesClause.insertTable
                    , list::add));
            if (list.size() == 0) {
                throw CriteriaUtils.conflictClauseIsEmpty(this.valuesClause.context);
            }
            valuesClause.conflictPairList = _CollectionUtils.unmodifiableList(list);
            return valuesClause;
        }

        @Override
        public Statement._DmlInsertClause<I> ifOnDuplicateKey(Consumer<ItemPairs<F>> consumer) {
            final MySQLComplexValuesClause<I, ?> valuesClause = this.valuesClause;
            if (valuesClause.conflictPairList != null) {
                throw ContextStack.castCriteriaApi(valuesClause.context);
            }
            final List<_ItemPair> list = new ArrayList<>();
            consumer.accept(CriteriaSupports.simpleFieldItemPairs(valuesClause.context, valuesClause.insertTable
                    , list::add));
            if (list.size() > 0) {
                valuesClause.conflictPairList = _CollectionUtils.unmodifiableList(list);
            }
            return valuesClause;
        }

        @Override
        public I asInsert() {
            return this.valuesClause.asInsert();
        }


    }//OnDuplicateKeyUpdateClause

    private static final class MySQLStaticValuesClause<I extends Item, T>
            extends InsertSupport.StaticColumnValuePairClause<T, MySQLInsert._StaticValuesLeftParenSpec<I, T>>
            implements MySQLInsert._StaticValuesLeftParenSpec<I, T> {

        private final MySQLComplexValuesClause<I, T> valuesClause;

        private MySQLStaticValuesClause(MySQLComplexValuesClause<I, T> valuesClause) {
            super(valuesClause.context, valuesClause::validateField);
            this.valuesClause = valuesClause;
        }

        @Override
        public I asInsert() {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .asInsert();
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeySetClause<I, FieldMeta<T>> onDuplicateKey() {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }

        @Override
        public Statement._DmlInsertClause<I> onDuplicateKey(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .onDuplicateKey(consumer);
        }

        @Override
        public Statement._DmlInsertClause<I> ifOnDuplicateKey(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKey(consumer);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateSpec<I, TypeTableField<T>> as(String rowAlias) {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .as(rowAlias);
        }


    }//MySQLStaticValuesClause


    private static final class MySQLComplexValuesClause<I extends Item, T> extends ComplexInsertValuesAssignmentClause<
            T,
            MySQLInsert._ComplexColumnDefaultSpec<I, T>,
            MySQLInsert._ValuesColumnDefaultSpec<I, T>,
            MySQLInsert._OnAsRowAliasSpec<I, T>,
            MySQLInsert._StaticAssignmentSpec<I, T>>
            implements MySQLInsert._PartitionSpec<I, T>
            , MySQLInsert._ComplexColumnDefaultSpec<I, T>
            , MySQLInsert._OnAsRowAliasSpec<I, T> {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction;

        private List<String> partitionList;

        private String rowAlias;

        private List<_ItemPair> conflictPairList;

        private MySQLComplexValuesClause(PrimaryInsertIntoClause options, SingleTableMeta<T> table
                , Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _CollectionUtils.safeList(options.hintList);
            this.modifierList = _CollectionUtils.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(ChildInsertIntoClause<?> options, ChildTableMeta<T> table
                , Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _CollectionUtils.safeList(options.hintList);
            this.modifierList = _CollectionUtils.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(PrimaryInsertIntoSingleClause<?> options, SingleTableMeta<T> table
                , Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _CollectionUtils.safeList(options.hintList);
            this.modifierList = _CollectionUtils.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<MySQLInsert._ColumnListSpec<I, T>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }

        @Override
        public MySQLInsert._MySQLStaticValuesLeftParenClause<I, T> values() {
            this.endColumnListClause(InsertMode.VALUES);
            return new MySQLStaticValuesClause<>(this);
        }

        @Override
        public MySQLQuery._WithSpec<MySQLInsert._OnAsRowAliasSpec<I, T>> space() {
            return MySQLQueries.subQuery(null, this.context, this::staticSpaceQueryEnd);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateSpec<I, TypeTableField<T>> as(final String rowAlias) {
            if (!_StringUtils.hasText(rowAlias)) {
                throw ContextStack.criteriaError(this.context, "rowAlias no text");
            }
            this.rowAlias = rowAlias;
            return new OnDuplicateKeyUpdateClause<>(this);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeySetClause<I, FieldMeta<T>> onDuplicateKey() {
            return new StaticOnDuplicateKeyClause<>(this);
        }

        @Override
        public Statement._DmlInsertClause<I> onDuplicateKey(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            if (this.conflictPairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<_ItemPair> list = new ArrayList<>();
            consumer.accept(CriteriaSupports.simpleFieldItemPairs(this.context, this.insertTable, list::add));
            if (list.size() == 0) {
                throw CriteriaUtils.conflictClauseIsEmpty(this.context);
            }
            this.conflictPairList = _CollectionUtils.unmodifiableList(list);
            return this;
        }

        @Override
        public Statement._DmlInsertClause<I> ifOnDuplicateKey(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            if (this.conflictPairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<_ItemPair> list = new ArrayList<>();
            consumer.accept(CriteriaSupports.simpleFieldItemPairs(this.context, this.insertTable, list::add));

            if (list.size() > 0) {
                this.conflictPairList = _CollectionUtils.unmodifiableList(list);
            }
            return this;
        }

        @Override
        public I asInsert() {
            this.endStaticAssignmentClauseIfNeed();
            if (this.getInsertMode() == InsertMode.QUERY && this.modifierList.contains(MySQLs.DELAYED)) {
                String m = String.format("MySQL query insert don't support modifier[%s].", MySQLs.DELAYED);
                throw ContextStack.criteriaError(this.context, m);
            }
            return this.dmlFunction.apply(this);
        }

        private Statement._DmlInsertClause<I> onDuplicateKeyClauseEnd(final List<_ItemPair> itemPairList) {
            if (this.conflictPairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.conflictPairList = itemPairList;
            return this;
        }


        private MySQLInsert._ColumnListSpec<I, T> partitionEnd(final List<String> list) {
            if (this.partitionList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.partitionList = list;
            return this;
        }


    }//MySQLComplexValuesClause


    static abstract class MySQLValueSyntaxStatement<I extends Statement.DmlInsert>
            extends ValueSyntaxInsertStatement<I>
            implements MySQLInsert, _MySQLInsert,Insert {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private final String rowAlias;

        private final List<_ItemPair> conflictPairList;

        private MySQLValueSyntaxStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _CollectionUtils.safeList(clause.conflictPairList);
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
            return this.rowAlias;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return this.conflictPairList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictPairList.size() > 0;
        }

        @Override
       final   Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLValueSyntaxStatement


    static abstract class DomainInsertStatement<I extends Statement.DmlInsert> extends MySQLValueSyntaxStatement<I>
            implements _MySQLInsert._MySQLDomainInsert {

        private DomainInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);

        }


    }//DomainInsertStatement

    private static final class PrimarySimpleDomainInsertStatement
            extends DomainInsertStatement<Insert>
            implements Insert {

        private final List<?> domainList;

        private PrimarySimpleDomainInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
            this.domainList = clause.domainListForNonParent();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//PrimarySimpleDomainInsertStatement


    private static final class PrimaryChildDomainInsertStatement extends DomainInsertStatement<Insert>
            implements _MySQLInsert._MySQLChildDomainInsert {

        private final PrimaryParentDomainInsertStatement<?> parentStatement;

        private PrimaryChildDomainInsertStatement(PrimaryParentDomainInsertStatement<?> parentStatement
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


    }//PrimaryChildDomainInsertStatement


    private static final class PrimaryParentDomainInsertStatement<P>
            extends DomainInsertStatement<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> {

        private final List<?> originalDomainList;

        private final List<?> domainList;

        private PrimaryParentDomainInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
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
        public _ChildInsertIntoSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentDomainInsertStatement


    static abstract class ValueInsertStatement<I extends Statement.DmlInsert> extends MySQLValueSyntaxStatement<I>
            implements _MySQLInsert._MySQLValueInsert {

        final List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private ValueInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.valuePairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.valuePairList;
        }


    }//ValuesStatement


    private static final class PrimarySimpleValueInsertStatement extends ValueInsertStatement<Insert>
            implements Insert {

        private PrimarySimpleValueInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }

    }//PrimarySimpleValueStatement


    private static final class PrimaryChildValueStatement extends ValueInsertStatement<Insert>
            implements _MySQLInsert._MySQLChildValueInsert, Insert {

        private final PrimaryParentValueInsertStatement<?> parentStatement;

        private PrimaryChildValueStatement(PrimaryParentValueInsertStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLValueInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimarySimpleValueStatement


    private static final class PrimaryParentValueInsertStatement<P>
            extends ValueInsertStatement<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> {

        private PrimaryParentValueInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public _ChildInsertIntoSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            if (childClause.rowPairList().size() != this.valuePairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueStatement(this, childClause)
                    .asInsert();
        }


    }//PrimarySimpleValueStatement


    static abstract class PrimaryAssignmentStatement<I extends Statement.DmlInsert>
            extends InsertSupport.AssignmentInsertStatement<I>
            implements MySQLInsert, _MySQLInsert._MySQLAssignmentInsert ,Insert{

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private final String rowAlias;

        private final List<_ItemPair> conflictPairList;

        private PrimaryAssignmentStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _CollectionUtils.safeList(clause.conflictPairList);
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
            return this.rowAlias;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return this.conflictPairList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictPairList.size() > 0;
        }

        @Override
       final   Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//PrimaryAssignmentStatement


    private static final class PrimarySimpleAssignmentInsertStatement extends PrimaryAssignmentStatement<Insert>
            implements Insert {

        private PrimarySimpleAssignmentInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }

    }//PrimarySimpleAssignmentStatement

    private static final class PrimaryChildAssignmentStatement extends PrimaryAssignmentStatement<Insert>
            implements _MySQLInsert._MySQLChildAssignmentInsert, Insert {

        private final PrimaryParentAssignmentInsertStatement<?> parentStatement;

        private PrimaryChildAssignmentStatement(PrimaryParentAssignmentInsertStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLAssignmentInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildAssignmentStatement

    private static final class PrimaryParentAssignmentInsertStatement<P>
            extends PrimaryAssignmentStatement<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> {

        private PrimaryParentAssignmentInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public MySQLInsert._ChildInsertIntoSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildAssignmentStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentAssignmentStatement


    static abstract class PrimaryQueryInsertStatement<I extends Statement.DmlInsert>
            extends InsertSupport.QuerySyntaxInsertStatement<I>
            implements MySQLInsert, _MySQLInsert._MySQLQueryInsert {


        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private final String rowAlias;

        private final List<_ItemPair> conflictPairList;

        private PrimaryQueryInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _CollectionUtils.safeList(clause.conflictPairList);
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
            return this.rowAlias;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return this.conflictPairList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictPairList.size() > 0;
        }

        @Override
        final   Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//PrimaryQueryInsertStatement

    private static final class PrimarySimpleQueryInsertStatement extends PrimaryQueryInsertStatement<Insert>
            implements Insert {

        private PrimarySimpleQueryInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }


    }//PrimarySimpleQueryStatement


    private static final class PrimaryChildQueryInsertStatement extends PrimaryQueryInsertStatement<Insert>
            implements Insert, _MySQLInsert._MySQLChildQueryInsert {

        private final PrimaryParentQueryInsertStatement<?> parentStatement;

        private PrimaryChildQueryInsertStatement(PrimaryParentQueryInsertStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLQueryInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildQueryStatement


    private static final class PrimaryParentQueryInsertStatement<P>
            extends PrimaryQueryInsertStatement<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> {

        private PrimaryParentQueryInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public _ChildInsertIntoSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimarySimpleQueryStatement


}
