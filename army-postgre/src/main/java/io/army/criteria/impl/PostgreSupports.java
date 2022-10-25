package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.postgre._PostgreCteStatement;
import io.army.criteria.postgre.*;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;


abstract class PostgreSupports extends CriteriaSupports {


    private PostgreSupports() {
    }


    static final List<Selection> RETURNING_ALL = Collections.emptyList();

    static PostgreCteBuilder cteBuilder(final boolean recursive, final CriteriaContext context) {
        return new PostgreCteBuilderImpl(recursive, context);
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
            return _StringUtils.builder()
                    .append(MaterializedOption.class.getSimpleName())
                    .append(this.name())
                    .toString();
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


    private static abstract class PostgreDynamicDmlCteLeftParenClause<I extends Item>
            extends ParenStringConsumerClause<Statement._StaticAsClaus<I>>
            implements DialectStatement._SimpleCteLeftParenSpec<I>
            , Statement._StaticAsClaus<I>
            , Statement._AsCteClause<PostgreCteBuilder> {

        private final String name;

        final PostgreCteBuilderImpl cteBuilder;

        private List<String> columnAliasList;

        private PostgreDynamicDmlCteLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(cteBuilder.context);
            this.name = name;
            this.cteBuilder = cteBuilder;
        }


        @Override
        public final PostgreCteBuilder asCte() {
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

        final Statement._AsCteClause<PostgreCteBuilder> subStmtEnd(final SubStatement stmt) {
            CriteriaUtils.createAndAddCte(this.cteBuilder.context, this.name, this.columnAliasList, stmt);
            return this;
        }

    }//PostgreDynamicDmlCteLeftParenClause


    private static final class PostgreDynamicInsertLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreInsert._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreInsert._DynamicSubInsertSpec {

        private PostgreDynamicInsertLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }

        @Override
        public PostgreInsert._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
            return PostgreInserts.dynamicSubInsert(this.context, this::subStmtEnd);
        }

    }//PostgreDynamicInsertLeftParenClause

    private static final class PostgreDynamicUpdateLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreUpdate._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreUpdate._DynamicCteUpdateSpec {

        private PostgreDynamicUpdateLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }


        @Override
        public PostgreUpdate._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
            return PostgreUpdates.dynamicCteUpdate(this.cteBuilder.context, this::subStmtEnd);
        }

    }//PostgreDynamicUpdateLeftParenClause

    private static final class PostgreDynamicDeleteLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreDelete._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreDelete._DynamicCteDeleteSpec {

        private PostgreDynamicDeleteLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }


        @Override
        public PostgreDelete._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
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


    private static final class PostgreCteSearchClause<I extends Item>
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

        private PostgreCteSearchClause(CriteriaContext context, SubStatement subStmt
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
                , StandardSyntax.WordDefault wordDefault, Expression cycleMarkDefault) {
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
            PostgreQuery._DynamicSubMaterializedSpec<PostgreStatement._CteSearchSpec<PostgreCteBuilder>>>
            implements PostgreQuery._DynamicCteQuerySpec {

        private PostgreDynamicQueryLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }


        @Override
        public PostgreQuery._DynamicSubMaterializedSpec<PostgreStatement._CteSearchSpec<PostgreCteBuilder>> as() {
            return PostgreQueries.dynamicCteQuery(this.cteBuilder.context, this::subQueryEnd);
        }

        private PostgreStatement._CteSearchSpec<PostgreCteBuilder> subQueryEnd(SubStatement subQuery) {
            return new PostgreCteSearchClause<>(this.cteBuilder.context, subQuery, this::searchOptionEnd);
        }

        private PostgreCteBuilder searchOptionEnd(SubStatement statement) {
            //TODO 将 search_seq_col_name 等加入 输出列
            return this.subStmtEnd(statement)
                    .asCte();
        }


    }//PostgreDynamicQueryLeftParenClause


    private static final class PostgreDynamicValuesLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreValues._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreValues._DynamicCteValuesSpec {

        private PostgreDynamicValuesLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }

        @Override
        public PostgreValues._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
            return PostgreValuesStatements.dynamicCteValues(this.cteBuilder.context, this::subStmtEnd);
        }

    }//PostgreDynamicValuesLeftParenClause


    private static final class PostgreCteBuilderImpl implements PostgreCteBuilder {

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


}
