package io.army.criteria.dialect;

import io.army.criteria.Selection;
import io.army.criteria.TableField;

import java.util.function.Function;

public interface Returnings {

    Returnings selection(Selection selection);

    Returnings selection(Selection selection1, Selection selection2);

    Returnings selection(Function<String, Selection> function, String alias);

    Returnings selection(Function<String, Selection> function1, String alias1,
                         Function<String, Selection> function2, String alias2);

    Returnings selection(Function<String, Selection> function, String alias, Selection selection);

    Returnings selection(Selection selection, Function<String, Selection> function, String alias);

    Returnings selection(TableField field1, TableField field2, TableField field3);

    Returnings selection(TableField field1, TableField field2, TableField field3, TableField field4);

}


