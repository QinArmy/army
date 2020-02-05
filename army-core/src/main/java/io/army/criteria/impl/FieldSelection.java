package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.sql.JDBCType;

/**
 * created  on 2019-02-22.
 */
final class FieldSelection<T extends IDomain, F> implements Selection {

    private final FieldMeta<T, F> field;

    private final String tableAlias;

    private final String alias;

    FieldSelection(@NonNull FieldMeta<T, F> field, @Nullable String tableAlias, @NonNull String alias) {
        this.field = field;
        this.tableAlias = tableAlias;
        this.alias = alias;
    }

    @Override
    public String alias() {
        return null;
    }

    @Override
    public Expression<?> expression() {
        return null;
    }

    public FieldMeta<T, F> getField() {
        return field;
    }

    public String getAlias() {
        return alias;
    }

    public String getTableAlias() {
        return tableAlias;
    }
}
