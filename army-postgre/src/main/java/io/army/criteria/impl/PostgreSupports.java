package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.postgre._PostgreCteStatement;
import io.army.criteria.impl.inner.postgre._PostgreTableBlock;
import io.army.criteria.postgre.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.BigDecimalType;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;


abstract class PostgreSupports extends CriteriaSupports {


    private PostgreSupports() {
    }


    static final List<Selection> RETURNING_ALL = Collections.emptyList();

    static PostgreCtes postgreCteBuilder(final boolean recursive, final CriteriaContext context) {
        return new PostgreCteBuilderImpl(recursive, context);
    }

    static PostgreQuery._WindowPartitionBySpec postgreNamedWindow(String name, CriteriaContext context
            , @Nullable String existingWindowName) {
        return new PostgreWindow(name, context, existingWindowName);
    }

    static PostgreQuery._WindowPartitionBySpec postgreAnonymousWindow(CriteriaContext context
            , @Nullable String existingWindowName) {
        return new PostgreWindow(context, existingWindowName);
    }


    enum MaterializedOption implements SQLWords {

        MATERIALIZED(" MATERIALIZED"),
        NOT_MATERIALIZED(" NOT MATERIALIZED");

        private final String spaceWord;

        MaterializedOption(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.sqlWordsToString(this);
        }


    }//MaterializedOption


    static final class PostgreSubStatement implements _PostgreCteStatement {

        private final MaterializedOption option;

        private final SubStatement statement;


        PostgreSubStatement(@Nullable MaterializedOption option, SubStatement statement) {
            this.option = option;
            this.statement = statement;
        }

        @Override
        public void prepared() {
            this.statement.prepared();
        }

        @Override
        public boolean isPrepared() {
            return this.statement.isPrepared();
        }

        @Override
        public SQLWords materializedOption() {
            return this.option;
        }

        @Override
        public SubStatement subStatement() {
            return this.statement;
        }


    }//PostgreSubStatement

    @SuppressWarnings("unchecked")
    static abstract class PostgreTableBlock<TR, RR, OR> extends OnClauseTableBlock<OR>
            implements _PostgreTableBlock
            , PostgreStatement._TableSampleClause<TR>
            , PostgreStatement._RepeatableClause<RR> {

        private final SQLWords modifier;

        private ArmyExpression sampleMethod;

        private ArmyExpression seed;

        PostgreTableBlock(_JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tableItem, String alias, OR stmt) {
            super(joinType, tableItem, alias, stmt);
            this.modifier = modifier;
        }

        PostgreTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TabularItem tableItem, String alias) {
            super(joinType, tableItem, alias);
            this.modifier = modifier;
        }


        @Override
        public final TR tableSample(final @Nullable Expression method) {
            if (method == null) {
                throw ContextStack.nullPointer(this.getContext());
            }
            this.sampleMethod = (ArmyExpression) method;
            return (TR) this;
        }

        @Override
        public final TR tableSample(String methodName, Expression argument) {
            this.sampleMethod = FunctionUtils.oneArgVoidFunc(methodName, argument);
            return (TR) this;
        }

        @Override
        public final TR tableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
            final List<Expression> expList = new ArrayList<>();
            consumer.accept(expList::add);
            this.sampleMethod = FunctionUtils.multiArgVoidFunc(methodName, expList);
            return (TR) this;
        }

        @Override
        public final <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, T argument) {
            return this.tableSample(method.apply(valueOperator, argument));
        }

        @Override
        public final <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            return this.tableSample(method.apply(valueOperator, supplier.get()));
        }

        @Override
        public final TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public final TR ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
            final List<Expression> expList = new ArrayList<>();
            consumer.accept(expList::add);
            if (expList.size() > 0) {
                this.sampleMethod = FunctionUtils.multiArgVoidFunc(methodName, expList);
            }
            return (TR) this;
        }

        @Override
        public final <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, @Nullable T argument) {
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            final T argument;
            argument = supplier.get();
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final TR ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object argument;
            argument = function.apply(keyName);
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final RR repeatable(final @Nullable Expression seed) {
            if (seed == null) {
                throw ContextStack.nullPointer(this.getContext());
            }
            this.seed = (ArmyExpression) seed;
            return (RR) this;
        }

        @Override
        public final RR repeatable(Supplier<Expression> supplier) {
            return this.repeatable(supplier.get());
        }

        @Override
        public final RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Number seedValue) {
            return this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, seedValue));
        }

        @Override
        public final RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator
                , Supplier<Number> supplier) {
            return this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, supplier.get()));
        }

        @Override
        public final RR repeatable(BiFunction<MappingType, Object, Expression> valueOperator
                , Function<String, ?> function
                , String keyName) {
            return this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, function.apply(keyName)));
        }

        @Override
        public final RR ifRepeatable(Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.seed = (ArmyExpression) expression;
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator
                , @Nullable Number seedValue) {
            if (seedValue != null) {
                this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, seedValue));
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator
                , Supplier<Number> supplier) {
            final Number seedValue;
            if ((seedValue = supplier.get()) != null) {
                this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, seedValue));
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(BiFunction<MappingType, Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            final Object seedValue;
            if ((seedValue = function.apply(keyName)) != null) {
                this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, seedValue));
            }
            return (RR) this;
        }

        @Override
        public final SQLWords modifier() {
            return this.modifier;
        }

        @Override
        public final _Expression sampleMethod() {
            return this.sampleMethod;
        }

        @Override
        public final _Expression seed() {
            return this.seed;
        }

        CriteriaContext getContext() {
            return ContextStack.peek();
        }


    }//PostgreTableBlock


    static final class PostgreNoOnTableBlock extends PostgreTableBlock<Object, Object, Object> {

        PostgreNoOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table, String alias) {
            super(joinType, modifier, table, alias);
        }

    }//PostgreNoOnTableBlock


    static abstract class PostgreOnTableBlock<TR, RR, OR> extends PostgreTableBlock<TR, RR, OR> {

        PostgreOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tableItem, String alias, OR stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//PostgreOnTableBlock


    private static abstract class PostgreDynamicDmlCteLeftParenClause<I extends Item>
            extends ParenStringConsumerClause<Statement._StaticAsClaus<I>>
            implements DialectStatement._SimpleCteLeftParenSpec<I>
            , Statement._StaticAsClaus<I>
            , Statement._AsCteClause<PostgreCtes> {

        private final String name;

        final PostgreCteBuilderImpl cteBuilder;

        private List<String> columnAliasList;

        private PostgreDynamicDmlCteLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(cteBuilder.context);
            this.name = name;
            this.cteBuilder = cteBuilder;
        }


        @Override
        public final PostgreCtes asCte() {
            return this.cteBuilder;
        }

        @Override
        final Statement._StaticAsClaus<I> stringConsumerEnd(final List<String> stringList) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = stringList;
            return this;
        }

        final Statement._AsCteClause<PostgreCtes> subStmtEnd(final SubStatement stmt) {
            CriteriaUtils.createAndAddCte(this.cteBuilder.context, this.name, this.columnAliasList, stmt);
            return this;
        }

    }//PostgreDynamicDmlCteLeftParenClause


    private static final class PostgreDynamicInsertLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreInsert._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCtes>>>
            implements PostgreInsert._DynamicSubInsertSpec {

        private PostgreDynamicInsertLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }

        @Override
        public PostgreInsert._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCtes>> as() {
            return PostgreInserts.dynamicSubInsert(this.context, this::subStmtEnd);
        }

    }//PostgreDynamicInsertLeftParenClause

    private static final class PostgreDynamicUpdateLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreUpdate._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCtes>>>
            implements PostgreUpdate._DynamicCteUpdateSpec {

        private PostgreDynamicUpdateLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }


        @Override
        public PostgreUpdate._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCtes>> as() {
            return PostgreUpdates.dynamicCteUpdate(this.cteBuilder.context, this::subStmtEnd);
        }

    }//PostgreDynamicUpdateLeftParenClause

    private static final class PostgreDynamicDeleteLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreDelete._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCtes>>>
            implements PostgreDelete._DynamicCteDeleteSpec {

        private PostgreDynamicDeleteLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }


        @Override
        public PostgreDelete._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCtes>> as() {
            return PostgreDeletes.dynamicCteDelete(this.cteBuilder.context, this::subStmtEnd);
        }

    }//PostgreDynamicDeleteLeftParenClause

    private enum CteSearchOption implements SQLWords {

        BREADTH(" BREADTH"),
        DEPTH(" DEPTH");

        private final String spaceWord;

        CteSearchOption(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(CteSearchOption.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }


    }//CteSearchOption


    static final class NonOperationPostgreCteSearchClause<I extends Item>
            implements PostgreStatement._CteSearchSpec<I> {

        private final CriteriaContext context;

        private final String cteName;

        private final SubStatement subStatement;

        private final Function<SubStatement, I> function;

        NonOperationPostgreCteSearchClause(CriteriaContext context, String cteName
                , SubStatement subStatement, Function<SubStatement, I> function) {
            this.context = context;
            this.cteName = cteName;
            this.subStatement = subStatement;
            this.function = function;
        }

        @Override
        public PostgreStatement._SearchFirstByClause<I> searchBreadth() {
            throw error("SEARCH");
        }

        @Override
        public PostgreStatement._SearchFirstByClause<I> searchDepth() {
            throw error("SEARCH");
        }

        @Override
        public PostgreStatement._SearchFirstByClause<I> searchBreadth(BooleanSupplier predicate) {
            throw error("SEARCH");
        }

        @Override
        public PostgreStatement._SearchFirstByClause<I> searchDepth(BooleanSupplier predicate) {
            throw error("SEARCH");
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(String columnName) {
            throw error("CYCLE");
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2) {
            throw error("CYCLE");
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2
                , String columnName3) {
            throw error("CYCLE");
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2
                , String columnName3, String columnName4) {
            throw error("CYCLE");
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(Consumer<Consumer<String>> consumer) {
            throw error("CYCLE");
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> ifCycle(Consumer<Consumer<String>> consumer) {
            throw error("CYCLE");
        }

        @Override
        public I asCte() {
            return this.function.apply(this.subStatement);
        }

        private CriteriaException error(String clause) {
            String m = String.format("%s support only recursive query,but cte[%s] isn't recursive."
                    , clause, this.cteName);
            return ContextStack.criteriaError(this.context, m);
        }


    }//NonOperationPostgreCteSearchClause


    static final class PostgreCteSearchClause<I extends Item>
            implements PostgreStatement._CteSearchSpec<I>
            , _PostgreCteStatement._SearchOptionClauseSpec
            , PostgreStatement._SearchFirstByClause<I>
            , PostgreStatement._SetSearchSeqColumnClause<I>
            , PostgreStatement._SetCycleMarkColumnClause<I>
            , PostgreStatement._CycleToMarkValueSpec<I>
            , PostgreStatement._CyclePathColumnClause<I> {

        private final CriteriaContext context;

        private final SubStatement subStmt;

        private final Function<SubStatement, I> function;

        private CteSearchOption searchOption;

        private List<String> firstByColumnList;

        private String searchSeqColumnName;

        private List<String> cycleColumnList;

        private String cycleMarkColumnName;

        private ArmyExpression cycleMarkValue;

        private ArmyExpression cycleMarkDefault;

        private String cyclePathColumnName;

        PostgreCteSearchClause(CriteriaContext context, SubStatement subStmt
                , Function<SubStatement, I> function) {
            this.context = context;
            this.subStmt = subStmt;
            this.function = function;
        }

        @Override
        public PostgreStatement._SearchFirstByClause<I> searchBreadth() {
            this.searchOption = CteSearchOption.BREADTH;
            return this;
        }

        @Override
        public PostgreStatement._SearchFirstByClause<I> searchDepth() {
            this.searchOption = CteSearchOption.DEPTH;
            return this;
        }

        @Override
        public PostgreStatement._SearchFirstByClause<I> searchBreadth(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.searchOption = CteSearchOption.BREADTH;
            } else {
                this.searchOption = null;
            }
            return this;
        }

        @Override
        public PostgreStatement._SearchFirstByClause<I> searchDepth(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.searchOption = CteSearchOption.DEPTH;
            } else {
                this.searchOption = null;
            }
            return this;
        }

        @Override
        public PostgreStatement._SetSearchSeqColumnClause<I> firstBy(String columnName) {
            if (this.searchOption != null) {
                this.firstByColumnList = Collections.singletonList(columnName);
            }
            return this;
        }

        @Override
        public PostgreStatement._SetSearchSeqColumnClause<I> firstBy(String columnName1, String columnName2) {
            if (this.searchOption != null) {
                this.firstByColumnList = ArrayUtils.asUnmodifiableList(columnName1, columnName2);
            }
            return this;
        }

        @Override
        public PostgreStatement._SetSearchSeqColumnClause<I> firstBy(String columnName1, String columnName2
                , String columnName3) {
            if (this.searchOption != null) {
                this.firstByColumnList = ArrayUtils.asUnmodifiableList(columnName1, columnName2, columnName3);
            }
            return this;
        }

        @Override
        public PostgreStatement._SetSearchSeqColumnClause<I> firstBy(
                String columnName1, String columnName2, String columnName3, String columnName4) {
            if (this.searchOption != null) {
                this.firstByColumnList = ArrayUtils.asUnmodifiableList(columnName1, columnName2
                        , columnName3, columnName4);
            }
            return this;
        }

        @Override
        public PostgreStatement._SetSearchSeqColumnClause<I> firstBy(Consumer<Consumer<String>> consumer) {
            if (this.searchOption != null) {
                final List<String> list = new ArrayList<>();
                consumer.accept(list::add);
                if (list.size() == 0) {
                    throw ContextStack.criteriaError(this.context, "firstBy column list required");
                }
                this.firstByColumnList = _CollectionUtils.unmodifiableList(list);
            }
            return this;
        }

        @Override
        public PostgreCteSearchClause<I> set(String columnName) {
            if (this.cycleColumnList != null) {
                this.cycleMarkColumnName = columnName;
            } else if (this.searchOption != null && this.searchSeqColumnName == null) {
                this.searchSeqColumnName = columnName;
            }
            return this;
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(String columnName) {
            this.cycleColumnList = Collections.singletonList(columnName);
            return this;
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2) {
            this.cycleColumnList = ArrayUtils.asUnmodifiableList(columnName1, columnName2);
            return this;
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2
                , String columnName3) {
            this.cycleColumnList = ArrayUtils.asUnmodifiableList(columnName1, columnName2, columnName3);
            return this;
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(String columnName1
                , String columnName2, String columnName3, String columnName4) {
            this.cycleColumnList = ArrayUtils.asUnmodifiableList(columnName1, columnName2, columnName3, columnName4);
            return this;
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> cycle(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() == 0) {
                throw ContextStack.criteriaError(this.context, "cycle column list required");
            }
            this.cycleColumnList = _CollectionUtils.unmodifiableList(list);
            return this;
        }

        @Override
        public PostgreStatement._SetCycleMarkColumnClause<I> ifCycle(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                this.cycleColumnList = _CollectionUtils.unmodifiableList(list);
            } else {
                this.cycleColumnList = null;
            }
            return this;
        }

        @Override
        public PostgreStatement._CyclePathColumnClause<I> to(Expression cycleMarkValue
                , SQLsSyntax.WordDefault wordDefault, Expression cycleMarkDefault) {
            if (this.cycleColumnList != null) {
                if (wordDefault != SQLs.DEFAULT) {
                    throw CriteriaUtils.unknownWords(this.context, wordDefault);
                }
                this.cycleMarkValue = (ArmyExpression) cycleMarkValue;
                this.cycleMarkDefault = (ArmyExpression) cycleMarkDefault;
            }
            return this;
        }

        @Override
        public PostgreStatement._CyclePathColumnClause<I> to(Consumer<BiConsumer<Expression, Expression>> consumer) {
            if (this.cycleColumnList != null) {
                consumer.accept(this::toMarkValueAndDefault);
            }
            return this;
        }

        @Override
        public PostgreStatement._CyclePathColumnClause<I> ifTo(Consumer<BiConsumer<Expression, Expression>> consumer) {
            if (this.cycleColumnList != null) {
                consumer.accept(this::toMarkValueAndDefault);
                if (this.cycleMarkValue == null || this.cycleMarkDefault == null) {
                    throw ContextStack.criteriaError(this.context, "cycle to clause required");
                }
            }
            return this;
        }

        @Override
        public Statement._AsCteClause<I> using(String cyclePathColumnName) {
            if (this.cycleColumnList != null) {
                this.cyclePathColumnName = cyclePathColumnName;
            }
            return this;
        }

        @Override
        public I asCte() {
            final SubStatement statement;
            if (this.searchOption != null || this.cycleColumnList != null) {
                statement = this;
            } else {
                statement = this.subStmt;
            }
            return this.function.apply(statement);
        }


        @Override
        public SQLWords materializedOption() {
            final SubStatement subQuery = this.subStmt;
            return subQuery instanceof _PostgreCteStatement
                    ? ((_PostgreCteStatement) subQuery).materializedOption()
                    : null;
        }

        @Override
        public SubStatement subStatement() {
            final SubStatement subQuery = this.subStmt;
            return subQuery instanceof _PostgreCteStatement
                    ? ((_PostgreCteStatement) subQuery).subStatement()
                    : subQuery;
        }

        @Override
        public void prepared() {
            this.subStmt.prepared();
        }

        @Override
        public boolean isPrepared() {
            return this.subStmt.isPrepared();
        }

        @Override
        public SQLWords searchOption() {
            return this.searchOption;
        }

        @Override
        public List<String> firstByList() {
            final List<String> list = this.firstByColumnList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        public String searchSeqColumnName() {
            final String columnName = this.searchSeqColumnName;
            if (columnName == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return columnName;
        }

        @Override
        public List<String> cycleColumnList() {
            return this.cycleColumnList;
        }

        @Override
        public String cycleMarkColumnName() {
            final String columnName = this.cycleMarkColumnName;
            if (columnName == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return columnName;
        }

        @Override
        public _Expression cycleMarkValue() {
            return this.cycleMarkValue;
        }

        @Override
        public _Expression cycleMarkDefault() {
            return this.cycleMarkDefault;
        }

        @Override
        public String cyclePathColumnName() {
            final String columnName = this.cyclePathColumnName;
            if (columnName == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return columnName;
        }

        private void toMarkValueAndDefault(Expression markValue, Expression markDefault) {
            this.cycleMarkValue = (ArmyExpression) markValue;
            this.cycleMarkDefault = (ArmyExpression) markDefault;
        }


    }//PostgreCteSearchClause


    private static final class PostgreDynamicQueryLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreQuery._DynamicSubMaterializedSpec<PostgreStatement._CteSearchSpec<PostgreCtes>>>
            implements PostgreQuery._DynamicCteQuerySpec {

        private PostgreDynamicQueryLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }


        @Override
        public PostgreQuery._DynamicSubMaterializedSpec<PostgreStatement._CteSearchSpec<PostgreCtes>> as() {
            return PostgreQueries.dynamicCteQuery(this.cteBuilder.context, this::subQueryEnd);
        }

        private PostgreStatement._CteSearchSpec<PostgreCtes> subQueryEnd(SubStatement subQuery) {
            return new PostgreCteSearchClause<>(this.cteBuilder.context, subQuery, this::searchOptionEnd);
        }

        private PostgreCtes searchOptionEnd(SubStatement statement) {
            //TODO 将 search_seq_col_name 等加入 输出列
            return this.subStmtEnd(statement)
                    .asCte();
        }


    }//PostgreDynamicQueryLeftParenClause


    private static final class PostgreDynamicValuesLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreValues._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCtes>>>
            implements PostgreValues._DynamicCteValuesSpec {

        private PostgreDynamicValuesLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }

        @Override
        public PostgreValues._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCtes>> as() {
            return PostgreValuesStatements.dynamicCteValues(this.cteBuilder.context, this::subStmtEnd);
        }

    }//PostgreDynamicValuesLeftParenClause


    private static final class PostgreCteBuilderImpl implements PostgreCtes {

        private final boolean recursive;

        private final CriteriaContext context;

        private PostgreCteBuilderImpl(final boolean recursive, CriteriaContext context) {
            this.recursive = recursive;
            this.context = context;
            context.onBeforeWithClause(recursive);
        }


        @Override
        public PostgreInsert._DynamicSubInsertSpec singleInsert(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicInsertLeftParenClause(name, this);
        }

        @Override
        public PostgreUpdate._DynamicCteUpdateSpec singleUpdate(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicUpdateLeftParenClause(name, this);
        }

        @Override
        public PostgreDelete._DynamicCteDeleteSpec singleDelete(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicDeleteLeftParenClause(name, this);
        }

        @Override
        public PostgreQuery._DynamicCteQuerySpec query(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicQueryLeftParenClause(name, this);
        }

        @Override
        public PostgreValues._DynamicCteValuesSpec cteValues(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicValuesLeftParenClause(name, this);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }


    }//PostgreCteBuilderImpl

    private enum FrameExclusion implements SQLWords {

        EXCLUDE_CURRENT_ROW(" EXCLUDE CURRENT ROW"),
        EXCLUDE_GROUP(" EXCLUDE GROUP"),
        EXCLUDE_TIES(" EXCLUDE TIES"),
        EXCLUDE_NO_OTHERS(" EXCLUDE NO OTHERS");

        private final String spaceWords;

        FrameExclusion(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(FrameExclusion.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//FrameExclusion


    private static final class PostgreWindow extends WindowClause<
            PostgreQuery._WindowOrderBySpec,
            PostgreQuery._PostgreFrameUnitSpec,
            PostgreQuery._PostgreFrameBetweenSpec,
            PostgreQuery._PostgreFrameEndExpBoundClause,
            PostgreQuery._PostgreFrameStartNonExpBoundClause,
            PostgreQuery._PostgreFrameStartExpBoundClause,
            PostgreQuery._PostgreFrameEndNonExpBoundClause>
            implements PostgreQuery._WindowPartitionBySpec
            , PostgreQuery._PostgreFrameBetweenSpec
            , PostgreQuery._PostgreFrameStartExpBoundClause
            , PostgreQuery._PostgreFrameStartNonExpBoundClause
            , PostgreQuery._PostgreFrameEndExpBoundClause
            , PostgreQuery._PostgreFrameEndNonExpBoundClause
            , PostgreQuery._PostgreFrameBetweenAndClause
            , PostgreQuery._FrameExclusionSpec
            , WindowClause.FrameExclusionSpec {

        private FrameExclusion frameExclusion;

        /**
         * @see #postgreNamedWindow(String, CriteriaContext, String)
         */
        private PostgreWindow(String windowName, CriteriaContext context, @Nullable String existingWindowName) {
            super(windowName, context, existingWindowName);
        }

        /**
         * @see #postgreAnonymousWindow(CriteriaContext, String)
         */
        private PostgreWindow(CriteriaContext context, @Nullable String existingWindowName) {
            super(context, existingWindowName);
        }

        @Override
        public PostgreQuery._PostgreFrameBetweenSpec groups() {
            return this.frameUnit(FrameUnits.GROUPS);
        }

        @Override
        public PostgreQuery._PostgreFrameBetweenSpec ifGroups(BooleanSupplier predicate) {
            return this.ifFrameUnit(predicate, FrameUnits.GROUPS);
        }

        @Override
        public PostgreQuery._PostgreFrameEndExpBoundClause groups(Expression expression) {
            return this.frameUnit(FrameUnits.GROUPS, expression);
        }

        @Override
        public PostgreQuery._PostgreFrameEndExpBoundClause groups(Supplier<Expression> supplier) {
            return this.frameUnit(FrameUnits.GROUPS, supplier.get());
        }

        @Override
        public <E> PostgreQuery._PostgreFrameEndExpBoundClause groups(Function<E, Expression> valueOperator
                , @Nullable E value) {
            return this.frameUnit(FrameUnits.GROUPS, valueOperator.apply(value));
        }

        @Override
        public <E> PostgreQuery._PostgreFrameEndExpBoundClause groups(Function<E, Expression> valueOperator
                , Supplier<E> supplier) {
            return this.frameUnit(FrameUnits.GROUPS, valueOperator.apply(supplier.get()));
        }

        @Override
        public PostgreQuery._PostgreFrameEndExpBoundClause groups(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return this.frameUnit(FrameUnits.GROUPS, valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public PostgreQuery._PostgreFrameEndExpBoundClause ifGroups(Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.frameUnit(FrameUnits.GROUPS, expression);
            }
            return this;
        }

        @Override
        public <E> PostgreQuery._PostgreFrameEndExpBoundClause ifGroups(Function<E, Expression> valueOperator
                , @Nullable E value) {
            if (value != null) {
                this.frameUnit(FrameUnits.GROUPS, valueOperator.apply(value));
            }
            return this;
        }

        @Override
        public <E> PostgreQuery._PostgreFrameEndExpBoundClause ifGroups(Function<E, Expression> valueOperator
                , Supplier<E> supplier) {
            final E value;
            if ((value = supplier.get()) != null) {
                this.frameUnit(FrameUnits.GROUPS, valueOperator.apply(value));
            }
            return this;
        }

        @Override
        public PostgreQuery._PostgreFrameEndExpBoundClause ifGroups(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            final Object value;
            if ((value = function.apply(keyName)) != null) {
                this.frameUnit(FrameUnits.GROUPS, valueOperator.apply(value));
            }
            return this;
        }

        @Override
        public PostgreWindow currentRow() {
            this.bound(FrameBound.CURRENT_ROW);
            return this;
        }

        @Override
        public PostgreWindow unboundedPreceding() {
            this.bound(FrameBound.UNBOUNDED_PRECEDING);
            return this;
        }

        @Override
        public PostgreWindow unboundedFollowing() {
            this.bound(FrameBound.UNBOUNDED_FOLLOWING);
            return this;
        }

        @Override
        public PostgreWindow preceding() {
            this.bound(FrameBound.PRECEDING);
            return this;
        }

        @Override
        public PostgreWindow following() {
            this.bound(FrameBound.FOLLOWING);
            return this;
        }

        @Override
        public PostgreWindow excludeCurrentRow() {
            this.frameExclusion = FrameExclusion.EXCLUDE_CURRENT_ROW;
            return this;
        }

        @Override
        public PostgreWindow excludeGroup() {
            this.frameExclusion = FrameExclusion.EXCLUDE_GROUP;
            return this;
        }

        @Override
        public PostgreWindow excludeTies() {
            this.frameExclusion = FrameExclusion.EXCLUDE_TIES;
            return this;
        }

        @Override
        public PostgreWindow excludeNoOthers() {
            this.frameExclusion = FrameExclusion.EXCLUDE_NO_OTHERS;
            return this;
        }

        @Override
        public PostgreWindow ifExcludeCurrentRow(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.frameExclusion = FrameExclusion.EXCLUDE_CURRENT_ROW;
            } else {
                this.frameExclusion = null;
            }
            return this;
        }

        @Override
        public PostgreWindow ifExcludeGroup(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.frameExclusion = FrameExclusion.EXCLUDE_GROUP;
            } else {
                this.frameExclusion = null;
            }
            return this;
        }

        @Override
        public PostgreWindow ifExcludeTies(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.frameExclusion = FrameExclusion.EXCLUDE_TIES;
            } else {
                this.frameExclusion = null;
            }
            return this;
        }

        @Override
        public PostgreWindow ifExcludeNoOthers(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.frameExclusion = FrameExclusion.EXCLUDE_NO_OTHERS;
            } else {
                this.frameExclusion = null;
            }
            return this;
        }

        @Override
        public void appendFrameExclusion(final _SqlContext context) {
            final FrameExclusion exclusion = this.frameExclusion;
            if (exclusion != null) {
                context.sqlBuilder().append(exclusion);
            }
        }


    }//PostgreWindow


}
