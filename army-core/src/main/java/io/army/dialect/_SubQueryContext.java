package io.army.dialect;

import io.army.meta.FieldMeta;

public interface _SubQueryContext extends _StmtContext {


    void appendOuterField(String tableAlias, FieldMeta<?, ?> field);

    void appendOuterField(FieldMeta<?, ?> field);

}
