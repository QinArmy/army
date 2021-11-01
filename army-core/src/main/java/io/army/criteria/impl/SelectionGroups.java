package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.dialect.SqlDialect;
import io.army.dialect.SQLBuilder;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class SelectionGroups implements SelectionGroup {


    static <T extends IDomain> SelectionGroups buildTableGroup(
            String tableAlias, List<FieldMeta<T, ?>> fieldMetaList) {
        return new TableFieldGroup<>(tableAlias, fieldMetaList);
    }

    static SelectionGroups buildDerivedGroup(String subQueryAlias) {
        return new SubQuerySelectionGroupImpl(subQueryAlias);
    }

    static SelectionGroups buildDerivedGroup(String subQueryAlias, List<String> derivedFieldNameList) {
        return new SubQueryListSelectionGroup(subQueryAlias, new ArrayList<>(derivedFieldNameList));
    }

    private SelectionGroups() {
    }

    @Override
    public void appendSQL(SQLContext context) {
        SQLBuilder builder = context.sqlBuilder().append(" ");
        final SqlDialect sql = context.dql();
        final String safeTableAlias = sql.quoteIfNeed(tableAlias());
        int index = 0;
        for (Selection selection : selectionList()) {
            if (index > 0) {
                builder.append(",");
            }
            String safeAlias = sql.quoteIfNeed(selection.alias());
            builder.append(safeTableAlias)
                    .append(".")
                    .append(safeAlias)
                    .append(" AS ")
                    .append(safeAlias);
            index++;
        }
    }


    /*################################## blow static inner class  ##################################*/


    private static final class TableFieldGroup<T extends IDomain> extends SelectionGroups
            implements TableSelectionGroup {

        private final String tableAlias;

        private final List<FieldMeta<T, ?>> fieldMetaList;

        public TableFieldGroup(String tableAlias, List<FieldMeta<T, ?>> fieldMetaList) {
            this.tableAlias = tableAlias;
            this.fieldMetaList = Collections.unmodifiableList(fieldMetaList);
        }

        @Override
        public String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.fieldMetaList;
        }

        @Override
        public void appendSQL(SQLContext context) {
            SQLBuilder builder = context.sqlBuilder().append(" ");
            final SqlDialect sql = context.dql();
            final String safeTableAlias = sql.quoteIfNeed(this.tableAlias);
            int index = 0;
            for (FieldMeta<T, ?> fieldMeta : this.fieldMetaList) {
                if (index > 0) {
                    builder.append(",");
                }
                builder.append(safeTableAlias)
                        .append(".")
                        .append(sql.quoteIfNeed(fieldMeta.fieldName()))
                        .append(" AS ")
                        .append(sql.quoteIfNeed(fieldMeta.propertyName()));
                index++;
            }
        }
    }

    private static final class SubQuerySelectionGroupImpl extends SelectionGroups
            implements SubQuerySelectionGroup {

        private final String subQueryAlias;

        private List<Selection> selectionList;

        private SubQuerySelectionGroupImpl(String subQueryAlias) {
            this.subQueryAlias = subQueryAlias;
        }

        @Override
        public String tableAlias() {
            return this.subQueryAlias;
        }

        @Override
        public void finish(SubQuery subQuery, String subQueryAlias) {
            Assert.state(this.selectionList == null, "selectPartList only singleUpdate once.");
            Assert.isTrue(this.subQueryAlias.equals(subQueryAlias)
                    , () -> String.format("SelectionGroup subQueryAlias[%s] and subQueryAlias[%s] not match"
                            , this.subQueryAlias, subQueryAlias));

            List<Selection> selectionList = new ArrayList<>();

            for (SelectPart selectPart : subQuery.selectPartList()) {
                if (selectPart instanceof Selection) {
                    selectionList.add((Selection) selectPart);
                } else if (selectPart instanceof SelectionGroup) {
                    selectionList.addAll(((SelectionGroup) selectPart).selectionList());
                } else {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                            , "a selectPart of SubQuery[%s] isn't Selection or SelectionGroup.", this.subQueryAlias);
                }
            }
            this.selectionList = Collections.unmodifiableList(selectionList);
        }

        @Override
        public List<Selection> selectionList() {
            Assert.state(this.selectionList != null, "selectPartList is null,SubQuerySelectGroup state error.");
            return this.selectionList;
        }

    }


    private static final class SubQueryListSelectionGroup extends SelectionGroups
            implements SubQuerySelectionGroup {

        private final String subQueryAlias;

        private List<String> derivedFieldNameList;

        private List<Selection> selectionList;

        private SubQueryListSelectionGroup(String subQueryAlias, List<String> derivedFieldNameList) {
            this.subQueryAlias = subQueryAlias;
            this.derivedFieldNameList = derivedFieldNameList;
        }

        @Override
        public void finish(SubQuery subQuery, String subQueryAlias) {
            Assert.state(this.selectionList == null, "SubQuerySelectGroup only singleUpdate once.");
            List<Selection> selectionList = new ArrayList<>(this.derivedFieldNameList.size());
            for (String derivedField : this.derivedFieldNameList) {
                selectionList.add(subQuery.selection(derivedField));
            }
            this.selectionList = Collections.unmodifiableList(selectionList);
            this.derivedFieldNameList = null;
        }

        @Override
        public String tableAlias() {
            return this.subQueryAlias;
        }

        @Override
        public List<Selection> selectionList() {
            Assert.state(this.selectionList != null, () -> String.format("selectionList is null,%s not finished"
                    , SubQueryListSelectionGroup.class.getSimpleName()));
            return this.selectionList;
        }

    }


}
