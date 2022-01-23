package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelectionGroup;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.*;

abstract class SelectionGroups implements _SelectionGroup {


    static <T extends IDomain> SelectionGroup singleGroup(
            String tableAlias, List<FieldMeta<T, ?>> fieldList) {
        return new TableFieldGroup<>(tableAlias, fieldList);
    }

    static <T extends IDomain> SelectionGroup singleGroup(TableMeta<T> table, String tableAlias) {
        return new TableFieldGroup<>(table, tableAlias);
    }

    static <T extends IDomain> SelectionGroup parentGroup(ParentTableMeta<T> table, String tableAlias) {
        return new TableFieldGroup<>(tableAlias, table);
    }


    static SelectionGroup buildDerivedGroup(String subQueryAlias) {
        return new SubQuerySelectionGroupImpl(subQueryAlias);
    }

    static SelectionGroup buildDerivedGroup(String subQueryAlias, List<String> derivedFieldNameList) {
        return new SubQueryListSelectionGroup(subQueryAlias, new ArrayList<>(derivedFieldNameList));
    }

    private SelectionGroups() {
    }

    @Override
    public void appendSql(final _SqlContext context) {
        StringBuilder builder = context.sqlBuilder().append(" ");
        final _Dialect dialect = context.dialect();
        final String safeTableAlias = dialect.quoteIfNeed(tableAlias());
        int index = 0;
        for (Selection selection : selectionList()) {
            if (index > 0) {
                builder.append(",");
            }
            String safeAlias = dialect.quoteIfNeed(selection.alias());
            builder.append(safeTableAlias)
                    .append(".")
                    .append(safeAlias)
                    .append(" AS ")
                    .append(safeAlias);
            index++;
        }
    }


    /*################################## blow static inner class  ##################################*/


    private static final class TableFieldGroup<T extends IDomain> implements _SelectionGroup {

        private final String tableAlias;

        private final List<FieldMeta<T, ?>> fieldList;

        private TableFieldGroup(String tableAlias, List<FieldMeta<T, ?>> fieldList) {
            this.tableAlias = tableAlias;
            this.fieldList = CollectionUtils.asUnmodifiableList(fieldList);
        }

        private TableFieldGroup(TableMeta<T> table, String tableAlias) {
            this.tableAlias = tableAlias;
            this.fieldList = Collections.unmodifiableList(new ArrayList<>(table.fields()));
        }

        private TableFieldGroup(String tableAlias, ParentTableMeta<T> parent) {
            Collection<FieldMeta<T, ?>> parentFields = parent.fields();
            final List<FieldMeta<T, ?>> fieldList = new ArrayList<>(parentFields.size());
            for (FieldMeta<T, ?> field : parentFields) {
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
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder();
            final _Dialect dialect = context.dialect();
            final String tableAlias = this.tableAlias;
            int index = 0;
            for (FieldMeta<T, ?> field : this.fieldList) {
                if (index > 0) {
                    builder.append(Constant.SPACE)
                            .append(Constant.COMMA);
                }
                context.appendField(tableAlias, field);
                builder.append(" AS ")
                        .append(dialect.quoteIfNeed(field.fieldName()));
                index++;
            }

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder()
                    .append(Constant.SPACE);
            int index = 0;
            for (FieldMeta<T, ?> field : fieldList) {
                if (index > 0) {
                    builder.append(Constant.SPACE)
                            .append(Constant.COMMA);
                }
                builder.append(Constant.SPACE)
                        .append(this.tableAlias)
                        .append(Constant.POINT)
                        .append(field.columnName())
                        .append(" AS ")
                        .append(field.fieldName());
                index++;
            }

            return builder.toString();
        }

    }//TableFieldGroup

    private static class SubQuerySelectionGroupImpl implements DerivedGroup, _SelectionGroup {

        final String subQueryAlias;

        private List<Selection> selectionList;

        private SubQuerySelectionGroupImpl(String subQueryAlias) {
            this.subQueryAlias = subQueryAlias;
        }

        @Override
        public final String tableAlias() {
            return this.subQueryAlias;
        }

        @Override
        public final void finish(SubQuery subQuery, String subQueryAlias) {
            if (this.selectionList != null) {
                throw new IllegalStateException("duplication");
            }
            if (!this.subQueryAlias.equals(subQueryAlias)) {
                throw new IllegalArgumentException("subQueryAlias not match.");
            }
            this.selectionList = createSelectionList(subQuery);
        }

        List<Selection> createSelectionList(SubQuery subQuery) {
            final List<Selection> selectionList = new ArrayList<>();
            for (SelectPart selectPart : subQuery.selectPartList()) {
                if (selectPart instanceof Selection) {
                    selectionList.add((Selection) selectPart);
                } else if (selectPart instanceof SelectionGroup) {
                    selectionList.addAll(((SelectionGroup) selectPart).selectionList());
                } else {
                    throw _Exceptions.unknownSelectPart(selectPart);
                }
            }
            return Collections.unmodifiableList(selectionList);
        }

        @Override
        public final List<Selection> selectionList() {
            _Assert.state(this.selectionList != null, "selectPartList is null,SubQuerySelectGroup state error.");
            return this.selectionList;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final List<Selection> selectionList = this.selectionList;
            if (CollectionUtils.isEmpty(selectionList)) {
                //here bug.
                throw new CriteriaException("DerivedSelectionGroup no selection.");
            }
            final StringBuilder builder = context.sqlBuilder();

            final _Dialect dialect = context.dialect();
            final String safeAlias = dialect.quoteIfNeed(this.subQueryAlias);
            int index = 0;
            for (Selection selection : selectionList) {
                if (index > 0) {
                    builder.append(Constant.SPACE)
                            .append(Constant.COMMA);
                }

                builder.append(Constant.SPACE)
                        .append(safeAlias)
                        .append(Constant.POINT)
                        .append(dialect.quoteIfNeed(selection.alias()));
                index++;
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
        List<Selection> createSelectionList(SubQuery subQuery) {
            final Set<String> filedNameSet = new HashSet<>(this.derivedFieldNameList);
            final List<Selection> selectionList = new ArrayList<>(filedNameSet.size());
            for (SelectPart selectPart : subQuery.selectPartList()) {

                if (selectPart instanceof Selection) {
                    if (filedNameSet.contains(((Selection) selectPart).alias())) {
                        selectionList.add((Selection) selectPart);
                    }
                } else if (selectPart instanceof SelectionGroup) {
                    for (Selection selection : ((SelectionGroup) selectPart).selectionList()) {
                        if (filedNameSet.contains(selection.alias())) {
                            selectionList.add(selection);
                        }
                    }
                } else {
                    throw _Exceptions.unknownSelectPart(selectPart);
                }
            }
            if (selectionList.size() != this.derivedFieldNameList.size()) {
                Set<String> actualNameSet = new HashSet<>();
                for (Selection selection : selectionList) {
                    actualNameSet.add(selection.alias());
                }
                filedNameSet.removeAll(actualNameSet);
                String m = String.format("Not found derived fields[%s] in Derived table[%s]"
                        , filedNameSet, this.subQueryAlias);
                throw new CriteriaException(m);
            }
            return Collections.unmodifiableList(selectionList);
        }


    }


}
