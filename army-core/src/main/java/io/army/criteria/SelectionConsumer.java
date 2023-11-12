package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.function.Function;

public interface SelectionConsumer extends Statement._DeferContextSpec {

    SelectionConsumer selection(Selection selection);

    SelectionConsumer selection(Function<String, Selection> function, String alias);

    SelectionConsumer selection(Selection selection1, Selection selection2);

    SelectionConsumer selection(Function<String, Selection> function, String alias, Selection selection);

    SelectionConsumer selection(Selection selection, Function<String, Selection> function, String alias);

    SelectionConsumer selection(Function<String, Selection> function1, String alias1, Function<String, Selection> function2, String alias2);

    SelectionConsumer selection(SqlField field1, SqlField field2, SqlField field3);

    SelectionConsumer selection(SqlField field1, SqlField field2, SqlField field3, SqlField field4);

    SelectionConsumer selection(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

    <P> SelectionConsumer selection(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                                    String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

    SelectionConsumer selection(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);


}
