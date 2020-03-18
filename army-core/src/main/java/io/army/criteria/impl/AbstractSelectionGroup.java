package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.criteria.SelectionGroup;
import io.army.criteria.SubQuery;
import io.army.dialect.TableDML;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.*;

abstract class AbstractSelectionGroup implements SelectionGroup {

    private AbstractSelectionGroup() {
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

    static SelectionGroup buildForField(String tableAlias, List<FieldMeta<?, ?>> fieldMetaList) {
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
        List<Selection> selectionList = new ArrayList<>(tableMeta.fieldCollection().size());
        selectionList.addAll(tableMeta.fieldCollection());
        return new ImmutableSelectionGroup(tableAlias, selectionList);
    }

    static SelectionGroup build(String subQueryAlias) {
        return new SubQuerySelectGroupImpl(subQueryAlias);
    }

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

    private static final class SubQuerySelectGroupImpl extends AbstractSelectionGroup
            implements SelectionGroup.SubQuerySelectGroup {

        private final String subQueryAlias;

        private List<Selection> selectionList;

        private SubQuerySelectGroupImpl(String subQueryAlias) {
            this.subQueryAlias = subQueryAlias;
        }

        @Override
        public String tableAlias() {
            return this.subQueryAlias;
        }

        @Override
        public void finish(SubQuery subQuery) {
            Assert.state(this.selectionList == null, "selectionList only update once.");
            this.selectionList = subQuery.selectionList();
        }

        @Override
        public List<Selection> selectionList() {
            Assert.state(this.selectionList != null, "selectionList is null,SubQuerySelectGroup state error.");
            return this.selectionList;
        }

    }

    private static final class ListSelectGroupImpl implements SelectionGroup.ListSelectGroup {

        private final List<Selection> originalSelectionList;

        private List<Selection> selectionList;

        private List<SelectionGroup> selectionGroupList;

        private ListSelectGroupImpl(List<Selection> originalSelectionList) {
            this.originalSelectionList = Collections.unmodifiableList(new ArrayList<>(originalSelectionList));
        }

        @Override
        public String tableAlias() {
            return null;
        }

        @Override
        public List<Selection> selectionList() {
            return null;
        }

        @Override
        public boolean tryFinish(Map<TableMeta<?>, String> tableAliasMap) {
            return false;
        }

        @Override
        public void appendSQL(SQLContext context) {

        }
    }


}
