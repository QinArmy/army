package io.army.criteria.impl;

import io.army.criteria.SortItem;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._SqlContext;

final class SortItemImpl implements _SortItem {

    private final SortItem sortItem;

    private final boolean ascExp;

    SortItemImpl(SortItem sortItem, boolean ascExp) {
        this.sortItem = sortItem;
        this.ascExp = ascExp;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        ((_SelfDescribed) this.sortItem).appendSql(context);
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
