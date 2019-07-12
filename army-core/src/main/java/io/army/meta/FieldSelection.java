package io.army.meta;

import io.army.criteria.Selection;
import io.army.domain.IDomain;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * created  on 2019-02-22.
 */
final class FieldSelection<T extends IDomain, F> implements Selection<F> {

    private final Field<T, F> field;

    private final String tableAlias;

    private final String alias;

    FieldSelection(@NonNull Field<T, F> field, @Nullable String tableAlias, @NonNull String alias) {
        this.field = field;
        this.tableAlias = tableAlias;
        this.alias = alias;
    }

    public Field<T, F> getField() {
        return field;
    }

    public String getAlias() {
        return alias;
    }

    public String getTableAlias() {
        return tableAlias;
    }
}
