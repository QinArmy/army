package io.army.criteria.standard;

import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

public interface StandardJoins extends Statement.JoinBuilder {

    Statement._OnClause<StandardStatement._DynamicJoinSpec> tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias);

    <T extends TabularItem> Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> tabular(Supplier<T> supplier);


}
