package io.army.criteria.impl;

import io.army.criteria.SortPart;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;

final class SortPartImpl implements _SelfDescribed, SortPart {

    private final SortPart sortPart;

    private final boolean ascExp;

    SortPartImpl(SortPart sortPart, boolean ascExp) {
        this.sortPart = sortPart;
        this.ascExp = ascExp;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        ((_SelfDescribed) this.sortPart).appendSql(context);

        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE);

        if (this.ascExp) {
            builder.append(" ASC");
        } else {
            builder.append(" DESC");
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append(this.sortPart);
        if (this.ascExp) {
            builder.append(" ASC");
        } else {
            builder.append(" DESC");
        }
        return builder.toString();
    }

}
