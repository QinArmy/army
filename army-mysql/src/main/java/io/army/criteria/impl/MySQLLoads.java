package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.mysql._MySQLLoadData;
import io.army.criteria.mysql.MySQLLoad;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.stmt.Stmt;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

abstract class MySQLLoads {

    private MySQLLoads() {
        throw new UnsupportedOperationException();
    }

    static <C> MySQLLoad._LoadDataClause<C> loadDataStmt(@Nullable C criteria) {
        return new LoadDataInfileClause<>(criteria);
    }


    private enum StrategyOption implements SQLWords {

        REPLACE(" REPLACE"),
        IGNORE(" IGNORE");

        private final String words;

        StrategyOption(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }


        @Override
        public final String toString() {
            return String.format("%s.%s", StrategyOption.class.getSimpleName(), this.name());
        }

    }//StrategyOption


    private static final class LoadDataInfileClause<C> implements MySQLLoad._LoadDataClause<C>
            , MySQLLoad._LoadInfileClause<C, MySQLLoad._StrategyOptionSpec<C>>
            , MySQLLoad._StrategyOptionSpec<C> {

        private final CriteriaContext criteriaContext;

        private List<MySQLWords> modifierList;

        private Path filePath;

        private StrategyOption strategyOption;

        private LoadDataInfileClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.otherPrimaryContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLLoad._LoadInfileClause<C, MySQLLoad._StrategyOptionSpec<C>> loadData(List<MySQLWords> modifierList) {
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifierList
                    , MySQLUtils::loadDataModifier);
            return this;
        }


        @Override
        public MySQLLoad._StrategyOptionSpec<C> infile(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        @Override
        public MySQLLoad._StrategyOptionSpec<C> infile(Supplier<Path> supplier) {
            this.filePath = supplier.get();
            return this;
        }

        @Override
        public MySQLLoad._StrategyOptionSpec<C> infile(Function<C, Path> function) {
            this.filePath = function.apply(this.criteriaContext.criteria());
            return this;
        }

        @Override
        public MySQLLoad._IntoTableClause<C> replace() {
            this.strategyOption = StrategyOption.REPLACE;
            return this;
        }

        @Override
        public MySQLLoad._IntoTableClause<C> ifReplace(Supplier<Boolean> supplier) {
            if (Boolean.TRUE.equals(supplier.get())) {
                this.strategyOption = StrategyOption.REPLACE;
            } else {
                this.strategyOption = null;
            }
            return this;
        }

        @Override
        public MySQLLoad._IntoTableClause<C> ifReplace(Predicate<C> predicate) {
            if (predicate.test(this.criteriaContext.criteria())) {
                this.strategyOption = StrategyOption.REPLACE;
            } else {
                this.strategyOption = null;
            }
            return this;
        }

        @Override
        public MySQLLoad._IntoTableClause<C> ignore() {
            this.strategyOption = StrategyOption.IGNORE;
            return this;
        }

        @Override
        public MySQLLoad._IntoTableClause<C> ifIgnore(Supplier<Boolean> supplier) {
            if (Boolean.TRUE.equals(supplier.get())) {
                this.strategyOption = StrategyOption.IGNORE;
            } else {
                this.strategyOption = null;
            }
            return this;
        }

        @Override
        public MySQLLoad._IntoTableClause<C> ifIgnore(Predicate<C> predicate) {
            if (predicate.test(this.criteriaContext.criteria())) {
                this.strategyOption = StrategyOption.IGNORE;
            } else {
                this.strategyOption = null;
            }
            return this;
        }

        @Override
        public <T> MySQLLoad._PartitionSpec<C, T> intoTable(@Nullable SimpleTableMeta<T> table) {
            if (table == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (this.filePath == null) {
                throw filePathIsNull(this.criteriaContext, table);
            }
            return new NonParentPartitionClause<>(this, table);
        }

        @Override
        public <T> MySQLLoad._ParentPartitionSpec<C, T> intoTable(@Nullable ParentTableMeta<T> table) {
            if (table == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (this.filePath == null) {
                throw filePathIsNull(this.criteriaContext, table);
            }
            return new ParentPartitionClause<>(this, table);
        }


    }//LoadDataInfileClause


    @SuppressWarnings("unchecked")
    private static abstract class PartitionClause<C, T, PR, CR, FR, OR, ER, DR, LR, RR, GR, VR, SR>
            extends InsertSupport.AssignmentSetClause<C, T, SR>
            implements MySQLQuery._PartitionClause<C, PR>
            , MySQLLoad._CharsetClause<C, CR>
            , MySQLLoad._FieldsColumnsClause<FR>
            , MySQLLoad._LinesClause<LR>
            , MySQLLoad._IgnoreLineClause<C, GR>
            , MySQLLoad._LineAfterIgnoreClause<GR>
            , MySQLLoad._ColumnOrVarListClause<C, VR>
            , Statement._RightParenClause<VR>
            , MySQLLoad._StaticColumnDualClause<VR>
            , MySQLLoad._EnclosedByClause<C, ER>
            , MySQLLoad._EscapedByClause<C, DR>
            , MySQLLoad._OptionallyClause<C, OR>
            , MySQLLoad._StartingByClause<C, RR> {

        final List<MySQLWords> modifierList;

        final Path filePath;

        final StrategyOption strategyOption;

        private List<String> partitionList;

        private String charsetName;

        Boolean fieldsKeyWord;

        String columnTerminatedBy;

        private boolean columnOptionallyEnclosed;

        private Character columnEnclosedBy;

        private Character columnEscapedBy;

        boolean linesClause;

        String lineTerminatedBy;

        private String lineStartingBy;

        private Long ignoreRows;

        private List<_Expression> fieldOrUserVarList;

        private PartitionClause(LoadDataInfileClause<C> clause, SingleTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);
            this.filePath = clause.filePath;
            assert this.filePath != null;

            this.strategyOption = clause.strategyOption;
        }

        private PartitionClause(ChildLoadDataInfileClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);
            this.filePath = clause.filePath;
            assert this.filePath != null;

            this.strategyOption = clause.strategyOption;
        }

        @Override
        public final MySQLQuery._PartitionLeftParenClause<C, PR> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }


        @Override
        public final CR characterSet(@Nullable String charsetName) {
            if (charsetName == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.charsetName = charsetName;
            return (CR) this;
        }

        @Override
        public final CR characterSet(Supplier<String> supplier) {
            return this.characterSet(supplier.get());
        }

        @Override
        public final CR characterSet(Function<C, String> function) {
            return this.characterSet(function.apply(this.criteria));
        }

        @Override
        public final CR ifCharacterSet(Supplier<String> supplier) {
            final String charsetName;
            charsetName = supplier.get();
            if (charsetName != null) {
                this.charsetName = charsetName;
            }
            return (CR) this;
        }

        @Override
        public final CR ifCharacterSet(Function<C, String> function) {
            final String charsetName;
            charsetName = function.apply(this.criteria);
            if (charsetName != null) {
                this.charsetName = charsetName;
            }
            return (CR) this;
        }


        @Override
        public final FR fields() {
            this.fieldsKeyWord = Boolean.TRUE;
            return (FR) this;
        }

        @Override
        public final FR columns() {
            this.fieldsKeyWord = Boolean.FALSE;
            return (FR) this;
        }

        @Override
        public final LR lines() {
            this.linesClause = true;
            return (LR) this;
        }

        @Override
        public final MySQLLoad._LineAfterIgnoreClause<GR> ignore(final long rowNumber) {
            if (rowNumber < 0L) {
                String m = String.format("ignore rowNumber[%s] must non-negative.", rowNumber);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            this.ignoreRows = rowNumber;
            return this;
        }

        @Override
        public final MySQLLoad._LineAfterIgnoreClause<GR> ignore(Supplier<Long> supplier) {
            final Long rowNumber;
            rowNumber = supplier.get();
            if (rowNumber == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            return this.ignore(rowNumber);
        }

        @Override
        public final MySQLLoad._LineAfterIgnoreClause<GR> ignore(Function<C, Long> function) {
            final Long rowNumber;
            rowNumber = function.apply(this.criteria);
            if (rowNumber == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            return this.ignore(rowNumber);
        }

        @Override
        public final MySQLLoad._LineAfterIgnoreClause<GR> ifIgnore(Supplier<Long> supplier) {
            final Long rowNumber;
            rowNumber = supplier.get();
            if (rowNumber != null) {
                this.ignore(rowNumber);
            }
            return this;
        }

        @Override
        public final MySQLLoad._LineAfterIgnoreClause<GR> ifIgnore(Function<C, Long> function) {
            final Long rowNumber;
            rowNumber = function.apply(this.criteria);
            if (rowNumber != null) {
                this.ignore(rowNumber);
            }
            return this;
        }

        @Override
        public final GR rows() {
            return (GR) this;
        }

        @Override
        public final Statement._RightParenClause<VR> leftParen(Consumer<Consumer<Expression>> consumer) {
            consumer.accept(this::addFieldOrVar);
            return this;
        }

        @Override
        public final Statement._RightParenClause<VR> leftParen(BiConsumer<C, Consumer<Expression>> consumer) {
            consumer.accept(this.criteria, this::addFieldOrVar);
            return this;
        }

        @Override
        public final Statement._RightParenClause<VR> leftParen(Expression fieldOrVar) {
            this.addFieldOrVar(fieldOrVar);
            return this;
        }

        @Override
        public final MySQLLoad._StaticColumnDualClause<VR> leftParen(Expression fieldOrVar1, Expression fieldOrVar2) {
            this.addFieldOrVar(fieldOrVar1);
            this.addFieldOrVar(fieldOrVar2);
            return this;
        }

        @Override
        public final Statement._RightParenClause<VR> comma(Expression fieldOrVar) {
            this.addFieldOrVar(fieldOrVar);
            return this;
        }

        @Override
        public final MySQLLoad._StaticColumnDualClause<VR> comma(Expression fieldOrVar1, Expression fieldOrVar2) {
            this.addFieldOrVar(fieldOrVar1);
            this.addFieldOrVar(fieldOrVar2);
            return this;
        }

        @Override
        public final VR rightParen() {
            return (VR) this;
        }

        @Override
        public final OR optionally() {
            this.columnOptionallyEnclosed = true;
            return (OR) this;
        }

        @Override
        public final OR ifOptionally(Supplier<Boolean> supplier) {
            this.columnOptionallyEnclosed = Boolean.TRUE.equals(supplier.get());
            return (OR) this;
        }

        @Override
        public final OR ifOptionally(Predicate<C> predicate) {
            this.columnOptionallyEnclosed = predicate.test(this.criteria);
            return (OR) this;
        }


        @Override
        public final ER enclosedBy(char ch) {
            this.columnEnclosedBy = ch;
            return (ER) this;
        }

        @Override
        public final ER enclosedBy(Supplier<Character> supplier) {
            final Character ch;
            ch = supplier.get();
            if (ch == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.columnEnclosedBy = ch;
            return (ER) this;
        }

        @Override
        public final ER enclosedBy(Function<C, Character> function) {
            final Character ch;
            ch = function.apply(this.criteria);
            if (ch == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.columnEnclosedBy = ch;
            return (ER) this;
        }

        @Override
        public final ER ifEnclosedBy(Supplier<Character> supplier) {
            this.columnEnclosedBy = supplier.get();
            return (ER) this;
        }

        @Override
        public final ER ifEnclosedBy(Function<C, Character> function) {
            this.columnEnclosedBy = function.apply(this.criteria);
            return (ER) this;
        }

        @Override
        public final DR escapedBy(char ch) {
            this.columnEscapedBy = ch;
            return (DR) this;
        }

        @Override
        public final DR escapedBy(Supplier<Character> supplier) {
            final Character ch;
            ch = supplier.get();
            if (ch == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.columnEscapedBy = ch;
            return (DR) this;
        }

        @Override
        public final DR escapedBy(Function<C, Character> function) {
            final Character ch;
            ch = function.apply(this.criteria);
            if (ch == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.columnEscapedBy = ch;
            return (DR) this;
        }

        @Override
        public final DR ifEscapedBy(Supplier<Character> supplier) {
            this.columnEscapedBy = supplier.get();
            return (DR) this;
        }

        @Override
        public final DR ifEscapedBy(Function<C, Character> function) {
            this.columnEscapedBy = function.apply(this.criteria);
            return (DR) this;
        }


        @Override
        public final RR startingBy(@Nullable String string) {
            if (string == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.lineStartingBy = string;
            return (RR) this;
        }

        @Override
        public final RR startingBy(Supplier<String> supplier) {
            return this.startingBy(supplier.get());
        }

        @Override
        public final RR startingBy(Function<C, String> function) {
            return this.startingBy(function.apply(this.criteria));
        }

        @Override
        public final RR ifStartingBy(Supplier<String> supplier) {
            this.lineStartingBy = supplier.get();
            return (RR) this;
        }

        @Override
        public final RR ifStartingBy(Function<C, String> function) {
            this.lineStartingBy = function.apply(this.criteria);
            return (RR) this;
        }


        @Override
        public final void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            if (field.tableMeta() != this.insertTable) {
                throw _Exceptions.unknownColumn(field);
            }
            //TODO check non-null filed
        }

        private PR partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return (PR) this;
        }


        final List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private void addFieldOrVar(final @Nullable Expression columnOrVar) {
            if (columnOrVar instanceof FieldMeta) {
                if (((FieldMeta<?>) columnOrVar).tableMeta() != this.insertTable) {
                    String m = String.format("%s isn't belong of %s", columnOrVar, this.insertTable);
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                }
            } else if (!(columnOrVar instanceof VarExpression)) {
                String m = String.format("support only %s or %s"
                        , FieldMeta.class.getName(), VarExpression.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

            if (!(columnOrVar instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }

            List<_Expression> fieldVarList = this.fieldOrUserVarList;
            if (fieldVarList == null) {
                fieldVarList = new ArrayList<>();
                this.fieldOrUserVarList = fieldVarList;
            }
            fieldVarList.add((ArmyExpression) columnOrVar);
        }

        private List<_Expression> columnOrUserVarList() {
            List<_Expression> list = this.fieldOrUserVarList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//PartitionClause


    private static final class NonParentPartitionClause<C, T>
            extends PartitionClause<
            C,
            T,
            MySQLLoad._CharsetSpec<C, T>,
            MySQLLoad._FieldsColumnsSpec<C, T>,
            MySQLLoad._ColumnTerminatedBySpec<C, T>,
            MySQLLoad._ColumnEnclosedByClause<C, T>,
            MySQLLoad._ColumnEscapedBySpec<C, T>,
            MySQLLoad._LinesSpec<C, T>,
            MySQLLoad._LineStartingBySpec<C, T>,
            MySQLLoad._LinesTerminatedBySpec<C, T>,
            MySQLLoad._ColumnOrVarListSpec<C, T>,
            MySQLLoad._LoadSetSpec<C, T>,
            MySQLLoad._LoadSetSpec<C, T>>
            implements MySQLLoad._PartitionSpec<C, T>
            , MySQLLoad._ColumnTerminatedBySpec<C, T>
            , MySQLLoad._ColumnEnclosedBySpec<C, T>
            , MySQLLoad._LineStartingBySpec<C, T>
            , MySQLLoad._LinesTerminatedBySpec<C, T> {

        private final ParentLoadDataStatement parentStmt;

        private NonParentPartitionClause(LoadDataInfileClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.parentStmt = null;
        }

        private NonParentPartitionClause(ChildLoadDataInfileClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.parentStmt = clause.parentStatement;
        }


        @Override
        public MySQLLoad._ColumnEnclosedBySpec<C, T> terminatedBy(final @Nullable String string) {
            if (string == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (this.linesClause) {
                this.lineTerminatedBy = string;
            } else if (this.fieldsKeyWord != null) {
                this.columnTerminatedBy = string;
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        @Override
        public MySQLLoad._ColumnEnclosedBySpec<C, T> terminatedBy(Supplier<String> supplier) {
            return this.terminatedBy(supplier.get());
        }

        @Override
        public MySQLLoad._ColumnEnclosedBySpec<C, T> terminatedBy(Function<C, String> function) {
            return this.terminatedBy(function.apply(this.criteria));
        }

        @Override
        public MySQLLoad._ColumnEnclosedBySpec<C, T> ifTerminatedBy(Supplier<String> supplier) {
            final String string;
            string = supplier.get();
            if (string != null) {
                this.terminatedBy(string);
            }
            return this;
        }

        @Override
        public MySQLLoad._ColumnEnclosedBySpec<C, T> ifTerminatedBy(Function<C, String> function) {
            final String string;
            string = function.apply(this.criteria);
            if (string != null) {
                this.terminatedBy(string);
            }
            return this;
        }

        @Override
        public MySQLLoad asLoadData() {
            final MySQLLoad._LoadDataSpec spec;
            if (this.parentStmt == null) {
                spec = new SingleLoadDataStatement(this);
            } else {
                spec = new ChildLoadDataStatement(this);
            }
            return spec.asLoadData();
        }


    }//SimplePartitionClause


    private static final class ParentPartitionClause<C, P> extends PartitionClause<
            C,
            P,
            MySQLLoad._ParentCharsetSpec<C, P>,
            MySQLLoad._ParentFieldsColumnsSpec<C, P>,
            MySQLLoad._ParentColumnTerminatedBySpec<C, P>,
            MySQLLoad._ParentColumnEnclosedByClause<C, P>,
            MySQLLoad._ParentColumnEscapedBySpec<C, P>,
            MySQLLoad._ParentLinesSpec<C, P>,
            MySQLLoad._ParentLineStartingBySpec<C, P>,
            MySQLLoad._ParentLineTerminatedBySpec<C, P>,
            MySQLLoad._ParentColumnVarListSpec<C, P>,
            MySQLLoad._ParentAssignmentSetSpec<C, P>,
            MySQLLoad._ParentAssignmentSetSpec<C, P>>
            implements MySQLLoad._ParentPartitionSpec<C, P>
            , MySQLLoad._ParentColumnTerminatedBySpec<C, P>
            , MySQLLoad._ParentColumnEnclosedBySpec<C, P>
            , MySQLLoad._ParentLineStartingBySpec<C, P>
            , MySQLLoad._ParentLineTerminatedBySpec<C, P> {

        private ParentPartitionClause(LoadDataInfileClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
        }

        @Override
        public MySQLLoad._ParentColumnEnclosedBySpec<C, P> terminatedBy(final @Nullable String string) {
            if (string == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (this.linesClause) {
                this.lineTerminatedBy = string;
            } else if (this.fieldsKeyWord != null) {
                this.columnTerminatedBy = string;
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        @Override
        public MySQLLoad._ParentColumnEnclosedBySpec<C, P> terminatedBy(Supplier<String> supplier) {
            return this.terminatedBy(supplier.get());
        }

        @Override
        public MySQLLoad._ParentColumnEnclosedBySpec<C, P> terminatedBy(Function<C, String> function) {
            return this.terminatedBy(function.apply(this.criteria));
        }

        @Override
        public MySQLLoad._ParentColumnEnclosedBySpec<C, P> ifTerminatedBy(Supplier<String> supplier) {
            final String string;
            string = supplier.get();
            if (string != null) {
                this.terminatedBy(string);
            }
            return this;
        }

        @Override
        public MySQLLoad._ParentColumnEnclosedBySpec<C, P> ifTerminatedBy(Function<C, String> function) {
            final String string;
            string = function.apply(this.criteria);
            if (string != null) {
                this.terminatedBy(string);
            }
            return this;
        }

        @Override
        public MySQLLoad._ChildLoadInfileClause<C, P> child(List<MySQLWords> modifierList) {
            final List<MySQLWords> list;
            list = MySQLUtils.asModifierList(this.criteriaContext, modifierList, MySQLUtils::loadDataModifier);
            return new ChildLoadDataInfileClause<>(this.criteriaContext, new ParentLoadDataStatement(this), list);
        }


    }//ParentPartitionClause


    private static final class ChildLoadDataInfileClause<C, P>
            implements MySQLLoad._ChildLoadInfileClause<C, P>
            , MySQLLoad._ChildLoadStrategySpec<C, P>
            , MySQLLoad._ChildIntoTableClause<C, P> {

        private final CriteriaContext criteriaContext;

        private final ParentLoadDataStatement parentStatement;

        private final List<MySQLWords> modifierList;

        private Path filePath;

        private StrategyOption strategyOption;

        private ChildLoadDataInfileClause(CriteriaContext criteriaContext, ParentLoadDataStatement parentStatement
                , List<MySQLWords> modifierList) {
            this.criteriaContext = criteriaContext;
            this.parentStatement = parentStatement;
            this.modifierList = modifierList;
        }

        @Override
        public MySQLLoad._ChildLoadStrategySpec<C, P> infile(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        @Override
        public MySQLLoad._ChildLoadStrategySpec<C, P> infile(Supplier<Path> supplier) {
            this.filePath = supplier.get();
            return this;
        }

        @Override
        public MySQLLoad._ChildLoadStrategySpec<C, P> infile(Function<C, Path> function) {
            this.filePath = function.apply(this.criteriaContext.criteria());
            return this;
        }

        @Override
        public MySQLLoad._ChildIntoTableClause<C, P> replace() {
            this.strategyOption = StrategyOption.REPLACE;
            return this;
        }

        @Override
        public MySQLLoad._ChildIntoTableClause<C, P> ifReplace(Supplier<Boolean> supplier) {
            if (Boolean.TRUE.equals(supplier.get())) {
                this.strategyOption = StrategyOption.REPLACE;
            } else {
                this.strategyOption = null;
            }
            return this;
        }

        @Override
        public MySQLLoad._ChildIntoTableClause<C, P> ifReplace(Predicate<C> predicate) {
            if (predicate.test(this.criteriaContext.criteria())) {
                this.strategyOption = StrategyOption.REPLACE;
            } else {
                this.strategyOption = null;
            }
            return this;
        }

        @Override
        public MySQLLoad._ChildIntoTableClause<C, P> ignore() {
            this.strategyOption = StrategyOption.IGNORE;
            return this;
        }

        @Override
        public MySQLLoad._ChildIntoTableClause<C, P> ifIgnore(Supplier<Boolean> supplier) {
            if (Boolean.TRUE.equals(supplier.get())) {
                this.strategyOption = StrategyOption.IGNORE;
            } else {
                this.strategyOption = null;
            }
            return this;
        }

        @Override
        public MySQLLoad._ChildIntoTableClause<C, P> ifIgnore(Predicate<C> predicate) {
            if (predicate.test(this.criteriaContext.criteria())) {
                this.strategyOption = StrategyOption.IGNORE;
            } else {
                this.strategyOption = null;
            }
            return this;
        }

        @Override
        public <T> MySQLLoad._PartitionSpec<C, T> intoTable(@Nullable ComplexTableMeta<P, T> table) {
            if (table == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (table.parentMeta() != this.parentStatement.parentTable) {
                String m = String.format("%s isn't child of %s", table, this.parentStatement.parentTable);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (this.filePath == null) {
                throw filePathIsNull(this.criteriaContext, table);
            }
            return new NonParentPartitionClause<>(this, table);
        }


    }//ChildLoadDataInfileClause


    static abstract class MySQLLoadDataStatement implements _MySQLLoadData, Statement.StatementMockSpec
            , DialectStatement {

        private final List<MySQLWords> modifierList;

        private final Path fileName;

        private final StrategyOption strategyOption;

        private final List<String> partitionList;

        private final String charsetName;

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

        private MySQLLoadDataStatement(PartitionClause<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            this.modifierList = clause.modifierList;
            this.fileName = clause.filePath;
            this.strategyOption = clause.strategyOption;
            this.partitionList = clause.partitionList();

            this.charsetName = clause.charsetName;
            this.fieldsKeyWords = clause.fieldsKeyWord;

            if (this.fieldsKeyWords == null) {
                this.columnTerminatedBy = null;
                this.columnOptionallyEnclosed = false;
                this.columnEnclosedBy = null;
                this.columnEscapedBy = null;
            } else {
                this.columnTerminatedBy = clause.columnTerminatedBy;
                this.columnOptionallyEnclosed = clause.columnOptionallyEnclosed;
                this.columnEnclosedBy = clause.columnEnclosedBy;
                this.columnEscapedBy = clause.columnEscapedBy;
            }

            this.linesClause = clause.linesClause;
            if (this.linesClause) {
                this.linesStartingBy = clause.lineStartingBy;
                this.linesTerminatedBy = clause.lineTerminatedBy;
            } else {
                this.linesStartingBy = null;
                this.linesTerminatedBy = null;
            }

            this.ignoreRows = clause.ignoreRows;
            this.columnOrUserVarList = clause.columnOrUserVarList();

            clause.endAssignmentSetClause();
            this.columItemPairList = clause.pairList();

        }


        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final DialectParser parser;
            parser = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = parser.dialectStmt(this, visible);
            return parser.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return _MockDialects.from(dialect).dialectStmt(this, visible);
        }

        @Override
        public final List<MySQLWords> modifierList() {
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
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String charsetName() {
            return this.charsetName;
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


    private static final class SingleLoadDataStatement extends MySQLLoadDataStatement
            implements MySQLLoad, _MySQLLoadData._SingleLoadData, MySQLLoad._LoadDataSpec {

        private final CriteriaContext criteriaContext;

        private final SingleTableMeta<?> table;


        private Boolean prepared;

        private SingleLoadDataStatement(NonParentPartitionClause<?, ?> clause) {
            super(clause);
            this.criteriaContext = clause.criteriaContext;
            this.table = (SingleTableMeta<?>) clause.insertTable;
        }

        @Override
        public MySQLLoad asLoadData() {
            _Assert.nonPrepared(this.prepared);
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
        }

        @Override
        public SingleTableMeta<?> table() {
            return this.table;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

    }//SingleLoadDataStatement


    private static final class ParentLoadDataStatement extends MySQLLoadDataStatement
            implements _MySQLLoadData._SingleLoadData {

        private final ParentTableMeta<?> parentTable;

        private ParentLoadDataStatement(final ParentPartitionClause<?, ?> clause) {
            super(clause);
            this.parentTable = (ParentTableMeta<?>) clause.insertTable;
        }

        @Override
        public void prepared() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPrepared() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SingleTableMeta<?> table() {
            return this.parentTable;
        }

    }//ParentLoadDataStatement


    private static final class ChildLoadDataStatement extends MySQLLoadDataStatement
            implements MySQLLoad, _MySQLLoadData._ChildLoadData, MySQLLoad._LoadDataSpec {


        private final CriteriaContext criteriaContext;
        private final ParentLoadDataStatement parentStatement;

        private final ChildTableMeta<?> childTable;

        private Boolean prepared;

        private ChildLoadDataStatement(NonParentPartitionClause<?, ?> clause) {
            super(clause);
            this.criteriaContext = clause.criteriaContext;
            this.parentStatement = clause.parentStmt;
            assert this.parentStatement != null;
            this.childTable = (ChildTableMeta<?>) clause.insertTable;
        }


        @Override
        public MySQLLoad asLoadData() {
            _Assert.nonPrepared(this.prepared);
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
        }


        @Override
        public ChildTableMeta<?> table() {
            return this.childTable;
        }

        @Override
        public _SingleLoadData parentLoadData() {
            return this.parentStatement;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//ChildLoadDataStatement


    private static CriteriaException filePathIsNull(CriteriaContext criteriaContext, TableMeta<?> table) {
        String m = String.format("file path is null for %s", table);
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }


}
