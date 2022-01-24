package io.army.criteria.impl;

import io.army.dialect._SqlContext;

final class SortItemImpl implements ArmySortItem {

    private final ArmySortItem sortItem;

    private final boolean ascExp;

    SortItemImpl(ArmySortItem sortItem, boolean ascExp) {
        this.sortItem = sortItem;
        this.ascExp = ascExp;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        this.sortItem.appendSql(context);
        if (this.ascExp) {
            context.sqlBuilder()
                    .append(" ASC");
        } else {
            context.sqlBuilder()
                    .append(" DESC");
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append(this.sortItem);
        if (this.ascExp) {
            builder.append(" ASC");
        } else {
            builder.append(" DESC");
        }
        return builder.toString();
    }

}
