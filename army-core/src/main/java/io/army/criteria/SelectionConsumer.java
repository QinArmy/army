package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.function.Function;

public interface SelectionConsumer extends Item {

    SelectionConsumer accept(Selection selection);

    SelectionConsumer accept(Function<String, Selection> function, String alias);

    SelectionConsumer accept(Selection selection1, Selection selection2);

    SelectionConsumer accept(Function<String, Selection> function, String alias, Selection selection);

    SelectionConsumer accept(Selection selection, Function<String, Selection> function, String alias);

    SelectionConsumer accept(Function<String, Selection> function1, String alias1, Function<String, Selection> function2, String alias2);

    SelectionConsumer accept(SQLField field1, SQLField field2, SQLField field3);

    SelectionConsumer accept(SQLField field1, SQLField field2, SQLField field3, SQLField field4);

    SelectionConsumer accept(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

    <P> SelectionConsumer accept(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                                 String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

    SelectionConsumer accept(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);

    DerivedField cteField(String derivedAlias, String selectionAlias);

    DerivedField refThis(String derivedAlias, String selectionAlias);

    DerivedField refOuter(String derivedAlias, String selectionAlias);


}
