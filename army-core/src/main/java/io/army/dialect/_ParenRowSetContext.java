package io.army.dialect;

import io.army.meta.FieldMeta;

public interface _ParenRowSetContext extends _SqlContext {

    void appendOuterField(String tableAlias, FieldMeta<?> field);


    void appendOuterField(FieldMeta<?> field);

    /**
     * @see _SqlContext#appendFieldOnly(FieldMeta)
     */
    void appendOuterFieldOnly(FieldMeta<?> field);

}
