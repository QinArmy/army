package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.DerivedTable;
import io.army.criteria.Selection;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._SelectionGroup;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
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
        return new TableFieldGroup<>(table, tableAlias);
    }

    static <T> _SelectionGroup groupWithoutId(ChildTableMeta<T> table, String tableAlias) {
        return new TableFieldGroup<>(tableAlias, table);
    }

    static DerivedGroup derivedGroup(String alias) {
        return new DerivedSelectionGroupImpl(alias);
    }


    /*################################## blow static inner class  ##################################*/


    private static final class TableFieldGroup<T> implements _SelectionGroup {

        private final String tableAlias;

        private final List<FieldMeta<T>> fieldList;

        private TableFieldGroup(TableMeta<T> table, String tableAlias) {
            this.tableAlias = tableAlias;
            this.fieldList = table.fieldList();
        }

        private TableFieldGroup(String tableAlias, ChildTableMeta<T> parent) {
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


    private static final class DerivedSelectionGroupImpl implements DerivedGroup {

        final String derivedAlias;

        private List<? extends Selection> selectionList;

        private DerivedSelectionGroupImpl(String derivedAlias) {
            this.derivedAlias = derivedAlias;
        }

        @Override
        public void finish(final DerivedTable table, final String alias) {
            if (this.selectionList != null) {
                throw new IllegalStateException("duplication");
            }
            if (!this.derivedAlias.equals(alias)) {
                throw new IllegalArgumentException("subQueryAlias not match.");
            }

            this.selectionList = ((_DerivedTable) table).refAllSelection();

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
            final StringBuilder builder = context.sqlBuilder();

            final DialectParser dialect = context.parser();
            final String safeAlias = dialect.identifier(this.derivedAlias);
            final int size = selectionList.size();
            Selection selection;
            String safeFieldAlias;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                selection = selectionList.get(i);
                safeFieldAlias = dialect.identifier(selection.selectionName());
                builder.append(_Constant.SPACE)
                        .append(safeAlias)
                        .append(_Constant.POINT)
                        .append(safeFieldAlias)
                        .append(_Constant.SPACE_AS_SPACE)
                        .append(safeFieldAlias);
            }


        }

    }// SubQuerySelectionGroupImpl




}
