package io.army.criteria.postgre;

import io.army.criteria.Item;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;

public interface PgSingleDeleteClause<I extends Item, Q extends Item> extends Item {

    PostgreDelete._SingleUsingSpec<I, Q> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias);

    PostgreDelete._SingleUsingSpec<I, Q> deleteFrom(@Nullable SQLs.WordOnly only, TableMeta<?> table, SQLs.WordAs as, String tableAlias);

    PostgreDelete._SingleUsingSpec<I, Q> deleteFrom(TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);

}
