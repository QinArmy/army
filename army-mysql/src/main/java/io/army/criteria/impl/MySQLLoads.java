/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.mysql._MySQLLoadData;
import io.army.criteria.mysql.MySQLLoadData;
import io.army.meta.*;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._Collections;

import io.army.lang.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * @see MySQLs#loadDataStmt()
 */
abstract class MySQLLoads {

    private MySQLLoads() {
        throw new UnsupportedOperationException();
    }

    static <I extends Item> MySQLLoadData._LoadDataClause<I> loadDataCommand(Function<DmlCommand, I> function) {
        return new PrimaryLoadDataClause<>(function);
    }


    private enum StrategyOption implements SQLWords {

        REPLACE(" REPLACE"),
        IGNORE(" IGNORE");

        private final String words;

        StrategyOption(String words) {
            this.words = words;
        }

        @Override
        public final String spaceRender() {
            return this.words;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    } //StrategyOption

    @SuppressWarnings("unchecked")
    private static abstract class LoadDataClause<T extends LoadDataClause<T>> {

        final CriteriaContext context;

        private List<MySQLs.Modifier> modifierList;

        private Path fileName;

        private StrategyOption strategyOption;


        private LoadDataClause() {
            this.context = CriteriaContexts.otherPrimaryContext(MySQLUtils.DIALECT);
            ContextStack.push(this.context);
        }


        public final T loadData(MySQLs.Modifier local) {
            if (MySQLUtils.loadDataModifier(local) < 0) {
                String m = String.format("MySQL LOAD DATA don't support %s", local);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            this.modifierList = Collections.singletonList(local);
            return (T) this;
        }

        public final T loadData(List<MySQLs.Modifier> modifierList) {
            this.modifierList = MySQLUtils.asModifierList(this.context, modifierList, MySQLUtils::loadDataModifier);
            return (T) this;
        }


        public final T infile(final @Nullable Path filePath) {
            if (filePath == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (Files.notExists(filePath)) {
                String m = String.format("%s not exists ", filePath);
                throw ContextStack.clearStackAndCriteriaError(m);
            } else if (Files.isDirectory(filePath) || !Files.isReadable(filePath)) {
                String m = String.format("%s isn't readable file", filePath);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            this.fileName = filePath;
            return (T) this;
        }

        public final T infile(Supplier<Path> supplier) {
            return this.infile(supplier.get());
        }


        public final T replace() {
            this.strategyOption = StrategyOption.REPLACE;
            return (T) this;
        }


        public final T ignore() {
            this.strategyOption = StrategyOption.IGNORE;
            return (T) this;
        }


        public final T ifReplace(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.strategyOption = StrategyOption.REPLACE;
            } else {
                this.strategyOption = null;
            }
            return (T) this;
        }


        public final T ifIgnore(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.strategyOption = StrategyOption.IGNORE;
            } else {
                this.strategyOption = null;
            }
            return (T) this;
        }


    } // LoadDataClause

    private static final class PrimaryLoadDataClause<I extends Item>
            extends LoadDataClause<PrimaryLoadDataClause<I>>
            implements MySQLLoadData._LoadDataClause<I>,
            MySQLLoadData._LocalInfileClause<I>,
            MySQLLoadData._StrategyOptionSpec<I> {

        private final Function<DmlCommand, I> function;

        private PrimaryLoadDataClause(Function<DmlCommand, I> function) {
            this.function = function;
        }

        @Override
        public <T> MySQLLoadData._PartitionSpec<I, T> intoTable(final @Nullable SimpleTableMeta<T> table) {
            if (table == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return new PartitionClause<>(this, table, this::simpleStmtEnd);
        }

        @Override
        public <T> MySQLLoadData._PartitionSpec<MySQLLoadData._ChildLoadData<I, T>, T> intoTable(final @Nullable ParentTableMeta<T> table) {
            if (table == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return new PartitionClause<>(this, table, this::parentStmtEnd);
        }

        private I simpleStmtEnd(PartitionClause<?, ?> clause) {
            return this.function.apply(new SimpleLoadDataStatement(clause).asCommand());
        }

        private <T> MySQLLoadData._ChildLoadData<I, T> parentStmtEnd(PartitionClause<?, ?> clause) {
            final Statement._AsCommandClause<MySQLLoadData._ChildLoadData<I, T>> spec;
            spec = new ParentLoadDataStatement<>(clause, this.function);
            return spec.asCommand();
        }


    }//PrimaryLoadDataClause

    private static final class ChildLoadDataClause<I extends Item, P>
            extends LoadDataClause<ChildLoadDataClause<I, P>>
            implements MySQLLoadData._ChildLoadDataClause<I, P>,
            MySQLLoadData._ChildLocalInfileClause<I, P>,
            MySQLLoadData._ChildStrategyOptionSpec<I, P> {

        private final Function<PartitionClause<?, ?>, I> function;

        private ChildLoadDataClause(Function<PartitionClause<?, ?>, I> function) {
            this.function = function;
        }

        @Override
        public <T> MySQLLoadData._PartitionSpec<I, T> intoTable(ComplexTableMeta<P, T> table) {
            return new PartitionClause<>(this, table, this.function);
        }


    } //ChildLoadDataClause


    private static final class PartitionClause<I extends Item, T>
            extends InsertSupports.AssignmentSetClause<
            T,
            MySQLLoadData._LoadSetSpec<I, T>>
            implements MySQLLoadData._PartitionSpec<I, T>,
            MySQLLoadData._FieldsColumnsSpec<I, T>,
            MySQLLoadData._LinesSpec<I, T>,
            MySQLLoadData._ColumnTerminatedBySpec,
            MySQLLoadData._StartingBySpec {


        private final List<MySQLs.Modifier> modifierList;

        private final Path fileName;

        private final StrategyOption strategyOption;

        private final Function<PartitionClause<?, ?>, I> function;

        private List<String> partitionList;

        private String charsetName;

        private Boolean fieldsKeyWords;

        private String fieldsTerminatedBy;

        private boolean fieldsOptionally;

        private String fieldsEnclosedBy;

        private String fieldsEscapedBy;

        private boolean linesClause;

        private String linesStartingBy;

        private String linesTerminatedBy;

        private Long ignoreLine;

        private SQLWords ignoreLineWord;

        private List<_Expression> columnExpList;


        private PartitionClause(LoadDataClause<?> clause, TableMeta<T> insertTable,
                                Function<PartitionClause<?, ?>, I> function) {
            super(clause.context, insertTable);

            this.modifierList = clause.modifierList;
            this.fileName = clause.fileName;
            this.strategyOption = clause.strategyOption;
            this.function = function;

            if (this.modifierList == null
                    || this.fileName == null
                    || this.strategyOption == null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
        }

        @Override
        public MySQLLoadData._CharsetSpec<I, T> partition(String first, String... rest) {
            this.partitionList = ArrayUtils.unmodifiableListOf(first, rest);
            return this;
        }

        @Override
        public MySQLLoadData._CharsetSpec<I, T> partition(Consumer<Consumer<String>> consumer) {
            this.partitionList = CriteriaUtils.stringList(this.context, true, consumer);
            return this;
        }

        @Override
        public MySQLLoadData._CharsetSpec<I, T> ifPartition(Consumer<Consumer<String>> consumer) {
            this.partitionList = CriteriaUtils.stringList(this.context, false, consumer);
            return this;
        }

        @Override
        public MySQLLoadData._FieldsColumnsSpec<I, T> characterSet(@Nullable String charsetName) {
            if (charsetName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.charsetName = charsetName;
            return this;
        }

        @Override
        public MySQLLoadData._FieldsColumnsSpec<I, T> ifCharacterSet(Supplier<String> supplier) {
            this.charsetName = supplier.get();
            return this;
        }

        @Override
        public MySQLLoadData._LinesSpec<I, T> fields(Consumer<MySQLLoadData._ColumnTerminatedBySpec> consumer) {
            this.fieldsKeyWords = Boolean.TRUE;
            CriteriaUtils.invokeConsumer(this, consumer);
            if (this.fieldsTerminatedBy == null && this.fieldsEnclosedBy == null && this.fieldsEscapedBy == null) {
                throw dontAddFieldsClause();
            }
            return this;
        }

        @Override
        public MySQLLoadData._LinesSpec<I, T> columns(Consumer<MySQLLoadData._ColumnTerminatedBySpec> consumer) {
            this.fieldsKeyWords = Boolean.FALSE;
            CriteriaUtils.invokeConsumer(this, consumer);
            if (this.fieldsTerminatedBy == null && this.fieldsEnclosedBy == null && this.fieldsEscapedBy == null) {
                throw dontAddFieldsClause();
            }

            return this;
        }


        @Override
        public MySQLLoadData._LinesSpec<I, T> ifFields(Consumer<MySQLLoadData._ColumnTerminatedBySpec> consumer) {
            this.fieldsKeyWords = Boolean.TRUE;
            CriteriaUtils.invokeConsumer(this, consumer);
            if (this.fieldsTerminatedBy == null && this.fieldsEnclosedBy == null && this.fieldsEscapedBy == null) {
                this.fieldsKeyWords = null;
            }
            return this;
        }

        @Override
        public MySQLLoadData._LinesSpec<I, T> ifColumns(Consumer<MySQLLoadData._ColumnTerminatedBySpec> consumer) {
            this.fieldsKeyWords = Boolean.FALSE;
            CriteriaUtils.invokeConsumer(this, consumer);
            if (this.fieldsTerminatedBy == null && this.fieldsEnclosedBy == null && this.fieldsEscapedBy == null) {
                this.fieldsKeyWords = null;
            }
            return this;
        }

        @Override
        public MySQLLoadData._IgnoreLineSpec<I, T> lines(Consumer<MySQLLoadData._StartingBySpec> consumer) {
            this.linesClause = true;
            CriteriaUtils.invokeConsumer(this, consumer);
            if (this.linesStartingBy == null && this.linesTerminatedBy == null) {
                throw ContextStack.clearStackAndCriteriaError("You don't add any lines clause");
            }
            return this;
        }

        @Override
        public MySQLLoadData._IgnoreLineSpec<I, T> ifLines(Consumer<MySQLLoadData._StartingBySpec> consumer) {
            this.linesClause = true;
            CriteriaUtils.invokeConsumer(this, consumer);
            this.linesClause = this.linesStartingBy != null || this.linesTerminatedBy != null;
            return this;
        }

        @Override
        public MySQLLoadData._ColumnOrVarListSpec<I, T> ignore(long rowNumber, SQLs.LinesWord word) {
            if (word != SQLs.LINES && word != SQLs.ROWS) {
                throw CriteriaUtils.unknownWords(word);
            }
            this.ignoreLine = rowNumber;
            this.ignoreLineWord = (SQLWords) word;
            return this;
        }

        @Override
        public MySQLLoadData._ColumnOrVarListSpec<I, T> ignore(Supplier<Long> supplier, SQLs.LinesWord word) {
            return this.ignore(ClauseUtils.invokeSupplier(supplier), word);
        }

        @Override
        public MySQLLoadData._ColumnOrVarListSpec<I, T> ifIgnore(Supplier<Long> supplier, SQLs.LinesWord word) {
            if (word != SQLs.LINES && word != SQLs.ROWS) {
                throw CriteriaUtils.unknownWords(word);
            }
            this.ignoreLine = ClauseUtils.invokeSupplier(supplier);
            this.ignoreLineWord = (SQLWords) word;
            return this;
        }

        @Override
        public MySQLLoadData._LoadSetSpec<I, T> parens(Consumer<Clause._VariadicExprSpaceClause> consumer) {
            this.columnExpList = ClauseUtils.invokeStaticExpressionClause(true, consumer);
            return this;
        }

        @Override
        public MySQLLoadData._LoadSetSpec<I, T> ifParens(Consumer<Clause._VariadicExprSpaceClause> consumer) {
            this.columnExpList = ClauseUtils.invokeStaticExpressionClause(false, consumer);
            return this;
        }

        @Override
        public MySQLLoadData._LoadSetSpec<I, T> parens(SQLs.SymbolSpace space, Consumer<Consumer<Expression>> consumer) {
            this.columnExpList = ClauseUtils.invokeDynamicExpressionClause(true, true, consumer);
            return this;
        }

        @Override
        public MySQLLoadData._LoadSetSpec<I, T> ifParens(SQLs.SymbolSpace space, Consumer<Consumer<Expression>> consumer) {
            this.columnExpList = ClauseUtils.invokeDynamicExpressionClause(false, true, consumer);
            return this;
        }


        @Override
        public PartitionClause<I, T> terminatedBy(final @Nullable String string) {
            if (string == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            if (this.linesClause) {
                this.linesTerminatedBy = string;
            } else {
                this.fieldsTerminatedBy = string;
            }
            return this;
        }

        @Override
        public PartitionClause<I, T> terminatedBy(Supplier<String> supplier) {
            return this.terminatedBy(ClauseUtils.invokeSupplier(supplier));
        }

        @Override
        public PartitionClause<I, T> ifTerminatedBy(Supplier<String> supplier) {
            final String str;
            str = CriteriaUtils.invokeIfSupplier(supplier);
            if (str != null) {
                terminatedBy(str);
            }
            return this;
        }

        @Override
        public MySQLLoadData._EscapedByClause enclosedBy(char ch) {
            this.fieldsOptionally = false;
            this.fieldsEnclosedBy = String.valueOf(ch);
            return this;
        }

        @Override
        public MySQLLoadData._EscapedByClause enclosedBy(final @Nullable String ch) {
            if (ch == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.fieldsOptionally = false;
            this.fieldsEnclosedBy = ch;
            return this;
        }

        @Override
        public MySQLLoadData._EscapedByClause ifEnclosedBy(Supplier<String> supplier) {
            final String ch;
            ch = CriteriaUtils.invokeIfSupplier(supplier);
            if (ch == null) {
                this.fieldsEnclosedBy = null;
            } else {
                this.fieldsOptionally = false;
                this.fieldsEnclosedBy = ch;
            }
            return this;
        }

        @Override
        public MySQLLoadData._EscapedByClause optionallyEnclosedBy(char ch) {
            this.fieldsOptionally = true;
            this.fieldsEnclosedBy = String.valueOf(ch);
            return this;
        }

        @Override
        public MySQLLoadData._EscapedByClause optionallyEnclosedBy(final @Nullable String ch) {
            if (ch == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.fieldsOptionally = true;
            this.fieldsEnclosedBy = ch;
            return this;
        }

        @Override
        public MySQLLoadData._EscapedByClause ifOptionallyEnclosedBy(Supplier<String> supplier) {
            final String ch;
            ch = CriteriaUtils.invokeIfSupplier(supplier);
            if (ch == null) {
                this.fieldsEnclosedBy = null;
            } else {
                this.fieldsOptionally = true;
                this.fieldsEnclosedBy = ch;
            }
            return this;
        }


        @Override
        public void escapedBy(char ch) {
            this.fieldsEscapedBy = String.valueOf(ch);
        }

        @Override
        public void escapedBy(final @Nullable String ch) {
            if (ch == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.fieldsEscapedBy = ch;
        }

        @Override
        public void ifEscapedBy(Supplier<String> supplier) {
            this.fieldsEscapedBy = CriteriaUtils.invokeIfSupplier(supplier);
        }


        @Override
        public MySQLLoadData._TerminatedByClause startingBy(final @Nullable String string) {
            if (string == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.linesStartingBy = string;
            return this;
        }

        @Override
        public MySQLLoadData._TerminatedByClause ifStartingBy(Supplier<String> supplier) {
            this.linesStartingBy = CriteriaUtils.invokeIfSupplier(supplier);
            return this;
        }


        @Override
        public I asCommand() {
            endAssignmentSetClause();
            return this.function.apply(this);
        }

        private static CriteriaException dontAddFieldsClause() {
            return ContextStack.clearStackAndCriteriaError("you don't add any field clause");
        }


    } // PartitionClause


    static abstract class MySQLLoadDataStatement<I extends Item>
            extends CriteriaSupports.StatementMockSupport
            implements _MySQLLoadData, Statement.StatementMockSpec,
            MySQLLoadData, Statement._AsCommandClause<I> {


        private final List<MySQLs.Modifier> modifierList;

        private final Path fileName;

        private final StrategyOption strategyOption;

        final TableMeta<?> targetTable;

        private final List<String> partitionList;

        private final String charset;

        private final Boolean fieldsKeyWords;

        private final String columnTerminatedBy;

        private final boolean columnOptionallyEnclosed;

        private final String columnEnclosedBy;

        private final String columnEscapedBy;

        private final boolean linesClause;

        private final String linesStartingBy;

        private final String linesTerminatedBy;


        private final Long ignoreRowNumber;

        private final SQLWords ignoreRowWord;

        private final List<_Expression> columnOrUserVarList;

        private final List<_Pair<FieldMeta<?>, _Expression>> columItemPairList;

        private Boolean prepared;

        private MySQLLoadDataStatement(PartitionClause<?, ?> clause) {
            super(clause.context);
            this.modifierList = clause.modifierList;
            this.fileName = clause.fileName;
            this.strategyOption = clause.strategyOption;
            this.targetTable = clause.insertTable;

            this.partitionList = _Collections.safeList(clause.partitionList);

            this.charset = clause.charsetName;
            this.fieldsKeyWords = clause.fieldsKeyWords;

            if (this.fieldsKeyWords == null) {
                this.columnTerminatedBy = null;
                this.columnOptionallyEnclosed = false;
                this.columnEnclosedBy = null;
                this.columnEscapedBy = null;
            } else {
                this.columnTerminatedBy = clause.fieldsTerminatedBy;
                this.columnOptionallyEnclosed = clause.fieldsOptionally;
                this.columnEnclosedBy = clause.fieldsEnclosedBy;
                this.columnEscapedBy = clause.fieldsEscapedBy;
            }

            this.linesClause = clause.linesClause;
            if (this.linesClause) {
                this.linesStartingBy = clause.linesStartingBy;
                this.linesTerminatedBy = clause.linesTerminatedBy;
            } else {
                this.linesStartingBy = null;
                this.linesTerminatedBy = null;
            }

            this.ignoreRowNumber = clause.ignoreLine;
            this.ignoreRowWord = clause.ignoreLineWord;
            this.columnOrUserVarList = _Collections.safeList(clause.columnExpList);
            this.columItemPairList = clause.assignmentPairList();
        }


        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public final void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
        }


        @SuppressWarnings("unchecked")
        @Override
        public final I asCommand() {
            _Assert.nonPrepared(this.prepared);
            ContextStack.pop(this.context);
            this.prepared = Boolean.TRUE;
            return (I) this;
        }


        @Override
        public final List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public final Path fileName() {
            return this.fileName;
        }

        @Override
        public final SQLWords strategyOption() {
            return this.strategyOption;
        }

        @Override
        public final TableMeta<?> table() {
            return this.targetTable;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String charset() {
            return this.charset;
        }

        @Override
        public final Boolean fieldsKeyWord() {
            return this.fieldsKeyWords;
        }

        @Override
        public final String columnTerminatedBy() {
            return this.columnTerminatedBy;
        }

        @Override
        public final boolean columnOptionallyEnclosed() {
            return this.columnOptionallyEnclosed;
        }

        @Override
        public final String columnEnclosedBy() {
            return this.columnEnclosedBy;
        }

        @Override
        public final String columnEscapedBy() {
            return this.columnEscapedBy;
        }

        @Override
        public final boolean linesClause() {
            return this.linesClause;
        }

        @Override
        public final String linesStartingBy() {
            return this.linesStartingBy;
        }

        @Override
        public final String linesTerminatedBy() {
            return this.linesTerminatedBy;
        }

        @Override
        public final Long ignoreRows() {
            return this.ignoreRowNumber;
        }

        @Nullable
        @Override
        public SQLWords ignoreRowWord() {
            return this.ignoreRowWord;
        }

        @Override
        public final List<_Expression> columnOrUserVarList() {
            return this.columnOrUserVarList;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> columItemPairList() {
            return this.columItemPairList;
        }


    }//MySQLLoadDataStatement

    private static final class SimpleLoadDataStatement extends MySQLLoadDataStatement<DmlCommand> {

        private SimpleLoadDataStatement(PartitionClause<?, ?> clause) {
            super(clause);
            assert this.targetTable instanceof SingleTableMeta;
        }

    }//SimpleLoadDataStatement


    private static final class ChildLoadDataStatement extends MySQLLoadDataStatement<DmlCommand>
            implements _MySQLLoadData._ChildLoadData {

        private final ParentLoadDataStatement<?, ?> parentStatement;

        private ChildLoadDataStatement(ParentLoadDataStatement<?, ?> parentStatement
                , PartitionClause<?, ?> childClause) {
            super(childClause);
            assert this.targetTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLLoadData parentLoadData() {
            return this.parentStatement;
        }

    }//ChildLoadDataStatement


    private static final class ParentLoadDataStatement<I extends Item, P>
            extends MySQLLoadDataStatement<MySQLLoadData._ChildLoadData<I, P>>
            implements MySQLLoadData._ChildLoadData<I, P> {

        private final Function<DmlCommand, I> function;

        private ParentLoadDataStatement(PartitionClause<?, ?> clause, Function<DmlCommand, I> function) {
            super(clause);
            assert this.targetTable instanceof ParentTableMeta;
            this.function = function;
        }

        @Override
        public _ChildLoadDataClause<I, P> child() {
            prepared();
            return new ChildLoadDataClause<>(this::childLoadDataEnd);
        }


        private I childLoadDataEnd(final PartitionClause<?, ?> childClause) {
            if (!(childClause.insertTable instanceof ChildTableMeta)
                    || ((ChildTableMeta<?>) childClause.insertTable).parentMeta() != this.targetTable) {
                String m = String.format("%s isn't child of %s", childClause.insertTable, this.targetTable);
                throw ContextStack.criteriaError(childClause.context, m);
            }
            final DmlCommand childCommand;
            childCommand = new ChildLoadDataStatement(this, childClause)
                    .asCommand();
            return this.function.apply(childCommand);
        }


    }//ParentLoadDataStatement


}
