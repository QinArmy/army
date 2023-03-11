package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.function.Function;

public interface Selections extends Item {

    Selections selection(Selection selection);

    Selections selection(Function<String, Selection> function, String alias);

    Selections selection(Selection selection1, Selection selection2);

    Selections selection(Function<String, Selection> function, String alias, Selection selection);

    Selections selection(Selection selection, Function<String, Selection> function, String alias);

    Selections selection(Function<String, Selection> function1, String alias1, Function<String, Selection> function2, String alias2);

    Selections selection(DataField field1, DataField field2, DataField field3);

    Selections selection(DataField field1, DataField field2, DataField field3, DataField field4);

    Selections selection(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

    <P> Selections selection(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                             String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

    Selections selection(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);


}
