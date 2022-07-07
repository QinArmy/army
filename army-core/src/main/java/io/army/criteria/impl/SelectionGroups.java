package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.DerivedTable;
import io.army.criteria.Selection;
import io.army.criteria.SelectionGroup;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._DialectParser;
import io.army.dialect._DialectUtils;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class SelectionGroups {

    private SelectionGroups() {
        throw new UnsupportedOperationException();
    }

    static <T extends IDomain> SelectionGroup singleGroup(
            String tableAlias, List<FieldMeta<T>> fieldList) {
        return new TableFieldGroup<>(tableAlias, fieldList);
    }

    static <T extends IDomain> SelectionGroup singleGroup(TableMeta<T> table, String tableAlias) {
        return new TableFieldGroup<>(table, tableAlias);
    }

    static <T extends IDomain> SelectionGroup groupWithoutId(TableMeta<T> table, String tableAlias) {
        return new TableFieldGroup<>(tableAlias, table);
    }

    static <T extends IDomain> SelectionGroup childGroup(ChildTableMeta<T> child, String childAlias, String parentAlias) {
        return new ChildTableGroup<>(child, childAlias, child.parentMeta(), parentAlias);
    }


    static DerivedGroup derivedGroup(String alias) {
        return new DerivedSelectionGroupImpl(alias);
    }

    static DerivedGroup derivedGroup(String alias, List<String> derivedFieldNameList) {
        return new DerivedFieldGroup(alias, new ArrayList<>(derivedFieldNameList));
    }


    /*################################## blow static inner class  ##################################*/


    private static final class TableFieldGroup<T extends IDomain> implements SelectionGroup, _SelfDescribed {

        private final String tableAlias;

        private final List<FieldMeta<T>> fieldList;

        private TableFieldGroup(String tableAlias, List<FieldMeta<T>> fieldList) {
            this.tableAlias = tableAlias;
            this.fieldList = _CollectionUtils.asUnmodifiableList(fieldList);
        }

        private TableFieldGroup(TableMeta<T> table, String tableAlias) {
            this.tableAlias = tableAlias;
            this.fieldList = table.fieldList();
        }

        private TableFieldGroup(String tableAlias, TableMeta<T> parent) {
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
        public List<? extends Selection> selectionList() {
            return this.fieldList;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder();
            final String tableAlias = this.tableAlias;

            final List<FieldMeta<T>> fieldList = this.fieldList;
            final int size = fieldList.size();
            FieldMeta<T> field;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                context.appendField(tableAlias, field);

                builder.append(_Constant.SPACE_AS_SPACE)
                        .append(((DefaultFieldMeta<T>) field).fieldName);
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


    private static final class ChildTableGroup<P extends IDomain, T extends IDomain>
            implements SelectionGroup, _SelfDescribed {

        private final String childAlias;

        private final String parentAlias;

        private final List<FieldMeta<?>> fieldList;

        private final int parentSize;

        private ChildTableGroup(ChildTableMeta<T> child, String childAlias
                , ParentTableMeta<P> parent, String parentAlias) {
            this.parentAlias = parentAlias;
            this.childAlias = childAlias;

            final Collection<FieldMeta<P>> parentFields = parent.fieldList();
            final Collection<FieldMeta<T>> childFields = child.fieldList();
            this.parentSize = parentFields.size() - 1;

            final List<FieldMeta<?>> fieldList = new ArrayList<>(this.parentSize + childFields.size());
            for (FieldMeta<P> field : parentFields) {
                if (field instanceof PrimaryFieldMeta) {
                    continue;
                }
                fieldList.add(field);
            }
            fieldList.addAll(childFields);
            this.fieldList = Collections.unmodifiableList(fieldList);
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.fieldList;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void appendSql(final _SqlContext context) {

            final StringBuilder builder = context.sqlBuilder();

            final List<FieldMeta<?>> fieldList = this.fieldList;
            final int parentSize = this.parentSize;
            final String parentAlias = this.parentAlias;
            final String childAlias = this.childAlias;


            final int size = fieldList.size();
            FieldMeta<?> field;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                if (i < parentSize) {
                    context.appendField(parentAlias, field);
                } else {
                    context.appendField(childAlias, field);
                }
                builder.append(_Constant.SPACE_AS_SPACE)
                        .append(((DefaultFieldMeta<T>) field).fieldName);

            }

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            final List<FieldMeta<?>> fieldList = this.fieldList;
            final int parentSize = this.parentSize;
            final String parentAlias = this.parentAlias;
            final String childAlias = this.childAlias;

            final int size = fieldList.size();
            FieldMeta<?> field;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(_Constant.SPACE);
                field = fieldList.get(i);
                if (i < parentSize) {
                    builder.append(parentAlias);
                } else {
                    builder.append(childAlias);
                }
                builder.append(_Constant.POINT)
                        .append(field.columnName())
                        .append(_Constant.SPACE_AS_SPACE)
                        .append(field.fieldName());

            }
            return builder.toString();
        }


    }//ChildTableGroup

    private static class DerivedSelectionGroupImpl implements DerivedGroup, _SelfDescribed {

        final String derivedAlias;

        private List<Selection> selectionList;

        private DerivedSelectionGroupImpl(String derivedAlias) {
            this.derivedAlias = derivedAlias;
        }

        @Override
        public final void finish(DerivedTable table, String alias) {
            if (this.selectionList != null) {
                throw new IllegalStateException("duplication");
            }
            if (!this.derivedAlias.equals(alias)) {
                throw new IllegalArgumentException("subQueryAlias not match.");
            }
            if (this instanceof DerivedFieldGroup) {
                this.selectionList = ((DerivedFieldGroup) this).createSelectionList(table);
            } else {
                this.selectionList = Collections.unmodifiableList(_DialectUtils.flatSelectItem(table.selectItemList()));
            }

        }


        @Override
        public final String tableAlias() {
            return this.derivedAlias;
        }

        @Override
        public final List<Selection> selectionList() {
            final List<Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                throw new CriteriaException("currently,couldn't reference selection,please check syntax.");
            }
            return selectionList;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final List<Selection> selectionList = this.selectionList;
            if (selectionList == null || selectionList.size() == 0) {
                //here bug.
                throw new CriteriaException("DerivedSelectionGroup no selection.");
            }
            final StringBuilder builder = context.sqlBuilder();

            final _DialectParser dialect = context.dialect();
            final String safeAlias = dialect.identifier(this.derivedAlias);
            final int size = selectionList.size();
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

    }// SubQuerySelectionGroupImpl


    private static final class DerivedFieldGroup extends DerivedSelectionGroupImpl {


        private final List<String> derivedFieldNameList;

        private DerivedFieldGroup(String subQueryAlias, List<String> derivedFieldNameList) {
            super(subQueryAlias);
            this.derivedFieldNameList = _CollectionUtils.asUnmodifiableList(derivedFieldNameList);
        }


        private List<Selection> createSelectionList(DerivedTable table) {
            final List<String> derivedFieldNameList = this.derivedFieldNameList;
            final List<Selection> selectionList = new ArrayList<>(derivedFieldNameList.size());
            Selection selection;
            for (String selectionAlias : derivedFieldNameList) {
                selection = table.selection(selectionAlias);
                if (selection == null) {
                    String m = String.format("unknown derived field %s.%s .", this.derivedAlias, selectionAlias);
                    throw new CriteriaException(m);
                }
                selectionList.add(selection);
            }
            return Collections.unmodifiableList(selectionList);
        }

    }//DerivedFieldGroup


}
