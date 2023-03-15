package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._RowSet;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner.postgre._PostgreCte;
import io.army.criteria.impl.inner.postgre._PostgreTableBlock;
import io.army.criteria.postgre.*;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.*;


abstract class PostgreSupports extends CriteriaSupports {


    private PostgreSupports() {
    }


    static final List<Selection> EMPTY_SELECTION_LIST = Collections.emptyList();

    static PostgreCtes postgreCteBuilder(final boolean recursive, final CriteriaContext context) {
        return new PostgreCteBuilder(recursive, context);
    }

    static PostgreWindow namedWindow(String name, CriteriaContext context, @Nullable String existingWindowName) {
        return new PostgreWindow(name, context, existingWindowName);
    }

    static PostgreWindow anonymousWindow(CriteriaContext context, @Nullable String existingWindowName) {
        return new PostgreWindow(context, existingWindowName);
    }

    static <I extends Item> PostgreQuery._StaticCteSearchSpec<I> noOperationStaticCteSearchSpec(
            Function<String, PostgreQuery._StaticCteParensSpec<I>> cteFunction, Supplier<I> endSupplier) {
        return new NoOperationStaticCteSearchSpec<>(cteFunction, endSupplier);
    }




    @SuppressWarnings("unchecked")
    static abstract class PostgreTableOnBlock<TR, RR, OR> extends OnClauseTableBlock.OnItemTableBlock<OR>
            implements _PostgreTableBlock,
            PostgreStatement._StaticTableSampleClause<TR>,
            PostgreStatement._RepeatableClause<RR> {
        private ArmyExpression sampleMethod;

        private ArmyExpression seed;

        PostgreTableOnBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table, String alias, OR stmt) {
            super(joinType, modifier, table, alias, stmt);
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
        public final TR tableSample(
                BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
            return this.tableSample(method.apply(valueOperator, argument));
        }

        @Override
        public final <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method,
                                        BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            return this.tableSample(method.apply(valueOperator, supplier.get()));
        }

        @Override
        public final TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                    BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                    String keyName) {
            return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public final TR ifTableSample(Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.tableSample(expression);
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
        public final RR repeatable(Function<Number, Expression> valueOperator, Number seedValue) {
            return this.repeatable(valueOperator.apply(seedValue));
        }

        @Override
        public final <E extends Number> RR repeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
            return this.repeatable(valueOperator.apply(supplier.get()));
        }

        @Override
        public final RR repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                   String keyName) {
            return this.repeatable(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public final RR ifRepeatable(Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.repeatable(expression);
            }
            return (RR) this;
        }

        @Override
        public final <E extends Number> RR ifRepeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
            final E value;
            value = supplier.get();
            if (value != null) {
                this.repeatable(valueOperator.apply(value));
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                     String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.repeatable(valueOperator.apply(value));
            }
            return (RR) this;
        }

        @Override
        public final _Expression sampleMethod() {
            return this.sampleMethod;
        }

        @Override
        public final _Expression seed() {
            return this.seed;
        }


    }//PostgreTableOnBlock


    static final class PostgreNoOnTableBlock extends TableBlock.NoOnModifierTableBlock
            implements _PostgreTableBlock {

        private ArmyExpression sampleMethod;

        private ArmyExpression seed;

        PostgreNoOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table, String alias) {
            super(joinType, modifier, table, alias);
        }

        void onSampleMethod(ArmyExpression sampleMethod) {
            this.sampleMethod = sampleMethod;
        }

        void onSeed(ArmyExpression seed) {
            this.seed = seed;
        }

        @Override
        public _Expression sampleMethod() {
            return this.sampleMethod;
        }

        @Override
        public _Expression seed() {
            return this.seed;
        }

    }//PostgreNoOnTableBlock


    private static abstract class PostgreDynamicDmlCteLeftParenClause<I extends Item>
            extends ParenStringConsumerClause<Statement._StaticAsClaus<I>>
            implements DialectStatement._SimpleCteLeftParenSpec<I>
            , Statement._StaticAsClaus<I>
            , Statement._AsCteClause<PostgreCtes> {

        private final String name;

        final PostgreCteBuilder cteBuilder;

        private List<String> columnAliasList;

        private PostgreDynamicDmlCteLeftParenClause(String name, PostgreCteBuilder cteBuilder) {
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


    static final class PostgreCte implements _PostgreCte {

        private final String name;

        private final List<String> columnAliasList;

        private final PostgreSyntax.WordMaterialized modifier;

        private final SubStatement subStatement;


        private final List<Selection> selectionList;

        private final Map<String, Selection> selectionMap;

        private final _SearchClause searchClause;

        private final _CycleClause cycleClause;

        PostgreCte(String name, @Nullable List<String> columnAliasList,
                   @Nullable PostgreSyntax.WordMaterialized modifier, SubStatement subStatement) {
            this(name, columnAliasList, modifier, subStatement, null, null);
        }

        PostgreCte(String name, final @Nullable List<String> columnAliasList,
                   @Nullable PostgreSyntax.WordMaterialized modifier, SubStatement subStatement,
                   final @Nullable _SearchClause searchClause, final @Nullable _CycleClause cycleClause) {
            this.name = name;
            this.columnAliasList = _CollectionUtils.safeList(columnAliasList);
            this.modifier = modifier;
            this.subStatement = subStatement;
            this.searchClause = searchClause;
            this.cycleClause = cycleClause;

            final _Pair<List<Selection>, Map<String, Selection>> pair;
            pair = createPair(searchClause, cycleClause);
            this.selectionList = pair.first;
            this.selectionMap = pair.second;
        }


        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Selection selection(String derivedAlias) {
            return this.selectionMap.get(derivedAlias);
        }

        @Override
        public List<Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public SubStatement subStatement() {
            return this.subStatement;
        }

        @Override
        public List<String> columnAliasList() {
            return this.columnAliasList;
        }

        @Override
        public Postgres.WordMaterialized modifier() {
            return this.modifier;
        }

        @Override
        public _SearchClause searchClause() {
            return this.searchClause;
        }

        @Override
        public _CycleClause cycleClause() {
            return this.cycleClause;
        }


        private _Pair<List<Selection>, Map<String, Selection>> createPair(final @Nullable _SearchClause searchClause,
                                                                          final @Nullable _CycleClause cycleClause) {
            final List<? extends Selection> stmtSelections;
            if (this.subStatement instanceof RowSet) {
                stmtSelections = ((_RowSet) this.subStatement).selectionList();
            } else {
                stmtSelections = ((_Statement._ReturningListSpec) this.subStatement).returningList();
            }
            final int selectionSize;
            selectionSize = stmtSelections.size();
            int newSize = selectionSize;
            final Selection searchSeqSelection, cycleMarkSelection, cyclePathSelection;
            if (searchClause == null) {
                searchSeqSelection = null;
            } else {
                searchSeqSelection = searchClause.searchSeqSelection();
                newSize++;
            }

            if (cycleClause == null) {
                cycleMarkSelection = null;
                cyclePathSelection = null;
            } else {
                cycleMarkSelection = cycleClause.cycleMarkSelection();
                cyclePathSelection = cycleClause.cyclePathSelection();
                newSize += 2;
            }

            final List<String> columnAliasList = this.columnAliasList;


            final int aliasSize;
            if ((aliasSize = columnAliasList.size()) > 0 && aliasSize != selectionSize) {
                throw CriteriaUtils.cteColumnAliasNotMatch(selectionSize, aliasSize);
            }
            final List<Selection> selectionList = new ArrayList<>(newSize);
            final Map<String, Selection> selectionMap = new HashMap<>((int) (newSize / 0.75f));
            String alias;
            Selection selection;
            for (int i = 0; i < selectionSize; i++) {
                selection = stmtSelections.get(i);
                if (aliasSize == 0) {
                    alias = selection.alias();
                } else {
                    alias = columnAliasList.get(i);
                    selection = ArmySelections.renameSelection(selection, alias);
                }

                if (selectionMap.putIfAbsent(alias, selection) != null && aliasSize > 0) {
                    throw CriteriaUtils.duplicateColumnAlias(ContextStack.peek(), alias);
                }
                selectionList.add(selection);

            }

            if (searchSeqSelection != null) {
                if (selectionMap.putIfAbsent(searchSeqSelection.alias(), searchSeqSelection) != null) {
                    throw CriteriaUtils.duplicateColumnAlias(ContextStack.peek(), searchSeqSelection.alias());
                }
                selectionList.add(searchSeqSelection);
            }

            if (cycleMarkSelection != null) {
                if (selectionMap.putIfAbsent(cycleMarkSelection.alias(), cycleMarkSelection) != null) {
                    throw CriteriaUtils.duplicateColumnAlias(ContextStack.peek(), cycleMarkSelection.alias());
                }
                selectionList.add(cycleMarkSelection);

                if (selectionMap.putIfAbsent(cyclePathSelection.alias(), cyclePathSelection) != null) {
                    throw CriteriaUtils.duplicateColumnAlias(ContextStack.peek(), cyclePathSelection.alias());
                }
                selectionList.add(cyclePathSelection);
            }

            assert selectionList.size() == selectionMap.size();
            return _Pair.create(
                    Collections.unmodifiableList(selectionList),
                    Collections.unmodifiableMap(selectionMap)
            );
        }


    }//PostgreCte


    private static abstract class NoActionCteSearchSpec<SR extends Item, CR extends Item>
            implements PostgreQuery._CteSearchClause<SR>,
            PostgreQuery._SearchFirstByClause<SR>,
            PostgreQuery._CteCycleClause<CR>,
            PostgreQuery._CycleToMarkValueSpec<CR>,
            PostgreQuery._CyclePathColumnClause<CR> {

        @Override
        public final CR using(String cyclePathColumnName) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final CR using(Supplier<String> supplier) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._CyclePathColumnClause<CR> to(Expression cycleMarkValue, SQLs.WordDefault wordDefault,
                                                                Expression cycleMarkDefault) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._CyclePathColumnClause<CR> to(Consumer<BiConsumer<Expression, Expression>> consumer) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._CyclePathColumnClause<CR> ifTo(Consumer<BiConsumer<Expression, Expression>> consumer) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SetCycleMarkColumnClause<CR> cycle(String firstColumnName, String... rest) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SetCycleMarkColumnClause<CR> cycle(Consumer<Consumer<String>> consumer) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SetCycleMarkColumnClause<CR> ifCycle(Consumer<Consumer<String>> consumer) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SetSearchSeqColumnClause<SR> firstBy(String firstColumnName, String... rest) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SetSearchSeqColumnClause<SR> firstBy(Consumer<Consumer<String>> consumer) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SearchFirstByClause<SR> searchBreadth() {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SearchFirstByClause<SR> searchDepth() {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SearchFirstByClause<SR> ifSearchBreadth(BooleanSupplier predicate) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

        @Override
        public final PostgreQuery._SearchFirstByClause<SR> ifSearchDepth(BooleanSupplier predicate) {
            throw NoOperationStaticCteSearchSpec.errorOperation();
        }

    }//NoActionCteSearchSpec


    private static final class NoOperationStaticCteSearchSpec<I extends Item> extends NoActionCteSearchSpec<
            PostgreQuery._StaticCteCycleSpec<I>,
            PostgreQuery._CteComma<I>>
            implements PostgreQuery._StaticCteSearchSpec<I>,
            PostgreQuery._SetSearchSeqColumnClause<PostgreQuery._StaticCteCycleSpec<I>>,
            PostgreQuery._SetCycleMarkColumnClause<PostgreQuery._CteComma<I>> {

        private final Function<String, PostgreQuery._StaticCteParensSpec<I>> cteFunction;

        private final Supplier<I> endSupplier;


        private NoOperationStaticCteSearchSpec(Function<String, PostgreQuery._StaticCteParensSpec<I>> cteFunction,
                                               Supplier<I> endSupplier) {
            this.cteFunction = cteFunction;
            this.endSupplier = endSupplier;
        }


        @Override
        public PostgreQuery._StaticCteParensSpec<I> comma(String name) {
            return this.cteFunction.apply(name);
        }

        @Override
        public I space() {
            return this.endSupplier.get();
        }

        @Override
        public NoOperationStaticCteSearchSpec<I> set(String searchSeqColumnName) {
            throw errorOperation();
        }

        @Override
        public NoOperationStaticCteSearchSpec<I> set(Supplier<String> supplier) {
            throw errorOperation();
        }

        private static CriteriaException errorOperation() {
            return ContextStack.clearStackAndCriteriaError(errorMessage());
        }

        private static String errorMessage() {
            return "Not recursive union query couldn't use SEARCH or CYCLE clause.";
        }

    } //NoOperationStaticCteSearchSpec


    @SuppressWarnings("unchecked")
    static abstract class PostgreCteCycleClause<I extends Item>
            implements PostgreQuery._CteCycleClause<I>,
            PostgreQuery._SetCycleMarkColumnClause<I>,
            PostgreQuery._CycleToMarkValueSpec<I>,
            PostgreQuery._CyclePathColumnClause<I>,
            _PostgreCte._CycleClause {

        private final CriteriaContext context;

        private List<String> cycleColumnList;

        private String cycleMarkColumnName;

        private ArmyExpression cycleMarkValue;

        private ArmyExpression cycleMarkDefault;

        private String cyclePathColumnName;

        PostgreCteCycleClause(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final List<String> cycleColumnList = this.cycleColumnList;
            if (cycleColumnList == null) {
                return;
            }
            final int columnSize = cycleColumnList.size();
            assert columnSize > 0;

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" CYCLE ");

            final DialectParser parser;
            parser = context.parser();

            for (int i = 0; i < columnSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                }
                parser.identifier(cycleColumnList.get(i), sqlBuilder);
            }

            final String cycleMarkColumnName = this.cycleMarkColumnName;
            if (cycleMarkColumnName == null) {
                throw _Exceptions.castCriteriaApi();
            }
            sqlBuilder.append(_Constant.SPACE_SET_SPACE);
            parser.identifier(cycleMarkColumnName, sqlBuilder);

            final ArmyExpression cycleMarkValue = this.cycleMarkValue, cycleMarkDefault = this.cycleMarkDefault;
            if (cycleMarkValue != null) {
                assert cycleMarkDefault != null;
                sqlBuilder.append(" TO");
                cycleMarkValue.appendSql(context);
                sqlBuilder.append(_Constant.SPACE_DEFAULT);
                cycleMarkDefault.appendSql(context);
            }

            final String cyclePathColumnName = this.cyclePathColumnName;
            if (cyclePathColumnName == null) {
                throw _Exceptions.castCriteriaApi();
            }
            sqlBuilder.append(_Constant.SPACE_USING);
            parser.identifier(cyclePathColumnName, sqlBuilder);

        }

        @Override
        public final PostgreQuery._SetCycleMarkColumnClause<I> cycle(String firstColumnName, String... rest) {
            this.cycleColumnList = _ArrayUtils.unmodifiableListOf(firstColumnName, rest);
            return this;
        }

        @Override
        public final PostgreQuery._SetCycleMarkColumnClause<I> cycle(Consumer<Consumer<String>> consumer) {
            this.cycleColumnList = CriteriaUtils.stringList(this.context, true, consumer);
            return this;
        }

        @Override
        public final PostgreQuery._SetCycleMarkColumnClause<I> ifCycle(Consumer<Consumer<String>> consumer) {
            final List<String> list;
            list = CriteriaUtils.stringList(this.context, false, consumer);
            this.cycleColumnList = list.size() > 0 ? list : null;
            return this;
        }

        @Override
        public final PostgreQuery._CycleToMarkValueSpec<I> set(final @Nullable String cycleMarkColumnName) {
            if (this.cycleColumnList == null) {
                this.cycleMarkColumnName = null;
            } else if (cycleMarkColumnName == null) {
                throw ContextStack.nullPointer(this.context);
            } else {
                this.cycleMarkColumnName = cycleMarkColumnName;
            }
            return this;
        }

        @Override
        public final PostgreQuery._CycleToMarkValueSpec<I> set(Supplier<String> supplier) {

            if (this.cycleColumnList == null) {
                this.cycleMarkColumnName = null;
            } else {
                final String columnName;
                columnName = supplier.get();
                if (columnName == null) {
                    throw ContextStack.nullPointer(this.context);
                }
                this.cycleMarkColumnName = columnName;
            }
            return this;
        }

        @Override
        public final PostgreQuery._CyclePathColumnClause<I> to(final @Nullable Expression cycleMarkValue,
                                                               SQLs.WordDefault wordDefault,
                                                               final @Nullable Expression cycleMarkDefault) {
            if (this.cycleColumnList == null) {
                this.cycleMarkValue = null;
                this.cycleMarkDefault = null;
            } else {
                this.cycleTo(cycleMarkValue, cycleMarkDefault);
            }
            return this;
        }

        @Override
        public final PostgreQuery._CyclePathColumnClause<I> to(Consumer<BiConsumer<Expression, Expression>> consumer) {
            if (this.cycleColumnList == null) {
                this.cycleMarkValue = null;
                this.cycleMarkDefault = null;
            } else {
                consumer.accept(this::cycleTo);
                if (this.cycleMarkValue == null || this.cycleMarkDefault == null) {
                    throw ContextStack.criteriaError(this.context, "You don't add TO clause");
                }
            }
            return this;
        }

        @Override
        public final PostgreQuery._CyclePathColumnClause<I> ifTo(Consumer<BiConsumer<Expression, Expression>> consumer) {
            if (this.cycleColumnList == null) {
                this.cycleMarkValue = null;
                this.cycleMarkDefault = null;
            } else {
                consumer.accept(this::cycleTo);
            }
            return this;
        }


        @Override
        public final I using(final @Nullable String cyclePathColumnName) {
            if (this.cycleColumnList == null) {
                this.cyclePathColumnName = null;
            } else if (cyclePathColumnName == null) {
                throw ContextStack.nullPointer(this.context);
            } else {
                this.cyclePathColumnName = cyclePathColumnName;
            }
            return (I) this;
        }

        @Override
        public final I using(Supplier<String> supplier) {
            if (this.cycleColumnList == null) {
                this.cyclePathColumnName = null;
            } else {
                this.using(supplier.get());
            }
            return (I) this;
        }

        @Override
        public final Selection cycleMarkSelection() {
            final String s = this.cycleMarkColumnName;
            assert s != null;
            return ArmySelections.forName(s, BooleanType.INSTANCE);// TODO optimizing
        }

        @Override
        public final Selection cyclePathSelection() {
            final String s = this.cyclePathColumnName;
            assert s != null;
            return ArmySelections.forName(s, StringType.INSTANCE);// TODO optimizing
        }

        final boolean hasCycleClause() {
            return this.cycleColumnList != null;
        }

        private void cycleTo(final @Nullable Expression cycleMarkValue,
                             final @Nullable Expression cycleMarkDefault) {
            if (cycleMarkValue == null || cycleMarkDefault == null) {
                throw ContextStack.nullPointer(this.context);
            } else {
                this.cycleMarkValue = (ArmyExpression) cycleMarkValue;
                this.cycleMarkDefault = (ArmyExpression) cycleMarkDefault;
            }
        }

    }// PostgreCteCycleClause


    @SuppressWarnings("unchecked")
    static abstract class PostgreCteSearchSpec<SR extends Item>
            implements PostgreQuery._CteSearchClause<SR>,
            PostgreQuery._SearchFirstByClause<SR>,
            PostgreQuery._SetSearchSeqColumnClause<SR>,
            _PostgreCte._SearchClause {

        final CriteriaContext context;

        private CteSearchOption searchOption;

        private List<String> firstByColumnList;

        private String searchSeqColumnName;

        PostgreCteSearchSpec(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final CteSearchOption searchOption = this.searchOption;
            if (searchOption == null) {
                return;
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            final DialectParser parser;
            parser = context.parser();

            sqlBuilder.append(searchOption.spaceWord);
            final List<String> firstByColumnList = this.firstByColumnList;
            if (firstByColumnList == null) {
                throw _Exceptions.castCriteriaApi();
            }
            final int columnSize = firstByColumnList.size();
            assert columnSize > 0;
            sqlBuilder.append(" FIRST BY ");
            for (int i = 0; i < columnSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                }
                parser.identifier(firstByColumnList.get(i), sqlBuilder);
            }
            sqlBuilder.append(_Constant.SPACE_SET_SPACE);
            final String searchSeqColumnName = this.searchSeqColumnName;
            if (searchSeqColumnName == null) {
                throw _Exceptions.castCriteriaApi();
            }
            parser.identifier(searchSeqColumnName, sqlBuilder);

        }


        @Override
        public final PostgreQuery._SearchFirstByClause<SR> searchBreadth() {
            this.searchOption = CteSearchOption.BREADTH;
            return this;
        }

        @Override
        public final PostgreQuery._SearchFirstByClause<SR> searchDepth() {
            this.searchOption = CteSearchOption.DEPTH;
            return this;
        }

        @Override
        public final PostgreQuery._SearchFirstByClause<SR> ifSearchBreadth(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.searchOption = CteSearchOption.BREADTH;
            } else {
                this.searchOption = null;
            }
            return this;
        }

        @Override
        public final PostgreQuery._SearchFirstByClause<SR> ifSearchDepth(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.searchOption = CteSearchOption.DEPTH;
            } else {
                this.searchOption = null;
            }
            return this;
        }

        @Override
        public final PostgreQuery._SetSearchSeqColumnClause<SR> firstBy(String firstColumnName, String... rest) {
            if (this.searchOption == null) {
                this.firstByColumnList = null;
            } else {
                this.firstByColumnList = _ArrayUtils.unmodifiableListOf(firstColumnName, rest);
            }
            return this;
        }

        @Override
        public final PostgreQuery._SetSearchSeqColumnClause<SR> firstBy(Consumer<Consumer<String>> consumer) {
            if (this.searchOption == null) {
                this.firstByColumnList = null;
            } else {
                this.firstByColumnList = CriteriaUtils.stringList(this.context, true, consumer);
            }
            return this;
        }

        @Override
        public final SR set(final @Nullable String searchSeqColumnName) {
            if (this.searchOption == null) {
                this.searchSeqColumnName = null;
            } else if (searchSeqColumnName == null) {
                throw ContextStack.nullPointer(this.context);
            } else {
                this.searchSeqColumnName = searchSeqColumnName;
            }
            return (SR) this;
        }

        @Override
        public final SR set(Supplier<String> supplier) {
            final String columnName;
            if (this.searchOption == null) {
                this.searchSeqColumnName = null;
            } else if ((columnName = supplier.get()) == null) {
                throw ContextStack.nullPointer(this.context);
            } else {
                this.searchSeqColumnName = columnName;
            }
            return (SR) this;
        }


        @Override
        public final Selection searchSeqSelection() {
            final String s = this.searchSeqColumnName;
            assert s != null;
            return ArmySelections.forName(s, StringType.INSTANCE);//TODO optimizing
        }

        final boolean hasSearchClause() {
            return this.searchOption != null;
        }


    }//PostgreCteSearchSpec


    private static final class NonOperationDynamicCteSearchSpec implements PostgreQuery._DynamicCteSearchSpec {

        private final PostgreCteBuilder builder;

        private NonOperationDynamicCteSearchSpec(PostgreCteBuilder builder) {
            this.builder = builder;
        }

        @Override
        public PostgreQuery._SetCycleMarkColumnClause<DialectStatement._CommaClause<PostgreCtes>> cycle(String firstColumnName, String... rest) {
            throw errorOperation();
        }

        @Override
        public PostgreQuery._SetCycleMarkColumnClause<DialectStatement._CommaClause<PostgreCtes>> cycle(Consumer<Consumer<String>> consumer) {
            throw errorOperation();
        }

        @Override
        public PostgreQuery._SetCycleMarkColumnClause<DialectStatement._CommaClause<PostgreCtes>> ifCycle(Consumer<Consumer<String>> consumer) {
            throw errorOperation();
        }

        @Override
        public PostgreQuery._SearchFirstByClause<PostgreQuery._DynamicCteCycleSpec> searchBreadth() {
            throw errorOperation();
        }

        @Override
        public PostgreQuery._SearchFirstByClause<PostgreQuery._DynamicCteCycleSpec> searchDepth() {
            throw errorOperation();
        }

        @Override
        public PostgreQuery._SearchFirstByClause<PostgreQuery._DynamicCteCycleSpec> ifSearchBreadth(BooleanSupplier predicate) {
            throw errorOperation();
        }

        @Override
        public PostgreQuery._SearchFirstByClause<PostgreQuery._DynamicCteCycleSpec> ifSearchDepth(BooleanSupplier predicate) {
            throw errorOperation();
        }

        @Override
        public PostgreCtes comma() {
            return this.builder;
        }

        private CriteriaException errorOperation() {
            return ContextStack.criteriaError(this.builder.context, NoOperationStaticCteSearchSpec.errorMessage());
        }

    }//NonOperationDynamicCteSearchSpec


    private static final class DynamicCteSearchSpec
            extends PostgreCteSearchSpec<PostgreQuery._DynamicCteCycleSpec>
            implements PostgreQuery._DynamicCteSearchSpec {

        private final PostgreCteBuilder builder;

        private final SubQuery query;

        private final Function<DynamicCteSearchSpec, PostgreCtes> function;

        private DynamicCteCycleSpec cycleSpec;

        /**
         * @see DynamicQueryParensClause#subQueryEnd(SubQuery)
         */
        private DynamicCteSearchSpec(PostgreCteBuilder builder, SubQuery query,
                                     Function<DynamicCteSearchSpec, PostgreCtes> function) {
            super(builder.context);
            this.builder = builder;
            this.query = query;
            this.function = function;

            builder.lastQueryCteEnder = this; // store callback
        }


        @Override
        public PostgreQuery._SetCycleMarkColumnClause<DialectStatement._CommaClause<PostgreCtes>> cycle(String firstColumnName, String... rest) {
            return this.createCycleSpec().cycle(firstColumnName, rest);
        }

        @Override
        public PostgreQuery._SetCycleMarkColumnClause<DialectStatement._CommaClause<PostgreCtes>> cycle(Consumer<Consumer<String>> consumer) {
            return this.createCycleSpec().cycle(consumer);
        }

        @Override
        public PostgreQuery._SetCycleMarkColumnClause<DialectStatement._CommaClause<PostgreCtes>> ifCycle(Consumer<Consumer<String>> consumer) {
            return this.createCycleSpec().ifCycle(consumer);
        }

        @Override
        public PostgreCtes comma() {
            this.builder.lastQueryCteEnder = null;// must clear
            return this.function.apply(this);
        }

        private DynamicCteCycleSpec createCycleSpec() {
            if (this.cycleSpec != null) {
                throw ContextStack.castCriteriaApi(this.builder.context);
            }
            final DynamicCteCycleSpec spec;
            spec = new DynamicCteCycleSpec(this.builder.context, this::comma);
            this.cycleSpec = spec;
            return spec;
        }


    }//DynamicCteSearchSpec

    private static final class DynamicCteCycleSpec
            extends PostgreSupports.PostgreCteCycleClause<DialectStatement._CommaClause<PostgreCtes>>
            implements PostgreQuery._DynamicCteCycleSpec {

        private final Supplier<PostgreCtes> ender;

        private DynamicCteCycleSpec(CriteriaContext context, Supplier<PostgreCtes> ender) {
            super(context);
            this.ender = ender;
        }

        @Override
        public PostgreCtes comma() {
            return this.ender.get();
        }

    }//DynamicCteCycleSpec


    private static final class DynamicCteInsertParensSpec extends CteParensClause<PostgreInsert._DynamicCteAsClause>
            implements PostgreInsert._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subSingleInsert(String)
         */
        private DynamicCteInsertParensSpec(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(Function<PostgreInsert._DynamicSubOptionSpec<DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            return this.as(null, function);
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(@Nullable Postgres.WordMaterialized modifier,
                                                             Function<PostgreInsert._DynamicSubOptionSpec<DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            this.modifier = modifier;
            return function.apply(PostgreInserts.dynamicCteInsert(this.context, this::subInsertEnd));
        }

        private DialectStatement._CommaClause<PostgreCtes> subInsertEnd(final SubStatement statement) {
            final PostgreCte cte;
            cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.context.onAddCte(cte);
            return this.builder;
        }


    }//DynamicCteInsertParensSpec


    private static final class DynamicUpdateParensClause extends CteParensClause<PostgreUpdate._DynamicCteAsClause>
            implements PostgreUpdate._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subSingleUpdate(String)
         */
        private DynamicUpdateParensClause(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(Function<PostgreUpdate._SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            return this.as(null, function);
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(@Nullable Postgres.WordMaterialized modifier,
                                                             Function<PostgreUpdate._SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            this.modifier = modifier;
            return function.apply(PostgreUpdates.subSimpleUpdate(this.context, this::subUpdateEnd));
        }


        private DialectStatement._CommaClause<PostgreCtes> subUpdateEnd(final SubStatement statement) {
            final PostgreCte cte;
            cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.context.onAddCte(cte);
            return this.builder;
        }


    }//DynamicUpdateParensClause

    private static final class DynamicDeleteParensClause extends CteParensClause<PostgreDelete._DynamicCteAsClause>
            implements PostgreDelete._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subSingleDelete(String)
         */
        private DynamicDeleteParensClause(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(Function<PostgreDelete._SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            return this.as(null, function);
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(@Nullable Postgres.WordMaterialized modifier,
                                                             Function<PostgreDelete._SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            this.modifier = modifier;
            return function.apply(PostgreDeletes.subSimpleDelete(this.context, this::subDeleteEnd));
        }

        private DialectStatement._CommaClause<PostgreCtes> subDeleteEnd(final SubStatement statement) {
            final PostgreCte cte;
            cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.context.onAddCte(cte);
            return this.builder;
        }


    }//DynamicDeleteParensClause


    private static final class DynamicQueryParensClause extends CteParensClause<PostgreQuery._DynamicCteAsClause>
            implements PostgreQuery._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subQuery(String)
         */
        private DynamicQueryParensClause(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }

        @Override
        public PostgreQuery._DynamicCteSearchSpec as(Function<PostgreQuery._WithSpec<PostgreQuery._DynamicCteSearchSpec>, PostgreQuery._DynamicCteSearchSpec> function) {
            return this.as(null, function);
        }

        @Override
        public PostgreQuery._DynamicCteSearchSpec as(@Nullable Postgres.WordMaterialized materialized, Function<PostgreQuery._WithSpec<PostgreQuery._DynamicCteSearchSpec>, PostgreQuery._DynamicCteSearchSpec> function) {
            this.modifier = materialized;
            return function.apply(PostgreQueries.subQuery(this.builder.context, this::subQueryEnd));
        }

        private PostgreQuery._DynamicCteSearchSpec subQueryEnd(final SubQuery query) {
            final PostgreQuery._DynamicCteSearchSpec spec;
            if (this.builder.recursive && PostgreUtils.isUnionQuery(query)) {
                spec = new DynamicCteSearchSpec(this.builder, query, this::searchClauseEnd);
            } else {
                spec = this.cteEndWithoutSearch(query);
            }
            return spec;
        }


        private PostgreQuery._DynamicCteSearchSpec cteEndWithoutSearch(final SubQuery query) {
            final PostgreCte cte;
            cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, query);
            this.context.onAddCte(cte);
            return new NonOperationDynamicCteSearchSpec(this.builder);
        }


        PostgreCtes searchClauseEnd(DynamicCteSearchSpec spec) {
            final _PostgreCte._SearchClause searchClause;
            searchClause = spec.hasSearchClause() ? spec : null;

            final _Cte cte;
            cte = new PostgreSupports.PostgreCte(this.name, this.columnAliasList, this.modifier, spec.query,
                    searchClause, spec.cycleSpec);
            this.builder.context.onAddCte(cte);
            return this.builder;
        }


    }//DynamicQueryParensClause


    private static final class DynamicValuesParensClause extends CteParensClause<PostgreValues._DynamicCteAsClause>
            implements PostgreValues._DynamicCteParensSpec {

        private final PostgreCteBuilder builder;

        private Postgres.WordMaterialized modifier;

        /**
         * @see PostgreCteBuilder#subValues(String)
         */
        private DynamicValuesParensClause(String name, PostgreCteBuilder builder) {
            super(name, builder.context);
            this.builder = builder;
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(Function<PostgreValues._WithSpec<DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            return this.as(null, function);
        }

        @Override
        public DialectStatement._CommaClause<PostgreCtes> as(@Nullable Postgres.WordMaterialized modifier,
                                                             Function<PostgreValues._WithSpec<DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function) {
            this.modifier = modifier;
            return function.apply(PostgreValuesStmt.subValues(this.context, this::subValuesEnd));
        }

        private DialectStatement._CommaClause<PostgreCtes> subValuesEnd(final SubValues statement) {
            final PostgreCte cte;
            cte = new PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.context.onAddCte(cte);
            return this.builder;
        }


    }//DynamicValuesParensClause

    private static final class PostgreCteBuilder implements PostgreCtes, CteBuilder,
            DialectStatement._CommaClause<PostgreCtes> {

        private final boolean recursive;

        private final CriteriaContext context;

        /**
         * @see DynamicCteSearchSpec#comma()
         * @see DynamicCteSearchSpec#DynamicCteSearchSpec(PostgreCteBuilder, SubQuery, Function)
         * @see DynamicQueryParensClause#subQueryEnd(SubQuery)
         */
        private DialectStatement._CommaClause<?> lastQueryCteEnder;

        private PostgreCteBuilder(final boolean recursive, CriteriaContext context) {
            context.onBeforeWithClause(recursive);
            this.recursive = recursive;
            this.context = context;

        }

        @Override
        public PostgreInsert._DynamicCteParensSpec subSingleInsert(String name) {
            this.endLastCte();

            return new DynamicCteInsertParensSpec(name, this);
        }

        @Override
        public PostgreUpdate._DynamicCteParensSpec subSingleUpdate(String name) {
            this.endLastCte();

            return new DynamicUpdateParensClause(name, this);
        }


        @Override
        public PostgreDelete._DynamicCteParensSpec subSingleDelete(String name) {
            this.endLastCte();

            return new DynamicDeleteParensClause(name, this);
        }

        @Override
        public PostgreQuery._DynamicCteParensSpec subQuery(String name) {
            this.endLastCte();

            return new DynamicQueryParensClause(name, this);
        }


        @Override
        public PostgreValues._DynamicCteParensSpec subValues(String name) {
            this.endLastCte();

            return new DynamicValuesParensClause(name, this);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public void endLastCte() {
            final DialectStatement._CommaClause<?> ender = this.lastQueryCteEnder;
            this.lastQueryCteEnder = null;
            if (ender != null) {
                ender.comma();
            }
        }

        @Override
        public PostgreCtes comma() {
            return this;
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
            return CriteriaUtils.sqlWordsToString(this);
        }

    }//FrameExclusion


    static final class PostgreWindow extends WindowClause<
            PostgreQuery._WindowOrderBySpec,
            PostgreQuery._PostgreFrameUnitSpec,
            PostgreQuery._PostgreFrameBetweenSpec,
            PostgreQuery._PostgreFrameEndExpBoundClause,
            PostgreQuery._PostgreFrameStartNonExpBoundClause,
            PostgreQuery._PostgreFrameStartExpBoundClause,
            PostgreQuery._PostgreFrameEndNonExpBoundClause>
            implements PostgreQuery._WindowPartitionBySpec,
            PostgreQuery._PostgreFrameBetweenSpec,
            PostgreQuery._PostgreFrameStartExpBoundClause,
            PostgreQuery._PostgreFrameStartNonExpBoundClause,
            PostgreQuery._PostgreFrameEndExpBoundClause,
            PostgreQuery._PostgreFrameEndNonExpBoundClause,
            PostgreQuery._PostgreFrameBetweenAndClause,
            PostgreQuery._FrameExclusionSpec,
            WindowClause.FrameExclusionSpec {

        private FrameExclusion frameExclusion;

        /**
         * @see #namedWindow(String, CriteriaContext, String)
         */
        private PostgreWindow(String windowName, CriteriaContext context, @Nullable String existingWindowName) {
            super(windowName, context, existingWindowName);
        }

        /**
         * @see #anonymousWindow(CriteriaContext, String)
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
        public PostgreQuery._PostgreFrameEndExpBoundClause groups(Function<Object, Expression> valueOperator,
                                                                  @Nullable Object value) {
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
