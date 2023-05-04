package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Selection;
import io.army.criteria.impl.inner.*;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class SelectionGroups {

    private SelectionGroups() {
        throw new UnsupportedOperationException();
    }

    static <T> _SelectionGroup singleGroup(TableMeta<T> table, String tableAlias) {
        return new TableFieldGroupImpl<>(table, tableAlias);
    }

    static <T> _SelectionGroup groupWithoutId(ChildTableMeta<T> table, String tableAlias) {
        return new TableFieldGroupImpl<>(tableAlias, table);
    }

    /**
     * for RETURNING clause
     */
    static <T> _SelectionGroup insertTableGroup(TableMeta<T> insertTable) {
        return new InsertTableGroup<>(insertTable);
    }

    static DerivedFieldGroup derivedGroup(String alias) {
        return new DelayDerivedSelectionGroup(alias);
    }

    static _SelectionGroup derivedGroup(_SelectionMap table, String alias) {
        return new DerivedSelectionGroup(table, alias);
    }

    /*-------------------below private method -------------------*/

    private static void appendDerivedFieldGroup(final String derivedAlias, final List<? extends Selection> selectionList,
                                                final _SqlContext context) {

        final StringBuilder builder = context.sqlBuilder();

        final DialectParser dialect = context.parser();
        final String safeAlias = dialect.identifier(derivedAlias);
        final int size = selectionList.size();
        assert size > 0;

        Selection selection;
        String safeFieldAlias;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            selection = selectionList.get(i);
            safeFieldAlias = dialect.identifier(selection.alias());
            builder.append(_Constant.SPACE)
                    .append(safeAlias)
                    .append(_Constant.POINT)
                    .append(safeFieldAlias)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(safeFieldAlias);
        }
    }

    private static void appendDerivedRow(final String derivedAlias, final List<? extends Selection> selectionList,
                                         final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder();

        final DialectParser dialect = context.parser();
        final String safeDerivedAlias = dialect.identifier(derivedAlias);
        final int size = selectionList.size();
        assert size > 0;

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(_Constant.SPACE)
                    .append(safeDerivedAlias)
                    .append(_Constant.POINT);

            dialect.identifier(selectionList.get(i).alias(), builder);

        }

    }


    /*################################## blow static inner class  ##################################*/


    /**
     * for RETURNING clause
     */
    private static final class InsertTableGroup<T> implements _SelectionGroup._TableFieldGroup, ArmyRowExpression {

        private final TableMeta<T> insertTable;

        private InsertTableGroup(TableMeta<T> insertTable) {
            this.insertTable = insertTable;
        }

        @Override
        public String tableAlias() {
            //no bug,never here
            throw new UnsupportedOperationException();
        }

        @Override
        public void appendSelectItem(final _SqlContext context) {

            final List<FieldMeta<T>> fieldList;
            fieldList = this.insertTable.fieldList();

            final int fieldSize;
            fieldSize = fieldList.size();

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            final DialectParser parser;
            parser = context.parser();
            FieldMeta<?> field;
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                context.appendField(field);
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                parser.identifier(field.fieldName(), sqlBuilder);

            }

        }

        @Override
        public void appendSql(final _SqlContext context) {
            final List<FieldMeta<T>> fieldList;
            fieldList = this.insertTable.fieldList();

            final int fieldSize;
            fieldSize = fieldList.size();

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendField(fieldList.get(i));

            }

        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.insertTable.fieldList();
        }

        @Override
        public boolean isLegalGroup(final @Nullable TableMeta<?> table) {
            return table == this.insertTable;
        }


    }//InsertTableGroup


    static final class TableFieldGroupImpl<T> implements _SelectionGroup._TableFieldGroup, ArmyRowExpression {

        private final String tableAlias;

        private final List<FieldMeta<T>> fieldList;

        private TableFieldGroupImpl(TableMeta<T> table, String tableAlias) {
            this.tableAlias = tableAlias;
            this.fieldList = table.fieldList();
        }

        private TableFieldGroupImpl(String tableAlias, ChildTableMeta<T> parent) {
            final List<FieldMeta<T>> fields = parent.fieldList();
            final List<FieldMeta<T>> fieldList = new ArrayList<>(fields.size() - 1);
            for (FieldMeta<T> field : fields) {
                if (field instanceof PrimaryFieldMeta) {
                    continue;
                }
                fieldList.add(field);
            }
            this.tableAlias = tableAlias;
            this.fieldList = Collections.unmodifiableList(fieldList);
        }


        @Override
        public String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.fieldList;
        }

        @Override
        public boolean isLegalGroup(final @Nullable TableMeta<?> table) {
            return table != null && table == this.fieldList.get(0).tableMeta();
        }

        @Override
        public void appendSelectItem(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder();
            final String tableAlias = this.tableAlias;
            final DialectParser parser;
            parser = context.parser();

            final List<FieldMeta<T>> fieldList = this.fieldList;
            final int size = fieldList.size();
            FieldMeta<T> field;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                context.appendField(tableAlias, field);

                builder.append(_Constant.SPACE_AS_SPACE);
                parser.identifier(((TableFieldMeta<T>) field).fieldName, builder);
            }

        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder();
            final String tableAlias = this.tableAlias;

            final List<FieldMeta<T>> fieldList = this.fieldList;
            final int size = fieldList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                context.appendField(tableAlias, fieldList.get(i));
            }

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            int index = 0;
            for (FieldMeta<T> field : this.fieldList) {
                if (index > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(_Constant.SPACE)
                        .append(this.tableAlias)
                        .append(_Constant.POINT)
                        .append(field.columnName())
                        .append(_Constant.SPACE_AS_SPACE)
                        .append(field.fieldName());
                index++;
            }
            return builder.toString();
        }

    }//TableFieldGroup


    private static final class DerivedSelectionGroup implements _SelectionGroup, ArmyRowExpression {

        private final String derivedAlias;

        private final List<? extends Selection> selectionList;

        DerivedSelectionGroup(_SelectionMap table, String alias) {
            this.derivedAlias = alias;
            this.selectionList = table.refAllSelection();
        }

        @Override
        public void appendSelectItem(final _SqlContext context) {
            appendDerivedFieldGroup(this.derivedAlias, this.selectionList, context);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            appendDerivedRow(this.derivedAlias, this.selectionList, context);
        }

        @Override
        public String tableAlias() {
            return this.derivedAlias;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.selectionList;
        }

    }//DerivedSelectionGroup


    private static final class DelayDerivedSelectionGroup implements DerivedFieldGroup, ArmyRowExpression {

        private final String derivedAlias;

        private List<? extends Selection> selectionList;

        private DelayDerivedSelectionGroup(String derivedAlias) {
            this.derivedAlias = derivedAlias;
        }

        @Override
        public void finish(final _SelectionMap table, final String alias) {
            assert table instanceof _DerivedTable || table instanceof _Cte || table instanceof _DoneFuncBlock;
            if (this.selectionList != null) {
                throw new IllegalStateException("duplication");
            }
            if (!this.derivedAlias.equals(alias)) {
                throw new IllegalArgumentException("subQueryAlias not match.");
            }

            this.selectionList = table.refAllSelection();

        }


        @Override
        public String tableAlias() {
            return this.derivedAlias;
        }

        @Override
        public List<? extends Selection> selectionList() {
            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                String m = "currently,couldn't reference selection,please check syntax.";
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            return selectionList;
        }

        @Override
        public void appendSelectItem(final _SqlContext context) {
            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList == null || selectionList.size() == 0) {
                throw new CriteriaException("DerivedSelectionGroup no selection.");
            }
            appendDerivedFieldGroup(this.derivedAlias, selectionList, context);

        }

        @Override
        public void appendSql(final _SqlContext context) {
            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList == null || selectionList.size() == 0) {
                throw new CriteriaException("DerivedSelectionGroup no selection.");
            }
            appendDerivedRow(this.derivedAlias, selectionList, context);
        }


    }// SubQuerySelectionGroupImpl


}
