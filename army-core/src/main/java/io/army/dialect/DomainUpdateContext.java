package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

public interface DomainUpdateContext extends DMLContext {

    TableMeta<?> tableMeta();

    String tableAlias();

    @Override
    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta);

    @Override
    void appendField(FieldMeta<?, ?> fieldMeta);
}
