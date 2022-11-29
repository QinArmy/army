package io.army.criteria.standard;


import io.army.criteria.DerivedTable;
import io.army.criteria.Statement;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

public interface StandardCrosses extends Statement.JoinBuilder {

    StandardStatement._DynamicJoinSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias);

    <T extends DerivedTable> Statement._AsClause<StandardStatement._DynamicJoinSpec> tabular(Supplier<T> supplier);


}
