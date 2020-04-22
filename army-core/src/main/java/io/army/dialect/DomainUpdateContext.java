package io.army.dialect;

import io.army.criteria.TableAliasException;
import io.army.criteria.impl.inner.InnerDomainUpdate;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

public interface DomainUpdateContext extends UpdateContext {

    @Override
    InnerDomainUpdate innerUpdate();

    TableMeta<?> tableMeta();

    String tableAlias();

    @Override
    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException;

    @Override
    void appendField(FieldMeta<?, ?> fieldMeta);
}