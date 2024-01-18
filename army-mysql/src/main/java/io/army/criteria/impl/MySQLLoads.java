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

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SQLWords;
import io.army.criteria.Statement;
import io.army.criteria.dialect.SQLCommand;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.mysql._MySQLLoadData;
import io.army.criteria.mysql.MySQLCharset;
import io.army.criteria.mysql.MySQLLoadData;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.*;
import io.army.util._Assert;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLLoads {

    private MySQLLoads() {
        throw new UnsupportedOperationException();
    }

    static <I extends Item> MySQLLoadData._LoadDataClause<I> loadDataCommand(Function<SQLCommand, I> function) {
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

    }//StrategyOption

    private static abstract class LoadDataClause<T extends LoadDataClause<T>> {

        final CriteriaContext context;

        private final T THIS;

        private List<MySQLs.Modifier> modifierList;

        private Path fileName;

        private StrategyOption strategyOption;


        @SuppressWarnings("unchecked")
        private LoadDataClause() {
            this.context = CriteriaContexts.otherPrimaryContext(MySQLUtils.DIALECT);
            ContextStack.push(this.context);
            this.THIS = (T) this;
        }


        public final T loadData(MySQLs.Modifier local) {
            if (local != MySQLs.LOCAL) {
                throw ContextStack.criteriaError(this.context, String.format("%s isn't %s", local, MySQLs.LOCAL));
            }
            this.modifierList = Collections.singletonList(local);
            return THIS;
        }

        public final T loadData(List<MySQLs.Modifier> modifierList) {
            if (!modifierList.contains(MySQLs.LOCAL)) {
                String m = String.format("%s don't contains %s", modifierList, MySQLs.LOCAL);
                throw ContextStack.criteriaError(this.context, m);
            }
            this.modifierList = MySQLUtils.asModifierList(this.context, modifierList, MySQLUtils::loadDataModifier);
            return THIS;
        }


        public final T infile(final @Nullable Path filePath) {
            if (filePath == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (!Files.notExists(filePath)) {
                String m = String.format("%s not exists ", filePath);
                throw ContextStack.criteriaError(this.context, m);
            } else if (Files.isDirectory(filePath) || !Files.isReadable(filePath)) {
                String m = String.format("%s isn't readable ", filePath);
                throw ContextStack.criteriaError(this.context, m);
            }
            this.fileName = filePath;
            return THIS;
        }

        public final T infile(Supplier<Path> supplier) {
            return this.infile(supplier.get());
        }


        public final T replace() {
            this.strategyOption = StrategyOption.REPLACE;
            return THIS;
        }


        public final T ignore() {
            this.strategyOption = StrategyOption.IGNORE;
            return THIS;
        }


        public final T ifReplace(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.strategyOption = StrategyOption.REPLACE;
            } else {
                this.strategyOption = null;
            }
            return THIS;
        }


        public final T ifIgnore(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.strategyOption = StrategyOption.IGNORE;
            } else {
                this.strategyOption = null;
            }
            return THIS;
        }


    }//LoadDataClause

    private static final class PrimaryLoadDataClause<I extends Item>
            extends LoadDataClause<PrimaryLoadDataClause<I>>
            implements MySQLLoadData._LoadDataClause<I>
            , MySQLLoadData._LocalInfileClause<I>
            , MySQLLoadData._StrategyOptionSpec<I> {

        private final Function<SQLCommand, I> function;

        private PrimaryLoadDataClause(Function<SQLCommand, I> function) {
            this.function = function;
        }

        @Override
        public <T> MySQLLoadData._PartitionSpec<I, T> intoTable(final @Nullable SingleTableMeta<T> table) {
            if (table == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return new PartitionClause<>(this, table, this::simpleStmtEnd);
        }

        @Override
        public <T> MySQLLoadData._PartitionSpec<MySQLLoadData._ParentLoadData<I, T>, T> intoTable(final @Nullable ParentTableMeta<T> table) {
            if (table == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return new PartitionClause<>(this, table, this::parentStmtEnd);
        }

        private I simpleStmtEnd(PartitionClause<?, ?> clause) {
            return this.function.apply(new SimpleLoadDataStatement(clause).asCommand());
        }

        private <T> MySQLLoadData._ParentLoadData<I, T> parentStmtEnd(PartitionClause<?, ?> clause) {
            final Statement._AsCommandClause<MySQLLoadData._ParentLoadData<I, T>> spec;
            spec = new ParentLoadDataStatement<>(clause, this.function);
            return spec.asCommand();
        }


    }//PrimaryLoadDataClause

    private static final class ChildLoadDataClause<I extends Item, P>
            extends LoadDataClause<ChildLoadDataClause<I, P>>
            implements MySQLLoadData._ChildLoadDataClause<I, P>
            , MySQLLoadData._ChildLocalInfileClause<I, P>
            , MySQLLoadData._ChildStrategyOptionSpec<I, P> {

        private final Function<PartitionClause<?, ?>, I> function;

        private ChildLoadDataClause(Function<PartitionClause<?, ?>, I> function) {
            this.function = function;
        }

        @Override
        public <T> MySQLLoadData._PartitionSpec<I, T> intoTable(ComplexTableMeta<P, T> table) {
            return new PartitionClause<>(this, table, this.function);
        }


    }//ChildLoadDataClause


    private static final class PartitionClause<I extends Item, T>
            extends InsertSupports.AssignmentSetClause<
            T,
            MySQLLoadData._LoadSetSpec<I, T>>
            implements MySQLLoadData._PartitionSpec<I, T>
            , MySQLLoadData._ColumnTerminatedBySpec<I, T>
            , MySQLLoadData._LineStartingBySpec<I, T>
            , MySQLLoadData._LinesTerminatedBySpec<I, T>
            , Statement._RightParenClause<MySQLLoadData._LoadSetSpec<I, T>>
            , MySQLLoadData._StaticColumnDualClause<MySQLLoadData._LoadSetSpec<I, T>>
            , MySQLLoadData._LineAfterIgnoreClause<I, T> {


        private final List<MySQLs.Modifier> modifierList;

        private final Path fileName;

        private final StrategyOption strategyOption;

        private final Function<PartitionClause<?, ?>, I> function;

        private List<String> partitionLit;

        private Object charset;

        private Boolean fieldsKeyWords;

        private String fieldsTerminatedBy;

        private boolean fieldsOptionally;

        private Character fieldsEnclosedBy;

        private Character fieldsEscapedBy;

        private boolean linesClause;

        private String linesStartingBy;

        private String linesTerminatedBy;

        private Long ignoreLine;

        private List<_Expression> columnExpList;


        private PartitionClause(LoadDataClause<?> clause, TableMeta<T> insertTable
                , Function<PartitionClause<?, ?>, I> function) {
            super(clause.context, insertTable);

            this.modifierList = clause.modifierList;
            this.fileName = clause.fileName;
            this.strategyOption = clause.strategyOption;
            this.function = function;

            if (this.modifierList == null
                    || this.fileName == null
                    || this.strategyOption == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
        }

        @Override
        public Statement._LeftParenStringQuadraOptionalSpec<MySQLLoadData._CharsetSpec<I, T>> partition() {
            return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
        }


        @Override
        public MySQLLoadData._FieldsColumnsSpec<I, T> characterSet(String charsetName) {
            this.charset = charsetName;
            return this;
        }

        @Override
        public MySQLLoadData._FieldsColumnsSpec<I, T> characterSet(MySQLCharset charset) {
            this.charset = charset;
            return this;
        }

        @Override
        public MySQLLoadData._FieldsColumnsSpec<I, T> ifCharacterSet(Supplier<String> supplier) {
            this.charset = supplier.get();
            return this;
        }

        @Override
        public MySQLLoadData._ColumnTerminatedBySpec<I, T> fields() {
            this.fieldsKeyWords = Boolean.TRUE;
            return this;
        }

        @Override
        public MySQLLoadData._ColumnTerminatedBySpec<I, T> columns() {
            this.fieldsKeyWords = Boolean.FALSE;
            return this;
        }

        @Override
        public MySQLLoadData._ColumnEnclosedBySpec<I, T> terminatedBy(final @Nullable String string) {
            if (this.linesClause) {
                if (string == null) {
                    throw ContextStack.nullPointer(this.context);
                }
                this.linesTerminatedBy = string;
            } else if (this.fieldsKeyWords != null) {
                if (string == null) {
                    throw ContextStack.nullPointer(this.context);
                }
                this.fieldsTerminatedBy = string;
            }
            return this;
        }

        @Override
        public MySQLLoadData._ColumnEnclosedBySpec<I, T> terminatedBy(Supplier<String> supplier) {
            if (this.linesClause || this.fieldsKeyWords != null) {
                this.terminatedBy(supplier.get());
            }
            return this;
        }

        @Override
        public MySQLLoadData._ColumnEnclosedBySpec<I, T> ifTerminatedBy(Supplier<String> supplier) {
            if (this.linesClause || this.fieldsKeyWords != null) {
                final String string;
                if ((string = supplier.get()) != null) {
                    this.terminatedBy(string);
                }
            }
            return this;
        }

        @Override
        public MySQLLoadData._ColumnEnclosedByClause<I, T> optionally() {
            this.fieldsOptionally = this.fieldsKeyWords != null;
            return this;
        }

        @Override
        public MySQLLoadData._ColumnEnclosedByClause<I, T> ifOptionally(BooleanSupplier predicate) {
            this.fieldsOptionally = this.fieldsKeyWords != null && predicate.getAsBoolean();
            return this;
        }

        @Override
        public MySQLLoadData._ColumnEscapedBySpec<I, T> enclosedBy(char ch) {
            this.fieldsEnclosedBy = this.fieldsKeyWords != null ? ch : null;
            return this;
        }

        @Override
        public MySQLLoadData._ColumnEscapedBySpec<I, T> enclosedBy(Supplier<Character> supplier) {
            if (this.fieldsKeyWords != null) {
                final Character ch;
                if ((ch = supplier.get()) == null) {
                    throw ContextStack.nullPointer(this.context);
                }
                this.fieldsEnclosedBy = ch;
            } else {
                this.fieldsEnclosedBy = null;
            }
            return this;
        }

        @Override
        public MySQLLoadData._ColumnEscapedBySpec<I, T> ifEnclosedBy(Supplier<Character> supplier) {
            this.fieldsEnclosedBy = this.fieldsKeyWords != null ? supplier.get() : null;
            return this;
        }

        @Override
        public MySQLLoadData._LinesSpec<I, T> escapedBy(char ch) {
            this.fieldsEscapedBy = this.fieldsKeyWords != null ? ch : null;
            return this;
        }

        @Override
        public MySQLLoadData._LinesSpec<I, T> escapedBy(Supplier<Character> supplier) {
            if (this.fieldsKeyWords != null) {
                final Character ch;
                if ((ch = supplier.get()) == null) {
                    throw ContextStack.nullPointer(this.context);
                }
                this.fieldsEscapedBy = ch;
            } else {
                this.fieldsEscapedBy = null;
            }
            return this;
        }

        @Override
        public MySQLLoadData._LinesSpec<I, T> ifEscapedBy(Supplier<Character> supplier) {
            this.fieldsEscapedBy = this.fieldsKeyWords != null ? supplier.get() : null;
            return this;
        }

        @Override
        public MySQLLoadData._LineStartingBySpec<I, T> lines() {
            this.linesClause = true;
            return this;
        }

        @Override
        public MySQLLoadData._LinesTerminatedBySpec<I, T> startingBy(final @Nullable String string) {
            if (!this.linesClause) {
                this.linesStartingBy = null;
            } else if (string == null) {
                throw ContextStack.nullPointer(this.context);
            } else {
                this.linesStartingBy = string;
            }
            return this;
        }

        @Override
        public MySQLLoadData._LinesTerminatedBySpec<I, T> startingBy(Supplier<String> supplier) {
            return this.startingBy(supplier.get());
        }

        @Override
        public MySQLLoadData._LinesTerminatedBySpec<I, T> ifStartingBy(Supplier<String> supplier) {
            this.linesStartingBy = this.linesClause ? supplier.get() : null;
            return this;
        }

        @Override
        public Statement._RightParenClause<MySQLLoadData._LoadSetSpec<I, T>> leftParen(Consumer<Consumer<Expression>> consumer) {
            consumer.accept(this::comma);
            return this;
        }

        @Override
        public Statement._RightParenClause<MySQLLoadData._LoadSetSpec<I, T>> leftParen(Expression fieldOrVar) {
            return this.comma(fieldOrVar);
        }

        @Override
        public MySQLLoadData._StaticColumnDualClause<MySQLLoadData._LoadSetSpec<I, T>> leftParen(Expression fieldOrVar1, Expression fieldOrVar2) {
            this.comma(fieldOrVar1);
            this.comma(fieldOrVar2);
            return this;
        }

        @Override
        public Statement._RightParenClause<MySQLLoadData._LoadSetSpec<I, T>> comma(final @Nullable Expression fieldOrVar) {
            if (fieldOrVar == null) {
                throw ContextStack.nullPointer(this.context);
            }
            List<_Expression> list = this.columnExpList;
            if (list == null) {
                list = _Collections.arrayList();
                this.columnExpList = list;
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add((ArmyExpression) fieldOrVar);
            return this;
        }

        @Override
        public MySQLLoadData._StaticColumnDualClause<MySQLLoadData._LoadSetSpec<I, T>> comma(Expression fieldOrVar1, Expression fieldOrVar2) {
            this.comma(fieldOrVar1);
            this.comma(fieldOrVar2);
            return this;
        }

        @Override
        public MySQLLoadData._LoadSetSpec<I, T> rightParen() {
            final List<_Expression> list = this.columnExpList;
            if (list == null) {
                this.columnExpList = Collections.emptyList();
            } else {
                this.columnExpList = _Collections.unmodifiableList(list);
            }
            return this;
        }

        @Override
        public MySQLLoadData._LineAfterIgnoreClause<I, T> ignore(long rowNumber) {
            this.ignoreLine = rowNumber;
            return this;
        }

        @Override
        public MySQLLoadData._LineAfterIgnoreClause<I, T> ignore(Supplier<Long> supplier) {
            final Long num;
            num = supplier.get();
            if (num == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.ignoreLine = num;
            return this;
        }

        @Override
        public MySQLLoadData._ColumnOrVarListSpec<I, T> rows() {
            return this;
        }

        @Override
        public MySQLLoadData._LineAfterIgnoreClause<I, T> ifIgnore(Supplier<Long> supplier) {
            this.ignoreLine = supplier.get();
            return this;
        }

        @Override
        public I asCommand() {
            this.endAssignmentSetClause();
            return this.function.apply(this);
        }


        private MySQLLoadData._CharsetSpec<I, T> partitionEnd(final List<String> list) {
            if (this.partitionLit != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.partitionLit = list;
            return this;
        }


    }//PartitionClause


    static abstract class MySQLLoadDataStatement<I extends Item>
            extends CriteriaSupports.StatementMockSupport
            implements _MySQLLoadData, Statement.StatementMockSpec
            , MySQLLoadData, Statement._AsCommandClause<I> {


        private final List<MySQLs.Modifier> modifierList;

        private final Path fileName;

        private final StrategyOption strategyOption;

        final TableMeta<?> targetTable;

        private final List<String> partitionList;

        private final Object charset;

        private final Boolean fieldsKeyWords;

        private final String columnTerminatedBy;

        private final boolean columnOptionallyEnclosed;

        private final Character columnEnclosedBy;

        private final Character columnEscapedBy;

        private final boolean linesClause;

        private final String linesStartingBy;

        private final String linesTerminatedBy;


        private final Long ignoreRows;

        private final List<_Expression> columnOrUserVarList;

        private final List<_Pair<FieldMeta<?>, _Expression>> columItemPairList;

        private Boolean prepared;

        private MySQLLoadDataStatement(PartitionClause<?, ?> clause) {
            super(clause.context);
            this.modifierList = clause.modifierList;
            this.fileName = clause.fileName;
            this.strategyOption = clause.strategyOption;
            this.targetTable = clause.insertTable;

            this.partitionList = _Collections.safeList(clause.partitionLit);

            this.charset = clause.charset;
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

            this.ignoreRows = clause.ignoreLine;
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
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
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
        public final Object charset() {
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
        public final Character columnEnclosedBy() {
            return this.columnEnclosedBy;
        }

        @Override
        public final Character columnEscapedBy() {
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
            return this.ignoreRows;
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

    private static final class SimpleLoadDataStatement extends MySQLLoadDataStatement<SQLCommand> {

        private SimpleLoadDataStatement(PartitionClause<?, ?> clause) {
            super(clause);
            assert this.targetTable instanceof SingleTableMeta;
        }

    }//SimpleLoadDataStatement


    private static final class ChildLoadDataStatement extends MySQLLoadDataStatement<SQLCommand>
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
            extends MySQLLoadDataStatement<MySQLLoadData._ParentLoadData<I, P>>
            implements MySQLLoadData._ParentLoadData<I, P> {

        private final Function<SQLCommand, I> function;

        private ParentLoadDataStatement(PartitionClause<?, ?> clause, Function<SQLCommand, I> function) {
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
            final SQLCommand childCommand;
            childCommand = new ChildLoadDataStatement(this, childClause)
                    .asCommand();
            return this.function.apply(childCommand);
        }


    }//ParentLoadDataStatement


}
