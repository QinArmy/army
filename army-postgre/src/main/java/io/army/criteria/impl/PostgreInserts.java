package io.army.criteria.impl;

import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreCtes;
import io.army.criteria.postgre.PostgreInsert;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect.*;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.struct.CodeEnum;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
abstract class PostgreInserts extends InsertSupports {

    private PostgreInserts() {
    }

    /**
     * <p>
     * create new single-table INSERT statement that is primary statement and support {@link io.army.meta.ChildTableMeta}.
     * </p>
     */
    static PostgreInsert._PrimaryOptionSpec singleInsert() {
        return new PrimaryInsertIntoClause();
    }

    /**
     * <p>
     * create new single-table INSERT statement that is primary statement for multi-statement and don't support {@link io.army.meta.ChildTableMeta}.
     * </p>
     */
    static <I extends Item> PostgreInsert._ComplexOptionSpec<I> fromDispatcher(ArmyStmtSpec spec,
                                                                               Function<PrimaryStatement, I> function) {
        return new ComplexInsertIntoClause<>(spec, function);
    }

    /**
     * <p>
     * create new single-table INSERT statement that is sub insert statement in dynamic with clause.
     * </p>
     */
    static <I extends Item> PostgreInsert._DynamicSubOptionSpec<I> dynamicSubInsert(
            CriteriaContext outContext, Function<SubStatement, I> function) {
        return new DynamicSubInsertIntoClause<>(outContext, function);
    }

    /**
     * <p>
     * create new single-table INSERT statement that is sub insert statement in static with clause.
     * </p>
     */
    static <I extends Item> PostgreInsert._StaticSubOptionSpec<I> staticSubInsert(ArmyStmtSpec spec,
                                                                                  Function<SubStatement, I> function) {
        return new StaticSubInsertIntoClause<>(spec, function);
    }


    /*-------------------below private method -------------------*/

    private static <P> PostgreInsert._ParentInsert<P> parentInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DmlInsertClause<PostgreInsert._ParentInsert<P>> spec;
        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryParentDomainInsertStatement<>(clause);
                break;
            case VALUES:
                spec = new PrimaryParentValueInsertStatement<>(clause);
                break;
            case QUERY:
                spec = new PrimaryParentQueryInsertStatement<>(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }

    private static <P> PostgreInsert._ParentReturnInsert<P> parentReturningEnd(PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DqlInsertClause<PostgreInsert._ParentReturnInsert<P>> spec;
        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryParentDomainReturningInsertStatement<>(clause);
                break;
            case VALUES:
                spec = new PrimaryParentValueReturningInsertStatement<>(clause);
                break;
            case QUERY:
                spec = new PrimaryParentQueryReturningInsertStatement<>(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asReturningInsert();
    }

    private static Insert insertEnd(PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DmlInsertClause<? extends Insert> spec;
        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimarySimpleDomainInsertStatement(clause);
                break;
            case VALUES:
                spec = new PrimaryValueInsertStatement(clause);
                break;
            case QUERY: {
                if (clause.insertTable instanceof ParentTableMeta) {
                    spec = new PrimaryParentQueryInsertStatement<>(clause);
                } else {
                    spec = new PrimaryQueryInsertStatement(clause);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }


    private static ReturningInsert returningInsertEnd(PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DqlInsertClause<? extends ReturningInsert> spec;
        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryDomainReturningInsertStatement(clause);
                break;
            case VALUES:
                spec = new PrimaryValueReturningInsertStatement(clause);
                break;
            case QUERY: {
                if (clause.insertTable instanceof ParentTableMeta) {
                    spec = new PrimaryParentQueryReturningInsertStatement<>(clause);
                } else {
                    spec = new PrimaryQueryReturningInsertStatement(clause);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asReturningInsert();
    }


    private static SubStatement subInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DmlInsertClause<? extends SubStatement> spec;
        final InsertMode mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new SubDomainInsertStatement(clause);
                break;
            case VALUES:
                spec = new SubValueInsertStatement(clause);
                break;
            case QUERY: {
                if (clause.insertTable instanceof ParentTableMeta) {
                    spec = new SubParentQueryInsertStatement(clause);
                } else {
                    spec = new SubNonParentQueryInsertStatement(clause);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }

    private static SubStatement subReturningInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DqlInsertClause<SubStatement> spec;
        final InsertMode mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new SubDomainReturningInsertStatement(clause);
                break;
            case VALUES:
                spec = new SubValueReturningInsertStatement(clause);
                break;
            case QUERY: {
                if (clause.insertTable instanceof ParentTableMeta) {
                    spec = new SubParentQueryReturingInsertStatement(clause);
                } else {
                    spec = new SubNonParentQueryReturningInsertStatement(clause);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asReturningInsert();
    }

    /**
     * <p>
     * Find parent insert sub-statement for childStmt in cteList.
     * </p>
     *
     * @see PrimarySimpleDomainInsertStatement#PrimarySimpleDomainInsertStatement(PostgreComplexValuesClause)
     */
    @Nullable
    private static ParentSubInsert parentSubInsert(final ArmyInsert childStmt, final List<_Cte> cteList,
                                                   final int childIndex) {

        final int cteSize = cteList.size();
        assert childIndex > -1 && childIndex <= cteSize;

        final ChildTableMeta<?> child = (ChildTableMeta<?>) childStmt.table();
        final ParentTableMeta<?> parent = child.parentMeta();
        final InsertMode childMode = childStmt.getInsertMode();
        final boolean childMigration = childStmt.isMigration();

        final boolean needParentRowNumberQuery = !childMigration
                && parent.id().generatorType() == GeneratorType.POST
                && childStmt.insertRowCount() > 1;

        PostgreSupports.PostgreCte cte;
        SubStatement subStatement;
        TableMeta<?> candidateParent;
        boolean parentRowNumberQuery = false;

        for (int i = childIndex - 1; i > -1; i--) {
            cte = (PostgreSupports.PostgreCte) cteList.get(i);
            subStatement = cte.subStatement;

            if (needParentRowNumberQuery && subStatement instanceof SubQuery) {
                continue;
            }

            if (!(subStatement instanceof _Insert
                    && (candidateParent = ((_Insert) subStatement).table()) instanceof ParentTableMeta)) {
                continue;
            } else if (candidateParent != parent
                    || ((ArmyInsert) subStatement).isMigration() != childMigration
                    || ((ArmyInsert) subStatement).getInsertMode() != childMode) {
                continue;
            }

            if (!parentRowNumberQuery) {
                String m = String.format("Not found parent row number query of %s", child);
                throw new CriteriaException(m);
            }

        }
        return null;
    }


    private interface ParentSubInsert {


        void validateChild(ChildTableMeta<?> child);

        void parentAsDomain();

    }

    private interface ParentDomainSubInsert extends ParentSubInsert {

        List<?> validateDomainList(ChildTableMeta<?> child, List<?> originalList);

        List<?> domainList();
    }


    /*-------------------below insert after values syntax class-------------------*/


    private static final class PrimaryInsertIntoClause extends NonQueryWithCteOption<
            PostgreInsert._PrimaryNullOptionSpec,
            PostgreCtes,
            PostgreInsert._PrimaryInsertIntoClause>
            implements PostgreInsert._PrimaryOptionSpec {

        private PrimaryInsertIntoClause() {
            super(CriteriaContexts.primaryInsertContext(PostgreUtils.DIALECT, null));
            ContextStack.push(this.context);
        }


        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._PrimaryInsertIntoClause> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._PrimaryInsertIntoClause> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, false, PostgreInserts::insertEnd, PostgreInserts::returningInsertEnd);
        }

        @Override
        public <P> PostgreInsert._TableAliasSpec<P, PostgreInsert._ParentInsert<P>, PostgreInsert._ParentReturnInsert<P>> insertInto(ParentTableMeta<P> table) {
            return new PostgreComplexValuesClause<>(this, table, false, PostgreInserts::parentInsertEnd, PostgreInserts::parentReturningEnd);
        }


        @Override
        PostgreCtes createCteBuilder(final boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//PrimaryInsertIntoClause


    private static final class ChildInsertIntoClause<P> extends ChildDynamicWithClause<
            PostgreCtes,
            PostgreInsert._ChildInsertIntoClause<P>>
            implements PostgreInsert._ChildWithCteSpec<P> {

        private final Function<PostgreComplexValuesClause<?, ?, ?>, Insert> dmlFunction;

        private final Function<PostgreComplexValuesClause<?, ?, ?>, ReturningInsert> dqlFunction;

        private ChildInsertIntoClause(ValueSyntaxOptions parentOption
                , Function<PostgreComplexValuesClause<?, ?, ?>, Insert> dmlFunction
                , Function<PostgreComplexValuesClause<?, ?, ?>, ReturningInsert> dqlFunction) {
            super(parentOption, CriteriaContexts.primaryInsertContext(PostgreUtils.DIALECT, null));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
            ContextStack.push(this.context);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._ChildInsertIntoClause<P>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._ChildInsertIntoClause<P>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table) {
            return new PostgreComplexValuesClause<>(this, table, true, this.dmlFunction, this.dqlFunction);
        }

        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//ChildInsertIntoClause

    private static final class ComplexInsertIntoClause<I extends Item> extends NonQueryWithCteOption<
            PostgreInsert._ComplexNullOptionSpec<I>,
            PostgreCtes,
            PostgreInsert._ComplexInsertIntoClause<I>>
            implements PostgreInsert._ComplexOptionSpec<I> {

        private final Function<PrimaryStatement, I> function;

        private ComplexInsertIntoClause(ArmyStmtSpec spec,
                                        Function<PrimaryStatement, I> function) {
            super(CriteriaContexts.primaryInsertContext(PostgreUtils.DIALECT, spec));
            this.function = function;
            ContextStack.push(this.context);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._ComplexInsertIntoClause<I>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._ComplexInsertIntoClause<I>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            //TODO fix two stmt mode for multi-statement
            return new PostgreComplexValuesClause<>(this, table, true, this.function.compose(PostgreInserts::insertEnd),
                    this.function.compose(PostgreInserts::returningInsertEnd));
        }

        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }

    }//ComplexInsertIntoClause


    private static final class DynamicSubInsertIntoClause<I extends Item>
            extends NonQueryWithCteOption<
            PostgreInsert._DynamicSubNullOptionSpec<I>,
            PostgreCtes,
            PostgreInsert._CteInsertIntoClause<I>>
            implements PostgreInsert._DynamicSubOptionSpec<I> {

        private final Function<SubStatement, I> function;

        private DynamicSubInsertIntoClause(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(CriteriaContexts.subInsertContext(PostgreUtils.DIALECT, null, outerContext));
            this.function = function;
            //just push sub context,here don't need to start cte
            ContextStack.push(this.context);

        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._CteInsertIntoClause<I>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._CteInsertIntoClause<I>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, false, this.function.compose(PostgreInserts::subInsertEnd),
                    this.function.compose(PostgreInserts::subReturningInsertEnd));
        }


        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }

    }//DynamicSubInsertIntoClause

    private static final class StaticSubInsertIntoClause<I extends Item> extends NonQueryWithCteOption<
            PostgreInsert._StaticSubNullOptionSpec<I>,
            PostgreCtes,
            PostgreInsert._CteInsertIntoClause<I>>
            implements PostgreInsert._StaticSubOptionSpec<I> {

        private final Function<SubStatement, I> function;

        private StaticSubInsertIntoClause(ArmyStmtSpec spec, Function<SubStatement, I> function) {
            super(CriteriaContexts.subInsertContext(PostgreUtils.DIALECT, spec, null));
            this.function = function;
            //just push sub context,here don't need to start cte
            ContextStack.push(this.context);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, false, this.function.compose(PostgreInserts::subInsertEnd),
                    this.function.compose(PostgreInserts::subReturningInsertEnd));
        }

        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            throw ContextStack.castCriteriaApi(this.context);
        }


    }//StaticSubInsertIntoClause


    private enum OverridingMode implements SQLWords {

        OVERRIDING_SYSTEM_VALUE(" OVERRIDING SYSTEM VALUE"),
        OVERRIDING_USER_VALUE(" OVERRIDING USER VALUE");

        private final String spaceWords;

        OverridingMode(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }


        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(OverridingMode.class.getSimpleName())
                    .append(_Constant.PERIOD)
                    .append(this.name())
                    .toString();
        }


    }//OverridingMode


    private static final class ConflictTargetItem<T, I extends Item, Q extends Item>
            implements PostgreInsert._ConflictCollateSpec<T, I, Q>
            , _ConflictTargetItem {

        private final OnConflictClause<T, I, Q> clause;

        private final ArmyExpression indexExpression;

        private String collationName;

        private String operatorClass;

        private ConflictTargetItem(OnConflictClause<T, I, Q> clause, ArmyExpression indexExpression) {
            this.clause = clause;
            this.indexExpression = indexExpression;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            if (this.indexExpression instanceof FieldMeta || this.indexExpression instanceof SQLFunction) {
                this.indexExpression.appendSql(sqlBuilder, context);
            } else {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                this.indexExpression.appendSql(sqlBuilder, context);
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            final DialectParser parser = context.parser();
            final String collationName = this.collationName;
            if (collationName != null) {
                sqlBuilder.append(" COLLATE ");
                parser.identifier(collationName, sqlBuilder);
            }
            final String operatorClass = this.operatorClass;
            if (operatorClass != null) {
                sqlBuilder.append(_Constant.SPACE);
                parser.identifier(operatorClass, sqlBuilder);
            }
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<T, I, Q> collation(final @Nullable String collationName) {
            if (collationName == null) {
                throw ContextStack.nullPointer(this.clause.valuesClause.context);
            } else if (this.collationName != null || this.operatorClass != null) {
                throw ContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            this.collationName = collationName;
            return this;
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<T, I, Q> collation(Supplier<String> supplier) {
            return this.collation(supplier.get());
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<T, I, Q> ifCollation(Supplier<String> supplier) {
            final String collation;
            collation = supplier.get();
            if (collation != null) {
                this.collation(collation);
            }
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetCommaSpec<T, I, Q> space(final @Nullable String operatorClass) {
            if (this.operatorClass != null) {
                throw ContextStack.castCriteriaApi(this.clause.valuesClause.context);
            } else if (operatorClass == null) {
                throw ContextStack.nullPointer(this.clause.valuesClause.context);
            } else if (!_DialectUtils.isSimpleIdentifier(operatorClass)) {
                throw nonSafeOperatorClassName(operatorClass);
            }
            this.operatorClass = operatorClass;
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetCommaSpec<T, I, Q> ifSpace(Supplier<String> supplier) {
            if (this.operatorClass != null) {
                throw ContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            final String operatorClass;
            operatorClass = supplier.get();
            if (operatorClass != null) {
                if (!_DialectUtils.isSimpleIdentifier(operatorClass)) {
                    throw nonSafeOperatorClassName(operatorClass);
                }
                this.operatorClass = operatorClass;
            }
            return this;
        }


        @Override
        public PostgreInsert._ConflictCollateSpec<T, I, Q> comma(IndexFieldMeta<T> indexColumn) {
            return this.clause.leftParen(indexColumn); // create and add
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<T, I, Q> comma(Expression indexExpression) {
            return this.clause.leftParen(indexExpression); // create and add
        }

        @Override
        public PostgreInsert._ConflictTargetWhereSpec<T, I, Q> rightParen() {
            return this.clause.targetItemClauseEnd();
        }

        private CriteriaException nonSafeOperatorClassName(String operatorClassName) {
            String m = String.format("operatorClass[%s] is illegal.", operatorClassName);
            return ContextStack.criteriaError(this.clause.context, m);
        }

    }//ConflictTargetItem


    private static final class ConflictDoUpdateActionClause<T, I extends Item, Q extends Item>
            extends SetWhereClause.SetWhereClauseClause<
            FieldMeta<T>,
            PostgreInsert._DoUpdateWhereSpec<T, I, Q>,
            PostgreInsert._ReturningSpec<I, Q>,
            PostgreInsert._DoUpdateWhereAndSpec<I, Q>>
            implements PostgreInsert._DoUpdateWhereSpec<T, I, Q>
            , PostgreInsert._DoUpdateWhereAndSpec<I, Q> {

        private final OnConflictClause<T, I, Q> onConflictClause;

        private ConflictDoUpdateActionClause(OnConflictClause<T, I, Q> clause) {
            super(clause.valuesClause.context, clause.valuesClause.insertTable, clause.safeTableAlias);
            this.onConflictClause = clause;
        }

        @Override
        public PostgreInsert._DoUpdateWhereClause<I, Q> sets(Consumer<UpdateStatement._RowPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.rowPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(selection);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection1, Selection selection2) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(selection1, selection2);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(function, alias);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(
                Function<String, Selection> function1, String alias1,
                Function<String, Selection> function2, String alias2) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(function1, alias1, function2, alias2);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias,
                                                                    Selection selection) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(function, alias, selection);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(
                Selection selection, Function<String, Selection> function, String alias) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(selection, function, alias);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(TableMeta<?> insertTable) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(insertTable);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2,
                                                                    TableField field3) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(field1, field2, field3);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2,
                                                                    TableField field3, TableField field4) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(field1, field2, field3, field4);
        }

        @Override
        public Statement._DqlInsertClause<Q> returningAll() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returningAll();
        }

        @Override
        public Statement._DqlInsertClause<Q> returning(Consumer<Returnings> consumer) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .returning(consumer);
        }

        @Override
        public I asInsert() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClauseIfNeed())
                    .asInsert();
        }


    }//ConflictDoUpdateActionClause


    private static final class OnConflictClause<T, I extends Item, Q extends Item>
            extends WhereClause.WhereClauseClause<
            PostgreInsert._ConflictActionClause<T, I, Q>,
            PostgreInsert._ConflictTargetWhereAndSpec<T, I, Q>>
            implements PostgreInsert._ConflictTargetOptionSpec<T, I, Q>
            , PostgreInsert._ConflictTargetWhereSpec<T, I, Q>
            , PostgreInsert._ConflictTargetWhereAndSpec<T, I, Q> {

        private final PostgreComplexValuesClause<T, I, Q> valuesClause;

        private final String safeTableAlias;

        private List<_ConflictTargetItem> targetItemList;

        private String constraintName;

        private boolean doNothing;

        private OnConflictClause(PostgreComplexValuesClause<T, I, Q> valuesClause) {
            super(valuesClause.context);
            this.valuesClause = valuesClause;
            final String tableAlias = valuesClause.tableAlias;
            this.safeTableAlias = tableAlias == null ? "" : tableAlias;
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<T, I, Q> leftParen(final IndexFieldMeta<T> indexColumn) {
            return this.addIndexExpression(indexColumn);
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<T, I, Q> leftParen(final Expression indexExpression) {
            return this.addIndexExpression(indexExpression);
        }


        @Override
        public PostgreInsert._ConflictActionClause<T, I, Q> onConstraint(final @Nullable String constraintName) {
            if (this.constraintName != null) {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            } else if (constraintName == null) {
                throw ContextStack.nullPointer(this.valuesClause.context);
            }
            this.constraintName = constraintName;
            return this;
        }

        @Override
        public PostgreInsert._ReturningSpec<I, Q> doNothing() {
            this.endWhereClauseIfNeed();
            this.doNothing = true;
            return this.valuesClause.conflictClauseEnd(new ConflictActionClauseResult(this));
        }

        @Override
        public PostgreInsert._DoUpdateSetClause<T, I, Q> doUpdate() {
            this.endWhereClauseIfNeed();
            return new ConflictDoUpdateActionClause<>(this);
        }

        private PostgreInsert._ConflictTargetWhereSpec<T, I, Q> targetItemClauseEnd() {
            final List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList instanceof ArrayList) {
                this.targetItemList = Collections.unmodifiableList(targetItemList);
            } else {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            }
            return this;
        }

        private PostgreInsert._ReturningSpec<I, Q> updateActionClauseEnd(List<_ItemPair> itemPairList
                , List<_Predicate> predicateList) {
            return this.valuesClause
                    .conflictClauseEnd(new ConflictActionClauseResult(this, itemPairList, predicateList));
        }

        private PostgreInsert._ConflictCollateSpec<T, I, Q> addIndexExpression(final Expression indexExpression) {
            if (!(indexExpression instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            } else if (indexExpression instanceof FieldMeta
                    && ((FieldMeta<?>) indexExpression).tableMeta() != this.valuesClause.insertTable) {
                String m = String.format("%s isn't field of %s.", indexExpression, this.valuesClause.insertTable);
                throw ContextStack.criteriaError(this.context, m);
            }
            List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList == null) {
                targetItemList = _Collections.arrayList();
                this.targetItemList = targetItemList;
            } else if (!(targetItemList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            }
            final ConflictTargetItem<T, I, Q> item = new ConflictTargetItem<>(this, (ArmyExpression) indexExpression);
            targetItemList.add(item);
            return item;
        }


    }//OnConflictClause


    private static final class ConflictActionClauseResult
            implements _PostgreInsert._ConflictActionClauseResult {

        private final List<_ConflictTargetItem> targetItemList;

        private final List<_Predicate> indexPredicateList;

        private final String constraintName;

        private final boolean doNothing;

        private final List<_ItemPair> itemPairList;

        private final List<_Predicate> updatePredicateList;

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?> clause) {
            this.targetItemList = _Collections.safeList(clause.targetItemList);
            if (this.targetItemList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(clause.valuesClause.context);
            }
            this.indexPredicateList = clause.wherePredicateList();
            this.constraintName = clause.constraintName;
            this.doNothing = clause.doNothing;

            this.itemPairList = Collections.emptyList();
            this.updatePredicateList = Collections.emptyList();
        }

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?> clause, List<_ItemPair> itemPairList
                , List<_Predicate> updatePredicateList) {
            this.doNothing = clause.doNothing;
            this.targetItemList = _Collections.safeList(clause.targetItemList);
            if (this.targetItemList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(clause.valuesClause.context);
            }
            this.indexPredicateList = clause.wherePredicateList();
            this.constraintName = clause.constraintName;


            this.itemPairList = itemPairList;
            this.updatePredicateList = updatePredicateList;
        }

        @Override
        public boolean hasConflictAction() {
            return true;
        }

        @Override
        public List<_ItemPair> updateSetClauseList() {
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
        public boolean isIgnorableConflict() {
            //true,Postgre support DO NOTHING and conflict_target and WHERE
            return true;
        }

        @Override
        public boolean isDoNothing() {
            return this.doNothing;
        }

        @Override
        public String rowAlias() {
            // null,postgre don't support row alias
            return null;
        }


    }//ConflictActionClauseResult


    private static final class PostgreComplexValuesClause<T, I extends Item, Q extends Item>
            extends ComplexInsertValuesClause<
            T,
            PostgreInsert._OverridingValueSpec<T, I, Q>,
            PostgreInsert._ValuesDefaultSpec<T, I, Q>,
            PostgreInsert._OnConflictSpec<T, I, Q>>
            implements PostgreInsert._TableAliasSpec<T, I, Q>,
            PostgreInsert._OnConflictSpec<T, I, Q>,
            PostgreInsert._OverridingValueSpec<T, I, Q>,
            PostgreInsert._ComplexColumnDefaultSpec<T, I, Q>,
            PostgreInsert._StaticReturningCommaSpec<Q>,
            Statement._DqlInsertClause<Q> {

        private final Function<PostgreComplexValuesClause<?, ?, ?>, I> dmlFunction;

        private final Function<PostgreComplexValuesClause<?, ?, ?>, Q> dqlFunction;

        private final boolean recursive;

        private final List<_Cte> cteList;

        private String tableAlias;

        private OverridingMode overridingMode;

        private _PostgreInsert._ConflictActionClauseResult conflictAction;

        private List<_SelectItem> returningList;


        /**
         * @see PrimaryInsertIntoClause#insertInto(TableMeta)
         * @see PrimaryInsertIntoClause#insertInto(ParentTableMeta)
         * @see ChildInsertIntoClause#insertInto(ComplexTableMeta)
         */
        private PostgreComplexValuesClause(WithValueSyntaxOptions options, TableMeta<T> table, boolean twoStmtMode,
                                           Function<PostgreComplexValuesClause<?, ?, ?>, I> dmlFunction,
                                           Function<PostgreComplexValuesClause<?, ?, ?>, Q> dqlFunction) {
            super(options, table, twoStmtMode);
            this.recursive = options.isRecursive();
            this.cteList = options.cteList();
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        public PostgreInsert._ColumnListSpec<T, I, Q> as(final String alias) {
            this.context.singleDmlTable(this.insertTable, alias);
            this.tableAlias = alias;
            return this;
        }

        @Override
        public PostgreInsert._ComplexColumnDefaultSpec<T, I, Q> overridingSystemValue() {
            this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            return this;
        }

        @Override
        public PostgreInsert._ComplexColumnDefaultSpec<T, I, Q> overridingUserValue() {
            this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            return this;
        }

        @Override
        public PostgreInsert._ComplexColumnDefaultSpec<T, I, Q> ifOverridingSystemValue(BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._ComplexColumnDefaultSpec<T, I, Q> ifOverridingUserValue(BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._PostgreValuesStaticParensClause<T, I, Q> values() {
            return new PostgreValuesParensClause<>(this);
        }

        @Override
        public PostgreQuery._WithSpec<PostgreInsert._OnConflictSpec<T, I, Q>> space() {
            return PostgreQueries.subQuery(this.context, this::spaceQueryEnd);
        }

        @Override
        public PostgreInsert._OnConflictSpec<T, I, Q> space(Supplier<SubQuery> supplier) {
            return this.spaceQueryEnd(supplier.get());
        }

        @Override
        public PostgreInsert._OnConflictSpec<T, I, Q> space(Function<PostgreQuery._WithSpec<PostgreInsert._OnConflictSpec<T, I, Q>>, PostgreInsert._OnConflictSpec<T, I, Q>> function) {
            return function.apply(PostgreQueries.subQuery(this.context, this::spaceQueryEnd));
        }

        @Override
        public PostgreInsert._ConflictTargetOptionSpec<T, I, Q> onConflict() {
            return new OnConflictClause<>(this);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection) {
            this.onAddSelection(selection);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection1, Selection selection2) {
            this.onAddSelection(selection1)
                    .onAddSelection(selection2);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias) {
            this.onAddSelection(function.apply(alias));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(
                Function<String, Selection> function1, String alias1,
                Function<String, Selection> function2, String alias2) {
            this.onAddSelection(function1.apply(alias1))
                    .onAddSelection(function2.apply(alias2));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias,
                                                                    Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .onAddSelection(selection);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(
                Selection selection, Function<String, Selection> function, String alias) {
            this.onAddSelection(selection)
                    .onAddSelection(function.apply(alias));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(final TableMeta<?> insertTable) {
            if (insertTable != this.insertTable) {
                throw CriteriaUtils.errorInsertTableGroup(this.context, this.insertTable, insertTable);
            }
            this.onAddSelection(SelectionGroups.insertTableGroup(insertTable));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2,
                                                                    TableField field3) {
            this.onAddSelection(field1)
                    .onAddSelection(field2)
                    .onAddSelection(field3);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2,
                                                                    TableField field3, TableField field4) {
            this.onAddSelection(field1)
                    .onAddSelection(field2)
                    .onAddSelection(field3)
                    .onAddSelection(field4);
            return this;
        }


        @Override
        public Statement._DqlInsertClause<Q> returningAll() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = PostgreSupports.EMPTY_SELECT_ITEM_LIST;
            return this;
        }

        @Override
        public Statement._DqlInsertClause<Q> returning(Consumer<Returnings> consumer) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = CriteriaUtils.selectionList(this.context, consumer);
            return this;
        }


        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(final Selection selection) {
            this.onAddSelection(selection);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(Selection selection1, Selection selection2) {
            this.onAddSelection(selection1)
                    .onAddSelection(selection2);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias) {
            this.onAddSelection(function.apply(alias));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(Function<String, Selection> function1, String alias1,
                                                                Function<String, Selection> function2, String alias2) {
            this.onAddSelection(function1.apply(alias1))
                    .onAddSelection(function2.apply(alias2));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias,
                                                                Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .onAddSelection(selection);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(Selection selection,
                                                                Function<String, Selection> function, String alias) {
            this.onAddSelection(selection)
                    .onAddSelection(function.apply(alias));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(final TableMeta<?> insertTable) {
            return this.returning(insertTable);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2,
                                                                TableField field3) {
            this.onAddSelection(field1)
                    .onAddSelection(field2)
                    .onAddSelection(field3);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3,
                                                                TableField field4) {
            this.onAddSelection(field1)
                    .onAddSelection(field2)
                    .onAddSelection(field3)
                    .onAddSelection(field4);
            return this;
        }

        @Override
        public I asInsert() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this.dmlFunction.apply(this);
        }


        @Override
        public Q asReturningInsert() {
            final List<_SelectItem> selectionList = this.returningList;
            if (selectionList != PostgreSupports.EMPTY_SELECT_ITEM_LIST) {
                if (!(selectionList instanceof ArrayList && selectionList.size() > 0)) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
                this.returningList = _Collections.unmodifiableList(selectionList);
            }
            return this.dqlFunction.apply(this);
        }

        @Override
        public String tableAlias() {
            return this.tableAlias;
        }

        private PostgreInsert._ReturningSpec<I, Q> conflictClauseEnd(_PostgreInsert._ConflictActionClauseResult result) {
            if (this.conflictAction != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.conflictAction = result;
            return this;
        }


        @SuppressWarnings("unchecked")
        private List<? extends _SelectItem> effectiveReturningList() {
            final List<_SelectItem> returningList = this.returningList;
            final List<? extends _SelectItem> effectiveList;
            final _PostgreInsert._ConflictActionClauseResult conflictAction;

            if (returningList == null || returningList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (returningList != PostgreSupports.EMPTY_SELECT_ITEM_LIST) {
                effectiveList = returningList;
            } else if ((conflictAction = this.conflictAction) == null) {
                effectiveList = (List<? extends _Selection>) this.effictiveFieldList();
            } else {
                effectiveList = this.doEffectiveFieldList(conflictAction.updateSetClauseList());
            }

            return effectiveList;
        }

        @SuppressWarnings("unchecked")
        private List<? extends _Selection> doEffectiveFieldList(final List<_ItemPair> itemPairList) {
            final List<? extends TableField> columnList = this.effictiveFieldList();
            final int itemPairSize;
            if (this.insertTable.fieldList() == columnList || (itemPairSize = itemPairList.size()) == 0) {
                return (List<? extends _Selection>) columnList;
            }
            final int totalSize = columnList.size() + itemPairSize;
            final Map<FieldMeta<?>, Boolean> fieldMap = _Collections.hashMap((int) (totalSize / 0.75f));
            final List<_Selection> fieldList = _Collections.arrayList(totalSize);

            FieldMeta<?> field;
            for (TableField column : columnList) {
                if (column instanceof FieldMeta) {
                    field = (FieldMeta<?>) column;
                } else if (column instanceof QualifiedField) {
                    field = column.fieldMeta();
                } else {
                    throw _Exceptions.unknownColumn(column);
                }
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                    // no bug,never here
                    throw new IllegalStateException("duplication field");
                }
                fieldList.add((_Selection) field);
            }
            CriteriaUtils.addAllField(itemPairList, f -> {
                if (fieldMap.putIfAbsent(f, Boolean.TRUE) == null) {
                    fieldList.add((_Selection) f);
                }
            });
            return _Collections.unmodifiableList(fieldList);
        }


        private PostgreComplexValuesClause<T, I, Q> onAddSelection(final @Nullable SelectItem selectItem) {
            if (selectItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            List<_SelectItem> list = this.returningList;
            if (list == null) {
                this.returningList = list = _Collections.arrayList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add((_Selection) selectItem);
            return this;
        }


    }//PostgreComplexInsertValuesClause


    private static final class PostgreValuesParensClause<T, I extends Item, Q extends Item>
            extends ValuesParensClauseImpl<
            T,
            PostgreInsert._PostgreValuesStaticParensCommaSpec<T, I, Q>>
            implements PostgreInsert._PostgreValuesStaticParensCommaSpec<T, I, Q>,
            PostgreInsert._PostgreValuesStaticParensClause<T, I, Q> {

        private final PostgreComplexValuesClause<T, I, Q> clause;

        private PostgreValuesParensClause(PostgreComplexValuesClause<T, I, Q> clause) {
            super(clause.context, clause.migration, clause::validateField);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._PostgreValuesStaticParensClause<T, I, Q> comma() {
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetOptionSpec<T, I, Q> onConflict() {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .onConflict();
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(selection);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection1, Selection selection2) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(selection1, selection2);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Function<String, Selection> function,
                                                                    String alias) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(function, alias);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(
                Function<String, Selection> function1, String alias1,
                Function<String, Selection> function2, String alias2) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(function1, alias1, function2, alias2);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias,
                                                                    Selection selection) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(function, alias, selection);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(
                Selection selection, Function<String, Selection> function, String alias) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(selection, function, alias);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(TableMeta<?> insertTable) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(insertTable);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2,
                                                                    TableField field3) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(field1, field2, field3);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2,
                                                                    TableField field3, TableField field4) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(field1, field2, field3, field4);
        }

        @Override
        public Statement._DqlInsertClause<Q> returningAll() {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returningAll();
        }


        @Override
        public Statement._DqlInsertClause<Q> returning(Consumer<Returnings> consumer) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(consumer);
        }

        @Override
        public I asInsert() {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .asInsert();
        }


    }//StaticValuesLeftParenClause


    static abstract class PostgreValueSyntaxInsertStatement<I extends Statement, Q extends Statement>
            extends ArmyValueSyntaxStatement<I, Q>
            implements PostgreInsert, _PostgreInsert {

        private final boolean recursive;

        final List<_Cte> cteList;


        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        final List<? extends _SelectItem> returningList;


        private PostgreValueSyntaxInsertStatement(final PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.overridingMode = clause.overridingMode;
            this.conflictAction = clause.conflictAction;
            if (this instanceof _ReturningDml) {
                this.returningList = clause.effectiveReturningList();
            } else {
                this.returningList = PostgreSupports.EMPTY_SELECT_ITEM_LIST;
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
        public final List<? extends _SelectItem> returningList() {
            if (!(this instanceof _ReturningDml)) {
                throw new UnsupportedOperationException();
            }
            return this.returningList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictAction != null;
        }

        @Override
        public final boolean isIgnorableConflict() {
            final _ConflictActionClauseResult conflictAction = this.conflictAction;
            return conflictAction != null && conflictAction.isIgnorableConflict();
        }

        @Override
        public final boolean isDoNothing() {
            final _ConflictActionClauseResult conflictAction = this.conflictAction;
            return conflictAction != null && conflictAction.isDoNothing();
        }

        @Override
        public final String rowAlias() {
            // null,postgre don't support row alias
            return null;
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
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }


    }//PrimaryValueSyntaxInsertStatement


    static abstract class DomainInsertStatement<I extends Statement, Q extends Statement>
            extends PostgreValueSyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreDomainInsert {


        private DomainInsertStatement(final PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);

        }


    }//DomainInsertStatement


    private static final class PrimarySimpleDomainInsertStatement extends DomainInsertStatement<Insert, ReturningInsert>
            implements Insert {

        private final List<?> domainList;

        private PrimarySimpleDomainInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert this.insertTable instanceof SimpleTableMeta;
            this.domainList = clause.domainListForSimple();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    }//PrimarySimpleDomainInsertStatement

    private static final class PrimaryChildDomainInsertOneStatement extends DomainInsertStatement<Insert, ReturningInsert>
            implements Insert, _Insert._OneStatementChildInsert {

        private final ParentDomainSubInsert parentStmt;

        private PrimaryChildDomainInsertOneStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert this.insertTable instanceof ChildTableMeta;
            final ParentSubInsert parent;
            parent = parentSubInsert(this, this.cteList, this.cteList.size());
            this.parentStmt = (ParentDomainSubInsert) parent;
        }

        @Override
        public List<?> domainList() {
            return this.parentStmt.domainList();
        }

        @Override
        public void validParentDomain() {
            this.parentStmt.validateChild((ChildTableMeta<?>) this.insertTable);
        }

    }//PrimaryChildDomainInsertOneStatement


    private static final class PrimaryChildDomainInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements Insert, _PostgreInsert._PostgreChildDomainInsert {

        private final PrimaryParentDomainInsertStatement<?> parentStatement;


        private PrimaryChildDomainInsertStatement(PrimaryParentDomainInsertStatement<?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            parentStatement.prepared();
            this.parentStatement = parentStatement;

        }

        @Override
        public List<?> domainList() {
            return this.parentStatement.domainList;
        }

        @Override
        public _PostgreDomainInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildDomainInsertStatement


    private static final class PrimaryParentDomainInsertStatement<P>
            extends DomainInsertStatement<PostgreInsert._ParentInsert<P>, ReturningInsert>
            implements PostgreInsert._ParentInsert<P>, ValueSyntaxOptions {

        private final List<?> originalDomainList;

        private final List<?> domainList;

        /**
         * @see PostgreInserts#parentInsertEnd(PostgreComplexValuesClause)
         */
        private PrimaryParentDomainInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _Collections.asUnmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            return new PrimaryChildDomainReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentDomainInsertStatement

    private static final class PrimaryDomainReturningInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, _ReturningDml {

        private final List<?> domainList;

        private PrimaryDomainReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForSimple();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//PrimaryDomainReturningInsertStatement


    private static final class PrimaryChildDomainReturningInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, _ReturningDml, _PostgreInsert._PostgreChildDomainInsert {

        private final DomainInsertStatement<?, ?> parentStatement;

        private final List<?> domainList;

        private PrimaryChildDomainReturningInsertStatement(DomainInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            this.parentStatement = parentStatement;
            if (parentStatement instanceof PrimaryParentDomainReturningInsertStatement) {
                this.domainList = ((PrimaryParentDomainReturningInsertStatement<?>) parentStatement).domainList;
            } else if (parentStatement instanceof PrimaryParentDomainInsertStatement) {
                this.domainList = ((PrimaryParentDomainInsertStatement<?>) parentStatement).domainList;
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _PostgreDomainInsert parentStmt() {
            return this.parentStatement;
        }

    }//PrimaryChildDomainReturningInsertStatement

    private static final class PrimaryParentDomainReturningInsertStatement<P>
            extends DomainInsertStatement<Insert, PostgreInsert._ParentReturnInsert<P>>
            implements PostgreInsert._ParentReturnInsert<P>, _ReturningDml, ValueSyntaxOptions {

        private final List<?> originalDomainList;
        private final List<?> domainList;

        /**
         * @see PostgreInserts#parentReturningEnd(PostgreComplexValuesClause)
         */
        private PrimaryParentDomainReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _Collections.asUnmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            throw _Exceptions.illegalTwoStmtMode();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            return new PrimaryChildDomainReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentDomainReturningInsertStatement


    private static final class SubDomainInsertStatement extends DomainInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private final List<?> domainList;

        private SubDomainInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            if (clause.insertTable instanceof ParentTableMeta) {
                this.domainList = _Collections.asUnmodifiableList(clause.originalDomainList());
            } else {
                this.domainList = clause.domainListForSimple();
            }
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    }//SubDomainInsertStatement

    private static final class SubDomainReturningInsertStatement
            extends DomainInsertStatement<SubStatement, SubStatement>
            implements SubStatement, _ReturningDml {

        private final List<?> domainList;

        private SubDomainReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            if (clause.insertTable instanceof ParentTableMeta) {
                this.domainList = _Collections.asUnmodifiableList(clause.originalDomainList());
            } else {
                this.domainList = clause.domainListForSimple();
            }
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//SubDomainReturningInsertStatement


    static abstract class ValueInsertStatement<I extends Statement, Q extends Statement>
            extends PostgreValueSyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreValueInsert {

        final List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private ValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }


    }//ValueInsertStatement


    private static final class PrimaryValueInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements Insert {

        private PrimaryValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueInsertStatement


    private static final class PrimaryChildValueInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements Insert, _PostgreInsert._PostgreChildValueInsert {

        private final ValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueInsertStatement(ValueInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert parentStatement instanceof PrimaryParentValueInsertStatement
                    || parentStatement instanceof PrimaryParentValueReturningInsertStatement;
            this.parentStatement = parentStatement;
        }

        @Override
        public _PostgreValueInsert parentStmt() {
            return this.parentStatement;
        }

    }//PrimaryChildValueInsertStatement


    private static final class PrimaryParentValueInsertStatement<P>
            extends ValueInsertStatement<PostgreInsert._ParentInsert<P>, ReturningInsert>
            implements Insert, PostgreInsert._ParentInsert<P>, ValueSyntaxOptions {

        /**
         * @see PostgreInserts#parentInsertEnd(PostgreComplexValuesClause)
         */
        private PrimaryParentValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentValueInsertStatement

    private static final class PrimaryValueReturningInsertStatement
            extends ValueInsertStatement<InsertStatement, ReturningInsert>
            implements ReturningInsert, _ReturningDml {

        private PrimaryValueReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryValueReturningInsertStatement

    private static final class PrimaryChildValueReturningInsertStatement
            extends ValueInsertStatement<InsertStatement, ReturningInsert>
            implements _PostgreInsert._PostgreChildValueInsert, ReturningInsert, _ReturningDml {

        private final ValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueReturningInsertStatement(ValueInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert parentStatement instanceof PrimaryParentValueInsertStatement
                    || parentStatement instanceof PrimaryParentValueReturningInsertStatement;
            parentStatement.prepared();
            this.parentStatement = parentStatement;
        }

        @Override
        public _PostgreValueInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildValueReturningInsertStatement

    private static final class PrimaryParentValueReturningInsertStatement<P>
            extends ValueInsertStatement<InsertStatement, PostgreInsert._ParentReturnInsert<P>>
            implements PostgreInsert._ParentReturnInsert<P>, ValueSyntaxOptions, _ReturningDml {

        /**
         * @see PostgreInserts#parentReturningEnd(PostgreComplexValuesClause)
         */
        private PrimaryParentValueReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentValueReturningInsertStatement


    private static final class SubValueInsertStatement
            extends ValueInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private SubValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//SubValueInsertStatement


    private static final class SubValueReturningInsertStatement
            extends ValueInsertStatement<SubStatement, SubStatement>
            implements SubStatement, _ReturningDml {

        private SubValueReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

    }//SubValueReturningInsertStatement


    static abstract class PostgreQueryInsertStatement<I extends Statement, Q extends Statement>
            extends ArmyQuerySyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreQueryInsert, PostgreInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        private final List<? extends _SelectItem> returningList;

        private PostgreQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            if (this instanceof _ReturningDml) {
                this.returningList = clause.effectiveReturningList();
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
        public final SQLWords overridingValueWords() {
            return this.overridingMode;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictAction != null;
        }

        @Override
        public final boolean isIgnorableConflict() {
            final _ConflictActionClauseResult conflictAction = this.conflictAction;
            return conflictAction != null && conflictAction.isIgnorableConflict();
        }

        @Override
        public final boolean isDoNothing() {
            final _ConflictActionClauseResult conflictAction = this.conflictAction;
            return conflictAction != null && conflictAction.isDoNothing();
        }

        @Override
        public final String rowAlias() {
            return this.tableAlias;
        }

        @Override
        public final _ConflictActionClauseResult getConflictActionResult() {
            return this.conflictAction;
        }

        @Override
        public final List<? extends _SelectItem> returningList() {
            if (!(this instanceof _ReturningDml)) {
                throw new UnsupportedOperationException();
            }
            return this.returningList;
        }


        @Override
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }


    }//QueryInsertStatement


    private static abstract class ParentQueryInsertStatement<I extends Statement, Q extends Statement>
            extends PostgreQueryInsertStatement<I, Q>
            implements ParentQueryInsert,
            _PostgreInsert._PostgreParentQueryInsert {

        private CodeEnum discriminatorValue;

        private ParentQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public final void onValidateEnd(final CodeEnum discriminatorValue) {
            assert this.discriminatorValue == null;
            this.discriminatorValue = discriminatorValue;
        }

        @Override
        public final CodeEnum discriminatorEnum() {
            final CodeEnum value = this.discriminatorValue;
            assert value != null;
            return value;
        }


    }//ParentQueryInsertStatement


    private static final class PrimaryQueryInsertStatement
            extends PostgreQueryInsertStatement<Insert, ReturningInsert>
            implements Insert {

        private PrimaryQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert !(clause.insertTable instanceof ParentTableMeta);
        }


    }//PrimaryNonParentQueryInsertStatement

    private static final class PrimaryChildQueryInsertStatement extends PostgreQueryInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildQueryInsert {

        private final ParentQueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryInsertStatement(ParentQueryInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert parentStatement instanceof PrimaryParentQueryInsertStatement
                    || parentStatement instanceof PrimaryParentQueryReturningInsertStatement;
            parentStatement.prepared();
            this.parentStatement = parentStatement;
        }

        @Override
        public _PostgreParentQueryInsert parentStmt() {
            return this.parentStatement;
        }

    }//PrimaryChildQueryInsertStatement


    private static final class PrimaryParentQueryInsertStatement<P>
            extends ParentQueryInsertStatement<PostgreInsert._ParentInsert<P>, ReturningInsert>
            implements PostgreInsert._ParentInsert<P> {

        /**
         * @see PostgreInserts#parentInsertEnd(PostgreComplexValuesClause)
         */
        private PrimaryParentQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentQueryInsertStatement

    private static final class PrimaryQueryReturningInsertStatement
            extends PostgreQueryInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, _ReturningDml {

        private PrimaryQueryReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert !(clause.insertTable instanceof ParentTableMeta);
        }


    }//PrimaryNonParentQueryReturningInsertStatement

    private static final class PrimaryChildQueryReturningInsertStatement
            extends PostgreQueryInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, _ReturningDml, _PostgreInsert._PostgreChildQueryInsert {

        private final ParentQueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryReturningInsertStatement(ParentQueryInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert parentStatement instanceof PrimaryParentQueryInsertStatement
                    || parentStatement instanceof PrimaryParentQueryReturningInsertStatement;
            parentStatement.prepared();
            this.parentStatement = parentStatement;
        }

        @Override
        public _PostgreParentQueryInsert parentStmt() {
            return this.parentStatement;
        }

    }//PrimaryQueryReturningInsertStatement


    private static final class PrimaryParentQueryReturningInsertStatement<P>
            extends ParentQueryInsertStatement<Insert, PostgreInsert._ParentReturnInsert<P>>
            implements PostgreInsert._ParentReturnInsert<P>,
            _ReturningDml {

        /**
         * @see PostgreInserts#parentReturningEnd(PostgreComplexValuesClause)
         */
        private PrimaryParentQueryReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            throw _Exceptions.illegalTwoStmtMode();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryQueryReturningInsertStatement


    private static final class SubNonParentQueryInsertStatement extends PostgreQueryInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private SubNonParentQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert !(clause.insertTable instanceof ParentTableMeta);
        }


    }//SubNonParentQueryInsertStatement

    private static final class SubParentQueryInsertStatement
            extends ParentQueryInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private SubParentQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

    }//SubParentQueryInsertStatement

    private static final class SubNonParentQueryReturningInsertStatement
            extends PostgreQueryInsertStatement<SubStatement, SubStatement>
            implements SubStatement, _ReturningDml {

        private SubNonParentQueryReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert !(clause.insertTable instanceof ParentTableMeta);
        }


    }//SubNonParentQueryReturningInsertStatement

    private static final class SubParentQueryReturingInsertStatement
            extends ParentQueryInsertStatement<SubStatement, SubStatement>
            implements SubStatement, _ReturningDml {

        private SubParentQueryReturingInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

    }//SubParentQueryReturingInsertStatement


}
