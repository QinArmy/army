package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLCteBuilder;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
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


    private static final class PrimaryComma implements MySQLInsert._PrimaryCteComma {

        private final boolean recursive;

        private final PrimaryInsertIntoClause primaryClause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLInsert._PrimaryCteComma>> function;

        private PrimaryComma(boolean recursive, PrimaryInsertIntoClause primaryClause) {
            this.recursive = recursive;
            this.primaryClause = primaryClause;
            this.function = MySQLQueries.complexCte(primaryClause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<MySQLInsert._PrimaryCteComma> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public MySQLInsert._PrimaryIntoClause insert(Supplier<List<Hint>> supplier, List<MySQLSyntax.Modifier> modifiers) {
            return this.staticWithClauseEnd().insert(supplier, modifiers);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> insertInto(SimpleTableMeta<T> table) {
            return this.staticWithClauseEnd().insertInto(table);
        }

        @Override
        public <P> MySQLInsert._PartitionSpec<Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>>, P> insertInto(ParentTableMeta<P> table) {
            return this.staticWithClauseEnd().insertInto(table);
        }

        private PrimaryInsertIntoClause staticWithClauseEnd() {
            final PrimaryInsertIntoClause primaryClause = this.primaryClause;
            primaryClause.endStaticWithClause(this.recursive);
            return primaryClause;
        }

    }//PrimaryComma

    private static final class PrimaryInsertIntoClause extends InsertSupport.NonQueryWithCteOption<
            MySQLInsert._PrimaryNullOptionSpec,
            MySQLInsert._PrimaryPreferLiteralSpec,
            MySQLInsert._PrimaryWithCteSpec,
            MySQLCteBuilder,
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
        public MySQLQuery._StaticCteLeftParenSpec<MySQLInsert._PrimaryCteComma> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new PrimaryComma(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<MySQLInsert._PrimaryCteComma> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new PrimaryComma(recursive, this).function.apply(name);
        }

        @Override
        public MySQLInsert._PrimaryIntoClause insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> into(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this::simpleInsertEnd);
        }

        @Override
        public <P> MySQLInsert._PartitionSpec<Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>>, P> into(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, this::parentInsertEnd);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> insertInto(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this::simpleInsertEnd);
        }

        @Override
        public <P> MySQLInsert._PartitionSpec<Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>>, P> insertInto(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, this::parentInsertEnd);
        }


        @Override
        MySQLCteBuilder createCteBuilder(boolean recursive) {
            return MySQLSupports.mySQLCteBuilder(recursive, this.context);
        }


        private Insert simpleInsertEnd(final MySQLComplexValuesClause<?, ?> clause) {
            final InsertMode mode;
            mode = clause.getInsertMode();
            final Statement._DmlInsertSpec<Insert> spec;
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

        private <P> Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>> parentInsertEnd(
                final MySQLComplexValuesClause<?, ?> clause) {
            final InsertMode mode;
            mode = clause.getInsertMode();
            final Statement._DmlInsertSpec<Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>>> spec;
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


    }//PrimaryInsertIntoClause

    private static final class ChildCteComma<P> implements MySQLInsert._ChildCteComma<P> {

        private final boolean recursive;

        private final ChildInsertIntoClause<P> clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLInsert._ChildCteComma<P>>> function;

        private ChildCteComma(boolean recursive, ChildInsertIntoClause<P> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<MySQLInsert._ChildCteComma<P>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public MySQLInsert._ChildIntoClause<P> insert(Supplier<List<Hint>> supplier
                , List<MySQLs.Modifier> modifiers) {
            return this.endStaticWithClause().insert(supplier, modifiers);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> insertInto(ComplexTableMeta<P, T> table) {
            return this.endStaticWithClause().insertInto(table);
        }

        private ChildInsertIntoClause<P> endStaticWithClause() {
            final ChildInsertIntoClause<P> clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
        }


    }//ChildCteComma


    private static final class ChildInsertIntoClause<P> extends ChildDynamicWithClause<
            MySQLCteBuilder,
            MySQLInsert._ChildInsertIntoSpec<P>>
            implements ValueSyntaxOptions
            , MySQLInsert._ChildWithCteSpec<P>
            , MySQLInsert._ChildIntoClause<P> {

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
        public MySQLQuery._StaticCteLeftParenSpec<MySQLInsert._ChildCteComma<P>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new ChildCteComma<>(recursive, this)
                    .function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<MySQLInsert._ChildCteComma<P>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new ChildCteComma<>(recursive, this)
                    .function.apply(name);
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

        @Override
        MySQLCteBuilder createCteBuilder(boolean recursive) {
            return MySQLSupports.mySQLCteBuilder(recursive, this.context);
        }


    }//ChildInsertIntoClause


    private static final class StaticOnDuplicateKeyClause<I extends Item, F extends TableField>
            extends SetWhereClause<
            F,
            ItemPairs<F>,
            MySQLInsert._StaticOnDuplicateKeySetSpec<I, F>,
            Object,
            Object,
            Object,
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

        @Override
        ItemPairs<F> createItemPairBuilder(Consumer<ItemPair> consumer) {
            throw ContextStack.castCriteriaApi(this.context);
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
        public Statement._DmlInsertSpec<I> onDuplicateKey(Consumer<ItemPairs<F>> consumer) {
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
        public Statement._DmlInsertSpec<I> ifOnDuplicateKey(Consumer<ItemPairs<F>> consumer) {
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
        public Statement._DmlInsertSpec<I> onDuplicateKey(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .onDuplicateKey(consumer);
        }

        @Override
        public Statement._DmlInsertSpec<I> ifOnDuplicateKey(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
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

        private MySQLComplexValuesClause(PrimaryInsertIntoClause options, TableMeta<T> table
                , Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _CollectionUtils.safeList(options.hintList);
            this.modifierList = _CollectionUtils.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(ChildInsertIntoClause<?> options, TableMeta<T> table
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
            return new MySQLStaticValuesClause<>(this);
        }

        @Override
        public MySQLQuery._MySQLSelectClause<MySQLInsert._OnDuplicateKeyUpdateSpec<I, FieldMeta<T>>> space() {
            return MySQLQueries.subQuery(this.context, this::staticSpaceQueryEnd);
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
        public Statement._DmlInsertSpec<I> onDuplicateKey(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
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
        public Statement._DmlInsertSpec<I> ifOnDuplicateKey(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
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
            return this.dmlFunction.apply(this);
        }

        private Statement._DmlInsertSpec<I> onDuplicateKeyClauseEnd(final List<_ItemPair> itemPairList) {
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


    static abstract class MySQLValueSyntaxStatement<I extends DmlInsert> extends ValueSyntaxInsertStatement<I>
            implements MySQLInsert, _MySQLInsert {

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
        public final List<? extends SQLWords> modifierList() {
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


    static abstract class DomainInsertStatement<I extends DmlInsert> extends MySQLValueSyntaxStatement<I>
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
            extends DomainInsertStatement<Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>> {

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
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentDomainInsertStatement


    static abstract class ValueInsertStatement<I extends DmlInsert> extends MySQLValueSyntaxStatement<I>
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
            extends ValueInsertStatement<Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>> {

        private PrimaryParentValueInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public _ChildWithCteSpec<P> child() {
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


    static abstract class PrimaryAssignmentStatement<I extends DmlInsert>
            extends InsertSupport.AssignmentInsertStatement<I>
            implements MySQLInsert, _MySQLInsert._MySQLAssignmentInsert {

        private final List<Hint> hintList;

        private final List<? extends SQLWords> modifierList;

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
        public final List<? extends SQLWords> modifierList() {
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
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
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
            extends PrimaryAssignmentStatement<Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>> {

        private PrimaryParentAssignmentInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public MySQLInsert._ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildAssignmentStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentAssignmentStatement


    static abstract class PrimaryQueryInsertStatement<I extends DmlInsert>
            extends InsertSupport.QuerySyntaxInsertStatement<I>
            implements MySQLInsert, _MySQLInsert._MySQLQueryInsert {


        private final List<Hint> hintList;

        private final List<? extends SQLWords> modifierList;

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
        public final List<? extends SQLWords> modifierList() {
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
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
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
            extends PrimaryQueryInsertStatement<Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildWithCteSpec<P>> {

        private PrimaryParentQueryInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimarySimpleQueryStatement


}
