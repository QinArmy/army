package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;

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


    static SelectionGroup buildDerivedGroup(String subQueryAlias) {
        return new SubQuerySelectionGroupImpl(subQueryAlias);
    }

    static SelectionGroup buildDerivedGroup(String subQueryAlias, List<String> derivedFieldNameList) {
        return new SubQueryListSelectionGroup(subQueryAlias, new ArrayList<>(derivedFieldNameList));
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
                    builder.append(Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                context.appendField(tableAlias, field);

                builder.append(Constant.SPACE_AS_SPACE)
                        .append(((DefaultFieldMeta<T>) field).fieldName);
            }

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            int index = 0;
            for (FieldMeta<T> field : this.fieldList) {
                if (index > 0) {
                    builder.append(Constant.SPACE_COMMA);
                }
                builder.append(Constant.SPACE)
                        .append(this.tableAlias)
                        .append(Constant.POINT)
                        .append(field.columnName())
                        .append(Constant.SPACE_AS_SPACE)
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
                    builder.append(Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                if (i < parentSize) {
                    context.appendField(parentAlias, field);
                } else {
                    context.appendField(childAlias, field);
                }
                builder.append(Constant.SPACE_AS_SPACE)
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
                    builder.append(Constant.SPACE_COMMA);
                }
                builder.append(Constant.SPACE);
                field = fieldList.get(i);
                if (i < parentSize) {
                    builder.append(parentAlias);
                } else {
                    builder.append(childAlias);
                }
                builder.append(Constant.POINT)
                        .append(field.columnName())
                        .append(Constant.SPACE_AS_SPACE)
                        .append(field.fieldName());

            }
            return builder.toString();
        }


    }//ChildTableGroup

    private static class SubQuerySelectionGroupImpl implements DerivedGroup, _SelfDescribed {

        final String subQueryAlias;

        private List<Selection> selectionList;

        private SubQuerySelectionGroupImpl(String subQueryAlias) {
            this.subQueryAlias = subQueryAlias;
        }

        @Override
        public final void finish(DerivedTable table, String alias) {
            if (this.selectionList != null) {
                throw new IllegalStateException("duplication");
            }
            if (!this.subQueryAlias.equals(alias)) {
                throw new IllegalArgumentException("subQueryAlias not match.");
            }
            this.selectionList = createSelectionList(table);
        }

        List<Selection> createSelectionList(DerivedTable table) {
            final List<Selection> selectionList = new ArrayList<>();
            for (SelectItem selectItem : table.selectItemList()) {
                if (selectItem instanceof Selection) {
                    selectionList.add((Selection) selectItem);
                } else if (selectItem instanceof SelectionGroup) {
                    selectionList.addAll(((SelectionGroup) selectItem).selectionList());
                } else {
                    throw _Exceptions.unknownSelectItem(selectItem);
                }
            }
            return Collections.unmodifiableList(selectionList);
        }

        @Override
        public String tableAlias() {
            return this.subQueryAlias;
        }

        @Override
        public final List<Selection> selectionList() {
            final List<Selection> selectionList = this.selectionList;
            assert selectionList != null;
            return selectionList;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final List<Selection> selectionList = this.selectionList;
            if (_CollectionUtils.isEmpty(selectionList)) {
                //here bug.
                throw new CriteriaException("DerivedSelectionGroup no selection.");
            }
            final StringBuilder builder = context.sqlBuilder();

            final _Dialect dialect = context.dialect();
            final String safeAlias = dialect.quoteIfNeed(this.subQueryAlias);
            final int size = selectionList.size();
            Selection selection;
            String safeFieldAlias;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(Constant.SPACE_COMMA);
                }
                selection = selectionList.get(i);
                safeFieldAlias = dialect.quoteIfNeed(selection.alias());
                builder.append(Constant.SPACE)
                        .append(safeAlias)
                        .append(Constant.POINT)
                        .append(safeFieldAlias)
                        .append(Constant.SPACE_AS_SPACE)
                        .append(safeFieldAlias);
            }


        }

    }// SubQuerySelectionGroupImpl


    private static final class SubQueryListSelectionGroup extends SubQuerySelectionGroupImpl {


        private final List<String> derivedFieldNameList;

        private SubQueryListSelectionGroup(String subQueryAlias, List<String> derivedFieldNameList) {
            super(subQueryAlias);
            this.derivedFieldNameList = Collections.unmodifiableList(derivedFieldNameList);
        }


        @Override
        List<Selection> createSelectionList(DerivedTable table) {
            final Set<String> filedNameSet = new HashSet<>(this.derivedFieldNameList);
            final List<Selection> selectionList = new ArrayList<>(filedNameSet.size());
            for (SelectItem selectItem : table.selectItemList()) {

                if (selectItem instanceof Selection) {
                    if (filedNameSet.contains(((Selection) selectItem).alias())) {
                        selectionList.add((Selection) selectItem);
                    }
                } else if (selectItem instanceof SelectionGroup) {
                    for (Selection selection : ((SelectionGroup) selectItem).selectionList()) {
                        if (filedNameSet.contains(selection.alias())) {
                            selectionList.add(selection);
                        }
                    }
                } else {
                    throw _Exceptions.unknownSelectItem(selectItem);
                }
            }
            final int fieldSize, fieldNameSize;
            fieldSize = selectionList.size();
            fieldNameSize = filedNameSet.size();

            if (fieldSize < fieldNameSize) {
                final Set<String> actualNameSet = new HashSet<>();
                for (Selection selection : selectionList) {
                    actualNameSet.
                            add(selection.alias());
                }
                final String m;
                filedNameSet.removeAll(actualNameSet);
                m = String.format("Not found derived fields[%s] in Derived table[%s]"
                        , filedNameSet, this.subQueryAlias);
                throw new CriteriaException(m);

            }
            return Collections.unmodifiableList(selectionList);
        }


    }//SubQueryListSelectionGroup


}
