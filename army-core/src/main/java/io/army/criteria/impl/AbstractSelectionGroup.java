package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.dialect.TableDML;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

abstract class AbstractSelectionGroup implements SelectionGroup {

    private AbstractSelectionGroup() {
    }

    @Override
    public final String toString() {
        return super.toString();
    }

    @Override
    public final void appendSQL(SQLContext context) {
        final String tableAlias = this.tableAlias();
        final List<Selection> selectionList = this.selectionList();

        final StringBuilder builder = context.stringBuilder();
        final TableDML dml = context.dml();

        builder.append(" ");

        Selection selection;
        for (Iterator<Selection> iterator = selectionList.iterator(); iterator.hasNext(); ) {
            selection = iterator.next();
            builder.append(dml.quoteIfNeed(tableAlias))
                    .append(".")
                    .append(dml.quoteIfNeed(selection.alias()));

            if (iterator.hasNext()) {
                builder.append(",");
            }

        }


    }

    static SelectionGroup buildForFieldList(String tableAlias, List<FieldMeta<?, ?>> fieldMetaList) {
        List<Selection> selectionList = new ArrayList<>(fieldMetaList.size());
        TableMeta<?> tableMeta = null;
        for (FieldMeta<?, ?> fieldMeta : fieldMetaList) {
            if (tableMeta == null) {
                tableMeta = fieldMeta.tableMeta();
            }
            if (fieldMeta.tableMeta() != tableMeta) {
                throw new IllegalArgumentException(
                        String.format("Field[%s] and table[%s] not match.", fieldMeta, tableMeta));
            }
            selectionList.add(fieldMeta);
        }
        return new ImmutableSelectionGroup(tableAlias, selectionList);
    }

    static SelectionGroup build(TableMeta<?> tableMeta, String tableAlias) {
        return new ImmutableSelectionGroup(tableAlias, new ArrayList<>(tableMeta.fieldCollection()));
    }

    static SelectionGroup build(String subQueryAlias) {
        return new SubQuerySelectionGroupImpl(subQueryAlias);
    }

    static SelectionGroup build(String subQueryAlias, List<String> derivedFieldNameList) {
        return new SubQueryListSelectionGroup(subQueryAlias, new ArrayList<>(derivedFieldNameList));
    }

    static SelectionGroup buildForFields(String tableAlias, List<Selection> fieldMetaList) {
        return new ImmutableSelectionGroup(tableAlias, fieldMetaList);
    }


    /*################################## blow static inner class  ##################################*/


    private static final class ImmutableSelectionGroup extends AbstractSelectionGroup {

        private final String tableAlias;

        private final List<Selection> selectionList;

        private ImmutableSelectionGroup(String tableAlias, List<Selection> selectionList) {
            this.tableAlias = tableAlias;
            this.selectionList = Collections.unmodifiableList(selectionList);
        }

        @Override
        public String tableAlias() {
            return tableAlias;
        }

        @Override
        public List<Selection> selectionList() {
            return selectionList;
        }

    }

    private static final class SubQuerySelectionGroupImpl extends AbstractSelectionGroup
            implements SubQuerySelectGroup {

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
        public void finish(SubQuery subQuery) {
            Assert.state(this.selectionList == null, "selectPartList only update once.");
            List<Selection> selectionList = new ArrayList<>();

            for (SelectPart selectPart : subQuery.selectPartList()) {
                if (selectPart instanceof Selection) {
                    selectionList.add((Selection) selectPart);
                } else if (selectPart instanceof SelectionGroup) {
                    selectionList.addAll(((SelectionGroup) selectPart).selectionList());
                } else {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                            , "a selectPart of SubQuery[%s] isn't Selection or SelectionGroup.", subQueryAlias);
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


    private static final class SubQueryListSelectionGroup extends AbstractSelectionGroup
            implements SubQuerySelectGroup {

        private final String subQueryAlias;

        private List<String> derivedFieldNameList;

        private List<Selection> selectionList;

        private SubQueryListSelectionGroup(String subQueryAlias, List<String> derivedFieldNameList) {
            this.subQueryAlias = subQueryAlias;
            this.derivedFieldNameList = Collections.unmodifiableList(derivedFieldNameList);
        }

        @Override
        public void finish(SubQuery subQuery) {
            Assert.state(this.selectionList == null, "SubQuerySelectGroup only update once.");
            List<Selection> selectionList = new ArrayList<>(derivedFieldNameList.size());
            for (String derivedField : derivedFieldNameList) {
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
