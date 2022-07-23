package io.army.dialect;

import io.army.meta.TableMeta;

public interface _ValueInsertContext extends _InsertContext {

    TableMeta<?> table();

    void appendFieldList();

    void appendValueList();

    void appendReturnIdIfNeed();

}
