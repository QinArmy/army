package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.Statement;
import io.army.criteria.VarExpression;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.mysql.MySQLLoad;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWords;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.TableMeta;

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


    private static final class LoadDataInfileClause<C> implements MySQLLoad._LoadDataClause<C>
            , MySQLLoad._LoadInfileClause<C, MySQLLoad._IntoTableClause<C>>
            , MySQLLoad._IntoTableClause<C> {

        private final CriteriaContext criteriaContext;

        private List<MySQLWords> modifierList;

        private Path path;

        private LoadDataInfileClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.otherPrimaryContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public final MySQLLoad._LoadInfileClause<C, MySQLLoad._IntoTableClause<C>> loadData(List<MySQLWords> modifierList) {
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifierList
                    , MySQLUtils::loadDataModifier);
            return this;
        }

        @Override
        public final MySQLLoad._IntoTableClause<C> infile(Path filePath) {
            this.path = filePath;
            return this;
        }

        @Override
        public final MySQLLoad._IntoTableClause<C> infile(Supplier<Path> supplier) {
            this.path = supplier.get();
            return this;
        }

        @Override
        public final MySQLLoad._IntoTableClause<C> infile(Function<C, Path> function) {
            this.path = function.apply(this.criteriaContext.criteria());
            return this;
        }

        @Override
        public final <T extends IDomain> MySQLLoad._PartitionSpec<C, T> intoTable(@Nullable SimpleTableMeta<T> table) {
            if (table == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (this.path == null) {
                throw filePathIsNull(this.criteriaContext, table);
            }
            return null;
        }

        @Override
        public final <T extends IDomain> MySQLLoad._ParentPartitionSpec<C, T> intoTable(@Nullable ParentTableMeta<T> table) {
            if (table == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (this.path == null) {
                throw filePathIsNull(this.criteriaContext, table);
            }
            return null;
        }


        private List<MySQLWords> modifierList() {
            List<MySQLWords> list = this.modifierList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//LoadDataInfileClause

    @SuppressWarnings("unchecked")
    private static abstract class PartitionClause<C, T extends IDomain, PR, CR, FR, TR, OR, ER, DR, LR, RR, GR, VR, SR>
            extends InsertSupport.AssignmentSetClause<C, FieldMeta<T>, SR>
            implements MySQLQuery._PartitionClause<C, PR>
            , MySQLLoad._CharsetClause<C, CR>
            , MySQLLoad._FieldsColumnsClause<FR>
            , MySQLLoad._LinesClause<LR>
            , MySQLLoad._IgnoreLineClause<C, GR>
            , MySQLLoad._LineAfterIgnoreClause<GR>
            , MySQLLoad._ColumnOrVarListClause<C, VR>
            , Statement._RightParenClause<VR>
            , MySQLLoad._StaticColumnDualClause<VR>
            , MySQLLoad._TerminatedByClause<C, TR>
            , MySQLLoad._EnclosedByClause<C, ER>
            , MySQLLoad._EscapedByClause<C, DR>
            , MySQLLoad._OptionallyClause<C, OR>
            , MySQLLoad._StartingByClause<C, RR> {

        final List<MySQLWords> modifierList;

        final Path path;

        final TableMeta<T> table;

        private List<String> partitionList;

        private String charsetName;

        private boolean fieldOrColumnClause;

        private String columnTerminatedBy;

        private boolean optionallyEnclosed;

        private Character columnEnclosedBy;

        private Character columnEscapedBy;

        private boolean linesClause;

        private String lineTerminatedBy;

        private String lineStartingBy;

        private long ignoreRows = -1L;

        private List<_Expression> fieldVarList;

        private PartitionClause(LoadDataInfileClause<C> clause, TableMeta<T> table) {
            super(clause.criteriaContext, false);
            this.modifierList = clause.modifierList();
            this.path = clause.path;

            assert this.path != null;
            this.table = table;

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
            this.fieldOrColumnClause = true;
            return (FR) this;
        }

        @Override
        public final FR columns() {
            this.fieldOrColumnClause = true;
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
        public final TR terminatedBy(@Nullable String string) {
            if (string == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (this.linesClause) {
                this.lineTerminatedBy = string;
            } else if (this.fieldOrColumnClause) {
                this.columnTerminatedBy = string;
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return (TR) this;
        }

        @Override
        public final TR terminatedBy(Supplier<String> supplier) {
            return this.terminatedBy(supplier.get());
        }

        @Override
        public final TR terminatedBy(Function<C, String> function) {
            return this.terminatedBy(function.apply(this.criteria));
        }

        @Override
        public final TR ifTerminatedBy(Supplier<String> supplier) {
            final String string;
            string = supplier.get();
            if (string != null) {
                this.terminatedBy(string);
            }
            return (TR) this;
        }

        @Override
        public final TR ifTerminatedBy(Function<C, String> function) {
            final String string;
            string = function.apply(this.criteria);
            if (string != null) {
                this.terminatedBy(string);
            }
            return (TR) this;
        }


        @Override
        public final OR optionally() {
            this.optionallyEnclosed = true;
            return (OR) this;
        }

        @Override
        public final OR ifOptionally(Supplier<Boolean> supplier) {
            this.optionallyEnclosed = Boolean.TRUE.equals(supplier.get());
            return (OR) this;
        }

        @Override
        public final OR ifOptionally(Predicate<C> predicate) {
            this.optionallyEnclosed = predicate.test(this.criteria);
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
        final boolean containField(FieldMeta<?> field) {
            return field.tableMeta() == this.table;
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
                if (((FieldMeta<?>) columnOrVar).tableMeta() != this.table) {
                    String m = String.format("%s isn't belong of %s", columnOrVar, this.table);
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

            List<_Expression> fieldVarList = this.fieldVarList;
            if (fieldVarList == null) {
                fieldVarList = new ArrayList<>();
                this.fieldVarList = fieldVarList;
            }
            fieldVarList.add((ArmyExpression) columnOrVar);
        }


    }//PartitionClause


    private static final class SimplePartitionClause<C, T extends IDomain>
            extends PartitionClause<
            C,
            T,
            MySQLLoad._CharsetSpec<C, T>,
            MySQLLoad._FieldsColumnsSpec<C, T>,
            MySQLLoad._ColumnTerminatedBySpec<C, T>,
            MySQLLoad._IgnoreLineSpec<C, T>,
            MySQLLoad._ColumnEnclosedByClause<C, T>,
            MySQLLoad._ColumnEscapedBySpec<C, T>,
            MySQLLoad._LinesBeforeIgnoreSpec<C, T>,
            MySQLLoad._LineStartingBySpec<C, T>,
            MySQLLoad._LinesTerminatedBySpec<C, T>,
            MySQLLoad._ColumnOrVarListSpec<C, T>,
            MySQLLoad._LoadSetSpec<C, FieldMeta<T>>,
            MySQLLoad._LoadSetSpec<C, FieldMeta<T>>>
            implements MySQLLoad._PartitionSpec<C, T>
            , MySQLLoad._ColumnTerminatedBySpec<C, T> {

        private SimplePartitionClause(LoadDataInfileClause<C> clause, TableMeta<T> table) {
            super(clause, table);
        }

        @Override
        public MySQLLoad asLoadData() {
            return null;
        }


    }//SimplePartitionClause


    private static CriteriaException filePathIsNull(CriteriaContext criteriaContext, TableMeta<?> table) {
        String m = String.format("file path is null for %s", table);
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }


}
